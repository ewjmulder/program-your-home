package com.programyourhome.voice.builder;

import com.programyourhome.voice.model.question.OpenQuestion;

public class OpenQuestionBuilder extends QuestionBuilder<OpenQuestionBuilder, OpenQuestionImpl, OpenQuestion, String> {

    private final OpenQuestionImpl openQuestion;

    protected OpenQuestionBuilder() {
        this.openQuestion = new OpenQuestionImpl();
    }

    @Override
    protected OpenQuestionBuilder getBuilder() {
        return this;
    }

    @Override
    protected OpenQuestionImpl getQuestion() {
        return this.openQuestion;
    }

    @Override
    public OpenQuestionBuilder acceptClaps(final boolean acceptClaps) {
        throw new IllegalStateException("Open questions can not be answered by claps.");
    }

}
