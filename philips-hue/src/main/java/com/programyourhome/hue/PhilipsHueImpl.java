package com.programyourhome.hue;

import org.springframework.stereotype.Component;

import com.programyourhome.hue.PhilipsHue;

@Component
public class PhilipsHueImpl implements PhilipsHue {

    @Override
    public String test() {
        return "Coming from the philips hue impl module!";
    }

}
