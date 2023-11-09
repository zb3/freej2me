/*
 *  Copyright 2022 Yury Kharchenko
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package ru.woesss.j2me.micro3d;

import java.io.IOException;
import java.nio.ByteBuffer;

public final class TextureImpl extends ClassWithNatives {
	static int sLastId;

	final TextureData image;
	private final boolean isMutable;

	int mTexId = -1;

	private native boolean _glIsTexture(int id);
	private native int _glGenTextureId(int biggerThan);
	private native void _loadToGl(int texId, int width, int height, boolean filter, ByteBuffer data);

	public TextureImpl() {
		image = new TextureData(256, 256);
		isMutable = true;
	}

	public TextureImpl(byte[] b) {
		if (b == null) {
			throw new NullPointerException();
		}
		try {
			image = Loader.loadBmpData(b, 0, b.length);
		} catch (IOException e) {
			System.err.println(Utils.TAG+": Error loading data");
			System.err.println(e);
			throw new RuntimeException(e);
		}
		isMutable = false;
	}

	public TextureImpl(byte[] b, int offset, int length) throws IOException {
		if (b == null) {
			throw new NullPointerException();
		}
		if (offset < 0 || offset + length > b.length) {
			throw new ArrayIndexOutOfBoundsException();
		}
		try {
			image = Loader.loadBmpData(b, offset, length);
		} catch (Exception e) {
			System.err.println(Utils.TAG+": Error loading data");
			System.err.println(e);
			throw e;
		}
		isMutable = false;
	}

	public TextureImpl(String name) throws IOException {
		if (name == null) {
			throw new NullPointerException();
		}
		byte[] b = Utils.getAppResourceAsBytes(name);
		if (b == null) {
			throw new IOException();
		}
		try {
			image = Loader.loadBmpData(b, 0, b.length);
		} catch (IOException e) {
			System.err.println(Utils.TAG+": Error loading data from [" + name + "]");
			System.err.println(e);
			throw new RuntimeException(e);
		}
		isMutable = false;
	}

	public void dispose() {
	}

	public boolean isMutable() {
		return isMutable;
	}

	int getId() {
		if (!_glIsTexture(mTexId)) {
			generateId();
		} else if (!isMutable) {
			return mTexId;
		}
		loadToGL();
		return mTexId;
	}

	int getWidth() {
		return image.width;
	}

	int getHeight() {
		return image.height;
	}

	private void generateId() {
		synchronized (TextureImpl.class) {
			sLastId = mTexId = _glGenTextureId(sLastId);
		}
		Render.checkGlError("glGenTextures");
	}

	private void loadToGL() {
		boolean filter = Boolean.getBoolean("micro3d.v3.texture.filter");
		_loadToGl(mTexId, image.width, image.height, filter, image.getRaster());
	}

	@Override
	protected void finalize() throws Throwable {
		try {
			dispose();
		} finally {
			super.finalize();
		}
	}
}
