package com.programyourhome.voice.builder;

import com.programyourhome.voice.model.question.OptionalClapsQuestion;

public abstract class OptionalClapsQuestionBuilder<BuilderType extends QuestionBuilder<?, ?, ?, AnswerType>, QuestionImplType extends OptionalClapsQuestionImpl<AnswerType>, QuestionType extends OptionalClapsQuestion<AnswerType>, AnswerType>
        extends QuestionBuilder<BuilderType, QuestionImplType, QuestionType, AnswerType> {

    public BuilderType acceptClaps(final boolean acceptClaps) {
        this.getQuestion().setAcceptClaps(acceptClaps);
        return this.getBuilder();
    }

}
