"use strict";

// Start a new require module.
// Contains and exposes all logic to interact with the server from within the different pages.
define(["rest", "util", "enums"],
		function (rest, util, enums) {
	
	//TODO: move ALL high level server interaction logic to here!
	
	// Save enum types from modules in local variables for easier accessing.
	var Resource = enums.Resource;

	// Toggle a rest resource: set it to on if currently off and vice versa.
	function toggleRestResource(resource, onVerb, offVerb, id, currentlyOn) {
		var verbToUse = currentlyOn ? offVerb : onVerb;
		rest.verb(resource, id, verbToUse);
	};
	
	// Perform an action by getting a certain URL. This can be seen as a remote method invocation.
	function performAction(url) {
		$.get(url, function(result) {
			//todo
		}).fail(util.createXHRFailFunction("url " + url));
	}

	return {
		// Start or stop an activity.
		toggleActivity: function (id, isActive) {
			toggleRestResource(Resource.ACTIVITIES, "start", "stop", id, isActive);
		},
		// Switch a light on or off.
		toggleLight: function (id, isOn) {
			toggleRestResource(Resource.LIGHTS, "on", "off", id, isOn);
		},
		// TODO: put this endless list in a sep. module: api.
		volumeUpDevice: function(deviceId) {
			rest.verb(Resource.DEVICES, deviceId, "volume/up");
		},
		volumeDownDevice: function(deviceId) {
			rest.verb(Resource.DEVICES, deviceId, "volume/down");
		},
		volumeMuteDevice: function(deviceId) {
			rest.verb(Resource.DEVICES, deviceId, "volume/mute");
		},
		//TODO: this is no rest at all! use simple $.get() for this and sanitize URL
		// Also consider not using RestController on backend? (although default JSON is nice)
		// Can still use that as result object for 'RPC' over HTTP
		moveMouseAbsolute: function(x, y) {
			rest.verbParam(Resource.MOUSE, 1, "moveAbsolute", x + "," + y);
		}
	};



// End of require module.
});