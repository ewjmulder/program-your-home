package com.programyourhome.barcodescanner.ui;

/**
 * Interface for a 16x2 LCD display with high level function definitions.
 * You can show a text, split it by line, give a timeout and decide if the backlight should blink.
 * Any text that is too long for the display will scroll to the end once.
 */
public interface LcdDisplay {

    public void clear();

    /**
     * Shows the text on line 1 only.
     *
     * @param text the text
     */
    public void show(String text);

    public void show(String textLine1, String textLine2);

    public void show(String textLine1, String textLine2, int millis);

}
