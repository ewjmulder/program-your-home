package com.programyourhome.voice.builder;

public class QuestionBuilderFactory {

    public static JustSayBuilder justSayBuilder() {
        return new JustSayBuilder();
    }

    public static NumberQuestionBuilder numberQuestionBuilder() {
        return new NumberQuestionBuilder();
    }

    public static YesNoQuestionBuilder yesNoQuestionBuilder() {
        return new YesNoQuestionBuilder();
    }

    public static MultipleChoiceQuestionBuilder multipleChoiceQuestionBuilder() {
        return new MultipleChoiceQuestionBuilder();
    }

    // TODO: document:
    // - lots of fancy trickery going on here. Overdesigned? Yes, for sure, but also for fun. Leave it like
    // a nice example of what you can do with generics
    // - An even nicer solution whould be to tell the compiler that the type parameter QuestionImplType extends QuestionType.
    // unfortunalety, this is not possible in Java: you can only give one type param or one class and several interfaces as extends types.
    // see also: http://chrononaut.org/2008/10/13/fun-or-rather-no-fun-with-generics/

}
