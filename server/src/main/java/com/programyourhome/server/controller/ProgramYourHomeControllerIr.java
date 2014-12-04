package com.programyourhome.server.controller;

import java.util.Collection;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.programyourhome.ir.InfraRed;
import com.programyourhome.ir.model.PyhRemote;

@RestController
@RequestMapping("ir")
public class ProgramYourHomeControllerIr extends AbstractProgramYourHomeController {

    // @Autowired
    private InfraRed infraRed;

    @RequestMapping("remotes")
    public Collection<PyhRemote> getRemotes() {
        // TODO: update device list on other laptop
        return this.infraRed.getRemotes();
    }

    @RequestMapping("remotes/{name}/{key}")
    public void pressRemoteKey(@PathVariable("name") final String name, @PathVariable("key") final String key) {
        this.infraRed.pressRemoteKey(name, key);
    }

}
