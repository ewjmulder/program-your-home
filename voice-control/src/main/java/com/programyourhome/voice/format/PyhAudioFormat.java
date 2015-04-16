package com.programyourhome.voice.format;

import java.nio.ByteOrder;
import javaFlacEncoder.StreamConfiguration;

import javax.sound.sampled.AudioFormat;

public class PyhAudioFormat {

    public static final SampleRate DEFAULT_SAMPLE_RATE = SampleRate.HIGH;
    public static final SampleSize DEFAULT_SAMPLE_SIZE = SampleSize.TWO_BYTES;
    public static final RecordingMode DEFAULT_RECORDING_MODE = RecordingMode.MONO;
    public static final ByteOrder DEFAULT_BYTE_ORDER = ByteOrder.BIG_ENDIAN;

    private final SampleRate sampleRate;
    private final SampleSize sampleSize;
    private final RecordingMode recordingMode;
    private final boolean signed;
    private final ByteOrder byteOrder;

    public static PyhAudioFormat getDefault() {
        return new PyhAudioFormat(DEFAULT_SAMPLE_RATE, DEFAULT_SAMPLE_SIZE, DEFAULT_RECORDING_MODE, DEFAULT_BYTE_ORDER);
    }

    public PyhAudioFormat(final SampleRate sampleRate, final SampleSize sampleSize,
            final RecordingMode recordingMode, final ByteOrder byteOrder) {
        this.sampleRate = sampleRate;
        this.sampleSize = sampleSize;
        this.recordingMode = recordingMode;
        // Always true, this is the PYH default.
        this.signed = true;
        this.byteOrder = byteOrder;
    }

    public AudioFormat getJavaAudioFormat() {
        return new AudioFormat(this.sampleRate.getNumberOfSamplesPerSecond(), this.sampleSize.getNumberOfBits(),
                this.recordingMode.getNumberOfChannels(), this.signed, this.byteOrder == ByteOrder.BIG_ENDIAN);
    }

    public StreamConfiguration getStreamConfiguration() {
        final StreamConfiguration streamConfiguration = new StreamConfiguration();
        streamConfiguration.setBitsPerSample(this.sampleSize.getNumberOfBits());
        streamConfiguration.setChannelCount(this.recordingMode.getNumberOfChannels());
        streamConfiguration.setSampleRate(this.sampleRate.getNumberOfSamplesPerSecond());
        return streamConfiguration;
    }

    public SampleRate getSampleRate() {
        return this.sampleRate;
    }

    public SampleSize getSampleSize() {
        return this.sampleSize;
    }

    public RecordingMode getRecordingMode() {
        return this.recordingMode;
    }

    public boolean isSigned() {
        return this.signed;
    }

    public ByteOrder getByteOrder() {
        return this.byteOrder;
    }

    public int getNumberOfBytesPerFrame() {
        return this.recordingMode.getNumberOfChannels() * this.sampleSize.getNumberOfBytes();
    }

    public int getNumberOfBytesPerSecond() {
        return this.sampleRate.getNumberOfSamplesPerSecond() * this.getNumberOfBytesPerFrame();
    }

    /**
     * The number of frames per second. Since we are using a non-compressed format, the frame rate is the same as the sample rate.
     *
     * @return number of frames per second
     */
    public int getNumberOfFramesPerSecond() {
        return this.sampleRate.getNumberOfSamplesPerSecond();
    }

    public int getMaximumPositiveValue() {
        return (int) (Math.pow(2, (this.sampleSize.getNumberOfBits() - 1)) - 1);
    }

    public int getMinimumNegativeValue() {
        return -1 * (int) (Math.pow(2, (this.sampleSize.getNumberOfBits() - 1)));
    }

}
