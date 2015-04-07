package com.programyourhome.voice.model;

public enum AnswerResultType {

    /** A proper answer was given, meaning it was applicable to the question context. */
    PROPER,
    /** No answer was given, meaning there was only silence or unrecognized sounds. */
    NONE,
    /** Some answer was given, but it was not applicable to the question context. */
    NOT_APPLICABLE,
    /** An answer was given, but it was ambiguous to the question context. This can only happen for questions with a fixed number of possible replies. */
    AMBIGUOUS;

}
