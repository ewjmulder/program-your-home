package com.programyourhome.voice.model;

public enum Language {

    ENGLISH_US("en-us"),
    ENGLISH_UK("en-uk"),
    DUTCH("nl-nl");

    private String code;

    private Language(final String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

}
