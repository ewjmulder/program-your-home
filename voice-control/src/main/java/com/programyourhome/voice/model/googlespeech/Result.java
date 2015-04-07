package com.programyourhome.voice.model.googlespeech;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Result {
    @JsonProperty("alternative")
    private List<Alternative> alternatives;
    @JsonProperty("final")
    private boolean isFinal;

    public List<Alternative> getAlternatives() {
        return this.alternatives;
    }
}
