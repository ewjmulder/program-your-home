"use strict";

// Start a new require module.
// Contains and exposes all logic to interact with the server from within the different pages.
//
// Note: the api implementation assumes that all callers of api methods will check beforehand if the module they are trying to reach is available or not.
// If a called tries to reach a non-available module, the api behavior is undefined and will most probably result in an error.
define(["jquery", "rest", "config", "util", "enums", "log"],
		function ($, rest, config, util, enums, log) {
	
	// Save enum types from modules in local variables for easier accessing.
	var HttpMethod = enums.HttpMethod;
	var Module = enums.Module;
	var Resource = enums.Resource;
	var RestVerb = enums.RestVerb;
	
	var SET_CHANNEL_DEVICE = "ir/devices/{0}/channel/set/{1}";
	var SET_CHANNEL_ACTIVITY = "main/activities/{0}/channel/set/{1}";
	var GET_MOUSE_POSITION = "pc/mouse/position";
	var MOVE_MOUSE_ABSOLUTE = "pc/mouse/moveAbsolute/{0},{1}";
	var MOVE_MOUSE_RELATIVE = "pc/mouse/moveRelative/{0},{1}";
	
	// The PYH modules that are available on the server. Filtered by what is running on the server and maybe other filters, like authorization.
	var availableModules = [];
	
	// Connect to the backend server. Performs some basic startup checks, like if the server is online.
	// Returns a promise that will resolve if everything is ok and fail otherwise.
	function connect() {
		var connecting = $.Deferred();		
		$.ajax({url: config.getValue("serverUrl") + "meta/status/ping", timeout: 3000}).done(function (pong) {
			// If we get a response, that's fine, no need to check the (pong) body.
			
			//TODO: The server can provide a list of activated modules and the UI can enable/disable pages based on that info.
			//TODO: define module / page filter based on API response from server that tells us what modules are available.
			//TODO: per module, there might also be a 'meta' availability, for instance for sensors which ones are available + what their props are
			// Some defaults will be provided as types: sun degree, temperature, humidity, sound level, light intensity, etc. + 'free format'
			// Actually, is there any way you could display a non standard sensor but just the data value? (but might still be useful of course)
			availableModules = [Module.MAIN, Module.HUE, Module.IR, Module.SENSORS, Module.PC];
			createRestResources();
			connecting.resolve();
		})
		.fail(util.createXHRFailFunction(null, "Could not reach server."))
		.fail(connecting.reject);
		return connecting.promise();
	};


	// Initializes all REST related data: creates the needed clients with all possible verbs.
	// Should be called before any other api methods that use REST.
	// Note: no use of HttpMethods here, since the Javascript property name must be 'hardcoded' and cannot be retrieved from a variable in this notation.
	// See also: http://stackoverflow.com/questions/5640988/how-do-i-interpolate-a-variable-as-a-key-in-a-javascript-object
	function createRestResources() {
		if (isModuleAvailable(Module.MAIN)) {
			rest.create(Resource.ACTIVITIES, {GET: [RestVerb.START, RestVerb.STOP, RestVerb.VOLUME_UP, RestVerb.VOLUME_DOWN, RestVerb.VOLUME_MUTE, RestVerb.CHANNEL_UP, RestVerb.CHANNEL_DOWN, RestVerb.PLAY_PLAY, RestVerb.PLAY_PAUSE, RestVerb.PLAY_STOP, RestVerb.PLAY_FAST_FORWARD, RestVerb.PLAY_REWIND, RestVerb.SKIP_NEXT, RestVerb.SKIP_PREVIOUS, RestVerb.RECORD, RestVerb.MENU_TOGGLE, RestVerb.MENU_SELECT, RestVerb.MENU_BACK, RestVerb.MENU_UP, RestVerb.MENU_DOWN, RestVerb.MENU_LEFT, RestVerb.MENU_RIGHT]});
		}
		if (isModuleAvailable(Module.HUE)) {
			rest.create(Resource.LIGHTS, {GET: [RestVerb.TURN_ON, RestVerb.TURN_OFF]});
			//TODO: plugs!
		}
		if (isModuleAvailable(Module.IR)) {
			rest.create(Resource.DEVICES, {GET: [RestVerb.POWER_ON, RestVerb.POWER_OFF, RestVerb.VOLUME_UP, RestVerb.VOLUME_DOWN, RestVerb.VOLUME_MUTE, RestVerb.CHANNEL_UP, RestVerb.CHANNEL_DOWN, RestVerb.PLAY_PLAY, RestVerb.PLAY_PAUSE, RestVerb.PLAY_STOP, RestVerb.PLAY_FAST_FORWARD, RestVerb.PLAY_REWIND, RestVerb.SKIP_NEXT, RestVerb.SKIP_PREVIOUS, RestVerb.RECORD, RestVerb.MENU_TOGGLE, RestVerb.MENU_SELECT, RestVerb.MENU_BACK, RestVerb.MENU_UP, RestVerb.MENU_DOWN, RestVerb.MENU_LEFT, RestVerb.MENU_RIGHT]});
		}
		if (isModuleAvailable(Module.SENSORS)) {
			rest.create(Resource.SUN_DEGREE, {});
		}
	};
	
	function isModuleAvailable(module) {
		return util.contains(availableModules, module);
	};
	
	// Toggle a rest resource: set it to on if currently off and vice versa.
	// A promise is returned in case the caller wants to act on the success/failure of the action.
	function toggleRestResource(resource, onVerb, offVerb, id, currentlyOn) {
		var verbToUse = currentlyOn ? offVerb : onVerb;
		return rest.verb(resource, id, verbToUse);
	};
	
	// Perform an action by getting a certain URL. This can be seen as a remote method invocation.
	// There will be no return value payload, but there can be a server error.
	// A promise is returned in case the caller wants to act on the success/failure of the action.
	function performAction(actionUrl) {
		var loading = $.Deferred();
		var url = config.getValue("serverUrl") + actionUrl;
		log.trace("Performing server action url: '" + url + "'.");
		$.get(url, function (result) {
			util.handleServiceResult(result, loading.reject, loading.resolve);	
		})
		.fail(util.createXHRFailFunction("url: " + url))
		.fail(loading.reject);
		return loading.promise();
	};

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
	};

	return {
		// ********************************************************************
		// Utility methods that are API related.
		// ********************************************************************
		connect: connect,
		isModuleAvailable: isModuleAvailable,

		// ********************************************************************
		// Subscription methods on the API data.
		// Methods that create a subscription on a certain topic and call the given callback.
		// ********************************************************************
		// Subscribe to value changes of the sun degree state.
		subscribeToSunDegreeState: function (callback) {
			events.subscribeForObject(EventTopic.SUN_DEGREE_ANGLE, callback);
		},

		// ********************************************************************
		// API to the Program Your Home server.
		// Methods that retrieve data and do not change the state of the server.
		// The return value of these methods is a promise, since the server call is async.
		// ********************************************************************
		// Get all resources of a certain type.
		getResources: function (resource) {
			return rest.readAll(resource);
		},
		// Get all activities.
		getActivities: function () {
			return rest.readAll(Resource.ACTIVITIES);
		},
		// Get all lights.
		getLights: function () {
			return rest.readAll(Resource.LIGHTS);
		},
		// Get all devices.
		getDevices: function () {
			return rest.readAll(Resource.DEVICES);
		},
		// Get resource of certain type by id.
		getResource: function (resource, id) {
			return rest.read(resource, id);
		},
		// Get activity by id.
		getActivity: function (id) {
			return rest.read(Resource.ACTIVITIES, id);
		},
		// Get light by id.
		getLight: function (id) {
			return rest.read(Resource.LIGHTS, id);
		},
		// Get devices by id.
		getDevice: function(id) {
			return rest.read(Resource.DEVICES, id);
		},

		// Get the position of the mouse on the server.
		getMousePosition: function () {
			return getDataPromise(GET_MOUSE_POSITION);
		},

		// ********************************************************************
		// API to the Program Your Home server
		// Methods to perform actions that change the state of the server
		// A promise is returned in case the caller wants to act on the success/failure of the action.
		// ********************************************************************
		// Start or stop an activity.
		toggleActivity: function (id, isActive) {
			return toggleRestResource(Resource.ACTIVITIES, "start", "stop", id, isActive);
		},
		
		// Switch a light on or off.
		toggleLight: function (id, isOn) {
			return toggleRestResource(Resource.LIGHTS, "on", "off", id, isOn);
		},
		
		volumeUpDevice: function (deviceId) {
			return rest.verb(Resource.DEVICES, deviceId, RestVerb.VOLUME_UP);
		},
		volumeDownDevice: function (deviceId) {
			return rest.verb(Resource.DEVICES, deviceId, RestVerb.VOLUME_DOWN);
		},
		volumeMuteDevice: function (deviceId) {
			return rest.verb(Resource.DEVICES, deviceId, RestVerb.VOLUME_MUTE);
		},
		channelUpDevice: function (deviceId) {
			return rest.verb(Resource.DEVICES, deviceId, RestVerb.CHANNEL_UP);
		},
		channelDownDevice: function (deviceId) {
			return rest.verb(Resource.DEVICES, deviceId, RestVerb.CHANNEL_DOWN);
		},
		setChannelDevice: function (deviceId, channel) {
			return performAction(SET_CHANNEL_DEVICE.format(deviceId, channel));
		},
		playDevice: function (deviceId) {
			return rest.verb(Resource.DEVICES, deviceId, RestVerb.PLAY_PLAY);
		},
		pauseDevice: function (deviceId) {
			return rest.verb(Resource.DEVICES, deviceId, RestVerb.PLAY_PAUSE);
		},
		stopDevice: function (deviceId) {
			return rest.verb(Resource.DEVICES, deviceId, RestVerb.PLAY_STOP);
		},
		fastForwardDevice: function (deviceId) {
			return rest.verb(Resource.DEVICES, deviceId, RestVerb.PLAY_FAST_FORWARD);
		},
		rewindDevice: function (deviceId) {
			return rest.verb(Resource.DEVICES, deviceId, RestVerb.PLAY_REWIND);
		},
		skipNextDevice: function (deviceId) {
			return rest.verb(Resource.DEVICES, deviceId, RestVerb.SKIP_NEXT);
		},
		skipPreviousDevice: function (deviceId) {
			return rest.verb(Resource.DEVICES, deviceId, RestVerb.SKIP_PREVIOUS);
		},
		recordDevice: function (deviceId) {
			return rest.verb(Resource.DEVICES, deviceId, RestVerb.RECORD);
		},
		menuToggleDevice: function (deviceId) {
			return rest.verb(Resource.DEVICES, deviceId, RestVerb.MENU_TOGGLE);
		},
		menuSelectDevice: function (deviceId) {
			return rest.verb(Resource.DEVICES, deviceId, RestVerb.MENU_SELECT);
		},
		menuBackDevice: function (deviceId) {
			return rest.verb(Resource.DEVICES, deviceId, RestVerb.MENU_BACK);
		},
		menuUpDevice: function (deviceId) {
			return rest.verb(Resource.DEVICES, deviceId, RestVerb.MENU_UP);
		},
		menuDownDevice: function (deviceId) {
			return rest.verb(Resource.DEVICES, deviceId, RestVerb.MENU_DOWN);
		},
		menuLeftDevice: function (deviceId) {
			return rest.verb(Resource.DEVICES, deviceId, RestVerb.MENU_LEFT);
		},
		menuRightDevice: function (deviceId) {
			return rest.verb(Resource.DEVICES, deviceId, RestVerb.MENU_RIGHT);
		},
		
		volumeUpActivity: function (activityId) {
			return rest.verb(Resource.ACTIVITIES, activityId, RestVerb.VOLUME_UP);
		},
		volumeDownActivity: function (activityId) {
			return rest.verb(Resource.ACTIVITIES, activityId, RestVerb.VOLUME_DOWN);
		},
		volumeMuteActivity: function (activityId) {
			return rest.verb(Resource.ACTIVITIES, activityId, RestVerb.VOLUME_MUTE);
		},
		channelUpActivity: function (activityId) {
			return rest.verb(Resource.ACTIVITIES, activityId, RestVerb.CHANNEL_UP);
		},
		channelDownActivity: function (activityId) {
			return rest.verb(Resource.ACTIVITIES, activityId, RestVerb.CHANNEL_DOWN);
		},
		setChannelActivity: function (activityId, channel) {
			return performAction(SET_CHANNEL_ACTIVITY.format(activityId, channel));
		},
		playActivity: function (activityId) {
			return rest.verb(Resource.ACTIVITIES, activityId, RestVerb.PLAY_PLAY);
		},
		pauseActivity: function (activityId) {
			return rest.verb(Resource.ACTIVITIES, activityId, RestVerb.PLAY_PAUSE);
		},
		stopActivity: function (activityId) {
			return rest.verb(Resource.ACTIVITIES, activityId, RestVerb.PLAY_STOP);
		},
		fastForwardActivity: function (activityId) {
			return rest.verb(Resource.ACTIVITIES, activityId, RestVerb.PLAY_FAST_FORWARD);
		},
		rewindActivity: function (activityId) {
			return rest.verb(Resource.ACTIVITIES, activityId, RestVerb.PLAY_REWIND);
		},
		skipNextActivity: function (activityId) {
			return rest.verb(Resource.ACTIVITIES, activityId, RestVerb.SKIP_NEXT);
		},
		skipPreviousActivity: function (activityId) {
			return rest.verb(Resource.ACTIVITIES, activityId, RestVerb.SKIP_PREVIOUS);
		},
		recordActivity: function (activityId) {
			return rest.verb(Resource.ACTIVITIES, activityId, RestVerb.RECORD);
		},
		menuToggleActivity: function (activityId) {
			return rest.verb(Resource.ACTIVITIES, activityId, RestVerb.MENU_TOGGLE);
		},
		menuSelectActivity: function (activityId) {
			return rest.verb(Resource.ACTIVITIES, activityId, RestVerb.MENU_SELECT);
		},
		menuBackActivity: function (activityId) {
			return rest.verb(Resource.ACTIVITIES, activityId, RestVerb.MENU_BACK);
		},
		menuUpActivity: function (activityId) {
			return rest.verb(Resource.ACTIVITIES, activityId, RestVerb.MENU_UP);
		},
		menuDownActivity: function (activityId) {
			return rest.verb(Resource.ACTIVITIES, activityId, RestVerb.MENU_DOWN);
		},
		menuLeftActivity: function (activityId) {
			return rest.verb(Resource.ACTIVITIES, activityId, RestVerb.MENU_LEFT);
		},
		menuRightActivity: function (activityId) {
			return rest.verb(Resource.ACTIVITIES, activityId, RestVerb.MENU_RIGHT);
		},
		
		moveMouseAbsolute: function(x, y) {
			return performAction(MOVE_MOUSE_ABSOLUTE.format(x, y));
		},
		moveMouseRelative: function(dx, dy) {
			return performAction(MOVE_MOUSE_RELATIVE.format(dx, dy));
		},
	};



// End of require module.
});