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
		VOLUME_UP: "volume/up",
		VOLUME_DOWN: "volume/down",
		VOLUME_MUTE: "volume/mute",
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