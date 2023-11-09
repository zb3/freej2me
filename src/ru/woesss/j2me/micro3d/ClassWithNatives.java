package ru.woesss.j2me.micro3d;

import pl.zb3.NativeLoader;

public class ClassWithNatives {
	static {
		NativeLoader.loadLibrary("micro3d");
	}
}
