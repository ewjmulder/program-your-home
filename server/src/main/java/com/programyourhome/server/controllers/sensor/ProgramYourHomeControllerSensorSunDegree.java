package com.programyourhome.server.controllers.sensor;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.programyourhome.sensors.SunDegreeSensor;
import com.programyourhome.server.controllers.AbstractProgramYourHomeController;

@RestController
@RequestMapping("sensors/sunDegree")
public class ProgramYourHomeControllerSensorSunDegree extends AbstractProgramYourHomeController {

    @Autowired
    private SunDegreeSensor sunDegreeSensor;

    @RequestMapping("")
    public BigDecimal getSunDegree() {
        return this.sunDegreeSensor.getSunDegree();
    }

}
