package com.programyourhome.voice;

import java.util.Collection;

import com.programyourhome.voice.model.PyhLanguage;
import com.programyourhome.voice.model.question.Question;

public interface VoiceControl {

    public Collection<PyhLanguage> getSupportedLanguages();

    public void say(String text, String locale);

    public void askQuestion(Question<?> question);

    // TODO: continuous listening for certain types of sound and providing them to a callback.

}
