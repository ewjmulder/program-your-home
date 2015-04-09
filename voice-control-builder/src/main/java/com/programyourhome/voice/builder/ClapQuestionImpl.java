package com.programyourhome.voice.builder;

import com.programyourhome.voice.model.question.ClapQuestion;
import com.programyourhome.voice.model.question.Question;

public class ClapQuestionImpl extends OptionalClapsQuestionImpl<Integer> implements ClapQuestion {

    public void setNextQuestionOnInteger(final Integer number, final Question<?> nextQuestion) {
        this.addNextQuestionOnProperResult(number, nextQuestion);
    }

}
