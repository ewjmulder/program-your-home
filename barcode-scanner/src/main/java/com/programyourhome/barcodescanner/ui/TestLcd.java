package com.programyourhome.barcodescanner.ui;

import com.pi4j.component.lcd.impl.I2CLcdDisplay;
import com.pi4j.io.i2c.I2CBus;

public class TestLcd {

    public static void main(final String[] args) throws Exception {
        final I2CLcdDisplay lcd = new I2CLcdDisplay(2, 16, I2CBus.BUS_1, 0x27, 3, 0, 1, 2, 7, 6, 5, 4);

        // Set backlight only works when something is written afterwards!! (see source code)
        lcd.setBacklight(true);
        lcd.clear();
        lcd.setCursorPosition(0);
        lcd.write("LCD Test 1!");
        Thread.sleep(1000);
        lcd.setBacklight(false);
        lcd.clear();
        Thread.sleep(1000);
        lcd.setBacklight(true);
        lcd.write("LCD Test 2!");
    }
}
