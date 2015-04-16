package com.programyourhome.common.functional;

public interface RunnableWithException<T extends Exception> {

    public void run() throws T;

}
