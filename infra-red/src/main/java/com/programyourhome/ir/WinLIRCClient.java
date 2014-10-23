package com.programyourhome.ir;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.springframework.stereotype.Component;

@Component
public class WinLIRCClient {

    // TODO: switch to nio?
    // TODO: proper closing
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public void connect(final String host, final int port) throws IOException {
        this.socket = new Socket(host, port);
        this.out = new PrintWriter(this.socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
    }

    public boolean isConnected() {
        return this.socket != null && this.socket.isConnected();
    }

    public void sendIRCommand(final String remote, final String key) {
        // TODO constants and the sort.
        this.out.println("SEND_ONCE " + remote + " " + key);
        try {
            final String line = this.in.readLine();
            // final String line2 = this.in.readLine();
            // final String line3 = this.in.readLine();
            // final String line4 = this.in.readLine();
            // final String line5 = this.in.readLine();
            // final String line6 = this.in.readLine();
            // final String line7 = this.in.readLine();
            final int i = 5;
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    // TODO: event system for info back: disconnection, other errors, recognized IR signals
}
