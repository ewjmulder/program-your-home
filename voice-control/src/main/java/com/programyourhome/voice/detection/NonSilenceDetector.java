package com.programyourhome.voice.detection;

/**
 * Marker interface for non-silence detectors. To enable wiring by type.
 * Also useful to specify some other non-silence detector functionality.
 */
public interface NonSilenceDetector extends AudioDetector<Boolean> {

    public boolean isNonSilenceDetected();

    public default boolean isOnlySilence() {
        return !isNonSilenceDetected();
    }

    public int getSilenceMillisSinceLastNonSilence(AudioFrame audioFrame);

    @Override
    public default boolean isValueDetected() {
        return true;
    }

    @Override
    public default Boolean getDetectedValue() {
        return isNonSilenceDetected();
    }

}
