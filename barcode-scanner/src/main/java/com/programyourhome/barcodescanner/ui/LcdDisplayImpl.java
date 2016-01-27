package com.programyourhome.barcodescanner.ui;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Component;

import com.pi4j.component.lcd.impl.I2CLcdDisplay;
import com.pi4j.io.i2c.I2CBus;

@Component
public class LcdDisplayImpl implements LcdDisplay {

    private final I2CLcdDisplay lcd;

    public LcdDisplayImpl() throws Exception {
        this.lcd = new I2CLcdDisplay(2, 16, I2CBus.BUS_1, 0x27, 3, 0, 1, 2, 7, 6, 5, 4);
    }

    @Override
    public void clear() {
        this.lcd.clear();
    }

    @Override
    public void show(final String text) {
        this.show(text, "");
    }

    @Override
    public void show(final String textLine1, final String textLine2) {
        this.clear();
        this.lcd.setCursorPosition(0, 0);
        this.lcd.write(textLine1);
        this.lcd.setCursorPosition(0, 1);
        this.lcd.write(textLine2);
    }

    @Override
    public void show(final String textLine1, final String textLine2, final int millis) {
        throw new NotImplementedException("Not yet implemented");
    }

}
