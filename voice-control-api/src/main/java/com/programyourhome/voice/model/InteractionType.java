package com.programyourhome.voice.model;

public enum InteractionType {

    NONE(Empty.class, false),
    CLAP(Integer.class, true),
    YES_NO(Boolean.class, false),
    YES_NO_CLAP(Boolean.class, false),
    MULTIPLE_CHOICE(Character.class, true),
    MULTIPLE_CHOICE_CLAP(Character.class, true),
    // TODO: Whould we like an open question with the possibility to answer in a certain (non-limited) amount of claps? :-)
    OPEN(String.class, false);

    // TODO: how to hardlink (compile time) match answer type class with question answer type?
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
