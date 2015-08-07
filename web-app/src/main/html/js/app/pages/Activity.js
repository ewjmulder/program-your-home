"use strict";

// Start a new require module.
define(["jquery", "BasePage", "enums", "pages"],
		function ($, BasePage, enums, pages) {
	
	return function Activity() {
		BasePage.call(this, enums.EventTopic.PYH_ACTIVITIES);
		
		this.updateResource = function (activity) {
			if (activity.active) {
				$("#activity-active-" + activity.id).removeClass("hidden");
				$("#activity-active-" + activity.id).addClass("visible");
			} else {
				$("#activity-active-" + activity.id).removeClass("visible");
				$("#activity-active-" + activity.id).addClass("hidden");
			}
		};
		
		this.clickPower = function (id) {
			var activity = this.getResource(id);
			var becomesInactive = activity.active;
			// First complete the toggle, then switch the page, to make sure we don't run into
			// a race condition where the main page ends up with the wrong activity state (if it was not already loaded).
			api.toggleActivity(id, activity.active).done(function() {
				if (becomesInactive) {
					// If the activity will become inactive by clicking the power button, go to the general activities page.
					pages.show("activities");
				}
			});
		};
	};
	
// End of require module.
});
