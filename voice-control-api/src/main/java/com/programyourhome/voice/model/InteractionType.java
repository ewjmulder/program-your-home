package com.programyourhome.voice.model;

public enum InteractionType {

    NONE(Empty.class, false),
    YES_NO(Boolean.class, false),
    NUMBER(Integer.class, false),
    MULTIPLE_CHOICE(Character.class, true),
    OPEN(String.class, false);

    // TODO: how to hardlink (compile time) match answer type class with question answer type? (unit test maybe?)
    private Class<?> answerType;
    private boolean hasPossibleAnswers;

    private InteractionType(final Class<?> answerType, final boolean hasPossibleAnswers) {
        this.answerType = answerType;
        this.hasPossibleAnswers = hasPossibleAnswers;
    }

    public Class<?> getAnswerType() {
        return this.answerType;
    }

    public boolean hasPossibleAnswers() {
        return this.hasPossibleAnswers;
    }

}
