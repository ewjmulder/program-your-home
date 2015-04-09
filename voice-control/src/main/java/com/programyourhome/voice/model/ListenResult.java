package com.programyourhome.voice.model;

import com.programyourhome.voice.model.googlespeech.GoogleSpeechResponse;

public class ListenResult {

    public ListenResultType resultType;
    public GoogleSpeechResponse googleSpeechResponse;
    public Integer numberOfClaps;

    private ListenResult(final ListenResultType resultType, final GoogleSpeechResponse googleSpeechResponse, final Integer numberOfClaps) {
        if (numberOfClaps != null && numberOfClaps < 0) {
            throw new IllegalArgumentException("Number of claps must be a non-negative number.");
        }
        this.resultType = resultType;
        this.googleSpeechResponse = googleSpeechResponse;
        this.numberOfClaps = numberOfClaps;
    }

    public ListenResultType getResultType() {
        return this.resultType;
    }

    public boolean isEmptyResult() {
        return this.resultType == ListenResultType.SILENCE ||
                this.resultType == ListenResultType.SPEECH_ENGINE && this.googleSpeechResponse.getTranscripts().isEmpty() ||
                this.resultType == ListenResultType.CLAPS && this.numberOfClaps == 0;
    }

    public GoogleSpeechResponse getGoogleSpeechResponse() {
        return this.googleSpeechResponse;
    }

    public Integer getNumberOfClaps() {
        return this.numberOfClaps;
    }

    public static ListenResult silence() {
        return new ListenResult(ListenResultType.SILENCE, null, null);
    }

    public static ListenResult googleSpeech(final GoogleSpeechResponse googleSpeechResponse) {
        return new ListenResult(ListenResultType.SPEECH_ENGINE, googleSpeechResponse, null);
    }

    public static ListenResult claps(final int numberOfClaps) {
        return new ListenResult(ListenResultType.CLAPS, null, numberOfClaps);
    }

}
