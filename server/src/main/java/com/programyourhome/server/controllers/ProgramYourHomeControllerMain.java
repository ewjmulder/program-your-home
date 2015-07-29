package com.programyourhome.server.controllers;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.programyourhome.common.functional.FailableConsumer;
import com.programyourhome.hue.PhilipsHue;
import com.programyourhome.ir.InfraRed;
import com.programyourhome.server.activities.ActivityCenter;
import com.programyourhome.server.activities.model.PyhActivityImpl;
import com.programyourhome.server.config.model.Activity;
import com.programyourhome.server.config.model.Activity.Modules;
import com.programyourhome.server.config.model.InfraRedActivityConfig;
import com.programyourhome.server.controllers.response.ServiceResult;
import com.programyourhome.server.events.activities.ActivityChangedEvent;

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

    @RequestMapping("activities")
    public ServiceResult<Collection<PyhActivityImpl>> getActivities() {
        return this.produce("Activities", () -> this.getServerConfig().getActivitiesConfig().getActivities().stream()
                .map(this::createPyhActivity)
                .collect(Collectors.toList()));
    }

    @RequestMapping("activities/{id}")
    public ServiceResult<PyhActivityImpl> getActivity(@PathVariable("id") final int id) {
        return this.find("Activity", id, this.activityCenter::findActivity)
                .produce(this::createPyhActivity);
    }

    public Optional<PyhActivityImpl> createPyhActivity(final int id) {
        return this.activityCenter.findActivity(id).map(this::createPyhActivity);
    }

    public PyhActivityImpl createPyhActivity(final Activity activity) {
        final String defaultIcon = this.getServerConfig().getActivitiesConfig().getDefaultIcon();
        return new PyhActivityImpl(activity, this.activityCenter.isActive(activity), "http://" + this.host + ":" + this.port + "/", defaultIcon);
    }

    @RequestMapping("activities/{id}/start")
    public ServiceResult<Void> startActivity(@PathVariable("id") final int id) {
        return this.find("Activity", id, this.activityCenter::findActivity)
                .ensure(this.activityCenter::isNotActive, "Activity is already active")
                .process(this::toggleActivity);
    }

    @RequestMapping("activities/{id}/stop")
    public ServiceResult<Void> stopActivity(@PathVariable("id") final int id) {
        return this.find("Activity", id, this.activityCenter::findActivity)
                .ensure(this.activityCenter::isActive, "Activity is not active")
                .process(this::toggleActivity);
    }

    private void toggleActivity(final Activity activity) {
        final PyhActivityImpl oldValue = this.createPyhActivity(activity);
        if (oldValue.isActive()) {
            this.activityCenter.stopActivity(activity);
        } else {
            this.activityCenter.startActivity(activity);
        }
        final PyhActivityImpl newValue = this.createPyhActivity(activity);
        this.eventPublisher.publishEvent(new ActivityChangedEvent(oldValue, newValue));
    }

    @RequestMapping("activities/{id}/volume/up")
    public ServiceResult<Void> activityVolumeUp(@PathVariable("id") final int id) {
        return this.activityVolumeAction(id, this.infraRed::volumeUp);
    }

    @RequestMapping("activities/{id}/volume/down")
    public ServiceResult<Void> activityVolumeDown(@PathVariable("id") final int id) {
        return this.activityVolumeAction(id, this.infraRed::volumeDown);
    }

    @RequestMapping("activities/{id}/volume/mute")
    public ServiceResult<Void> activityVolumeMute(@PathVariable("id") final int id) {
        return this.activityVolumeAction(id, this.infraRed::volumeMute);
    }

    @RequestMapping("activities/{id}/channel/up")
    public ServiceResult<Void> activityChannelUp(@PathVariable("id") final int id) {
        return this.activityChannelAction(id, this.infraRed::channelUp);
    }

    @RequestMapping("activities/{id}/channel/down")
    public ServiceResult<Void> activityChannelDown(@PathVariable("id") final int id) {
        return this.activityChannelAction(id, this.infraRed::channelDown);
    }

    @RequestMapping("activities/{id}/channel/set/{channel}")
    public ServiceResult<Void> activitySetChannel(@PathVariable("id") final int id, @PathVariable("channel") final int channel) {
        return this.activityChannelAction(id, deviceId -> this.infraRed.setChannel(deviceId, channel));
    }

    @RequestMapping("activities/{id}/play/play")
    public ServiceResult<Void> activityPlay(@PathVariable("id") final int id) {
        return this.activityPlayAction(id, this.infraRed::play);
    }

    @RequestMapping("activities/{id}/play/pause")
    public ServiceResult<Void> activityPause(@PathVariable("id") final int id) {
        return this.activityPlayAction(id, this.infraRed::pause);
    }

    @RequestMapping("activities/{id}/play/stop")
    public ServiceResult<Void> activityStop(@PathVariable("id") final int id) {
        return this.activityPlayAction(id, this.infraRed::stop);
    }

    @RequestMapping("activities/{id}/play/fastForward")
    public ServiceResult<Void> activityFastForward(@PathVariable("id") final int id) {
        return this.activityPlayAction(id, this.infraRed::fastForward);
    }

    @RequestMapping("activities/{id}/play/rewind")
    public ServiceResult<Void> activityRewind(@PathVariable("id") final int id) {
        return this.activityPlayAction(id, this.infraRed::rewind);
    }

    @RequestMapping("activities/{id}/skip/next")
    public ServiceResult<Void> activitySkipNext(@PathVariable("id") final int id) {
        return this.activitySkipAction(id, this.infraRed::skipNext);
    }

    @RequestMapping("activities/{id}/skip/previous")
    public ServiceResult<Void> activitySkipPrevious(@PathVariable("id") final int id) {
        return this.activitySkipAction(id, this.infraRed::skipPrevious);
    }

    @RequestMapping("activities/{id}/record")
    public ServiceResult<Void> activityRecord(@PathVariable("id") final int id) {
        return this.activityRecordAction(id, this.infraRed::record);
    }

    @RequestMapping("activities/{id}/menu/toggle")
    public ServiceResult<Void> activityMenuToggle(@PathVariable("id") final int id) {
        return this.activityMenuAction(id, this.infraRed::menuToggle);
    }

    @RequestMapping("activities/{id}/menu/select")
    public ServiceResult<Void> activityMenuSelect(@PathVariable("id") final int id) {
        return this.activityMenuAction(id, this.infraRed::menuSelect);
    }

    @RequestMapping("activities/{id}/menu/back")
    public ServiceResult<Void> activityMenuBack(@PathVariable("id") final int id) {
        return this.activityMenuAction(id, this.infraRed::menuBack);
    }

    @RequestMapping("activities/{id}/menu/up")
    public ServiceResult<Void> activityMenuUp(@PathVariable("id") final int id) {
        return this.activityMenuAction(id, this.infraRed::menuUp);
    }

    @RequestMapping("activities/{id}/menu/down")
    public ServiceResult<Void> activityMenuDown(@PathVariable("id") final int id) {
        return this.activityMenuAction(id, this.infraRed::menuDown);
    }

    @RequestMapping("activities/{id}/menu/left")
    public ServiceResult<Void> activityMenuLeft(@PathVariable("id") final int id) {
        return this.activityMenuAction(id, this.infraRed::menuLeft);
    }

    @RequestMapping("activities/{id}/menu/right")
    public ServiceResult<Void> activityMenuRight(@PathVariable("id") final int id) {
        return this.activityMenuAction(id, this.infraRed::menuRight);
    }

    private ServiceResult<Void> activityVolumeAction(final int id, final FailableConsumer<Integer> infraRedAction) {
        return this.activityInfraRedAction(id, InfraRedActivityConfig::getVolumeControl, infraRedAction);
    }

    private ServiceResult<Void> activityChannelAction(final int id, final FailableConsumer<Integer> infraRedAction) {
        return this.activityInfraRedAction(id, InfraRedActivityConfig::getChannelControl, infraRedAction);
    }

    private ServiceResult<Void> activityPlayAction(final int id, final FailableConsumer<Integer> infraRedAction) {
        return this.activityInfraRedAction(id, InfraRedActivityConfig::getPlayControl, infraRedAction);
    }

    private ServiceResult<Void> activitySkipAction(final int id, final FailableConsumer<Integer> infraRedAction) {
        return this.activityInfraRedAction(id, InfraRedActivityConfig::getSkipControl, infraRedAction);
    }

    private ServiceResult<Void> activityRecordAction(final int id, final FailableConsumer<Integer> infraRedAction) {
        return this.activityInfraRedAction(id, InfraRedActivityConfig::getRecordControl, infraRedAction);
    }

    private ServiceResult<Void> activityMenuAction(final int id, final FailableConsumer<Integer> infraRedAction) {
        return this.activityInfraRedAction(id, InfraRedActivityConfig::getMenuControl, infraRedAction);
    }

    private ServiceResult<Void> activityInfraRedAction(final int id, final Function<InfraRedActivityConfig, Integer> getDeviceId,
            final FailableConsumer<Integer> infraRedAction) {
        return this.find("Activity", id, this.activityCenter::findActivity)
                .ensure(this.activityCenter::isActive, "Activity not active")
                .map(Activity::getModules, "Modules")
                .map(Modules::getInfraRed, "InfraRed")
                .map(getDeviceId, "Device id")
                .process(infraRedAction);
    }

}
