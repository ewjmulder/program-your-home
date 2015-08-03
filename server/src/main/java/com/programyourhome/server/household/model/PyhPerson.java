package com.programyourhome.server.household.model;

public interface PyhPerson {

    public int getId();

    public PersonType getType();

    public String getFirstName();

    public String getLastName();

    public PyhState getState();

}