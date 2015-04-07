package com.programyourhome.voice.model;

import java.util.Collection;

import com.programyourhome.voice.config.Language;

public class PyhLanguageImpl implements PyhLanguage {

    private final int id;
    private final String name;
    private final String locale;
    private final Collection<String> confirmations;
    private final Collection<String> negations;

    public PyhLanguageImpl(final Language language) {
        this(language.getId(), language.getName(), language.getLocale(), language.getConfirmations(), language.getNegations());
    }

    public PyhLanguageImpl(final int id, final String name, final String locale, final Collection<String> confirmations, final Collection<String> negations) {
        this.id = id;
        this.name = name;
        this.locale = locale;
        this.confirmations = confirmations;
        this.negations = negations;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getLocale() {
        return this.locale;
    }

    @Override
    public Collection<String> getConfirmations() {
        return this.confirmations;
    }

    @Override
    public Collection<String> getNegations() {
        return this.negations;
    }

}
