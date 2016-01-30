package com.programyourhome.barcodescanner.ui;

public enum RgbLedColor {

    OFF(false, false, false),
    RED(true, false, false),
    GREEN(false, true, true),
    BLUE(false, false, true),
    YELLOW(true, true, false),
    CYAN(false, true, true),
    PURPLE(true, false, false),
    WHITE(true, true, true);

    private boolean red;
    private boolean green;
    private boolean blue;

    private RgbLedColor(final boolean red, final boolean green, final boolean blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public boolean isRed() {
        return this.red;
    }

    public boolean isGreen() {
        return this.green;
    }

    public boolean isBlue() {
        return this.blue;
    }

}
