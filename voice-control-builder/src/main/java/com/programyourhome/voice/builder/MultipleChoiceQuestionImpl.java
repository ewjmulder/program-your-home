package com.programyourhome.voice.builder;

import com.programyourhome.voice.model.question.MultipleChoiceQuestion;
import com.programyourhome.voice.model.question.Question;

public class MultipleChoiceQuestionImpl extends QuestionImpl<Character> implements MultipleChoiceQuestion {

    public void setNextQuestionOnCharacter(final Character character, final Question<?> nextQuestion) {
        this.addNextQuestionOnProperResult(character, nextQuestion);
    }

    @Override
    protected boolean acceptSpeechAsAnswerDefault() {
        return MultipleChoiceQuestion.super.acceptSpeechAsAnswer();
    }

    @Override
    protected boolean acceptClapsAsAnswerDefault() {
        return MultipleChoiceQuestion.super.acceptClapsAsAnswer();
    }

    @Override
    protected boolean isApplicableAnswerDefault(final Character answer) {
        return MultipleChoiceQuestion.super.isApplicableAnswer(answer);
    }

}
