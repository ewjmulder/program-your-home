package com.programyourhome.model;

public interface Device {

    /**
     * The name of the device. Should be unique,
     * but this is not a technical requirement.
     *
     * @return the name of the device
     */
    public String getName();

    public Room getRoom();

}
