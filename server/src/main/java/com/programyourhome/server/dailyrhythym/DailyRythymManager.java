package com.programyourhome.server.dailyrhythym;

import java.time.LocalTime;
import java.util.Date;
import java.util.LinkedList;
import java.util.ListIterator;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import com.programyourhome.sensors.SunDegreeSensor;
import com.programyourhome.server.config.ServerConfigHolder;
import com.programyourhome.server.config.model.KeyFrame;
import com.programyourhome.server.config.model.PhilipsHueDailyRhythymConfig;

@Component
public class DailyRythymManager {

    // TODO: move to properties of some kind
    // TODO: rewrite to class duration or so?
    private static final int UPDATE_INITIAL_DELAY = 2000;
    private static final long UPDATE_INTERVAL = 60 * 1000;

    @Autowired
    private ServerConfigHolder configHolder;

    @Autowired
    private SunDegreeSensor sunDegreeSensor;

    // TODO: use Spring scheduling annotations?
    @Autowired
    @Qualifier("PyhExecutor")
    private TaskScheduler rhythymScheduler;

    @PostConstruct
    public void init() {
        this.rhythymScheduler.scheduleAtFixedRate(this::updateRhythym, DateUtils.addMilliseconds(new Date(), UPDATE_INITIAL_DELAY), UPDATE_INTERVAL);
    }

    // Note: this logic assumes the times of the keyframes were previously validated to be in ascending order.
    public void updateRhythym() {
        final PhilipsHueDailyRhythymConfig dailyRhythm = this.configHolder.getConfig().getDailyRhythm();
        // Copy the list in the config.
        final LinkedList<KeyFrame> keyFrames = new LinkedList<>(dailyRhythm.getKeyFrames());

        KeyFrame previousKeyFrame;
        KeyFrame nextKeyFrame;

        final LocalTime currentTime = LocalTime.now();
        if (this.isBefore(currentTime, keyFrames.getFirst())
                || this.isEqualOrAfter(currentTime, keyFrames.getLast())) {
            previousKeyFrame = keyFrames.getLast();
            nextKeyFrame = keyFrames.getFirst();
        } else {
            final ListIterator<KeyFrame> listIterator = keyFrames.listIterator();
            previousKeyFrame = listIterator.next();
            while (listIterator.hasNext()) {
                nextKeyFrame = listIterator.next();
                if (this.isEqualOrAfter(currentTime, previousKeyFrame)
                        && this.isBefore(currentTime, nextKeyFrame)) {
                    break;
                } else {
                    previousKeyFrame = nextKeyFrame;
                }
            }
        }
    }

    private boolean isBefore(final LocalTime time, final KeyFrame keyFrame) {
        final LocalTime keyFrameTime = this.toTime(keyFrame);
        return time.isBefore(keyFrameTime);
    }

    private boolean isEqualOrAfter(final LocalTime time, final KeyFrame keyFrame) {
        final LocalTime keyFrameTime = this.toTime(keyFrame);
        return time.equals(keyFrameTime) || time.isAfter(keyFrameTime);
    }

    private LocalTime toTime(final KeyFrame keyFrame) {
        return LocalTime.parse(keyFrame.getTime());
    }
}
