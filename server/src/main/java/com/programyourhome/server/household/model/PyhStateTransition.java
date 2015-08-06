package com.programyourhome.server.household.model;

public interface PyhStateTransition {

    public int getFrom();

    public int getTo();

    public String getName();

    public String getDescription();

}