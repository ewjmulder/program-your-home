package com.programyourhome.hue.model;

public enum Mood {

    // TODO: These are the ones that Philips provides, but all Kelvin color temps in a certain range (2000-6500) can be set,
    // so maybe change / extens this interface to reflect that
    // TODO: test what the color temps are for the default moods and try values in between to see if it is useful to take those as well.

    CONCENTRATE,
    ENERGIZE,
    READ,
    RELAX;

}
