package com.programyourhome.voice.model.builder;

import java.util.ArrayList;
import java.util.List;

import com.programyourhome.voice.model.AnswerCallback;
import com.programyourhome.voice.model.question.JustSay;
import com.programyourhome.voice.model.question.Question;

public class QuestionFactory {

    private static final AnswerCallback<?> EMPTY_CALLBACK = type -> {
        return null;
    };

    public static JustSayBuilder justSayBuilder() {
        return new JustSayBuilder();
    }

    // TODO: probably better to get these classes out of here and move to package scope maybe? (to prevent exposure in API module)

    private static abstract class QuestionImpl<AnswerType> implements Question<AnswerType> {

        private String text;
        private String locale;
        @SuppressWarnings("unchecked")
        private AnswerCallback<AnswerType> answerCallback = (AnswerCallback<AnswerType>) EMPTY_CALLBACK;
        private final List<String> possibleAnswers = new ArrayList<>();

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
        public List<String> getPossibleAnswers() {
            return this.possibleAnswers;
        }

        public void addPossibleAnswer(final String answer) {
            this.possibleAnswers.add(answer);
        }

        @Override
        public AnswerCallback<AnswerType> getAnswerCallback() {
            return this.answerCallback;
        }

        public void setAnswerCallback(final AnswerCallback<AnswerType> answerCallback) {
            this.answerCallback = answerCallback;
        }

    }

    // TODO: document:
    // - lots of fancy trickery going on here. Overdesigned? Yes, for sure, but also for fun. Leave it like
    // a nice example of what you can do with generics
    // - An even nicer solution whould be to tell the compiler that the type parameter QuestionImplType extends QuestionType.
    // unfortunalety, this is not possible in Java: you can only give one type param or one class and several interfaces as extends types.
    // see also: http://chrononaut.org/2008/10/13/fun-or-rather-no-fun-with-generics/
    private static abstract class QuestionBuilder<BuilderType extends QuestionBuilder<?, ?, ?>, QuestionImplType extends QuestionImpl<?>, QuestionType extends Question<?>> {

        public BuilderType text(final String text) {
            this.getQuestion().setText(text);
            return this.getBuilder();
        }

        public BuilderType locale(final String locale) {
            this.getQuestion().setLocale(locale);
            return this.getBuilder();
        }

        @SuppressWarnings("unchecked")
        public QuestionType build() {
            return (QuestionType) this.getQuestion();
        }

        protected abstract BuilderType getBuilder();

        protected abstract QuestionImplType getQuestion();

    }

    private static class JustSayImpl extends QuestionImpl<Void> implements JustSay {

        @Override
        public void textIsSaid() {
            System.out.println("Text is said is called");
        }

    }

    public static class JustSayBuilder extends QuestionBuilder<JustSayBuilder, JustSayImpl, JustSay> {

        private final JustSayImpl justSay;

        private JustSayBuilder() {
            this.justSay = new JustSayImpl();
        }

        @Override
        protected JustSayBuilder getBuilder() {
            return this;
        }

        @Override
        protected JustSayImpl getQuestion() {
            return this.justSay;
        }

    }
}
