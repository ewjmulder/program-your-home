package com.programyourhome.server.dailyrhythym;

import java.time.LocalTime;

import org.junit.Assert;
import org.junit.Test;

import com.programyourhome.server.config.model.KeyFrame;

public class RhythymSectionTest {

    @Test
    public void testDoesCrossMidnight() {
        Assert.assertFalse(this.createRhythymSection("05:00", "10:00").doesCrossMidnight());
        Assert.assertFalse(this.createRhythymSection("00:00", "05:00").doesCrossMidnight());
        Assert.assertFalse(this.createRhythymSection("10:00", "23:59").doesCrossMidnight());

        Assert.assertTrue(this.createRhythymSection("10:00", "05:00").doesCrossMidnight());
        Assert.assertTrue(this.createRhythymSection("05:00", "00:00").doesCrossMidnight());
        Assert.assertTrue(this.createRhythymSection("23:59", "10:00").doesCrossMidnight());
    }

    @Test
    public void testGetDurationInSeconds() {
        Assert.assertEquals(60, this.createRhythymSection("00:00", "00:01").getDurationInSeconds());
        Assert.assertEquals(60, this.createRhythymSection("23:59", "00:00").getDurationInSeconds());
    }

    @Test
    public void testContains() {
        Assert.assertTrue(this.createRhythymSection("05:00", "10:00").contains(LocalTime.parse("07:35")));
        Assert.assertTrue(this.createRhythymSection("00:00", "00:01").contains(LocalTime.parse("00:00")));

        Assert.assertFalse(this.createRhythymSection("05:00", "10:00").contains(LocalTime.parse("17:00")));
        Assert.assertFalse(this.createRhythymSection("00:00", "00:01").contains(LocalTime.parse("00:01")));

        Assert.assertTrue(this.createRhythymSection("10:00", "05:00").contains(LocalTime.parse("19:00")));
        Assert.assertTrue(this.createRhythymSection("10:00", "05:00").contains(LocalTime.parse("00:00")));

        Assert.assertFalse(this.createRhythymSection("10:00", "05:00").contains(LocalTime.parse("07:00")));
        Assert.assertFalse(this.createRhythymSection("10:00", "05:00").contains(LocalTime.parse("05:00")));
    }

    @Test
    public void testGetSectionFraction() {
        Assert.assertEquals(0.5, this.createRhythymSection("10:00", "12:00").getFraction(LocalTime.parse("11:00")), 0);
        Assert.assertEquals(0.0, this.createRhythymSection("10:00", "12:00").getFraction(LocalTime.parse("10:00")), 0);
        Assert.assertEquals(0.999, this.createRhythymSection("00:00", "12:00").getFraction(LocalTime.parse("11:59")), 0.001);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetSectionFractionException() {
        this.createRhythymSection("00:00", "10:00").getFraction(LocalTime.parse("12:00"));
    }

    private RhythymSection createRhythymSection(final String fromTime, final String toTime) {
        return new RhythymSection(this.createKeyFrame(fromTime), this.createKeyFrame(toTime));
    }

    private KeyFrame createKeyFrame(final String time) {
        final KeyFrame keyFrame = new KeyFrame();
        keyFrame.setTime(time);
        return keyFrame;
    }

}
