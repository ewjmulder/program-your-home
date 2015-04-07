package com.programyourhome.voice.model;

import com.programyourhome.voice.model.question.Question;

public interface AnswerCallback<AnswerType> {

    public Question<?> answer(AnswerResult<AnswerType> answerResult);

}
