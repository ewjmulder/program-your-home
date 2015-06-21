"use strict";

// Start a new require module.
define(["jquery", "util", "events", "enums"],
		function ($, util, events, enums) {
	
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
		createPage: util.createPageFunctionForResources(
				activities, enums.EventTopic.PYH_ACTIVITIES, updateActivity, events.subscribeForObject),
		isActive: function(id) {
			return activities[id].active;
		}
	};

	
// End of require module.
});
