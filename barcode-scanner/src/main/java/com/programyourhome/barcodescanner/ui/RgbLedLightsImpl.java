package com.programyourhome.barcodescanner.ui;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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

    private enum LedActivity {
        NONE, PULSE, BLINK;
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

    @Value("${led.pulse.time}")
    private int pulseTime;

    private GpioController gpio;

    private final Map<Led, List<GpioPinDigitalOutput>> ledConfiguration;

    private final Map<Led, Set<Future<?>>> ledTaskMap;

    public RgbLedLightsImpl() {
        this.ledConfiguration = new HashMap<>();
        this.ledTaskMap = new HashMap<>();

        this.ledTaskMap.put(Led.SYSTEM_STATE, new HashSet<>());
        this.ledTaskMap.put(Led.MODE, new HashSet<>());
        this.ledTaskMap.put(Led.TRANSACTION, new HashSet<>());
    }

    @PostConstruct
    public void init() {
        this.gpio = GpioFactory.getInstance();

        this.ledConfiguration.put(Led.SYSTEM_STATE, Arrays.asList(
                this.getPin(this.systemStateRed), this.getPin(this.systemStateGreen), this.getPin(this.systemStateBlue)));
        this.ledConfiguration.put(Led.MODE, Arrays.asList(
                this.getPin(this.modeRed), this.getPin(this.modeGreen), this.getPin(this.modeBlue)));
        this.ledConfiguration.put(Led.TRANSACTION, Arrays.asList(
                this.getPin(this.transactionRed), this.getPin(this.transactionGreen), this.getPin(this.transactionBlue)));
    }

    @PreDestroy
    public void cleanup() {
        this.gpio.shutdown();
    }

    private GpioPinDigitalOutput getPin(final int number) {
        return this.gpio.provisionDigitalOutputPin(RaspiPin.getPinByName(GPIO_PIN_NAME_PREFIX + number));
    }

    private void addTask(final Led led, final Future<?> task) {
        this.ledTaskMap.get(led).add(task);
    }

    private void stopAndClearTasks(final Led led) {
        final Iterator<Future<?>> ledTasks = this.ledTaskMap.get(led).iterator();
        // Cancel and remove all existing tasks.
        while (ledTasks.hasNext()) {
            final Future<?> task = ledTasks.next();
            if (!task.isDone()) {
                task.cancel(true);
            }
            ledTasks.remove();
        }
    }

    private synchronized void setLed(final Led led, final RgbLedColor color, final LedActivity ledActivity) {
        // Stop any existing tasks for this led.
        this.stopAndClearTasks(led);

        final GpioPinDigitalOutput redPin = this.ledConfiguration.get(led).get(0);
        final GpioPinDigitalOutput greenPin = this.ledConfiguration.get(led).get(1);
        final GpioPinDigitalOutput bluePin = this.ledConfiguration.get(led).get(2);

        this.setState(led, redPin, color.isRed(), ledActivity);
        this.setState(led, greenPin, color.isGreen(), ledActivity);
        this.setState(led, bluePin, color.isBlue(), ledActivity);
    }

    private void setState(final Led led, final GpioPinDigitalOutput pin, final boolean on, final LedActivity ledActivity) {
        if (on) {
            if (ledActivity == LedActivity.NONE) {
                pin.high();
            } else if (ledActivity == LedActivity.PULSE) {
                this.addTask(led, pin.pulse(this.pulseTime));
            } else if (ledActivity == LedActivity.BLINK) {
                this.addTask(led, pin.blink(this.blinkDelay));
            }
        } else {
            pin.low();
        }
    }

    @Override
    public void setSystemStateBooting() {
        this.setLed(Led.SYSTEM_STATE, RgbLedColor.BLUE, LedActivity.NONE);
    }

    @Override
    public void setSystemStateNormal() {
        this.setLed(Led.SYSTEM_STATE, RgbLedColor.GREEN, LedActivity.NONE);
    }

    @Override
    public void setSystemStateError() {
        this.setLed(Led.SYSTEM_STATE, RgbLedColor.RED, LedActivity.BLINK);
    }

    @Override
    public void setModeNone() {
        this.setLed(Led.MODE, RgbLedColor.OFF, LedActivity.NONE);
    }

    @Override
    public void setModeInfo() {
        this.setLed(Led.MODE, RgbLedColor.BLUE, LedActivity.NONE);
    }

    @Override
    public void setModeAddToStock() {
        this.setLed(Led.MODE, RgbLedColor.GREEN, LedActivity.NONE);
    }

    @Override
    public void setModeRemoveFromStock() {
        this.setLed(Led.MODE, RgbLedColor.RED, LedActivity.NONE);
    }

    @Override
    public void setTransactionNone() {
        this.setLed(Led.TRANSACTION, RgbLedColor.OFF, LedActivity.NONE);
    }

    @Override
    public void setTransactionProcessing() {
        this.setLed(Led.TRANSACTION, RgbLedColor.BLUE, LedActivity.BLINK);
    }

    @Override
    public void setTransactionOk() {
        this.setLed(Led.TRANSACTION, RgbLedColor.GREEN, LedActivity.PULSE);
    }

    @Override
    public void setTransactionError() {
        this.setLed(Led.TRANSACTION, RgbLedColor.RED, LedActivity.PULSE);
    }

}
