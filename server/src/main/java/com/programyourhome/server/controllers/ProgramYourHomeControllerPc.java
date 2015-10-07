package com.programyourhome.server.controllers;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.programyourhome.pc.PcInstructor;
import com.programyourhome.pc.model.PyhDimension;
import com.programyourhome.pc.model.PyhPoint;
import com.programyourhome.server.response.ServiceResult;

@RestController
@RequestMapping("pc")
public class ProgramYourHomeControllerPc extends AbstractProgramYourHomeController {

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
