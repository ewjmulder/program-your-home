package com.programyourhome.voice.model.googlespeech;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GoogleSpeechResponse {
    @JsonProperty("result")
    private List<Result> results;
    @JsonProperty("result_index")
    private int resultIndex;

    public List<Alternative> getAlternatives() {
        return this.results.stream()
                .flatMap(result -> result.getAlternatives().stream())
                .collect(Collectors.toList());
    }

    public List<String> getTranscripts() {
        return this.getAlternatives().stream()
                .map(alternative -> alternative.getTranscript())
                .collect(Collectors.toList());
    }

}
