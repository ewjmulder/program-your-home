package com.programyourhome.voice;

import java.io.BufferedInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.TargetDataLine;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.programyourhome.voice.config.ConfigUtil;
import com.programyourhome.voice.config.VoiceControlConfigHolder;
import com.programyourhome.voice.detection.AudioDetector;
import com.programyourhome.voice.detection.AudioFrame;
import com.programyourhome.voice.detection.ClapDetector;
import com.programyourhome.voice.detection.NonSilenceDetector;
import com.programyourhome.voice.detection.SpeechDetector;
import com.programyourhome.voice.format.PyhAudioFormat;
import com.programyourhome.voice.model.AnswerResult;
import com.programyourhome.voice.model.AnswerResultImpl;
import com.programyourhome.voice.model.AnswerResultType;
import com.programyourhome.voice.model.ListenResult;
import com.programyourhome.voice.model.ListenResultType;
import com.programyourhome.voice.model.question.Question;

@Component
public class AnswerListener {

    private final PyhAudioFormat audioFormat;

    @Autowired
    private VoiceControlConfigHolder configHolder;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private AudioPlayer audioPlayer;

    @Value("${listeningStartTimeoutInMillis}")
    private int listeningStartTimeoutInMillis;
    @Value("${listeningStopTimeoutInMillis}")
    private int listeningStopTimeoutInMillis;

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

    public <T> AnswerResult<T> listenForAnswer(final Question<T> question,
            final BiConsumer<List<String>, AnswerResult<T>> speechResultProcessor,
            final BiConsumer<Integer, AnswerResult<T>> clapResultProcessor) throws Exception {
        final ListenResult listenResult = this.listen(question);
        final AnswerResultImpl<T> answerResult = new AnswerResultImpl<>(listenResult.getResultType());

        if (listenResult.getResultType() == ListenResultType.SPEECH) {
            speechResultProcessor.accept(listenResult.getTranscripts(), answerResult);
            answerResult.setTranscripts(listenResult.getTranscripts());
        } else if (listenResult.getResultType() == ListenResultType.CLAPS) {
            clapResultProcessor.accept(listenResult.getNumberOfClaps(), answerResult);
        }

        if (answerResult.getAnswer() != null) {
            answerResult.setAnswerResultType(AnswerResultType.PROPER);
        } else {
            if (listenResult.isEmptyResult()) {
                answerResult.setAnswerResultType(AnswerResultType.NONE);
            } else {
                // Something was recognized, but it contained no applicable answer.
                answerResult.setAnswerResultType(AnswerResultType.NOT_APPLICABLE);
            }
        }
        return answerResult;
    }

    public AnswerResult<Integer> listenForNumber(final Question<Integer> question) throws Exception {
        return this.listenForAnswer(question,
                (transcripts, answerResult) -> {
                    this.processAnswersGiven(this.getAllNumericTranscripts(transcripts), answerResult);
                },
                (claps, answerResult) -> {
                    answerResult.setAnswer(claps);
                });
    }

    public AnswerResult<Boolean> listenForYesNo(final Question<Boolean> question) throws Exception {
        final List<Boolean> answersGiven = new ArrayList<>();
        return this.listenForAnswer(question,
                (transcripts, answerResult) -> {
                    if (this.atLeastOneWordFoundInTranscript(transcripts, this.getConfirmations(question))) {
                        answersGiven.add(true);
                    }
                    if (this.atLeastOneWordFoundInTranscript(transcripts, this.getNegations(question))) {
                        answersGiven.add(false);
                    }
                    this.processAnswersGiven(answersGiven, answerResult);
                },
                (claps, answerResult) -> {
                    if (claps == 1) {
                        answersGiven.add(true);
                    }
                    if (claps == 2) {
                        answersGiven.add(false);
                    }
                    this.processAnswersGiven(answersGiven, answerResult);
                });
    }

    private Collection<String> getConfirmations(final Question<?> question) {
        return ConfigUtil.getConfirmations(this.configHolder.getConfig(), question.getLocale());
    }

    private Collection<String> getNegations(final Question<?> question) {
        return ConfigUtil.getNegations(this.configHolder.getConfig(), question.getLocale());
    }

    public AnswerResult<Character> listenForMultipleChoice(final Question<Character> question) throws Exception {
        // TODO: have backup that tries to match the spoken text to one of the answer texts?
        return this.listenForAnswer(question,
                (transcripts, answerResult) -> {
                    final List<Character> answersGiven = new ArrayList<>();
                    for (final Character character : question.getPossibleAnswers().keySet()) {
                        if (this.atLeastOneTranscriptMatches(transcripts, character.toString())) {
                            answersGiven.add(character);
                        }
                    }
                    this.processAnswersGiven(answersGiven, answerResult);
                },
                (claps, answerResult) -> {
                    if (claps > 0 && claps <= question.getPossibleAnswers().size()) {
                        answerResult.setAnswer(new ArrayList<>(question.getPossibleAnswers().keySet()).get(claps - 1));
                    }
                });
    }

    private <T> void processAnswersGiven(final Collection<T> answersGiven, final AnswerResult<T> answerResult) {
        if (answersGiven.size() == 1) {
            answerResult.setAnswer(answersGiven.iterator().next());
        } else if (answersGiven.size() > 1) {
            answerResult.setAnswerResultType(AnswerResultType.AMBIGUOUS);
        }
    }

    private Collection<Integer> getAllNumericTranscripts(final List<String> transcripts) {
        return transcripts.stream()
                .filter(StringUtils::isNumeric)
                .map(Integer::valueOf)
                .collect(Collectors.toList());
    }

    private boolean atLeastOneWordFoundInTranscript(final List<String> transcripts, final Collection<String> words) {
        return transcripts.stream()
                .flatMap(transcript -> Arrays.stream(StringUtils.split(transcript)))
                .anyMatch(word -> words.contains(word.toLowerCase()));
    }

    private boolean atLeastOneTranscriptMatches(final List<String> transcripts, final String word) {
        return transcripts.stream()
                .anyMatch(transcript -> transcript.toLowerCase().equals(word.toLowerCase()));
    }

    // TODO: use less generic exception type(s)?
    private ListenResult listen(final Question<?> question) throws Exception {
        final boolean listenForSpeech = question.getListenMode().shouldListenForSpeech();
        final boolean listenForClaps = question.getListenMode().shouldListenForClaps();

        final TargetDataLine line = this.openNewLine();
        line.start();
        final AudioInputStream audioInputStream = new AudioInputStream(line);

        // Since we want to have full fine grained control, we'll have a buffer of just one frame, and process every frame
        // from the stream immediately and separately.
        final int bufferSize = this.audioFormat.getNumberOfBytesPerFrame();
        // Byte array to read from the audio input stream.
        final byte[] frameBytes = new byte[bufferSize];
        // Play a small audio sample to indicate the system starts listening. This also has the nice side effect of
        // having a small 'startup' time for the microphone input stream, so any 'startup noise' will be skipped.
        this.audioPlayer.playMp3(this.getClass().getResourceAsStream("/com/programyourhome/config/voice-control/sounds/blip.mp3"));
        // Skip any sound already recorded up until this point.
        audioInputStream.skip(audioInputStream.available());
        final BufferedInputStream bufferedInputStream = new BufferedInputStream(audioInputStream, bufferSize);

        final List<AudioDetector<?>> audioDetectors = new ArrayList<>();
        final NonSilenceDetector nonSilenceDetector = this.conditionallyRegisterDetector(NonSilenceDetector.class, true, audioDetectors);
        final ClapDetector clapDetector = this.conditionallyRegisterDetector(ClapDetector.class, listenForClaps, audioDetectors);
        final SpeechDetector speechDetector = this.conditionallyRegisterDetector(SpeechDetector.class, listenForSpeech, audioDetectors);
        if (listenForSpeech) {
            speechDetector.setLocale(question.getLocale());
        }

        for (final AudioDetector<?> audioDetector : audioDetectors) {
            audioDetector.audioStreamOpened(this.audioFormat);
        }

        boolean stopListening = false;

        // Keep track of the total number of frames read as a way to track the timing in the audio stream.
        int totalFramesRead = 0;
        while (!stopListening) {
            final int bytesRead = bufferedInputStream.read(frameBytes);
            if (bytesRead != frameBytes.length) {
                throw new IllegalStateException("End of audio input stream reached while listening for answer.");
            }

            totalFramesRead++;
            final AudioFrame audioFrame = new AudioFrame(this.audioFormat, totalFramesRead, frameBytes);

            // Provide the detectors with the next audio frame.
            for (final AudioDetector<?> audioDetector : audioDetectors) {
                audioDetector.nextFrame(audioFrame);
            }

            if (nonSilenceDetector.isOnlySilence() && audioFrame.getMillisSinceStart() >= this.listeningStartTimeoutInMillis) {
                stopListening = true;
            } else if (nonSilenceDetector.getSilenceMillisSinceLastNonSilence(audioFrame) >= this.listeningStopTimeoutInMillis) {
                stopListening = true;
            }
        }

        line.stop();
        line.close();
        bufferedInputStream.close();

        // Play a small audio sample to indicate the system has stopped listening.
        this.audioPlayer.playMp3(this.getClass().getResourceAsStream("/com/programyourhome/config/voice-control/sounds/blip.mp3"));

        for (final AudioDetector<?> audioDetector : audioDetectors) {
            audioDetector.audioStreamClosed();
        }
        // TODO: put this call under some logic about whether to do this (so maybe we actually prefer claps??
        if (listenForSpeech) {
            speechDetector.performSpeechRecognition();
        }

        // We prefer speech results over clap results.
        if (listenForSpeech && speechDetector.isValueDetected()) {
            return ListenResult.speech(speechDetector.getDetectedValue());
        } else if (listenForClaps && clapDetector.isValueDetected()) {
            return ListenResult.claps(clapDetector.getDetectedValue());
        } else if (listenForSpeech) {
            return ListenResult.emptySpeech();
        } else if (listenForClaps) {
            return ListenResult.emptyClaps();
        } else {
            throw new IllegalStateException("Not listening for anything.");
        }
    }

    private <T extends AudioDetector<?>> T conditionallyRegisterDetector(
            final Class<T> clazz, final boolean shouldBeCreated, final List<AudioDetector<?>> audioDetectors) {
        final T detector;
        if (shouldBeCreated) {
            detector = this.applicationContext.getBean(clazz);
            audioDetectors.add(detector);
        } else {
            detector = null;
        }
        return detector;
    }
}
