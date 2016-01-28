package com.programyourhome.barcodescanner.ui;

/**
 * Interface for a 16x2 LCD display with high level function definitions.
 */
public interface LcdDisplay {

    public void clear();

    public void show(String textLine1, String textLine2);

}
