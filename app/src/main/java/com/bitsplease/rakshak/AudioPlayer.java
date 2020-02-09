package com.bitsplease.rakshak;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

import static android.content.ContentValues.TAG;
import static android.os.Process.THREAD_PRIORITY_AUDIO;
import static android.os.Process.setThreadPriority;


public class AudioPlayer {
    private final InputStream mInputStream;

    private volatile boolean mAlive;


    private Thread mThread;


    public AudioPlayer(InputStream inputStream) {
        mInputStream = inputStream;
    }


    public boolean isPlaying() {
        return mAlive;
    }


    public void start() {
        mAlive = true;
        mThread =
                new Thread() {
                    @Override
                    public void run() {
                        setThreadPriority(THREAD_PRIORITY_AUDIO);

                        Buffer buffer = new Buffer();
                        AudioTrack audioTrack =
                                new AudioTrack(
                                        AudioManager.STREAM_MUSIC,
                                        buffer.sampleRate,
                                        AudioFormat.CHANNEL_OUT_MONO,
                                        AudioFormat.ENCODING_PCM_16BIT,
                                        buffer.size,
                                        AudioTrack.MODE_STREAM);
                        audioTrack.play();

                        int len;
                        try {
                            while (isPlaying() && (len = mInputStream.read(buffer.data)) > 0) {
                                audioTrack.write(buffer.data, 0, len);
                            }
                        } catch (IOException e) {
                            Log.e(TAG, "Exception with playing stream", e);
                        } finally {
                            stopInternal();
                            audioTrack.release();
                            onFinish();
                        }
                    }
                };
        mThread.start();
    }

    private void stopInternal() {
        mAlive = false;
        try {
            mInputStream.close();
        } catch (IOException e) {
            Log.e(TAG, "Failed to close input stream", e);
        }
    }

    /** Stops playing the stream. */
    public void stop() {
        stopInternal();
        try {
            mThread.join();
        } catch (InterruptedException e) {
            Log.e(TAG, "Interrupted while joining AudioRecorder thread", e);
            Thread.currentThread().interrupt();
        }
    }


    protected void onFinish() {}

    private static class Buffer extends AudioBuffer {
        @Override
        protected boolean validSize(int size) {
            return size != AudioTrack.ERROR && size != AudioTrack.ERROR_BAD_VALUE;
        }

        @Override
        protected int getMinBufferSize(int sampleRate) {
            return AudioTrack.getMinBufferSize(
                    sampleRate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
        }
    }
}
