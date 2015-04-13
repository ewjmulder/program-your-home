package com.programyourhome.voice.builder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;

import com.programyourhome.voice.model.AnswerCallback;
import com.programyourhome.voice.model.AnswerResultType;
import com.programyourhome.voice.model.SpeechMode;
import com.programyourhome.voice.model.question.Question;

public abstract class QuestionImpl<AnswerType> implements Question<AnswerType> {

    private String text;
    private String locale;
    private boolean acceptSpeech;
    private boolean acceptClaps;
    private Function<AnswerType, Boolean> applicableAnswerCallback;

    private SpeechMode speechMode;
    private int timesAsked;
    private final SortedMap<AnswerType, String> possibleAnswers;

    private final Map<AnswerType, Question<?>> nextQuestionsOnProperResult;
    private ProperResultCallback<AnswerType> properResultCallback;

    private final Map<AnswerResultType, ImproperResultConfig> improperConfigurations;
    private ImproperResultPolicy improperResultPolicy;

    private final Random random;

    public QuestionImpl() {
        this.speechMode = SpeechMode.QUESTION_AND_POSSIBLE_ANSWERS;
        this.timesAsked = 0;
        this.possibleAnswers = new TreeMap<>();
        this.nextQuestionsOnProperResult = new HashMap<>();
        this.improperConfigurations = new HashMap<>();
        this.improperConfigurations.put(AnswerResultType.NONE, new ImproperResultConfig());
        this.improperConfigurations.put(AnswerResultType.NOT_APPLICABLE, new ImproperResultConfig());
        this.improperConfigurations.put(AnswerResultType.AMBIGUOUS, new ImproperResultConfig());
        this.random = new Random();
    }

    @Override
    public String getText() {
        return this.text;
    }

    public void setText(final String text) {
        this.text = text;
    }

    @Override
    public String getLocale() {
        return this.locale;
    }

    public void setLocale(final String locale) {
        this.locale = locale;
    }

    @Override
    public boolean acceptSpeechAsAnswer() {
        return this.acceptSpeech;
    }

    public void setAcceptSpeech(final boolean acceptSpeech) {
        this.acceptSpeech = acceptSpeech;
    }

    @Override
    public boolean acceptClapsAsAnswer() {
        return this.acceptClaps;
    }

    public void setAcceptClaps(final boolean acceptClaps) {
        this.acceptClaps = acceptClaps;
    }

    @Override
    public SpeechMode getSpeechMode() {
        return this.speechMode;
    }

    @Override
    public SortedMap<AnswerType, String> getPossibleAnswers() {
        return this.possibleAnswers;
    }

    public void addPossibleAnswer(final AnswerType key, final String answer) {
        this.possibleAnswers.put(key, answer);
    }

    public void addNextQuestionOnProperResult(final AnswerType key, final Question<?> nextQuestion) {
        this.nextQuestionsOnProperResult.put(key, nextQuestion);
    }

    public void setNextQuestionOnNoResult(final Question<?> nextQuestion) {
        this.improperConfigurations.get(AnswerResultType.NONE).setNextQuestion(nextQuestion);
    }

    public void setNextQuestionOnNotApplicableResult(final Question<?> nextQuestion) {
        this.improperConfigurations.get(AnswerResultType.NOT_APPLICABLE).setNextQuestion(nextQuestion);
    }

    public void setNextQuestionOnAmbiguousResult(final Question<?> nextQuestion) {
        this.improperConfigurations.get(AnswerResultType.AMBIGUOUS).setNextQuestion(nextQuestion);
    }

    public void setProperAnswerCallback(final ProperResultCallback<AnswerType> properAnswerCallback) {
        this.properResultCallback = properAnswerCallback;
    }

    public void setNoResultCallback(final Runnable callback) {
        this.improperConfigurations.get(AnswerResultType.NONE).setCallback(callback);
    }

    public void setNotApplicableResultCallback(final Runnable callback) {
        this.improperConfigurations.get(AnswerResultType.NOT_APPLICABLE).setCallback(callback);
    }

    public void setAmbiguousResultCallback(final Runnable callback) {
        this.improperConfigurations.get(AnswerResultType.AMBIGUOUS).setCallback(callback);
    }

    public void setImproperResultPolicy(final ImproperResultPolicy improperResultPolicy) {
        this.improperResultPolicy = improperResultPolicy;
    }

    public void setApplicableAnswerCallback(final Function<AnswerType, Boolean> applicableAnswerCallback) {
        this.applicableAnswerCallback = applicableAnswerCallback;
    }

    @Override
    public boolean isApplicableAnswer(final AnswerType answer) {
        boolean isApplicable;
        if (this.applicableAnswerCallback != null) {
            isApplicable = this.applicableAnswerCallback.apply(answer);
        } else {
            isApplicable = this.isApplicableAnswerDefault(answer);
        }
        return isApplicable;
    }

    protected abstract boolean isApplicableAnswerDefault(final AnswerType answer);

    @Override
    public AnswerCallback<AnswerType> getAnswerCallback() {
        return answerResult -> {
            this.timesAsked++;
            Question<?> nextQuestion = null;

            // TODO: handle callback exceptions?
            if (answerResult == null) {
                // For just say questions, the answer result is null, so we need specific handling for that.
                // Also the next question (if provided) is saved in the map with a fixed key value.
                nextQuestion = this.nextQuestionsOnProperResult.get(JustSayImpl.PROPER_RESULT_KEY);
            } else {
                if (answerResult.getAnswerResultType() == AnswerResultType.PROPER) {
                    if (this.properResultCallback != null) {
                        this.properResultCallback.answer(answerResult.getAnswer());
                    }
                    nextQuestion = this.nextQuestionsOnProperResult.get(answerResult.getAnswer());
                } else {
                    return this.processImproperResultConfig(answerResult.getAnswerResultType());
                }
            }

            return nextQuestion;
        };
    }

    private Question<?> processImproperResultConfig(final AnswerResultType answerResultType) {
        final ImproperResultConfig improperResultConfig = this.improperConfigurations.get(answerResultType);
        Question<?> nextQuestion;
        if (improperResultConfig.hasCallback()) {
            improperResultConfig.getCallback().run();
        }
        if (improperResultConfig.hasNextQuestion()) {
            nextQuestion = improperResultConfig.getNextQuestion();
        } else if (this.improperResultPolicy != null) {
            if (this.timesAsked == this.improperResultPolicy.getMaxRetries()) {
                final List<String> maxRetriesComments = this.improperResultPolicy.getMaxRetriesReachedComments();
                final String comment = maxRetriesComments.get(this.random.nextInt(maxRetriesComments.size()));
                // Max retries reached, stop repeating.
                nextQuestion = JustSayFactory.justSay(comment, this.locale);
            } else {
                final RepeatPolicy repeatQuestionPolicy = this.improperResultPolicy.getRepeatQuestionPolicy();
                this.speechMode = repeatQuestionPolicy.matches(this.timesAsked) ? SpeechMode.QUESTION_AND_POSSIBLE_ANSWERS : SpeechMode.NONE;
                final RepeatPolicy commentPolicy = this.improperResultPolicy.getCommentPolicy();
                // Comment repeats should be out of sync with question repeats (so + 1), so twice alternate will always give some feedback.
                if (commentPolicy.matches(this.timesAsked + 1)) {
                    final List<String> resultComments = this.improperResultPolicy.getResultComments(answerResultType);
                    final String comment = resultComments.get(this.random.nextInt(resultComments.size()));
                    nextQuestion = JustSayFactory.justSayWithNextQuestion(comment, this.locale, this);
                } else {
                    nextQuestion = this;
                }
            }
        } else {
            // No specific next question and no policy, so default to no next question.
            nextQuestion = null;
        }
        return nextQuestion;
    }
}
