package com.programyourhome.voice.builder;

import com.programyourhome.voice.model.question.MultipleChoiceQuestion;
import com.programyourhome.voice.model.question.Question;

public class MultipleChoiceQuestionBuilder extends
        OptionalClapsQuestionBuilder<MultipleChoiceQuestionBuilder, MultipleChoiceQuestionImpl, MultipleChoiceQuestion, Character> {

    private final MultipleChoiceQuestionImpl multipleChoiceQuestion;

    protected MultipleChoiceQuestionBuilder() {
        this.multipleChoiceQuestion = new MultipleChoiceQuestionImpl();
    }

    @Override
    protected MultipleChoiceQuestionBuilder getBuilder() {
        return this;
    }

    @Override
    protected MultipleChoiceQuestionImpl getQuestion() {
        return this.multipleChoiceQuestion;
    }

    public MultipleChoiceQuestionBuilder possibleAnswer(final Character character, final String answer, final Question<?> nextQuestion) {
        this.multipleChoiceQuestion.addPossibleAnswer(character, answer);
        this.multipleChoiceQuestion.setNextQuestionOnCharacter(character, nextQuestion);
        return this;
    }

}
