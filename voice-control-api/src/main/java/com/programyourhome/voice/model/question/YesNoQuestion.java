package com.programyourhome.voice.model.question;

import com.programyourhome.voice.model.InteractionType;

public interface YesNoQuestion extends OptionalClapsQuestion<Boolean> {

    @Override
    public default InteractionType getInteractionType() {
        return acceptClapsAsAnswer() ? InteractionType.YES_NO_CLAP : InteractionType.YES_NO;
    }

}
