package com.programyourhome.voice.builder;

import com.programyourhome.voice.model.question.NumberQuestion;

public class NumberQuestionBuilder extends QuestionBuilder<NumberQuestionBuilder, NumberQuestionImpl, NumberQuestion, Integer> {

    private final NumberQuestionImpl numberQuestion;

    protected NumberQuestionBuilder() {
        this.numberQuestion = new NumberQuestionImpl();
    }

    @Override
    protected NumberQuestionBuilder getBuilder() {
        return this;
    }

    @Override
    protected NumberQuestionImpl getQuestion() {
        return this.numberQuestion;
    }

}
