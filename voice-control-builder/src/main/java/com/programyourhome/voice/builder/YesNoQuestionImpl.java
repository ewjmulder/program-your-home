package com.programyourhome.voice.builder;

import com.programyourhome.voice.model.question.Question;
import com.programyourhome.voice.model.question.YesNoQuestion;

public class YesNoQuestionImpl extends QuestionImpl<Boolean> implements YesNoQuestion {

    public void setNextQuestionOnYes(final Question<?> nextQuestion) {
        this.addNextQuestionOnProperResult(true, nextQuestion);
    }

    public void setNextQuestionOnNo(final Question<?> nextQuestion) {
        this.addNextQuestionOnProperResult(false, nextQuestion);
    }

    @Override
    protected boolean isApplicableAnswerDefault(final Boolean answer) {
        return YesNoQuestion.super.isApplicableAnswer(answer);
    }

}
