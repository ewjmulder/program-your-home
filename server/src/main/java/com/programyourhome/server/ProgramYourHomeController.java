package com.programyourhome.server;

import java.awt.Color;
import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.programyourhome.activities.model.Activity;
import com.programyourhome.config.ServerConfig;
import com.programyourhome.hue.PhilipsHue;
import com.programyourhome.hue.model.Light;
import com.programyourhome.hue.model.Mood;
import com.programyourhome.hue.model.Plug;
import com.programyourhome.ir.InfraRed;
import com.programyourhome.ir.model.Remote;
import com.programyourhome.server.config.ConfigLoader;

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
    // TODO: Related to the point described above, the number parsing now is locale dependent, so we should take that out of Spring into our own hands anyway.

    @Autowired
    private PhilipsHue philipsHue;

    // @Autowired
    private InfraRed infraRed;

    @Autowired
    private ConfigLoader configLoader;
    private ServerConfig serverConfig;

    @PostConstruct
    private void init() {
        this.serverConfig = this.configLoader.loadConfig();
    }

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

    @RequestMapping("/hue/lights/{name}/dim/{dim:[0-9\\.]+}")
    // TODO: filtering on known plug-lights on server side with config
    public void dimLight(@PathVariable("name") final String name, @PathVariable("dim") final double dimFraction) {
        this.philipsHue.dim(name, dimFraction);
    }

    @RequestMapping("/hue/lights/{name}/colorRGB/{red:[0-9]+},{green:[0-9]+},{blue:[0-9]+}")
    // TODO: filtering on known plug-lights on server side with config
    public void setColor(@PathVariable("name") final String name,
            @PathVariable("red") final int red, @PathVariable("green") final int green, @PathVariable("blue") final int blue) {
        this.philipsHue.setColorRGB(name, new Color(red, green, blue));
    }

    @RequestMapping("/hue/lights/{name}/colorXY/{x:[0-9\\.]},{y:[0-9\\.]}")
    // TODO: filtering on known plug-lights on server side with config
    public void setColorXY(@PathVariable("name") final String name, @PathVariable("x") final float x, @PathVariable("y") final float y) {
        this.philipsHue.setColorXY(name, x, y);
    }

    @RequestMapping("/hue/lights/{name}/colorHueSaturation/{hue:[0-9\\.]},{saturation:[0-9\\.]}")
    // TODO: filtering on known plug-lights on server side with config
    public void setColorHueSaturation(@PathVariable("name") final String name, @PathVariable("hue") final double hueFraction,
            @PathVariable("saturation") final double saturationFraction) {
        this.philipsHue.setColorHueSaturation(name, hueFraction, saturationFraction);
    }

    @RequestMapping("/hue/lights/{name}/colorTemperature/{temperature:[0-9\\.]+}")
    // TODO: filtering on known plug-lights on server side with config
    public void setColorTemperature(@PathVariable("name") final String name, @PathVariable("temperature") final double temperatureFraction) {
        this.philipsHue.setColorTemperature(name, temperatureFraction);
    }

    @RequestMapping("/hue/lights/{name}/mood/{moodName}")
    // TODO: filtering on known plug-lights on server side with config
    public void setMood(@PathVariable("name") final String name, @PathVariable("moodName") final String moodName) {
        this.philipsHue.setMood(name, Mood.valueOf(moodName.toUpperCase()));
    }

    // TODO: ASCII ART SEPERATION OF STUFF
    // TODO: ASCII ART SEPERATION OF STUFF
    // TODO: ASCII ART SEPERATION OF STUFF
    // or have some way to split this Class? It'll become really big!

    @RequestMapping("/ir/remotes")
    public Collection<Remote> getRemotes() {
        // TODO: update device list on other laptop
        return this.infraRed.getRemotes();
    }

    @RequestMapping("/ir/remotes/{name}/{key}")
    public void pressRemoteKey(@PathVariable("name") final String name, @PathVariable("key") final String key) {
        this.infraRed.pressRemoteKey(name, key);
    }

    // TODO: ASCII ART SEPERATION OF STUFF
    // TODO: ASCII ART SEPERATION OF STUFF
    // TODO: ASCII ART SEPERATION OF STUFF
    // or have some way to split this Class? It'll become really big!
    // TODO: Result values for action method calls with info about success / error / message / overrides / etc

    // TODO: put activities in seperate module?
    @RequestMapping("/main/activities")
    public Collection<Activity> getActivities() {
        return this.serverConfig.getActivities().stream()
                .map(activity -> new Activity(activity.getName(), activity.getDescription(), "http://192.168.2.28:3737/img/icons/" + activity.getIcon()))
                .collect(Collectors.toList());
    }

    @RequestMapping("/main/activities/{name}/start")
    public void startActivity(@PathVariable("name") final String name) {
        // TODO: Get from some kind of config / script
        // TODO: Stop activities that conflict
        if (name.equals("Watch TV")) {
            this.infraRed.pressRemoteKey("SAMSUNG-BN59-00685A", "POWER");
            this.sleep(200);
            this.infraRed.pressRemoteKey("YAMAHA-RAV338-MAIN-ZONE", "POWER");
            this.sleep(200);
            this.infraRed.pressRemoteKey("MOTOROLA-VIP1853", "POWER");
            this.sleep(4000);
            this.infraRed.pressRemoteKey("YAMAHA-RAV338-MAIN-ZONE", "INPUT_HDMI1");
        }
    }

    @RequestMapping("/main/activities/{name}/stop")
    public void stopActivity(@PathVariable("name") final String name) {
        // TODO: Get from some kind of config / script
        // TODO: is that activity actually running?
        if (name.equals("Watch TV")) {
            this.infraRed.pressRemoteKey("SAMSUNG-BN59-00685A", "POWER");
            this.sleep(200);
            this.infraRed.pressRemoteKey("YAMAHA-RAV338-MAIN-ZONE", "POWER");
            this.sleep(200);
            this.infraRed.pressRemoteKey("MOTOROLA-VIP1853", "POWER");
        }
    }

    // TODO: ASCII ART SEPERATION OF STUFF
    // TODO: ASCII ART SEPERATION OF STUFF
    // TODO: ASCII ART SEPERATION OF STUFF
    // or have some way to split this Class? It'll become really big!

    @RequestMapping(value = "/img/icons/{filename:.*}", method = RequestMethod.GET)
    public byte[] getIcon(@PathVariable("filename") final String filename) throws IOException {
        return IOUtils.toByteArray(this.getClass().getResourceAsStream("/com/programyourhome/config/icons/" + filename));
    }

    private void sleep(final int millis) {
        try {
            Thread.sleep(millis);
        } catch (final InterruptedException e) {
        }
    }

}
