/*
 *  Siemens API for MicroEmulator
 *  Copyright (C) 2003 Markus Heberling <markus@heberling.net>
 *
 *  It is licensed under the following two licenses as alternatives:
 *    1. GNU Lesser General Public License (the "LGPL") version 2.1 or any newer version
 *    2. Apache License (the "AL") Version 2.0
 *
 *  You may not use this file except in compliance with at least one of
 *  the above two licenses.
 *
 *  You may obtain a copy of the LGPL at
 *      http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 *
 *  You may obtain a copy of the AL at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the LGPL or the AL for the specific language governing permissions and
 *  limitations.
 */
package com.siemens.mp.media;

import com.siemens.mp.media.protocol.DataSource;

import java.io.IOException;
import java.io.InputStream;

public final class Manager {

	public static String[] getSupportedContentTypes(String protocol) {
		return javax.microedition.media.Manager.getSupportedContentTypes(protocol);
	}

	public static String[] getSupportedProtocols(String content_type) {
		return javax.microedition.media.Manager.getSupportedProtocols(content_type);
	}

	public static Player createPlayer(String locator) throws IOException, MediaException {
		try {
            return new PlayerWrapper(javax.microedition.media.Manager.createPlayer(locator));
        } catch (javax.microedition.media.MediaException e) {
            throw new MediaException(e.getMessage());
        }
	}

	public static Player createPlayer(InputStream stream, String type) throws IOException, MediaException {
		try {
            return new PlayerWrapper(javax.microedition.media.Manager.createPlayer(stream, type));
        } catch (javax.microedition.media.MediaException e) {
            throw new MediaException(e.getMessage());
        }
	}

	public static Player createPlayer(DataSource source) throws IOException, MediaException {
		throw new MediaException("Not implemented");
	}

	public static void playTone(int note, int duration, int volume) throws MediaException {
		try {
            javax.microedition.media.Manager.playTone(note, duration, volume);
        } catch (javax.microedition.media.MediaException e) {
            throw new MediaException(e.getMessage());
        }
	}
}

