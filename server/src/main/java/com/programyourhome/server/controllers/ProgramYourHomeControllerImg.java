package com.programyourhome.server.controllers;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("img")
public class ProgramYourHomeControllerImg extends AbstractProgramYourHomeServerController {

    // The .* in {filename:.*} is needed to receive the full filename, otherwise the file extension is dropped.
    @RequestMapping(value = "icons/{filename:.*}", method = RequestMethod.GET)
    public byte[] getIcon(@PathVariable("filename") final String filename) throws IOException {
        return IOUtils.toByteArray(this.getClass().getResourceAsStream("/com/programyourhome/config/server/icons/" + filename));
    }

}
