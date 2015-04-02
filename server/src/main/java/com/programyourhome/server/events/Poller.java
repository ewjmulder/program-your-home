package com.programyourhome.server.events;

public interface Poller {

    public long getIntervalInMillis();

    public void poll();

}
