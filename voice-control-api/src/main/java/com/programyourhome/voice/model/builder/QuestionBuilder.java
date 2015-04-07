package com.programyourhome.voice.model.builder;

import com.programyourhome.voice.model.AnswerCallback;

public class QuestionBuilder {

    private static final AnswerCallback<?> EMPTY_CALLBACK = type -> {
        return null;
    };

    // private Map<Question, >
    //
    // public QuestionBuilder() {
    //
    // }
    //
    // public AudioInteractionBuilder justSay(final String text, final Language language) {
    // this.question = new YesNoQ
    // }
    //
    // private class QuestionImpl<AnswerType> implements Question {
    //
    // private final String text;
    // private final Language language;
    // private final Interaction interaction;
    // private final AnswerCallback<AnswerType> answerCallback;
    //
    // private QuestionImpl(final String text, final Language language, final Interaction interaction, final AnswerCallback<AnswerType> answerCallback) {
    // this.text = text;
    // this.language = language;
    // this.interaction = interaction;
    // this.answerCallback = answerCallback;
    // }
    //
    // @Override
    // public String getText() {
    // return this.text;
    // }
    //
    // @Override
    // public Language getLanguage() {
    // return this.language;
    // }
    //
    // @Override
    // public Interaction getInteraction() {
    // return this.interaction;
    // }
    //
    // @Override
    // public AnswerCallback<AnswerType> getAnswerCallback() {
    // return this.answerCallback;
    // }
    //
    // @Override
    // public String toString() {
    // return "[" + this.language + "] " + this.text;
    // }
    //
    // }

}
