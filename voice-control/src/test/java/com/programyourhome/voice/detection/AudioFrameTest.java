package com.programyourhome.voice.detection;

import java.nio.ByteOrder;

import org.junit.Assert;
import org.junit.Test;

import com.programyourhome.voice.format.PyhAudioFormat;
import com.programyourhome.voice.format.RecordingMode;
import com.programyourhome.voice.format.SampleRate;
import com.programyourhome.voice.format.SampleSize;

public class AudioFrameTest {

    private static final byte ALL_ONES = -1;
    private static final byte ALL_ZEROS = 0;
    private static final byte MAX_BIG_1 = 127;
    private static final byte MAX_BIG_2 = ALL_ONES;
    private static final byte MIN_BIG_1 = -128;
    private static final byte MIN_BIG_2 = ALL_ZEROS;
    private static final byte MAX_LITTLE_1 = MAX_BIG_2;
    private static final byte MAX_LITTLE_2 = MAX_BIG_1;
    private static final byte MIN_LITTLE_1 = MIN_BIG_2;
    private static final byte MIN_LITTLE_2 = MIN_BIG_1;

    @Test
    public void testVolumePercentage() {
        // Repeat the same tests for all sample rates, since that shouldn't make a difference for the volume percentage.
        for (final SampleRate sampleRate : SampleRate.values()) {
            final PyhAudioFormat oneMono = new PyhAudioFormat(sampleRate, SampleSize.ONE_BYTE, RecordingMode.MONO, ByteOrder.BIG_ENDIAN);
            this.assertVolumeFromBytes(oneMono, 0, 0);
            this.assertVolumeFromBytes(oneMono, 78, 100);
            this.assertVolumeFromBytes(oneMono, 100, 127);
            this.assertVolumeFromBytes(oneMono, 50, -64);
            this.assertVolumeFromBytes(oneMono, 100, -128);

            final PyhAudioFormat oneStereo = new PyhAudioFormat(sampleRate, SampleSize.ONE_BYTE, RecordingMode.STEREO, ByteOrder.BIG_ENDIAN);
            this.assertVolumeFromBytes(oneStereo, 0, 0, 0);
            this.assertVolumeFromBytes(oneStereo, 78, 100, 100);
            this.assertVolumeFromBytes(oneStereo, 100, 127, 127);
            this.assertVolumeFromBytes(oneStereo, 50, 0, 127);
            this.assertVolumeFromBytes(oneStereo, 50, 127, 0);
            this.assertVolumeFromBytes(oneStereo, 100, 127, -128);

            final PyhAudioFormat twoMono = new PyhAudioFormat(sampleRate, SampleSize.TWO_BYTES, RecordingMode.MONO, ByteOrder.BIG_ENDIAN);
            this.assertVolumeFromBytes(twoMono, 0, 0, 0);
            this.assertVolumeFromBytes(twoMono, 78, 100, 0);
            this.assertVolumeFromBytes(twoMono, 100, MAX_BIG_1, MAX_BIG_2);
            this.assertVolumeFromBytes(twoMono, 50, -64, 0);
            this.assertVolumeFromBytes(twoMono, 100, MIN_BIG_1, MIN_BIG_2);

            final PyhAudioFormat twoStereo = new PyhAudioFormat(sampleRate, SampleSize.TWO_BYTES, RecordingMode.STEREO, ByteOrder.BIG_ENDIAN);
            this.assertVolumeFromBytes(twoStereo, 0, 0, 0, 0, 0);
            this.assertVolumeFromBytes(twoStereo, 78, 100, 0, 100, 0);
            this.assertVolumeFromBytes(twoStereo, 100, MAX_BIG_1, MAX_BIG_2, MAX_BIG_1, MAX_BIG_2);
            this.assertVolumeFromBytes(twoStereo, 50, 0, 0, MAX_BIG_1, MAX_BIG_2);
            this.assertVolumeFromBytes(twoStereo, 50, MAX_BIG_1, MAX_BIG_2, 0, 0);
            this.assertVolumeFromBytes(twoStereo, 100, MAX_BIG_1, MAX_BIG_2, MIN_BIG_1, MIN_BIG_2);
        }

        // Take one final set to test little endian as well.
        final PyhAudioFormat twoStereoLittleEndian = new PyhAudioFormat(SampleRate.LOW, SampleSize.TWO_BYTES, RecordingMode.STEREO, ByteOrder.LITTLE_ENDIAN);
        this.assertVolumeFromBytes(twoStereoLittleEndian, 0, 0, 0, 0, 0);
        this.assertVolumeFromBytes(twoStereoLittleEndian, 78, 0, 100, 0, 100);
        this.assertVolumeFromBytes(twoStereoLittleEndian, 100, MAX_LITTLE_1, MAX_LITTLE_2, MAX_LITTLE_1, MAX_LITTLE_2);
        this.assertVolumeFromBytes(twoStereoLittleEndian, 50, 0, 0, MAX_LITTLE_1, MAX_LITTLE_2);
        this.assertVolumeFromBytes(twoStereoLittleEndian, 50, MAX_LITTLE_1, MAX_LITTLE_2, 0, 0);
        this.assertVolumeFromBytes(twoStereoLittleEndian, 100, MAX_LITTLE_1, MAX_LITTLE_2, MIN_LITTLE_1, MIN_LITTLE_2);
    }

    private void assertVolumeFromBytes(final PyhAudioFormat audioFormat, final int volumePercentage, final int... bytes) {
        final byte[] frameBytes = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            frameBytes[i] = (byte) (bytes[i]);
        }
        this.assertVolumeFromBytes(audioFormat, volumePercentage, frameBytes);
    }

    private void assertVolumeFromBytes(final PyhAudioFormat audioFormat, final int volumePercentage, final byte... frameBytes) {
        final AudioFrame audioFrame = this.createAudioFrame(audioFormat, frameBytes);
        Assert.assertEquals(volumePercentage, audioFrame.getVolumePercentage());
    }

    private AudioFrame createAudioFrame(final PyhAudioFormat audioFormat, final byte... frameBytes) {
        return new AudioFrame(audioFormat, 0, frameBytes);
    }
}
