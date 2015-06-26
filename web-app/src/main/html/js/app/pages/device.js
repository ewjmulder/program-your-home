"use strict";

// Start a new require module.
define(["jquery", "BasePage", "enums"],
		function ($, BasePage, enums) {
	
	function Device() {
		BasePage.call(this, enums.EventTopic.IR_DEVICES);
		
		this.updateResource = function (device) {
			$("#device-todo-" + device.id).html(device.on ? "On todo" : "Off todo");
		};
	};
		
	// Return the 'singleton' object as external interface.
	return new Device();
	
// End of require module.
});
