"use strict";

// Start a new require module.
define(["jquery", "BasePage", "enums", "pages", "hammer", "log", "api"],
		function ($, BasePage, enums, pages, Hammer, log, api) {
	
	return function Activity() {
		BasePage.call(this, enums.EventTopic.PYH_ACTIVITIES);

		// DOM content elements for drawing the touch pad.
		var touchAreaContentElement = null;
		var lmbContentElement = null;
		var rmbContentElement = null;
		
		// PanData class that keeps track of the last (cumulative) delta x and y of a pan.
		function PanData() {
			var lastDeltaX = 0;
			var lastDeltaY = 0;
			this.isEmpty = function () {
				return lastDeltaX == 0 && lastDeltaY == 0;
			};
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
		
		this.initPage = function () {
			// This is a page for one specific activity, so we should be able to safely get the first element.
			var activity = this.getResources()[0];
			var activityId = activity.id;
			if (activity.mouseActivity) {
				touchAreaContentElement = document.getElementById("draw-activity-touch-area-" + activityId);
				lmbContentElement = document.getElementById("draw-activity-lmb-" + activityId);
				rmbContentElement = document.getElementById("draw-activity-rmb-" + activityId);
				
				this.drawTouchPadImage(touchAreaContentElement, "touchpad-move-area.png");
				this.drawTouchPadImage(lmbContentElement, "touchpad-lmb.png");
				this.drawTouchPadImage(rmbContentElement, "touchpad-rmb.png");
				this.configureTouchEvents();
			}
			if (activity.keyboardActivity) {
				var typeInputElement = document.getElementById("type-activity-" + activityId);
				$(typeInputElement).keyup(function (event) {
					// If any actual character entry has been made.
					if (typeInputElement.value.length > 0) {
						api.pressCharacterKeys(typeInputElement.value);
						typeInputElement.value = "";
					}
				});
			}
		}
		
		this.drawTouchPadImage = function (contentElement, imageFilename) {
			var touchPadImage = new Image();
			$(touchPadImage).load(function() {
				// Create a canvas element dynamically, with the right size and add it to the DOM tree.
				var canvasElement = document.createElement('canvas');
				canvasElement.width = touchPadImage.width;
				canvasElement.height = touchPadImage.height;
				contentElement.appendChild(canvasElement);
		
				// Get the drawing context from the canvas.
				var context = canvasElement.getContext('2d');
				context.drawImage(touchPadImage, 0, 0);
			})
			// Set the source at the end, so we'll be sure the load() function will be called.
			.attr({ src: "img/" + imageFilename });
		};
		
		this.configureTouchEvents = function () {
			// For the trackpad area, configure a pan recognizer as the single recognizer.
			var hammerManager = new Hammer.Manager(touchAreaContentElement, {
			    recognizers: [
			        // Set the threshold very low, to detect any mouse/finger movement as soon as possible.
			        // In practice, most mobile browsers will force a higher threshold, probably a best practice
			        // for human finger touch movement detection.
	  			    [Hammer.Pan, {threshold: 1, direction: Hammer.DIRECTION_ALL}]
			    ]
			});
			// When the user pans over the trackpad, use that motion as a mouse movement trigger on the server.
			// The panmove event has deltaX and deltaY properties, but those are cumulative. So we have to calculate
			// a diff on the previous delta that was recorded and use that as input for the server.
			hammerManager.on("panmove", function (e) {
				// TODO: make the mutiplier into a setting
				var multiplier = 3;
				// Do not use the multiplier on the first movement, because the high browser minimum movement
				// for pan detection will cause a big first 'jump' otherwise.
				if (currentPan.isEmpty()) {
					multiplier = 1;
				}
				var paramDeltaX = currentPan.nextDeltaX(e.deltaX) * multiplier;
				var paramDeltaY = currentPan.nextDeltaY(e.deltaY) * multiplier;
				// Only do a server call if there is any actual movement to process.
				if (paramDeltaX != 0 || paramDeltaY != 0) {
					api.moveMouseRelative(paramDeltaX, paramDeltaY);
				}
			});
			// When a pan stops (either end or cancel), reset the currentPan to erase the delta diff records.
			hammerManager.on("panend pancancel", function (e) {
				currentPan.reset();
			});
			// For the left and right mouse buttons, we can simply suffice with on click events.
			//TODO: mouse down/up events for dragging etc.
			$(lmbContentElement).click(api.clickLeftMouseButton);
			$(rmbContentElement).click(api.clickRightMouseButton);
		};
		
		this.resourceChanged = function (oldActivityValue, newActivityValue) {
			// If we become inactive and are currently shown, switch back to the main activities page.
			if (!newActivityValue.active && this.isCurrentPage()) {
				pages.show("activities");
			}
		};

		this.updateUI = function (activity) {
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
			api.toggleActivity(id, activity.active);
		};
	};
	
// End of require module.
});
