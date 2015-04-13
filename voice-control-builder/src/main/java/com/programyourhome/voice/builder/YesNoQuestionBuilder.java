package com.programyourhome.voice.builder;

import com.programyourhome.voice.model.question.Question;
import com.programyourhome.voice.model.question.YesNoQuestion;

public class YesNoQuestionBuilder extends QuestionBuilder<YesNoQuestionBuilder, YesNoQuestionImpl, YesNoQuestion, Boolean> {

    private final YesNoQuestionImpl yesNoQuestion;

    protected YesNoQuestionBuilder() {
        this.yesNoQuestion = new YesNoQuestionImpl();
    }

    @Override
    protected YesNoQuestionBuilder getBuilder() {
        return this;
    }

    @Override
    protected YesNoQuestionImpl getQuestion() {
        return this.yesNoQuestion;
    }

    public YesNoQuestionBuilder onYes(final Question<?> nextQuestion) {
        this.yesNoQuestion.setNextQuestionOnYes(nextQuestion);
        return this;
    }

    public YesNoQuestionBuilder onNo(final Question<?> nextQuestion) {
        this.yesNoQuestion.setNextQuestionOnNo(nextQuestion);
        return this;
    }

}
