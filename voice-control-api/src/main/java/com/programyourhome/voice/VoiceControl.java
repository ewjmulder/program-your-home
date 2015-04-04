package com.programyourhome.voice;

import com.programyourhome.voice.model.AudioInteraction;
import com.programyourhome.voice.model.Language;

public interface VoiceControl {

    /**
     * @see say, no callback
     */
    public void say(String text, Language language);

    /**
     * Speak out loud the given text in the given language.
     * This method will return immediately.
     * It will say the text as soon as possible, after any other text still to be said.
     *
     * @param text the text to say
     * @param language the language to use
     * @param callback the callback to call when the saying is done
     */
    public void say(String text, Language language, Runnable callback);

    // TODO: ask question: implies other speech will be postponed until answer is given.

    public void performAudioInteraction(AudioInteraction audioInteraction);

    // TODO: continuous listening for certain types of sound and providing them to a callback.

}
