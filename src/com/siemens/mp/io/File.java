/*
 *  Copyright 2018 Nikita Shakarun
 *  Copyright 2021 Yury Kharchenko
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

package com.siemens.mp.io;

import java.io.IOException;
import java.util.Arrays;

public class File {
	public static final int INSIDE_STORAGE_PATH = 1;
	public static final int OUTSIDE_STORAGE_PATH = 0;
	public static final String STORAGE_DRIVE = "a:";

	public static String buildPath(String fileName) {
		return fileName;
	}

	public static int checkFileName(String fileName) {
		return fileName.indexOf(':') == -1 ? INSIDE_STORAGE_PATH : OUTSIDE_STORAGE_PATH;
	}

	public int close(int fileDescriptor) throws IOException {
		return -1;
	}

	public static int copy(String source, String dest) throws IOException {
		return 1;
	}

	public static int debugWrite(String fileName, String infoString) throws IOException {
		return 1;
	}

	public static int delete(String fileName) {
		return -1;
	}

	public static int exists(String fileName) throws IOException {
		return -1;
	}

	public static boolean getIsHidden(String fileName) throws IOException {
		return false;
	}

	public static long getLastModified(String fileName) throws IOException {
		return 0;
	}

	public static boolean isDirectory(String fileName) throws IOException {
		return false;
	}

	public int length(int fileDescriptor) throws IOException {
		return -1;
	}

	public static String[] list(String pathName) throws IOException {
		return list(pathName, false);
	}

	public static String[] list(String pathName, boolean includeHidden) throws IOException {
		return new String[]{};
	}

	public int open(String fileName) throws IOException {
		return -1;
	}

	public int read(int fileDescriptor, byte[] buf, int offset, int numBytes) throws IOException {
		return -1;
	}

	public static int rename(String source, String dest) {
		return -1;
	}

	public int seek(int fileDescriptor, int seekpos) throws IOException {
		return -1;
	}

	public static int spaceAvailable() throws IOException {
		return 0;
	}

	public static void truncate(int fileDescriptor, int size) throws IOException {
	}

	public int write(int fileDescriptor, byte[] buf, int offset, int numBytes) throws IOException {
		return -1;
	}
}