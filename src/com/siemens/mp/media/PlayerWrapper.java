package com.siemens.mp.media;

import com.siemens.mp.media.control.VolumeControlWrapper;

public class PlayerWrapper implements Player {

    private final javax.microedition.media.Player delegate;

    public PlayerWrapper(javax.microedition.media.Player delegate) {
        this.delegate = delegate;
    }

    @Override
    public void realize() throws MediaException {
        try {
            delegate.realize();
        } catch (javax.microedition.media.MediaException e) {
            throw new MediaException(e.getMessage());
        }
    }

    @Override
    public void prefetch() throws MediaException {
        try {
            delegate.prefetch();
        } catch (javax.microedition.media.MediaException e) {
            throw new MediaException(e.getMessage());
        }
    }

    @Override
    public void start() throws MediaException {
        try {
            delegate.start();
        } catch (javax.microedition.media.MediaException e) {
            throw new MediaException(e.getMessage());
        }
    }

    @Override
    public void stop() {
        delegate.stop();
    }

    @Override
    public void deallocate() {
        delegate.deallocate();
    }

    @Override
    public void close() {
        delegate.close();
    }

    @Override
    public long setMediaTime(long now) throws MediaException {
        try {
            return delegate.setMediaTime(now);
        } catch (javax.microedition.media.MediaException e) {
            throw new MediaException(e.getMessage());
        }
    }

    @Override
    public long getMediaTime() {
        return delegate.getMediaTime();
    }

    @Override
    public long getDuration() {
        return delegate.getDuration();
    }

    @Override
    public void setLoopCount(int count) {
        delegate.setLoopCount(count);
    }

    @Override
    public int getState() {
        return delegate.getState();
    }

    @Override
    public void addPlayerListener(PlayerListener playerListener) {
        delegate.addPlayerListener(playerListener);
    }

    @Override
    public void removePlayerListener(PlayerListener playerListener) {
        delegate.removePlayerListener(playerListener);
    }

    @Override
    public String getContentType() {
        return delegate.getContentType();
    }

    @Override
    public Control getControl(String controlType) {
        if (controlType.contains("VolumeControl")) {
            return new VolumeControlWrapper((javax.microedition.media.control.VolumeControl)delegate.getControl("VolumeControl"));
        }
        return null;
    }

    @Override
    public Control[] getControls() {
        return new Control[]{};
    }
}