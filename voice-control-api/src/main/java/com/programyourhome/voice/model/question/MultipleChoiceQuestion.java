package com.programyourhome.voice.model.question;

import com.programyourhome.voice.model.InteractionType;

public interface MultipleChoiceQuestion extends Question<Character> {

    public boolean acceptClap();

    @Override
    public default InteractionType getInteractionType() {
        return acceptClap() ? InteractionType.MULTIPLE_CHOICE_CLAP : InteractionType.MULTIPLE_CHOICE;
    }

}
