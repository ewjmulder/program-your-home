package com.programyourhome.voice.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class AnswerResultImpl<AnswerType> implements AnswerResult<AnswerType> {

    private AnswerResultType answerResultType;
    private AnswerType answer;
    private ListenResultType listenResultType;
    private List<String> transcripts;

    public AnswerResultImpl(final ListenResultType listenResultType) {
        this(null, null, listenResultType, new ArrayList<>());
    }

    public AnswerResultImpl(final AnswerResultType answerResultType, final AnswerType answer,
            final ListenResultType listenResultType, final List<String> transcripts) {
        this.answerResultType = answerResultType;
        this.answer = answer;
        this.listenResultType = listenResultType;
        this.transcripts = transcripts;
    }

    @Override
    public AnswerResultType getAnswerResultType() {
        return this.answerResultType;
    }

    @Override
    public void setAnswerResultType(final AnswerResultType answerResultType) {
        this.answerResultType = answerResultType;
    }

    @Override
    public AnswerType getAnswer() {
        return this.answer;
    }

    @Override
    public void setAnswer(final AnswerType answer) {
        this.answer = answer;
    }

    @Override
    public ListenResultType getListenResultType() {
        return this.listenResultType;
    }

    @Override
    public void setListenResultType(final ListenResultType listenResultType) {
        this.listenResultType = listenResultType;
    }

    @Override
    public List<String> getTranscripts() {
        return this.transcripts;
    }

    @Override
    public void setTranscripts(final List<String> transcripts) {
        this.transcripts = transcripts;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.reflectionToString(this);
    }

}
