package com.programyourhome.voice.model.question;

import java.util.Map;

public interface NoPossibleAnswersQuestion<AnswerType> extends Question<AnswerType> {

    @Override
    public default Map<AnswerType, String> getPossibleAnswers() {
        throw new UnsupportedOperationException("This type of question does not provide possible answers.");
    }

}
