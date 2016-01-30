package com.programyourhome.barcodescanner.ui;

import java.util.HashMap;
import java.util.Map;

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

    private GpioController gpio;

    private final Map<Led, GpioPinDigitalOutput[]> ledConfiguration;

    public RgbLedLightsImpl() {
        this.ledConfiguration = new HashMap<>();
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

    private void setLed(final Led led, final RgbLedColor color, final boolean blinking) {
        final GpioPinDigitalOutput redPin = this.ledConfiguration.get(led)[0];
        final GpioPinDigitalOutput greenPin = this.ledConfiguration.get(led)[1];
        final GpioPinDigitalOutput bluePin = this.ledConfiguration.get(led)[2];

        redPin.setState(color.isRed());
        greenPin.setState(color.isGreen());
        bluePin.setState(color.isBlue());

        // TODO: blinking!?!
        // redPin.blink(delay, duration)
        // redPin.pulse(delay, duration)
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
