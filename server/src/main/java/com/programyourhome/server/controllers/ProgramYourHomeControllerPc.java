package com.programyourhome.server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.programyourhome.pc.PcInstructor;
import com.programyourhome.server.controllers.response.ServiceResult;

@RestController
@RequestMapping("pc")
public class ProgramYourHomeControllerPc extends AbstractProgramYourHomeController {

    @Autowired
    private PcInstructor pcInstructor;

    @RequestMapping("mouse/moveAbsolute/{x},{y}")
    public ServiceResult<Void> moveMouseAbsolute(@PathVariable("x") final int x, @PathVariable("y") final int y) {
        return this.run(() -> this.pcInstructor.moveMouseAbsolute(x, y));
    }

    @RequestMapping("mouse/moveRelative/{dx},{dy}")
    public ServiceResult<Void> moveMouseRelative(@PathVariable("dx") final int dx, @PathVariable("dy") final int dy) {
        return this.run(() -> this.pcInstructor.moveMouseRelative(dx, dy));
    }

    @RequestMapping("mouse/click/left")
    public ServiceResult<Void> clickLeftMouseButton() {
        return this.run(() -> this.pcInstructor.clickLeftMouseButton());
    }

    @RequestMapping("mouse/click/middle")
    public ServiceResult<Void> clickMiddleMouseButton() {
        return this.run(() -> this.pcInstructor.clickMiddleMouseButton());
    }

    @RequestMapping("mouse/click/right")
    public ServiceResult<Void> clickRightMouseButton() {
        return this.run(() -> this.pcInstructor.clickRightMouseButton());
    }

}
