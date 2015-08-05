package com.programyourhome.server.controllers.ir;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.programyourhome.ir.InfraRed;
import com.programyourhome.ir.model.PyhDevice;
import com.programyourhome.server.controllers.AbstractProgramYourHomeController;
import com.programyourhome.server.response.ServiceResult;

@RestController
@RequestMapping("ir/devices")
public class ProgramYourHomeControllerIrDevices extends AbstractProgramYourHomeController {

    @Autowired
    private InfraRed infraRed;

    @RequestMapping("")
    public ServiceResult<Collection<PyhDevice>> getDevices() {
        return this.produce("Devices", () -> this.infraRed.getDevices());
    }

    @RequestMapping("{id}")
    public ServiceResult<PyhDevice> getDevice(@PathVariable("id") final int deviceId) {
        return this.produce("Device", () -> this.infraRed.getDevice(deviceId));
    }

    @RequestMapping("{id}/power/on")
    public ServiceResult<Void> turnOn(@PathVariable("id") final int deviceId) {
        return this.run(() -> this.infraRed.turnOn(deviceId));
    }

    @RequestMapping("{id}/power/off")
    public ServiceResult<Void> turnOff(@PathVariable("id") final int deviceId) {
        return this.run(() -> this.infraRed.turnOff(deviceId));
    }

    @RequestMapping("{id}/input/{input}")
    public ServiceResult<Void> setInput(@PathVariable("id") final int deviceId, @PathVariable("input") final String input) {
        return this.run(() -> this.infraRed.setInput(deviceId, input));
    }

    @RequestMapping("{id}/volume/up")
    public ServiceResult<Void> volumeUp(@PathVariable("id") final int deviceId) {
        return this.run(() -> this.infraRed.volumeUp(deviceId));
    }

    @RequestMapping("{id}/volume/down")
    public ServiceResult<Void> volumeDown(@PathVariable("id") final int deviceId) {
        return this.run(() -> this.infraRed.volumeDown(deviceId));
    }

    @RequestMapping("{id}/volume/mute")
    public ServiceResult<Void> volumeMute(@PathVariable("id") final int deviceId) {
        return this.run(() -> this.infraRed.volumeMute(deviceId));
    }

    @RequestMapping("{id}/channel/up")
    public ServiceResult<Void> channelUp(@PathVariable("id") final int deviceId) {
        return this.run(() -> this.infraRed.channelUp(deviceId));
    }

    @RequestMapping("{id}/channel/down")
    public ServiceResult<Void> channelDown(@PathVariable("id") final int deviceId) {
        return this.run(() -> this.infraRed.channelDown(deviceId));
    }

    @RequestMapping("{id}/channel/set/{channel}")
    public ServiceResult<Void> setChannel(@PathVariable("id") final int deviceId, @PathVariable("channel") final int channel) {
        return this.run(() -> this.infraRed.setChannel(deviceId, channel));
    }

    @RequestMapping("{id}/play/play")
    public ServiceResult<Void> play(@PathVariable("id") final int deviceId) {
        return this.run(() -> this.infraRed.play(deviceId));
    }

    @RequestMapping("{id}/play/pause")
    public ServiceResult<Void> pause(@PathVariable("id") final int deviceId) {
        return this.run(() -> this.infraRed.pause(deviceId));
    }

    @RequestMapping("{id}/play/stop")
    public ServiceResult<Void> stop(@PathVariable("id") final int deviceId) {
        return this.run(() -> this.infraRed.stop(deviceId));
    }

    @RequestMapping("{id}/play/fastForward")
    public ServiceResult<Void> fastForward(@PathVariable("id") final int deviceId) {
        return this.run(() -> this.infraRed.fastForward(deviceId));
    }

    @RequestMapping("{id}/play/rewind")
    public ServiceResult<Void> rewind(@PathVariable("id") final int deviceId) {
        return this.run(() -> this.infraRed.rewind(deviceId));
    }

    @RequestMapping("{id}/skip/next")
    public ServiceResult<Void> skipNext(@PathVariable("id") final int deviceId) {
        return this.run(() -> this.infraRed.skipNext(deviceId));
    }

    @RequestMapping("{id}/skip/previous")
    public ServiceResult<Void> skipPrevious(@PathVariable("id") final int deviceId) {
        return this.run(() -> this.infraRed.skipPrevious(deviceId));
    }

    @RequestMapping("{id}/record")
    public ServiceResult<Void> record(@PathVariable("id") final int deviceId) {
        return this.run(() -> this.infraRed.record(deviceId));
    }

    @RequestMapping("{id}/menu/toggle")
    public ServiceResult<Void> menuToggle(@PathVariable("id") final int deviceId) {
        return this.run(() -> this.infraRed.menuToggle(deviceId));
    }

    @RequestMapping("{id}/menu/select")
    public ServiceResult<Void> menuSelect(@PathVariable("id") final int deviceId) {
        return this.run(() -> this.infraRed.menuSelect(deviceId));
    }

    @RequestMapping("{id}/menu/back")
    public ServiceResult<Void> menuBack(@PathVariable("id") final int deviceId) {
        return this.run(() -> this.infraRed.menuBack(deviceId));
    }

    @RequestMapping("{id}/menu/up")
    public ServiceResult<Void> menuUp(@PathVariable("id") final int deviceId) {
        return this.run(() -> this.infraRed.menuUp(deviceId));
    }

    @RequestMapping("{id}/menu/down")
    public ServiceResult<Void> menuDown(@PathVariable("id") final int deviceId) {
        return this.run(() -> this.infraRed.menuDown(deviceId));
    }

    @RequestMapping("{id}/menu/left")
    public ServiceResult<Void> menuLeft(@PathVariable("id") final int deviceId) {
        return this.run(() -> this.infraRed.menuLeft(deviceId));
    }

    @RequestMapping("{id}/menu/right")
    public ServiceResult<Void> menuRight(@PathVariable("id") final int deviceId) {
        return this.run(() -> this.infraRed.menuRight(deviceId));
    }

    @RequestMapping("{id}/pressKeyOnRemote/{keyId}")
    public ServiceResult<Void> pressKeyOnRemote(@PathVariable("id") final int deviceId, @PathVariable("keyId") final int keyId) {
        return this.run(() -> this.infraRed.pressKeyOnRemote(deviceId, keyId));
    }

}
