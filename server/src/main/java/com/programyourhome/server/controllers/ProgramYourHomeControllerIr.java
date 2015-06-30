package com.programyourhome.server.controllers;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.programyourhome.ir.InfraRed;
import com.programyourhome.ir.model.PyhDevice;
import com.programyourhome.server.model.ServiceResult;

@RestController
@RequestMapping("ir")
public class ProgramYourHomeControllerIr extends AbstractProgramYourHomeController {

    @Autowired
    private InfraRed infraRed;

    @RequestMapping("devices")
    public Collection<PyhDevice> getDevices() {
        return this.infraRed.getDevices();
    }

    @RequestMapping("devices/{id}")
    public PyhDevice getDevice(@PathVariable("id") final int deviceId) {
        return this.infraRed.getDevice(deviceId);
    }

    @RequestMapping("devices/{id}/power/on")
    public ServiceResult turnOn(@PathVariable("id") final int deviceId) {
        this.infraRed.turnOn(deviceId);
        return ServiceResult.success();
    }

    @RequestMapping("devices/{id}/power/off")
    public ServiceResult turnOff(@PathVariable("id") final int deviceId) {
        this.infraRed.turnOff(deviceId);
        return ServiceResult.success();
    }

    @RequestMapping("devices/{id}/input/{input}")
    public ServiceResult setInput(@PathVariable("id") final int deviceId, @PathVariable("input") final String input) {
        this.infraRed.setInput(deviceId, input);
        return ServiceResult.success();
    }

    @RequestMapping("devices/{id}/volume/up")
    public ServiceResult volumeUp(@PathVariable("id") final int deviceId) {
        this.infraRed.volumeUp(deviceId);
        return ServiceResult.success();
    }

    @RequestMapping("devices/{id}/volume/down")
    public ServiceResult volumeDown(@PathVariable("id") final int deviceId) {
        this.infraRed.volumeDown(deviceId);
        return ServiceResult.success();
    }

    @RequestMapping("devices/{id}/volume/mute")
    public ServiceResult volumeMute(@PathVariable("id") final int deviceId) {
        this.infraRed.volumeMute(deviceId);
        return ServiceResult.success();
    }

    @RequestMapping("devices/{id}/channel/up")
    public ServiceResult channelUp(@PathVariable("id") final int deviceId) {
        this.infraRed.channelUp(deviceId);
        return ServiceResult.success();
    }

    @RequestMapping("devices/{id}/channel/down")
    public ServiceResult channelDown(@PathVariable("id") final int deviceId) {
        this.infraRed.channelDown(deviceId);
        return ServiceResult.success();
    }

    @RequestMapping("devices/{id}/channel/set/{channel}")
    public ServiceResult setChannel(@PathVariable("id") final int deviceId, @PathVariable("channel") final int channel) {
        this.infraRed.setChannel(deviceId, channel);
        return ServiceResult.success();
    }

    @RequestMapping("devices/{id}/play/play")
    public ServiceResult play(@PathVariable("id") final int deviceId) {
        this.infraRed.play(deviceId);
        return ServiceResult.success();
    }

    @RequestMapping("devices/{id}/play/pause")
    public ServiceResult pause(@PathVariable("id") final int deviceId) {
        this.infraRed.pause(deviceId);
        return ServiceResult.success();
    }

    @RequestMapping("devices/{id}/play/stop")
    public ServiceResult stop(@PathVariable("id") final int deviceId) {
        this.infraRed.stop(deviceId);
        return ServiceResult.success();
    }

    @RequestMapping("devices/{id}/play/fastForward")
    public ServiceResult fastForward(@PathVariable("id") final int deviceId) {
        this.infraRed.fastForward(deviceId);
        return ServiceResult.success();
    }

    @RequestMapping("devices/{id}/play/rewind")
    public ServiceResult rewind(@PathVariable("id") final int deviceId) {
        this.infraRed.rewind(deviceId);
        return ServiceResult.success();
    }

    @RequestMapping("devices/{id}/skip/next")
    public ServiceResult skipNext(@PathVariable("id") final int deviceId) {
        this.infraRed.skipNext(deviceId);
        return ServiceResult.success();
    }

    @RequestMapping("devices/{id}/skip/previous")
    public ServiceResult skipPrevious(@PathVariable("id") final int deviceId) {
        this.infraRed.skipPrevious(deviceId);
        return ServiceResult.success();
    }

    @RequestMapping("devices/{id}/record")
    public ServiceResult record(@PathVariable("id") final int deviceId) {
        this.infraRed.record(deviceId);
        return ServiceResult.success();
    }

    @RequestMapping("devices/{id}/menu/toggle")
    public ServiceResult menuToggle(@PathVariable("id") final int deviceId) {
        this.infraRed.menuToggle(deviceId);
        return ServiceResult.success();
    }

    @RequestMapping("devices/{id}/menu/select")
    public ServiceResult menuSelect(@PathVariable("id") final int deviceId) {
        this.infraRed.menuSelect(deviceId);
        return ServiceResult.success();
    }

    @RequestMapping("devices/{id}/menu/back")
    public ServiceResult menuBack(@PathVariable("id") final int deviceId) {
        this.infraRed.menuBack(deviceId);
        return ServiceResult.success();
    }

    @RequestMapping("devices/{id}/menu/up")
    public ServiceResult menuUp(@PathVariable("id") final int deviceId) {
        this.infraRed.menuUp(deviceId);
        return ServiceResult.success();
    }

    @RequestMapping("devices/{id}/menu/down")
    public ServiceResult menuDown(@PathVariable("id") final int deviceId) {
        this.infraRed.menuDown(deviceId);
        return ServiceResult.success();
    }

    @RequestMapping("devices/{id}/menu/left")
    public ServiceResult menuLeft(@PathVariable("id") final int deviceId) {
        this.infraRed.menuLeft(deviceId);
        return ServiceResult.success();
    }

    @RequestMapping("devices/{id}/menu/right")
    public ServiceResult menuRight(@PathVariable("id") final int deviceId) {
        this.infraRed.menuRight(deviceId);
        return ServiceResult.success();
    }

    @RequestMapping("devices/{id}/pressKeyOnRemote/{keyId}")
    public ServiceResult pressKeyOnRemote(@PathVariable("id") final int deviceId, @PathVariable("keyId") final int keyId) {
        this.infraRed.pressKeyOnRemote(deviceId, keyId);
        return ServiceResult.success();
    }

}
