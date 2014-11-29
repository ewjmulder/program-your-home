package com.programyourhome.hue;

import java.awt.Color;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.bridge.impl.PHBridgeResourcesCacheImpl;
import com.philips.lighting.hue.sdk.connection.impl.PHHueHttpConnection;
import com.philips.lighting.hue.sdk.heartbeat.PHHeartbeatManager;
import com.philips.lighting.hue.sdk.heartbeat.PHHeartbeatProcessor;
import com.philips.lighting.hue.sdk.heartbeat.PHHeartbeatTimer;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeConfiguration;
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

    @Autowired
    private SDKListener sdkListener;

    private final PHHueSDK sdk;
    private final PHAccessPoint accessPoint;

    public PhilipsHueImpl() {
        this.sdk = PHHueSDK.getInstance();
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

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(500);
                        final PHHeartbeatManager heartbeatManager = PHHeartbeatManager.getInstance();
                        final Field hbTimerField = heartbeatManager.getClass().getDeclaredField("hbTimer");
                        hbTimerField.setAccessible(true);
                        final PHHeartbeatTimer heartbeatTimer = (PHHeartbeatTimer) hbTimerField.get(null);
                        final Field mapField = heartbeatTimer.getClass().getDeclaredField("heartbeatMap");
                        mapField.setAccessible(true);
                        final HashMap map = (HashMap) mapField.get(heartbeatTimer);
                        final PHHeartbeatProcessor heartbeatProcessor = (PHHeartbeatProcessor) ((ArrayList) map.values().iterator().next()).get(0);
                        final Field retryField = heartbeatProcessor.getClass().getDeclaredField("currentTry");
                        retryField.setAccessible(true);
                        final int retryValue = retryField.getInt(heartbeatProcessor);
                        final Field resumeField = heartbeatProcessor.getClass().getDeclaredField("notifyConnectionResume");
                        resumeField.setAccessible(true);
                        final boolean resumeValue = resumeField.getBoolean(heartbeatProcessor);

                        final PHBridgeResourcesCacheImpl cacheImpl = (PHBridgeResourcesCacheImpl) PhilipsHueImpl.this.getBridge().getResourceCache();
                        final PHBridgeConfiguration bridgeConfig = cacheImpl.getBridgeConfiguration();

                        final String ipAddress = bridgeConfig.getIpAddress();
                        final String username = bridgeConfig.getUsername();
                        final String url = "http://" + ipAddress + "/api/" + username + "/lights";

                        final PHHueHttpConnection httpConnection = new PHHueHttpConnection();
                        final String data = resumeValue ? "No-get-on-status-ok" : httpConnection.getData(url);

                        // Test result: after first connection lost, no more HTTP requests are sent!
                        // Conclusion: bug/status issue inside PHHueHttpConnectionImpl and not a Bridge issue!
                        // But maybe not: question is: are the last requests in wireshark the last requests from the SDK while everything was working or already
                        // when a null was received?

                        // Java bug?
                        // https://www.java.net/node/703177
                        // -Djava.net.preferIPv4Stack=true in VMargs
                        // Also seems to be Windows Firewall related, try to set all Java exes to allow all traffic

                        // System.out.println("Retry: " + retryValue + ", resume: " + resumeValue + ", IP address: " + ipAddress
                        // + ", username: " + username + ", data: " + data);
                        // System.out.println("lostCount: " + PhilipsHueImpl.this.sdkListener.lostCount);

                    } catch (final Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
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
