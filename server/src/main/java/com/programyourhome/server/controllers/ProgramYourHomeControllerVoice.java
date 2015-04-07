package com.programyourhome.server.controllers;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.programyourhome.voice.VoiceControl;
import com.programyourhome.voice.model.AnswerCallback;
import com.programyourhome.voice.model.AnswerResultType;
import com.programyourhome.voice.model.PyhLanguage;
import com.programyourhome.voice.model.builder.QuestionFactory;
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
        this.voiceControl.askQuestion(QuestionFactory.justSayBuilder()
                .text("This is the beginning")
                .locale("en-us")
                .build());
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
                return answerResult -> {
                    if (answerResult.getAnswerResultType() == AnswerResultType.NONE) {
                        return new JustSay() {
                            @Override
                            public String getText() {
                                return "Don't be shy, please answer the question.";
                            }

                            @Override
                            public String getLocale() {
                                return "en-us";
                            }

                            @Override
                            public void textIsSaid() {
                            }
                        };
                    } else if (answerResult.getAnswerResultType() == AnswerResultType.AMBIGUOUS) {
                        return new JustSay() {
                            @Override
                            public String getText() {
                                return "Please be consistent in your answers!";
                            }

                            @Override
                            public String getLocale() {
                                return "en-uk";
                            }

                            @Override
                            public void textIsSaid() {
                            }
                        };
                    } else if (answerResult.getAnswerResultType() == AnswerResultType.NOT_APPLICABLE) {
                        return new JustSay() {
                            @Override
                            public String getText() {
                                return "I was asking for a yes or a no.";
                            }

                            @Override
                            public String getLocale() {
                                return "en-uk";
                            }

                            @Override
                            public void textIsSaid() {
                            }
                        };
                    } else if (answerResult.getAnswerResultType() == AnswerResultType.PROPER) {
                        return new JustSay() {
                            @Override
                            public String getText() {
                                return "Vriendelijk bedankt voor uw medewerking.";
                            }

                            @Override
                            public String getLocale() {
                                return "nl-be";
                            }

                            @Override
                            public void textIsSaid() {
                            }
                        };
                    }
                    return null;
                };
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
