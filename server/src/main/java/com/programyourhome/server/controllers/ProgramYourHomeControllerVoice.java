package com.programyourhome.server.controllers;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.programyourhome.voice.VoiceControl;
import com.programyourhome.voice.builder.ImproperResultPolicy;
import com.programyourhome.voice.builder.JustSayFactory;
import com.programyourhome.voice.builder.QuestionBuilderFactory;
import com.programyourhome.voice.builder.RepeatPolicy;
import com.programyourhome.voice.model.PyhLanguage;

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
        final ImproperResultPolicy improperResultPolicy = new ImproperResultPolicy(3, RepeatPolicy.ALTERNATELY, RepeatPolicy.ALWAYS,
                Arrays.asList("No result comment number 1", "No result comment number 2"),
                Arrays.asList("Not applicable result comment number 1", "Not applicable comment number 2"),
                Arrays.asList("Ambiguous result comment number 1", "Ambiguous result comment number 2"),
                Arrays.asList("Max retries reached comment number 1", "Max retries reached comment number 1"));

        // this.voiceControl.askQuestion(QuestionBuilderFactory.yesNoQuestionBuilder()
        // .text("Do you want to answer this question?")
        // .locale("en-us")
        // .improperResultPolicy(improperResultPolicy)
        // .onNo(JustSayFactory.justSay("Dan niet...", "nl-nl"))
        // .onYes(JustSayFactory.justSay("Vriendelijk bedankt voor uw medewerking.", "nl-nl"))
        // .build());

        this.voiceControl.askQuestion(QuestionBuilderFactory.multipleChoiceQuestionBuilder()
                .text("Who was responsible for conquering half of Europe around 1800?")
                .locale("en-us")
                .improperResultPolicy(improperResultPolicy)
                .possibleAnswer('A', "Mister De Groot", JustSayFactory.justSay("No, no, no...", "en-uk"))
                .possibleAnswer('B', "Napoleon Boneparte", JustSayFactory.justSay("Tu est correct", "fr-fr"))
                .possibleAnswer('Y', "The boy next door", JustSayFactory.justSay("He only wishes", "en-us"))
                .build());
    }
}
