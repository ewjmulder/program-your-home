package com.programyourhome.hue;

import java.awt.Color;
import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeResourcesCache;
import com.philips.lighting.model.PHLight;
import com.programyourhome.hue.model.Light;
import com.programyourhome.hue.model.LightImpl;
import com.programyourhome.hue.model.LightType;
import com.programyourhome.hue.model.Mood;
import com.programyourhome.hue.model.Plug;
import com.programyourhome.hue.model.PlugImpl;

@Component
public class PhilipsHueImpl implements PhilipsHue, InitializingBean {

    // TODO: There is a rate limit on the bridge, limiting the amount of requests it can handle / you can send
    // per second / time interval. Probably a good idea to have some safety mechanism to prevent command dropping.
    // For instance: a queue with the command to be executed on it and a small (0.1 sec or smaller) sleep (use times) in between.

    // Man, don't ever, ever! call setters on objects you get directly from the SDK, esp on cache items!! Because you get internal direct object access,
    // you can easily screw up the inner workings and create weird or unexpected behaviour.
    // Best practice: always create a new LightState object!

    // Problem - solution description
    //
    // When running the Philips Hue SDK with heartbeat, it always failed after a few minutes to max half an hour. Every heartbeat
    // resulted in a connection lost event. The only solution was a reboot of the server.
    //
    // The problem seems to be a combination of Java using IPv6 as default since JDK7, and some Windows configuration
    // preventing IPv6 communication in some cases. This can be because a part of used the software does not support IPv6
    // at all and the connection will fail every time. This is not the case here, since most of the api requests run without problems.
    // Also Windows Firewall, Virus scanners, Routers, etc. can interfere and cause problems. Maybe something like that is going on
    // and after some time an interference happens and subsequent requests in that JVM will fail from that point onwards. Indeed, a reboot
    // of the server fixes the problem. A workaround that works is setting a VM flag that forces the use of IPv4 over IPv6.
    // A possible final solution is in the right setup and configuration of the Windows environment. Possibly removing any firewall
    // rules regarding java executables.
    // Exception that is causing the problem: java.net.SocketException: Permission denied: connect
    // Bug report with comments: https://www.java.net/node/703177
    // JVM arg workaround: -Djava.net.preferIPv4Stack=true

    @Autowired
    private SDKListener sdkListener;

    private final PHHueSDK sdk;
    private final PHAccessPoint accessPoint;

    public PhilipsHueImpl() {
        this.sdk = PHHueSDK.getInstance();
        // Use this to enable SDK debug logging
        // PHLog.setSdkLogLevel(PHLog.DEBUG);
        this.accessPoint = new PHAccessPoint();
        // TODO: add to config?
        // TODO: document how to create the user (http://192.168.2.100/debug/clip.html):
        // POST on /api with {"devicetype": "Program Your Home Server","username": "program-your-home"}
        this.accessPoint.setIpAddress("192.168.2.100");
        this.accessPoint.setUsername("program-your-home");
    }

    @Override
    public void afterPropertiesSet() {
        this.sdk.setAppName("Program Your Home");
        this.sdk.setDeviceName("Program Your Home Server");
        this.sdk.getNotificationManager().registerSDKListener(this.sdkListener);

        this.sdk.connect(this.accessPoint);
        // TODO: proper shutdown, sdk, bridge, heartbeat.
        // heartbeatManager.disableAllHeartbeats(bridge);
    }

    @Override
    public boolean isConnectedToBridge() {
        return this.sdk.isAccessPointConnected(this.accessPoint);
    }

    private PHBridge getBridge() {
        return this.sdk.getSelectedBridge();
    }

    private PHBridgeResourcesCache getCache() {
        return this.getBridge().getResourceCache();
    }

    @Override
    public Collection<Light> getLights() {
        return this.getCache().getAllLights().stream()
                .map(phLight -> new LightImpl(phLight))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Plug> getPlugs() {
        return this.getLights().stream()
                .filter(light -> light.getType() == LightType.LIVING_WHITES_PLUG)
                .map(light -> new PlugImpl(light))
                .collect(Collectors.toList());
    }

    // TODO: error handling when light not found (or trust server side to do that?)
    private PHLight getPHLight(final String lightName) {
        return this.getCache().getAllLights().stream()
                .filter(phLight -> phLight.getName().equals(lightName))
                .findFirst()
                .get();
    }

    @Override
    public void turnOnLight(final String lightName) {
        this.switchLight(lightName, true);
    }

    @Override
    public void turnOffLight(final String lightName) {
        this.switchLight(lightName, false);
    }

    // Note: will now always send a command, but in case no change it will be empty, refactor? -> stop in sending method if new state empty
    // Only update 'on' state when it will actually change something. This might run into race conditions between multiple commands
    // and the heartbeat update frequency. This could be changed to always execute the on/off command if such problems arise.
    // TODO: Do not send anything upon no 'on' change?
    // System.out.println("On/off switch command received for the same state.");
    private void switchLight(final String lightName, final boolean on) {
        this.applyNewState(new PHLightStateBuilder(this.getPHLight(lightName), on));
    }

    @Override
    public void turnOnPlug(final String plugName) {
        // TODO Auto-generated method stub

    }

    @Override
    public void turnOffPlug(final String plugName) {
        // TODO Auto-generated method stub

    }

    private PHLightStateBuilder createBuilder(final String lightName) {
        return new PHLightStateBuilder(this.getPHLight(lightName));
    }

    private void applyNewState(final PHLightStateBuilder builder) {
        this.getBridge().updateLightState(builder.getPHLight(), builder.build(), this.sdkListener);
    }

    @Override
    public void dim(final String lightName, final double dimFraction) {
        this.applyNewState(this.createBuilder(lightName)
                .dim(dimFraction));
    }

    @Override
    public void setColorRGB(final String lightName, final Color color) {
        this.applyNewState(this.createBuilder(lightName)
                .colorRGB(color));
    }

    @Override
    public void setMood(final String lightName, final Mood mood) {
        this.applyNewState(this.createBuilder(lightName)
                .mood(mood));
    }

    @Override
    public void setColorXY(final String lightName, final float x, final float y) {
        this.applyNewState(this.createBuilder(lightName)
                .colorXY(x, y));
    }

    @Override
    public void setColorHueSaturation(final String lightName, final double hueFraction, final double saturationFraction) {
        this.applyNewState(this.createBuilder(lightName)
                .colorHueSaturation(hueFraction, saturationFraction));
    }

    @Override
    public void setColorTemperature(final String lightName, final double temperatureFraction) {
        this.applyNewState(this.createBuilder(lightName)
                .colorTemperature(temperatureFraction));
    }

    @Override
    public void dimToColorRGB(final String lightName, final double dimFraction, final Color color) {
        this.applyNewState(this.createBuilder(lightName)
                .dim(dimFraction)
                .colorRGB(color));
    }

    @Override
    public void dimToColorXY(final String lightName, final double dimFraction, final float x, final float y) {
        this.applyNewState(this.createBuilder(lightName)
                .dim(dimFraction)
                .colorXY(x, y));
    }

    @Override
    public void dimToColorHueSaturation(final String lightName, final double dimFraction, final double hueFraction, final double saturationFraction) {
        this.applyNewState(this.createBuilder(lightName)
                .dim(dimFraction)
                .colorHueSaturation(hueFraction, saturationFraction));
    }

    @Override
    public void dimToColorTemperature(final String lightName, final double dimFraction, final double temperatureFraction) {
        this.applyNewState(this.createBuilder(lightName)
                .dim(dimFraction)
                .colorTemperature(temperatureFraction));
    }

    @Override
    public void dimToMood(final String lightName, final double dimFraction, final Mood mood) {
        this.applyNewState(this.createBuilder(lightName)
                .dim(dimFraction)
                .mood(mood));
    }

}
