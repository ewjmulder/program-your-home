package com.programyourhome.voice.model.question;

import com.programyourhome.voice.model.InteractionType;

public interface OpenQuestion extends Question<String> {

    @Override
    public default InteractionType getInteractionType() {
        return InteractionType.OPEN;
    }

}
