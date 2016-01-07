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

import javax.inject.Inject;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import com.programyourhome.ir.LircType;

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

    // TODO: Why not have the type, host and port props in here with @Value?

    private final Log log = LogFactory.getLog(this.getClass());

    @Inject
    @Qualifier("PyhExecutor")
    private TaskScheduler refreshScheduler;

    private LircType lircType;
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

    public void connect(final LircType lircType, final String host, final int port) throws IOException {
        this.lircType = lircType;
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
            this.log.error("IOException while refreshing the WinLIRC remote/key data.", e);
            // TODO: throw event of some sort. The remotes are now empty, is that ok? (I guess, because refreshing did fail so the server has a problem)
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
        return this.sendCommand(COMMAND_LIST + " " + remote.getName()).getData().stream()
                // Difference between lirc and winlirc: lirc has a code before the key name.
                .map(remoteLine -> this.lircType == LircType.WINLIRC ? remoteLine : remoteLine.split(" ")[1])
                .collect(Collectors.toList());
    }

    public void pressRemoteKey(final String remoteName, final String key) {
        if (!this.remotes.containsKey(remoteName)) {
            throw new IllegalArgumentException("Unknown remote: '" + remoteName + "'.");
        }
        if (!this.remotes.get(remoteName).getKeys().contains(key)) {
            throw new IllegalArgumentException("Remote: '" + remoteName + "' does not contain the key: '" + key + "'.");
        }
        try {
            this.sendCommand(COMMAND_SEND + " " + remoteName + " " + key);
        } catch (final IOException e) {
            this.log.error("IOException while sending a key press to WinLIRC.", e);
            // TODO: throw event of some sort. The remotes are now empty, is that ok? (I guess, because refreshing did fail so the server has a problem)
        }
    }

    private synchronized ServerReply sendCommand(final String command) throws IOException {
        this.log.trace("About to send command to WinLIRC server: " + command);
        this.out.println(command);
        final List<String> replyLines = this.readServerReply();
        this.log.trace("WinLIRC server reply:\n" + replyLines);
        final ServerReply reply = ServerReply.parse(replyLines);
        if (!reply.isSuccess()) {
            // TODO: proper error handling
            this.log.error("WinLIRC error received: " + reply.getData());
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
