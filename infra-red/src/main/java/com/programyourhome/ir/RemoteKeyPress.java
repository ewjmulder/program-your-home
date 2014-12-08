package com.programyourhome.ir;

public class RemoteKeyPress {

    private final String remoteName;
    private final String keyName;
    private final long delay;
    private long pressedOn;

    public RemoteKeyPress(final String remoteName, final String keyName, final long delay) {
        this.remoteName = remoteName;
        this.keyName = keyName;
        this.delay = delay;
        this.pressedOn = 0;
    }

    public String getRemoteName() {
        return this.remoteName;
    }

    public String getKeyName() {
        return this.keyName;
    }

    public long getDelay() {
        return this.delay;
    }

    public long getPressedOn() {
        return this.pressedOn;
    }

    public void press() {
        if (this.pressedOn > 0) {
            throw new IllegalStateException("This key was already pressed, remote: '" + this.remoteName + "', key: '" + this.keyName + "'.");
        }
        this.pressedOn = System.currentTimeMillis();
    }

}
