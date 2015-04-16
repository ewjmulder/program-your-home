package com.programyourhome.voice.model;

import java.util.List;

public interface AnswerResult<AnswerType> {

    /**
     * The type of answer result. This can be a proper result or some type of erroneous answer.
     *
     * @return the answer result
     */
    public AnswerResultType getAnswerResultType();

    public void setAnswerResultType(AnswerResultType answerResultType);

    /**
     * The actual answer value. The type is different per type of question.
     * This value will be null if the AnswerResultType is not PROPER.
     *
     * @return the answer
     */
    public AnswerType getAnswer();

    public void setAnswer(AnswerType answer);

    /**
     * The type of listen result that was recorded.
     *
     * @return the listen result type
     */
    public ListenResultType getListenResultType();

    public void setListenResultType(ListenResultType listenResultType);

    /**
     * The 'raw' transcripts as returned by the speech engine. May be an empty list in case of:
     * - getListenResultType is not SPEECH
     * - no text recognized by speech engine
     *
     * @return the transcripts
     */
    public List<String> getTranscripts();

    public void setTranscripts(List<String> transcripts);

}
