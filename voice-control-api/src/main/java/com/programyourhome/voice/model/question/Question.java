package com.programyourhome.voice.model.question;

import java.util.List;

import com.programyourhome.voice.model.AnswerCallback;
import com.programyourhome.voice.model.InteractionType;

public interface Question<AnswerType> {

    public String getText();

    public String getLocale();

    public InteractionType getInteractionType();

    public List<String> getPossibleAnswers();

    public AnswerCallback<AnswerType> getAnswerCallback();

}
