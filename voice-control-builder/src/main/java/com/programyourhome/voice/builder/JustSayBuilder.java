package com.programyourhome.voice.builder;

import com.programyourhome.voice.model.Empty;
import com.programyourhome.voice.model.question.JustSay;
import com.programyourhome.voice.model.question.Question;

public class JustSayBuilder extends QuestionBuilder<JustSayBuilder, JustSayImpl, JustSay, Empty> {

    private final JustSayImpl justSay;

    protected JustSayBuilder() {
        this.justSay = new JustSayImpl();
    }

    @Override
    protected JustSayBuilder getBuilder() {
        return this;
    }

    @Override
    protected JustSayImpl getQuestion() {
        return this.justSay;
    }

    public JustSayBuilder nextQuestion(final Question<?> nextQuestion) {
        this.justSay.setNextQuestion(nextQuestion);
        return this;
    }

    @Override
    public JustSayBuilder acceptSpeech(final boolean acceptSpeech) {
        throw new IllegalStateException("Just say does not have any interaction, so it cannot accept speech.");
    }

    @Override
    public JustSayBuilder acceptClaps(final boolean acceptClaps) {
        throw new IllegalStateException("Just say does not have any interaction, so it cannot accept claps.");
    }

}
