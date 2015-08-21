"use strict";

// Start a new require module.
define(["jquery", "BasePage", "enums"],
		function ($, BasePage, enums) {
	
	return function Device() {
		BasePage.call(this, enums.EventTopic.IR_DEVICES);
		
		this.updateUI = function (device) {
			$("#device-todo-" + device.id).html(device.on ? "On todo" : "Off todo");
		};
	};
	
// End of require module.
});
