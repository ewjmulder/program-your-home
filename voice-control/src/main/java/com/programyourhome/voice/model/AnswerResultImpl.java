package com.programyourhome.voice.model;

import java.util.List;

public class AnswerResultImpl<AnswerType> implements AnswerResult<AnswerType> {

    private AnswerResultType answerResultType;
    private AnswerType answer;
    private List<String> transcripts;

    public AnswerResultImpl(final List<String> transcripts) {
        this(null, null, transcripts);
    }

    public AnswerResultImpl(final AnswerResultType answerResultType, final AnswerType answer, final List<String> transcripts) {
        this.answerResultType = answerResultType;
        this.answer = answer;
        this.transcripts = transcripts;
    }

    @Override
    public AnswerResultType getAnswerResultType() {
        return this.answerResultType;
    }

    public void setAnswerResultType(final AnswerResultType answerResultType) {
        this.answerResultType = answerResultType;
    }

    @Override
    public AnswerType getAnswer() {
        return this.answer;
    }

    public void setAnswer(final AnswerType answer) {
        this.answer = answer;
    }

    @Override
    public List<String> getTranscripts() {
        return this.transcripts;
    }

}
