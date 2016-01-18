package com.programyourhome.pcinstructor.server.controller;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.programyourhome.common.controller.AbstractProgramYourHomeController;
import com.programyourhome.common.response.ServiceResult;
import com.programyourhome.pcinstructor.PcInstructor;
import com.programyourhome.pcinstructor.model.KeyPress;
import com.programyourhome.pcinstructor.model.MouseClick;
import com.programyourhome.pcinstructor.model.MouseScroll;
import com.programyourhome.pcinstructor.model.PyhDimension;
import com.programyourhome.pcinstructor.model.PyhDistance;
import com.programyourhome.pcinstructor.model.PyhPoint;
import com.programyourhome.pcinstructor.model.Text;

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
    @RequestMapping(value = "screen/resolution", method = RequestMethod.GET)
    public ServiceResult<PyhDimension> getScreenResolution() {
        return this.produce("Screen resolution", () -> this.pcInstructor.getScreenResolution());
    }

    @RequestMapping(value = "mouse/position", method = RequestMethod.GET)
    public ServiceResult<PyhPoint> getMousePosition() {
        return this.produce("Mouse position", () -> this.pcInstructor.getMousePosition());
    }

    @RequestMapping(value = "mouse/position", method = RequestMethod.PUT, consumes = MIME_JSON)
    public ServiceResult<Void> moveMouseAbsolute(@RequestBody final PyhPoint mousePosition) {
        return this.run(() -> this.pcInstructor.moveMouseAbsolute(mousePosition.getX(), mousePosition.getY()));
    }

    @RequestMapping(value = "mouse/position/relative", method = RequestMethod.POST, consumes = MIME_JSON)
    public ServiceResult<Void> moveMouseRelative(@RequestBody final PyhDistance moveDistance) {
        return this.run(() -> this.pcInstructor.moveMouseRelative(moveDistance.getDx(), moveDistance.getDy()));
    }

    @RequestMapping(value = "mouse/click", method = RequestMethod.POST, consumes = MIME_JSON)
    public ServiceResult<Void> clickMouseButton(@RequestBody final MouseClick mouseClick) {
        return this.run(() -> this.pcInstructor.clickMouseButton(mouseClick.getButton()));
    }

    @RequestMapping(value = "mouse/scroll", method = RequestMethod.POST, consumes = MIME_JSON)
    public ServiceResult<Void> scrollMouse(@RequestBody final MouseScroll mouseScroll) {
        return this.run(() -> this.pcInstructor.scrollMouse(mouseScroll));
    }

    @RequestMapping(value = "key/press", method = RequestMethod.POST, consumes = MIME_JSON)
    public ServiceResult<Void> pressKey(@RequestBody final KeyPress keyPress) {
        return this.run(() -> this.pcInstructor.pressKey(keyPress));
    }

    @RequestMapping(value = "key/write", method = RequestMethod.POST, consumes = MIME_JSON)
    public ServiceResult<Void> writeText(@RequestBody final Text text) {
        return this.run(() -> this.pcInstructor.writeText(text.getText()));
    }

}
