package com.programyourhome.server.activities.model;

public interface PyhActivity {

    public int getId();

    public String getName();

    public String getDescription();

    public String getIconUrl();

    public boolean isVolumeActivity();

    public boolean isChannelActivity();

    public boolean isPlayActivity();

    public boolean isSkipActivity();

    public boolean isRecordActivity();

    public boolean isMenuActivity();

    public boolean isMouseActivity();

    public boolean isKeyboardActivity();

    public boolean isActive();

}
