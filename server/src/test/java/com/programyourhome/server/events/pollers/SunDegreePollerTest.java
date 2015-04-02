package com.programyourhome.server.events.pollers;

import java.time.LocalDate;
import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import com.programyourhome.sensors.SunDegreeSensor;
import com.programyourhome.server.events.sundegree.SunsetSunriseEvent;
import com.programyourhome.server.events.sundegree.SunDegreeMoment;
import com.programyourhome.server.events.sundegree.SunDegreePoller;
import com.programyourhome.server.events.sundegree.SunriseSunset;

public class SunDegreePollerTest {

    private SunDegreePoller poller;
    private ApplicationEventPublisher eventPublisherMock;
    private SunDegreeSensor sunDegreeSensorMock;

    @Before
    public void init() {
        this.poller = new SunDegreePoller();
        // TODO: is there a better, Springier way to set/autowire these fields?
        this.eventPublisherMock = Mockito.mock(ApplicationEventPublisher.class);
        this.sunDegreeSensorMock = Mockito.mock(SunDegreeSensor.class);
        ReflectionTestUtils.setField(this.poller, "eventPublisher", this.eventPublisherMock);
        ReflectionTestUtils.setField(this.poller, "sunDegreeSensor", this.sunDegreeSensorMock);
    }

    @Test
    public void testPoll() {
        this.poller.poll();
        Mockito.verify(this.eventPublisherMock, Mockito.never()).publishEvent(Matchers.anyObject());

        this.testSpecificEvent(() -> this.sunDegreeSensorMock.isAstronomicalSunrise(Matchers.anyInt()), SunDegreeMoment.ASTRONOMICAL, SunriseSunset.SUNRISE);
        this.testSpecificEvent(() -> this.sunDegreeSensorMock.isNauticalSunrise(Matchers.anyInt()), SunDegreeMoment.NAUTICAL, SunriseSunset.SUNRISE);
        this.testSpecificEvent(() -> this.sunDegreeSensorMock.isCivilSunrise(Matchers.anyInt()), SunDegreeMoment.CIVIL, SunriseSunset.SUNRISE);
        this.testSpecificEvent(() -> this.sunDegreeSensorMock.isOfficialSunrise(Matchers.anyInt()), SunDegreeMoment.OFFICIAL, SunriseSunset.SUNRISE);
        this.testSpecificEvent(() -> this.sunDegreeSensorMock.isOfficialSunset(Matchers.anyInt()), SunDegreeMoment.OFFICIAL, SunriseSunset.SUNSET);
        this.testSpecificEvent(() -> this.sunDegreeSensorMock.isCivilSunset(Matchers.anyInt()), SunDegreeMoment.CIVIL, SunriseSunset.SUNSET);
        this.testSpecificEvent(() -> this.sunDegreeSensorMock.isNauticalSunset(Matchers.anyInt()), SunDegreeMoment.NAUTICAL, SunriseSunset.SUNSET);
        this.testSpecificEvent(() -> this.sunDegreeSensorMock.isAstronomicalSunset(Matchers.anyInt()), SunDegreeMoment.ASTRONOMICAL, SunriseSunset.SUNSET);
    }

    @Test
    public void testDoubleEventPrevention() {
        Mockito.when(this.sunDegreeSensorMock.isOfficialSunrise(Matchers.anyInt())).thenReturn(true);
        this.poller.poll();
        this.poller.poll();
        Mockito.verify(this.eventPublisherMock, Mockito.times(1)).publishEvent(
                new SunsetSunriseEvent(LocalDate.now(), SunDegreeMoment.OFFICIAL, SunriseSunset.SUNRISE));
    }

    private void testSpecificEvent(final Supplier<Boolean> mockMethod, final SunDegreeMoment moment, final SunriseSunset type) {
        Mockito.reset(this.sunDegreeSensorMock, this.eventPublisherMock);
        Mockito.when(mockMethod.get()).thenReturn(true);
        this.poller.poll();
        Mockito.verify(this.eventPublisherMock, Mockito.times(1)).publishEvent(new SunsetSunriseEvent(LocalDate.now(), moment, type));
    }

}
