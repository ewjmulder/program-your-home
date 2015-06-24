package com.programyourhome.server.events;

public interface Poller {

    public long getIntervalInMillis();

    public void poll();

    public default long millis(final long amount) {
        return amount;
    }

    public default long seconds(final long amount) {
        return millis(amount * 1000);
    }

    public default long minutes(final long amount) {
        return seconds(amount * 60);
    }

    public default long hours(final long amount) {
        return minutes(amount * 60);
    }

    public default long days(final long amount) {
        return hours(amount * 24);
    }

}
