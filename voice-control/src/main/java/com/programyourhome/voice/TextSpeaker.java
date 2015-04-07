package com.programyourhome.voice;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TextSpeaker {

    private static final String ENCODING_UTF8 = "UTF-8";
    private static final String GOOGLE_SPEECH_TTS_URL = "http://translate.google.com/translate_tts?q=%s&tl=%s&ie=%s";

    @Value("${googleSpeechTts.userAgent}")
    private String googleSpeechUserAgent;

    public void say(final String text, final String locale) throws IOException {
        // Create the full url by filling in the text, locale and encoding.
        final String urlString = String.format(GOOGLE_SPEECH_TTS_URL,
                URLEncoder.encode(text, ENCODING_UTF8), locale, ENCODING_UTF8);
        final URLConnection urlConnection = new URL(urlString).openConnection();
        // Set the user agent to a sane value to prevent a 403 response.
        urlConnection.addRequestProperty("User-Agent", this.googleSpeechUserAgent);
        // Input stream of the response is an mp3 stream with the given text as audio.
        final InputStream audioInputStream = new BufferedInputStream(urlConnection.getInputStream());
        try {
            // Play the mp3 using the javazoom library.
            new Player(audioInputStream).play();
        } catch (final JavaLayerException e) {
            throw new IOException("JavaLayerException while playing stream.", e);
        } finally {
            audioInputStream.close();
        }
    }

}
