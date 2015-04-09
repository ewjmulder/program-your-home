package com.programyourhome.voice.builder;

import com.programyourhome.voice.model.question.ClapQuestion;
import com.programyourhome.voice.model.question.Question;

public class ClapQuestionBuilder extends QuestionBuilder<ClapQuestionBuilder, ClapQuestionImpl, ClapQuestion, Integer> {

    private final ClapQuestionImpl clapQuestion;

    protected ClapQuestionBuilder() {
        this.clapQuestion = new ClapQuestionImpl();
    }

    @Override
    protected ClapQuestionBuilder getBuilder() {
        return this;
    }

    @Override
    protected ClapQuestionImpl getQuestion() {
        return this.clapQuestion;
    }

    public ClapQuestionBuilder possibleAnswer(final Integer number, final String answer, final Question<?> nextQuestion) {
        this.clapQuestion.addPossibleAnswer(number, answer);
        this.clapQuestion.setNextQuestionOnInteger(number, nextQuestion);
        return this;
    }

}
