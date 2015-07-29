"use strict";

// Start a new require module.
define(["jquery", "BasePage", "enums"],
		function ($, BasePage, enums) {
	
	function Activity() {
		BasePage.call(this, enums.EventTopic.PYH_ACTIVITIES);
		
		this.updateResource = function (activity) {
			$("#activity-todo-" + activity.id).html(activity.active ? "Active todo" : "Not active todo");
		};
	};
		
	// Return the 'singleton' object as external interface.
	return new Activity();
	
// End of require module.
});
