package com.programyourhome.server.controllers;

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("pc")
public class ProgramYourHomeControllerPcProxy extends AbstractProgramYourHomeServerController {

    // TODO: move this and the API to a sep project that is included by PYH server and sep, new project PCInstructorRunner oid
    // Server implements api with REST proxy to other URL, new project takes current PCInstructorApi
    // (sep project in new app? makes most sense...)

    // TODO: allow for more than one PcInstructor service to be called, since it's now a separate running service.

    private final RestTemplate restTemplate;

    public ProgramYourHomeControllerPcProxy() {
        this.restTemplate = new RestTemplate();
    }

    // http://stackoverflow.com/questions/14726082/spring-mvc-rest-service-redirect-forward-proxy
    @RequestMapping(value = "/**", produces = "application/json", method = { RequestMethod.GET, RequestMethod.OPTIONS })
    public String mirrorRest(final HttpMethod method, final HttpServletRequest request,
            final HttpServletResponse response) throws URISyntaxException {
        // FIXME: make configurable!
        final URI uri = new URI("http", null, "localhost", 31412, request.getRequestURI(), request.getQueryString(), null);

        final ResponseEntity<String> responseEntity =
                this.restTemplate.exchange(uri, method, null/* new HttpEntity<String>(body) */, String.class);

        return responseEntity.getBody();
    }

    // http://stackoverflow.com/questions/14726082/spring-mvc-rest-service-redirect-forward-proxy
    @RequestMapping(value = "/**", produces = "application/json", method = { RequestMethod.PUT, RequestMethod.POST })
    public String mirrorRest(@RequestBody final String body, final HttpMethod method, final HttpServletRequest request,
            final HttpServletResponse response) throws URISyntaxException {
        // FIXME: make configurable!
        final URI uri = new URI("http", null, "localhost", 31412, request.getRequestURI(), request.getQueryString(), null);

        final ResponseEntity<String> responseEntity =
                this.restTemplate.exchange(uri, method, new HttpEntity<String>(body), String.class);

        return responseEntity.getBody();
    }
}
