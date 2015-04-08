package com.programyourhome.voice.builder;

import com.programyourhome.voice.model.question.Question;

public abstract class QuestionBuilder<BuilderType extends QuestionBuilder<?, ?, ?, AnswerType>, QuestionImplType extends QuestionImpl<AnswerType>, QuestionType extends Question<AnswerType>, AnswerType> {

    public BuilderType text(final String text) {
        this.getQuestion().setText(text);
        return this.getBuilder();
    }

    public BuilderType locale(final String locale) {
        this.getQuestion().setLocale(locale);
        return this.getBuilder();
    }

    public BuilderType onNoResult(final Question<?> nextQuestion) {
        this.getQuestion().setNextQuestionOnNoResult(nextQuestion);
        return this.getBuilder();
    }

    public BuilderType onNotApplicableResult(final Question<?> nextQuestion) {
        this.getQuestion().setNextQuestionOnNotApplicableResult(nextQuestion);
        return this.getBuilder();
    }

    public BuilderType onAmbiguousResult(final Question<?> nextQuestion) {
        this.getQuestion().setNextQuestionOnAmbiguousResult(nextQuestion);
        return this.getBuilder();
    }

    public BuilderType properResultCallback(final ProperResultCallback<AnswerType> callback) {
        this.getQuestion().setProperAnswerCallback(callback);
        return this.getBuilder();
    }

    public BuilderType noResultCallback(final Runnable callback) {
        this.getQuestion().setNoResultCallback(callback);
        return this.getBuilder();
    }

    public BuilderType notApplicableResultCallback(final Runnable callback) {
        this.getQuestion().setNotApplicableResultCallback(callback);
        return this.getBuilder();
    }

    public BuilderType ambiguousResultCallback(final Runnable callback) {
        this.getQuestion().setAmbiguousResultCallback(callback);
        return this.getBuilder();
    }

    public BuilderType improperResultPolicy(final ImproperResultPolicy improperResultPolicy) {
        this.getQuestion().setImproperResultPolicy(improperResultPolicy);
        return this.getBuilder();
    }

    @SuppressWarnings("unchecked")
    public QuestionType build() {
        return (QuestionType) this.getQuestion();
    }

    protected abstract BuilderType getBuilder();

    protected abstract QuestionImplType getQuestion();

}
