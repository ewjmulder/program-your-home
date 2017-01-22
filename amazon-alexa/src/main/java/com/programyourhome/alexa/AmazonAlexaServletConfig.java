package com.programyourhome.alexa;

import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazon.speech.speechlet.servlet.SpeechletServlet;
import com.programyourhome.server.controllers.AbstractProgramYourHomeServerController;

@Configuration
public class AmazonAlexaServletConfig extends AbstractProgramYourHomeServerController {

	@Bean
	public ServletRegistrationBean servletRegistrationBean(){
	    SpeechletServlet servlet = new SpeechletServlet();
	    servlet.setSpeechlet(new AmazonAlexaSpeechlet());
	    return new ServletRegistrationBean(servlet, "alexa/*");
	}

}
