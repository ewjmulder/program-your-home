package com.programyourhome.voice.detection;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import javaFlacEncoder.FLACEncoder;
import javaFlacEncoder.FLACStreamOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.programyourhome.common.functional.RunnableWithException;
import com.programyourhome.voice.format.PyhAudioFormat;
import com.programyourhome.voice.model.googlespeech.GoogleSpeechResponse;

@Component
@Scope("prototype")
public class GoogleSpeechDetector implements SpeechDetector {

    private final Log log = LogFactory.getLog(this.getClass());

    private static final String GOOGLE_SPEECH_API_URL = "https://www.google.com/speech-api/v2/recognize?output=json&lang=%s&client=%s&app=%s&key=%s";
    private static final String ENCODING_UTF8 = "UTF-8";

    @Value("${googleSpeechApi.client}")
    private String googleSpeechClient;
    @Value("${googleSpeechApi.app}")
    private String googleSpeechApp;
    @Value("${googleSpeechApi.key}")
    private String googleSpeechKey;

    private PyhAudioFormat audioFormat;
    private FLACEncoder encoder;
    private ByteArrayOutputStream outputStream;
    private String locale;

    private byte[] flacBytes;
    private List<String> transcripts;

    @Override
    public void setLocale(final String locale) {
        this.locale = locale;
    }

    @Override
    public void audioStreamOpened(final PyhAudioFormat audioFormat) {
        this.runWithExceptionHandling(() -> {
            this.audioFormat = audioFormat;
            this.outputStream = new ByteArrayOutputStream();
            this.encoder = this.openFLACEncoder(this.outputStream);
            this.transcripts = new ArrayList<>();
        });
    }

    @Override
    public void nextFrame(final AudioFrame audioFrame) {
        this.runWithExceptionHandling(() -> {
            // We add samples 1 at a time.
            final int numberOfSamples = 1;
            // TODO: change to only add samples if they are to be included for transcription.
            this.encoder.addSamples(audioFrame.getValuesPerChannel(), numberOfSamples);
            this.encoder.encodeSamples(this.encoder.fullBlockSamplesAvailableToEncode(), false);
        });
    }

    @Override
    public void audioStreamClosed() {
        this.runWithExceptionHandling(() -> {
            this.encoder.encodeSamples(this.encoder.samplesAvailableToEncode(), true);
            this.flacBytes = this.outputStream.toByteArray();
            this.outputStream.close();
        });
    }

    @Override
    public void performSpeechRecognition() {
        this.runWithExceptionHandling(() -> {
            final String urlString = String.format(GOOGLE_SPEECH_API_URL,
                    this.locale,
                    URLEncoder.encode(this.googleSpeechClient, ENCODING_UTF8),
                    URLEncoder.encode(this.googleSpeechApp, ENCODING_UTF8),
                    this.googleSpeechKey);

            // TODO: cut off silence before and after non-silence, so we'll not send too much data to the API
            final Content googleSpeechResponse = Request.Post(urlString)
                    .bodyStream(new ByteArrayInputStream(this.flacBytes))
                    .userAgent(this.googleSpeechClient)
                    // We need to provide the content type, including the sample rate.
                    .setHeader("Content-Type", "audio/x-flac; rate=" + this.audioFormat.getSampleRate().getNumberOfSamplesPerSecond() + ";")
                    .execute().returnContent();

            this.log.debug("Google speech response: " + googleSpeechResponse);
            this.transcripts.addAll(this.parseGoogleResponse(googleSpeechResponse.asString()).getTranscripts());
        });
    }

    @Override
    public boolean isValueDetected() {
        return !this.transcripts.isEmpty();
    }

    @Override
    public List<String> getDetectedValue() {
        return this.transcripts;
    }

    // TODO: document use of flac encoder: default block size 4096, encodes per block, one time the rest of the bytes at the end.
    private FLACEncoder openFLACEncoder(final OutputStream outputStream) throws IOException {
        final FLACEncoder encoder = new FLACEncoder();
        encoder.setStreamConfiguration(this.audioFormat.getStreamConfiguration());
        encoder.setOutputStream(new FLACStreamOutputStream(outputStream));
        encoder.openFLACStream();
        return encoder;
    }

    private GoogleSpeechResponse parseGoogleResponse(final String googleResponseString) throws IOException {
        final String[] googleResponseStringLines = googleResponseString.split("\n");
        final String transcriptsResponse;
        // The response string contains either:
        if (googleResponseStringLines.length == 1) {
            // Just one line, if the transcript is determined with full confidence.
            transcriptsResponse = googleResponseStringLines[0];
        } else {
            // Two lines, if multiple and/or partly confident transcripts are given. In that case the first line contains no information.
            transcriptsResponse = googleResponseStringLines[1];
        }
        final GoogleSpeechResponse googleSpeechResponse = new ObjectMapper().readValue(transcriptsResponse, GoogleSpeechResponse.class);
        return googleSpeechResponse;
    }

    private void runWithExceptionHandling(final RunnableWithException<IOException> runnable) {
        try {
            runnable.run();
        } catch (final IOException e) {
            throw new IllegalStateException("IOException occured in Google speech detector.", e);
        }
    }

}
