package com.programyourhome.voice.format;

public enum RecordingMode {

    MONO(1),
    STEREO(2);

    private int numberOfChannels;

    private RecordingMode(final int numberOfChannels) {
        this.numberOfChannels = numberOfChannels;
    }

    public int getNumberOfChannels() {
        return this.numberOfChannels;
    }

}
