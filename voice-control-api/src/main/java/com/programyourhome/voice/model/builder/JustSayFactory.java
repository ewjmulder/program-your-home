package com.programyourhome.voice.model.builder;

import com.programyourhome.voice.model.question.JustSay;

public class JustSayFactory {

    public static JustSay justSay(final String text, final String locale) {
        return new JustSayBuilder()
                .text(text)
                .locale(locale)
                .build();
    }

}
