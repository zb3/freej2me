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

public class ActTableImpl {
	Action[] actions;

	public ActTableImpl(byte[] b) {
		if (b == null) {
			throw new NullPointerException();
		}
		try {
			actions = Loader.loadMtraData(b, 0, b.length);
		} catch (IOException e) {
			System.err.println(Utils.TAG+": Error loading data");
			System.err.println(e);
			throw new RuntimeException(e);
		}
	}

	public ActTableImpl(byte[] b, int offset, int length) throws IOException {
		if (b == null) {
			throw new NullPointerException();
		}
		if (offset < 0 || offset + length > b.length) {
			throw new ArrayIndexOutOfBoundsException();
		}
		try {
			actions = Loader.loadMtraData(b, offset, length);
		} catch (Exception e) {
			System.err.println(Utils.TAG+": Error loading data");
			System.err.println(e);
			throw e;
		}
	}

	public ActTableImpl(String name) throws IOException {
		if (name == null) {
			throw new NullPointerException();
		}
		byte[] bytes = Utils.getAppResourceAsBytes(name);
		if (bytes == null) {
			throw new IOException();
		}
		try {
			actions = Loader.loadMtraData(bytes, 0, bytes.length);
		} catch (IOException e) {
			System.err.println(Utils.TAG+": Error loading data from [" + name + "]");
			System.err.println(e);
			throw new RuntimeException(e);
		}
	}

	public final void dispose() {
		actions = null;
	}

	public final int getNumActions() {
		checkDisposed();
		return actions.length;
	}

	public final int getNumFrames(int idx) {
		checkDisposed();
		if (idx < 0 || idx >= actions.length) {
			throw new IllegalArgumentException();
		}
		return actions[idx].keyframes << 16;
	}

	private void checkDisposed() {
		if (actions == null) throw new IllegalStateException("ActionTable disposed!");
	}

	public int getPattern(int action, int frame, int defValue) {
		Action act = actions[action];
		final int[] dynamic = act.dynamic;
		if (dynamic != null) {
			int iFrame = frame < 0 ? 0 : frame >> 16;
			if (iFrame < dynamic.length) {
				return dynamic[iFrame];
			}
		}
		return defValue;
	}
}
