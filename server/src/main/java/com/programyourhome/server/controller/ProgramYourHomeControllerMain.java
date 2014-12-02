package com.programyourhome.server.controller;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.programyourhome.activities.model.Activity;
import com.programyourhome.hue.PhilipsHue;
import com.programyourhome.ir.InfraRed;

@RestController
@RequestMapping("main")
public class ProgramYourHomeControllerMain extends AbstractProgramYourHomeController {

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

    // TODO: Result values for action method calls with info about success / error / message / overrides / etc

    @Autowired
    private PhilipsHue philipsHue;

    // @Autowired
    private InfraRed infraRed;

    // TODO: put activities in seperate module?
    @RequestMapping("activities")
    public Collection<Activity> getActivities() {
        return this.getServerConfig().getActivities().stream()
                .map(activity -> new Activity(activity.getName(), activity.getDescription(), "http://192.168.2.28:3737/img/icons/" + activity.getIcon()))
                .collect(Collectors.toList());
    }

    @RequestMapping("activities/{name}/start")
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

    @RequestMapping("activities/{name}/stop")
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

    private void sleep(final int millis) {
        try {
            Thread.sleep(millis);
        } catch (final InterruptedException e) {
        }
    }

}
