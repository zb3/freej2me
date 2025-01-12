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
import java.util.Arrays;
import java.util.Stack;

public class FigureImpl {
	final Stack<RenderNode.FigureNode> stack = new Stack<>();
	Model model;
	private int pattern;

	public FigureImpl(byte[] b) {
		if (b == null) {
			throw new NullPointerException();
		}
		try {
			init(b, 0, b.length);
		} catch (Exception e) {
			System.err.println(Utils.TAG+": Error loading data");
			System.err.println(e);
			throw new RuntimeException(e);
		}
	}

	public FigureImpl(byte[] b, int offset, int length) throws IOException {
		if (b == null) {
			throw new NullPointerException();
		}
		if (offset < 0 || offset + length > b.length) {
			throw new ArrayIndexOutOfBoundsException();
		}
		try {
			init(b, offset, length);
		} catch (Exception e) {
			System.err.println(Utils.TAG+": Error loading data");
			System.err.println(e);
			throw e;
		}
	}

	public FigureImpl(String name) throws IOException {
		byte[] bytes = Utils.getAppResourceAsBytes(name);
		if (bytes == null) {
			throw new IOException("Error reading resource: " + name);
		}
		try {
			init(bytes, 0, bytes.length);
		} catch (Exception e) {
			System.err.println(Utils.TAG+": Error loading data from [" + name + "]");
			System.err.println(e);
			throw new RuntimeException(e);
		}
	}

	private synchronized void init(byte[] bytes, int offset, int length) throws IOException {
		model = Loader.loadMbacData(bytes, offset, length);
		Utils.transform(model.originalVertices, model.vertices,
				model.originalNormals, model.normals, model.bones, null);
		sortPolygons();
		fillTexCoordBuffer();
	}

	private void sortPolygons() {
		Model.Polygon[] polygonsT = model.polygonsT;
		Arrays.sort(polygonsT, (a, b) -> {
			if (a.blendMode != b.blendMode) {
				return a.blendMode - b.blendMode;
			}
			if (a.face != b.face) {
				return a.face - b.face;
			}
			return a.doubleFace - b.doubleFace;
		});
		int[][][] subMeshesLengthsT = model.subMeshesLengthsT;
		int[] indexArray = model.indices;
		int pos = 0;
		for (Model.Polygon p : polygonsT) {
			int[] indices = p.indices;
			int length = indices.length;
			subMeshesLengthsT[p.blendMode >> 1][p.face][p.doubleFace] += length;
			System.arraycopy(indices, 0, indexArray, pos, length);
			pos += length;
		}

		Model.Polygon[] polygonsC = model.polygonsC;
		Arrays.sort(polygonsC, (a, b) -> {
			if (a.blendMode != b.blendMode) {
				return a.blendMode - b.blendMode;
			}
			return a.doubleFace - b.doubleFace;
		});
		int[][] subMeshesLengthsC = model.subMeshesLengthsC;
		for (Model.Polygon p : polygonsC) {
			int[] indices = p.indices;
			int length = indices.length;
			subMeshesLengthsC[p.blendMode >> 1][p.doubleFace] += length;
			System.arraycopy(indices, 0, indexArray, pos, length);
			pos += length;
		}
	}

	private void fillTexCoordBuffer() {
		int tcPos = 0;
		for (Model.Polygon poly : model.polygonsT) {
			System.arraycopy(poly.texCoords, 0, model.texCoordArray, tcPos, poly.texCoords.length);
			tcPos += poly.texCoords.length;
			poly.texCoords = null;
		}
		for (Model.Polygon poly : model.polygonsC) {
			System.arraycopy(poly.texCoords, 0, model.texCoordArray, tcPos, poly.texCoords.length);
			tcPos += poly.texCoords.length;
			poly.texCoords = null;
		}
	}

	public final void dispose() {
		model = null;
	}

	public synchronized final void setPosture(ActTableImpl actTable, int action, int frame) {
		if (action < 0 || action >= actTable.getNumActions()) {
			throw new IllegalArgumentException();
		}
		Action act = actTable.actions[action];
		final int[] dynamic = act.dynamic;
		if (dynamic != null) {
			int iFrame = frame < 0 ? 0 : frame >> 16;
			if (iFrame < dynamic.length) {
				pattern = dynamic[iFrame];
				applyPattern();
			}
		}
		//noinspection ManualMinMaxCalculation
		applyBoneAction(act, frame < 0 ? 0 : frame);
	}

	private void applyBoneAction(Action act, int frame) {
		Action.Bone[] actionBones = act.boneActions;
		if (actionBones.length == 0) return;
		synchronized (act.matrices) {
			for (final Action.Bone actionBone : actionBones) {
				actionBone.setFrame(frame);
			}
			Utils.transform(model.originalVertices, model.vertices,
					model.originalNormals, model.normals, model.bones, act.matrices);
		}
	}

	private void applyPattern() {
		int[] indexArray = model.indices;
		int pos = 0;
		int invalid = model.vertices.length / 3 - 1;
		for (Model.Polygon p : model.polygonsT) {
			int[] indices = p.indices;
			int length = indices.length;
			int pp = p.pattern;
			if ((pp & pattern) == pp) {
				for (int i = 0; i < length; i++) {
					indexArray[pos++] = indices[i];
				}
			} else {
				while (length > 0) {
					indexArray[pos++] = invalid;
					length--;
				}
			}
		}

		for (Model.Polygon p : model.polygonsC) {
			int[] indices = p.indices;
			int length = indices.length;
			int pp = p.pattern;
			if ((pp & pattern) == pp) {
				for (int i = 0; i < length; i++) {
					indexArray[pos++] = indices[i];
				}
			} else {
				while (length > 0) {
					indexArray[pos++] = invalid;
					length--;
				}
			}
		}
	}

	public final int getNumPattern() {
		return model.numPatterns;
	}

	public synchronized final void setPattern(int idx) {
		pattern = idx;
		applyPattern();
	}

	synchronized void prepareBuffers() {
		if (model.vertexArray == null) {
			model.vertexArray = new float[model.vertexArrayCapacity];
		}
		Utils.fillBuffer(model.vertexArray, model.vertices, model.indices);

		if (model.originalNormals == null) {
			return;
		}
		if (model.normalsArray == null) {
			model.normalsArray = new float[model.vertexArrayCapacity];
		}
		Utils.fillBuffer(model.normalsArray, model.normals, model.indices);
	}

	public synchronized void setPosture(ActTableImpl actTable, int action, int frame, int pattern) {
		if (action < 0 || action >= actTable.getNumActions()) {
			throw new IllegalArgumentException();
		}
		this.pattern = pattern;
		applyPattern();
		//noinspection ManualMinMaxCalculation
		applyBoneAction(actTable.actions[action], frame < 0 ? 0 : frame);
	}

	synchronized void fillBuffers(float[] vertices, float[] normals) {
		Utils.fillBuffer(vertices, model.vertices, model.indices);
		if (normals != null) {
			Utils.fillBuffer(normals, model.normals, model.indices);
		}
	}
}
