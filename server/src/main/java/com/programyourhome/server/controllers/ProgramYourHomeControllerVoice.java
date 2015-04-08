package com.programyourhome.server.controllers;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.programyourhome.voice.VoiceControl;
import com.programyourhome.voice.model.PyhLanguage;
import com.programyourhome.voice.model.builder.JustSayFactory;
import com.programyourhome.voice.model.builder.QuestionBuilderFactory;

@RestController
@RequestMapping("voice")
public class ProgramYourHomeControllerVoice extends AbstractProgramYourHomeController {

    @Autowired
    private VoiceControl voiceControl;

    @RequestMapping("languages")
    public Collection<PyhLanguage> getLanguage() {
        return this.voiceControl.getSupportedLanguages();
    }

    @RequestMapping("test")
    public void doTest() {
        this.voiceControl.askQuestion(JustSayFactory.justSay("This is the beginning.", "en-us"));

        this.voiceControl.askQuestion(QuestionBuilderFactory.yesNoQuestionBuilder()
                .text("Do you want to answer this question?")
                .locale("en-uk")
                .onNoResult(JustSayFactory.justSay("Don't be shy, please answer the question.", "en-us"))
                .noResultCallback(() -> System.out.println("No result"))
                .onNotApplicableResult(JustSayFactory.justSay("I was asking for a yes or a no!", "en-us"))
                .noResultCallback(() -> System.out.println("Not applicable result"))
                .onAmbiguousResult(JustSayFactory.justSay("Please be consistent in your answers!", "en-uk"))
                .noResultCallback(() -> System.out.println("Ambiguous result"))
                .onNo(JustSayFactory.justSay("Dan niet...", "nl-nl"))
                .onYes(JustSayFactory.justSay("Vriendelijk bedankt voor uw medewerking.", "nl-nl"))
                .build());

        this.voiceControl.askQuestion(JustSayFactory.justSay("This is the end.", "en-us"));
    }
}
