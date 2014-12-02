package com.programyourhome.activities.model;

//TODO: Abstract with interfaces?
public class Activity {

    private final String name;
    private final String description;
    private final String iconUrl;

    public Activity(final String name, final String description, final String iconUrl) {
        this.name = name;
        this.description = description;
        this.iconUrl = iconUrl;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public String getIconUrl() {
        return this.iconUrl;
    }

}
