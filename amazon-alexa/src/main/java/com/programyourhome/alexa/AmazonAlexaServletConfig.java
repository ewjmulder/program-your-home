package com.programyourhome.alexa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazon.speech.speechlet.servlet.SpeechletServlet;

@Configuration
public class AmazonAlexaServletConfig {

	@Autowired
	private AmazonAlexaSpeechlet speechlet;
	
	@Bean
	public ServletRegistrationBean servletRegistrationBean() {
		// Disable signature check on local service. Will not work, because of proxy + useful for local testing.
		System.setProperty("com.amazon.speech.speechlet.servlet.disableRequestSignatureCheck", "true");

		SpeechletServlet servlet = new SpeechletServlet();
		servlet.setSpeechlet(speechlet);
		return new ServletRegistrationBean(servlet, "/alexa/*");
	}

}
