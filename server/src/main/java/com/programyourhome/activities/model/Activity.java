package com.programyourhome.activities.model;

//TODO: Abstract with interfaces?
public class Activity {

    private final String name;
    private final String description;

    public Activity(final String name, final String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

}
