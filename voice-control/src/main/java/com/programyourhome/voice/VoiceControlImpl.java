package com.programyourhome.voice;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import com.programyourhome.voice.config.VoiceControlConfigHolder;
import com.programyourhome.voice.model.AnswerResult;
import com.programyourhome.voice.model.InteractionType;
import com.programyourhome.voice.model.PyhLanguage;
import com.programyourhome.voice.model.PyhLanguageImpl;
import com.programyourhome.voice.model.question.Question;

@Component
@PropertySource("classpath:com/programyourhome/config/voice-control/properties/voice-control.properties")
public class VoiceControlImpl implements VoiceControl {

    private final Log log = LogFactory.getLog(this.getClass());

    @Autowired
    private TaskScheduler questionProcessingScheduler;

    @Autowired
    private VoiceControlConfigHolder configHolder;

    @Autowired
    private TextSpeaker textSpeaker;

    @Autowired
    private AnswerListener answerListener;

    @Value("${questionQueuePoller.interval}")
    private int questionQueuePollerInterval;

    private final Queue<Question<?>> questionQueue;

    public VoiceControlImpl() {
        this.questionQueue = new LinkedList<>();
    }

    @PostConstruct
    public void init() {
        this.questionProcessingScheduler.scheduleWithFixedDelay(this::processNextQuestionInQueue,
                DateUtils.addMilliseconds(new Date(), this.questionQueuePollerInterval), this.questionQueuePollerInterval);
    }

    @Override
    public Collection<PyhLanguage> getSupportedLanguages() {
        return this.configHolder.getConfig().getLanguages().stream()
                .map(language -> new PyhLanguageImpl(language))
                .collect(Collectors.toList());
    }

    @Override
    public void askQuestion(final Question<?> question) {
        this.questionQueue.add(question);
    }

    /**
     * Process the next question in the queue. This method is called constantly to see if there are any new questions
     * to process. We don'n need to use synchronized or some other blocking mechanism, because the task scheduler
     * will not call this method again before the previous execution has finished.
     */
    private void processNextQuestionInQueue() {
        if (!this.questionQueue.isEmpty()) {
            final Question<?> question = this.questionQueue.poll();
            this.doAskQuestion(question);
        }
    }

    private void doAskQuestion(final Question<?> question) {
        this.log.info("Asking question: " + question.asString());
        try {
            this.textSpeaker.say(question.getText(), question.getLocale());
            if (question.getInteractionType() == InteractionType.NONE) {
                question.getAnswerCallback().answer(null);
            } else if (question.getInteractionType() == InteractionType.YES_NO) {
                final AnswerResult<Boolean> answerResult = this.answerListener.listenForYesNo("en-us");
                // TODO: how to prevent this?
                // TODO: handle callback exceptions?
                final Question<?> nextQuestion = ((Question<Boolean>) question).getAnswerCallback().answer(answerResult);
                if (nextQuestion != null) {
                    this.doAskQuestion(nextQuestion);
                }
            }
        } catch (final Exception e) {
            throw new IllegalStateException("Exception encountered while saying: '" + question.getText() + "'.", e);
        }
    }
}
