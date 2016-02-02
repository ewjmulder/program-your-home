package com.programyourhome.barcodescanner.ui;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.commons.lang3.mutable.MutableInt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import com.pi4j.component.lcd.impl.I2CLcdDisplay;
import com.pi4j.io.i2c.I2CBus;

/**
 * This class implements the lcd display interface. Any text shown on the screen will be removed
 * after a certain inactivity timeout.
 */
@Component
public class LcdDisplayImpl implements LcdDisplay {

    private static final int ROW_WIDTH = 16;

    @Inject
    private TaskScheduler taskScheduler;

    @Value("${lcd.i2c.address.hex}")
    private String hexI2CAddress;

    @Value("${lcd.sleep.after.seconds}")
    private int sleepAfterSeconds;

    @Value("${scroll.delay.initial}")
    private int scrollInitialDelay;

    @Value("${scroll.delay.character}")
    private int scrollCharacterDelay;

    private I2CLcdDisplay lcd;

    private LocalDateTime lastUpdate;

    private Set<Future<?>> scrollingTasks;

    @PostConstruct
    public void init() throws Exception {
        final int i2cAddress = Integer.decode(this.hexI2CAddress);
        this.lcd = new I2CLcdDisplay(2, 16, I2CBus.BUS_1, i2cAddress, 3, 0, 1, 2, 7, 6, 5, 4);

        this.lastUpdate = LocalDateTime.MIN;
        this.taskScheduler.scheduleWithFixedDelay(this::detectInactivity, 1000);
        this.scrollingTasks = new HashSet<>();
    }

    @PreDestroy
    public void cleanup() {
        this.clearScrollingTasks();
    }

    private synchronized void clearScrollingTasks() {
        final Iterator<Future<?>> iter = this.scrollingTasks.iterator();
        while (iter.hasNext()) {
            final Future<?> scrollingTask = iter.next();
            if (!scrollingTask.isDone()) {
                scrollingTask.cancel(true);
            }
            iter.remove();
        }
    }

    @Override
    public synchronized void clear() {
        this.clear(true);
    }

    public synchronized void clear(final boolean backlight) {
        this.clearScrollingTasks();
        this.lcd.setBacklight(backlight);
        this.lcd.clear();
    }

    @Override
    public synchronized void show(final String textLine1, final String textLine2) {
        this.clear(true);
        this.writeLine(0, textLine1);
        this.writeLine(1, textLine2);
        this.lastUpdate = LocalDateTime.now();
    }

    private void writeLine(final int row, final String text) {
        this.lcd.setCursorPosition(row, 0);
        if (text.length() <= ROW_WIDTH) {
            this.lcd.write(text);
        } else {
            this.lcd.write(text.substring(0, ROW_WIDTH));
            final MutableInt scrollPosition = new MutableInt(0);
            this.scrollingTasks.add(this.taskScheduler.scheduleWithFixedDelay(() -> {
                scrollPosition.increment();
                this.lcd.setCursorPosition(row, 0);
                this.lcd.write(text.substring(scrollPosition.getValue(), scrollPosition.getValue() + ROW_WIDTH));
                if (text.length() - scrollPosition.getValue() <= ROW_WIDTH) {
                    this.clearScrollingTasks();
                }
                this.lastUpdate = LocalDateTime.now();
            }, this.getScrollStart(), this.scrollCharacterDelay));
        }
    }

    private Date getScrollStart() {
        return new Date(System.currentTimeMillis() + this.scrollInitialDelay);
    }

    private void detectInactivity() {
        if (LocalDateTime.now().minusSeconds(this.sleepAfterSeconds).compareTo(this.lastUpdate) > 0) {
            this.clear(false);
        }
    }

}
