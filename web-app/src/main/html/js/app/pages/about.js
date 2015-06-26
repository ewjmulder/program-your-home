"use strict";

// Start a new require module.
define(["BasePage"],
		function (BasePage) {
	
	function About() {
		BasePage.call(this);
	};
		
	// Return the 'singleton' object as external interface.
	return new About();

	
// End of require module.
});
