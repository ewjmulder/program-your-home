package com.programyourhome.activities.model;

import com.programyourhome.config.Activity;

//TODO: Abstract with interfaces?
public class PyhActivity {

    private final int id;
    private final String name;
    private final String description;
    private final String iconUrl;

    public PyhActivity(final Activity activity, final String defaultIconFilename) {
        this.id = activity.getId();
        this.name = activity.getName();
        this.description = activity.getDescription();
        final String iconFilename;
        if (activity.getIcon() != null) {
            iconFilename = activity.getIcon();
        } else {
            iconFilename = defaultIconFilename;
        }
        this.iconUrl = "http://192.168.2.28:3737/img/icons/" + iconFilename;
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
