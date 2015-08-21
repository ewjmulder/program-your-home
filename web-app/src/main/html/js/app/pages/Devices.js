"use strict";

// Start a new require module.
define(["jquery", "BasePage", "enums"],
		function ($, BasePage, enums) {
	
	return function Devices() {
		BasePage.call(this, enums.EventTopic.IR_DEVICES);
		
		this.updateUI = function (device) {
			$("#device-onoff-" + device.id).html(device.on ? "On" : "Off");
		};
	};
	
// End of require module.
});
