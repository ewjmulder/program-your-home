package com.programyourhome.alexa;

import java.awt.Color;

import org.springframework.beans.factory.annotation.Autowired;

import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.Speechlet;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.programyourhome.hue.PhilipsHue;

public class AmazonAlexaSpeechlet implements Speechlet {

	@Autowired
	private PhilipsHue philipsHue;
	
	@Override
	public void onSessionStarted(SessionStartedRequest request, Session session) throws SpeechletException {
		// Nothing to do ... so far.
	}

	@Override
	public SpeechletResponse onLaunch(LaunchRequest request, Session session) throws SpeechletException {
		return buildResponse("You can control the home with your voice!");
	}

	@Override
	public SpeechletResponse onIntent(IntentRequest request, Session session) throws SpeechletException {
		philipsHue.setColorRGB(1, Color.RED);
		philipsHue.setColorRGB(2, Color.GREEN);
		philipsHue.setColorRGB(3, Color.BLUE);
		return buildResponse("Ok! Fun light colors!");
	}

	@Override
	public void onSessionEnded(SessionEndedRequest request, Session session) throws SpeechletException {
		// Nothing to do ... so far.
	}

	private static SpeechletResponse buildResponse(String text) {
		final PlainTextOutputSpeech output = new PlainTextOutputSpeech();
		output.setText(text);

		final SpeechletResponse response = new SpeechletResponse();
		response.setOutputSpeech(output);

		return response;
	}

}
