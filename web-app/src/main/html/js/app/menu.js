"use strict";

// Start a new require module.
// Lets you activate the menu of the application. This module contains the technical details of this activation and hides the frameworks used.
define(["jquery", "mmenu", "hammer", "settings", "pages", "enums"],
		function ($, mmenu, Hammer, settings, pages, enums) {
	
	// Create the menu, meaning invoking mmenu() on the html list that was dynamically build for that.
	function createMenu() {
		$("#menu").mmenu({
			extensions			: [// Dark background with bright text instead of the other way around.
			          			   "theme-dark",
			          			   // Extend 'border line' between menu items all the way to the left.
			          			   "border-full",
			          			   // Display a set of icons always on screen for fast top level menu switching.
			          			   "iconbar",
			          			   // Wraps the menu item text into multiple lines if needed.
			          			   "multiline",
			          			   // Display a page shadow on the menu when the menu is activated.
			          			   "pageshadow"],
			// Whether to slide to the right into a separate submenu (true), or open the submenu below a menu item (false).
			slidingSubmenus		: settings.getSettingValue(enums.SettingName.SLIDING_SUBMENUS),
			// If the menu is open and the browser 'back' button is pressed, it closes the menu instead of going back a page.
			backButton			: {
				close	: true
			},
			// Display a menu header, that defaults to 'Menu' at the top level, and changes when selecting submenus.
			header				: {
				add		: true,
				update	: true,
				title	: 'Menu'
			},
			// Display a fixed menu footer.
			footer: {
				add: 	true,
			    content: "(c) Erik Mulder",
			},
			// Allow the menu to be 'dragged open' from the left.
			dragOpen			: {
				maxStartPos	: 100,
				open		: true,
				pageNode	: $("#menu"),
				threshold	: 50
			}
		});
	};
	
	function configureSliding() {
		// Override the Hammer default recognizers. This makes the mmenu code use these instead of the defaults.
		// - Only Pan and Press, no other fancy stuff needed.
		// - Big move margin and no minimum time for press, so selecting a menu item is 'forgiving'.
		//   (you can move your finger a little while pressing, before it 'becomes' a pan)
		Hammer.defaults.preset =
			[
			    [Hammer.Pan, {threshold: 25, direction: Hammer.DIRECTION_HORIZONTAL}],
			    [Hammer.Press, { threshold: 25, time: 0}]
            ];

		// Create a hammer listener on the menu DOM element.
		var hammer = new Hammer($("#menu")[0], {});
		// When a press is detected, programmatically click on the center of the press, effectively selecting an underlying menu item (if present).
		hammer.on("pressup", function (e) {
			$(document.elementFromPoint(e.center.x, e.center.y)).click();  
		});
		
		// Bind to the touchstart event to be able to close the menu on touch events.
		// Do this binding only once, when the menu is opened for the first time.
		// It must be done after menu opening, probably because then the touchstart of the mmenu blocker div is registered first and gets priority.
		// This is needed to work around a bug on the Android browser, where the touchstart event is somehow
		// not coming through if the menu is opened. With this logic, we can swipe-close the menu the way we like it. :)
		var touchstartBound = false;
		getMmenuApi().bind("opened", function() {
			if (!touchstartBound) {
				$("#body").on("touchstart", function (e) {
					// Interestingly, no logic is needed here.
					// Probably this will make the touchstart work on the mmenu blocker div as well.
				});
				touchstartBound = true;
			}
		});
	};
	
	// Get the mmenu api object. Only accessible after mmenu() has been called on the menu DOM element.
	function getMmenuApi() {
		return $("#menu").data("mmenu");
	}
	
	return {
		// Function to be called once from the outside to activate the menu.
		activate: function () {
			createMenu();
			configureSliding();
			// Select the menu item (page) that is provided as the home page.
			getMmenuApi().setSelected($("#menu-" + pages.byName(settings.getSettingValue(enums.SettingName.HOME_PAGE)).id));
		}
	};


// End of require module.
});