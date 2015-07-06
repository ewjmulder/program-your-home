package com.programyourhome.common.functional;

public interface FailableRunnable<T extends Exception> {

    public void run() throws T;

}
