/*
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
package pl.zb3.freej2me.audio;

import java.io.IOException;
import java.io.InputStream;

import javax.sound.midi.InvalidMidiDataException;


public class MIDIFilePlayer extends MIDIPlayer
{
	private String contentType = "";
	private InputStream stream;

	public MIDIFilePlayer(InputStream stream, String type)
	{
		super();
		contentType = type;
		this.stream = stream;
	}

	@Override	
	public void doPrefetch() throws IOException {
		if (midi == null) return;
		if (loaded) return;

		try {
			midi.setSequence(stream);
			loaded = true;
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String doGetContentType() {
		return contentType;
	}

}

