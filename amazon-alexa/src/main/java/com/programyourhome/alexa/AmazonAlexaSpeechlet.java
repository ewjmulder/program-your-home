package com.programyourhome.alexa;

import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.Speechlet;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;

public class AmazonAlexaSpeechlet implements Speechlet {

	@Override
	public void onSessionStarted(SessionStartedRequest request, Session session) throws SpeechletException {
		// Nothing to do ... so far.
	}

	@Override
	public SpeechletResponse onLaunch(LaunchRequest request, Session session) throws SpeechletException {
		return buildResponse("Welcome to program you home!");
	}

	@Override
	public SpeechletResponse onIntent(IntentRequest request, Session session) throws SpeechletException {
		return buildResponse("Welcome to program you home!");
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
