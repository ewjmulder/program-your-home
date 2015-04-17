package com.programyourhome.server.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.programyourhome.server.ProgramYourHomeServer;

@RestController
@RequestMapping("meta")
public class ProgramYourHomeControllerMeta extends AbstractProgramYourHomeController {

    // TODO: more specific health/status check info and other meta service (logs / data / etc)

    /**
     * This provides an easy way the see if the REST service is reachable.
     *
     * @return the text string 'pong'
     */
    @RequestMapping("status/ping")
    public String pingServer() {
        return "pong";
    }

    /**
     * Feature: shutdown the server with a REST request.
     */
    @RequestMapping("server/shutdown")
    public void shutdownServer() {
        ProgramYourHomeServer.stopServer();
    }

}
