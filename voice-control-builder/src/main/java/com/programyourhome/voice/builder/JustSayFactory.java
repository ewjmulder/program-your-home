package com.programyourhome.voice.builder;

import com.programyourhome.voice.model.question.JustSay;
import com.programyourhome.voice.model.question.Question;

public class JustSayFactory {

    public static JustSay justSay(final String text, final String locale) {
        return new JustSayBuilder()
                .text(text)
                .locale(locale)
                .build();
    }

    public static JustSay justSayWithNextQuestion(final String text, final String locale, final Question<?> nextQuestion) {
        return new JustSayBuilder()
                .text(text)
                .locale(locale)
                .nextQuestion(nextQuestion)
                .build();
    }

}
