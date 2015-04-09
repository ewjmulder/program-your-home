package com.programyourhome.voice.builder;

import com.programyourhome.voice.model.question.OptionalClapsQuestion;

public abstract class OptionalClapsQuestionImpl<AnswerType> extends QuestionImpl<AnswerType> implements OptionalClapsQuestion<AnswerType> {

    private boolean acceptClaps;

    @Override
    public boolean acceptClapsAsAnswer() {
        return this.acceptClaps;
    }

    public void setAcceptClaps(final boolean acceptClaps) {
        this.acceptClaps = acceptClaps;
    }

}
