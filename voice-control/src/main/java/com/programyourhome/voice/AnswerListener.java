package com.programyourhome.voice;

import java.io.BufferedInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.TargetDataLine;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.programyourhome.voice.builder.QuestionBuilderFactory;
import com.programyourhome.voice.config.ConfigUtil;
import com.programyourhome.voice.config.VoiceControlConfigHolder;
import com.programyourhome.voice.detection.AudioDetector;
import com.programyourhome.voice.detection.AudioFrame;
import com.programyourhome.voice.detection.ClapDetector;
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

    // TODO: speech API does not work with Dutch! Hoe to fix if possible?
    // TODO: fix / retry. Currently (april 7, 2015 it is broken)

    // TODO: use this for tracing
    private final Log log = LogFactory.getLog(this.getClass());

    private final PyhAudioFormat audioFormat;

    @Autowired
    private VoiceControlConfigHolder configHolder;

    @Autowired
    private ApplicationContext applicationContext;

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

        if (listenResult.getResultType() == ListenResultType.SPEECH) {
            // Add the transcripts to the answer result, but don't try to get any data from them, since we are only interested in claps.
            answerResult.setTranscripts(listenResult.getTranscripts());
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
        if (listenResult.getResultType() == ListenResultType.SPEECH) {
            yesFound = this.atLeastOneWordFoundInTranscript(listenResult.getTranscripts(),
                    ConfigUtil.getConfirmations(this.configHolder.getConfig(), question.getLocale()));
            noFound = this.atLeastOneWordFoundInTranscript(listenResult.getTranscripts(),
                    ConfigUtil.getNegations(this.configHolder.getConfig(), question.getLocale()));
            answerResult.setTranscripts(listenResult.getTranscripts());
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

        if (listenResult.getResultType() == ListenResultType.SPEECH) {
            for (final Character character : question.getPossibleAnswers().keySet()) {
                if (this.atLeastOneTranscriptMatches(listenResult.getTranscripts(), character.toString())) {
                    answersGiven.add(character);
                }
            }
            answerResult.setTranscripts(listenResult.getTranscripts());
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

    private boolean atLeastOneWordFoundInTranscript(final List<String> transcripts, final Collection<String> words) {
        return transcripts.stream()
                .flatMap(transcript -> Arrays.stream(StringUtils.split(transcript)))
                .anyMatch(word -> words.contains(word.toLowerCase()));
    }

    private boolean atLeastOneTranscriptMatches(final List<String> transcripts, final String word) {
        return transcripts.stream()
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
    // TODO: support for 2 byte (16 bit) samples / multiple channels / etc, see if the generic code holds for that.
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

        final List<AudioDetector<?>> audioDetectors = new ArrayList<>();
        final ClapDetector clapDetector = this.conditionallyRegisterDetector(ClapDetector.class, listenForClaps, audioDetectors);
        final SpeechDetector speechDetector = this.conditionallyRegisterDetector(SpeechDetector.class, listenForSpeech, audioDetectors);
        if (listenForSpeech) {
            speechDetector.setLocale(question.getLocale());
        }

        for (final AudioDetector<?> audioDetector : audioDetectors) {
            audioDetector.audioStreamOpened(this.audioFormat);
        }

        // TODO: refactor out the encoder, similar to refactoring out the clap detector.
        boolean listeningStartTimeoutReached = false;
        boolean listeningStopTimeoutReached = false;

        long lastNonSilenceMilli = 0;
        boolean nonSilenceDetected = false;

        int totalFramesRead = 0;
        final long listeningStart = System.currentTimeMillis();
        while (!listeningStartTimeoutReached && !listeningStopTimeoutReached) {
            final int bytesRead = bufferedInputStream.read(frameBytes);

            if (bytesRead != frameBytes.length) {
                throw new IllegalStateException("End of audio input stream reached while listening for answer.");
            }

            totalFramesRead++;
            final AudioFrame audioFrame = new AudioFrame(this.audioFormat, totalFramesRead, frameBytes);

            // TODO: from properties file + differ for open & non-open questions
            final int listeningStartTimeoutInMillis = 3000;
            final int listeningStopTimeoutInMillis = 1000;

            final int maxSilenceVolumePercentage = 20;

            // TODO: clean up code below
            // TODO: cut off silence before and after non-silence
            // TODO: minimum non silence 'period' (for speech) to not include microphone 'freaks' (but what is a period?)
            // - min amount of time a non silence is detected max x bytes / millis in between

            for (final AudioDetector<?> audioDetector : audioDetectors) {
                audioDetector.nextFrame(audioFrame);
            }

            // TODO: add this logic below in a non-silence detector?
            if (audioFrame.getVolumePercentage() > maxSilenceVolumePercentage) {
                if (!nonSilenceDetected) {
                    System.out.println("Non silence detected!");
                }
                nonSilenceDetected = true;
                lastNonSilenceMilli = audioFrame.getMillisSinceStart();
            } else {
                if (nonSilenceDetected) {
                    final long silenceMillis = audioFrame.getMillisSinceStart() - lastNonSilenceMilli;
                    if (silenceMillis >= listeningStopTimeoutInMillis) {
                        listeningStopTimeoutReached = true;
                    }
                } else {
                    final double silenceMillis = audioFrame.getMillisSinceStart() - listeningStart;
                    if (silenceMillis >= listeningStartTimeoutInMillis) {
                        listeningStartTimeoutReached = true;
                    }
                }
            }
        }

        System.out.println("listening start timeout: " + listeningStartTimeoutReached);
        System.out.println("listening stop  timeout: " + listeningStopTimeoutReached);
        System.exit(0);

        line.stop();
        line.close();
        bufferedInputStream.close();

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
