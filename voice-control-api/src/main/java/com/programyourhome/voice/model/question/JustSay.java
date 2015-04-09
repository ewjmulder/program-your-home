package com.programyourhome.voice.model.question;

import com.programyourhome.voice.model.AnswerCallback;
import com.programyourhome.voice.model.Empty;
import com.programyourhome.voice.model.InteractionType;

public interface JustSay extends Question<Empty> {

    @Override
    public default InteractionType getInteractionType() {
        return InteractionType.NONE;
    }

    @Override
    public default AnswerCallback<Empty> getAnswerCallback() {
        return value -> {
            if (getJustSaidCallback() != null) {
                getJustSaidCallback().run();
            }
            return getNextQuestion();
        };
    }

    public default Question<?> getNextQuestion() {
        return null;
    }

    public Runnable getJustSaidCallback();
}
