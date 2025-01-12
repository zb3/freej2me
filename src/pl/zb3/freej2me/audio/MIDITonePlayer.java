/*
 * Copyright 2020 Nikita Shakarun
 *
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.microedition.media.control.ToneControl;
import javax.microedition.media.tone.ToneSequence;
import javax.sound.midi.InvalidMidiDataException;


public class MIDITonePlayer extends MIDIPlayer implements ToneControl
{
    private byte[] midiSequence;
	private long duration;

	public MIDITonePlayer()
	{
		super();
        addControl(ToneControl.class.getName(), this);
	}

	public MIDITonePlayer(InputStream stream)
	{
		this();

		try {
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			int nRead;
			byte[] data = new byte[16384];
			while ((nRead = stream.read(data, 0, data.length)) != -1) {
				buffer.write(data, 0, nRead);
			}
			buffer.flush();

			midiSequence = buffer.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

    @Override
	public long doGetDuration() {
		return duration;
	}

    // ToneControl
    @Override
	public void setSequence(byte[] sequence) {
        if (midi == null) return;

        try {
			ToneSequence tone = new ToneSequence(sequence);
			tone.process();
			midiSequence = tone.getByteArray();
			duration = tone.getDuration();
		} catch (Exception e) {
			e.printStackTrace();
		}

        try {
			midi.setSequence(new ByteArrayInputStream(midiSequence));
			loaded = true;
		} catch (InvalidMidiDataException | IOException e) {
			e.printStackTrace();
		}
	}

}

