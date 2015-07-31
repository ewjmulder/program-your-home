package com.programyourhome.server.controllers.hue;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.programyourhome.hue.PhilipsHue;
import com.programyourhome.hue.model.PyhPlug;
import com.programyourhome.server.controllers.AbstractProgramYourHomeController;

@RestController
@RequestMapping("hue/plugs")
public class ProgramYourHomeControllerHuePlugs extends AbstractProgramYourHomeController {

    @Autowired
    private PhilipsHue philipsHue;

    @RequestMapping("")
    // TODO: filtering on known plug-lights on server side with config
    public Collection<PyhPlug> getPLugs() {
        return this.philipsHue.getPlugs();
    }

}
