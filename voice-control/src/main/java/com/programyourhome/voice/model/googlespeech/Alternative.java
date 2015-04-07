package com.programyourhome.voice.model.googlespeech;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Alternative {
    @JsonProperty
    private String transcript;
    @JsonProperty
    private double confidence;

    public String getTranscript() {
        return this.transcript;
    }

    public double getConfidence() {
        return this.confidence;
    }
}
