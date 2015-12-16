package com.programyourhome.server.controllers;

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.programyourhome.common.response.ServiceResultError;

@RestController
@RequestMapping("pc")
public class ProgramYourHomeControllerPcProxy extends AbstractProgramYourHomeServerController {

    // TODO: move this and the API to a sep project that is included by PYH server and sep, new project PCInstructorRunner oid
    // Server implements api with REST proxy to other URL, new project takes current PCInstructorApi
    // (sep project in new app? makes most sense...)

    // TODO: allow for more than one PcInstructor service to be called, since it's now a separate running service.
    // TODO: document, inspiration from: http://stackoverflow.com/questions/14726082/spring-mvc-rest-service-redirect-forward-proxy
    // Split between with and without request body is needed, because otherwise the @RequestBody param will produce an exception
    // if no request body is present.

    @Value("${pcinstructor.enabled}")
    private boolean enabled;

    @Value("${pcinstructor.server.address}")
    private String serverAddress;

    @Value("${pcinstructor.server.port}")
    private int serverPort;

    private final RestTemplate restTemplate;

    public ProgramYourHomeControllerPcProxy() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Proxy a GET or OPTIONS request, so without a request body.
     *
     * @param method
     * @param request
     * @return
     * @throws URISyntaxException
     */
    @RequestMapping(value = "/**", produces = "application/json", method = { RequestMethod.GET, RequestMethod.OPTIONS })
    public String proxyNoRequestBody(final HttpMethod method, final HttpServletRequest request) throws Exception {
        return this.proxy(method, request, null);
    }

    /**
     * Proxy a PUT or POST request, so with a request body.
     *
     * @param body
     * @param method
     * @param request
     * @return
     * @throws URISyntaxException
     */
    @RequestMapping(value = "/**", produces = "application/json", method = { RequestMethod.PUT, RequestMethod.POST })
    public String proxyWithRequestBody(@RequestBody final String body, final HttpMethod method, final HttpServletRequest request) throws Exception {
        return this.proxy(method, request, body);
    }

    private String proxy(final HttpMethod method, final HttpServletRequest request, final String body) throws Exception {
        if (this.enabled) {
            final URI uri = new URI("http", null, this.serverAddress, this.serverPort, request.getRequestURI(), request.getQueryString(), null);
            final HttpHeaders headers = new HttpHeaders();
            if (body != null) {
                // When there is a body, we'll assume it to contain JSON.
                headers.add(HttpHeaders.CONTENT_TYPE, MIME_JSON);
            }
            final HttpEntity<String> entity = new HttpEntity<String>(body, headers);
            final ResponseEntity<String> responseEntity = this.restTemplate.exchange(uri, method, entity, String.class);
            return responseEntity.getBody();
        } else {
            return new ObjectMapper().writeValueAsString(new ServiceResultError<Void>("PC Instructor proxying is not enabled.").result());
        }
    }
}
