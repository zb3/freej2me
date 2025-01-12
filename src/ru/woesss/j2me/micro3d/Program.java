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

import com.mascotcapsule.micro3d.v3.Graphics3D;

import pl.zb3.freej2me.bridge.gles2.GLES2;


abstract class Program {
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
	}

	static void create() {
		if (isCreated) return;
		tex = new Tex();
		color = new Color();
		simple = new Simple();
		sprite = new Sprite();
	}

	private int createProgram(String vertexShader, String fragmentShader) {
		String vertexShaderCode = processShader(loadShaderCode(vertexShader));
		String fragmentShaderCode = processShader(loadShaderCode(fragmentShader));

		int program = GLES2.createProgram(vertexShaderCode, fragmentShaderCode);
		return program;
	}

	private String loadShaderCode(String path) {
		return Utils.loadTextFileFromJar(SHADER_BASE_PATH + path);
	}

	protected String processShader(String shaderCode) { // todo merge?
		return shaderCode;
	}

	void use() {
		GLES2.useProgram(id);
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
		GLES2.deleteProgram(id);
	}

	void setLight(Light light) {
		if (light == null) {
			GLES2.uniform1f(uAmbIntensity, -1.0f);
			return;
		}
		GLES2.uniform1f(uAmbIntensity, MathUtil.clamp(light.ambIntensity, 0, 4096) * MathUtil.TO_FLOAT);
		GLES2.uniform1f(uDirIntensity, MathUtil.clamp(light.dirIntensity, 0, 16384) * MathUtil.TO_FLOAT);
		float x = light.x;
		float y = light.y;
		float z = light.z;
		float rlf = -1.0f / (float) Math.sqrt(x * x + y * y + z * z);
		GLES2.uniform3f(uLightDir, x * rlf, y * rlf, z * rlf);
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

			aPosition = GLES2.getAttribLocation(id, "aPosition");
			aNormal = GLES2.getAttribLocation(id, "aNormal");
			aColorData = GLES2.getAttribLocation(id, "aColorData");
			aMaterial = GLES2.getAttribLocation(id, "aMaterial");

			uMatrix = GLES2.getUniformLocation(id, "uMatrix");
			uNormalMatrix = GLES2.getUniformLocation(id, "uNormalMatrix");
			uAmbIntensity = GLES2.getUniformLocation(id, "uAmbIntensity");
			uDirIntensity = GLES2.getUniformLocation(id, "uDirIntensity");
			uLightDir = GLES2.getUniformLocation(id, "uLightDir");
			uSphereSize = GLES2.getUniformLocation(id, "uSphereSize");
			uToonThreshold = GLES2.getUniformLocation(id, "uToonThreshold");
			uToonHigh = GLES2.getUniformLocation(id, "uToonHigh");
			uToonLow = GLES2.getUniformLocation(id, "uToonLow");
			use();
			GLES2.uniform1i(GLES2.getUniformLocation(id, "uSphereUnit"), 2);
		}

		void setColor(byte[] rgb) {
			float r = (rgb[0] & 0xff) / 255.0f;
			float g = (rgb[1] & 0xff) / 255.0f;
			float b = (rgb[2] & 0xff) / 255.0f;
			GLES2.vertexAttrib3f(aColorData, r, g, b);
		}

		void setToonShading(int attrs, int threshold, int high, int low) {
			if ((attrs & Graphics3D.ENV_ATTR_TOON_SHADING) != 0) {
				GLES2.uniform1f(uToonThreshold, threshold / 255.0f);
				GLES2.uniform1f(uToonHigh, high / 255.0f);
				GLES2.uniform1f(uToonLow, low / 255.0f);
			} else {
				GLES2.uniform1f(uToonThreshold, -1.0f);
			}
		}

		void bindMatrices(float[] mvp, float[] mv) {
			GLES2.uniformMatrix4fv(uMatrix, false, mvp);
			GLES2.uniformMatrix3fv(uNormalMatrix, false, mv);
		}

		void setSphere(TextureImpl sphere) {
			if (sphere != null) {
				int id = sphere.getId();
				GLES2.activeTexture(GL_TEXTURE2);
    			GLES2.bindTexture(GL_TEXTURE_2D, id);
				GLES2.uniform2f(uSphereSize, sphere.getWidth(), sphere.getHeight());
			} else {
				GLES2.uniform2f(uSphereSize, -1, -1);
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
			aPosition = GLES2.getAttribLocation(id, "a_position");
			aTexture = GLES2.getAttribLocation(id, "a_texcoord0");
			use();
			GLES2.uniform1i(GLES2.getUniformLocation(id, "sampler0"), 1);
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
			aPosition = GLES2.getAttribLocation(id, "aPosition");
			aNormal = GLES2.getAttribLocation(id, "aNormal");
			aColorData = GLES2.getAttribLocation(id, "aColorData");
			aMaterial = GLES2.getAttribLocation(id, "aMaterial");

			uTexSize = GLES2.getUniformLocation(id, "uTexSize");
			uSphereSize = GLES2.getUniformLocation(id, "uSphereSize");
			uMatrix = GLES2.getUniformLocation(id, "uMatrix");
			uNormalMatrix = GLES2.getUniformLocation(id, "uNormalMatrix");
			uAmbIntensity = GLES2.getUniformLocation(id, "uAmbIntensity");
			uDirIntensity = GLES2.getUniformLocation(id, "uDirIntensity");
			uLightDir = GLES2.getUniformLocation(id, "uLightDir");
			uToonThreshold = GLES2.getUniformLocation(id, "uToonThreshold");
			uToonHigh = GLES2.getUniformLocation(id, "uToonHigh");
			uToonLow = GLES2.getUniformLocation(id, "uToonLow");
			use();
			GLES2.uniform1i(GLES2.getUniformLocation(id, "uTextureUnit"), 0);
			GLES2.uniform1i(GLES2.getUniformLocation(id, "uSphereUnit"), 2);
		}

		void setTex(TextureImpl tex) {
			if (tex != null) {
				int id = tex.getId();
				GLES2.activeTexture(GL_TEXTURE0);
    			GLES2.bindTexture(GL_TEXTURE_2D, id);
				GLES2.uniform2f(uTexSize, tex.getWidth(), tex.getHeight());
			} else {
				GLES2.uniform2f(uTexSize, 256, 256);
				GLES2.bindTexture(GL_TEXTURE_2D, 0);
			}
		}

		void setToonShading(int attrs, int threshold, int high, int low) {
			if ((attrs & Graphics3D.ENV_ATTR_TOON_SHADING) != 0) {
				GLES2.uniform1f(uToonThreshold, threshold / 255.0f);
				GLES2.uniform1f(uToonHigh, high / 255.0f);
				GLES2.uniform1f(uToonLow, low / 255.0f);
			} else {
				GLES2.uniform1f(uToonThreshold, -1.0f);
			}
		}

		void bindMatrices(float[] mvp, float[] mv) {
			GLES2.uniformMatrix4fv(uMatrix, false, mvp);
			GLES2.uniformMatrix3fv(uNormalMatrix, false, mv);
		}

		void setSphere(TextureImpl sphere) {
			if (sphere != null) {
				int id = sphere.getId();
				GLES2.activeTexture(GL_TEXTURE2);
    			GLES2.bindTexture(GL_TEXTURE_2D, id);
				GLES2.uniform2f(uSphereSize, sphere.getWidth(), sphere.getHeight());
			} else {
				GLES2.uniform2f(uSphereSize, -1, -1);
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
			aPosition = GLES2.getAttribLocation(id, "aPosition");
			aColorData = GLES2.getAttribLocation(id, "aColorData");

			uTexSize = GLES2.getUniformLocation(id, "uTexSize");
			uIsTransparency = GLES2.getUniformLocation(id, "uIsTransparency");
			use();
			GLES2.uniform1i(GLES2.getUniformLocation(id, "uTextureUnit"), 0);
		}

		void setTexture(TextureImpl texture) {
			int id = texture.getId();
			GLES2.activeTexture(GL_TEXTURE0);
    		GLES2.bindTexture(GL_TEXTURE_2D, id);
			GLES2.uniform2f(uTexSize, texture.getWidth(), texture.getHeight());
		}
	}
}
