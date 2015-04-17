package com.programyourhome.voice.detection;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.programyourhome.voice.format.PyhAudioFormat;

@Component
@Scope("prototype")
public class VolumePeakNonSilenceDetector implements NonSilenceDetector {

    // Peak larger than this volume to detect non-silence.
    private final int maxSilenceVolumePercentage = 20;

    private boolean nonSilenceDetected;
    private int lastNonSilenceMilli;

    public VolumePeakNonSilenceDetector() {
        this.nonSilenceDetected = false;
        this.lastNonSilenceMilli = -1;
    }

    @Override
    public void audioStreamOpened(final PyhAudioFormat audioFormat) {
    }

    // TODO: Add minimum time above peak / number of peak in certain time to detect non-silence to avoid mic 'freaks' (if needed).
    // - min amount of time a non silence is detected max x bytes / millis in between
    @Override
    public void nextFrame(final AudioFrame audioFrame) {
        if (audioFrame.getVolumePercentage() > this.maxSilenceVolumePercentage) {
            this.nonSilenceDetected = true;
            this.lastNonSilenceMilli = audioFrame.getMillisSinceStart();
        }
    }

    @Override
    public void audioStreamClosed() {
    }

    @Override
    public boolean isNonSilenceDetected() {
        return this.nonSilenceDetected;
    }

    @Override
    public int getSilenceMillisSinceLastNonSilence(final AudioFrame audioFrame) {
        return audioFrame.getMillisSinceStart() - this.lastNonSilenceMilli;
    }

}
