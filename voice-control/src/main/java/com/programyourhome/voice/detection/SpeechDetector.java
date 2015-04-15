package com.programyourhome.voice.detection;

import java.util.List;

/**
 * Marker interface for speech detectors. To enable wiring by type.
 * Also useful to provide an extra speech detector specific methods.
 */
public interface SpeechDetector extends AudioDetector<List<String>> {

    /**
     * Set the locale of the speech to be detected.
     *
     * @param locale the locale
     */
    public void setLocale(String locale);

    /**
     * Perform the actual speech recognition. This method is explicitly separated from the rest of the
     * normal audio detector flow, because this is a very heavy operation in terms of processing time
     * and/or CPU cycles. By splitting out this logic, it is possible for users of the detector
     * to make a conscious decision about whether or not to perform the actual speech recognition.
     */
    public void performSpeechRecognition();

}
