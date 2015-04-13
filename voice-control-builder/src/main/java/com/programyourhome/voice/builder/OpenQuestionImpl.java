package com.programyourhome.voice.builder;

import com.programyourhome.voice.model.question.OpenQuestion;

public class OpenQuestionImpl extends QuestionImpl<String> implements OpenQuestion {

    @Override
    protected boolean isApplicableAnswerDefault(final String answer) {
        return OpenQuestion.super.isApplicableAnswer(answer);
    }

}
