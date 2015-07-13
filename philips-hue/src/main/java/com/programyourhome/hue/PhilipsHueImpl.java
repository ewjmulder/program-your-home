package com.programyourhome.hue;

import java.awt.Color;
import java.util.Collection;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeResourcesCache;
import com.philips.lighting.model.PHLight;
import com.programyourhome.hue.model.LightType;
import com.programyourhome.hue.model.Mood;
import com.programyourhome.hue.model.PlugImpl;
import com.programyourhome.hue.model.PyhLight;
import com.programyourhome.hue.model.PyhLightImpl;
import com.programyourhome.hue.model.PyhPlug;

@Component
@PropertySource("classpath:com/programyourhome/config/philips-hue/properties/philips-hue.properties")
public class PhilipsHueImpl implements PhilipsHue {

    // TODO: There is a rate limit on the bridge, limiting the amount of requests it can handle / you can send
    // per second / time interval. Probably a good idea to have some safety mechanism to prevent command dropping.
    // For instance: a queue with the command to be executed on it and a small (0.1 sec or smaller) sleep (use times) in
    // between.

    // Man, don't ever, ever! call setters on objects you get directly from the SDK, esp on cache items!! Because you
    // get internal direct object access,
    // you can easily screw up the inner workings and create weird or unexpected behaviour.
    // Best practice: always create a new LightState object!

    // TODO: Document these test results somewhere
    // Test result: after first connection lost, no more HTTP requests are sent!
    // Conclusion: bug/status issue inside PHHueHttpConnectionImpl and not a Bridge issue!
    // But maybe not: question is: are the last requests in wireshark the last requests from the SDK while everything
    // was working or already
    // when a null was received?

    // Java bug?
    // https://www.java.net/node/703177
    // -Djava.net.preferIPv4Stack=true in VMargs
    // Also seems to be Windows Firewall related, try to set all Java exes to allow all traffic

    @Value("${bridge.ip}")
    private String bridgeIp;

    @Value("${api.username}")
    private String username;

    @Autowired
    private SDKListener sdkListener;

    private final PHHueSDK sdk;
    private final PHAccessPoint accessPoint;

    public PhilipsHueImpl() {
        this.sdk = PHHueSDK.getInstance();
        // PHLog.setSdkLogLevel(PHLog.DEBUG);
        this.accessPoint = new PHAccessPoint();
    }

    @PostConstruct
    public void init() {
        // TODO: add to config?
        // TODO: document how to create the user (http://192.168.2.100/debug/clip.html):
        // POST on /api with {"devicetype": "Program Your Home Server","username": "program-your-home"}
        this.accessPoint.setIpAddress(this.bridgeIp);
        this.accessPoint.setUsername(this.username);

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
    public Collection<PyhLight> getLights() {
        return this.getCache().getAllLights().stream()
                .map(phLight -> new PyhLightImpl(phLight))
                .collect(Collectors.toList());
    }

    @Override
    public PyhLight getLight(final int lightId) {
        return new PyhLightImpl(this.getPHLight(lightId));
    }

    @Override
    public Collection<PyhPlug> getPlugs() {
        return this.getLights().stream()
                .filter(light -> light.getType() == LightType.LIVING_WHITES_PLUG)
                .map(light -> new PlugImpl(light))
                .collect(Collectors.toList());
    }

    private PHLight getPHLight(final int lightId) {
        return this.getCache().getAllLights().stream()
                .filter(phLight -> phLight.getIdentifier().equals(Integer.toString(lightId)))
                .findFirst()
                .get();
    }

    @Override
    public void turnOnLight(final int lightId) {
        this.switchLight(lightId, true);
    }

    @Override
    public void turnOffLight(final int lightId) {
        this.switchLight(lightId, false);
    }

    // Note: will now always send a command, but in case no change it will be empty, refactor? -> stop in sending method
    // if new state empty
    // Only update 'on' state when it will actually change something. This might run into race conditions between
    // multiple commands
    // and the heartbeat update frequency. This could be changed to always execute the on/off command if such problems
    // arise.
    // TODO: Do not send anything upon no 'on' change?
    // System.out.println("On/off switch command received for the same state.");
    private void switchLight(final int lightId, final boolean on) {
        this.applyNewState(new PHLightStateBuilder(this.getPHLight(lightId), on));
    }

    @Override
    public void turnOnPlug(final int plugId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void turnOffPlug(final int plugId) {
        // TODO Auto-generated method stub

    }

    private PHLightStateBuilder createBuilder(final int lightId) {
        return new PHLightStateBuilder(this.getPHLight(lightId));
    }

    private void applyNewState(final PHLightStateBuilder builder) {
        this.getBridge().updateLightState(builder.getPHLight(), builder.build(), this.sdkListener);
    }

    @Override
    public void dim(final int lightId, final int dimBasisPoints) {
        this.applyNewState(this.createBuilder(lightId)
                .dim(dimBasisPoints));
    }

    @Override
    public void setColorRGB(final int lightId, final Color color) {
        this.applyNewState(this.createBuilder(lightId)
                .colorRGB(color));
    }

    @Override
    public void setMood(final int lightId, final Mood mood) {
        this.applyNewState(this.createBuilder(lightId)
                .mood(mood));
    }

    @Override
    public void setColorXY(final int lightId, final float x, final float y) {
        this.applyNewState(this.createBuilder(lightId)
                .colorXY(x, y));
    }

    @Override
    public void setColorHueSaturation(final int lightId, final int hueBasisPoints, final int saturationBasisPoints) {
        this.applyNewState(this.createBuilder(lightId)
                .colorHueSaturation(hueBasisPoints, saturationBasisPoints));
    }

    @Override
    public void setColorTemperature(final int lightId, final int temperatureBasisPoints) {
        this.applyNewState(this.createBuilder(lightId)
                .colorTemperature(temperatureBasisPoints));
    }

    @Override
    public void dimToColorRGB(final int lightId, final int dimBasisPoints, final Color color) {
        this.applyNewState(this.createBuilder(lightId)
                .dim(dimBasisPoints)
                .colorRGB(color));
    }

    @Override
    public void dimToColorXY(final int lightId, final int dimBasisPoints, final float x, final float y) {
        this.applyNewState(this.createBuilder(lightId)
                .dim(dimBasisPoints)
                .colorXY(x, y));
    }

    @Override
    public void dimToColorHueSaturation(final int lightId, final int dimBasisPoints, final int hueBasisPoints, final int saturationBasisPoints) {
        this.applyNewState(this.createBuilder(lightId)
                .dim(dimBasisPoints)
                .colorHueSaturation(hueBasisPoints, saturationBasisPoints));
    }

    @Override
    public void dimToColorTemperature(final int lightId, final int dimBasisPoints, final int temperatureBasisPoints) {
        this.applyNewState(this.createBuilder(lightId)
                .dim(dimBasisPoints)
                .colorTemperature(temperatureBasisPoints));
    }

    @Override
    public void dimToMood(final int lightId, final int dimBasisPoints, final Mood mood) {
        this.applyNewState(this.createBuilder(lightId)
                .dim(dimBasisPoints)
                .mood(mood));
    }

}
