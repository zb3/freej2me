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

import static pl.zb3.freej2me.bridge.gles2.GLES2.Constants.*;

import java.io.IOException;

import pl.zb3.freej2me.bridge.gles2.GLES2;
import pl.zb3.freej2me.bridge.gles2.TextureHolder;

public final class TextureImpl implements TextureHolder {
	final TextureData image;
	private final boolean isMutable;

	int mTexId = 0;

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
		if (mTexId != 0) {
			Render.getRender().textureResourceManager.resetHolder(this);
		}
	}

	public boolean isMutable() {
		return isMutable;
	}

	int getId() {
		if (mTexId == 0) {
			mTexId = GLES2.createTexture();
			Render.getRender().textureResourceManager.registerHolder(this, mTexId);
		} else if (!isMutable) {
			return mTexId;
		}

		// for "mutable" we always refresh this, albeit this seems to be used only by the
		// untested motorola iden api

		loadToGL();

		return mTexId;
	}

	int getWidth() {
		return image.width;
	}

	int getHeight() {
		return image.height;
	}


	private void loadToGL() {
		boolean filter = Boolean.getBoolean("micro3d.v3.texture.filter");

		GLES2.activeTexture(GL_TEXTURE0);
		GLES2.bindTexture(GL_TEXTURE_2D, mTexId);

		GLES2.texParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, filter ? GL_LINEAR : GL_NEAREST);
		GLES2.texParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, filter ? GL_LINEAR : GL_NEAREST);
		GLES2.texParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		GLES2.texParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

		GLES2.texImage2D(GL_TEXTURE_2D, 0, GL_RGBA, image.width, image.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, image.getRaster());
		GLES2.bindTexture(GL_TEXTURE_2D, 0);
	}

	public void clearId() {
		mTexId = 0;
	}
}
