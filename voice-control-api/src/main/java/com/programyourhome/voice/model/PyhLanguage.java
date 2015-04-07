package com.programyourhome.voice.model;

import java.util.Collection;

public interface PyhLanguage {

    public int getId();

    public String getName();

    public String getLocale();

    public Collection<String> getConfirmations();

    public Collection<String> getNegations();

}
