package com.programyourhome.hue;

import java.util.List;

import org.springframework.stereotype.Component;

import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHMessageType;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHHueParsingError;

@Component
public class SDKListener implements PHSDKListener {

    private final PHHueSDK sdk;

    public SDKListener() {
        this.sdk = PHHueSDK.getInstance();
    }

    @Override
    public void onAccessPointsFound(final List<PHAccessPoint> accessPoints) {
        // Handle your bridge search results here. Typically if multiple results are returned you will want to display
        // them in a list and let the user select their bridge. If one is found you may opt to connect automatically to
        // that bridge.
        System.out.println("onAccessPointsFound: " + accessPoints);
    }

    @Override
    public void onAuthenticationRequired(final PHAccessPoint accessPoint) {
        // Arriving here indicates that Pushlinking is required (to prove the User has physical access to the bridge).
        // Typically here you will display a pushlink image (with a timer) indicating to to the user they need to push
        // the button on their bridge within 30 seconds.
        System.out.println("onAuthenticationRequired: " + accessPoint);
    }

    @Override
    public void onBridgeConnected(final PHBridge bridge) {
        System.out.println("onBridgeConnected: " + bridge);
        this.sdk.setSelectedBridge(bridge);
        this.sdk.enableHeartbeat(bridge, PHHueSDK.HB_INTERVAL);
    }

    @Override
    public void onCacheUpdated(final List<Integer> cacheNotificationsList, final PHBridge bridge) {
        // Here you receive notifications that the BridgeResource Cache was updated. Use the PHMessageType to
        // check which cache was updated, e.g.
        if (cacheNotificationsList.contains(PHMessageType.LIGHTS_CACHE_UPDATED)) {
            System.out.println("Lights Cache Updated ");
        }
        System.out.println("onCacheUpdated: " + cacheNotificationsList + ", " + bridge);
    }

    @Override
    public void onConnectionLost(final PHAccessPoint accessPoint) {
        // Bridge disconnected
        System.out.println("onConnectionLost: " + accessPoint);
    }

    @Override
    public void onConnectionResumed(final PHBridge bridge) {
        // Bridge reconnected? I get this event constantly...
        // System.out.println("onConnectionResumed: " + bridge);
    }

    @Override
    public void onError(final int code, final String message) {
        System.out.println("onError: " + code + ", " + message);
    }

    @Override
    public void onParsingErrors(final List<PHHueParsingError> parsingErrorsList) {
        // Any JSON parsing errors are returned here. Typically your program should never return these.
        System.out.println("onParsingErrors: " + parsingErrorsList);
    }

    // TODO: Remove this old code after it's no longer an interesting resource.
    // while (true) {
    // for (PHLight light : bridge.getResourceCache().getLights().values()) {
    // if (new Random().nextDouble() > 0.5) {

    // TODO: Hoe kunnen we living color lights aansturen anders dan met scenes?
    // TODO: source van de jars erbij pakken met JD-Eclipse: http://jd.benow.ca/

    // System.out.println("Update light: " + light.getName());
    // System.out.println("brightness: " + light.getLastKnownLightState().getBrightness());
    // System.out.println("ct: " + light.getLastKnownLightState().getCt());
    // System.out.println("hue: " + light.getLastKnownLightState().getHue());
    // System.out.println("sat: " + light.getLastKnownLightState().getSaturation());
    // System.out.println("color mode: " + light.getLastKnownLightState().getColorMode());
    // PHLightState state = new PHLightState(light.getLastKnownLightState());
    // state.setOn(!state.isOn());
    // state.setColorMode(PHLightColorMode.COLORMODE_HUE_SATURATION);
    // bridge.updateLightState(light, state, new PHLightListener() {
    //
    // public void onSuccess() {
    // System.out.println("onSuccess");
    // }
    // public void onStateUpdate(Hashtable<String, String> arg0,
    // List<PHHueError> arg1) {
    // System.out.println("onStateUpdate");
    // }
    // public void onError(int arg0, String arg1) {
    // System.out.println("onError");
    // System.out.println("arg0: " + arg0);
    // System.out.println("arg1: " + arg1);
    // }
    // @Override
    // public void onReceivingLightDetails(PHLight light) {
    // System.out.println("onReceivingLightDetails");
    // }
    // @Override
    // public void onSearchComplete() {
    // System.out.println("onSearchComplete");
    // }
    // });
    // }
    // try { Thread.sleep(500); } catch (InterruptedException e) {}
    // }
    // }

    // bridge. AllScenes(new PHSceneListener() {
    //
    // public void onSuccess() {
    // // TODO Auto-generated method stub
    //
    // }
    //
    // public void onStateUpdate(Hashtable<String, String> arg0,
    // List<PHHueError> arg1) {
    // // TODO Auto-generated method stub
    //
    // }
    //
    // public void onError(int arg0, String arg1) {
    // // TODO Auto-generated method stub
    //
    // }
    //
    // @Override
    // public void onScenesReceived(List<PHScene> sceneList) {
    // for (PHScene scene : sceneList) {
    // try {
    // System.out.println(scene.getName());
    // bridge.activateScene(scene.getSceneIdentifier(), "1", null);
    // try { Thread.sleep(2000); } catch (InterruptedException e) {}
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // }
    // }
    // });

}
