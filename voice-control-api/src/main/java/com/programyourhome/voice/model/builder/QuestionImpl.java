package com.programyourhome.voice.model.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.programyourhome.voice.model.AnswerCallback;
import com.programyourhome.voice.model.AnswerResultType;
import com.programyourhome.voice.model.question.Question;

public abstract class QuestionImpl<AnswerType> implements Question<AnswerType> {

    private String text;
    private String locale;
    private final List<String> possibleAnswers = new ArrayList<>();

    private final Map<AnswerType, Question<?>> nextQuestionsOnProperResult;
    private Question<?> nextQuestionOnNoResult;
    private Question<?> nextQuestionOnNotApplicableResult;
    private Question<?> nextQuestionOnAmbiguousResult;

    private ProperResultCallback<AnswerType> properResultCallback;
    private Runnable noResultCallback;
    private Runnable notApplicableResultCallback;
    private Runnable ambiguousResultCallback;

    private ImproperResultPolicy improperResultPolicy;

    public QuestionImpl() {
        this.nextQuestionsOnProperResult = new HashMap<>();
    }

    // TODO: remove unused getters?

    @Override
    public String getText() {
        return this.text;
    }

    public void setText(final String text) {
        this.text = text;
    }

    @Override
    public String getLocale() {
        return this.locale;
    }

    public void setLocale(final String locale) {
        this.locale = locale;
    }

    @Override
    public List<String> getPossibleAnswers() {
        return this.possibleAnswers;
    }

    public void addPossibleAnswer(final String answer) {
        this.possibleAnswers.add(answer);
    }

    public void addNextQuestionOnProperResult(final AnswerType key, final Question<?> nextQuestion) {
        this.nextQuestionsOnProperResult.put(key, nextQuestion);
    }

    public void setNextQuestionOnNoResult(final Question<?> nextQuestionOnNoResult) {
        this.nextQuestionOnNoResult = nextQuestionOnNoResult;
    }

    public Question<?> getNextQuestionOnNotApplicableResult() {
        return this.nextQuestionOnNotApplicableResult;
    }

    public void setNextQuestionOnNotApplicableResult(final Question<?> nextQuestionOnNotApplicableResult) {
        this.nextQuestionOnNotApplicableResult = nextQuestionOnNotApplicableResult;
    }

    public Question<?> getNextQuestionOnAmbiguousResult() {
        return this.nextQuestionOnAmbiguousResult;
    }

    public void setNextQuestionOnAmbiguousResult(final Question<?> nextQuestionOnAmbiguousResult) {
        this.nextQuestionOnAmbiguousResult = nextQuestionOnAmbiguousResult;
    }

    public ProperResultCallback<AnswerType> getProperAnswerCallback() {
        return this.properResultCallback;
    }

    public void setProperAnswerCallback(final ProperResultCallback<AnswerType> properAnswerCallback) {
        this.properResultCallback = properAnswerCallback;
    }

    public Runnable getNoResultCallback() {
        return this.noResultCallback;
    }

    public void setNoResultCallback(final Runnable noResultCallback) {
        this.noResultCallback = noResultCallback;
    }

    public Runnable getNotApplicableResultCallback() {
        return this.notApplicableResultCallback;
    }

    public void setNotApplicableResultCallback(final Runnable notApplicableResultCallback) {
        this.notApplicableResultCallback = notApplicableResultCallback;
    }

    public Runnable getAmbiguousResultCallback() {
        return this.ambiguousResultCallback;
    }

    public void setAmbiguousResultCallback(final Runnable ambiguousResultCallback) {
        this.ambiguousResultCallback = ambiguousResultCallback;
    }

    public void setImproperResultPolicy(final ImproperResultPolicy improperResultPolicy) {
        this.improperResultPolicy = improperResultPolicy;
    }

    @Override
    public AnswerCallback<AnswerType> getAnswerCallback() {
        return answerResult -> {
            Question<?> nextQuestion = null;

            // TODO: handle callback exceptions?

            // TODO: include handling of improper result policy, that can be overridden by specific next questions and callbacks still apply.

            if (answerResult.getAnswerResultType() == AnswerResultType.PROPER) {
                this.properResultCallback.answer(answerResult.getAnswer());
                // TODO: throw exception if answer not found in map?
                nextQuestion = this.nextQuestionsOnProperResult.get(answerResult.getAnswer());
            } else if (answerResult.getAnswerResultType() == AnswerResultType.NONE) {
                this.noResultCallback.run();
                nextQuestion = this.nextQuestionOnNoResult;
            } else if (answerResult.getAnswerResultType() == AnswerResultType.NOT_APPLICABLE) {
                this.notApplicableResultCallback.run();
                nextQuestion = this.nextQuestionOnNotApplicableResult;
            } else if (answerResult.getAnswerResultType() == AnswerResultType.AMBIGUOUS) {
                this.ambiguousResultCallback.run();
                nextQuestion = this.nextQuestionOnAmbiguousResult;
            }

            return nextQuestion;
        };
    }

}
