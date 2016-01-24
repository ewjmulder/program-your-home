package com.programyourhome.barcodescanner.ui;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;

public class TestLcd {

    // # commands
    private static final byte LCD_CLEARDISPLAY = 0x01;
    private static final byte LCD_RETURNHOME = 0x02;
    private static final byte LCD_ENTRYMODESET = 0x04;
    private static final byte LCD_DISPLAYCONTROL = 0x08;
    private static final byte LCD_CURSORSHIFT = 0x10;
    private static final byte LCD_FUNCTIONSET = 0x20;
    private static final byte LCD_SETCGRAMADDR = 0x40;
    // private static final byte LCD_SETDDRAMADDR = 0x80;

    // # flags for display entry mode
    private static final byte LCD_ENTRYRIGHT = 0x00;
    private static final byte LCD_ENTRYLEFT = 0x02;
    private static final byte LCD_ENTRYSHIFTINCREMENT = 0x01;
    private static final byte LCD_ENTRYSHIFTDECREMENT = 0x00;

    // # flags for display on/off control
    private static final byte LCD_DISPLAYON = 0x04;
    private static final byte LCD_DISPLAYOFF = 0x00;
    private static final byte LCD_CURSORON = 0x02;
    private static final byte LCD_CURSOROFF = 0x00;
    private static final byte LCD_BLINKON = 0x01;
    private static final byte LCD_BLINKOFF = 0x00;

    // # flags for display/cursor shift
    private static final byte LCD_DISPLAYMOVE = 0x08;
    private static final byte LCD_CURSORMOVE = 0x00;
    private static final byte LCD_MOVERIGHT = 0x04;
    private static final byte LCD_MOVELEFT = 0x00;

    // # flags for function set
    private static final byte LCD_8BITMODE = 0x10;
    private static final byte LCD_4BITMODE = 0x00;
    private static final byte LCD_2LINE = 0x08;
    private static final byte LCD_1LINE = 0x00;
    private static final byte LCD_5x10DOTS = 0x04;
    private static final byte LCD_5x8DOTS = 0x00;

    // # flags for backlight control
    private static final byte LCD_BACKLIGHT = 0x08;
    private static final byte LCD_NOBACKLIGHT = 0x00;

    public static void main(final String[] args) throws Exception {
        final I2CBus i2cBus = I2CFactory.getInstance(1);
        final I2CDevice lcd = i2cBus.getDevice(0x27);
        lcd.write(LCD_NOBACKLIGHT);

        Thread.sleep(1000);

        lcd.write(LCD_BACKLIGHT);
    }

}
