//TODO: Also make an example file.
//TODO: consider using a .properties file or plain json file and some javascript parsing logic.

// Start a new require module.
define([],
		function () {

	var configMap = [];
	
	var activityIconMap = [];
	activityIconMap[1] = "server";
	activityIconMap[2] = "server";
	activityIconMap[3] = "server";
	
	configMap["activityIconMap"] = activityIconMap;

	var deviceIconMap = [];
	deviceIconMap[1] = "server";
	deviceIconMap[2] = "desktop";
	deviceIconMap[3] = "desktop";
	deviceIconMap[4] = "desktop";
	deviceIconMap[5] = "desktop";
	
	configMap["deviceIconMap"] = deviceIconMap;
	
	var topLevelIconMap = [];
	topLevelIconMap["activities"] = "star";
	topLevelIconMap["lights"] = "lightbulb-o";
	topLevelIconMap["devices"] = "hdd-o";
	topLevelIconMap["settings"] = "gears";
	topLevelIconMap["about"] = "info-circle";
	
	configMap["topLevelIconMap"] = topLevelIconMap;

	// Whether or not to use the default server url.
	// If set to true, the server url will be the protocol and host of the client app webserver, but then on port 3737.
	// If set to false, the customServerUrl property will be used.
	// Note: a mix of http and https for client web server and pyh server will not work. Use customServerUrl in that case.
	var useDefaultServerUrl = true;

	// Custom server URL for the PYH server. Please remember to put a '/' at the end.
	var customServerUrl = "http://example.com:3737/";
	
	configMap["serverUrl"] = useDefaultServerUrl ? buildDefaultServerUrl() : customServerUrl;
	
	configMap["logLevel"] = "trace";

	configMap["showErrorsOnScreen"] = true;

	function buildDefaultServerUrl() {
		var protocolLength = 7;
		if (window.location.href.substring(0, 5) == "https") {
			protocolLength = 8;
		}
		// Search after the 'http(s)://' part, find the first colon or slash from there gives us our default server URL.
		var indexOfColon = window.location.href.indexOf(":", protocolLength);
		var indexOfSlash = window.location.href.indexOf("/", protocolLength);
		var cutIndex = indexOfColon > -1 ? indexOfColon : indexOfSlash;
		// Take the same host on port 3737 as default server url for the Program Your Home server.
		// Also add the username / password as basic authentication in the url.
		return window.location.href.substring(0, protocolLength)
				+ window.location.href.substring(protocolLength, cutIndex) + ":3737/";
	}
	
	return {
		getValue: function (name) {
			return configMap[name];
		}
	};

	
// End of require module.
});
