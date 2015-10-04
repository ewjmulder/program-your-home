package com.programyourhome.bootstrap;

import com.programyourhome.PyhSpringBootApplication;

/**
 * Entry point of the application: starts the server.
 */
public class ProgramYourHome {

    public static void main(final String[] args) {
        // TODO: document why and that this only works when starting in a new JVM.
        System.setProperty("java.net.preferIPv4Stack", "true");
        PyhSpringBootApplication.startApplication();
    }

}