package com.programyourhome.voice.model.question;

import com.programyourhome.voice.model.InteractionType;

public interface YesNoQuestion extends Question<Boolean> {

    @Override
    public default InteractionType getInteractionType() {
        return InteractionType.YES_NO;
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
    public default boolean isApplicableAnswer(final Boolean answer) {
        return true;
    }

}
