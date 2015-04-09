package com.programyourhome.voice.builder;

import com.programyourhome.voice.model.question.MultipleChoiceQuestion;
import com.programyourhome.voice.model.question.Question;

public class MultipleChoiceQuestionImpl extends OptionalClapsQuestionImpl<Character> implements MultipleChoiceQuestion {

    public void setNextQuestionOnCharacter(final Character character, final Question<?> nextQuestion) {
        this.addNextQuestionOnProperResult(character, nextQuestion);
    }

}
