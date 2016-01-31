package com.programyourhome.barcodescanner.ui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.RaspiPin;

@Component
public class RgbLedLightsImpl implements RgbLedLights {

    private static final String GPIO_PIN_NAME_PREFIX = "GPIO ";

    private enum Led {
        SYSTEM_STATE, MODE, TRANSACTION;
    }

    @Value("${led.systemstate.red}")
    private int systemStateRed;

    @Value("${led.systemstate.green}")
    private int systemStateGreen;

    @Value("${led.systemstate.blue}")
    private int systemStateBlue;

    @Value("${led.mode.red}")
    private int modeRed;

    @Value("${led.mode.green}")
    private int modeGreen;

    @Value("${led.mode.blue}")
    private int modeBlue;

    @Value("${led.transaction.red}")
    private int transactionRed;

    @Value("${led.transaction.green}")
    private int transactionGreen;

    @Value("${led.transaction.blue}")
    private int transactionBlue;

    @Value("${led.blink.delay}")
    private int blinkDelay;

    private GpioController gpio;

    private final Map<Led, GpioPinDigitalOutput[]> ledConfiguration;

    private final Map<Led, Set<Future<?>>> blinkTaskMap;

    public RgbLedLightsImpl() {
        this.ledConfiguration = new HashMap<>();
        this.blinkTaskMap = new HashMap<>();

        this.blinkTaskMap.put(Led.SYSTEM_STATE, new HashSet<>());
        this.blinkTaskMap.put(Led.MODE, new HashSet<>());
        this.blinkTaskMap.put(Led.TRANSACTION, new HashSet<>());
    }

    @PostConstruct
    public void init() {
        this.gpio = GpioFactory.getInstance();

        this.ledConfiguration.put(Led.SYSTEM_STATE, new GpioPinDigitalOutput[] {
                this.getPin(this.systemStateRed), this.getPin(this.systemStateGreen), this.getPin(this.systemStateBlue)
        });
        this.ledConfiguration.put(Led.MODE, new GpioPinDigitalOutput[] {
                this.getPin(this.modeRed), this.getPin(this.modeGreen), this.getPin(this.modeBlue)
        });
        this.ledConfiguration.put(Led.TRANSACTION, new GpioPinDigitalOutput[] {
                this.getPin(this.transactionRed), this.getPin(this.transactionGreen), this.getPin(this.transactionBlue)
        });
    }

    @PreDestroy
    public void cleanup() {
        this.gpio.shutdown();
    }

    private GpioPinDigitalOutput getPin(final int number) {
        return this.gpio.provisionDigitalOutputPin(RaspiPin.getPinByName(GPIO_PIN_NAME_PREFIX + number));
    }

    private void addBlinkTask(final Led led, final Future<?> blinkTask) {
        this.blinkTaskMap.get(led).add(blinkTask);
    }

    private void stopAndClearBlinkTask(final Led led) {
        final Iterator<Future<?>> blinkTasks = this.blinkTaskMap.get(led).iterator();
        // Cancel and remove all existing blink tasks.
        while (blinkTasks.hasNext()) {
            final Future<?> blinkTask = blinkTasks.next();
            blinkTask.cancel(true);
            blinkTasks.remove();
        }
    }

    private synchronized void setLed(final Led led, final RgbLedColor color, final boolean blinking) {
        // Stop any existing blink tasks for this led.
        this.stopAndClearBlinkTask(led);

        final GpioPinDigitalOutput redPin = this.ledConfiguration.get(led)[0];
        final GpioPinDigitalOutput greenPin = this.ledConfiguration.get(led)[1];
        final GpioPinDigitalOutput bluePin = this.ledConfiguration.get(led)[2];

        this.setState(led, redPin, blinking, color.isRed());
        this.setState(led, greenPin, blinking, color.isGreen());
        this.setState(led, bluePin, blinking, color.isBlue());
    }

    private void setState(final Led led, final GpioPinDigitalOutput pin, final boolean blinking, final boolean on) {
        if (on) {
            if (blinking) {
                this.addBlinkTask(led, pin.blink(this.blinkDelay));
            } else {
                pin.high();
            }
        } else {
            pin.low();
        }
    }

    @Override
    public void setSystemStateBooting() {
        this.setLed(Led.SYSTEM_STATE, RgbLedColor.BLUE, true);
    }

    @Override
    public void setSystemStateNormal() {
        this.setLed(Led.SYSTEM_STATE, RgbLedColor.GREEN, false);
    }

    @Override
    public void setSystemStateError() {
        this.setLed(Led.SYSTEM_STATE, RgbLedColor.RED, true);
    }

    @Override
    public void setModeInfo() {
        this.setLed(Led.MODE, RgbLedColor.BLUE, false);
    }

    @Override
    public void setModeAddToStock() {
        this.setLed(Led.MODE, RgbLedColor.GREEN, false);
    }

    @Override
    public void setModeRemoveFromStock() {
        this.setLed(Led.MODE, RgbLedColor.RED, false);
    }

    @Override
    public void setTransactionNone() {
        this.setLed(Led.TRANSACTION, RgbLedColor.OFF, false);
    }

    @Override
    public void setTransactionProcessing() {
        this.setLed(Led.TRANSACTION, RgbLedColor.BLUE, true);
    }

    @Override
    public void setTransactionOk() {
        this.setLed(Led.TRANSACTION, RgbLedColor.GREEN, false);
    }

    @Override
    public void setTransactionError() {
        this.setLed(Led.TRANSACTION, RgbLedColor.RED, false);
    }

}
