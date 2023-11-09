/*
 * Copyright 2020-2023 Yury Kharchenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.woesss.j2me.micro3d;


import org.recompile.mobile.Mobile;

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

// zb3: the whole class needs to be native

public class Utils extends ClassWithNatives {
	static final String TAG = "micro3d";

	static void getSpriteVertex(float[] vertex, float angle, float halfW, float halfH) {
		angle *= MathUtil.TO_RADIANS;
		float sin = (float) Math.sin(angle);
		float cos = (float) Math.cos(angle);
		float x = vertex[0];
		float y = vertex[1];
		float z = vertex[2];
		float w = vertex[3];
		vertex[0] = -halfW * cos + halfH * -sin + x;
		vertex[1] = -halfW * sin + halfH *  cos + y;
		vertex[2] = z;
		vertex[3] = w;
		float bx = -halfW * cos + -halfH * -sin + x;
		float by = -halfW * sin + -halfH *  cos + y;
		vertex[4] = bx;
		vertex[5] = by;
		vertex[6] = z;
		vertex[7] = w;
		float cx = halfW * cos + halfH * -sin + x;
		float cy = halfW * sin + halfH *  cos + y;
		vertex[8] = cx;
		vertex[9] = cy;
		vertex[10] = z;
		vertex[11] = w;
		vertex[12] = cx;
		vertex[13] = cy;
		vertex[14] = z;
		vertex[15] = w;
		vertex[16] = bx;
		vertex[17] = by;
		vertex[18] = z;
		vertex[19] = w;
		vertex[20] = halfW * cos + -halfH * -sin + x;
		vertex[21] = halfW * sin + -halfH *  cos + y;
		vertex[22] = z;
		vertex[23] = w;
	}

	public static void multiplyMV(float[] v, float[] m) {
		float x = v[4];
		float y = v[5];
		float z = v[6];
		float w = v[7];
		v[0] = x * m[0] + y * m[4] + z * m[ 8] + w * m[12];
		v[1] = x * m[1] + y * m[5] + z * m[ 9] + w * m[13];
		v[2] = x * m[2] + y * m[6] + z * m[10] + w * m[14];
		v[3] = x * m[3] + y * m[7] + z * m[11] + w * m[15];
	}

	static native void fillBuffer(FloatBuffer buffer, FloatBuffer vertices, int[] indices);

	static native void transform(FloatBuffer srcVertices, FloatBuffer dstVertices,
								 FloatBuffer srcNormals, FloatBuffer dstNormals,
								 ByteBuffer boneMatrices, float[] actionMatrices);


	public static String loadTextFileFromJar(String fileName) {
		ClassLoader classLoader = Utils.class.getClassLoader();
		InputStream inputStream = classLoader.getResourceAsStream(fileName);

		if (inputStream == null) {
			throw new IllegalArgumentException("File not found in the JAR: " + fileName);
		}

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
			StringBuilder stringBuilder = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line).append('\n');
			}

			return stringBuilder.toString();
		} catch (IOException e) {
			throw new RuntimeException("Error while reading the file: " + fileName, e);
		}
	}

	public static byte[] getAppResourceAsBytes(String name) {
		return Mobile.getPlatform().loader.getMIDletResourceAsByteArray(name);
	}
}
