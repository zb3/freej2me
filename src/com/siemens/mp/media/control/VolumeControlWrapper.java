package com.siemens.mp.media.control;

public class VolumeControlWrapper implements VolumeControl {
    private javax.microedition.media.control.VolumeControl delegate;

    public VolumeControlWrapper(javax.microedition.media.control.VolumeControl delegate) {
        this.delegate = delegate;
    }

    @Override
    public int getLevel() {
        return delegate.getLevel();
    }

    @Override
    public boolean isMuted() {
        return delegate.isMuted();
    }

    @Override
    public int setLevel(int level) {
        return delegate.setLevel(level);
    }

    @Override
    public void setMute(boolean mute) {
        delegate.setMute(mute);
    }
}
