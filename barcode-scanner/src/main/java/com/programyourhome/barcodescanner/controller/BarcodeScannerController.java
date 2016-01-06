package com.programyourhome.barcodescanner.controller;

import java.util.List;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.programyourhome.barcodescanner.BarcodeEventPublisher;
import com.programyourhome.barcodescanner.BarcodeProcessor;
import com.programyourhome.barcodescanner.ScannedBarcodeLogger;
import com.programyourhome.barcodescanner.model.ProcessorMode;
import com.programyourhome.common.controller.AbstractProgramYourHomeController;
import com.programyourhome.common.response.ServiceResult;

@RestController
@RequestMapping("barcodescanner")
public class BarcodeScannerController extends AbstractProgramYourHomeController {

    @Inject
    private BarcodeProcessor barcodeProcessor;

    @Inject
    private ScannedBarcodeLogger scannedBarcodeLogger;

    @Inject
    private BarcodeEventPublisher barcodeEventPublisher;

    /**
     * This provides an easy way the see if the REST service is reachable.
     *
     * @return the text string 'pong'
     */
    @RequestMapping(value = "status/ping", method = RequestMethod.GET)
    public String pingService() {
        return "pong";
    }

    @RequestMapping(value = "mode", method = RequestMethod.GET)
    public ServiceResult<ProcessorMode> getMode() {
        return this.produce("Mode", () -> this.barcodeProcessor.getMode());
    }

    @RequestMapping(value = "log", method = RequestMethod.GET)
    public ServiceResult<List<String>> getLog() {
        return this.produce("LogLines", () -> this.scannedBarcodeLogger.getLogLines());
    }

    @RequestMapping(value = "barcodeScanned/{barcode}", method = RequestMethod.POST)
    public ServiceResult<Void> simulateBarcodeScan(@PathVariable("barcode") final String barcode) {
        return this.run(() -> this.barcodeEventPublisher.publishBarcodeEvent(barcode));
    }

}
