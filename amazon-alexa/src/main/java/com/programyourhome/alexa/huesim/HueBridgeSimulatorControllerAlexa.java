package com.programyourhome.alexa.huesim;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.programyourhome.huebridgesimulator.model.menu.MenuItem;
import com.programyourhome.huebridgesimulator.model.menu.SimColor;
import com.programyourhome.server.activities.ActivityCenter;
import com.programyourhome.server.config.model.Activity;
import com.programyourhome.server.controllers.AbstractProgramYourHomeServerController;

@RestController
@RequestMapping("huebridgesimulator/alexa")
public class HueBridgeSimulatorControllerAlexa extends AbstractProgramYourHomeServerController {

    @Inject
    private ActivityCenter activityCenter;

    @RequestMapping(value = "currentMenu", method = RequestMethod.GET)
    public MenuItem[] getCurrentMenu() {

        Map<String, String> tempMap = new HashMap<>();
        tempMap.put("Laptop on TV", "Laptop");
        tempMap.put("Watch TV", "Television");
        tempMap.put("Wii", "Nintendo");

        return this.getServerConfig().getActivitiesConfig().getActivities().stream()
                .map(activity -> new MenuItem(tempMap.get(activity.getName()), this.activityCenter.isActive(activity), new SimColor(Color.GREEN)))
                .collect(Collectors.toList())
                .toArray(new MenuItem[0]);
    }

    @RequestMapping(value = "menuItemClicked/{name}/{on}", method = RequestMethod.PUT)
    public void menuItemClicked(@PathVariable("name") final String activityName, @PathVariable("on") final boolean on) {

        Map<String, String> tempMap = new HashMap<>();
        tempMap.put("Laptop", "Laptop on TV");
        tempMap.put("Television", "Watch TV");
        tempMap.put("Nintendo", "Wii");

        final Optional<Activity> activity = this.activityCenter.findActivity(tempMap.get(activityName));
        if (activity.isPresent()) {
            if (on) {
                this.activityCenter.startActivity(activity.get());
            } else {
                this.activityCenter.stopActivity(activity.get());
            }
        } else {
            // TODO: How to process an error here?
            throw new IllegalArgumentException("Activity: '" + activityName + "' does not exist.");
        }
    }

}
