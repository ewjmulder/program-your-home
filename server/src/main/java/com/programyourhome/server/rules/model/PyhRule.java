package com.programyourhome.server.rules.model;

public interface PyhRule {

    public String getName();

    public String getDescription();

    public boolean isTriggered();

    public void executeAction();

}
