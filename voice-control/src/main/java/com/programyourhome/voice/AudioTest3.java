package com.programyourhome.voice;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.data.Buffer;
import net.beadsproject.beads.ugens.Function;
import net.beadsproject.beads.ugens.Gain;
import net.beadsproject.beads.ugens.WavePlayer;

public class AudioTest3 {

	public static void main(String[] args) throws Exception {
		// AudioContext ac = new AudioContext();
		// Gain g = new Gain(ac, 1, 1f);
		//
		//
		// Noise n = new Noise(ac);
		// g.addInput(n);
		//
		// //UGen microphoneIn = ac.getAudioInput();
		// //g.addInput(microphoneIn);
		//
		// g.add
		//
		// RecordToFile rec = new RecordToFile(ac, 1, new File("tryout.wav"));
		// rec.addInput(g);
		// System.out.println("Call chain start");
		// ac.printCallChain();
		// System.out.println("Call chain end");
		// ac.start();
		// try {
		// Thread.sleep(2000);
		// } catch (InterruptedException e) {}
		// rec.kill();
		// ac.stop();

		AudioContext ac;

		ac = new AudioContext();
		/*
		 * In the last example, we used an Envelope to control the frequency of
		 * a WavePlayer.
		 * 
		 * In this example, we'll use another WavePlayer. This is called FM
		 * synthesis.
		 * 
		 * Here's the modulating WavePlayer. It has a low frequency.
		 */
		WavePlayer freqModulator = new WavePlayer(ac, 2, Buffer.SINE);
		/*
		 * The next line might look outrageous if you're not experienced in
		 * Java. Basically we're defining a Function on the fly which takes the
		 * freqModulator and maps it to a sensible range. Since the input to the
		 * function is a sine wave (freqModulator), the output will be a sine
		 * wave that goes from 500 to 700, 50 times a second.
		 */
		Function function = new Function(freqModulator) {
			public float calculate() {
				return x[0] * 100.0f + 600.0f;
			}
		};
		/*
		 * Here's the WavePlayer that will actually play. Now we plug in the
		 * function. Compare this to the previous example, where we plugged in
		 * an envelope.
		 */
		WavePlayer wp = new WavePlayer(ac, function, Buffer.SINE);
		/*
		 * Connect it all together as before.
		 */
		Gain g = new Gain(ac, 1, 0.1f);
		g.addInput(wp);
		ac.out.addInput(g);
		ac.start();
	}

}
