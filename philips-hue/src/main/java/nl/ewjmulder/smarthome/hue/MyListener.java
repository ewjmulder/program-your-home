package nl.ewjmulder.smarthome.hue;

import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import com.philips.lighting.hue.listener.PHSceneListener;
import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHScene;

public class MyListener implements PHSDKListener {

	PHHueSDK sdk;
	
	public MyListener(PHHueSDK sdk) {
		this.sdk = sdk;
	}

	public void onAccessPointsFound(List<PHAccessPoint> arg0) {
		System.out.println("onAccessPointsFound: " + arg0);
	}

	public void onAuthenticationRequired(PHAccessPoint arg0) {
		System.out.println("onAuthenticationRequired: " + arg0);
	}

	public void onBridgeConnected(final PHBridge bridge) {
		System.out.println("onBridgeConnected: " + bridge);
		
		Date now = new Date();
		System.out.println("Hours: " + now.getHours());
		if (now.getHours() >= 20 && now.getHours() < 23) {
			bridge.activateScene("ac637e2f0-on-0", "1", null);
		} else {
			bridge.activateScene("19a493ad2-off-0", "1", null);
		}
		
//		while (true) {
//			for (PHLight light : bridge.getResourceCache().getLights().values()) {
//				if (new Random().nextDouble() > 0.5) {

// TODO: Hoe kunnen we living color lights aansturen anders dan	met scenes?
// TODO: source van de jars erbij pakken met JD-Eclipse: http://jd.benow.ca/
		
//					System.out.println("Update light: " + light.getName());
//					System.out.println("brightness: " + light.getLastKnownLightState().getBrightness());
//					System.out.println("ct: " + light.getLastKnownLightState().getCt());
//					System.out.println("hue: " + light.getLastKnownLightState().getHue());
//					System.out.println("sat: " + light.getLastKnownLightState().getSaturation());
//					System.out.println("color mode: " + light.getLastKnownLightState().getColorMode());
//					PHLightState state = new PHLightState(light.getLastKnownLightState());
//					state.setOn(!state.isOn());
//					state.setColorMode(PHLightColorMode.COLORMODE_HUE_SATURATION);
//					bridge.updateLightState(light, state, new PHLightListener() {
//						
//						public void onSuccess() {
//							System.out.println("onSuccess");
//						}
//						public void onStateUpdate(Hashtable<String, String> arg0,
//								List<PHHueError> arg1) {
//							System.out.println("onStateUpdate");
//						}
//						public void onError(int arg0, String arg1) {
//							System.out.println("onError");
//							System.out.println("arg0: " + arg0);
//							System.out.println("arg1: " + arg1);
//						}
//						@Override
//						public void onReceivingLightDetails(PHLight light) {
//							System.out.println("onReceivingLightDetails");
//						}
//						@Override
//						public void onSearchComplete() {
//							System.out.println("onSearchComplete");
//						}
//					});
//				}
//				try { Thread.sleep(500); } catch (InterruptedException e) {}
//			}
//		}
		
//		bridge. AllScenes(new PHSceneListener() {
//			
//			public void onSuccess() {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			public void onStateUpdate(Hashtable<String, String> arg0,
//					List<PHHueError> arg1) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			public void onError(int arg0, String arg1) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onScenesReceived(List<PHScene> sceneList) {
//				for (PHScene scene : sceneList) {
//					try {
//						System.out.println(scene.getName());
//						bridge.activateScene(scene.getSceneIdentifier(), "1", null);
//						try { Thread.sleep(2000); } catch (InterruptedException e) {}
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		});
	}

	public void onCacheUpdated(int arg0, PHBridge arg1) {
		System.out.println("onCacheUpdated: " + arg0);
	}

	public void onConnectionLost(PHAccessPoint arg0) {
		System.out.println("onConnectionLost: " + arg0);
	}

	public void onConnectionResumed(PHBridge arg0) {
		System.out.println("onConnectionResumed: " + arg0);
	}

	public void onError(int arg0, String arg1) {
		System.out.println("onError: " + arg0 + ", " + arg1);
	}

	
	
}
