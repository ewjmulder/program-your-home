package com.programyourhome.server.controllers;

import java.util.Collection;
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
    public Collection<PyhActivity> getActivities() {
        return this.getServerConfig().getActivitiesConfig().getActivities().stream()
                .map(activity -> this.getActivity(activity.getId()))
                .collect(Collectors.toList());
    }

    @RequestMapping("activities/{id}")
    public PyhActivity getActivity(@PathVariable("id") final int id) {
        final Optional<Activity> activity = this.findActivity(id);
        // TODO: see todo below
        if (!activity.isPresent()) {
            // TODO: or throw exception?
            return null;
        } else {
            return this.createPyhActivity(activity.get());
        }
    }

    public PyhActivity createPyhActivity(final Activity activity) {
        final String defaultIcon = this.getServerConfig().getActivitiesConfig().getDefaultIcon();
        return new PyhActivity(activity, this.activityCenter.isActive(activity), "http://" + this.host + ":" + this.port + "/", defaultIcon);
    }

    @RequestMapping("activities/{id}/start")
    public ServiceResult startActivity(@PathVariable("id") final int id) {
        // TODO: generalize! (see below). How about a general 'ensure' for Option<T> with a message if not. But how to get that to the client?
        // I guess I should have a custom Result type with ok or error, just like the in the Scala talks. Then all server/controller methods
        // should return that and have some monadic way of not proceeding when a failure was encountered. Can that even be done in Java?
        // Otherwise just custom or with helper method in common or so.
        // -> yes, use the Optional.map function!
        // Idea: map over service resuls or at least in a similar way and as long as everything ok, kep the success
        // Upon exceptions, safe that error, and skip the rest of the mapped functions. Probably best to break from pure mapping
        // and use functions names like check, run, etc.
        final Optional<Activity> activity = this.findActivity(id);
        if (!activity.isPresent()) {
            return ServiceResult.error("Activity: '" + id + "' not found in config.");
        } else if (this.activityCenter.isActive(activity.get())) {
            return ServiceResult.error("Activity: '" + id + "' is already active.");
        } else {
            final PyhActivity oldValue = this.createPyhActivity(activity.get());
            this.activityCenter.startActivity(activity.get());
            final PyhActivity newValue = this.createPyhActivity(activity.get());
            this.eventPublisher.publishEvent(new ActivityChangedEvent(oldValue, newValue));
            return ServiceResult.success();
        }
    }

    @RequestMapping("activities/{id}/stop")
    public ServiceResult stopActivity(@PathVariable("id") final int id) {
        final Optional<Activity> activity = this.findActivity(id);
        if (!activity.isPresent()) {
            return ServiceResult.error("Activity: '" + id + "' not found in config.");
        } else if (!this.activityCenter.isActive(activity.get())) {
            return ServiceResult.error("Activity: '" + id + "' is not active.");
        } else {
            final PyhActivity oldValue = this.createPyhActivity(activity.get());
            this.activityCenter.stopActivity(activity.get());
            final PyhActivity newValue = this.createPyhActivity(activity.get());
            this.eventPublisher.publishEvent(new ActivityChangedEvent(oldValue, newValue));
            return ServiceResult.success();
        }
    }

    @RequestMapping("activities/{id}/volumeUp")
    public void activityVolumeUp(@PathVariable("id") final int id) {
        final Optional<Activity> activity = this.findActivity(id);
        if (!activity.isPresent()) {
            // TODO: error 'page' -> double, see above.
            throw new IllegalArgumentException("Activity: '" + id + "' not found in config.");
            // TODO: no else, we'll just use the optional way in a smart way.
        } else {
            // TODO: Another step in the cycle: optional usage really wanted here (read online how to)
            final Optional<Integer> volumeDevice = this.getVolumeDeviceId(activity.get());
            if (!volumeDevice.isPresent()) {
                // TODO: error 'page' -> double, see above.
                throw new IllegalArgumentException("Activity: '" + id + "' has no volume device.");
            }
            this.infraRed.volumeUp(volumeDevice.get());
        }
    }

    private Optional<Integer> getVolumeDeviceId(final Activity activity) {
        Optional<Integer> volumeDevice = Optional.empty();
        if (activity.getModules().getInfraRed() != null) {
            volumeDevice = Optional.ofNullable(activity.getModules().getInfraRed().getVolumeControl());
        }
        return volumeDevice;
    }

}
