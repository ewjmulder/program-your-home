package com.programyourhome.voice.builder;

import com.programyourhome.voice.model.question.JustSay;
import com.programyourhome.voice.model.question.Question;

public class JustSayImpl extends QuestionImpl<Void> implements JustSay {

    @Override
    public Runnable getJustSaidCallback() {
        throw new UnsupportedOperationException("Just said callback is not used in the builder module.");
    }

    public void setNextQuestion(final Question<?> nextQuestion) {
        this.addNextQuestionOnProperResult(null, nextQuestion);
    }

}
