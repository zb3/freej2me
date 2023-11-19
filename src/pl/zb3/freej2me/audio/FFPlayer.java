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
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.microedition.media.Player;
import javax.microedition.media.BasePlayer;


public class FFPlayer extends BasePlayer implements LineListener
{
	private Clip clip;
	private long time = 0L;
	private String contentType = "";
	private InputStream stream;
	private FloatControl gainControl;

	public FFPlayer(InputStream stream, String type)
	{
		contentType = type;
		this.stream = stream;
	}

	// doRealize does nothing, the work is done on prefetch

	@Override	
	public void doPrefetch() throws IOException {
		// we can't repeat this, so simple ignore deallocate requests
		if (clip != null) return;

		AudioInputStream ais = FFAudioInputStream.load(stream, contentType);

		try {
			if (ais != null) {
				clip = AudioSystem.getClip();
				clip.open(ais);
				state = Player.PREFETCHED;
			}
		} catch (Exception e) { 
			System.out.println("Couldn't load wav file: " + e.getMessage());
			e.printStackTrace();

			if (clip != null)
			clip.close();
		} finally {
			// this assumes that the clip does a full copy on open
			// because the close method is not called otherwise.
			if (ais != null)
			ais.close();
		}

		if (clip != null) {
			gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

			clip.addLineListener(this);
		}
	}

	@Override
	public void doStart() {
		if (clip == null) return;

		if(clip.getFramePosition() >= clip.getFrameLength()) {
			clip.setFramePosition(0);
		}
		time = clip.getMicrosecondPosition();
		clip.start();
	}

	@Override
	public void doStop() {
		if (clip == null) return;

		clip.stop();
		time = clip.getMicrosecondPosition();
	}

	// doReset intentionally does nothing

	@Override
	public void doClose() {
		// note the outer class has called stop before we get here
		if (clip == null) return;
		clip.close();
		clip = null;
	}

	@Override
	public void doSetMediaTime(long now) {
		if (clip == null) return;

		System.out.println("KOKO clip set position "+now);
		clip.setMicrosecondPosition(now);
	}

	@Override
	public long doGetMediaTime() {
		if (clip == null) return 0L;

		return clip.getMicrosecondPosition();
	}

	@Override
	public long doGetDuration() {
		if (clip == null) return Player.TIME_UNKNOWN;

		return clip.getMicrosecondLength();
	}

	@Override
	public void doSetLooping(boolean looping) {
		/*
		 * this is only for infinite looping
		 * for finite loops, the BasePlayer needs the completion callback..
		 */
		if (clip == null) return;

		clip.loop(looping ? -1 : 0);
	}

	@Override
	public String doGetContentType() {
		return contentType;
	}

    private static float linearToLog(float linear) {
        return (float) (20.0 * Math.log10(linear));
    }

	@Override
	public void doSetVolume(float vol) {
		if (gainControl != null)
		gainControl.setValue(linearToLog(vol));
	}

	@Override
	public void update(LineEvent event) {
		if (state == STARTED && event.getType() == LineEvent.Type.STOP) {
			complete();
		}
	}
}
