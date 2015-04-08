package com.programyourhome.voice.model.builder;

import com.programyourhome.voice.model.question.JustSay;

public class JustSayBuilder extends QuestionBuilder<JustSayBuilder, JustSayImpl, JustSay, Void> {

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

}
