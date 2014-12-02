package com.programyourhome.server.controller;

import java.awt.Color;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.programyourhome.hue.PhilipsHue;
import com.programyourhome.hue.model.Light;
import com.programyourhome.hue.model.Mood;
import com.programyourhome.hue.model.Plug;

@RestController
@RequestMapping("hue")
public class ProgramYourHomeControllerHue extends AbstractProgramYourHomeController {

    @Autowired
    private PhilipsHue philipsHue;

    @RequestMapping("lights")
    // TODO: filtering on known plug-lights on server side with config
    public Collection<Light> getLights() {
        return this.philipsHue.getLights();
    }

    @RequestMapping("plugs")
    // TODO: filtering on known plug-lights on server side with config
    public Collection<Plug> getPLugs() {
        return this.philipsHue.getPlugs();
    }

    @RequestMapping("lights/{name}/on")
    // TODO: filtering on known plug-lights on server side with config
    public void turnOnLight(@PathVariable("name") final String name) {
        this.philipsHue.turnOnLight(name);
    }

    @RequestMapping("lights/{name}/off")
    // TODO: filtering on known plug-lights on server side with config
    public void turnOffLight(@PathVariable("name") final String name) {
        this.philipsHue.turnOffLight(name);
    }

    @RequestMapping("lights/{name}/dim/{dim:[0-9]+}")
    // TODO: filtering on known plug-lights on server side with config
    public void dimLight(@PathVariable("name") final String name, @PathVariable("dim") final int dimBasisPoints) {
        this.philipsHue.dim(name, dimBasisPoints);
    }

    @RequestMapping("lights/{name}/colorRGB/{red:[0-9]+},{green:[0-9]+},{blue:[0-9]+}")
    // TODO: filtering on known plug-lights on server side with config
    public void setColor(@PathVariable("name") final String name,
            @PathVariable("red") final int red, @PathVariable("green") final int green, @PathVariable("blue") final int blue) {
        this.philipsHue.setColorRGB(name, new Color(red, green, blue));
    }

    @RequestMapping("lights/{name}/colorXY/{x:[0-9\\.]},{y:[0-9\\.]}")
    // TODO: filtering on known plug-lights on server side with config
    public void setColorXY(@PathVariable("name") final String name, @PathVariable("x") final float x, @PathVariable("y") final float y) {
        this.philipsHue.setColorXY(name, x, y);
    }

    @RequestMapping("lights/{name}/colorHueSaturation/{hue:[0-9]+},{saturation:[0-9]+}")
    // TODO: filtering on known plug-lights on server side with config
    public void setColorHueSaturation(@PathVariable("name") final String name, @PathVariable("hue") final int hueBasisPoints,
            @PathVariable("saturation") final int saturationBasisPoints) {
        this.philipsHue.setColorHueSaturation(name, hueBasisPoints, saturationBasisPoints);
    }

    @RequestMapping("lights/{name}/colorTemperature/{temperature:[0-9]+}")
    // TODO: filtering on known plug-lights on server side with config
    public void setColorTemperature(@PathVariable("name") final String name, @PathVariable("temperature") final int temperatureBasisPoints) {
        this.philipsHue.setColorTemperature(name, temperatureBasisPoints);
    }

    @RequestMapping("lights/{name}/mood/{moodName}")
    // TODO: filtering on known plug-lights on server side with config
    public void setMood(@PathVariable("name") final String name, @PathVariable("moodName") final String moodName) {
        this.philipsHue.setMood(name, Mood.valueOf(moodName.toUpperCase()));
    }

}
