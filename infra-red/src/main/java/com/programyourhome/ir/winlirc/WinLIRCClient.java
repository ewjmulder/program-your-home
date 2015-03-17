package com.programyourhome.ir.winlirc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

@Component
public class WinLIRCClient {

    // TODO: expose version of WinLIRC
    private static final String COMMAND_VERSION = "VERSION";
    private static final String COMMAND_LIST = "LIST";
    private static final String COMMAND_SEND = "SEND_ONCE";

    // TODO: move to properties of some kind
    // TODO: rewrite to class duration or so?
    private static final int REFRESH_INITIAL_DELAY = 500;
    private static final long REFRESH_INTERVAL = 5 * 60 * 1000;

    // TODO: switch to nio?
    // TODO: proper closing
    // TODO: where to catch the possible IOExceptions? Inside this class probably best and make it some kind of error event.
    // TODO: in that respect Async is very nice: give listener and wait for wither success or error event to happen and handle instead of misusing the return
    // type somehow
    // TODO: use hot and port for an auto-reconnect service

    // TODO: When the config is refreshed on the serverside, the client gets a notification on the socket!
    // So probably, no manual refresh service needed, just listen to these incoming messages. Only problem is how to distinguish normal replies from this
    // The command received is: BEGIN \n SIGHUP \n END.
    // TODO: Handle this properly somehow. Maybe test for this when receiving a reply to a command and if so, first refresh, then continue with reading
    // actual response to command (which may be an error, because of the refesh) Hmm, another idea is to keep the refresh loop, but instead of
    // bluntly reloading all data, first check if there is unread data on the line (in.ready()) and if so, see if that is the sighup signal. If so, do the
    // actual refresh
    // If not sighup, print the received data and quit, since that is an unknown state atm. If no data ready to be read, that refresh action is done.

    @Autowired
    private TaskScheduler refreshScheduler;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    private String version;
    private final Map<String, WinLIRCRemote> remotes;

    public WinLIRCClient() {
        this.remotes = new HashMap<>();
    }

    public String getVersion() {
        return this.version;
    }

    public synchronized Collection<WinLIRCRemote> getRemotes() {
        return this.remotes.values();
    }

    public void connect(final String host, final int port) throws IOException {
        this.socket = new Socket(host, port);
        this.out = new PrintWriter(this.socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        // TODO: Maybe if we facilitate refreshing the remotes with RNA on the WinLIRC app we can replace this?
        this.refreshScheduler.scheduleAtFixedRate(this::refresh, DateUtils.addMilliseconds(new Date(), REFRESH_INITIAL_DELAY), REFRESH_INTERVAL);
    }

    private synchronized void refresh() {
        try {
            this.version = this.retrieveVersion();
            this.remotes.clear();
            this.remotes.putAll(this.retrieveRemoteNames().stream()
                    .map(remoteIdentifier -> new WinLIRCRemote(remoteIdentifier))
                    .collect(Collectors.toMap(remote -> remote.getName(), remote -> remote)));
            // Classic for-loop, because the Iterable forEach needs extra exception handling.
            for (final WinLIRCRemote remote : this.remotes.values()) {
                remote.addAllKeys(this.retrieveKeys(remote));
            }
        } catch (final IOException e) {
            // TODO: throw event of some sort. The remotes are now empty, is that ok? (I guess, because refreshing did fail so the server has a problem)
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return this.socket != null && this.socket.isConnected();
    }

    private String retrieveVersion() throws IOException {
        return this.sendCommand(COMMAND_VERSION).getData().get(0);
    }

    // TODO: validation of these values against the internal config
    private List<String> retrieveRemoteNames() throws IOException {
        return this.sendCommand(COMMAND_LIST).getData();
    }

    // TODO: validation of these values against the internal config
    private List<String> retrieveKeys(final WinLIRCRemote remote) throws IOException {
        return this.sendCommand(COMMAND_LIST + " " + remote.getName()).getData();
    }

    public void pressRemoteKey(final String remoteName, final String key) {
        if (!this.remotes.containsKey(remoteName)) {
            System.out.println("Unknown remote: '" + remoteName + "'.");
            throw new IllegalArgumentException("Unknown remote: '" + remoteName + "'.");
        }
        if (!this.remotes.get(remoteName).getKeys().contains(key)) {
            System.out.println("Remote: '" + remoteName + "' does not contain the key: '" + key + "'.");
            throw new IllegalArgumentException("Remote: '" + remoteName + "' does not contain the key: '" + key + "'.");
        }
        try {
            System.out.println("About to send command: " + remoteName + "->" + key);
            this.sendCommand(COMMAND_SEND + " " + remoteName + " " + key);
        } catch (final IOException e) {
            // TODO: throw event of some sort. The remotes are now empty, is that ok? (I guess, because refreshing did fail so the server has a problem)
            e.printStackTrace();
        }
    }

    private synchronized ServerReply sendCommand(final String command) throws IOException {
        this.out.println(command);
        final ServerReply reply = ServerReply.parse(this.readServerReply());
        if (!reply.isSuccess()) {
            // TODO: proper error handling
            System.out.println("WinLIRC error: " + reply.getData());
        }
        return reply;
    }

    private List<String> readServerReply() throws IOException {
        final List<String> replyLines = new ArrayList<>();
        String replyLine;
        // TODO: handle EOF (end of stream, null reply)
        while ((replyLine = this.in.readLine()) != null) {
            replyLines.add(replyLine);
            if (replyLine.equals(ServerReply.REPLY_END)) {
                break;
            }
        }
        return replyLines;
    }
    // TODO: event system for info back: disconnection, other errors, recognized IR signals
}
