package com.programyourhome.voice.model.question;

public interface OptionalClapsQuestion<AnswerType> extends Question<AnswerType> {

    public boolean acceptClapsAsAnswer();

}
