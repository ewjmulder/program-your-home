package com.programyourhome.voice.model.question;

import com.programyourhome.voice.model.InteractionType;

public interface ClapQuestion extends Question<Integer> {

    @Override
    public default InteractionType getInteractionType() {
        return InteractionType.CLAP;
    }

}
