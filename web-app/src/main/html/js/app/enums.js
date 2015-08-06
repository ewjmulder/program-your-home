"use strict";

// Start a new require module.
// Contains and exposes all PYH specific enums in the app that are used by several modules.
define([],
		function () {

	// Enum-like definition of all HTTP methods.
	var HttpMethod = Object.freeze({
		GET: "GET",
		POST: "POST",
		PUT: "PUT",
		DELETE: "DELETE",
		HEAD: "HEAD",
		OPTIONS: "OPTIONS",
		TRACE: "TRACE"
	});

	// Enum-like definition of all Program Your Home modules.
	// The values are the URL prefixes on the server.
	var Module = Object.freeze({
		MAIN: "main",
		HUE: "hue",
		IR: "ir",
		SENSORS: "sensors",
		PC: "pc"
	});
	
	// Enum-like definition of all Program Your Home resources, connected to their corresponding module.
	// The name equals the resource part of the URL on the server.
	var Resource = Object.freeze({
		ACTIVITIES:   { module: Module.MAIN,     name: "activities" },
		LIGHTS:       { module: Module.HUE,      name: "lights"     },
		DEVICES:      { module: Module.IR,       name: "devices"    },
		SUN_DEGREE:   { module: Module.SENSORS,  name: "sunDegree"  }
	});

	// Enum-like definition of all possible setting names. So essentially all settings that are available.
	var SettingName = Object.freeze({
		SLIDING_SUBMENUS: "slidingSubmenus",
		HOME_PAGE: "homePage"
	});

	// Enum-like definition of all possible event topics.
	// Can be either a string with the name of the topic or a function that can build a topic name given a certain input.
	var EventTopic = Object.freeze({
		PYH_ACTIVITIES: function (id) { return "/topic/pyh/activities/" + id; },
		HUE_LIGHTS: function (id) { return "/topic/hue/lights/" + id; },
		IR_DEVICES: function (id) { return "/topic/ir/devices/" + id; },
		SUN_DEGREE_ANGLE: "/topic/sensors/sunDegree/angle",
		SUN_DEGREE_EVENT: "/topic/sensors/sunDegree/event",
		MOUSE_POSITION: "/topic/pc/mouse/position",
	});

	// Enum-like definition of all possible REST verbs.
	var RestVerb = Object.freeze({
		// Main - activities
		START: "start",
		STOP: "stop",
		
		// Hue - lights
		TURN_ON: "on",
		TURN_OFF: "off",
		
		// Ir - devices
		POWER_ON: "power/on",
		POWER_OFF: "power/off",
		VOLUME_UP: "volume/up",
		VOLUME_DOWN: "volume/down",
		VOLUME_MUTE: "volume/mute",
		CHANNEL_UP: "channel/up",
		CHANNEL_DOWN: "channel/down",
		//CHANNEL_SET cannot be implemented as a rest verb!
		PLAY_PLAY: "play/play",
		PLAY_PAUSE: "play/pause",
		PLAY_STOP: "play/stop",
		PLAY_FAST_FORWARD: "play/fastForward",
		PLAY_REWIND: "play/rewind",
		SKIP_NEXT: "skip/next",
		SKIP_PREVIOUS: "skip/previous",
		RECORD: "record",
		MENU_TOGGLE: "menu/toggle",
		MENU_SELECT: "menu/select",
		MENU_BACK: "menu/back",
		MENU_UP: "menu/up",
		MENU_DOWN: "menu/down",
		MENU_LEFT: "menu/left",
		MENU_RIGHT: "menu/right"
	});
	
	// Expose the enums as properties of this module.
	return {
		HttpMethod: HttpMethod,
		Module: Module,
		Resource: Resource,
		SettingName: SettingName,
		EventTopic: EventTopic,
		RestVerb: RestVerb
	};


// End of require module.
});