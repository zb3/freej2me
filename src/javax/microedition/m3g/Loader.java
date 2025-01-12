package javax.microedition.m3g;

import java.io.IOException;

import kemulator.m3g.impl.M3GLoader;

public class Loader {
	public static Object3D[] load(String var0) throws IOException {
		return M3GLoader.load(var0);
	}

	public static Object3D[] load(byte[] var0, int var1) throws IOException {
		return M3GLoader.load(var0, var1);
	}
}
