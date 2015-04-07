package com.programyourhome.voice;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collection;
import javaFlacEncoder.FLACEncoder;
import javaFlacEncoder.FLACStreamOutputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.TargetDataLine;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.programyourhome.voice.config.ConfigUtil;
import com.programyourhome.voice.config.VoiceControlConfigHolder;
import com.programyourhome.voice.model.AnswerResult;
import com.programyourhome.voice.model.AnswerResultImpl;
import com.programyourhome.voice.model.AnswerResultType;
import com.programyourhome.voice.model.googlespeech.GoogleSpeechResponse;

@Component
public class AnswerListener {

    // TODO: speech API does not work with Dutch! Hoe to fix if possible?
    // TODO: fix / retry. Currently (april 7, 2015 it is broken)

    private static final String GOOGLE_SPEECH_API_URL = "https://www.google.com/speech-api/v2/recognize?output=json&lang=%s&client=%s&app=%s&key=%s";
    private static final String ENCODING_UTF8 = "UTF-8";

    private final PyhAudioFormat audioFormat;

    @Autowired
    private VoiceControlConfigHolder configHolder;

    @Value("${googleSpeechApi.client}")
    private String googleSpeechClient;
    @Value("${googleSpeechApi.app}")
    private String googleSpeechApp;
    @Value("${googleSpeechApi.key}")
    private String googleSpeechKey;

    public AnswerListener() {
        // TODO: make configurable?
        this.audioFormat = PyhAudioFormat.getDefault();
    }

    private TargetDataLine openNewLine() throws Exception {
        final AudioFormat format = this.audioFormat.getJavaAudioFormat();
        final TargetDataLine line = AudioSystem.getTargetDataLine(format);
        line.open(format);
        return line;
    }

    public AnswerResult<Boolean> listenForYesNo(final String locale) throws Exception {
        final GoogleSpeechResponse googleSpeechResponse = this.listen(locale);
        final boolean yesFound = this.atLeastOneWordFoundInTranscript(googleSpeechResponse,
                ConfigUtil.getConfirmations(this.configHolder.getConfig(), locale));
        final boolean noFound = this.atLeastOneWordFoundInTranscript(googleSpeechResponse,
                ConfigUtil.getNegations(this.configHolder.getConfig(), locale));

        final AnswerResultImpl<Boolean> answerResult = new AnswerResultImpl<>(googleSpeechResponse.getTranscripts());
        if (yesFound && !noFound) {
            answerResult.setAnswerResultType(AnswerResultType.PROPER);
            answerResult.setAnswer(true);
        } else if (!yesFound && noFound) {
            answerResult.setAnswerResultType(AnswerResultType.PROPER);
            answerResult.setAnswer(false);
        } else {
            if (googleSpeechResponse.getTranscripts().isEmpty()) {
                answerResult.setAnswerResultType(AnswerResultType.NONE);
            } else if (yesFound && noFound) {
                answerResult.setAnswerResultType(AnswerResultType.AMBIGUOUS);
            } else {
                // Something was said and recognized, but it contained neither yes or no.
                answerResult.setAnswerResultType(AnswerResultType.NOT_APPLICABLE);
            }
        }
        return answerResult;
    }

    private boolean atLeastOneWordFoundInTranscript(final GoogleSpeechResponse googleSpeechResponse, final Collection<String> words) {
        return googleSpeechResponse.getTranscripts().stream()
                .flatMap(transcript -> Arrays.stream(StringUtils.split(transcript)))
                .anyMatch(word -> words.contains(word.toLowerCase()));
    }

    private GoogleSpeechResponse parseGoogleResponse(final String googleResponseString) throws IOException, JsonParseException, JsonMappingException {
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

    private GoogleSpeechResponse listen(final String locale) throws Exception {
        final long start = System.currentTimeMillis();
        System.out.println("start: " + start);
        final TargetDataLine line = this.openNewLine();
        line.start();
        final AudioInputStream ais = new AudioInputStream(line);

        final FLACEncoder encoder = new FLACEncoder();
        encoder.setStreamConfiguration(this.audioFormat.getStreamConfiguration());
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        encoder.setOutputStream(new FLACStreamOutputStream(outputStream));
        encoder.openFLACStream();

        // Buffer frequency means the buffer size is filled that many times per second.
        final int bufferFrequency = 5;
        final int bufferSize = this.audioFormat.getSampleRate() / bufferFrequency;
        final BufferedInputStream bis = new BufferedInputStream(ais, bufferSize);
        // TODO: support for 2 byte (16 bit) samples
        // TODO: fail for channels > 1, no use anyway
        final int seconds = 3;
        final int abufferSize = bufferSize;
        final byte[] byteArray = new byte[abufferSize];
        final int[] intArray = new int[byteArray.length];
        // TODO: some startup time in which no peaks will be detected because of 'mic going on' noise...
        final int frameSize = this.audioFormat.getJavaAudioFormat().getFrameSize();
        System.out.println("Speak now: " + (System.currentTimeMillis() - start));
        for (long i = 0; i < ((long) this.audioFormat.getSampleRate() * (this.audioFormat.getSampleSizeInBits() / Byte.SIZE)
                * this.audioFormat.getNumberOfChannels() * seconds) / abufferSize; i++) {
            bis.read(byteArray);
            // converting byteArray to intArray
            for (int j = 0; j < byteArray.length; j++) {
                intArray[j] = byteArray[j];
            }
            final int numberOfInterchannelSamples = abufferSize;
            encoder.addSamples(intArray, numberOfInterchannelSamples);
            encoder.encodeSamples(encoder.fullBlockSamplesAvailableToEncode(), false);
        }
        // TODO: document use of flac encoder: default block size 4096, encodes per block, one time the rest of the bytes at the end.
        encoder.encodeSamples(encoder.samplesAvailableToEncode(), true);
        System.out.println("Done recording, now encoding: " + (System.currentTimeMillis() - start));
        bis.close();
        line.stop();
        line.close();

        final String urlString = String.format(GOOGLE_SPEECH_API_URL,
                locale,
                URLEncoder.encode(this.googleSpeechClient, ENCODING_UTF8),
                URLEncoder.encode(this.googleSpeechApp, ENCODING_UTF8),
                this.googleSpeechKey);

        final byte[] flacBytes = outputStream.toByteArray();
        outputStream.close();

        final Content content = Request.Post(urlString)
                .bodyStream(new ByteArrayInputStream(flacBytes))
                .userAgent(this.googleSpeechClient)
                .setHeader("Content-Type", "audio/x-flac; rate=" + this.audioFormat.getSampleRate() + ";") // needed, including rate
                .execute().returnContent();

        System.out.println("content: " + content);
        return this.parseGoogleResponse(content.asString());
    }
}
