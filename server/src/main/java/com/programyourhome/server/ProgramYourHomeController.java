package com.programyourhome.server;

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
public class ProgramYourHomeController {

    /*
     * 6.3. @RequestMapping – a fallback for all requests
     * To implement a simple fallback for all requests using a specific HTTP method:
     * @RequestMapping(value = "*")
     * @ResponseBody
     * public String getFallback() {
     * return "Fallback for GET Requests";
     * }
     * Or even for all request:
     * @RequestMapping(value = "*", method = { RequestMethod.GET, RequestMethod.POST ... })
     * @ResponseBody
     * public String allFallback() {
     * return "Fallback for All Requests";
     * }
     */

    // TODO: change HTTP Methods to specific GET, PUT, POST, DELETE according to functionality. (keep simple URL, no put payload)
    // Introduce debug mode: listen on both GET and PUT, allow GET only in debug mode to easily test with browser.

    // TODO: exception handling for parameter parsing.
    // Choice: request map all probably better, so you can give a 'usage' error instead of general 404. (see e.g. dim fraction and color)

    @Autowired
    private PhilipsHue philipsHue;

    @RequestMapping("/hue/lights")
    // TODO: filtering on known plug-lights on server side with config
    public Collection<Light> getLights() {
        return this.philipsHue.getLights();
    }

    @RequestMapping("/hue/plugs")
    // TODO: filtering on known plug-lights on server side with config
    public Collection<Plug> getPLugs() {
        return this.philipsHue.getPlugs();
    }

    @RequestMapping("/hue/lights/{name}/on")
    // TODO: filtering on known plug-lights on server side with config
    public void turnOnLight(@PathVariable("name") final String name) {
        this.philipsHue.turnOnLight(name);
    }

    @RequestMapping("/hue/lights/{name}/off")
    // TODO: filtering on known plug-lights on server side with config
    public void turnOffLight(@PathVariable("name") final String name) {
        this.philipsHue.turnOffLight(name);
    }

    @RequestMapping("/hue/lights/{name}/dim/{fraction:[0-9\\.]+}")
    // TODO: filtering on known plug-lights on server side with config
    public void dimLight(@PathVariable("name") final String name, @PathVariable("fraction") final double fraction) {
        this.philipsHue.dimLight(name, fraction);
    }

    @RequestMapping("/hue/lights/{name}/color/{red:[0-9]+}-{green:[0-9]+}-{blue:[0-9]+}")
    // TODO: filtering on known plug-lights on server side with config
    public void setColor(@PathVariable("name") final String name,
            @PathVariable("red") final int red, @PathVariable("green") final int green, @PathVariable("blue") final int blue) {
        this.philipsHue.setColor(name, new Color(red, green, blue));
    }

    @RequestMapping("/hue/lights/{name}/mood/{moodName}")
    // TODO: filtering on known plug-lights on server side with config
    public void setMood(@PathVariable("name") final String name, @PathVariable("moodName") final String moodName) {
        this.philipsHue.setMood(name, Mood.valueOf(moodName.toUpperCase()));
    }

    @RequestMapping("/hue/lights/{name}/colorTemperature/{mirek}")
    // TODO: filtering on known plug-lights on server side with config
    public void setColorTemperature(@PathVariable("name") final String name, @PathVariable("mirek") final int mirek) {
        this.philipsHue.setColorTemperature(name, mirek);
    }

}
