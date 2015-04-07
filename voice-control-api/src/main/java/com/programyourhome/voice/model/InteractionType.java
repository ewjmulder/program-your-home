package com.programyourhome.voice.model;

public enum InteractionType {

    NONE(Void.class),
    CLAP(Integer.class),
    YES_NO(Boolean.class),
    YES_NO_CLAP(Boolean.class),
    MULTIPLE_CHOICE(Character.class),
    MULTIPLE_CHOICE_CLAP(Character.class),
    OPEN(String.class);

    // TODO: how to hardlink (compile time) match answer type class with question answer type?
    private Class<?> answerType;

    private InteractionType(final Class<?> answerType) {
        this.answerType = answerType;
    }

    public Class<?> getAnswerType() {
        return this.answerType;
    }

}
