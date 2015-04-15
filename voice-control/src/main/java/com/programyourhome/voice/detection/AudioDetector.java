package com.programyourhome.voice.detection;

import com.programyourhome.voice.format.PyhAudioFormat;

public interface AudioDetector<T> {

    /**
     * Indication that the audio stream has opened. Any initialization can be done here.
     * The audio format of the stream is provided, so it can be used to fine tune the clap detection.
     * This method will always be called as the first method on this interface and will be called only once.
     * After this method call, a series of calls to nextFrame will follow.
     *
     * @param audioFormat the audio format of the stream
     */
    public void audioStreamOpened(PyhAudioFormat audioFormat);

    /**
     * Report the next frame that has been recorded.
     *
     * @param audioFrame the audio frame
     */
    public void nextFrame(AudioFrame audioFrame);

    /**
     * Indication that the audio stream has closed. Any finalization can be done here.
     * After this method call, no more calls to nextFrame will happen.
     * After this method call, the methods isValueDetected and getDetectedValue can be called.
     */
    public void audioStreamClosed();

    /**
     * Whether or not this detector has detected any actual value.
     * This method can only be called after audioStreamClosed has been called.
     *
     * @return value detected (true) or not (false)
     */
    public boolean isValueDetected();

    /**
     * The detected value.
     * This method can only be called after audioStreamClosed has been called.
     *
     * @return detected value
     */
    public T getDetectedValue();

}
