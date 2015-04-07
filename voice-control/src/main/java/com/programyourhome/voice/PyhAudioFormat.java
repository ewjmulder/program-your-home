package com.programyourhome.voice;

import javaFlacEncoder.StreamConfiguration;

import javax.sound.sampled.AudioFormat;

public class PyhAudioFormat {

    public static final int DEFAULT_SAMPLE_RATE = 16000;
    public static final int DEFAULT_SAMPLE_SIZE = 8;
    public static final int DEFAULT_NUMBER_OF_CHANNELS = 1;
    public static final boolean DEFAULT_SIGNED = true;
    public static final boolean DEFAULT_BIG_ENDIAN = false;

    private final int sampleRate;
    private final int sampleSizeInBits;
    private final int numberOfChannels;
    private final boolean signed;
    private final boolean bigEndian;

    public static PyhAudioFormat getDefault() {
        return new PyhAudioFormat(DEFAULT_SAMPLE_RATE, DEFAULT_SAMPLE_SIZE, DEFAULT_NUMBER_OF_CHANNELS, DEFAULT_SIGNED, DEFAULT_BIG_ENDIAN);
    }

    public PyhAudioFormat(final int sampleRate, final int sampleSizeInBits, final int numberOfChannels, final boolean signed, final boolean bigEndian) {
        this.sampleRate = sampleRate;
        this.sampleSizeInBits = sampleSizeInBits;
        this.numberOfChannels = numberOfChannels;
        this.signed = signed;
        this.bigEndian = bigEndian;
    }

    public AudioFormat getJavaAudioFormat() {
        return new AudioFormat(this.sampleRate, this.sampleSizeInBits, this.numberOfChannels, this.signed, this.bigEndian);
    }

    public StreamConfiguration getStreamConfiguration() {
        final StreamConfiguration streamConfiguration = new StreamConfiguration();
        streamConfiguration.setBitsPerSample(this.sampleSizeInBits);
        streamConfiguration.setChannelCount(this.numberOfChannels);
        // TODO: what to enter here?
        // streamConfiguration.setMaxBlockSize(size)
        streamConfiguration.setSampleRate(this.sampleRate);
        return streamConfiguration;
    }

    public int getSampleRate() {
        return this.sampleRate;
    }

    public int getSampleSizeInBits() {
        return this.sampleSizeInBits;
    }

    public int getNumberOfChannels() {
        return this.numberOfChannels;
    }

    public boolean isSigned() {
        return this.signed;
    }

    public boolean isBigEndian() {
        return this.bigEndian;
    }

    public int getFrameSize() {
        return this.getJavaAudioFormat().getFrameSize();
    }

}
