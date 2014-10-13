package nl.ewjmulder.programyourhome.hue;

import org.springframework.stereotype.Component;

@Component
public class PhilipsHueImpl implements PhilipsHue {

    @Override
    public String test() {
        return "Coming from the philips hue impl module!";
    }

}
