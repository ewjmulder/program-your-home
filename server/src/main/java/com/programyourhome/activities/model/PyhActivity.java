package com.programyourhome.activities.model;

//TODO: Abstract with interfaces?
public class PyhActivity {

    private final int id;
    private final String name;
    private final String description;
    private final String iconUrl;

    public PyhActivity(final int id, final String name, final String description, final String iconUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.iconUrl = iconUrl;
    }

    public int getId() {
        return this.id;
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
