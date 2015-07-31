package com.programyourhome.server.dailyrhythym;

import java.time.LocalTime;

import com.programyourhome.common.util.LocalTimeUtil;
import com.programyourhome.server.config.model.KeyFrame;
import com.programyourhome.server.config.model.LightState;

public class RhythymSection {

    private static final int FULL_DAY_IN_SECONDS = 24 * 60 * 60;

    private final KeyFrame fromKeyFrame;
    private final KeyFrame toKeyFrame;
    private final LocalTime fromTime;
    private final LocalTime toTime;

    public RhythymSection(final KeyFrame fromKeyFrame, final KeyFrame toKeyFrame) {
        this.fromKeyFrame = fromKeyFrame;
        this.toKeyFrame = toKeyFrame;
        this.fromTime = LocalTime.parse(this.fromKeyFrame.getTime());
        this.toTime = LocalTime.parse(this.toKeyFrame.getTime());
    }

    public LocalTime getFromTime() {
        return this.fromTime;
    }

    public LocalTime getToTime() {
        return this.toTime;
    }

    public boolean doesCrossMidnight() {
        return this.toTime.isBefore(this.fromTime);
    }

    public int getDurationInSeconds() {
        int duration;
        if (this.doesCrossMidnight()) {
            duration = this.toTime.toSecondOfDay() + FULL_DAY_IN_SECONDS - this.fromTime.toSecondOfDay();
        } else {
            duration = this.toTime.toSecondOfDay() - this.fromTime.toSecondOfDay();
        }
        return duration;
    }

    public LightState getFromLightState() {
        LightState fromLightState;
        if (this.fromKeyFrame.getStartState() != null) {
            fromLightState = this.fromKeyFrame.getStartState();
        } else {
            fromLightState = this.fromKeyFrame.getEndState();
        }
        return fromLightState;
    }

    public LightState getToLightState() {
        return this.toKeyFrame.getEndState();
    }

    public boolean contains(final LocalTime time) {
        boolean inSection;
        if (this.doesCrossMidnight()) {
            inSection = time.isBefore(this.toTime) || LocalTimeUtil.isEqualOrAfter(time, this.fromTime);
        } else {
            inSection = LocalTimeUtil.isEqualOrAfter(time, this.fromTime) && time.isBefore(this.toTime);
        }
        return inSection;
    }

    /**
     * Calculates the fraction of time within this section that has passed, based on the time provided.
     *
     * @param time the time for the fraction
     * @return the fraction of time within this section
     * @throws IllegalArgumentException if the provided time is not in this section
     */
    public double getFraction(final LocalTime time) {
        if (!this.contains(time)) {
            throw new IllegalArgumentException("The time must be inside the section to calculate a fraction.");
        }
        final double secondsSinceStart;
        if (this.doesCrossMidnight() && time.isBefore(this.toTime)) {
            secondsSinceStart = time.toSecondOfDay() + FULL_DAY_IN_SECONDS - this.fromTime.toSecondOfDay();
        } else {
            secondsSinceStart = time.toSecondOfDay() - this.fromTime.toSecondOfDay();
        }
        return secondsSinceStart / this.getDurationInSeconds();
    }

}
