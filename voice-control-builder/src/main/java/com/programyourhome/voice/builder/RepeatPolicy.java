package com.programyourhome.voice.builder;

public enum RepeatPolicy {

    ALWAYS(1),
    ALTERNATELY(2),
    ONE_IN_THREE(3),
    NEVER(Integer.MAX_VALUE);

    private int periodicity;

    private RepeatPolicy(final int periodicity) {
        this.periodicity = periodicity;
    }

    public int getPeriodicity() {
        return this.periodicity;
    }

    public boolean matches(final int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be larger than 0.");
        }
        return amount % this.periodicity == 0;
    }

}
