package com.programyourhome.voice.detection;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.programyourhome.voice.format.PyhAudioFormat;

@Component
@Scope("prototype")
public class PeakIntervalClapDetector implements ClapDetector {

    // TODO: 80/50/10 works quite nicely, but it can still miss a slightly silent clap that does not produce enough peaks
    // above the 80% mark. Idea: more general analysis of peaks over time to detect typical clapping pattern (see audacity)
    // For instance: take sections of 20 millis, from the moment of the first peak and over 10 of these sections,
    // take the average volume percentage. Of those 10 averages, you should be able to draw the conclusion if it
    // was a clapping sound. -> maybe leave this to when usage of current implementation is not satisfactory
    // IDEA2: have some kind of clap detection interface with different implementations available.

    // Reach this volume to detect a clap peak.
    private final int minClapVolumePercentage = 80;
    // The maximum interval between volume peaks to be considered of the same clap.
    private final int maximumClapPeakIntervalInMillis = 50;
    // Minimum amount of peaks to be considered one clap. This value is here to prevent registering 1 single high volume byte as a clap.
    private final int minimumClapPeaksInOneClap = 10;

    // TODO: proper init (audio stream opened)
    private boolean currentlyInClap = false;
    private int currentClapPeaks = 0;
    private long lastClapStart = 0;
    private long lastClapPeak = 0;

    private int numberOfClapsDetected;

    @Override
    public void audioStreamOpened(final PyhAudioFormat audioFormat) {
        this.currentlyInClap = false;
        this.currentClapPeaks = 0;
        this.lastClapStart = -1;
        this.lastClapPeak = -1;
    }

    @Override
    public void nextFrame(final AudioFrame audioFrame) {
        final int volumePercentage = audioFrame.getVolumePercentage();
        final int millisSinceStart = audioFrame.getMillisSinceStart();

        if (!this.currentlyInClap) {
            System.out.println("Vol: " + volumePercentage);
            if (volumePercentage >= this.minClapVolumePercentage) {
                System.out.println("Start of clap detected: " + millisSinceStart);
                this.currentlyInClap = true;
                // This is the first peak.
                this.currentClapPeaks++;
                this.lastClapStart = millisSinceStart;
                this.lastClapPeak = millisSinceStart;
            }
        } else { // Currently in clap.
            if (volumePercentage >= this.minClapVolumePercentage) {
                final long currentClapPeakIntervalMillis = millisSinceStart - this.lastClapPeak;
                if (currentClapPeakIntervalMillis <= this.maximumClapPeakIntervalInMillis) {
                    this.currentClapPeaks++;
                    this.lastClapPeak = millisSinceStart;
                }
            } else {
                final long sinceLastClapPeakMillis = millisSinceStart - this.lastClapPeak;
                if (sinceLastClapPeakMillis > this.maximumClapPeakIntervalInMillis) {
                    System.out.println("Stop of clap detected: " + millisSinceStart);
                    this.currentlyInClap = false;
                    System.out.println("Clap time: " + (millisSinceStart - this.lastClapStart));
                    System.out.println("Clap peak amount: " + this.currentClapPeaks);
                    if (this.currentClapPeaks >= this.minimumClapPeaksInOneClap) {
                        this.numberOfClapsDetected++;
                    }
                    this.currentClapPeaks = 0;
                }
            }
        }

        // TODO: furthermore: 'around' the clapping should be silence. if there is too much other 'noise', speech gets precedence
    }

    @Override
    public void audioStreamClosed() {
        // No closing logic need.
    }

    @Override
    public boolean isValueDetected() {
        return this.numberOfClapsDetected > 0;
    }

    @Override
    public Integer getDetectedValue() {
        return this.numberOfClapsDetected;
    }

}
