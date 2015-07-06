package com.programyourhome.server.controllers;

import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.programyourhome.hue.PhilipsHue;
import com.programyourhome.ir.InfraRed;
import com.programyourhome.server.activities.ActivityCenter;
import com.programyourhome.server.activities.model.PyhActivity;
import com.programyourhome.server.config.model.Activity;
import com.programyourhome.server.events.activities.ActivityChangedEvent;
import com.programyourhome.server.model.ServiceResult;

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

    @Autowired
    private InfraRed infraRed;

    @Autowired
    private ActivityCenter activityCenter;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Value("${server.address}")
    private String host;

    @Value("${server.port}")
    private int port;

    // TODO: put activities in separate module?
    @RequestMapping("activities")
    public ServiceResult getActivities() {
        return this.produce("Activities", () -> this.getServerConfig().getActivitiesConfig().getActivities().stream()
                .map(this::createPyhActivity)
                .collect(Collectors.toList()));
    }

    @RequestMapping("activities/{id}")
    public ServiceResult getActivity(@PathVariable("id") final int id) {
        return this.find("Activity", id, this::findActivity)
                .produce(this::createPyhActivity);
    }

    public Optional<PyhActivity> createPyhActivity(final int id) {
        return this.findActivity(id).map(this::createPyhActivity);
    }

    public PyhActivity createPyhActivity(final Activity activity) {
        final String defaultIcon = this.getServerConfig().getActivitiesConfig().getDefaultIcon();
        return new PyhActivity(activity, this.activityCenter.isActive(activity), "http://" + this.host + ":" + this.port + "/", defaultIcon);
    }

    @RequestMapping("activities/{id}/start")
    public ServiceResult startActivity(@PathVariable("id") final int id) {
        return this.find("Activity", id, this::findActivity)
                .ensure(this.activityCenter::isNotActive, "Activity is already active")
                .process(this::toggleActivity);
    }

    @RequestMapping("activities/{id}/stop")
    public ServiceResult stopActivity(@PathVariable("id") final int id) {
        return this.find("Activity", id, this::findActivity)
                .ensure(this.activityCenter::isActive, "Activity is not active")
                .process(this::toggleActivity);
    }

    private void toggleActivity(final Activity activity) {
        final PyhActivity oldValue = this.createPyhActivity(activity);
        if (oldValue.isActive()) {
            this.activityCenter.stopActivity(activity);
        } else {
            this.activityCenter.startActivity(activity);
        }
        final PyhActivity newValue = this.createPyhActivity(activity);
        this.eventPublisher.publishEvent(new ActivityChangedEvent(oldValue, newValue));
    }

    @RequestMapping("activities/{id}/volumeUp")
    public void activityVolumeUp(@PathVariable("id") final int id) {
        this.find("Activity", id, this::findActivity)
        .ensure(this.activityCenter::isActive, "Activity not active")
        .flatMap(this::getVolumeDeviceId, "Device id")
        .process(this.infraRed::volumeUp);
    }

    private Optional<Integer> getVolumeDeviceId(final Activity activity) {
        Optional<Integer> volumeDevice = Optional.empty();
        if (activity.getModules().getInfraRed() != null) {
            volumeDevice = Optional.ofNullable(activity.getModules().getInfraRed().getVolumeControl());
        }
        return volumeDevice;
    }

}
