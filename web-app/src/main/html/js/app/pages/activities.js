"use strict";

// Start a new require module.
define(["jquery", "BasePage", "enums", 'api', 'pages'],
		function ($, BasePage, enums, api, pages) {

	function Activities() {
		BasePage.call(this, enums.EventTopic.PYH_ACTIVITIES);
		
		this.isActive = function (id) {
			return this.getResource(id).active;
		};
		
		this.clickActivity = function (id, name) {
			var isActive = this.isActive(id);
			var becomesActive = !isActive;
			// First complete the toggle, then switch the page, to make sure we don't run into
			// a race condition where the child page ends up with the wrong activity state.
			api.toggleActivity(id, isActive).done(function() {
				if (becomesActive) {
					// If the activity will become active by clicking the icon, go to the activity page for details.
					pages.show("activity-" + name);
				}
			});
		};

		this.updateResource = function (activity) {
			if (activity.active) {
				$("#activity-img-" + activity.id).removeClass("grayscale");
			} else {
				$("#activity-img-" + activity.id).addClass("grayscale");
			}
		};
	};
		
	// Return the 'singleton' object as external interface.
	return new Activities();

	
// End of require module.
});
