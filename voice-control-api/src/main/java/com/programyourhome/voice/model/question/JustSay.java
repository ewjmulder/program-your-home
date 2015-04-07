package com.programyourhome.voice.model.question;

import com.programyourhome.voice.model.AnswerCallback;
import com.programyourhome.voice.model.InteractionType;

public interface JustSay extends NoPossibleAnswersQuestion<Void> {

    @Override
    public default InteractionType getInteractionType() {
        return InteractionType.NONE;
    }

    @Override
    public default AnswerCallback<Void> getAnswerCallback() {
        return value -> {
            textIsSaid();
            return null;
        };
    }

    public void textIsSaid();
}
