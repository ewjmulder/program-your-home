package com.programyourhome.voice.builder;

import com.programyourhome.voice.model.question.NumberQuestion;
import com.programyourhome.voice.model.question.Question;

public class NumberQuestionImpl extends QuestionImpl<Integer> implements NumberQuestion {

    public void setNextQuestionOnNumber(final Integer number, final Question<?> nextQuestion) {
        this.addNextQuestionOnProperResult(number, nextQuestion);
    }

    @Override
    protected boolean isApplicableAnswerDefault(final Integer answer) {
        return NumberQuestion.super.isApplicableAnswer(answer);
    }

}
