package com.programyourhome.voice.builder;

import java.util.List;

import com.programyourhome.voice.model.AnswerResultType;

public class ImproperResultPolicy {

    private final int maxRetries;
    private final RepeatPolicy repeatQuestionPolicy;
    private final RepeatPolicy commentPolicy;
    private final List<String> noResultComments;
    private final List<String> notApplicableResultComments;
    private final List<String> ambiguousResultComments;
    private final List<String> maxRetriesReachedComments;

    public ImproperResultPolicy(final int maxRetries,
            final RepeatPolicy repeatQuestionPolicy, final RepeatPolicy commentPolicy,
            final List<String> noResultComments, final List<String> notApplicableResultComments,
            final List<String> ambiguousResultComments, final List<String> maxRetriesReachedComments) {
        this.maxRetries = maxRetries;
        this.repeatQuestionPolicy = repeatQuestionPolicy;
        this.commentPolicy = commentPolicy;
        this.noResultComments = noResultComments;
        this.notApplicableResultComments = notApplicableResultComments;
        this.ambiguousResultComments = ambiguousResultComments;
        this.maxRetriesReachedComments = maxRetriesReachedComments;
    }

    public int getMaxRetries() {
        return this.maxRetries;
    }

    public RepeatPolicy getRepeatQuestionPolicy() {
        return this.repeatQuestionPolicy;
    }

    public RepeatPolicy getCommentPolicy() {
        return this.commentPolicy;
    }

    public List<String> getNoResultComments() {
        return this.noResultComments;
    }

    public List<String> getNotApplicableResultComments() {
        return this.notApplicableResultComments;
    }

    public List<String> getAmbiguousResultComments() {
        return this.ambiguousResultComments;
    }

    public List<String> getMaxRetriesReachedComments() {
        return this.maxRetriesReachedComments;
    }

    public List<String> getResultComments(final AnswerResultType answerResultType) {
        List<String> resultComments;
        if (answerResultType == AnswerResultType.NONE) {
            resultComments = this.noResultComments;
        } else if (answerResultType == AnswerResultType.NOT_APPLICABLE) {
            resultComments = this.notApplicableResultComments;
        } else if (answerResultType == AnswerResultType.AMBIGUOUS) {
            resultComments = this.ambiguousResultComments;
        } else {
            throw new IllegalArgumentException("No improper answer result type: " + answerResultType);
        }
        return resultComments;
    }
}
