package nl.ewjmulder.programyourhome.server;

import nl.ewjmulder.programyourhome.hue.PhilipsHue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProgramYourHomeController {

    @Autowired
    private PhilipsHue philipsHue;

    @RequestMapping("/test*")
    public Pojo test() {
        final Pojo pojo = new Pojo();
        pojo.setTest(this.philipsHue.test());
        return pojo;
    }

    public class Pojo {
        private String test;

        public String getTest() {
            return this.test;
        }

        public void setTest(final String test) {
            this.test = test;
        }
    }
}
