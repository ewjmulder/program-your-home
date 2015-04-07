package com.programyourhome.voice.model;

import java.util.List;

public interface AnswerResult<AnswerType> {

    /**
     * The type of answer result. This can be a proper result or some type of erroneous answer.
     *
     * @return answer result
     */
    public AnswerResultType getAnswerResultType();

    /**
     * The actual answer value. The type is different per type of question.
     * This value will be null if the AnswerResultType is not PROPER.
     *
     * @return the answer
     */
    public AnswerType getAnswer();

    /**
     * The 'raw' transcripts as returned by the speech engine. May be an empty list.
     *
     * @return the transcripts
     */
    public List<String> getTranscripts();

}
