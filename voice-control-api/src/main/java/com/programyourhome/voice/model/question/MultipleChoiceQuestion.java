package com.programyourhome.voice.model.question;

import com.programyourhome.voice.model.InteractionType;

public interface MultipleChoiceQuestion extends Question<Character> {

    @Override
    public default InteractionType getInteractionType() {
        return InteractionType.MULTIPLE_CHOICE;
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
    public default boolean isApplicableAnswer(final Character answer) {
        return this.getPossibleAnswers().containsKey(answer);
    }

}
