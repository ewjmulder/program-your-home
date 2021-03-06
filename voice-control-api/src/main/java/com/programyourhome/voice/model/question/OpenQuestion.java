package com.programyourhome.voice.model.question;

import com.programyourhome.voice.model.InteractionType;

public interface OpenQuestion extends Question<String> {

    @Override
    public default InteractionType getInteractionType() {
        return InteractionType.OPEN;
    }

    @Override
    public default boolean acceptSpeechAsAnswer() {
        return true;
    }

    @Override
    public default boolean acceptClapsAsAnswer() {
        return false;
    }

    @Override
    public default boolean isApplicableAnswer(final String answer) {
        return true;
    }

}
