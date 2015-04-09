package com.programyourhome.voice;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javaFlacEncoder.FLACEncoder;
import javaFlacEncoder.FLACStreamOutputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.TargetDataLine;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import com.programyourhome.voice.model.ListenResult;
import com.programyourhome.voice.model.ListenResultType;
import com.programyourhome.voice.model.googlespeech.GoogleSpeechResponse;
import com.programyourhome.voice.model.question.Question;

@Component
public class AnswerListener {

    // TODO: speech API does not work with Dutch! Hoe to fix if possible?
    // TODO: fix / retry. Currently (april 7, 2015 it is broken)

    private static final String GOOGLE_SPEECH_API_URL = "https://www.google.com/speech-api/v2/recognize?output=json&lang=%s&client=%s&app=%s&key=%s";
    private static final String ENCODING_UTF8 = "UTF-8";

    private final Log log = LogFactory.getLog(this.getClass());

    private final PyhAudioFormat audioFormat;

    @Autowired
    private VoiceControlConfigHolder configHolder;

    @Value("${googleSpeechApi.client}")
    private String googleSpeechClient;
    @Value("${googleSpeechApi.app}")
    private String googleSpeechApp;
    @Value("${googleSpeechApi.key}")
    private String googleSpeechKey;

    @Autowired
    private AudioPlayer audioPlayer;

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

    // TODO: refactor whole listenfor logic: extract common functionality and split up in smaller methods.
    // Callback for handling specific listen results and handling definitive answer result.

    public AnswerResult<Integer> listenForClaps(final Question<Integer> question) throws Exception {
        final ListenResult listenResult = this.listen(question);
        final List<Character> answersGiven = new ArrayList<>();
        final AnswerResultImpl<Character> answerResult = new AnswerResultImpl<>(listenResult.getResultType());

        if (listenResult.getResultType() == ListenResultType.SPEECH_ENGINE) {
            final GoogleSpeechResponse googleSpeechResponse = listenResult.getGoogleSpeechResponse();
            for (final Character character : question.getPossibleAnswers().keySet()) {
                if (this.atLeastOneTranscriptMatches(googleSpeechResponse, character.toString())) {
                    answersGiven.add(character);
                }
            }
            answerResult.setTranscripts(googleSpeechResponse.getTranscripts());
            // TODO: have backup that tries to match the spoken text to one of the answer texts?
        } else if (listenResult.getResultType() == ListenResultType.CLAPS) {
            final int numberOfClaps = listenResult.getNumberOfClaps();
            if (numberOfClaps > 0 && numberOfClaps <= question.getPossibleAnswers().size()) {
                answersGiven.add(new ArrayList<>(question.getPossibleAnswers().keySet()).get(numberOfClaps - 1));
            }
        } else {
            // ListenResultType.SILENCE
            // Empty list of answers given.
        }

        if (answersGiven.size() == 1) {
            answerResult.setAnswerResultType(AnswerResultType.PROPER);
            answerResult.setAnswer(answersGiven.get(0));
        } else if (answersGiven.size() > 1) {
            answerResult.setAnswerResultType(AnswerResultType.AMBIGUOUS);
        } else {
            // TODO: generify this last bit, same for all questions, right?
            if (listenResult.isEmptyResult()) {
                answerResult.setAnswerResultType(AnswerResultType.NONE);
            } else {
                // Something was said and recognized, but it contained no applicable answer.
                answerResult.setAnswerResultType(AnswerResultType.NOT_APPLICABLE);
            }
        }
        return answerResult;
    }

    public AnswerResult<Boolean> listenForYesNo(final Question<Boolean> question) throws Exception {
        final ListenResult listenResult = this.listen(question);
        final boolean yesFound;
        final boolean noFound;
        final AnswerResultImpl<Boolean> answerResult = new AnswerResultImpl<>(listenResult.getResultType());
        if (listenResult.getResultType() == ListenResultType.SPEECH_ENGINE) {
            final GoogleSpeechResponse googleSpeechResponse = listenResult.getGoogleSpeechResponse();
            yesFound = this.atLeastOneWordFoundInTranscript(googleSpeechResponse,
                    ConfigUtil.getConfirmations(this.configHolder.getConfig(), question.getLocale()));
            noFound = this.atLeastOneWordFoundInTranscript(googleSpeechResponse,
                    ConfigUtil.getNegations(this.configHolder.getConfig(), question.getLocale()));
            answerResult.setTranscripts(googleSpeechResponse.getTranscripts());
        } else if (listenResult.getResultType() == ListenResultType.CLAPS) {
            final int numberOfClaps = listenResult.getNumberOfClaps();
            yesFound = numberOfClaps == 1;
            noFound = numberOfClaps == 2;
        } else {
            // ListenResultType.SILENCE
            yesFound = false;
            noFound = false;
        }

        if (yesFound && !noFound) {
            answerResult.setAnswerResultType(AnswerResultType.PROPER);
            answerResult.setAnswer(true);
        } else if (!yesFound && noFound) {
            answerResult.setAnswerResultType(AnswerResultType.PROPER);
            answerResult.setAnswer(false);
        } else if (yesFound && noFound) {
            answerResult.setAnswerResultType(AnswerResultType.AMBIGUOUS);
        } else {
            // TODO: generify this last bit, same for all questions, right?
            if (listenResult.isEmptyResult()) {
                answerResult.setAnswerResultType(AnswerResultType.NONE);
            } else {
                // Something was said and recognized, but it contained no applicable answer.
                answerResult.setAnswerResultType(AnswerResultType.NOT_APPLICABLE);
            }
        }
        return answerResult;
    }

    public AnswerResult<Character> listenForMultipleChoice(final Question<Character> question) throws Exception {
        final ListenResult listenResult = this.listen(question);
        final List<Character> answersGiven = new ArrayList<>();
        final AnswerResultImpl<Character> answerResult = new AnswerResultImpl<>(listenResult.getResultType());

        if (listenResult.getResultType() == ListenResultType.SPEECH_ENGINE) {
            final GoogleSpeechResponse googleSpeechResponse = listenResult.getGoogleSpeechResponse();
            for (final Character character : question.getPossibleAnswers().keySet()) {
                if (this.atLeastOneTranscriptMatches(googleSpeechResponse, character.toString())) {
                    answersGiven.add(character);
                }
            }
            answerResult.setTranscripts(googleSpeechResponse.getTranscripts());
            // TODO: have backup that tries to match the spoken text to one of the answer texts?
        } else if (listenResult.getResultType() == ListenResultType.CLAPS) {
            final int numberOfClaps = listenResult.getNumberOfClaps();
            if (numberOfClaps > 0 && numberOfClaps <= question.getPossibleAnswers().size()) {
                answersGiven.add(new ArrayList<>(question.getPossibleAnswers().keySet()).get(numberOfClaps - 1));
            }
        } else {
            // ListenResultType.SILENCE
            // Empty list of answers given.
        }

        if (answersGiven.size() == 1) {
            answerResult.setAnswerResultType(AnswerResultType.PROPER);
            answerResult.setAnswer(answersGiven.get(0));
        } else if (answersGiven.size() > 1) {
            answerResult.setAnswerResultType(AnswerResultType.AMBIGUOUS);
        } else {
            // TODO: generify this last bit, same for all questions, right?
            if (listenResult.isEmptyResult()) {
                answerResult.setAnswerResultType(AnswerResultType.NONE);
            } else {
                // Something was said and recognized, but it contained no applicable answer.
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

    private boolean atLeastOneTranscriptMatches(final GoogleSpeechResponse googleSpeechResponse, final String word) {
        return googleSpeechResponse.getTranscripts().stream()
                .anyMatch(transcript -> transcript.toLowerCase().equals(word.toLowerCase()));
    }

    // TODO: use less generic exception type(s)?
    private ListenResult listen(final Question<?> question) throws Exception {
        final boolean listenForSpeech = question.getInteractionType().getListenMode().shouldListenForSpeech();
        final boolean listenForClaps = question.getInteractionType().getListenMode().shouldListenForClaps();

        final TargetDataLine line = this.openNewLine();
        line.start();
        final AudioInputStream ais = new AudioInputStream(line);

        final FLACEncoder encoder = new FLACEncoder();
        if (listenForSpeech) {
            encoder.setStreamConfiguration(this.audioFormat.getStreamConfiguration());
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            encoder.setOutputStream(new FLACStreamOutputStream(outputStream));
            encoder.openFLACStream();
        }

        // Buffer frequency means the buffer size is filled that many times per second.
        final int bufferFrequency = 5;
        final int bufferSize = this.audioFormat.getSampleRate() / bufferFrequency;
        // TODO: support for 2 byte (16 bit) samples
        // TODO: fail for channels > 1, no use anyway
        final int seconds = 3;
        final int abufferSize = bufferSize;
        final byte[] byteArray = new byte[abufferSize];
        final int[] intArray = new int[byteArray.length];
        final int frameSize = this.audioFormat.getJavaAudioFormat().getFrameSize();
        // TODO: some startup time in which no peaks will be detected because of 'mic going on' noise...
        // Maybe that is facilitated by playing a small audio sample first?
        this.audioPlayer.playMp3(this.getClass().getResourceAsStream("/com/programyourhome/config/voice-control/sounds/blip.mp3"));
        // Skip any sound already recorded up until this point.
        ais.skip(ais.available());
        final BufferedInputStream bis = new BufferedInputStream(ais, bufferSize);
        for (long i = 0; i < ((long) this.audioFormat.getSampleRate() * (this.audioFormat.getSampleSizeInBits() / Byte.SIZE)
                * this.audioFormat.getNumberOfChannels() * seconds) / abufferSize; i++) {
            bis.read(byteArray);

            // TODO: sound processing:
            // - detect silence before and after 'data-sound' to be cut off: not encoded and not sent to speech api
            // - maxSilenceVolume parameter (percentage of max?)
            // - max timeout for listening to possible sound data
            // - timeout for silence total or after last 'sound-data', different!
            // -> also possibly differ between open and non-open questions, to give open answers more thinking time.

            // converting byteArray to intArray
            for (int j = 0; j < byteArray.length; j++) {
                intArray[j] = byteArray[j];
            }
            final int numberOfInterchannelSamples = abufferSize;
            // TODO: change to only add samples if they are to be included for transcription.
            if (listenForSpeech) {
                encoder.addSamples(intArray, numberOfInterchannelSamples);
                encoder.encodeSamples(encoder.fullBlockSamplesAvailableToEncode(), false);
            }
            if (listenForClaps) {
                // TODO: Logic to decide if there is clapping going on.
                // idea: treshold for: (percentage of max?)
                // - minClapVolume (reach this volume to detect a single clap)
                // - unClapVolume (drop to this volume to 'finish' the previous clap and continue to a possible next one)
                // - maxTimeBetweenClaps (after this time the amount of claps will be determined and no new sound data will be processed for clapping)
                // furthermore: 'around' the clapping should be silence. if there is too much other 'noise', speech gets precedence
                // we could also still keep the flac encoder and just not use it to send stuff to Google.
                // performance overhead is minimal and code stays cleaner
            }
        }

        // TODO: document use of flac encoder: default block size 4096, encodes per block, one time the rest of the bytes at the end.
        encoder.encodeSamples(encoder.samplesAvailableToEncode(), true);
        bis.close();
        line.stop();
        line.close();
        this.audioPlayer.playMp3(this.getClass().getResourceAsStream("/com/programyourhome/config/voice-control/sounds/blip.mp3"));

        final String urlString = String.format(GOOGLE_SPEECH_API_URL,
                question.getLocale(),
                URLEncoder.encode(this.googleSpeechClient, ENCODING_UTF8),
                URLEncoder.encode(this.googleSpeechApp, ENCODING_UTF8),
                this.googleSpeechKey);

        final byte[] flacBytes = outputStream.toByteArray();
        outputStream.close();

        final Content googleSpeechResponse = Request.Post(urlString)
                .bodyStream(new ByteArrayInputStream(flacBytes))
                .userAgent(this.googleSpeechClient)
                .setHeader("Content-Type", "audio/x-flac; rate=" + this.audioFormat.getSampleRate() + ";") // needed, including rate
                .execute().returnContent();

        this.log.debug("Google speech response: " + googleSpeechResponse);
        return ListenResult.googleSpeech(this.parseGoogleResponse(googleSpeechResponse.asString()));
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

}
