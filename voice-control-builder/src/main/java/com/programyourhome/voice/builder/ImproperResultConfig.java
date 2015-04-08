package com.programyourhome.voice.builder;

import com.programyourhome.voice.model.question.Question;

public class ImproperResultConfig {

    private Question<?> nextQuestion;
    private Runnable callback;

    public ImproperResultConfig() {
        this(null, null);
    }

    public ImproperResultConfig(final Question<?> nextQuestion, final Runnable callback) {
        this.nextQuestion = nextQuestion;
        this.callback = callback;
    }

    public boolean hasNextQuestion() {
        return this.nextQuestion != null;
    }

    public Question<?> getNextQuestion() {
        return this.nextQuestion;
    }

    public void setNextQuestion(final Question<?> nextQuestion) {
        this.nextQuestion = nextQuestion;
    }

    public boolean hasCallback() {
        return this.callback != null;
    }

    public Runnable getCallback() {
        return this.callback;
    }

    public void setCallback(final Runnable callback) {
        this.callback = callback;
    }

}
