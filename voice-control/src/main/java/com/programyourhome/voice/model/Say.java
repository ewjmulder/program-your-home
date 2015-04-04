package com.programyourhome.voice.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class Say {

    private final String text;
    private final Language language;
    private final Runnable callback;

    public Say(final String text, final Language language) {
        this(text, language, () -> { /* Empty callback */
        });
    }

    public Say(final String text, final Language language, final Runnable callback) {
        this.text = text;
        this.language = language;
        this.callback = callback;
    }

    public String getText() {
        return this.text;
    }

    public Language getLanguage() {
        return this.language;
    }

    public Runnable getCallback() {
        return this.callback;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
