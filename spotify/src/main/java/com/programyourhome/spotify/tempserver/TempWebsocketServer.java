package com.programyourhome.spotify.tempserver;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

public class TempWebsocketServer extends WebSocketServer {

	public static void main(String[] args) throws Exception {
		new TempWebsocketServer().start();
	}
	
	public TempWebsocketServer() throws Exception {
		super(new InetSocketAddress(InetAddress.getByName("127.0.0.1"), 8081));
	}
	
	@Override
	public void onClose(WebSocket webSocket, int arg1, String arg2, boolean arg3) {
		System.out.println("onClose");
	}
	
	@Override
	public void onError(WebSocket arg0, Exception arg1) {
		System.out.println("onError" + arg1);
	}
	
	@Override
	public void onMessage(WebSocket arg0, String arg1) {
	}
	
	@Override
	public void onOpen(WebSocket webSocket, ClientHandshake handshake) {
		System.out.println("onOpen");
		webSocket.send(Command.START + " " + SpotifyUriType.TRACK + " spotify:track:5IMH4b76A0kZJPm9v9g91R");
//		webSocket.send(Command.START + " " + SpotifyUriType.PLAYLIST + " spotify:user:larixlynx:playlist:1vvPRv3eyUsbcKj8kCcwFC");
	}
	
}
