package com.programyourhome.voice.model;

public enum InteractionType {

    NONE(Empty.class, ListenMode.NONE, false),
    CLAP(Integer.class, ListenMode.CLAPS, true),
    YES_NO(Boolean.class, ListenMode.SPEECH, false),
    YES_NO_CLAP(Boolean.class, ListenMode.SPEECH_AND_CLAPS, false),
    MULTIPLE_CHOICE(Character.class, ListenMode.SPEECH, true),
    MULTIPLE_CHOICE_CLAP(Character.class, ListenMode.SPEECH_AND_CLAPS, true),
    // TODO: Whould we like an open question with the possibility to answer in a certain (non-limited) amount of claps? :-)
    OPEN(String.class, ListenMode.SPEECH, false);

    // TODO: how to hardlink (compile time) match answer type class with question answer type? (unit test maybe?)
    private Class<?> answerType;
    private boolean hasPossibleAnswers;
    private ListenMode listenMode;

    private InteractionType(final Class<?> answerType, final ListenMode listenMode, final boolean hasPossibleAnswers) {
        this.answerType = answerType;
        this.listenMode = listenMode;
        this.hasPossibleAnswers = hasPossibleAnswers;
    }

    public Class<?> getAnswerType() {
        return this.answerType;
    }

    public ListenMode getListenMode() {
        return this.listenMode;
    }

    public boolean hasPossibleAnswers() {
        return this.hasPossibleAnswers;
    }

}
