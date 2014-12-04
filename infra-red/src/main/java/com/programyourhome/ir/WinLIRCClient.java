package com.programyourhome.ir;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import com.programyourhome.ir.model.PyhRemote;
import com.programyourhome.ir.model.RemoteImpl;
import com.programyourhome.ir.model.ServerReply;

@Component
public class WinLIRCClient {

    // TODO: expose version of WinLIRC
    private static final String COMMAND_VERSION = "VERSION";
    private static final String COMMAND_LIST = "LIST";
    private static final String COMMAND_SEND = "SEND_ONCE";

    // TODO: move to properties of some kind
    private static final long REFRESH_INITIAL_DELAY = 500;
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

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    private String version;
    private final Map<String, RemoteImpl> remotes;
    private final ScheduledExecutorService refreshService;

    public WinLIRCClient() {
        this.remotes = new HashMap<>();
        this.refreshService = Executors.newScheduledThreadPool(1);
    }

    public String getVersion() {
        return this.version;
    }

    public synchronized Collection<RemoteImpl> getRemotes() {
        return this.remotes.values();
    }

    public void connect(final String host, final int port) throws IOException {
        this.socket = new Socket(host, port);
        this.out = new PrintWriter(this.socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        // TODO: Maybe if we facilitate refreshing the remotes with RNA on the WinLIRC app we can replace this?
        this.refreshService.scheduleAtFixedRate(this::refresh, REFRESH_INITIAL_DELAY,
                REFRESH_INTERVAL, TimeUnit.MILLISECONDS);
    }

    private synchronized void refresh() {
        try {
            this.version = this.retrieveVersion();
            this.remotes.clear();
            this.remotes.putAll(this.retrieveRemoteNames().stream()
                    // FIXME: Id's for remotes should be mapped by the device config XML (which is TODO).
                    .map(remoteIdentifier -> new RemoteImpl(1, remoteIdentifier, this.getDevice(remoteIdentifier)))
                    .collect(Collectors.toMap(remote -> remote.getName(), remote -> remote)));
            // Classic for-loop, because the Iterable forEach needs extra exception handling.
            for (final RemoteImpl remote : this.remotes.values()) {
                remote.addAllKeys(this.retrieveKeys(remote));
            }
        } catch (final IOException e) {
            // TODO: throw event of some sort. The remotes are now empty, is that ok? (I guess, because refreshing did fail so the server has a problem)
            e.printStackTrace();
        }
    }

    // TODO: document file pattern FullRemoteIdentification(User Friendly Device Name).conf
    // TODO: No, this should come from the XML config instead!
    private String getDevice(final String remoteName) {
        String deviceName = "<unknown>";
        try {
            final ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            final Resource[] resources = resolver.getResources("classpath*:/remotes-conf/*.conf");
            // TODO: what if not found?
            final String filename = Arrays.stream(resources)
                    .filter(resource -> resource.getFilename().startsWith(remoteName))
                    .findFirst()
                    .get()
                    .getFilename();
            deviceName = filename.substring(filename.indexOf('(') + 1, filename.indexOf(')'));
        } catch (final IOException e) {
            // TODO: throw event of some sort. The remotes are now empty, is that ok? (I guess, because refreshing did fail so the server has a problem)
            e.printStackTrace();
        }
        return deviceName;
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
    private List<String> retrieveKeys(final PyhRemote remote) throws IOException {
        return this.sendCommand(COMMAND_LIST + " " + remote.getName()).getData();
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
            // TODO: throw event of dome sort. The remotes are now empty, is that ok? (I guess, because refreshing did fail so the server has a problem)
            e.printStackTrace();
        }
    }

    private ServerReply sendCommand(final String command) throws IOException {
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
