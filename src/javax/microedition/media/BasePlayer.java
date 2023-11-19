/*
 * Copyright 2018 Nikita Shakarun
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

package javax.microedition.media;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.microedition.media.control.VolumeControl;

public class BasePlayer implements Player, VolumeControl {
	private TimeBase timeBase;
	protected int state;
	private int loopCount;

	private final ArrayList<PlayerListener> listeners;
	private final HashMap<String, Control> controls;
	private boolean mute;
	private int level;

	public BasePlayer() {
		state = UNREALIZED;

		mute = false;
		level = 100;
		loopCount = 1;

		listeners = new ArrayList<>();
		controls = new HashMap<>();

		controls.put(VolumeControl.class.getName(), this);
	}

	public void addControl(String name, Control control) {
		controls.put(name, control);
	}

	public void complete() {
		if (state == CLOSED) {
			return;
		}
		postEvent(PlayerListener.END_OF_MEDIA, Long.valueOf(getMediaTime()));

		if (loopCount == 1) {
			state = PREFETCHED;
			doReset();
		} else if (loopCount > 1) {
			loopCount--;
		}

		if (state == STARTED && loopCount != -1) {
			doStart();
			postEvent(PlayerListener.STARTED, Long.valueOf(getMediaTime()));
		}
	}

	@Override
	public Control getControl(String controlType) {
		checkRealized();
		if (!controlType.contains(".")) {
			controlType = "javax.microedition.media.control." + controlType;
		}
		return controls.get(controlType);
	}

	@Override
	public Control[] getControls() {
		checkRealized();
		return controls.values().toArray(new Control[0]);
	}

	@Override
	public void addPlayerListener(PlayerListener playerListener) {
		checkClosed();
		if (!listeners.contains(playerListener) && playerListener != null) {
			listeners.add(playerListener);
		}
	}

	@Override
	public void removePlayerListener(PlayerListener playerListener) {
		checkClosed();
		listeners.remove(playerListener);
	}

	public synchronized void postEvent(String event, Object eventData) {
		for (PlayerListener listener : listeners) {
			// Callbacks should be async
			Runnable r = () -> listener.playerUpdate(this, event, eventData);
			(new Thread(r, "MIDletPlayerCallback")).start();
		}
	}

	@Override
	public synchronized void realize() throws MediaException {
		checkClosed();

		if (state == UNREALIZED) {
			try {
				doRealize();
			} catch (IOException e) {
				throw new MediaException(e.getMessage());
			}

			state = REALIZED;
		}
	}

	@Override
	public synchronized void prefetch() throws MediaException {
		checkClosed();

		if (state == UNREALIZED) {
			realize();
		}

		if (state == REALIZED) {
			try {
				doPrefetch();
			} catch (Exception e) {
				e.printStackTrace();
			}
			state = PREFETCHED;
		}
	}

	@Override
	public synchronized void start() throws MediaException {
		prefetch();

		if (state == PREFETCHED) {
			doStart();

			state = STARTED;
			postEvent(PlayerListener.STARTED, Long.valueOf(getMediaTime()));
		}
	}

	@Override
	public synchronized void stop() {
		checkClosed();
		if (state == STARTED) {
			doStop();

			state = PREFETCHED;
			postEvent(PlayerListener.STOPPED, Long.valueOf(getMediaTime()));
		}
	}

	@Override
	public synchronized void deallocate() {
		stop();

		if (state == PREFETCHED) {
			doReset();
			state = UNREALIZED;

			try {
				realize();
			} catch (MediaException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public synchronized void close() {
		if (state != CLOSED) {
			deallocate();
			doClose();

			state = CLOSED;
			postEvent(PlayerListener.CLOSED, null);
		}
	}

	protected void checkClosed() {
		if (state == CLOSED) {
			throw new IllegalStateException("player is closed");
		}
	}

	protected void checkRealized() {
		checkClosed();

		if (state == UNREALIZED) {
			throw new IllegalStateException("call realize() before using the player");
		}
	}

	@Override
	public long setMediaTime(long now) throws MediaException {
		checkRealized();
		if (state < PREFETCHED) {
			return 0;
		} else {
			if (now != doGetMediaTime()) {
				doSetMediaTime(now);
			}
			return getMediaTime();
		}
	}

	@Override
	public long getMediaTime() {
		checkClosed();
		if (state < PREFETCHED) {
			return TIME_UNKNOWN;
		} else {
			return doGetMediaTime();
		}
	}

	@Override
	public long getDuration() {
		checkClosed();
		if (state < PREFETCHED) {
			return TIME_UNKNOWN;
		} else {
			return doGetDuration();
		}
	}

	@Override
	public void setLoopCount(int count) {
		checkClosed();
		if (state == STARTED)
			throw new IllegalStateException("player must not be in STARTED state while using setLoopCount()");

		if (count == 0) {
			throw new IllegalArgumentException("loop count must not be 0");
		}

		doSetLooping(count == -1);
		loopCount = count;
	}

	@Override
	public int getState() {
		return state;
	}

	@Override
	public String getContentType() {
		checkRealized();
		return doGetContentType();
	}

	// VolumeControl

	private void updateVolume() {
		float volume;

		if (mute) {
			volume = 0;
		} else {
			if (level == 100) {
				volume = 1.0f;
			} else {
				volume = (float) (1 - (Math.log(100 - level) / Math.log(100)));
			}
		}

        doSetVolume(volume);
		postEvent(PlayerListener.VOLUME_CHANGED, this);
	}

	@Override
	public void setMute(boolean mute) {
		if (state == CLOSED) {
			// Avoid IllegalStateException in MediaPlayer.setVolume()
			return;
		}

		this.mute = mute;
		updateVolume();
	}

	@Override
	public boolean isMuted() {
		return mute;
	}

	@Override
	public int setLevel(int level) {
		if (state == CLOSED) {
			// Avoid IllegalStateException in MediaPlayer.setVolume()
			return this.level;
		}

		if (level < 0) {
			level = 0;
		} else if (level > 100) {
			level = 100;
		}

		this.level = level;
		updateVolume();

		return level;
	}

	@Override
	public int getLevel() {
		return level;
	}

	//@Override
	public TimeBase getTimeBase() {
		if (timeBase == null) {
			return Manager.getSystemTimeBase();
		}
		return timeBase;
	}

	public void setTimeBase(TimeBase master) throws MediaException {
		timeBase = master;
	}

	// Implementation-defined methods

	public void doRealize() throws IOException {
	}

	public void doPrefetch()  throws IOException {
	}

	public void doStart() {
	}

	public void doStop() {
	}

	public void doClose() {
	}

	public void doReset() {
	}

	public void doSetMediaTime(long usec) {
	}

	public long doGetMediaTime() {
		return 0;
	}

	public long doGetDuration() {
		return 0;
	}

	public void doSetLooping(boolean looping) {
	}

	public String doGetContentType() {
		return "";
	}

	public void doSetVolume(float vol) {
	}
}
