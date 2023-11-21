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

import javax.microedition.media.control.MIDIControl;
import javax.microedition.media.control.TempoControl;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;

import pl.zb3.ThreadUtility;

import javax.microedition.media.BasePlayer;

public class MIDIPlayer extends BasePlayer implements MetaEventListener, MIDIControl, TempoControl
{
	protected Sequencer midi;
	protected boolean loaded = false;

	public MIDIPlayer()
	{
		try {
			midi = MidiSystem.getSequencer();
			midi.open();
			midi.addMetaEventListener(this);
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		}

		addControl(MIDIControl.class.getName(), this);
		addControl(TempoControl.class.getName(), this);
		//System.out.println("media type: "+type);
	}

	// doRealize does nothing, the work is done on prefetch

	@Override
	public void doStart() {
		if (!loaded) return;
		midi.start();
	}

	@Override
	public void doStop() {
		if (!loaded) return;

		midi.stop();
	}

	// doReset intentionally does nothing

	@Override
	public void doClose() {
		if (midi != null) {
			midi.removeMetaEventListener(this);
			midi.close();
			midi = null;
			loaded = false;
		}
	}

	@Override
	public void doSetMediaTime(long now) {
		try {
			midi.setTickPosition(now);
		}
		catch (Exception e) { }
	}

	@Override
	public long doGetMediaTime() {
		return midi.getTickPosition();
	}

	@Override
	public long doGetDuration() {
		return midi.getTickLength();
	}

	@Override
	public void doSetLooping(boolean looping) {
		/*
		 * this is only for infinite looping
		 * for finite loops, the BasePlayer needs the completion callback..
		 */
		midi.setLoopCount(looping ? Sequencer.LOOP_CONTINUOUSLY : 0);
	}

	@Override
	public void doSetVolume(float vol) {
		// ??? good luck
	}

	@Override
	public void meta(MetaMessage msg) {
		if (msg.getType() == 47 && !midi.isRunning()) {
			complete();

			// Intervention: if the player has no listeners, we need to close ourselves
			// because the app won't do this so we'll have many sequencers
			// we'll not do this immediately so as to preserve the echo of the note
			if (!hasListeners()) {
				ThreadUtility.run(() -> {
					try {
						Thread.sleep(5000); // wait 5 seconds
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					close();
				});
			}
		}
	}

	// MIDIControl
	@Override
	public int[] getBankList(boolean custom) {
		return new int[0];
	}

	@Override
	public int getChannelVolume(int channel) {
		return -1;
	}

	@Override
	public String getKeyName(int bank, int prog, int key) {
		return null;
	}

	@Override
	public int[] getProgram(int channel) {
		return new int[0];
	}

	@Override
	public int[] getProgramList(int bank) {
		return new int[0];
	}

	@Override
	public String getProgramName(int bank, int prog) {
		return "";
	}

	@Override
	public boolean isBankQuerySupported() {
		return false;
	}

	@Override
	public int longMidiEvent(byte[] data, int offset, int length) {
		if (midi == null) return -1;

		midi.getTransmitters().get(0).getReceiver().send(new RawMessage(data), -1L);
		return data.length;
	}

	@Override
	public void setChannelVolume(int channel, int volume) {
	}

	@Override
	public void setProgram(int channel, int bank, int program) {
		shortMidiEvent(ShortMessage.PROGRAM_CHANGE | channel, bank, program); // program, bank ??
	}

	@Override
	public void shortMidiEvent(int type, int data1, int data2) {
		if (midi == null) return;

		try {
			midi.getTransmitters().get(0).getReceiver().send(new ShortMessage(type, data1, data2), -1L);
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
	}

	// TempoControl
	@Override
	public int getTempo() { 
		if (midi == null) return 0;
		return (int)midi.getTempoInBPM() * 1000;
	}

	public int setTempo(int millitempo) {
		midi.setTempoInBPM(millitempo / 1000);
		return millitempo;
	}

	public int getMaxRate() { return 100000; }

	public int getMinRate() { return 100000; }

	public int getRate() { return 100000; }

	public int setRate(int millirate) { return 100000; }
}


class RawMessage extends MidiMessage {
	public RawMessage(byte[] data) {
		super(data);
	}

	 @Override
    public Object clone() {
        byte[] newData = new byte[length];
        System.arraycopy(data, 0, newData, 0, newData.length);
        return new RawMessage(newData);
    }
}