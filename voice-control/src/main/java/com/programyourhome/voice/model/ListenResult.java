package com.programyourhome.voice.model;

import java.util.ArrayList;
import java.util.List;

public class ListenResult {

    public ListenResultType resultType;
    public List<String> transcripts;
    public Integer numberOfClaps;

    private ListenResult(final ListenResultType resultType, final List<String> transcripts, final Integer numberOfClaps) {
        if (numberOfClaps != null && numberOfClaps < 0) {
            throw new IllegalArgumentException("Number of claps must be a non-negative number.");
        }
        this.resultType = resultType;
        this.transcripts = transcripts;
        this.numberOfClaps = numberOfClaps;
    }

    public ListenResultType getResultType() {
        return this.resultType;
    }

    public boolean isEmptyResult() {
        return this.resultType == ListenResultType.SILENCE ||
                this.resultType == ListenResultType.SPEECH && this.transcripts.isEmpty() ||
                this.resultType == ListenResultType.CLAPS && this.numberOfClaps == 0;
    }

    public List<String> getTranscripts() {
        return this.transcripts;
    }

    public Integer getNumberOfClaps() {
        return this.numberOfClaps;
    }

    public static ListenResult silence() {
        return new ListenResult(ListenResultType.SILENCE, null, null);
    }

    public static ListenResult speech(final List<String> transcripts) {
        return new ListenResult(ListenResultType.SPEECH, transcripts, null);
    }

    public static ListenResult emptySpeech() {
        return new ListenResult(ListenResultType.SPEECH, new ArrayList<>(), null);
    }

    public static ListenResult claps(final int numberOfClaps) {
        return new ListenResult(ListenResultType.CLAPS, null, numberOfClaps);
    }

    public static ListenResult emptyClaps() {
        return new ListenResult(ListenResultType.CLAPS, null, 0);
    }

}
