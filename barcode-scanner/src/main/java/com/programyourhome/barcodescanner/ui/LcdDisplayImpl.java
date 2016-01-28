package com.programyourhome.barcodescanner.ui;

import java.time.LocalDateTime;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

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

    @Inject
    private TaskScheduler taskScheduler;

    @Value("lcd.i2c.address.hex")
    private String hexI2CAddress;

    @Value("lcd.sleep.after.seconds")
    private int sleepAfterSeconds;

    private I2CLcdDisplay lcd;

    private LocalDateTime lastUpdate;

    @PostConstruct
    public void init() throws Exception {
        final int i2cAddress = Integer.decode(this.hexI2CAddress);
        this.lcd = new I2CLcdDisplay(2, 16, I2CBus.BUS_1, i2cAddress, 3, 0, 1, 2, 7, 6, 5, 4);

        this.lastUpdate = LocalDateTime.MIN;
        this.taskScheduler.scheduleWithFixedDelay(this::detectInactivity, 1000);
    }

    @Override
    public void clear() {
        this.clear(true);
    }

    public void clear(final boolean backlight) {
        this.lcd.setBacklight(true);
        this.lcd.clear();
    }

    @Override
    public void show(final String textLine1, final String textLine2) {
        this.clear(true);
        this.lcd.setCursorPosition(0, 0);
        this.lcd.write(textLine1);
        this.lcd.setCursorPosition(0, 1);
        this.lcd.write(textLine2);

        this.lastUpdate = LocalDateTime.now();
    }

    private void detectInactivity() {
        if (LocalDateTime.now().minusSeconds(this.sleepAfterSeconds).compareTo(this.lastUpdate) > 0) {
            this.clear(false);
        }
    }

}
