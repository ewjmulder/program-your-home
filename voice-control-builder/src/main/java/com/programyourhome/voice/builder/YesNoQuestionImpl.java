package com.programyourhome.voice.builder;

import com.programyourhome.voice.model.question.Question;
import com.programyourhome.voice.model.question.YesNoQuestion;

public class YesNoQuestionImpl extends OptionalClapsQuestionImpl<Boolean> implements YesNoQuestion {

    public void setNextQuestionOnYes(final Question<?> nextQuestion) {
        this.addNextQuestionOnProperResult(true, nextQuestion);
    }

    public void setNextQuestionOnNo(final Question<?> nextQuestion) {
        this.addNextQuestionOnProperResult(false, nextQuestion);
    }

}
