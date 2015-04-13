package com.programyourhome.voice.model.question;

import com.programyourhome.voice.model.InteractionType;

public interface NumberQuestion extends Question<Integer> {

    @Override
    public default InteractionType getInteractionType() {
        return InteractionType.NUMBER;
    }

    @Override
    public default boolean acceptSpeechAsAnswer() {
        return true;
    }

    @Override
    public default boolean acceptClapsAsAnswer() {
        return true;
    }

    @Override
    public default boolean isApplicableAnswer(final Integer answer) {
        return true;
    }
}
