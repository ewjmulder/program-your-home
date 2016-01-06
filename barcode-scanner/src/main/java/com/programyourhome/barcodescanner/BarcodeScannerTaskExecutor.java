package com.programyourhome.barcodescanner;

import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("serial")
public class BarcodeScannerTaskExecutor extends ThreadPoolTaskScheduler {

    public BarcodeScannerTaskExecutor() {
        // We just need 1 thread to do the scanning.
        this.setPoolSize(1);
    }

}
