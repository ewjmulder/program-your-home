package com.programyourhome.webapp;

import java.io.FileInputStream;
import java.io.IOException;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response.Status;
import fi.iki.elonen.ServerRunner;

public class TestStandalone extends NanoHTTPD {

    private final static String BASE_PATH;
    static {
        String runningInFolder = TestStandalone.class.getProtectionDomain().getCodeSource().getLocation().toString();
        if (System.getProperty("os.name").equals("Linux") || System.getProperty("os.name").equals("Mac OS X")) {
            runningInFolder = "/" + runningInFolder;
        }
        final String uriStyle = runningInFolder.substring(6, runningInFolder.length() - 20) + "src/main/html";
        BASE_PATH = uriStyle.replace("%20", " ");
    }

    public static void main(final String[] args) {
        final TestStandalone server = new TestStandalone(args[0], Integer.parseInt(args[1]));
        ServerRunner.executeInstance(server);
    }

    public TestStandalone(final String host, final int port) {
        super(host, port);
        System.out.println("Host: " + host + ", port: " + port);
    }

    @Override
    public Response serve(final IHTTPSession session) {
        final Method method = session.getMethod();
        String uri = session.getUri();
        System.out.println(method + " '" + uri + "'");
        // Not so subtle way to allow for remote shutdown.
        if (uri.endsWith("shutdown")) {
            System.exit(0);
        }
        // Welcome-files ;-)
        if (uri.equals("/")) {
            uri = "/index.html";
        }

        final FileInputStream dataStream;
        try {
            dataStream = new FileInputStream(BASE_PATH + uri);
        } catch (final IOException e) {
            throw new IllegalStateException(e);
        }

        final Response response = new Response(Status.OK, this.decideMimeType(uri), dataStream);
        final String uriWithoutParams = uri.contains("?") ? uri.substring(0, uri.indexOf('?')) : uri;
        // Add cache header for certain types of files.
        if (uriWithoutParams.endsWith("gif") || uriWithoutParams.endsWith("png") || uriWithoutParams.endsWith("woff")
                || uriWithoutParams.endsWith("woff2") || uriWithoutParams.endsWith("eot") || uriWithoutParams.endsWith("svg")
                || uriWithoutParams.endsWith("otf") || uriWithoutParams.endsWith("ttf")) {
            response.addHeader("Cache-Control", "max-age=3600");
        }
        return response;
    }

    private String decideMimeType(final String uri) {
        final String uriWithoutParams = uri.contains("?") ? uri.substring(0, uri.indexOf('?')) : uri;
        if (uriWithoutParams.endsWith("html")) {
            return "text/html";
        } else if (uriWithoutParams.endsWith("js")) {
            return "application/javascript";
        } else if (uriWithoutParams.endsWith("css")) {
            return "text/css";
        } else if (uriWithoutParams.endsWith("gif")) {
            return "image/gif";
        } else if (uriWithoutParams.endsWith("png")) {
            return "image/png";
        } else if (uriWithoutParams.endsWith("woff")) {
            return "application/font-woff";
        } else if (uriWithoutParams.endsWith("woff2")) {
            return "application/font-woff2";
        } else if (uriWithoutParams.endsWith("eot")) {
            return "application/vnd.ms-fontobject";
        } else if (uriWithoutParams.endsWith("svg")) {
            return "image/svg+xml";
        } else if (uriWithoutParams.endsWith("ttf")) {
            return "application/x-font-ttf";
        } else if (uriWithoutParams.endsWith("otf")) {
            return "application/x-font-otf";
        } else {
            throw new IllegalStateException("Unknown file type: " + uri);
        }
    }

}
