package com.programyourhome.voice.detection;

import java.nio.ByteBuffer;

import com.programyourhome.voice.format.PyhAudioFormat;
import com.programyourhome.voice.format.SampleSize;

public class AudioFrame {

    // Provided values.
    private final PyhAudioFormat audioFormat;
    private final int frameNumber;
    private final byte[] frameBytes;

    // Derived values.
    private int millisSinceStart;
    private int[] valuesPerChannel;
    private byte volumePercentage;

    public AudioFrame(final PyhAudioFormat audioFormat, final int frameNumber, final byte[] frameBytes) {
        this.audioFormat = audioFormat;
        this.frameNumber = frameNumber;
        this.frameBytes = frameBytes;
        if (audioFormat.getNumberOfBytesPerFrame() != frameBytes.length) {
            throw new IllegalArgumentException("Number of bytes per frame: " + audioFormat.getNumberOfBytesPerFrame()
                    + " does not match the length of the byte[]: " + frameBytes.length);
        }
        this.calculateDerivedValues();
    }

    // TODO: Unit tests!!
    private void calculateDerivedValues() {
        this.millisSinceStart = (int) ((this.frameNumber / (double) this.audioFormat.getNumberOfFramesPerSecond()) * 1000);
        this.valuesPerChannel = new int[this.audioFormat.getRecordingMode().getNumberOfChannels()];
        final byte[] volumePercentagesPerChannel = new byte[this.valuesPerChannel.length];
        for (int i = 0; i < this.valuesPerChannel.length; i++) {
            if (this.audioFormat.getSampleSize() == SampleSize.ONE_BYTE) {
                this.valuesPerChannel[i] = this.frameBytes[i];
            } else if (this.audioFormat.getSampleSize() == SampleSize.TWO_BYTES) {
                final byte[] sample = new byte[2];
                System.arraycopy(this.frameBytes, i * 2, sample, 0, 2);
                this.valuesPerChannel[i] = ByteBuffer.wrap(sample).order(this.audioFormat.getByteOrder()).getShort();
            } else {
                throw new IllegalStateException("Unknown sample size: " + this.audioFormat.getSampleSize());
            }
            volumePercentagesPerChannel[i] = this.calculateVolumePercentage(this.valuesPerChannel[i]);
        }
        this.volumePercentage = this.calculateAverage(volumePercentagesPerChannel);
    }

    public PyhAudioFormat getAudioFormat() {
        return this.audioFormat;
    }

    public int getFrameNumber() {
        return this.frameNumber;
    }

    public byte[] getFrameBytes() {
        return this.frameBytes;
    }

    public int getMillisSinceStart() {
        return this.millisSinceStart;
    }

    public int[] getValuesPerChannel() {
        return this.valuesPerChannel;
    }

    public byte getVolumePercentage() {
        return this.volumePercentage;
    }

    private byte calculateVolumePercentage(final int value) {
        final int applicableMax = value >= 0 ? this.audioFormat.getMaximumPositiveValue() : this.audioFormat.getMinimumNegativeValue();
        return (byte) ((value / (double) applicableMax) * 100);
    }

    private byte calculateAverage(final byte[] values) {
        short sum = 0;
        for (final byte value : values) {
            sum += value;
        }
        return (byte) (sum / values.length);
    }
}
