"use strict";

// Start a new require module.
define(["jquery", "pages", "util", "events", "enums"],
		function ($, pages, util, events, enums) {
	
	var activities = {};
	
	function updateActivity(activity) {
		if (activity.active) {
			$("#activity-img-" + activity.id).removeClass("grayscale");
		} else {
			$("#activity-img-" + activity.id).addClass("grayscale");
		}
	};
	
	return {
		backgroundColor: "white",
		createPage: pages.createPageFunctionForResources(
				"activities", activities, enums.EventTopic.PYH_ACTIVITIES, updateActivity, events.subscribeForObject),
		showPage: 
		isActive: function(id) {
			return activities[id].active;
		}
	};

	
// End of require module.
});
