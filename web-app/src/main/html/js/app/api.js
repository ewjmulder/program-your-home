"use strict";

// Start a new require module.
// Contains and exposes all logic to interact with the server from within the different pages.
define(["rest", "config", "util", "enums", "log"],
		function (rest, config, util, enums, log) {
	
	//TODO: move ALL high level server interaction logic to here!
	
	// Save enum types from modules in local variables for easier accessing.
	var Resource = enums.Resource;

	const GET_MOUSE_POSITION = "pc/mouse/position";
	const MOVE_MOUSE_ABSOLUTE = "pc/mouse/moveAbsolute/{0},{1}";
	const MOVE_MOUSE_RELATIVE = "pc/mouse/moveRelative/{0},{1}";
	
	// Toggle a rest resource: set it to on if currently off and vice versa.
	function toggleRestResource(resource, onVerb, offVerb, id, currentlyOn) {
		var verbToUse = currentlyOn ? offVerb : onVerb;
		rest.verb(resource, id, verbToUse);
	};
	
	// Perform an action by getting a certain URL. This can be seen as a remote method invocation.
	// There will be no return value payload, but there can be a server error.
	function performAction(actionUrl) {
		var url = config.getValue("serverUrl") + actionUrl;
		log.trace("Performing server action url: '" + url + "'.");
		$.get(url, function (result) {
			// There will be no payload in this case and in case of an error, we just want it to get logged, so 2 times no-op for the callbacks.
			util.handleServiceResult(result, util.noop, util.noop);			
		}).fail(util.createXHRFailFunction("url: " + url));
	}

	// Request some data from the server by getting a certain URL. This can be seen as a remote method invocation with a return value.
	// The return value of this method is a promise, since the server call is async.
	function getDataPromise(dataUrl) {
		var loading = $.Deferred();		
		var url = config.getValue("serverUrl") + dataUrl;
		log.trace("Retrieving server data for url: '" + url + "'.");
		$.get(url, function (result) {
			util.handleServiceResult(result, loading.reject, loading.resolve);			
		})
		.fail(util.createXHRFailFunction("url: " + url))
	    .fail(loading.reject);
		return loading.promise();
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
		channelUpDevice: function (deviceId) {
			rest.verb(Resource.DEVICES, deviceId, "channel/up");
		},
		channelDownDevice: function (deviceId) {
			rest.verb(Resource.DEVICES, deviceId, "channel/down");
		},
		setChannelDevice: function (deviceId, channel) {
			rest.verb(Resource.DEVICES, deviceId, "channel/set/" + channel);
		},
		
		volumeUpActivity: function (activityId) {
			rest.verb(Resource.ACTIVITIES, activityId, "volume/up");
		},
		volumeDownActivity: function (activityId) {
			rest.verb(Resource.ACTIVITIES, activityId, "volume/down");
		},
		volumeMuteActivity: function (activityId) {
			rest.verb(Resource.ACTIVITIES, activityId, "volume/mute");
		},
		channelUpActivity: function (activityId) {
			rest.verb(Resource.ACTIVITIES, activityId, "channel/up");
		},
		channelDownActivity: function (activityId) {
			rest.verb(Resource.ACTIVITIES, activityId, "channel/down");
		},
		setChannelActivity: function (activityId, channel) {
			rest.verb(Resource.ACTIVITIES, activityId, "channel/set/" + channel);
		},
		playActivity: function (activityId) {
			rest.verb(Resource.ACTIVITIES, activityId, "play/play");
		},
		pauseActivity: function (activityId) {
			rest.verb(Resource.ACTIVITIES, activityId, "play/pause");
		},
		stopActivity: function (activityId) {
			rest.verb(Resource.ACTIVITIES, activityId, "play/stop");
		},
		fastForwardActivity: function (activityId) {
			rest.verb(Resource.ACTIVITIES, activityId, "play/fastForward");
		},
		rewindActivity: function (activityId) {
			rest.verb(Resource.ACTIVITIES, activityId, "play/rewind");
		},
		skipNextActivity: function (activityId) {
			rest.verb(Resource.ACTIVITIES, activityId, "skip/next");
		},
		skipPreviousActivity: function (activityId) {
			rest.verb(Resource.ACTIVITIES, activityId, "skip/previous");
		},
		recordActivity: function (activityId) {
			rest.verb(Resource.ACTIVITIES, activityId, "record");
		},
		menuToggleActivity: function (activityId) {
			rest.verb(Resource.ACTIVITIES, activityId, "menu/toggle");
		},
		menuSelectActivity: function (activityId) {
			rest.verb(Resource.ACTIVITIES, activityId, "menu/select");
		},
		menuBackActivity: function (activityId) {
			rest.verb(Resource.ACTIVITIES, activityId, "menu/back");
		},
		menuUpActivity: function (activityId) {
			rest.verb(Resource.ACTIVITIES, activityId, "menu/up");
		},
		menuDownActivity: function (activityId) {
			rest.verb(Resource.ACTIVITIES, activityId, "menu/down");
		},
		menuLeftActivity: function (activityId) {
			rest.verb(Resource.ACTIVITIES, activityId, "menu/left");
		},
		menuRightActivity: function (activityId) {
			rest.verb(Resource.ACTIVITIES, activityId, "menu/right");
		},
		
		getMousePosition: function() {
			return getDataPromise(GET_MOUSE_POSITION);
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