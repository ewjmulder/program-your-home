"use strict";

// Start a new require module.
// Contains and exposes all logic to interact with the server from within the different pages.
define(["rest", "config", "util", "enums", "log"],
		function (rest, config, util, enums, log) {
	
	//TODO: move ALL high level server interaction logic to here!
	
	// Save enum types from modules in local variables for easier accessing.
	var Resource = enums.Resource;

	const MOVE_MOUSE_ABSOLUTE = "pc/mouse/moveAbsolute/{0},{1}";
	const MOVE_MOUSE_RELATIVE = "pc/mouse/moveRelative/{0},{1}";
	
	// Toggle a rest resource: set it to on if currently off and vice versa.
	function toggleRestResource(resource, onVerb, offVerb, id, currentlyOn) {
		var verbToUse = currentlyOn ? offVerb : onVerb;
		rest.verb(resource, id, verbToUse);
	};
	
	// Perform an action by getting a certain URL. This can be seen as a remote method invocation.
	function performAction(actionUrl) {
		var url = config.getValue("serverUrl") + actionUrl;
		log.trace("Performing server action url: '" + url + "'.");
		$.get(url, function (result) {
			// There will be no payload in this case and in case of an error, we just want it to get logged, so 2 times no-op for the callbacks.
			util.handleServiceResult(result, util.noop, util.noop);			
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
		volumeUpDevice: function (deviceId) {
			rest.verb(Resource.DEVICES, deviceId, "volume/up");
		},
		volumeDownDevice: function (deviceId) {
			rest.verb(Resource.DEVICES, deviceId, "volume/down");
		},
		volumeMuteDevice: function (deviceId) {
			rest.verb(Resource.DEVICES, deviceId, "volume/mute");
		},
		
		volumeUpActivity: function (activityId) {
			rest.verb(Resource.ACTIVITIES, activityId, "volume/up");
		},
		volumeDownActivity: function (activityId) {
			rest.verb(Resource.ACTIVITIES, activityId, "volume/down");
		},
		volumeMuteDevice: function (activityId) {
			rest.verb(Resource.ACTIVITIES, activityId, "volume/mute");
		},
		
		moveMouseAbsolute: function(x, y) {
			performAction(MOVE_MOUSE_ABSOLUTE.format(x, y));
		},
		moveMouseRelative: function(dx, dy) {
			performAction(MOVE_MOUSE_RELATIVE.format(dx, dy));
		},
	};



// End of require module.
});