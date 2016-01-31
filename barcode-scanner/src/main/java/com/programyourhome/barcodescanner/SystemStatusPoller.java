package com.programyourhome.barcodescanner;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import com.programyourhome.barcodescanner.ui.LcdDisplay;
import com.programyourhome.barcodescanner.ui.RgbLedLights;

@Component
public class SystemStatusPoller {

    @Inject
    private BarcodeScanDetector scanDetector;

    @Inject
    private LcdDisplay lcdDisplay;

    @Inject
    private RgbLedLights ledLights;

    private boolean detectorReady;

    public SystemStatusPoller() {
        this.detectorReady = false;
    }

    @Inject
    private TaskScheduler taskScheduler;

    @PostConstruct
    public void init() {
        this.taskScheduler.scheduleWithFixedDelay(this::pollSystemStatus, new Date(), 1000);
    }

    private void pollSystemStatus() {
        if (!this.detectorReady) {
            if (this.scanDetector.isActivelyScanning()) {
                this.detectorReady = true;
                this.lcdDisplay.show("Application", "booted and ready");
                this.ledLights.setSystemStateNormal();
            }
        } else {
            if (!this.scanDetector.isActivelyScanning()) {
                this.detectorReady = false;
                this.ledLights.setSystemStateError();
            }
        }
    }

}
