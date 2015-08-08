"use strict";

// Start a new require module.
define(["jquery", "BasePage", "enums", "pages", "hammer", "log", "api"],
		function ($, BasePage, enums, pages, Hammer, log, api) {
	
	return function Activity() {
		BasePage.call(this, enums.EventTopic.PYH_ACTIVITIES);

		// PanData class that keeps track of the last (cumulative) delta x and y of a pan.
		function PanData() {
			var lastDeltaX = 0;
			var lastDeltaY = 0;
			this.reset = function () {
				lastDeltaX = 0;
				lastDeltaY = 0;				
			};
			// The next delta x/y functions calculate and return the diff with the previous delta x/y and save the
			// provided value als last known value.
			this.nextDeltaX = function (nextDeltaX) {
				var diffDeltaX = nextDeltaX - lastDeltaX;
				lastDeltaX = nextDeltaX;
				return diffDeltaX;
			};
			this.nextDeltaY = function (nextDeltaY) {
				var diffDeltaY = nextDeltaY - lastDeltaY;
				lastDeltaY = nextDeltaY;
				return diffDeltaY;
			};
		};
		var currentPan = new PanData();
		
		this.initPage = function (resources) {
			// This is a page for one specific activity, so we should be able to safely get the id of the first element.
			var activityId = resources[0].id;
			// For the trackpad area, configure a pan recognizer as the single recognizer.
			var hammerManager = new Hammer.Manager(document.getElementById("activity-trackpad-" + activityId), {
			    recognizers: [
	  			    [Hammer.Pan, {threshold: 1, direction: Hammer.DIRECTION_ALL}]
			    ]
			});
			// When the user pans over the trackpad, use that motion as a mouse movement trigger on the server.
			// The panmove event has deltaX and deltaY properties, but those are cumulative. So we have to calculate
			// a diff on the previous delta that was recorded and use that as input for the server.
			hammerManager.on("panmove", function (e) {
				var diffDeltaX = currentPan.nextDeltaX(e.deltaX);
				var diffDeltaY = currentPan.nextDeltaY(e.deltaY);
				// TODO: make the mutiplier into a setting
				var multiplier = 3;
				api.moveMouseRelative(diffDeltaX * multiplier, diffDeltaY * multiplier);
			});
			// When a pan stops (either end or cancel), reset the currentPan to erase the delta diff records.
			hammerManager.on("panend pancancel", function (e) {
				currentPan.reset();
			});
			
			//TODO:
			//- Try to get pan minimum treshold, config of 1 is not working
			//(alternative: first pan in series no multiplier)
			//(second alternative: use tap or press (prob press) to get the first movement)
			//-don't work with <img> directly, as it triggers image saving browser logic, but use canvas and draw image on it
			//-split images for main movement and LMB, RMB
			//-Make it possible to hold LMB, RMB for dragging? (needs API/REST update for pressLMB/RMB, releaseLMB/RMB (with timeout?))
		}
		
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
