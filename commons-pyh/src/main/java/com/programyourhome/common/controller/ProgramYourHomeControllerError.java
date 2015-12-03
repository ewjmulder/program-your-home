package com.programyourhome.common.controller;

import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.programyourhome.common.response.ServiceResult;

@RestController
public class ProgramYourHomeControllerError extends AbstractProgramYourHomeController implements ErrorController {

    // TODO: change HTTP Methods to specific GET, PUT, POST, DELETE according to functionality. (keep simple URL, no put payload)
    // Introduce debug mode: listen on both GET and PUT, allow GET only in debug mode to easily test with browser.

    // TODO: exception handling for parameter parsing.
    // Choice: request map all probably better, so you can give a 'usage' error instead of general 404. (see e.g. dim fraction and color)
    // TODO: Related to the point described above, the number parsing now is locale dependent, so we should take that out of Spring into our own hands anyway.
    // --> never use fractional numbers in URL's!

    /*
     * {"timestamp":1438355537832,"status":400,"error":"Bad Request","exception":"org.springframework.beans.TypeMismatchException","message":
     * "Failed to convert value of type 'java.lang.String' to required type 'int'; nested exception is java.lang.NumberFormatException: For input string: \"lala\""
     * ,"path":"/main/activities/lala"}
     */

    private final ErrorAttributes errorAttributes;

    @Inject
    public ProgramYourHomeControllerError(final ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }

    @RequestMapping("/error")
    public ServiceResult<Void> error(final HttpServletRequest request) {
        // TODO: Use produce ServiceResult wrapper to catch exceptions in this code? :)
        final Map<String, Object> errorAttributes = this.getErrorAttributes(request);
        final String errorMessage;
        final Integer status = (Integer) errorAttributes.get("status");
        final String exception = (String) errorAttributes.get("exception");
        final String message = (String) errorAttributes.get("message");
        // Specific 'not found' message for either a direct 404 or a proxied 404.
        if (status == 404 || (exception.endsWith("HttpClientErrorException") && message.startsWith("404"))) {
            errorMessage = "No mapping found for path: '" + errorAttributes.get("path") + "'.";
        } else {
            if (exception != null) {
                errorMessage = "Exception occured on server: " + exception.substring(exception.lastIndexOf('.') + 1) + ": " + message;
            } else {
                errorMessage = "Error on server: '" + message + "'.";
            }
        }
        return ServiceResult.error(errorMessage);
    }

    private Map<String, Object> getErrorAttributes(final HttpServletRequest request) {
        return this.errorAttributes.getErrorAttributes(new ServletRequestAttributes(request), false);
    }
}
