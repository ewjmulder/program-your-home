package com.programyourhome.voice;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
import com.programyourhome.voice.builder.QuestionBuilderFactory;
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

    // TODO: refactor whole listen for logic: extract common functionality and split up in smaller methods.
    // Callback for handling specific listen results and handling definitive answer result.

    public AnswerResult<Integer> listenForNumber(final Question<Integer> question) throws Exception {
        final ListenResult listenResult = this.listen(question);
        Integer answer = null;
        final AnswerResultImpl<Integer> answerResult = new AnswerResultImpl<>(listenResult.getResultType());

        if (listenResult.getResultType() == ListenResultType.SPEECH_ENGINE) {
            // Add the transcripts to the answer result, but don't try to get any data from them, since we are only interested in claps.
            final GoogleSpeechResponse googleSpeechResponse = listenResult.getGoogleSpeechResponse();
            answerResult.setTranscripts(googleSpeechResponse.getTranscripts());
        } else if (listenResult.getResultType() == ListenResultType.CLAPS) {
            final int numberOfClaps = listenResult.getNumberOfClaps();
            for (final Integer integer : question.getPossibleAnswers().keySet()) {
                if (integer == numberOfClaps) {
                    answer = integer;
                }
            }
        } else {
            // ListenResultType.SILENCE
            // Leave default answer of null
        }

        if (answer != null) {
            answerResult.setAnswerResultType(AnswerResultType.PROPER);
            answerResult.setAnswer(answer);
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

    public static void main(final String[] args) throws Exception {
        new AnswerListener().listen(QuestionBuilderFactory.yesNoQuestionBuilder()
                .text("text")
                .locale("en-us")
                .acceptClaps(true)
                .build());
    }

    // TODO: use less generic exception type(s)?
    // TODO: support for 2 byte (16 bit) samples
    private ListenResult listen(final Question<?> question) throws Exception {
        final boolean listenForSpeech = question.getListenMode().shouldListenForSpeech();
        final boolean listenForClaps = question.getListenMode().shouldListenForClaps();

        final TargetDataLine line = this.openNewLine();
        line.start();
        final AudioInputStream audioInputStream = new AudioInputStream(line);

        // final InputStream audioInputStream = new InputStream() {
        //
        // @Override
        // public int available() throws IOException {
        // return 1;
        // }
        //
        // private boolean high;
        //
        // @Override
        // public int read() throws IOException {
        // this.high = !this.high;
        // return this.high ? 127 : 5;
        // }
        //
        // };

        // Always start the FLAC encoder, even if we don't want to listen to speech.
        // The overhead is minimal, but the code stays simpler.
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final FLACEncoder encoder = this.openFLACEncoder(outputStream);

        // Buffer frequency means the buffer size is filled that many times per second.
        final int bufferFrequency = 5;
        final int bufferSize = this.audioFormat.getByteRate() / bufferFrequency;
        // Byte array to read from the audio input stream.
        final byte[] byteArray = new byte[bufferSize];
        // Int array of the same size, because the encoder works with int's.
        final int[] intArray = new int[byteArray.length];
        // Play a small audio sample to indicate the system starts listening. This also has the nice side effect of
        // having a small 'startup' time for the microphone input stream, so any 'startup noise' will be skipped.
        // TODO: re-enable
        // this.audioPlayer.playMp3(this.getClass().getResourceAsStream("/com/programyourhome/config/voice-control/sounds/blip.mp3"));
        // TODO: remove
        try {
            Thread.sleep(200);
        } catch (final InterruptedException e) {
        }
        // Skip any sound already recorded up until this point.
        audioInputStream.skip(audioInputStream.available());
        final BufferedInputStream bufferedInputStream = new BufferedInputStream(audioInputStream, bufferSize);

        boolean listeningStartTimeoutReached = false;
        boolean listeningStopTimeoutReached = false;
        int totalBytesRead = 0;
        int lastNonSilenceByte = 0;
        boolean nonSilenceDetected = false;
        int numberOfClapsDetected = 0;
        boolean currentlyInClap = false;
        int currentClapPeaks = 0;
        int lastClapStartByte = 0;
        int lastClapPeakByte = 0;
        int lastClapStopByte = 0;
        while (!listeningStartTimeoutReached && !listeningStopTimeoutReached) {
            final int bytesRead = bufferedInputStream.read(byteArray);

            if (bytesRead != byteArray.length) {
                throw new IllegalStateException("End of audio input stream reached while listening for answer.");
            }

            // TODO: from properties file + differ for open & non-open questions
            final int listeningStartTimeoutInMillis = 3000;
            final int listeningStopTimeoutInMillis = 1000;

            final int maxSilenceVolumePercentage = 20;

            // TODO: clean up code below
            // TODO: cut off silence before and after non-silence
            // TODO: minimum non silence 'period' (for speech) to not include microphone 'freaks' (but what is a period?)
            // - min amount of time a non silence is detected max x bytes / millis in between

            // Loop through all the bytes read for detailed sound inspection, for instance silence and claps detection.
            // In the same loop: put those values in the intArray, for the encoder.
            for (int i = 0; i < byteArray.length && !listeningStartTimeoutReached && !listeningStopTimeoutReached; i++) {
                totalBytesRead++;
                intArray[i] = byteArray[i];

                final double volumePercentage = (byteArray[i] / ((byteArray[i] < 0) ? -128.0 : 127.0)) * 100;
                if (volumePercentage > maxSilenceVolumePercentage) {
                    if (!nonSilenceDetected) {
                        System.out.println("Non silence detected!");
                    }
                    nonSilenceDetected = true;
                    lastNonSilenceByte = totalBytesRead;
                } else {
                    if (nonSilenceDetected) {
                        final int currentSilenceBytes = totalBytesRead - lastNonSilenceByte;
                        final double silenceMillis = (currentSilenceBytes / (double) this.audioFormat.getByteRate()) * 1000;
                        if (silenceMillis >= listeningStopTimeoutInMillis) {
                            listeningStopTimeoutReached = true;
                        }
                    } else {
                        final int currentSilenceBytes = totalBytesRead - lastNonSilenceByte;
                        final double silenceMillis = (currentSilenceBytes / (double) this.audioFormat.getByteRate()) * 1000;
                        if (silenceMillis >= listeningStartTimeoutInMillis) {
                            listeningStartTimeoutReached = true;
                        }
                    }
                }

                if (listenForClaps) {
                    // Reach this volume to detect a clap peak.
                    final int minClapVolumePercentage = 60;
                    // TODO: test with real clapping to see if this value needs to be
                    // - higher (if clap sound 'echos' with increase peak after 5 millis)
                    // - or lower (if clap peaks always are a fraction of this after one another)
                    // The maximum interval between volume peaks to be considered of the same clap.
                    final int maximumClapPeakIntervalInMillis = 5;
                    // TODO: test with real clapping to see if this value needs to be
                    // - higher (if clap sounds always produce numerous peaks)
                    // - or lower (if clap sounds can be as small as 1 peak)
                    // Minimum amount of peaks to be considered one clap. This value is here to prevent registering 1 single high volume byte as a clap.
                    final int minimumClapPeaksInOneClap = 2;
                    // After this amount of time with no new claps the amount of claps will be determined and no new sound data will be processed for clapping.
                    // TODO: what if smaller then other timeout?
                    final int maxTimeBetweenClapsInMillis = 1000;

                    if (!currentlyInClap) {
                        if (volumePercentage >= minClapVolumePercentage) {
                            System.out.println("Start of clap detected: " + totalBytesRead);
                            currentlyInClap = true;
                            // This is the first peak.
                            currentClapPeaks++;
                            lastClapStartByte = totalBytesRead;
                            lastClapPeakByte = totalBytesRead;
                        } else if (numberOfClapsDetected > 0) {
                            final int currentSilenceBytes = totalBytesRead - lastClapStopByte;
                            final double silenceMillis = (currentSilenceBytes / (double) this.audioFormat.getByteRate()) * 1000;
                            if (silenceMillis >= maxTimeBetweenClapsInMillis) {
                                System.out.println("Max time between claps reached!");
                                listeningStopTimeoutReached = true;
                            }
                        } else {
                            // TODO: general silence detection timeout
                        }
                    } else {
                        if (volumePercentage >= minClapVolumePercentage) {
                            final int currentClapPeakIntervalBytes = totalBytesRead - lastClapPeakByte;
                            final double clapPeakIntervalMillis = (currentClapPeakIntervalBytes / (double) this.audioFormat.getByteRate()) * 1000;
                            if (clapPeakIntervalMillis <= maximumClapPeakIntervalInMillis) {
                                System.out.println("Peak in clap: since last peak: "
                                        + ((currentClapPeakIntervalBytes / (double) this.audioFormat.getByteRate())
                                        * 1000) + ", since start of clap: "
                                        + (((totalBytesRead - lastClapStartByte) / (double) this.audioFormat.getByteRate())
                                        * 1000));
                                currentClapPeaks++;
                                lastClapPeakByte = totalBytesRead;
                            }
                        } else {
                            final int sinceLastClapPeakBytes = totalBytesRead - lastClapPeakByte;
                            final double sinceLastClapPeakMillis = (sinceLastClapPeakBytes / (double) this.audioFormat.getByteRate()) * 1000;
                            if (sinceLastClapPeakMillis > maximumClapPeakIntervalInMillis) {
                                System.out.println("Stop of clap detected: " + totalBytesRead);
                                currentlyInClap = false;
                                lastClapStopByte = totalBytesRead;
                                System.out.println("Clap time: " + (((lastClapStopByte - lastClapStartByte) / (double) this.audioFormat.getByteRate())
                                        * 1000));
                                System.out.println("Clap peak amount: " + currentClapPeaks);
                                if (currentClapPeaks >= minimumClapPeaksInOneClap) {
                                    numberOfClapsDetected++;
                                }
                                currentClapPeaks = 0;
                            }
                        }
                    }

                    // TODO: furthermore: 'around' the clapping should be silence. if there is too much other 'noise', speech gets precedence
                }

            }

            final int numberOfInterchannelSamples = bufferSize;
            // TODO: change to only add samples if they are to be included for transcription.
            encoder.addSamples(intArray, numberOfInterchannelSamples);
            encoder.encodeSamples(encoder.fullBlockSamplesAvailableToEncode(), false);
        }
        System.out.println("listening start timeout: " + listeningStartTimeoutReached);
        System.out.println("listening stop  timeout: " + listeningStopTimeoutReached);
        System.out.println("#claps: " + numberOfClapsDetected);
        System.exit(0);

        encoder.encodeSamples(encoder.samplesAvailableToEncode(), true);
        bufferedInputStream.close();
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

        if (listenForSpeech) {
            final Content googleSpeechResponse = Request.Post(urlString)
                    .bodyStream(new ByteArrayInputStream(flacBytes))
                    .userAgent(this.googleSpeechClient)
                    .setHeader("Content-Type", "audio/x-flac; rate=" + this.audioFormat.getSampleRate() + ";") // needed, including rate
                    .execute().returnContent();
            this.log.debug("Google speech response: " + googleSpeechResponse);
            return ListenResult.googleSpeech(this.parseGoogleResponse(googleSpeechResponse.asString()));
        } else if (listenForClaps) {
            // TODO
            return ListenResult.claps(5);
        } else {
            throw new IllegalStateException("Not listening for anything.");
        }
    }

    // TODO: document use of flac encoder: default block size 4096, encodes per block, one time the rest of the bytes at the end.
    private FLACEncoder openFLACEncoder(final OutputStream outputStream) throws IOException {
        final FLACEncoder encoder = new FLACEncoder();
        encoder.setStreamConfiguration(this.audioFormat.getStreamConfiguration());
        encoder.setOutputStream(new FLACStreamOutputStream(outputStream));
        encoder.openFLACStream();
        return encoder;
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
