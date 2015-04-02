package com.programyourhome.server.controllers;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.programyourhome.sensors.SunDegreeSensor;

@RestController
@RequestMapping("sensors")
public class ProgramYourHomeControllerSensor extends AbstractProgramYourHomeController {

    @Autowired
    private SunDegreeSensor sunDegreeSensor;

    @RequestMapping("sunDegree")
    public BigDecimal getSunDegree() {
        return this.sunDegreeSensor.getSunDegree();
    }

}
