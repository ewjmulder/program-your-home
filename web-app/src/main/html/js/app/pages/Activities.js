"use strict";

// Start a new require module.
define(["jquery", "BasePage", "enums", 'api', 'pages'],
		function ($, BasePage, enums, api, pages) {

	return function Activities() {
		BasePage.call(this, enums.EventTopic.PYH_ACTIVITIES);
		
		var self = this;
		
		// When this page is shown, but one of the activities is active, go to that activity details page instead.
		this.showPage = function () {
			self.getResources().forEach(function (activity) {
				if (activity.active) {
					self.showActivityDetailsPage(activity);
				}
			});
		};
		
		this.isActive = function (id) {
			return self.getResource(id).active;
		};
		
		this.showActivityDetailsPage = function (activity) {
			pages.show("activity-" + activity.name);
		};

		this.resourceChanged = function (oldActivityValue, newActivityValue) {
			// If an activity becomes active and we are the current page, go to that activity page for details.
			if (newActivityValue.active && self.isCurrentPage()) {
				self.showActivityDetailsPage(newActivityValue);
			}
		};
		
		this.updateUI = function (activity) {
			if (activity.active) {
				$("#activity-img-" + activity.id).removeClass("grayscale");
			} else {
				$("#activity-img-" + activity.id).addClass("grayscale");
			}
		};
		
		this.clickActivity = function (id) {
			var activity = self.getResource(id);
			var isActive = self.isActive(id);
			api.toggleActivity(id, isActive);
		};
	};
	
// End of require module.
});
