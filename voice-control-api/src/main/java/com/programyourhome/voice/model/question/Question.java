package com.programyourhome.voice.model.question;

import java.util.SortedMap;
import java.util.TreeMap;

import com.programyourhome.voice.model.AnswerCallback;
import com.programyourhome.voice.model.InteractionType;
import com.programyourhome.voice.model.ListenMode;
import com.programyourhome.voice.model.SpeechMode;

public interface Question<AnswerType> {

    public String getText();

    public String getLocale();

    public InteractionType getInteractionType();

    public default boolean acceptSpeechAsAnswer() {
        return true;
    }

    public default boolean acceptClapsAsAnswer() {
        return false;
    }

    public default ListenMode getListenMode() {
        return ListenMode.fromSpeechClaps(acceptSpeechAsAnswer(), acceptClapsAsAnswer());
    }

    public default SortedMap<AnswerType, String> getPossibleAnswers() {
        if (!getInteractionType().hasPossibleAnswers()) {
            throw new UnsupportedOperationException("The interaction type of this question does not provide possible answers.");
        } else {
            return new TreeMap<>();
        }
    }

    public AnswerCallback<AnswerType> getAnswerCallback();

    public default SpeechMode getSpeechMode() {
        return SpeechMode.QUESTION_AND_POSSIBLE_ANSWERS;
    }

    public boolean isApplicableAnswer(AnswerType answer);

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
