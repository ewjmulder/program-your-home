package com.programyourhome.voice;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;
import java.util.stream.Collectors;

import javazoom.jl.player.Player;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import com.programyourhome.voice.config.VoiceControlConfigHolder;
import com.programyourhome.voice.model.InteractionType;
import com.programyourhome.voice.model.PyhLanguage;
import com.programyourhome.voice.model.PyhLanguageImpl;
import com.programyourhome.voice.model.question.Question;

@Component
@PropertySource("classpath:com/programyourhome/config/voice-control/properties/voice-control.properties")
public class VoiceControlImpl implements VoiceControl {

    private static final String ENCODING_UTF8 = "UTF-8";
    private static final String GOOGLE_SPEECH_TTS_URL = "http://translate.google.com/translate_tts?q=%s&tl=%s&ie=%s";

    private final Log log = LogFactory.getLog(this.getClass());

    @Autowired
    private final TaskScheduler speechScheduler;

    @Autowired
    private VoiceControlConfigHolder configHolder;

    @Autowired
    private AnswerListener answerListener;

    @Value("${googleSpeechTts.userAgent}")
    private String googleSpeechUserAgent;

    private final Queue<Question<?>> questionQueue;

    private boolean busy;

    public VoiceControlImpl() {
        this.busy = false;
        this.questionQueue = new LinkedList<>();
        this.speechScheduler = new ThreadPoolTaskScheduler();
        ((ThreadPoolTaskScheduler) this.speechScheduler).initialize();

        this.speechScheduler.scheduleAtFixedRate(this::speak, DateUtils.addMilliseconds(new Date(), 50), 50);
    }

    @Override
    public Collection<PyhLanguage> getSupportedLanguages() {
        return this.configHolder.getConfig().getLanguages().stream()
                .map(language -> new PyhLanguageImpl(language))
                .collect(Collectors.toList());
    }

    private synchronized void speak() {
        if (!this.questionQueue.isEmpty() && !this.busy) {
            this.busy = true;
            final Question<?> question = this.questionQueue.poll();
            // TODO: reflect all question info
            this.log.info("Saying: '" + question.getText() + "' in language: '" + question.getLocale() + "'.");
            this.doAskQuestion(question);
        }
    }

    @Override
    public void askQuestion(final Question<?> question) {
        this.questionQueue.add(question);
    }

    // @Override
    // public void say(final String text, final Language language) {
    // this.say(text, language, () -> { /* Empty callback */
    // });
    // }
    //
    // @Override
    // public void say(final String text, final Language language, final Runnable callback) {
    // this.sayQueue.add(new Question(text, language, callback));
    // }

    private void doAskQuestion(final Question<?> question) {
        try {
            final String urlString = String.format(GOOGLE_SPEECH_TTS_URL,
                    URLEncoder.encode(question.getText(), ENCODING_UTF8), question.getLocale(), ENCODING_UTF8);
            final URL url = new URL(urlString);
            final URLConnection urlConnection = url.openConnection();
            // Set the user agent to a sane value to prevent a 403 response.
            urlConnection.addRequestProperty("User-Agent", this.googleSpeechUserAgent);
            final InputStream audioInputStream = new BufferedInputStream(urlConnection.getInputStream());
            final Player player = new Player(audioInputStream);
            player.play();
            // TODO: listen to answer
            if (question.getInteractionType() == InteractionType.NONE) {
                question.getAnswerCallback().answer(null);
            } else if (question.getInteractionType() == InteractionType.YES_NO) {
                // TODO: handle no result situation
                final Boolean yesNo = this.answerListener.listenForYesNo("en-us");
                // TODO: how to prevent this?
                final Question<?> nextQuestion = ((Question<Boolean>) question).getAnswerCallback().answer(yesNo);
                if (nextQuestion != null) {
                    this.doAskQuestion(nextQuestion);
                }
            }
        } catch (final Exception e) {
            throw new IllegalStateException("Exception encountered while saying: '" + question.getText() + "'.", e);
        } finally {
            this.busy = false;
            // TODO: handle callback exceptions?
        }
    }

}
