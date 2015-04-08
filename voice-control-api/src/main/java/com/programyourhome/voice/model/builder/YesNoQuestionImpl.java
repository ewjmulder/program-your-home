package com.programyourhome.voice.model.builder;

import com.programyourhome.voice.model.question.Question;
import com.programyourhome.voice.model.question.YesNoQuestion;

public class YesNoQuestionImpl extends QuestionImpl<Boolean> implements YesNoQuestion {

    private boolean acceptClap;

    @Override
    public boolean acceptClap() {
        return this.acceptClap;
    }

    public void setAcceptClap(final boolean acceptClap) {
        this.acceptClap = acceptClap;
    }

    public void setNextQuestionOnYes(final Question<?> nextQuestion) {
        this.addNextQuestionOnProperResult(true, nextQuestion);
    }

    public void setNextQuestionOnNo(final Question<?> nextQuestion) {
        this.addNextQuestionOnProperResult(false, nextQuestion);
    }

}
