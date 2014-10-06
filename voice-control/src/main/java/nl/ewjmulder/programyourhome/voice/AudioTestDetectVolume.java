package nl.ewjmulder.programyourhome.voice;

import java.io.BufferedInputStream;
import java.util.Date;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.TargetDataLine;

public class AudioTestDetectVolume {

	public static void main(String[] args) throws Exception {
		int sampleRate = 16000;
		int sampleSizeInBits = 8;
		int channels = 1;
		boolean signed = true;
		boolean bigEndian = false;
        AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
        
        TargetDataLine line = AudioSystem.getTargetDataLine(format);
        line.open(format);
        line.start();   // start capturing
        System.out.println("Start capturing...");
        AudioInputStream ais = new AudioInputStream(line);
        System.out.println("Start recording...");
        long start = System.currentTimeMillis();
        // Buffer frequency means the buffer size is filled that many times per second. 
        int bufferFrequency = 5;
        int bufferSize = sampleRate / bufferFrequency;
        int threshold = 100;
        BufferedInputStream bis = new BufferedInputStream(ais, bufferSize);
        // TODO: support for 2 byte (16 bit) samples
        // TODO: fail for channels > 1, no use anyway
        int seconds = 500;
        // This amount of millis threshold detected means the next threshold break will count as another peak.
        int minTimeBetweenPeaksInMillis = 200;
        long lastPeakStart = System.currentTimeMillis() - minTimeBetweenPeaksInMillis;
        // How many peaks will make one trigger?
        int numberOfPeaksInTrigger = 3;
        long lastTriggerStart = lastPeakStart;
        int peakCount = 0;
        // What is the maximum time interval for a trigger?
        int maxTimeForTriggerInMillis = 1000;
        byte[] singleByte = new byte[1];
        // TODO: some startup time in which no peaks will be detected because of 'mic going on' noise...
        for (long i = 0; i < (long)sampleRate * (sampleSizeInBits / Byte.SIZE) * channels * seconds; i++) {
//	        byte[] buffer = new byte[bufferSize];
//	        byte max = max(buffer);
//	        byte min = min(buffer);
        	// TODO: use read() and cast to byte or read with buffer?
        	bis.read(singleByte);
	        byte b = singleByte[0];
        	if (b >  threshold || b < -1 * threshold) {
        		long thresholdBreakTime = System.currentTimeMillis();
        		if (thresholdBreakTime > lastPeakStart + minTimeBetweenPeaksInMillis) {
        			System.out.println("Peak!");
        			lastPeakStart = thresholdBreakTime;
        			peakCount++;
        			if (peakCount == 1) {
        				// First peak of a possible new trigger
        				lastTriggerStart = lastPeakStart;
        			} else if (peakCount == numberOfPeaksInTrigger) {
        				if (lastPeakStart < lastTriggerStart + maxTimeForTriggerInMillis) {
        					System.out.println("Trigger!!! How can i help you? :)");
        					peakCount = 0;
        				} else {
	        				// TODO: do not hard reset, but 'soft reset' to time of one peak after last trigger start,
	        				// so you don't have to group the peaks to get a result.
        					System.out.println("Trigger 'timeout'");
        					peakCount = 0;
        				}
        			}
        		}
	        }
        }
        line.stop();
        line.close();
        System.out.println("Done, time: " + (new Date().getTime() - start));
	}
	
	// TODO: use java 8 streams or will that only slow things down?
	// TODO: combine in 1 function that has comparator (>, <) as an argument? (go functional! :)
	// TODO: exception handling?
	private static byte max(byte[] bytes) {
		byte max = Byte.MIN_VALUE;
		for (int i = 0; i < bytes.length; i++) {
			if (bytes[i] > max) {
				max = bytes[i];
			}
		}
		return max;
	}
	private static byte min(byte[] bytes) {
		byte min = Byte.MAX_VALUE;
		for (int i = 0; i < bytes.length; i++) {
			if (bytes[i] < min) {
				min = bytes[i];
			}
		}
		return min;
	}
	
}
