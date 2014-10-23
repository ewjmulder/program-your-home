package com.programyourhome.hue.model;

public enum Mood {

    /*
     * Interestingly, when we set ct ourselves eg to read-343 it'll be 340 after recalculation at the bridge/lamp.
     * But if we let the Philips app set the read-343 it'll stay that way. Probably it actually sets the x,y to a slightly different point
     * that'll give 343 on the ct side after reculculation. Take this into account? --> actually the HueSat seems to be leading.
     * TODO: Probably a good one to upgrade this enum with the HS values and use those in the API, but keep the temp values too for reference.
     */

    ENERGY(155),
    FOCUS(234),
    READ(343),
    RELAX(467);

    private int temperature;

    private Mood(final int temperature) {
        this.temperature = temperature;
    }

    public int getTemperature() {
        return this.temperature;
    }

}
