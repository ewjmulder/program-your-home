package com.programyourhome.voice.model.question;

import com.programyourhome.voice.model.InteractionType;

public interface MultipleChoiceQuestion extends OptionalClapsQuestion<Character> {

    @Override
    public default InteractionType getInteractionType() {
        return acceptClapsAsAnswer() ? InteractionType.MULTIPLE_CHOICE_CLAP : InteractionType.MULTIPLE_CHOICE;
    }

}
