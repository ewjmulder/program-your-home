package com.programyourhome.server.activities.model;

import com.programyourhome.common.model.PyhImpl;
import com.programyourhome.server.config.model.Activity;

//TODO: Abstract with interfaces?
public class PyhActivity extends PyhImpl {

    private final int id;
    private final String name;
    private final String description;
    private final String iconUrl;
    private final boolean isActive;

    // TODO: Weird to provide baseUrl to activity, make a separate icon/image class?
    public PyhActivity(final Activity activity, final boolean isActive, final String baseUrl, final String defaultIconFilename) {
        this.id = activity.getId();
        this.name = activity.getName();
        this.description = activity.getDescription();
        final String iconFilename;
        if (activity.getIcon() != null) {
            iconFilename = activity.getIcon();
        } else {
            iconFilename = defaultIconFilename;
        }
        this.iconUrl = baseUrl + "/img/icons/" + iconFilename;
        this.isActive = isActive;
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

    public boolean isActive() {
        return this.isActive;
    }

}
