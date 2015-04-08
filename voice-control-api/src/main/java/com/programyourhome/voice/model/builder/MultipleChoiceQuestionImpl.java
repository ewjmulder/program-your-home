package com.programyourhome.voice.model.builder;

import com.programyourhome.voice.model.question.MultipleChoiceQuestion;

public class MultipleChoiceQuestionImpl extends QuestionImpl<Character> implements MultipleChoiceQuestion {

    private boolean acceptClap;

    @Override
    public boolean acceptClap() {
        return this.acceptClap;
    }

    public void setAcceptClap(final boolean acceptClap) {
        this.acceptClap = acceptClap;
    }

}
