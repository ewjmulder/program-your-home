package com.programyourhome.server.activities.model;

import java.util.function.Function;

import com.programyourhome.common.model.PyhImpl;
import com.programyourhome.server.config.model.Activity;
import com.programyourhome.server.config.model.InfraRedActivityConfig;
import com.programyourhome.server.config.model.PcInstructorActivityConfig;

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
    private final boolean mouseActivity;
    private final boolean keyboardActivity;
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
        this.volumeActivity = this.hasIrControl(activity, InfraRedActivityConfig::getVolumeControl);
        this.channelActivity = this.hasIrControl(activity, InfraRedActivityConfig::getChannelControl);
        this.playActivity = this.hasIrControl(activity, InfraRedActivityConfig::getPlayControl);
        this.skipActivity = this.hasIrControl(activity, InfraRedActivityConfig::getSkipControl);
        this.recordActivity = this.hasIrControl(activity, InfraRedActivityConfig::getRecordControl);
        this.menuActivity = this.hasIrControl(activity, InfraRedActivityConfig::getMenuControl);
        this.mouseActivity = this.hasPcControl(activity, PcInstructorActivityConfig::isMouseControl);
        this.keyboardActivity = this.hasPcControl(activity, PcInstructorActivityConfig::isKeyboardControl);
        this.active = active;
    }

    private boolean hasIrControl(final Activity activity, final Function<InfraRedActivityConfig, Integer> controlGetter) {
        return activity.getModules().getInfraRed() != null && controlGetter.apply(activity.getModules().getInfraRed()) != null;
    }

    private boolean hasPcControl(final Activity activity, final Function<PcInstructorActivityConfig, Boolean> hasControlFunction) {
        return activity.getModules().getPcInstructor() != null && hasControlFunction.apply(activity.getModules().getPcInstructor());
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
    public boolean isMouseActivity() {
        return this.mouseActivity;
    }

    @Override
    public boolean isKeyboardActivity() {
        return this.keyboardActivity;
    }

    @Override
    public boolean isActive() {
        return this.active;
    }

}
