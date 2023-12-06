package pl.zb3.freej2me.audio;

import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

import pl.zb3.freej2me.NativeLoader;

/*
import java.io.*;

class FileDumpInputStream extends InputStream {
    private static int instanceCounter = 0;

    private InputStream originalInputStream;
    private String fileName;
    private FileInputStream fileInputStream;

    public FileDumpInputStream(InputStream originalInputStream) {
        this.originalInputStream = originalInputStream;
        this.fileName = "dumpedFile" + instanceCounter + ".tmp";
        instanceCounter++;
        dumpToFile();
        openFileInputStream();
    }

    private void dumpToFile() {
        try (FileOutputStream fileOutputStream = new FileOutputStream(fileName)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = originalInputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openFileInputStream() {
        try {
            fileInputStream = new FileInputStream(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int read() throws IOException {
        return fileInputStream.read();
    }

    @Override
    public int read(byte[] b) throws IOException {
        return fileInputStream.read(b);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return fileInputStream.read(b, off, len);
    }

    @Override
    public long skip(long n) throws IOException {
        return fileInputStream.skip(n);
    }

    @Override
    public int available() throws IOException {
        return fileInputStream.available();
    }

    @Override
    public void close() throws IOException {
        try {
            super.close();
        } finally {
            fileInputStream.close();
        }
    }
}
*/


class FFAudioInternalStream extends InputStream {
    private long stateHandle;

    public FFAudioInternalStream(long stateHandle) {
        this.stateHandle = stateHandle;
    }

    public int read() throws IOException {
        byte[] data = new byte[1];
        int temp = this.read(data);
        return temp <= 0 ? -1 : data[0] & 255;
    }

    public native int read(byte[] b, int off, int len) throws IOException;    
    public native void close() throws IOException;
}


public class FFAudioInputStream extends AudioInputStream {
    public FFAudioInputStream(InputStream stream, AudioFormat format, long length) {
        super(stream, format, length);
    }

    /*
    public static FFAudioInputStream doInit(InputStream stream, AudioFormat format, long length) {
        return new FFAudioInputStream(new FileDumpInputStream(stream), format, length);

    }*/

    public static native FFAudioInputStream load(InputStream is, String contentType);   

    static {
        NativeLoader.loadLibrary("ffaudio");
    }
}
