package com.programyourhome.voice.model.question;

import com.programyourhome.voice.model.InteractionType;

public interface YesNoQuestion extends NoPossibleAnswersQuestion<Boolean> {

    public boolean acceptClap();

    @Override
    public default InteractionType getInteractionType() {
        return acceptClap() ? InteractionType.YES_NO_CLAP : InteractionType.YES_NO;
    }

}
