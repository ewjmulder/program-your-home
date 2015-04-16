package com.programyourhome.voice.detection;

import java.nio.ByteOrder;

import org.junit.Assert;
import org.junit.Test;

import com.programyourhome.voice.format.PyhAudioFormat;
import com.programyourhome.voice.format.RecordingMode;
import com.programyourhome.voice.format.SampleRate;
import com.programyourhome.voice.format.SampleSize;

public class AudioFrameTest {

    @Test
    public void testVolumePercentage() {
        final PyhAudioFormat lowOneMono = new PyhAudioFormat(SampleRate.LOW, SampleSize.ONE_BYTE, RecordingMode.MONO, ByteOrder.LITTLE_ENDIAN);
        this.assertVolumeFromBytes(lowOneMono, 0, 0);
        this.assertVolumeFromBytes(lowOneMono, 78, 100);
        this.assertVolumeFromBytes(lowOneMono, 100, 127);
        this.assertVolumeFromBytes(lowOneMono, 50, -64);
        this.assertVolumeFromBytes(lowOneMono, 100, -128);

        final PyhAudioFormat highOneMono = new PyhAudioFormat(SampleRate.HIGH, SampleSize.ONE_BYTE, RecordingMode.MONO, ByteOrder.LITTLE_ENDIAN);
        this.assertVolumeFromBytes(highOneMono, 0, 0);
        this.assertVolumeFromBytes(highOneMono, 78, 100);
        this.assertVolumeFromBytes(highOneMono, 100, 127);
        this.assertVolumeFromBytes(highOneMono, 50, -64);
        this.assertVolumeFromBytes(highOneMono, 100, -128);

        final PyhAudioFormat lowTwoMono = new PyhAudioFormat(SampleRate.LOW, SampleSize.TWO_BYTES, RecordingMode.MONO, ByteOrder.LITTLE_ENDIAN);
        this.assertVolumeFromBytes(lowTwoMono, 0, 0);
        this.assertVolumeFromBytes(lowTwoMono, 78, 100);
        this.assertVolumeFromBytes(lowTwoMono, 100, 127);
        this.assertVolumeFromBytes(lowTwoMono, 50, -64);
        this.assertVolumeFromBytes(lowTwoMono, 100, -128);

    }

    private void assertVolumeFromBytes(final PyhAudioFormat audioFormat, final int volumePercentage, final int... bytes) {
        final AudioFrame audioFrame = this.createAudioFrame(audioFormat, bytes);
        Assert.assertEquals(volumePercentage, audioFrame.getVolumePercentage());
    }

    private AudioFrame createAudioFrame(final PyhAudioFormat audioFormat, final int... bytes) {
        final byte[] frameBytes = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            frameBytes[i] = (byte) (bytes[i]);
        }
        return new AudioFrame(audioFormat, 0, frameBytes);
    }
}
