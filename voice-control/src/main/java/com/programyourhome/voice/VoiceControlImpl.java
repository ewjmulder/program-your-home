package com.programyourhome.voice;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

import javazoom.jl.player.Player;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import com.programyourhome.voice.model.AudioInteraction;
import com.programyourhome.voice.model.Language;
import com.programyourhome.voice.model.Say;

@Component
public class VoiceControlImpl implements VoiceControl {

    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String ENCODING_UTF8 = "UTF-8";
    private static final String SPEECH_URL = "http://translate.google.com/translate_tts?q=%s&tl=%s&ie=%s";

    private final Log log = LogFactory.getLog(this.getClass());

    @Autowired
    private final TaskScheduler speechScheduler;

    private final Queue<Say> sayQueue;

    private boolean freeToSpeak;

    public VoiceControlImpl() {
        this.freeToSpeak = true;
        this.sayQueue = new LinkedList<>();
        this.speechScheduler = new ThreadPoolTaskScheduler();
        ((ThreadPoolTaskScheduler) this.speechScheduler).initialize();

        this.speechScheduler.scheduleAtFixedRate(this::speak, DateUtils.addMilliseconds(new Date(), 50), 50);
    }

    private synchronized void speak() {
        if (!this.sayQueue.isEmpty() && this.freeToSpeak) {
            this.freeToSpeak = false;
            final Say say = this.sayQueue.poll();
            this.log.info("Saying: '" + say.getText() + "' in language: '" + say.getLanguage() + "'.");
            this.doSay(say);
        }
    }

    @Override
    public void say(final String text, final Language language) {
        this.say(text, language, () -> { /* Empty callback */
        });
    }

    @Override
    public void say(final String text, final Language language, final Runnable callback) {
        this.sayQueue.add(new Say(text, language, callback));
    }

    @Override
    public void performAudioInteraction(final AudioInteraction audioInteraction) {
        // TODO Auto-generated method stub

    }

    private void doSay(final Say say) {
        try {
            final String urlString = String.format(SPEECH_URL,
                    URLEncoder.encode(say.getText(), ENCODING_UTF8), say.getLanguage().getCode(), ENCODING_UTF8);
            final URL url = new URL(urlString);
            final URLConnection urlConnection = url.openConnection();
            // Set the user agent to a sane value to prevent a 403 response.
            urlConnection.addRequestProperty("User-Agent", USER_AGENT);
            final InputStream audioInputStream = new BufferedInputStream(urlConnection.getInputStream());
            final Player player = new Player(audioInputStream);
            player.play();
        } catch (final Exception e) {
            throw new IllegalStateException("Exception encountered while saying: '" + say + "'.", e);
        } finally {
            this.freeToSpeak = true;
            // TODO: handle callback exceptions?
            say.getCallback().run();
        }
    }

    public static void main(final String[] args) {
        System.out.println("Begin");
        final VoiceControl voice = new VoiceControlImpl();
        voice.say("Keep up the good work.", Language.ENGLISH_UK, () -> System.out.println("Done uk"));
        voice.say("Houd het goede werk omhoog.", Language.DUTCH, () -> System.out.println("Done nl"));
        voice.say("Keep up the good work.", Language.ENGLISH_US, () -> {
            System.out.println("Done us");
            System.exit(0);
        });
        System.out.println("End");
    }
}
