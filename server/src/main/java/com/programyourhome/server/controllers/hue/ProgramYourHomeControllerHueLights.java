package com.programyourhome.server.controllers.hue;

import java.awt.Color;
import java.util.Collection;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.programyourhome.hue.PhilipsHue;
import com.programyourhome.hue.model.Mood;
import com.programyourhome.hue.model.PyhLight;
import com.programyourhome.hue.model.PyhPlug;
import com.programyourhome.server.controllers.AbstractProgramYourHomeController;
import com.programyourhome.server.response.ServiceResult;

@RestController
@RequestMapping("hue/lights")
public class ProgramYourHomeControllerHueLights extends AbstractProgramYourHomeController {

    @Inject
    private PhilipsHue philipsHue;

    @RequestMapping("")
    // TODO: filtering on known plug-lights on server side with config
    public ServiceResult<Collection<PyhLight>> getLights() {
        return this.produce("Lights", () -> this.philipsHue.getLights());
    }

    @RequestMapping("{id}")
    // TODO: filtering on known plug-lights on server side with config
    public ServiceResult<PyhLight> getLight(@PathVariable("id") final int id) {
        return this.produce("Light", () -> this.philipsHue.getLight(id));
    }

    @RequestMapping("plugs")
    // TODO: filtering on known plug-lights on server side with config
    public Collection<PyhPlug> getPLugs() {
        return this.philipsHue.getPlugs();
    }

    @RequestMapping("{id}/on")
    // TODO: filtering on known plug-lights on server side with config
    public ServiceResult<Void> turnOn(@PathVariable("id") final int id) {
        return this.run(() -> this.philipsHue.turnOnLight(id));
    }

    @RequestMapping("{id}/off")
    // TODO: filtering on known plug-lights on server side with config
    public ServiceResult<Void> turnOff(@PathVariable("id") final int id) {
        return this.run(() -> this.philipsHue.turnOffLight(id));
    }

    @RequestMapping("{id}/dim/{dim:[0-9]+}")
    // TODO: filtering on known plug-lights on server side with config
    public ServiceResult<Void> dim(@PathVariable("id") final int id, @PathVariable("dim") final int dimBasisPoints) {
        return this.run(() -> this.philipsHue.dim(id, dimBasisPoints));
    }

    @RequestMapping("{id}/colorRGB/{red:[0-9]+},{green:[0-9]+},{blue:[0-9]+}")
    // TODO: filtering on known plug-lights on server side with config
    public ServiceResult<Void> setColor(@PathVariable("id") final int id,
            @PathVariable("red") final int red, @PathVariable("green") final int green, @PathVariable("blue") final int blue) {
        return this.run(() -> this.philipsHue.setColorRGB(id, new Color(red, green, blue)));
    }

    @RequestMapping("{id}/colorXY/{x:[0-9\\.]},{y:[0-9\\.]}")
    // TODO: filtering on known plug-lights on server side with config
    public ServiceResult<Void> setColorXY(@PathVariable("id") final int id, @PathVariable("x") final float x, @PathVariable("y") final float y) {
        return this.run(() -> this.philipsHue.setColorXY(id, x, y));
    }

    @RequestMapping("{id}/colorHueSaturation/{hue:[0-9]+},{saturation:[0-9]+}")
    // TODO: filtering on known plug-lights on server side with config
    public ServiceResult<Void> setColorHueSaturation(@PathVariable("id") final int id, @PathVariable("hue") final int hueBasisPoints,
            @PathVariable("saturation") final int saturationBasisPoints) {
        return this.run(() -> this.philipsHue.setColorHueSaturation(id, hueBasisPoints, saturationBasisPoints));
    }

    @RequestMapping("{id}/colorTemperature/{temperature:[0-9]+}")
    // TODO: filtering on known plug-lights on server side with config
    public ServiceResult<Void> setColorTemperature(@PathVariable("id") final int id, @PathVariable("temperature") final int temperatureBasisPoints) {
        return this.run(() -> this.philipsHue.setColorTemperature(id, temperatureBasisPoints));
    }

    @RequestMapping("{id}/mood/{moodName}")
    // TODO: filtering on known plug-lights on server side with config
    public ServiceResult<Void> setMood(@PathVariable("id") final int id, @PathVariable("moodName") final String moodName) {
        return this.run(() -> this.philipsHue.setMood(id, Mood.valueOf(moodName.toUpperCase())));
    }

}
