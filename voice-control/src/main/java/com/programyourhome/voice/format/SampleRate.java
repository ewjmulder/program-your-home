package com.programyourhome.voice.format;

public enum SampleRate {

    LOW(16000),
    HIGH(44100);

    private int numberOfSamplesPerSecond;

    private SampleRate(final int numberOfSamplesPerSecond) {
        this.numberOfSamplesPerSecond = numberOfSamplesPerSecond;
    }

    public int getNumberOfSamplesPerSecond() {
        return this.numberOfSamplesPerSecond;
    }

}
