package com.programyourhome.server.events.pollers;

public interface Poller {

    public long getIntervalInMillis();

    public void poll();

}
