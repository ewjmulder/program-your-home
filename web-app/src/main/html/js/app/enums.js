"use strict";

// Start a new require module.
// Contains and exposes all PYH specific enums in the app that are used by several modules.
define([],
		function () {
	
	// Enum-like definition of all Program Your Home modules.
	// The values are the URL prefixes on the server.
	var Module = Object.freeze({
		MAIN: "main",
		HUE: "hue",
		IR: "ir",
		SENSORS: "sensors"
	});
	
	// Enum-like definition of all Program Your Home resources, connected to their corresponding module.
	// The name equals the resource part of the URL on the server.
	var Resource = Object.freeze({
		ACTIVITIES:   { module: Module.MAIN,     name: "activities" },
		LIGHTS:       { module: Module.HUE,      name: "lights"     },
		DEVICES:      { module: Module.IR,       name: "devices"    },
		SUN_DEGREE:   { module: Module.SENSORS,  name: "sunDegree"  },
	});

	// Enum-like definition of all possible setting names. So essentially all settings that are available.
	var SettingName = Object.freeze({
		SLIDING_SUBMENUS: "slidingSubmenus",
		HOME_PAGE: "homePage"
	});

	// Enum-like definition of all possible event topics.
	// Can be either a string with the name of the topic or a function that can build a topic name given a certain input.
	var EventTopic = Object.freeze({
		SUN_DEGREE: "/topic/event/sunDegree",
		PYH_ACTIVITIES: function (id) { return "/topic/pyh/activities/" + id; }
	});

	// Expose the enums as properties of this module.
	return {
		Module: Module,
		Resource: Resource,
		SettingName: SettingName,
		EventTopic: EventTopic
	};


// End of require module.
});