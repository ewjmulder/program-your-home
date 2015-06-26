"use strict";

// Start a new require module.
define(["jquery", "BasePage", "enums"],
		function ($, BasePage, enums) {
	
	function Devices() {
		BasePage.call(this, enums.EventTopic.IR_DEVICES);
		
		this.updateResource = function (device) {
			$("#device-onoff-" + device.id).html(device.on ? "On" : "Off");
		};
	};
		
	// Return the 'singleton' object as external interface.
	return new Devices();
	
// End of require module.
});
