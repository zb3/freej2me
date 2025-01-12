package kemulator.m3g.gles2;

public class BUtils {
    private static byte[] colorBuffer = null;
    private static short[] vertexFlatLightBuffer = null;

    public static byte[] getColorBuffer(byte[] values, float alphaFactor, int vertexCount) {
		int targetLength = 4 * vertexCount;
		if (colorBuffer == null || colorBuffer.length < targetLength) {
			colorBuffer = new byte[targetLength * 4 / 3];
		}

		if ((values.length == targetLength) && alphaFactor == 1.0F) {
            System.arraycopy(values, 0, colorBuffer, 0, targetLength);
		} else {
			int idx = 0;
			if (values.length == targetLength) {
				idx = 0;

				while (idx < targetLength) {
                    colorBuffer[idx] = values[idx++];
                    colorBuffer[idx] = values[idx++];
                    colorBuffer[idx] = values[idx++];
					colorBuffer[idx] = (byte) ((int) ((float) (values[idx++] & 255) * alphaFactor + 0.5F));
				}
			} else {
                int dstIdx = 0;

				while (idx < values.length) {
                    colorBuffer[dstIdx++] = values[idx++];
                    colorBuffer[dstIdx++] = values[idx++];
                    colorBuffer[dstIdx++] = values[idx++];
                    colorBuffer[dstIdx++] = (byte) ((int) (255.0F * alphaFactor + 0.5F));
				}
			}
		}

		return colorBuffer;
	}


	public static short[] getVertexBufferFlatLight(short[] var0) {
        if (vertexFlatLightBuffer == null || vertexFlatLightBuffer.length < var0.length) {
			vertexFlatLightBuffer = new short[var0.length * 4 / 3];
		}

		for (int t=0;t<var0.length;t+=9) {
			vertexFlatLightBuffer[t+0] = var0[t+6+0];
            vertexFlatLightBuffer[t+1] = var0[t+6+1];
            vertexFlatLightBuffer[t+2] = var0[t+6+2];

            vertexFlatLightBuffer[t+3] = var0[t+6+0];
            vertexFlatLightBuffer[t+4] = var0[t+6+1];
            vertexFlatLightBuffer[t+5] = var0[t+6+2];

            vertexFlatLightBuffer[t+6] = var0[t+6+0];
            vertexFlatLightBuffer[t+7] = var0[t+6+1];
            vertexFlatLightBuffer[t+8] = var0[t+6+2];
		}

		return vertexFlatLightBuffer;
	}


	public static short[] getVertexBufferFlatLight(byte[] var0) {
	    if (vertexFlatLightBuffer == null || vertexFlatLightBuffer.length < var0.length) {
			vertexFlatLightBuffer = new short[var0.length * 4 / 3];
		}

		for (int t=0;t<var0.length;t+=9) {
			vertexFlatLightBuffer[t+0] = var0[t+6+0];
			vertexFlatLightBuffer[t+1] = var0[t+6+1];
			vertexFlatLightBuffer[t+2] = var0[t+6+2];

			vertexFlatLightBuffer[t+3] = var0[t+6+0];
			vertexFlatLightBuffer[t+4] = var0[t+6+1];
			vertexFlatLightBuffer[t+5] = var0[t+6+2];

			vertexFlatLightBuffer[t+6] = var0[t+6+0];
			vertexFlatLightBuffer[t+7] = var0[t+6+1];
			vertexFlatLightBuffer[t+8] = var0[t+6+2];
		}

		return vertexFlatLightBuffer;
	}




}
