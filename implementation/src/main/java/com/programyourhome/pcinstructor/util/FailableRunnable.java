package com.programyourhome.pcinstructor.util;

public interface FailableRunnable<T extends Exception> {

    public void run() throws T;

}
