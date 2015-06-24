"use strict";

// Start a new require module.
define(["jquery", "BasePage", "enums"],
		function ($, BasePage, enums) {

	function Activities() {
		BasePage.call(this, enums.EventTopic.PYH_ACTIVITIES);
		
		this.isActive = function (id) {
			return this.getResource(id).active;
		};

		this.updateResource = function (activity) {
			if (activity.active) {
				$("#activity-img-" + activity.id).removeClass("grayscale");
			} else {
				$("#activity-img-" + activity.id).addClass("grayscale");
			}
		};
	};
	
	// Required code for making BasePage the 'superclass'.
	Activities.prototype = Object.create(BasePage.prototype);
	Activities.prototype.constructor = Activities;
	
	// Return the 'singleton' object as external interface.
	return new Activities();

	
// End of require module.
});
