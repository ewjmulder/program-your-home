package com.programyourhome.voice.model;

public enum SpeechMode {

    NONE(false, false),
    QUESTION_ONLY(true, false),
    POSSIBLE_ANSWERS_ONLY(false, true),
    QUESTION_AND_POSSIBLE_ANSWERS(true, true);

    private boolean sayQuestion;
    private boolean sayPossibleAnswers;

    private SpeechMode(final boolean sayQuestion, final boolean sayPossibleAnswers) {
        this.sayQuestion = sayQuestion;
        this.sayPossibleAnswers = sayPossibleAnswers;
    }

    public boolean shouldSayQuestion() {
        return this.sayQuestion;
    }

    public boolean shouldSayPossibleAnswers() {
        return this.sayPossibleAnswers;
    }

}
