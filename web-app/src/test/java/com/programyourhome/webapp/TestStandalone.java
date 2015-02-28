package com.programyourhome.webapp;

import java.io.FileInputStream;
import java.io.IOException;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response.Status;
import fi.iki.elonen.ServerRunner;

public class TestStandalone extends NanoHTTPD {

    private final static String BASE_PATH;
    static {
        final String runningInFolder = TestStandalone.class.getProtectionDomain().getCodeSource().getLocation().toString();
        final String uriStyle = runningInFolder.substring(6, runningInFolder.length() - 20) + "src/main/html";
        BASE_PATH = uriStyle.replace("%20", " ");
    }

    public static void main(final String[] args) {
        final TestStandalone server = new TestStandalone();
        ServerRunner.executeInstance(server);
    }

    public TestStandalone() {
        super("localhost", 8080);
    }

    @Override
    public Response serve(final IHTTPSession session) {
        final Method method = session.getMethod();
        final String uri = session.getUri();
        System.out.println(method + " '" + uri + "'");

        final FileInputStream dataStream;
        try {
            dataStream = new FileInputStream(BASE_PATH + uri);
        } catch (final IOException e) {
            throw new IllegalStateException(e);
        }

        return new NanoHTTPD.Response(Status.OK, this.decideMimeType(uri), dataStream);
    }

    private String decideMimeType(final String uri) {
        if (uri.endsWith("html")) {
            return "text/html";
        } else if (uri.endsWith("js")) {
            return "application/javascript";
        } else if (uri.endsWith("css")) {
            return "text/css";
        } else if (uri.endsWith("gif")) {
            return "image/gif";
        } else {
            throw new IllegalStateException("Unknown file type: " + uri);
        }
    }

}
