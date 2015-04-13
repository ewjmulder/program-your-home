package com.programyourhome.voice.builder;

import com.programyourhome.voice.model.Empty;
import com.programyourhome.voice.model.question.JustSay;
import com.programyourhome.voice.model.question.Question;

public class JustSayImpl extends QuestionImpl<Empty> implements JustSay {

    public static final Empty PROPER_RESULT_KEY = Empty.EMPTY;

    @Override
    public Runnable getJustSaidCallback() {
        throw new UnsupportedOperationException("Just said callback is not used in the builder module.");
    }

    public void setNextQuestion(final Question<?> nextQuestion) {
        this.addNextQuestionOnProperResult(PROPER_RESULT_KEY, nextQuestion);
    }

    @Override
    protected boolean isApplicableAnswerDefault(final Empty answer) {
        return JustSay.super.isApplicableAnswer(answer);
    }

}
