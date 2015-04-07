package com.programyourhome.voice.model.question;

import java.util.List;

public interface NoPossibleAnswersQuestion<AnswerType> extends Question<AnswerType> {

    @Override
    public default List<String> getPossibleAnswers() {
        throw new UnsupportedOperationException("This type of question does not provide possible answers.");
    }

}
