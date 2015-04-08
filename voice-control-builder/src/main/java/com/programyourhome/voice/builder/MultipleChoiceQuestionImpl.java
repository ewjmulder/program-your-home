package com.programyourhome.voice.builder;

import com.programyourhome.voice.model.question.MultipleChoiceQuestion;
import com.programyourhome.voice.model.question.Question;

public class MultipleChoiceQuestionImpl extends QuestionImpl<Character> implements MultipleChoiceQuestion {

    private boolean acceptClap;

    @Override
    public boolean acceptClap() {
        return this.acceptClap;
    }

    public void setAcceptClap(final boolean acceptClap) {
        this.acceptClap = acceptClap;
    }

    public void setNextQuestionOnCharacter(final Character character, final Question<?> nextQuestion) {
        this.addNextQuestionOnProperResult(character, nextQuestion);
    }

}
