package com.programyourhome.server.activities.model;

import com.programyourhome.common.model.PyhImpl;
import com.programyourhome.server.config.model.Activity;

public class PyhActivityImpl extends PyhImpl implements PyhActivity {

    private final int id;
    private final String name;
    private final String description;
    private final String iconUrl;
    private final boolean volumeActivity;
    private final boolean channelActivity;
    private final boolean playActivity;
    private final boolean skipActivity;
    private final boolean recordActivity;
    private final boolean menuActivity;
    private final boolean active;

    // TODO: Weird to provide baseUrl to activity, make a separate icon/image class?
    public PyhActivityImpl(final Activity activity, final boolean active, final String baseUrl, final String defaultIconFilename) {
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
        this.volumeActivity = activity.getModules().getInfraRed().getVolumeControl() != null;
        this.channelActivity = activity.getModules().getInfraRed().getChannelControl() != null;
        this.playActivity = activity.getModules().getInfraRed().getPlayControl() != null;
        this.skipActivity = activity.getModules().getInfraRed().getSkipControl() != null;
        this.recordActivity = activity.getModules().getInfraRed().getRecordControl() != null;
        this.menuActivity = activity.getModules().getInfraRed().getMenuControl() != null;
        this.active = active;
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
    public String getDescription() {
        return this.description;
    }

    @Override
    public String getIconUrl() {
        return this.iconUrl;
    }

    @Override
    public boolean isVolumeActivity() {
        return this.volumeActivity;
    }

    @Override
    public boolean isChannelActivity() {
        return this.channelActivity;
    }

    @Override
    public boolean isPlayActivity() {
        return this.playActivity;
    }

    @Override
    public boolean isSkipActivity() {
        return this.skipActivity;
    }

    @Override
    public boolean isRecordActivity() {
        return this.recordActivity;
    }

    @Override
    public boolean isMenuActivity() {
        return this.menuActivity;
    }

    @Override
    public boolean isActive() {
        return this.active;
    }

}
