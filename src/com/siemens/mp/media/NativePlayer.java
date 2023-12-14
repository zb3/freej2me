package com.siemens.mp.media;

public class NativePlayer implements Player {

    @Override
    public void realize() throws MediaException {
    }

    @Override
    public void prefetch() throws MediaException {
    }

    @Override
    public void start() throws MediaException {
    }

    @Override
    public void stop() {
    }

    @Override
    public void deallocate() {
    }

    @Override
    public void close() {
    }

    @Override
    public long setMediaTime(long now) throws MediaException {
        return 0;
    }

    @Override
    public long getMediaTime() {
        return 0;
    }

    @Override
    public long getDuration() {
        return 0;
    }

    @Override
    public void setLoopCount(int count) {
    }

    @Override
    public int getState() {
        return 0;
    }

    @Override
    public void addPlayerListener(PlayerListener playerListener) {
    }

    @Override
    public void removePlayerListener(PlayerListener playerListener) {
    }

    @Override
    public String getContentType() {
        return "";
    }

    @Override
    public Control getControl(String controlType) {
        return null;
    }

    @Override
    public Control[] getControls() {
        return new Control[]{};
    }
}
