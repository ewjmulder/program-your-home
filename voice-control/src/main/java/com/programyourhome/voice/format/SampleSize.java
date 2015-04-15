package com.programyourhome.voice.format;

public enum SampleSize {

    ONE_BYTE(1),
    TWO_BYTES(2);

    private int numberOfBytes;

    private SampleSize(final int numberOfBytes) {
        this.numberOfBytes = numberOfBytes;
    }

    public int getNumberOfBytes() {
        return this.numberOfBytes;
    }

    public int getNumberOfBits() {
        return this.numberOfBytes * 8;
    }
}
