package nl.ewjmulder.programyourhome.hue;

import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHHueSDK;

public class Main {

	public static void main(String[] args) {
		PHHueSDK sdk = PHHueSDK.create();
		sdk.setDeviceName("SmartHome");
		sdk.getNotificationManager().registerSDKListener(new MyListener(sdk));
		
		PHAccessPoint accessPoint = new PHAccessPoint();
		accessPoint.setIpAddress("192.168.2.100");
		accessPoint.setUsername("newdeveloper");

		sdk.connect(accessPoint);
	}

}
