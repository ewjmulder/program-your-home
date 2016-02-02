package com.programyourhome.barcodescanner;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
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
        this.ledLights.setSystemStateBooting();
    }

    @PreDestroy
    public void shutdown() {
        // Set the lights default values at shutdown, because the Pi will remember those pin states and show them
        // during the next boot cycle. (unfortunately, reboot-remembering is not the case, but still ok to set to default)
        this.ledLights.setSystemStateBooting();
        this.ledLights.setModeNone();
        this.ledLights.setTransactionNone();
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
