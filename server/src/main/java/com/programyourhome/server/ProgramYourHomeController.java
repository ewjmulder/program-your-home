package com.programyourhome.server;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.programyourhome.hue.PhilipsHue;
import com.programyourhome.hue.model.Light;
import com.programyourhome.hue.model.Plug;

@RestController
public class ProgramYourHomeController {

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

    @RequestMapping("/hue/lights/{name}/dim/{fraction:.+}")
    // TODO: filtering on known plug-lights on server side with config
    public void dimLight(@PathVariable("name") final String name, @PathVariable("fraction") final double fraction) {
        this.philipsHue.dimLight(name, fraction);
    }

}
