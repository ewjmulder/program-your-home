package com.programyourhome.server.dailyrhythym;

import java.time.LocalTime;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.util.streamex.StreamEx;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import com.programyourhome.sensors.SunDegreeSensor;
import com.programyourhome.server.config.ServerConfigHolder;
import com.programyourhome.server.config.model.KeyFrame;

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
        final LocalTime currentTime = LocalTime.now();
        final RhythymSection activeSection = this.getActiveSection(currentTime);
        final double fraction = activeSection.getFraction(currentTime);
        // TODO: calculate current light state to be set based on section and fraction.
    }

    /**
     * Get the active section, that means the section that contains the provided time.
     * This method should always return a non-null result, since by design there will always be a active section.
     *
     * @param time the time
     * @return the active section
     */
    private RhythymSection getActiveSection(final LocalTime time) {
        final List<KeyFrame> keyFrames = this.configHolder.getConfig().getDailyRhythm().getKeyFrames();
        return StreamEx.of(keyFrames)
                .append(keyFrames.get(0)) // append the first item to 'close the loop'
                .pairMap(RhythymSection::new)
                .findFirst(section -> section.contains(time))
                .get(); // save to call get(), since the sections cover the full day, so there must always be a section that contains the provided time
    }
}
