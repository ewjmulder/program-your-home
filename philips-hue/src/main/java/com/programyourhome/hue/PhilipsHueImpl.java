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
import com.philips.lighting.model.PHLightState;
import com.programyourhome.hue.model.Light;
import com.programyourhome.hue.model.LightImpl;
import com.programyourhome.hue.model.LightType;
import com.programyourhome.hue.model.Mood;
import com.programyourhome.hue.model.Plug;
import com.programyourhome.hue.model.PlugImpl;

@Component
public class PhilipsHueImpl implements PhilipsHue, InitializingBean {

    @Autowired
    private SDKListener sdkListener;

    private final PHHueSDK sdk;
    private final PHAccessPoint accessPoint;

    public PhilipsHueImpl() {
        this.sdk = PHHueSDK.getInstance();
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
        // To stop the heartbeat you can use either of the below
        // heartbeatManager.disableLightsHeartbeat(bridge);
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

    private void switchLight(final String lightName, final boolean on) {
        final PHLight phLight = this.getPHLight(lightName);
        final PHLightState lightState = phLight.getLastKnownLightState();
        // TODO: Or do it always anyway, last known state might be out of date?
        if (lightState.isOn() != on) {
            lightState.setOn(on);
            // TODO: use listener, or not at all useful? maybe use 1 general light listener that 'displays' (fires events) upon error/succes/state change?
            this.getBridge().updateLightState(phLight, lightState);
        }
    }

    @Override
    public void turnOnPlug(final String plugName) {
        // TODO Auto-generated method stub

    }

    @Override
    public void turnOffPlug(final String plugName) {
        // TODO Auto-generated method stub

    }

    @Override
    public void dimLight(final String lightName, final double fraction) {
        final PHLight phLight = this.getPHLight(lightName);
        final PHLightState lightState = phLight.getLastKnownLightState();
        // The maximum value accepted is 254 (instead of the 255 suggested by the documentation).
        lightState.setBrightness((int) (fraction * 254));
        this.getBridge().updateLightState(phLight, lightState);
    }

    @Override
    public void setColor(final String lightName, final Color color) {
        // TODO Auto-generated method stub

        /*
         * float xy[] = PHUtilities.calculateXYFromRGB(255, 0, 255, light.getModelNumber());
         * PHLightState lightState = new PHLightState();
         * lightState.setX(xy[0]);
         * lightState.setY(xy[1]);
         * bridge.updateLightState(light, lightState . . .
         */

    }

    @Override
    public void setMood(final String lightName, final Mood mood) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setLight(final String lightName, final double dimFraction, final Color color) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setLight(final String lightName, final double dimFraction, final Mood mood) {
        // TODO Auto-generated method stub

    }

}
