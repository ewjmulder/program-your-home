package com.programyourhome.ir;

import java.util.List;

import com.programyourhome.ir.model.Remote;

public interface InfraRed {

    public List<Remote> getDevices();

    // TODO: Use more abstracted methodes in interface.
    public void sendCommand(String remote, String key);

}