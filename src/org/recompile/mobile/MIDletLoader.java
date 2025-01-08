/*
	This file is part of FreeJ2ME.

	FreeJ2ME is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	FreeJ2ME is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with FreeJ2ME.  If not, see http://www.gnu.org/licenses/
*/
package org.recompile.mobile;


import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.lang.ClassLoader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.ArrayList;
import java.util.Enumeration;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;
import javax.microedition.io.*;

public class MIDletLoader extends URLClassLoader
{
	public String name;
	public String icon;
	private String className;

	public String suitename;

	private Class<?> mainClass;
	private MIDlet mainInst;

	private HashMap<String, String> properties = new HashMap<String, String>();


	public MIDletLoader(URL urls[], Map<String, String> descriptorProperties, String mainClassOverride)
	{
		super(urls);

		className = mainClassOverride;

		try {
			String jarName = Paths.get(urls[0].toURI()).getFileName().toString().replace('.', '_');
			suitename = jarName;
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		try
		{
			Mobile.getPlatform().addSystemProperty("microedition.platform", "j2me");
			Mobile.getPlatform().addSystemProperty("microedition.profiles", "MIDP-2.0");
			Mobile.getPlatform().addSystemProperty("microedition.configuration", "CLDC-1.1");
			Mobile.getPlatform().addSystemProperty("microedition.locale", "en-US");
			Mobile.getPlatform().addSystemProperty("microedition.encoding", "ISO-8859-1");
			Mobile.getPlatform().addSystemProperty("microedition.m3g.version", "1.1");
			Mobile.getPlatform().addSystemProperty("wireless.messaging.sms.smsc", "+8613800010000");
			Mobile.getPlatform().addSystemProperty("device.imei", "000000000000000");

		}
		catch (Exception e)
		{
			System.out.println("Can't add CLDC System Properties");
		}

		try
		{
			loadManifest();
		}
		catch (Exception e)
		{
			System.out.println("Can't Read Manifest!");
		}

		if (className == null) {
			className = findMainClassInJars(urls);
		}

		// so finally overrides > descriptor > manifest
		properties.putAll(descriptorProperties);
	}

	public static String findMainClassInJars(URL[] urls) {
		// we search for a class file containing "startApp"
		// note this is just an approximation, but it often works
		// the class might be abstract though..


		for (URL url : urls) {
			File file;
			try {
				file = new File(url.toURI());
			} catch (URISyntaxException e) {
				return null;
			}
			try (JarFile jarFile = new JarFile(file)) {
				Enumeration<JarEntry> entries = jarFile.entries();
				while (entries.hasMoreElements()) {
					JarEntry entry = entries.nextElement();
					if (entry.getName().endsWith(".class")) {
						String className = entry.getName().replace('/', '.').replace(".class", "");
						if (hasStartApp(className, jarFile.getInputStream(entry))) {
							return className;
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private static boolean hasStartApp(String className, InputStream is) {
		byte[] pattern = "startApp".getBytes();
		try {
			byte[] classBytes = readBytes(is);
			for (int i = 0; i < classBytes.length - pattern.length; i++) {
				int j = 0;
				for (j = 0; j < pattern.length; j++) {
					if (classBytes[i+j] != pattern[j]) {
						break;
					}
				}
				if (j == pattern.length) {
					return true;
				}
			}
		} catch (IOException e) {
				e.printStackTrace();
		}
  		finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	private static byte[] readBytes(InputStream is) throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		int nRead;
		byte[] data = new byte[1024];
		while ((nRead = is.read(data, 0, data.length)) != -1) {
			buffer.write(data, 0, nRead);
		}
		return buffer.toByteArray();
	}

	public void start() throws MIDletStateChangeException
	{
		Method start = null;

		try
		{
			mainClass = loadClass(className);

			Constructor constructor;
			constructor = mainClass.getConstructor();
			constructor.setAccessible(true);

			MIDlet.initAppProperties(properties);
			mainInst = (MIDlet)constructor.newInstance();
		}
		catch (Exception e)
		{
			System.out.println("Problem Constructing " + name + " class: " +className);
			throw new RuntimeException(e);
		}

		try
		{
			while (start == null)
			{
				try
				{
					start = mainClass.getDeclaredMethod("startApp");
					start.setAccessible(true);
				}
				catch (NoSuchMethodException e)
				{
					mainClass = mainClass.getSuperclass();
					if (mainClass == null || mainClass == MIDlet.class)
					{
						throw e;
					}

					mainClass = loadClass(mainClass.getName(), true);
				}
			}
		}
		catch (Exception e)
		{
			System.out.println("Can't Find startApp Method");
			throw new RuntimeException(e);
		}

		try
		{
			start.invoke(mainInst);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public static void parseDescriptorInto(InputStream is, Map<String, String> keyValueMap) {
		String currentKey = null;
		StringBuilder currentValue = new StringBuilder();

		try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
			String line;
			while ((line = br.readLine()) != null) {
				if (line.trim().isEmpty()) {
					continue;
				}
				if (line.startsWith(" ")) {
					currentValue.append(line, 1, line.length());
				} else {
					if (currentKey != null) {
						keyValueMap.put(currentKey, currentValue.toString().trim());
						currentValue.setLength(0);
					}

					int colonIndex = line.indexOf(':');
					if (colonIndex != -1) {
						currentKey = line.substring(0, colonIndex).trim();
						currentValue.append(line.substring(colonIndex + 1).trim());
					}
				}
			}

			if (currentKey != null) {
				keyValueMap.put(currentKey, currentValue.toString().trim());
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	private void loadManifest()
	{
		String resource = "META-INF/MANIFEST.MF";
		URL url = findResource(resource); // this has a case-insensitive fallback
		if (url == null) {
			return;
		}

		try {
			parseDescriptorInto(url.openStream(), properties);
		} catch (Exception e) {
			System.out.println("Can't Read Jar Manifest!");
			e.printStackTrace();
		}

		if (properties.containsKey("MIDlet-1")) {
			String val = properties.get("MIDlet-1");

			String[] parts = val.split(",");
			if (parts.length == 3) {
				name = parts[0].trim();
				icon = parts[1].trim();

				if (className == null) {
					className = parts[2].trim();
				}

				suitename = name;
				suitename = suitename.replace(":","");
			}
		}
	}


	public InputStream getResourceAsStream(String resource)
	{
		URL url;
		//System.out.println("Loading Resource: " + resource);

		if(resource.startsWith("/"))
		{
			resource = resource.substring(1);

			if(resource.startsWith("/"))
			{
				resource = resource.substring(1);
			}
		}

		try
		{
			url = findResource(resource);
			// Read all bytes, return ByteArrayInputStream //
			InputStream stream = url.openStream();

			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			int count=0;
			byte[] data = new byte[4096];
			while (count!=-1)
			{
				count = stream.read(data);
				if(count!=-1) { buffer.write(data, 0, count); }
			}
			return new ByteArrayInputStream(buffer.toByteArray());
		}
		catch (Exception e)
		{
			System.out.println(resource + " Not Found");
			return super.getResourceAsStream(resource);
		}
	}


	public URL getResource(String resource)
	{
		if(resource.startsWith("/"))
		{
			resource = resource.substring(1);
			if(resource.startsWith("/"))
			{
				resource = resource.substring(1);
			}
		}
		try
		{
			URL url = findResource(resource);
			return url;
		}
		catch (Exception e)
		{
			System.out.println(resource + " Not Found");
			return super.getResource(resource);
		}
	}

	@Override
	public URL findResource(String name) {
		// First, try to find the resource with the original, case-sensitive name
		URL resource = super.findResource(name);
		if (resource != null) {
			return resource;
		}

		// For each URL, check if it is a JAR file and perform a case-insensitive search
		for (URL url : getURLs()) {
			resource = findResourceInJar(url, name);
			if (resource != null) {
				return resource;
			}
		}

		// If not found, return null
		return null;
	}

	private URL findResourceInJar(URL jarUrl, String resourceName) {
		if (jarUrl.getProtocol().equals("file") && jarUrl.getPath().endsWith(".jar")) {
			try (JarFile jarFile = new JarFile(new File(jarUrl.toURI()))) {
				Enumeration<JarEntry> entries = jarFile.entries();
				while (entries.hasMoreElements()) {
					JarEntry entry = entries.nextElement();
					String entryName = entry.getName();
					if (entryName.equalsIgnoreCase(resourceName)) {
						// Construct the URL for the found resource
						String jarEntryUrl = "jar:" + jarUrl.toExternalForm() + "!/" + entryName;
						return new URL(jarEntryUrl);
					}
				}
			} catch (URISyntaxException | IOException e) {
				// Handle exceptions as needed
				e.printStackTrace();
			}
		}
		return null;
	}

	/*
		********  loadClass Modifies Methods with ObjectWeb ASM  ********
		Replaces java.lang.Class.getResourceAsStream calls with calls
		to Mobile.getResourceAsStream which calls
		MIDletLoader.getResourceAsStream(class, string)
	*/

	public InputStream getMIDletResourceAsStream(String resource)
	{
		//System.out.println("Get Resource: "+resource);

		URL url = getResource(resource);

		// Read all bytes, return ByteArrayInputStream //
		try
		{
			InputStream stream = url.openStream();

			// zb3: why not return a stream? or a bufferedinputstream for marks?

			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			int count=0;
			byte[] data = new byte[4096];
			while (count!=-1)
			{
				count = stream.read(data);
				if(count!=-1) { buffer.write(data, 0, count); }
			}
			return new ByteArrayInputStream(buffer.toByteArray());
		}
		catch (Exception e)
		{
			return super.getResourceAsStream(resource);
		}
	}

	public byte[] getMIDletResourceAsByteArray(String resource)
	{
		URL url = getResource(resource);

		try
		{
			InputStream stream = url.openStream();

			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			int count=0;
			byte[] data = new byte[4096];
			while (count!=-1)
			{
				count = stream.read(data);
				if(count!=-1) { buffer.write(data, 0, count); }
			}
			return buffer.toByteArray();
		}
		catch (Exception e)
		{
			System.out.println(resource + " Not Found");
			return new byte[0];
		}
	}


	public Class loadClass(String name) throws ClassNotFoundException
	{
		InputStream stream;
		String resource;
		byte[] code;

		Class c = this.findLoadedClass(name);
		if (c != null) {
			return c;
		}

		// System.out.println("Load Class "+name);

		// zb3: this needs to be improved as this won't transform games
		// like hypothetical com.nokia.tictactoe
		// but this isn't that simple because games ship shims for builtins
		// we'd not want these to replace our implementations
		if(
			name.startsWith("java.") || name.startsWith("javax.") || name.startsWith("com.nokia") ||
			name.startsWith("com.mascotcapsule") || name.startsWith("com.samsung") || name.startsWith("sun.") ||
			name.startsWith("com.siemens") || name.startsWith("org.recompile") || name.startsWith("jdk.") ||
			name.startsWith("com.vodafone.") || name.startsWith("com.jblend.") || name.startsWith("com.motorola.") || name.startsWith("com.sprintpcs.") || name.startsWith("org."))
		{
			return loadClass(name, true);
		}

		try
		{
			//System.out.println("Instrumenting Class "+name);
			resource = name.replace(".", "/") + ".class";
			stream = super.getResourceAsStream(resource);
			code = instrument(stream);
			return defineClass(name, code, 0, code.length);
		}
		catch (Exception e)
		{
			System.out.println("Error Adapting Class "+name);
			System.out.println(e.toString());
			return null;
		}

	}


/* **************************************************************
 * Special Siemens Stuff
 * ************************************************************** */

	public InputStream getMIDletResourceAsSiemensStream(String resource)
	{
		URL url = getResource(resource);

		try
		{
			InputStream stream = url.openStream();

			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			int count=0;
			byte[] data = new byte[4096];
			while (count!=-1)
			{
				count = stream.read(data);
				if(count!=-1) { buffer.write(data, 0, count); }
			}
			return new SiemensInputStream(buffer.toByteArray());
		}
		catch (Exception e)
		{
			return super.getResourceAsStream(resource);
		}
	}

	private class SiemensInputStream extends InputStream
	{
		private ByteArrayInputStream iostream;

		public SiemensInputStream(byte[] data)
		{
			iostream = new ByteArrayInputStream(data);
		}

		public int read()
		{
			int t = iostream.read();
			if (t == -1) { return 0; }
			return t;
		}
		public int read(byte[] b, int off, int len)
		{
			int t = iostream.read(b, off, len);
			if (t == -1) { return 0; }
			return t;
		}
	}


/* **************************************************************
 * Instrumentation
 * ************************************************************** */

	private byte[] instrument(InputStream stream) throws Exception
	{
		ClassReader reader = new ClassReader(stream);
		ClassWriter writer = new ClassWriter(0);
		ClassVisitor visitor = new ASMVisitor(writer);
		reader.accept(visitor, ClassReader.SKIP_DEBUG);
		return writer.toByteArray();
	}

	private class ASMVisitor extends ClassAdapter
	{
		public ASMVisitor(ClassVisitor visitor)
		{
			super(visitor);
		}

		public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces)
		{
			super.visit(version, access, name, signature, superName, interfaces);
		}

		public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions)
		{
			return new ASMMethodVisitor(super.visitMethod(access, name, desc, signature, exceptions));
		}

		private class ASMMethodVisitor extends MethodAdapter implements Opcodes
		{
			public ASMMethodVisitor(MethodVisitor visitor)
			{
				super(visitor);
			}

			public void visitMethodInsn(int opcode, String owner, String name, String desc)
			{
				if(opcode == INVOKEVIRTUAL && name.equals("getResourceAsStream") && owner.equals("java/lang/Class"))
				{
					mv.visitMethodInsn(INVOKESTATIC, "org/recompile/mobile/Mobile", name, "(Ljava/lang/Class;Ljava/lang/String;)Ljava/io/InputStream;");
				}
				else if (opcode == INVOKESTATIC && name.equals("setListener") && owner.equals("com/siemens/mp/io/Connection")) {
					mv.visitMethodInsn(opcode, owner, "setListenerCompat", desc);
				}
				else
				{
					mv.visitMethodInsn(opcode, owner, name, desc);
				}
			}
		}
	}
}
