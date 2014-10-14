package com.programyourhome.spotify.tempserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class TempServer {

	public static void main(String[] args) throws Exception {
		int portNumber = 3737;
	    ServerSocket serverSocket = new ServerSocket(portNumber);
		System.out.println("Creating server socket");
		while (true) {
			try {
				System.out.println("Ready to accept client connection");
			    Socket clientSocket = serverSocket.accept();
				System.out.println("Client connected!");
			    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
			    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			    String line;
			    while ((line = in.readLine()) != null) {
					System.out.println("Line read: '" + line + "'");
			    	// TODO: real server should keep track of state.
			    	if (line.equals("KITCHEN ON")) {
						System.out.println("Turning music on");
			    		out.write("Very well, kitchen will play music!\n");
			    		out.flush();
			    		playKitchenMusic();
			    	} else if (line.equals("KITCHEN OFF")) {
						System.out.println("Turning music off");
			    		out.write("Very well, kitchen will be silent!\n");
			    		out.flush();
			    		silenceKitchen();
			    	} else {
			    		out.write("Not recognized: " + line + "\n");
			    	}
			    }
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static Process p;
	
	private static void playKitchenMusic() {
		String hostName = "192.168.2.47";
		int portNumber = 8765;
		try {
		    Socket socket = new Socket(hostName, portNumber);
		    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			System.out.println("About to send power signal");
		    out.write("SEND_ONCE YAMAHA-RAV338-ZONE2 POWER\n");
		    // Wouldn't work, too fast after previous...
		    //out.write("SEND_ONCE YAMAHA-RAV338-ZONE2 INPUT_AUDIO_2");
		    out.close();
		    in.close();
		    socket.close();

		    // Output music to Speakers
			p = Runtime.getRuntime().exec("nircmd.exe setdefaultsounddevice Speakers");

			// Sleep, so it's clear then receiver turned on first, then the music started.
			try {
				Thread.sleep(3 * 1000);
			} catch (InterruptedException e) {}

		    
		    //TODO: start spotify --> damn, that is hard shit, no API, using browser web player instead
			System.out.println("About to open browser");
			p = Runtime.getRuntime().exec("C:\\Users\\Erik\\AppData\\Local\\Google\\Chrome\\Application\\chrome.exe https://play.spotify.com/radio/user/larixlynx/playlist/1vvPRv3eyUsbcKj8kCcwFC");
//			p = Runtime.getRuntime().exec("C:\\Program Files (x86)\\Mozilla Firefox\\firefox.exe https://play.spotify.com/radio/user/larixlynx/playlist/1vvPRv3eyUsbcKj8kCcwFC");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void silenceKitchen() throws IOException {
	    //TODO: stop spotify -- the easy way. (or not)
		//Doesn't seem to work unfortunately
//		System.out.println("About to stop process, is alive: " + p.isAlive());
//		p.destroyForcibly();
//		System.out.println("Destroy call done");
		// The ruse way then.
		Runtime.getRuntime().exec("taskkill /im chrome.exe /F");

	    // Output music back to Hdmi
		p = Runtime.getRuntime().exec("nircmd.exe setdefaultsounddevice Hdmi");

		// Sleep, so it's clear the music stopped first, then receiver off.
		try {
			Thread.sleep(3 * 1000);
		} catch (InterruptedException e) {}
		
		
		//FIXME: copy-paste!!
		String hostName = "192.168.2.47";
		int portNumber = 8765;
		try {
		    Socket socket = new Socket(hostName, portNumber);
		    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			System.out.println("About to send power signal");
		    out.write("SEND_ONCE YAMAHA-RAV338-ZONE2 POWER\n");
		    out.close();
		    in.close();
		    socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
