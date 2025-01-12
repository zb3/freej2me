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

public class Utils {
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

	static class Vec3f {
		float x, y, z;

		Vec3f(float x, float y, float z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
	}

	static class Matrix {
		float m00, m01, m02, m03;
		float m10, m11, m12, m13;
		float m20, m21, m22, m23;

		void multiply(Matrix lm, Matrix rm) {
			float l00 = lm.m00, l01 = lm.m01, l02 = lm.m02, l03 = lm.m03;
			float l10 = lm.m10, l11 = lm.m11, l12 = lm.m12, l13 = lm.m13;
			float l20 = lm.m20, l21 = lm.m21, l22 = lm.m22, l23 = lm.m23;

			float r00 = rm.m00, r01 = rm.m01, r02 = rm.m02, r03 = rm.m03;
			float r10 = rm.m10, r11 = rm.m11, r12 = rm.m12, r13 = rm.m13;
			float r20 = rm.m20, r21 = rm.m21, r22 = rm.m22, r23 = rm.m23;

			m00 = l00 * r00 + l01 * r10 + l02 * r20;
			m01 = l00 * r01 + l01 * r11 + l02 * r21;
			m02 = l00 * r02 + l01 * r12 + l02 * r22;
			m03 = l00 * r03 + l01 * r13 + l02 * r23 + l03;

			m10 = l10 * r00 + l11 * r10 + l12 * r20;
			m11 = l10 * r01 + l11 * r11 + l12 * r21;
			m12 = l10 * r02 + l11 * r12 + l12 * r22;
			m13 = l10 * r03 + l11 * r13 + l12 * r23 + l13;

			m20 = l20 * r00 + l21 * r10 + l22 * r20;
			m21 = l20 * r01 + l21 * r11 + l22 * r21;
			m22 = l20 * r02 + l21 * r12 + l22 * r22;
			m23 = l20 * r03 + l21 * r13 + l22 * r23 + l23;
		}

		void multiply(Matrix rm) {
			multiply(this, rm);
		}

		void transformPoint(Vec3f dst, Vec3f src) {
			transformVector(dst, src);
			dst.x += m03;
			dst.y += m13;
			dst.z += m23;
		}

		void transformVector(Vec3f dst, Vec3f src) {
			float x = src.x, y = src.y, z = src.z;
			dst.x = x * m00 + y * m01 + z * m02;
			dst.y = x * m10 + y * m11 + z * m12;
			dst.z = x * m20 + y * m21 + z * m22;
		}
	}

	static class Bone {
		int length;
		int parent;
		Matrix matrix;

		Bone(int length, int parent, Matrix matrix) {
			this.length = length;
			this.parent = parent;
			this.matrix = matrix;
		}
	}

    public static void fillBuffer(float[] buffer, float[] vertices, int[] indices) {
		int bufPos = 0;

        for (int index : indices) {
			buffer[bufPos++] = vertices[index * 3];
			buffer[bufPos++] = vertices[index * 3 + 1];
			buffer[bufPos++] = vertices[index * 3 + 2];
        }
    }

    public static void transform(
            float[] srcVertices, float[] dstVertices,
            float[] srcNormals, float[] dstNormals,
            ByteBuffer boneBuffer, float[] actionMatrices) {
		int svPos = 0, dvPos = 0, snPos = 0, dnPos = 0;
		boneBuffer.rewind();

        Bone[] bones = readBones(boneBuffer);
		int numActions = 0;
        Matrix[] actionMatricesParsed = null;
		if (actionMatrices != null) {
			actionMatricesParsed =  parseActionMatrices(actionMatrices);
			numActions = actionMatricesParsed.length;
		}
        Matrix[] tmpMatrices = new Matrix[bones.length];

        for (int i = 0; i < bones.length; i++) {
            Bone bone = bones[i];
            int parent = bone.parent;

            Matrix matrix = new Matrix();
            if (parent == -1) {
                matrix = bone.matrix;
            } else {
                matrix.multiply(tmpMatrices[parent], bone.matrix);
            }

            if (i < numActions) {
                matrix.multiply(actionMatricesParsed[i]);
            }

            tmpMatrices[i] = matrix;

            for (int j = 0; j < bone.length; j++) {
                Vec3f srcVert = new Vec3f(srcVertices[svPos++], srcVertices[svPos++], srcVertices[svPos++]);
                Vec3f dstVert = new Vec3f(0, 0, 0);
                matrix.transformPoint(dstVert, srcVert);
				dstVertices[dvPos++] = dstVert.x;
				dstVertices[dvPos++] = dstVert.y;
				dstVertices[dvPos++] = dstVert.z;

                if (srcNormals != null) {
                    Vec3f srcNorm = new Vec3f(srcNormals[snPos++], srcNormals[snPos++], srcNormals[snPos++]);
                    Vec3f dstNorm = new Vec3f(0, 0, 0);
                    matrix.transformVector(dstNorm, srcNorm);
					dstNormals[dnPos++] = dstNorm.x;
					dstNormals[dnPos++] = dstNorm.y;
					dstNormals[dnPos++] = dstNorm.z;
                }
            }
        }
    }

    private static Bone[] readBones(ByteBuffer boneBuffer) {
        // Assumes boneBuffer contains packed bone data
        int boneCount = boneBuffer.remaining() / (4 + 4 + 12 * 4); // ints for length & parent + 12 floats for matrix
        Bone[] bones = new Bone[boneCount];

        for (int i = 0; i < boneCount; i++) {
            int length = boneBuffer.getInt();
            int parent = boneBuffer.getInt();
            Matrix matrix = new Matrix();
            matrix.m00 = boneBuffer.getFloat();
            matrix.m01 = boneBuffer.getFloat();
            matrix.m02 = boneBuffer.getFloat();
            matrix.m03 = boneBuffer.getFloat();
            matrix.m10 = boneBuffer.getFloat();
            matrix.m11 = boneBuffer.getFloat();
            matrix.m12 = boneBuffer.getFloat();
            matrix.m13 = boneBuffer.getFloat();
            matrix.m20 = boneBuffer.getFloat();
            matrix.m21 = boneBuffer.getFloat();
            matrix.m22 = boneBuffer.getFloat();
            matrix.m23 = boneBuffer.getFloat();

            bones[i] = new Bone(length, parent, matrix);
        }
        return bones;
    }

    private static Matrix[] parseActionMatrices(float[] actionMatrices) {
        int matrixCount = actionMatrices.length / 12;
        Matrix[] matrices = new Matrix[matrixCount];
        for (int i = 0; i < matrixCount; i++) {
            Matrix matrix = new Matrix();
            int offset = i * 12;
            matrix.m00 = actionMatrices[offset];
            matrix.m01 = actionMatrices[offset + 1];
            matrix.m02 = actionMatrices[offset + 2];
            matrix.m03 = actionMatrices[offset + 3];
            matrix.m10 = actionMatrices[offset + 4];
            matrix.m11 = actionMatrices[offset + 5];
            matrix.m12 = actionMatrices[offset + 6];
            matrix.m13 = actionMatrices[offset + 7];
            matrix.m20 = actionMatrices[offset + 8];
            matrix.m21 = actionMatrices[offset + 9];
            matrix.m22 = actionMatrices[offset + 10];
            matrix.m23 = actionMatrices[offset + 11];

            matrices[i] = matrix;
        }
        return matrices;
    }
}
