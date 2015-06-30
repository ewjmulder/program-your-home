package com.programyourhome.server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.programyourhome.pc.PcInstructor;
import com.programyourhome.server.model.ServiceResult;

@RestController
@RequestMapping("pc")
public class ProgramYourHomeControllerPc extends AbstractProgramYourHomeController {

    @Autowired
    private PcInstructor pcInstructor;

    // TODO: keep REST style? which dictates that there could be several mouses? (if so, then also getter for all mouses + mouse of certain id)
    // Sounds nice and all, but this is actually more of an API over HTTP, isn't there anything for that more flexible besides REST? eg Json RPC
    @RequestMapping("mouse/1/moveAbsolute/{x},{y}")
    public ServiceResult getActivity(@PathVariable("x") final int x, @PathVariable("y") final int y) {
        this.pcInstructor.moveMouseAbsolute(x, y);
        return ServiceResult.success();
    }

}
