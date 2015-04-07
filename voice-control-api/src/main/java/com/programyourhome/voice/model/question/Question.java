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

    /**
     * Provides a default toString() implementation. The callback object is not included in the string.
     *
     * Unfortunately, Java does not allow us to default the actual toString method, so we'll call it asString.
     *
     * @return a string representation of the question
     */
    public default String asString() {
        String asString = "[" + this.getLocale() + "] " + this.getText() + " <" + this.getInteractionType() + ">";
        if (this.getInteractionType().hasPossibleAnswers()) {
            asString += " possible answers: " + this.getPossibleAnswers();
        }
        return asString;
    }

}
