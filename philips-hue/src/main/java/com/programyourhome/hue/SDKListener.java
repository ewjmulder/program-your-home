package com.programyourhome.hue;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.philips.lighting.hue.listener.PHLightListener;
import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.hue.sdk.heartbeat.PHHeartbeatManager;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeResource;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHHueParsingError;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

@Component
public class SDKListener implements PHSDKListener, PHLightListener {

    // TODO: More deep diving into event stuff in SDK?
    // Conclusions / ideas / thoughts:
    // - OnCachechanged event only if actual changes in lights / state -> no, also on local changes, but only if no errors occured!
    // - Should only happen when other actor is acting on lights -> no!
    // - Own modifications are directly saved in the state -> yes, but also events happen.
    // - Maybe a race condition in updating the state and doing a heartbeat will result in a onCacheUpdate -> maybe
    // - Or the get of the API actually returns the old state, while the cache is already updated with the new state -> hmm, that would be a big conflict / bug
    // --> This actually indeed happens, but only for values not set in the last command, they will be updated later on. For on/off it even seems to be a little
    // more unreliable
    // - So: still use oncacheupdate to send update events to the PYH server, but not useful 'locally' so far -> indeed, but also a bit useful for
    // local changes, because it actually confirms the success of the local command
    // - Open question: why do we get a lot of onCacheUpdate events when we set a scene, even after a few seconds: -> slowly dropping in the changes,
    // bridge/lamp hardware related?
    // transition time? or maybe color values 'syncing'? -> the last indeed, and even on/off 'settling'
    // - TODO test: Print the entire lights+state cache list state upon receiving a cache update event and find the changes manually to see why these event
    // occur
    // - TODO test: Print on state 20 times a second (like existing Thread in comment) and change state with Android app to see when it is picked up

    // Test result: event should always be fired from local change, but 1 param error will prevent that. In this case, it was the reachable param
    // that cannot be set from the outside that prevented the onCacheUpdate, because the notifyBridgeAPIError code path stops further processing.
    // TODO: this really is more of a bug than a feature. Try to find a way to contact the Philips Java API developers and file some bug reports.

    // Correction by lamp done on setting a value, setting the correction value gives another correction, but always the same values
    // example: set Color.RED = x: 0,674 y: 0,322 -> correction to x: 0.6736, y: 0,3221 setting those gives correction to: x: 0.6725, y: 0,3223
    // Setting those last corrected values gives the same values back, so no correction.
    // Second example: Color.BLUE = x: 0.168, y: 0.041 -> correction to x: 0,1684, y: 0,0416 setting those gives correction to x: 0.1684, y: 0.0417
    // Setting those gives another correction to: x: 0.1692, y: 0.0429, followed by another correction: x: 0.1693, y: 0.043 -> x: 0.1701, y: 0.0443
    // After that finally stays the same (but hue is corrected from 47127 to 47126 :)
    // Setting hue/sat directly does not result in a hue/sat correction, only updated values for other color params (if needed)
    // Color temp settings are corrected, e.g. READ = 343 is corrected to 340 -> corrected to 337 -> 334 -> 331 -> 328
    // Another example 499 -> 497 -> 492 -> 488 (always the same)
    // Seems to be the same for every lamp for all params (a few 'steekproeven': for 2 lamps exacty the same resuls!)

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

    private PHLightState stateBeforeEvent1;
    private PHLightState stateBeforeEvent2;
    private PHLightState stateBeforeEvent3;
    private PHLightState stateBeforeEvent4;
    private PHLightState stateBeforeEvent5;
    private PHLightState stateBeforeEvent6;
    private PHLightState stateBeforeEvent7;
    private PHLightState stateBeforeEvent8;

    @Override
    public void onBridgeConnected(final PHBridge bridge) {
        System.out.println("onBridgeConnected: " + bridge);
        this.sdk.setSelectedBridge(bridge);
        // TODO: Add other heartbeats when / if necessary
        // TODO: check everyhue.com forum for replies on javadoc and error message bug report
        // TODO: Pick nice value for lights heartbeat (250 seems ok, 4 timer per second updae should be enough)
        final PHHeartbeatManager heartbeatManager = PHHeartbeatManager.getInstance();
        heartbeatManager.enableLightsHeartbeat(bridge, 100);
        // this.sdk.enableHeartbeat(bridge, PHHueSDK.HB_INTERVAL);

        this.stateBeforeEvent1 = new PHLightState(bridge.getResourceCache().getLights().get("1").getLastKnownLightState());
        this.stateBeforeEvent2 = new PHLightState(bridge.getResourceCache().getLights().get("2").getLastKnownLightState());
        this.stateBeforeEvent3 = new PHLightState(bridge.getResourceCache().getLights().get("3").getLastKnownLightState());
        this.stateBeforeEvent4 = new PHLightState(bridge.getResourceCache().getLights().get("4").getLastKnownLightState());
        this.stateBeforeEvent5 = new PHLightState(bridge.getResourceCache().getLights().get("5").getLastKnownLightState());
        this.stateBeforeEvent6 = new PHLightState(bridge.getResourceCache().getLights().get("6").getLastKnownLightState());
        this.stateBeforeEvent7 = new PHLightState(bridge.getResourceCache().getLights().get("7").getLastKnownLightState());
        this.stateBeforeEvent8 = new PHLightState(bridge.getResourceCache().getLights().get("8").getLastKnownLightState());

        this.bridge = bridge;

        // This shows the cache state is actually successfully updated, even though the cacheupdate heartbeat events are coming in much slower
        // or not at all?
        // new Thread(new Runnable() {
        // @Override
        // public void run() {
        // while (true) {
        // final boolean on = SDKListener.this.sdk.getSelectedBridge().getResourceCache().getLights().get("3").getLastKnownLightState().isOn();
        // final int bri = SDKListener.this.sdk.getSelectedBridge().getResourceCache().getLights().get("3").getLastKnownLightState().getBrightness();
        // System.out.println(on + ", state: "
        // + SDKListener.this.sdk.getSelectedBridge().getResourceCache().getLights().get("3").getLastKnownLightState().toString() +
        // "  light: "
        // + SDKListener.this.sdk.getSelectedBridge().getResourceCache().getLights().get("3").hashCode());
        // try {
        // Thread.sleep(200);
        // } catch (final InterruptedException e) {
        // }
        // }
        // }
        // }).start();

    }

    @Override
    public void onCacheUpdated(final List<Integer> cacheNotificationsList, final PHBridge bridge) {
        // Here you receive notifications that the BridgeResource Cache was updated. Use the PHMessageType to
        // check which cache was updated, e.g.
        // if (cacheNotificationsList.contains(PHMessageType.LIGHTS_CACHE_UPDATED)) {
        // System.out.println("Lights Cache Updated ");
        // }
        // System.out.println("onCacheUpdated: " + cacheNotificationsList + ", " + bridge);

        System.out.println("Previous state: 1 " + this.toString(this.stateBeforeEvent1));
        System.out.println("New state:      1 " + this.toString(bridge.getResourceCache().getLights().get("1").getLastKnownLightState()));
        System.out.println("Previous state: 2 " + this.toString(this.stateBeforeEvent2));
        System.out.println("New state:      2 " + this.toString(bridge.getResourceCache().getLights().get("2").getLastKnownLightState()));
        System.out.println("Previous state: 3 " + this.toString(this.stateBeforeEvent3));
        System.out.println("New state:      3 " + this.toString(bridge.getResourceCache().getLights().get("3").getLastKnownLightState()));
        System.out.println("Previous state: 4 " + this.toString(this.stateBeforeEvent4));
        System.out.println("New state:      4 " + this.toString(bridge.getResourceCache().getLights().get("4").getLastKnownLightState()));
        System.out.println("Previous state: 5 " + this.toString(this.stateBeforeEvent5));
        System.out.println("New state:      5 " + this.toString(bridge.getResourceCache().getLights().get("5").getLastKnownLightState()));
        System.out.println("Previous state: 6 " + this.toString(this.stateBeforeEvent6));
        System.out.println("New state:      6 " + this.toString(bridge.getResourceCache().getLights().get("6").getLastKnownLightState()));
        System.out.println("Previous state: 7 " + this.toString(this.stateBeforeEvent7));
        System.out.println("New state:      7 " + this.toString(bridge.getResourceCache().getLights().get("7").getLastKnownLightState()));
        System.out.println("Previous state: 8 " + this.toString(this.stateBeforeEvent8));
        System.out.println("New state:      8 " + this.toString(bridge.getResourceCache().getLights().get("8").getLastKnownLightState()));
        System.out.println();
        this.stateBeforeEvent1 = new PHLightState(bridge.getResourceCache().getLights().get("1").getLastKnownLightState());
        this.stateBeforeEvent2 = new PHLightState(bridge.getResourceCache().getLights().get("2").getLastKnownLightState());
        this.stateBeforeEvent3 = new PHLightState(bridge.getResourceCache().getLights().get("3").getLastKnownLightState());
        this.stateBeforeEvent4 = new PHLightState(bridge.getResourceCache().getLights().get("4").getLastKnownLightState());
        this.stateBeforeEvent5 = new PHLightState(bridge.getResourceCache().getLights().get("5").getLastKnownLightState());
        this.stateBeforeEvent6 = new PHLightState(bridge.getResourceCache().getLights().get("6").getLastKnownLightState());
        this.stateBeforeEvent7 = new PHLightState(bridge.getResourceCache().getLights().get("7").getLastKnownLightState());
        this.stateBeforeEvent8 = new PHLightState(bridge.getResourceCache().getLights().get("8").getLastKnownLightState());
    }

    private PHBridge bridge;

    private String toString(final PHLightState state) {
        return "On: " + StringUtils.leftPad("" + state.isOn(), 5) +
                ", bri: " + StringUtils.leftPad("" + state.getBrightness(), 3) +
                ", cm: " + StringUtils.leftPad("" + state.getColorMode(), 25) +
                ", hue: " + StringUtils.leftPad("" + state.getHue(), 5) +
                ", sat: " + StringUtils.leftPad("" + state.getSaturation(), 3) +
                ", x: " + StringUtils.leftPad("" + state.getX(), 6) +
                ", y: " + StringUtils.leftPad("" + state.getY(), 6) +
                ", ct: " + StringUtils.leftPad("" + state.getCt(), 3) +
                ", reach: " + StringUtils.leftPad("" + state.isReachable(), 5) +
                ", trans: " + StringUtils.leftPad("" + state.getTransitionTime(), 4) +
                ", alert: " + StringUtils.leftPad("" + state.getAlertMode(), 10) +
                ", effect: " + StringUtils.leftPad("" + state.getEffectMode(), 10);
    }

    @Override
    public void onReceivingLightDetails(final PHLight paramPHLight) {
        System.out.println("onReceivingLightDetails: " + paramPHLight);
    }

    @Override
    public void onReceivingLights(final List<PHBridgeResource> paramList) {
        System.out.println("onReceivingLights: " + paramList);
    }

    @Override
    public void onSearchComplete() {
        System.out.println("onSearchComplete");
    }

    @Override
    public void onStateUpdate(final Map<String, String> paramMap, final List<PHHueError> paramList) {
        System.out.println("onStateUpdate: " + paramMap + ", " + paramList);
    }

    @Override
    public void onSuccess() {
        System.out.println("onSuccess");
    }

    @Override
    public void onConnectionLost(final PHAccessPoint accessPoint) {
        // Bridge disconnected
        System.out.println("onConnectionLost: " + accessPoint);
        // TODO: This happens quite a lot, after the server is running for a while. Looking at the source code, this is probably caused by
        // the bridge not responding (fast enough) twice (does not have to be in a row). Probably the easiest fix is just to reconnect upon
        // a connection lost.
    }

    @Override
    public void onConnectionResumed(final PHBridge bridge) {
        // This method will be called on every 'tick' of the heartbeat. Not sure this is a bug or a feature.
        // No implementation necessary.
        // System.out.println("onConnectionResumed aka heartbeat");
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
