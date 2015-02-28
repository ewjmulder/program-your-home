package com.programyourhome.server.controllers;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.programyourhome.ir.InfraRed;
import com.programyourhome.ir.model.PyhDevice;

@RestController
@RequestMapping("ir")
public class ProgramYourHomeControllerIr extends AbstractProgramYourHomeController {

    @Autowired
    private InfraRed infraRed;

    @RequestMapping("devices")
    public Collection<PyhDevice> getDevices() {
        return this.infraRed.getDevices();
    }

    @RequestMapping("devices/{id}/power/on")
    public void turnOn(@PathVariable("id") final int deviceId) {
        this.infraRed.turnOn(deviceId);
    }

    @RequestMapping("devices/{id}/power/off")
    public void turnOff(@PathVariable("id") final int deviceId) {
        this.infraRed.turnOff(deviceId);
    }

    @RequestMapping("devices/{id}/input/{input}")
    public void setInput(@PathVariable("id") final int deviceId, @PathVariable("input") final String input) {
        this.infraRed.setInput(deviceId, input);
    }

    @RequestMapping("devices/{id}/volume/up")
    public void volumeUp(@PathVariable("id") final int deviceId) {
        this.infraRed.volumeUp(deviceId);
    }

    @RequestMapping("devices/{id}/volume/down")
    public void volumeDown(@PathVariable("id") final int deviceId) {
        this.infraRed.volumeDown(deviceId);
    }

    @RequestMapping("devices/{id}/volume/mute")
    public void volumeMute(@PathVariable("id") final int deviceId) {
        this.infraRed.volumeMute(deviceId);
    }

    @RequestMapping("devices/{id}/channel/up")
    public void channelUp(@PathVariable("id") final int deviceId) {
        this.infraRed.channelUp(deviceId);
    }

    @RequestMapping("devices/{id}/channel/down")
    public void channelDown(@PathVariable("id") final int deviceId) {
        this.infraRed.channelDown(deviceId);
    }

    @RequestMapping("devices/{id}/channel/set/{channel}")
    public void setChannel(@PathVariable("id") final int deviceId, @PathVariable("channel") final int channel) {
        this.infraRed.setChannel(deviceId, channel);
    }

    @RequestMapping("devices/{id}/play/play")
    public void play(@PathVariable("id") final int deviceId) {
        this.infraRed.play(deviceId);
    }

    @RequestMapping("devices/{id}/play/pause")
    public void pause(@PathVariable("id") final int deviceId) {
        this.infraRed.pause(deviceId);
    }

    @RequestMapping("devices/{id}/play/stop")
    public void stop(@PathVariable("id") final int deviceId) {
        this.infraRed.stop(deviceId);
    }

    @RequestMapping("devices/{id}/play/fastForward")
    public void fastForward(@PathVariable("id") final int deviceId) {
        this.infraRed.fastForward(deviceId);
    }

    @RequestMapping("devices/{id}/play/rewind")
    public void rewind(@PathVariable("id") final int deviceId) {
        this.infraRed.rewind(deviceId);
    }

    @RequestMapping("devices/{id}/skip/next")
    public void skipNext(@PathVariable("id") final int deviceId) {
        this.infraRed.skipNext(deviceId);
    }

    @RequestMapping("devices/{id}/skip/previous")
    public void skipPrevious(@PathVariable("id") final int deviceId) {
        this.infraRed.skipPrevious(deviceId);
    }

    @RequestMapping("devices/{id}/record")
    public void record(@PathVariable("id") final int deviceId) {
        this.infraRed.record(deviceId);
    }

    @RequestMapping("devices/{id}/menu/toggle")
    public void menuToggle(@PathVariable("id") final int deviceId) {
        this.infraRed.menuToggle(deviceId);
    }

    @RequestMapping("devices/{id}/menu/select")
    public void menuSelect(@PathVariable("id") final int deviceId) {
        this.infraRed.menuSelect(deviceId);
    }

    @RequestMapping("devices/{id}/menu/back")
    public void menuBack(@PathVariable("id") final int deviceId) {
        this.infraRed.menuBack(deviceId);
    }

    @RequestMapping("devices/{id}/menu/up")
    public void menuUp(@PathVariable("id") final int deviceId) {
        this.infraRed.menuUp(deviceId);
    }

    @RequestMapping("devices/{id}/menu/down")
    public void menuDown(@PathVariable("id") final int deviceId) {
        this.infraRed.menuDown(deviceId);
    }

    @RequestMapping("devices/{id}/menu/left")
    public void menuLeft(@PathVariable("id") final int deviceId) {
        this.infraRed.menuLeft(deviceId);
    }

    @RequestMapping("devices/{id}/menu/right")
    public void menuRight(@PathVariable("id") final int deviceId) {
        this.infraRed.menuRight(deviceId);
    }

    @RequestMapping("devices/{id}/pressKeyOnRemote/{keyId}")
    public void pressKeyOnRemote(@PathVariable("id") final int deviceId, @PathVariable("keyId") final int keyId) {
        this.infraRed.pressKeyOnRemote(deviceId, keyId);
    }

}
