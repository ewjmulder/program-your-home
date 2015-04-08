package com.programyourhome.voice.model.builder;

import com.programyourhome.voice.model.question.JustSay;

public class JustSayImpl extends QuestionImpl<Void> implements JustSay {

    private JustSaidCallback justSaidCallback;

    @Override
    public JustSaidCallback getJustSaidCallback() {
        return this.justSaidCallback;
    }

    public void setJustSaidCallback(final JustSaidCallback justSaidCallback) {
        this.justSaidCallback = justSaidCallback;
    }

}
