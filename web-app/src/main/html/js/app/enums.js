"use strict";

// Start a new require module.
// Contains and exposes all enums in the app that are used by several modules.
define([],
		function () {
	
	// Enum-like definition of all possible setting names. So essentially the settings that are available.
	var SettingName = Object.freeze({
		SLIDING_SUBMENUS: "slidingSubmenus",
		HOME_PAGE: "homePage"
	});

	// Enum-like definition of all possible event topics.
	var EventTopic = Object.freeze({
		SUN_DEGREE: "/topic/event/sunDegree",
		PYH_ACTIVITIES: function (id) { return "/topic/pyh/activities/" + id; }
	});
    
	return {
		SettingName: SettingName,
		EventTopic: EventTopic
	};


// End of require module.
});