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

import com.mascotcapsule.micro3d.v3.Graphics3D;

import java.nio.Buffer;
import java.nio.ByteBuffer;


abstract class Program extends ClassWithNatives {
	public static final String SHADER_BASE_PATH = "m3d_shaders/";
	static Tex tex;
	static Color color;
	static Simple simple;
	static Sprite sprite;
	private static boolean isCreated;

	protected final int id;
	protected int uAmbIntensity;
	protected int uDirIntensity;
	protected int uLightDir;
	protected int uMatrix;
	protected int uNormalMatrix;
	int aPosition;
	int aNormal;
	int aColorData;
	int aMaterial;

	Program(String vertexShader, String fragmentShader) {
		id = createProgram(vertexShader, fragmentShader);
		getLocations();
		Render.checkGlError("getLocations");
	}

	native int _loadShader(boolean fragment, String source);
	native void _glUseProgram(int program);
	native void _glDeleteProgram(int program);
	static native void _glReleaseShaderCompiler();
	native int _glGetShaderLocation(int pid, boolean uniform, String name);
	native int _doCreateProgram(int vertexId, int fragmentId);
	native void _glUniform1i(int id, int a);
	native void _glUniform1f(int id, float a);
	native void _glUniform2f(int id, float a, float b);
	native void _glUniform3f(int id, float a, float b, float c);
	native void _glVertexAttrib3f(int id, float a, float b, float c);
	native void _bindMatrices(int uMatrix, int uNormalMatrix, float[] mvp, float[] mv);
	native void _bindTexture2D(int no, int tid);
	native void _unbindTexture2D();

	static void create() {
		if (isCreated) return;
		tex = new Tex();
		color = new Color();
		simple = new Simple();
		sprite = new Sprite();
		_glReleaseShaderCompiler();
	}

	private int createProgram(String vertexShader, String fragmentShader) {
		String vertexShaderCode = processShader(loadShaderCode(vertexShader));
		String fragmentShaderCode = processShader(loadShaderCode(fragmentShader));

		int vertexId = _loadShader(false, vertexShaderCode);
		int fragmentId = _loadShader(true, fragmentShaderCode);

		int program = _doCreateProgram(vertexId, fragmentId);             // create empty OpenGL Program
		Render.checkGlError("glLinkProgram");
		return program;
	}

	private String loadShaderCode(String path) {
		return Utils.loadTextFileFromJar(SHADER_BASE_PATH + path);
	}

	protected String processShader(String shaderCode) { // todo merge?
		return shaderCode;
	}

	void use() {
		_glUseProgram(id);
	}

	protected abstract void getLocations();

	static void release() {
		if (!isCreated) return;
		tex.delete();
		color.delete();
		simple.delete();
		sprite.delete();
		isCreated = false;
	}

	void delete() {
		_glDeleteProgram(id);
		Render.checkGlError("program delete");
	}

	void setLight(Light light) {
		if (light == null) {
			_glUniform1f(uAmbIntensity, -1.0f);
			return;
		}
		_glUniform1f(uAmbIntensity, MathUtil.clamp(light.ambIntensity, 0, 4096) * MathUtil.TO_FLOAT);
		_glUniform1f(uDirIntensity, MathUtil.clamp(light.dirIntensity, 0, 16384) * MathUtil.TO_FLOAT);
		float x = light.x;
		float y = light.y;
		float z = light.z;
		float rlf = -1.0f / (float) Math.sqrt(x * x + y * y + z * z);
		_glUniform3f(uLightDir, x * rlf, y * rlf, z * rlf);
	}

	static final class Color extends Program {
		private static final String VERTEX = "color.vsh";
		private static final String FRAGMENT = "color.fsh";
		int uSphereSize;
		int uToonThreshold;
		int uToonHigh;
		int uToonLow;

		Color() {
			super(VERTEX, FRAGMENT);
		}

		@Override
		protected void getLocations() {
			aPosition = _glGetShaderLocation(id, false, "aPosition");
			aNormal = _glGetShaderLocation(id, false, "aNormal");
			aColorData = _glGetShaderLocation(id, false, "aColorData");
			aMaterial = _glGetShaderLocation(id, false, "aMaterial");
			uMatrix = _glGetShaderLocation(id, true, "uMatrix");
			uNormalMatrix = _glGetShaderLocation(id, true, "uNormalMatrix");
			uAmbIntensity = _glGetShaderLocation(id, true, "uAmbIntensity");
			uDirIntensity = _glGetShaderLocation(id, true, "uDirIntensity");
			uLightDir = _glGetShaderLocation(id, true, "uLightDir");
			uSphereSize = _glGetShaderLocation(id, true, "uSphereSize");
			uToonThreshold = _glGetShaderLocation(id, true, "uToonThreshold");
			uToonHigh = _glGetShaderLocation(id, true, "uToonHigh");
			uToonLow = _glGetShaderLocation(id, true, "uToonLow");
			use();
			_glUniform1i(_glGetShaderLocation(id, true, "uSphereUnit"), 2);
		}

		void setColor(ByteBuffer rgb) {
			((Buffer)rgb).rewind();
			float r = (rgb.get() & 0xff) / 255.0f;
			float g = (rgb.get() & 0xff) / 255.0f;
			float b = (rgb.get() & 0xff) / 255.0f;
			_glVertexAttrib3f(aColorData, r, g, b);
		}

		void setToonShading(int attrs, int threshold, int high, int low) {
			if ((attrs & Graphics3D.ENV_ATTR_TOON_SHADING) != 0) {
				_glUniform1f(uToonThreshold, threshold / 255.0f);
				_glUniform1f(uToonHigh, high / 255.0f);
				_glUniform1f(uToonLow, low / 255.0f);
			} else {
				_glUniform1f(uToonThreshold, -1.0f);
			}
		}

		void bindMatrices(float[] mvp, float[] mv) {
			_bindMatrices(uMatrix, uNormalMatrix, mvp, mv);
		}

		void setSphere(TextureImpl sphere) {
			if (sphere != null) {
				int id = sphere.getId();
				_bindTexture2D(2, id);
				_glUniform2f(uSphereSize, sphere.getWidth(), sphere.getHeight());
			} else {
				_glUniform2f(uSphereSize, -1, -1);
			}
		}
	}

	static final class Simple extends Program {
		private static final String VERTEX = "simple.vsh";
		private static final String FRAGMENT = "simple.fsh";
		int aTexture;

		Simple() {
			super(VERTEX, FRAGMENT);
		}

		protected void getLocations() {
			aPosition = _glGetShaderLocation(id, false, "a_position");
			aTexture = _glGetShaderLocation(id, false, "a_texcoord0");
			use();
			_glUniform1i(_glGetShaderLocation(id, true, "sampler0"), 1);
		}
	}

	static final class Tex extends Program {
		private static final String VERTEX = "tex.vsh";
		private static final String FRAGMENT = "tex.fsh";
		int uTexSize;
		int uSphereSize;
		int uToonThreshold;
		int uToonHigh;
		int uToonLow;

		Tex() {
			super(VERTEX, FRAGMENT);
		}

		@Override
		protected String processShader(String shaderCode) {
			if (Boolean.getBoolean("micro3d.v3.texture.filter")) {
				shaderCode = "#define FILTER\n" + shaderCode;
			}
			return shaderCode;
		}

		protected void getLocations() {
			aPosition = _glGetShaderLocation(id, false, "aPosition");
			aNormal = _glGetShaderLocation(id, false, "aNormal");
			aColorData = _glGetShaderLocation(id, false, "aColorData");
			aMaterial = _glGetShaderLocation(id, false, "aMaterial");
			uTexSize = _glGetShaderLocation(id, true, "uTexSize");
			uSphereSize = _glGetShaderLocation(id, true, "uSphereSize");
			uMatrix = _glGetShaderLocation(id, true, "uMatrix");
			uNormalMatrix = _glGetShaderLocation(id, true, "uNormalMatrix");
			uAmbIntensity = _glGetShaderLocation(id, true, "uAmbIntensity");
			uDirIntensity = _glGetShaderLocation(id, true, "uDirIntensity");
			uLightDir = _glGetShaderLocation(id, true, "uLightDir");
			uToonThreshold = _glGetShaderLocation(id, true, "uToonThreshold");
			uToonHigh = _glGetShaderLocation(id, true, "uToonHigh");
			uToonLow = _glGetShaderLocation(id, true, "uToonLow");
			use();
			_glUniform1i(_glGetShaderLocation(id, true, "uTextureUnit"), 0);
			_glUniform1i(_glGetShaderLocation(id, true, "uSphereUnit"), 2);
		}

		void setTex(TextureImpl tex) {
			if (tex != null) {
				int id = tex.getId();
				_bindTexture2D(0, id);
				_glUniform2f(uTexSize, tex.getWidth(), tex.getHeight());
			} else {
				_glUniform2f(uTexSize, 256, 256);
				_unbindTexture2D();
			}
		}

		void setToonShading(int attrs, int threshold, int high, int low) {
			if ((attrs & Graphics3D.ENV_ATTR_TOON_SHADING) != 0) {
				_glUniform1f(uToonThreshold, threshold / 255.0f);
				_glUniform1f(uToonHigh, high / 255.0f);
				_glUniform1f(uToonLow, low / 255.0f);
			} else {
				_glUniform1f(uToonThreshold, -1.0f);
			}
		}

		void bindMatrices(float[] mvp, float[] mv) {
			_bindMatrices(uMatrix, uNormalMatrix, mvp, mv);
		}

		void setSphere(TextureImpl sphere) {
			if (sphere != null) {
				int id = sphere.getId();
				_bindTexture2D(2, id);
				_glUniform2f(uSphereSize, sphere.getWidth(), sphere.getHeight());
			} else {
				_glUniform2f(uSphereSize, -1, -1);
			}
		}
	}

	static class Sprite extends Program {
		private static final String VERTEX = "sprite.vsh";
		private static final String FRAGMENT = "sprite.fsh";
		int uTexSize;
		int uIsTransparency;

		Sprite() {
			super(VERTEX, FRAGMENT);
		}

		@Override
		protected String processShader(String shaderCode) {
			if (Boolean.getBoolean("micro3d.v3.texture.filter")) {
				shaderCode = "#define FILTER\n" + shaderCode;
			}
			return shaderCode;
		}

		protected void getLocations() {
			aPosition = _glGetShaderLocation(id, false, "aPosition");
			aColorData = _glGetShaderLocation(id, false, "aColorData");
			uTexSize = _glGetShaderLocation(id, true, "uTexSize");
			uIsTransparency = _glGetShaderLocation(id, true, "uIsTransparency");
			use();
			_glUniform1i(_glGetShaderLocation(id, true, "uTextureUnit"), 0);
		}

		void setTexture(TextureImpl texture) {
			int id = texture.getId();
			_bindTexture2D(0, id);
			_glUniform2f(uTexSize, texture.getWidth(), texture.getHeight());
		}
	}
}
