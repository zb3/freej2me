package javax.microedition.m3g;

import kemulator.m3g.utils.G3DUtils;

public class VertexArray extends Object3D {
	private int componentType;
	private int vertexCount;
	private int componentCount;

	private byte[] byteValues;
	private short[] shortValues;

	// exploded part
	protected int explodedVertexCount;
	protected byte[] explodedByteValues;
	protected short[] explodedShortValues;
	protected boolean explodedSubstituted;
	protected IndexBuffer explodedFor;
	// exploded

	public VertexArray(int numVertices, int numComponents, int componentSize) {
		if ((numVertices >= 1 && numVertices <= 65535) && (numComponents >= 2 || numComponents <= 4) && (componentSize >= 1 || componentSize <= 2)) {
			vertexCount = numVertices;
			componentCount = numComponents;
			componentType = componentSize;

			if (componentType == 1) {
				byteValues = new byte[numVertices * numComponents];
			} else {
				shortValues = new short[numVertices * numComponents];
			}
		} else {
			throw new IllegalArgumentException();
		}
	}

	protected Object3D duplicateObject() {
		VertexArray clone = (VertexArray) super.duplicateObject();

		if (componentType == 1) {
			clone.byteValues = (byte[]) byteValues.clone();
		} else {
			clone.shortValues = (short[]) shortValues.clone();
		}

		clone.explodedFor = null;

		return clone;
	}

	public void set(int firstVertex, int numVertices, short[] values) {
		if (values == null) {
			throw new NullPointerException();
		} else if (componentType != 2) {
			throw new IllegalStateException();
		} else if (numVertices < 0 || values.length < numVertices * componentCount) {
			throw new IllegalArgumentException();
		} else if (firstVertex < 0 || firstVertex + numVertices > vertexCount) {
			throw new IndexOutOfBoundsException();
		}

		System.arraycopy(values, 0, shortValues, firstVertex * componentCount, numVertices * componentCount);
		explodedFor = null;
	}

	public void set(int firstVertex, int numVertices, byte[] values) {
		if (values == null) {
			throw new NullPointerException();
		} else if (componentType != 1) {
			throw new IllegalStateException();
		} else if (numVertices < 0 || values.length < numVertices * componentCount) {
			throw new IllegalArgumentException();
		} else if (firstVertex < 0 || firstVertex + numVertices > vertexCount) {
			throw new IndexOutOfBoundsException();
		}

		System.arraycopy(values, 0, byteValues, firstVertex * componentCount, numVertices * componentCount);
		explodedFor = null;
	}

	public int getVertexCount() {
		return vertexCount;
	}

	public int getExplodedVertexCount() {
		return explodedVertexCount;
	}

	public int getComponentCount() {
		return componentCount;
	}

	public int getComponentType() {
		return componentType;
	}

	public void get(int firstVertex, int numVertices, short[] values) {
		if (values == null) {
			throw new NullPointerException();
		} else if (componentType != 2) {
			throw new IllegalStateException();
		} else if (numVertices < 0 || values.length < numVertices * componentCount) {
			throw new IllegalArgumentException();
		} else if (firstVertex < 0 || firstVertex + numVertices > vertexCount) {
			throw new IndexOutOfBoundsException();
		}

		System.arraycopy(shortValues, firstVertex * componentCount, values, 0, numVertices * componentCount);
	}

	public void get(int firstVertex, int numVertices, byte[] values) {
		if (values == null) {
			throw new NullPointerException();
		} else if (componentType != 1) {
			throw new IllegalStateException();
		} else if (numVertices < 0 || values.length < numVertices * componentCount) {
			throw new IllegalArgumentException();
		} else if (firstVertex < 0 || firstVertex + numVertices > vertexCount) {
			throw new IndexOutOfBoundsException();
		}

		System.arraycopy(byteValues, firstVertex * componentCount, values, 0, numVertices * componentCount);
	}

	public byte[] getByteValues() {
		return byteValues;
	}

	public short[] getShortValues() {
		return shortValues;
	}

	public byte[] getExplodedByteValues() {
		return explodedByteValues;
	}

	public short[] getExplodedShortValues() {
		return explodedShortValues;
	}


	private boolean equals_(VertexArray va) {
		return va != null && componentType == va.componentType && componentCount == va.componentCount && vertexCount == va.vertexCount;
	}

	public void morph(VertexArray[] targets, VertexArray base, float[] weights, float baseWeight) {
		for (VertexArray target : targets) {
			if (!equals_(target)) {
				throw new IllegalStateException();
			}
		}

		if (this.componentType == 1) {
			for (int i = 0; i < byteValues.length; i++) {
				float val = 0;

				for (int t = 0; t < targets.length; t++) {
					val += targets[t].byteValues[i] * weights[t];
				}

				val += base.byteValues[i] * baseWeight;
				byteValues[i] = (byte) G3DUtils.round(val);
			}
		} else {
			for (int i = 0; i < shortValues.length; i++) {
				float val = 0;

				for (int t = 0; t < targets.length; t++) {
					val += targets[t].shortValues[i] * weights[t];
				}

				val += base.shortValues[i] * baseWeight;
				shortValues[i] = (short) G3DUtils.round(val);
			}
		}

		explodedFor = null;
	}

	public void morphColors(VertexArray[] targets, VertexArray base, float[] weights, float baseWeight) {
		for (VertexArray target : targets) {
			if (!equals_(target)) {
				throw new IllegalStateException();
			}
		}

		if (componentType == 1) {
			for (int i = 0; i < byteValues.length; i++) {
				float val = 0;

				for (int t = 0; t < targets.length; t++) {
					val += (targets[t].byteValues[i] & 255) * weights[t];
				}

				val += (base.byteValues[i] & 255) * baseWeight;
				this.byteValues[i] = (byte) G3DUtils.limit(G3DUtils.round(val), 0, 255);
			}
		}

		explodedFor = null;
	}

	public void explodeFor(TriangleStripArray tsa, boolean substitute) {
		if (explodedFor == tsa && explodedSubstituted == substitute) {
			// cached
			return;
		}

		explodedFor = tsa;
		explodedSubstituted = substitute;

		explodedVertexCount = 0;
		for (int s=0; s<tsa.stripLengths.length; s++) {
			explodedVertexCount += (tsa.stripLengths[s] - 2) * 3;
		}

		if (componentType == 1) {
			explodedByteValues = new byte[explodedVertexCount * componentCount];
		} else {
			explodedShortValues = new short[explodedVertexCount * componentCount];
		}

		int stripStart = 0;
		int[] triIndices = new int[]{0, 0, 0};
		int writeIdx = 0;
		for (int s=0; s<tsa.stripLengths.length; s++) {
			int stlen = tsa.stripLengths[s];

			for (int t=0; t<stlen-2; t++) {
				if (substitute) {
					triIndices[0] = triIndices[1] = triIndices[2] = t+2;
				} else {
					triIndices[0] = ((t & 1)==1) ? t+1 : t;
					triIndices[1] = ((t & 1)==1) ? t : t+1;
					triIndices[2] = t+2;
				}

				triIndices[0] = tsa.indices[stripStart + triIndices[0]];
				triIndices[1] = tsa.indices[stripStart + triIndices[1]];
				triIndices[2] = tsa.indices[stripStart + triIndices[2]];

				// write triangles

				for (int x=0; x<3; x++) {
					if (componentType == 1) {
						System.arraycopy(byteValues, triIndices[x] * componentCount, explodedByteValues, writeIdx, componentCount);

					} else {
						System.arraycopy(shortValues, triIndices[x] * componentCount, explodedShortValues, writeIdx, componentCount);
					}

					writeIdx += componentCount;
				}
			}

			stripStart += tsa.stripLengths[s];
		}

	}
}
