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
    public default boolean acceptClapsAsAnswer() {
        return false;
    }

    @Override
    public default boolean acceptSpeechAsAnswer() {
        return false;
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

    @Override
    public default boolean isApplicableAnswer(final Empty answer) {
        throw new IllegalStateException("Just say does not accept any answers.");
    }
}
