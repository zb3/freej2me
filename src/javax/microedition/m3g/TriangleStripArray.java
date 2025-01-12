package javax.microedition.m3g;

public class TriangleStripArray extends IndexBuffer {
	int[] stripLengths;
	int[] triangles;

	public TriangleStripArray(int firstIndex, int[] stripLengths) {
		if (stripLengths == null) {
			throw new NullPointerException();
		} else if (stripLengths.length == 0) {
			throw new IllegalArgumentException();
		}

		int sumStripLengths = 0;

		for (int i = stripLengths.length - 1; i >= 0; --i) {
			if (stripLengths[i] < 3) {
				throw new IllegalArgumentException();
			}

			sumStripLengths += stripLengths[i];
		}

		if (firstIndex + sumStripLengths > 65535) {
			throw new IllegalArgumentException();
		} else {
			super.indices = new int[sumStripLengths];

			for (int i = 0; i < sumStripLengths; ++i) {
				super.indices[i] = firstIndex + i;
			}
			this.stripLengths = new int[stripLengths.length];
			System.arraycopy(stripLengths, 0, this.stripLengths, 0, stripLengths.length);
			compile();
		}
	}

	public TriangleStripArray(int[] indices, int[] stripLengths) {
		if (indices == null || stripLengths == null) {
			throw new NullPointerException();
		} else if (stripLengths.length == 0) {
			throw new IllegalArgumentException();
		}

		int sumStripLengths = 0;

		for (int i = stripLengths.length - 1; i >= 0; --i) {
			if (stripLengths[i] < 3) {
				throw new IllegalArgumentException();
			}

			sumStripLengths += stripLengths[i];
		}

		if (indices.length < sumStripLengths) {
			throw new IllegalArgumentException();
		}

		for (int i = sumStripLengths - 1; i >= 0; --i) {
			if (indices[i] < 0 || indices[i] > 65535) {
				throw new IllegalArgumentException();
			}
		}

		super.indices = new int[sumStripLengths];
		System.arraycopy(indices, 0, super.indices, 0, sumStripLengths);
		this.stripLengths = new int[stripLengths.length];
		System.arraycopy(stripLengths, 0, this.stripLengths, 0, stripLengths.length);
		compile();
	}

	protected Object3D duplicateObject() {
		TriangleStripArray clone = (TriangleStripArray) super.duplicateObject();
		clone.indices = (int[]) super.indices.clone();
		clone.stripLengths = (int[]) stripLengths.clone();
		clone.compile();

		return clone;
	}

	public int getStripCount() {
		return stripLengths.length;
	}

	public int[] getIndexStrip(int index) {
		if (index >= 0 && index < stripLengths.length) {
			int sumStripLengths = 0;

			for (int i = 0; i < index; ++i) {
				sumStripLengths += stripLengths[i];
			}

			int[] resIndexStrip = new int[stripLengths[index]];
			if (this.stripLengths != null) {
				System.arraycopy(super.indices, sumStripLengths, resIndexStrip, 0, stripLengths[index]);
			}

			return resIndexStrip;
		} else {
			return null;
		}
	}

	public int[] getTriangles() {
		return triangles;
	}

	protected boolean getIndices(int index, int[] indices) {
		int index2 = 0;

		for (int i = 0; i < stripLengths.length; ++i) {
			if (index < stripLengths[i] - 2) {
				indices[0] = super.indices[index2 + index + 0];
				indices[1] = super.indices[index2 + index + 1];
				indices[2] = super.indices[index2 + index + 2];
				indices[3] = index & 1;

				return true;
			}

			index -= stripLengths[i] - 2;
			index2 += stripLengths[i];
		}

		return false;
	}

	private void compile() {
		// duplicated vertices so that one call to gldrawelements will draw more than one disconnected triangle strip
		// additional vertices are duplicated so as to maintain parity

		int joinedIndexCount = 0;

		int stripCount = stripLengths.length;
		int strip;
		for (strip = 0; strip < stripCount; ++strip) {
			if (strip != 0) {
				joinedIndexCount += ((joinedIndexCount & 1) != 0) ? 3 : 2;
			}
			joinedIndexCount += stripLengths[strip];
		}

		triangles = new int[joinedIndexCount];

		int src = 0, dst = 0;
		for (strip = 0; strip < stripCount; ++strip) {
			if (strip != 0) {
				triangles[dst++] = indices[src - 1];
				triangles[dst++] = indices[src];
				if ((stripLengths[strip - 1] & 1) != 0) {
					triangles[dst++] = indices[src];
				}
			}
			for (int i = 0; i < stripLengths[strip]; ++i) {
				triangles[dst++] = indices[src++];
			}
		}
	}



	public int profilerCount() {
		return indices.length - stripLengths.length * 2;
	}
}
