package com.programyourhome.pcinstructor.server.controller;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.programyourhome.common.controller.AbstractProgramYourHomeController;
import com.programyourhome.common.response.ServiceResult;
import com.programyourhome.pcinstructor.PcInstructor;
import com.programyourhome.pcinstructor.model.PyhDimension;
import com.programyourhome.pcinstructor.model.PyhPoint;

@RestController
@RequestMapping("pc")
public class ProgramYourHomeControllerPc extends AbstractProgramYourHomeController {

    // TODO: move this and the API to a sep project that is included by PYH server and sep, new project PCInstructorRunner oid
    // Server implements api with REST proxy to other URL, new project takes current PCInstructorApi
    // (sep project in new app? makes most sense...)

    // TODO: allow for more than one PcInstructor service to be called, since it's now a separate running service.

    @Inject
    private PcInstructor pcInstructor;

    // TODO: split into sep controllers for screen / mouse?
    @RequestMapping("screen/resolution")
    public ServiceResult<PyhDimension> getScreenResolution() {
        return this.produce("Screen resolution", () -> this.pcInstructor.getScreenResolution());
    }

    @RequestMapping("mouse/position")
    public ServiceResult<PyhPoint> getMousePosition() {
        return this.produce("Mouse position", () -> this.pcInstructor.getMousePosition());
    }

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

    @RequestMapping("mouse/scroll/up/{amount}")
    public ServiceResult<Void> scrollMouseUp(@PathVariable("amount") final int amount) {
        return this.run(() -> this.pcInstructor.scrollMouseUp(amount));
    }

    @RequestMapping("mouse/scroll/down/{amount}")
    public ServiceResult<Void> scrollMouseDown(@PathVariable("amount") final int amount) {
        return this.run(() -> this.pcInstructor.scrollMouseDown(amount));
    }

}
