package com.programyourhome.server.controllers;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.programyourhome.voice.VoiceControl;
import com.programyourhome.voice.model.AnswerCallback;
import com.programyourhome.voice.model.PyhLanguage;
import com.programyourhome.voice.model.question.JustSay;
import com.programyourhome.voice.model.question.YesNoQuestion;

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
        this.voiceControl.askQuestion(new JustSay() {
            @Override
            public String getText() {
                return "This is the beginning.";
            }

            @Override
            public String getLocale() {
                return "en-us";
            }

            @Override
            public void textIsSaid() {
                System.out.println("Text is said is called for the beginning.");
            }
        });
        final YesNoQuestion yesNoQuestion = new YesNoQuestion() {
            @Override
            public String getText() {
                return "Do you want to answer this question?";
            }

            @Override
            public String getLocale() {
                return "en-uk";
            }

            @Override
            public AnswerCallback<Boolean> getAnswerCallback() {
                return answer -> answer != null && answer ? null : this;
            }

            @Override
            public boolean acceptClap() {
                return false;
            }
        };
        this.voiceControl.askQuestion(yesNoQuestion);
        this.voiceControl.askQuestion(new JustSay() {
            @Override
            public String getText() {
                return "This is the end.";
            }

            @Override
            public String getLocale() {
                return "en-us";
            }

            @Override
            public void textIsSaid() {
                System.out.println("Text is said is called for the end.");
            }
        });
    }
}
