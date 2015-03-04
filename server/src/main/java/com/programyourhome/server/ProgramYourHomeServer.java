package com.programyourhome.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

import com.programyourhome.ComponentScanBase;

@EnableAutoConfiguration
@ComponentScan(basePackageClasses = ComponentScanBase.class)
@PropertySource("classpath:com/programyourhome/config/server/properties/server.properties")
public class ProgramYourHomeServer {

    private static boolean shutdown = false;

    public static void startServer() {
        final ApplicationContext springBootContext = SpringApplication.run(ProgramYourHomeServer.class, new String[0]);
        // Start a shutdown poller for graceful shutdown when needed.
        new ShutdownPoller(springBootContext).start();
    }

    public static void stopServer() {
        // Trigger the shutdown poller, so the actual shutting down is done outside of the Spring context and threads.
        shutdown = true;
    }

    /**
     * This shutdown poller will wait until the shutdown flag is set to true and then trigger
     * a graceful Spring Boot shutdown and exit the java process after that.
     */
    private static class ShutdownPoller extends Thread {
        private final ApplicationContext springBootContext;

        public ShutdownPoller(final ApplicationContext springBootContext) {
            super("Shutdown poller");
            this.springBootContext = springBootContext;
        }

        @Override
        public void run() {
            while (!shutdown) {
                try {
                    Thread.sleep(100);
                } catch (final InterruptedException e) {
                }
            }
            System.exit(SpringApplication.exit(this.springBootContext, () -> 0));
        }
    }

}
