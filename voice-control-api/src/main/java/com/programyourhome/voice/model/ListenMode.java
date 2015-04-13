package com.programyourhome.voice.model;

public enum ListenMode {

    NONE(false, false),
    SPEECH(true, false),
    CLAPS(false, true),
    SPEECH_AND_CLAPS(true, true);

    private boolean listenForSpeech;
    private boolean listenForClaps;

    private ListenMode(final boolean listenForSpeech, final boolean listenForClaps) {
        this.listenForSpeech = listenForSpeech;
        this.listenForClaps = listenForClaps;
    }

    public boolean shouldListenForSpeech() {
        return this.listenForSpeech;
    }

    public boolean shouldListenForClaps() {
        return this.listenForClaps;
    }

    public static ListenMode fromSpeechClaps(final boolean acceptSpeechAsAnswer, final boolean acceptClapsAsAnswer) {
        ListenMode listenMode = null;
        for (final ListenMode aListenMode : ListenMode.values()) {
            if (aListenMode.shouldListenForSpeech() == acceptSpeechAsAnswer
                    && aListenMode.shouldListenForClaps() == acceptClapsAsAnswer) {
                listenMode = aListenMode;
            }
        }
        return listenMode;
    }

}
