"use strict";

// Start a new require module.
// Thin wrapper around the used logging framework.
define(["loglevel", "config", "toast"],
		function (loglevel, config, toast) {
	
	return {
		trace: function (obj) {
			loglevel.trace(obj);
		},

		debug: function (obj) {
			loglevel.debug(obj);
		},
		
		info: function (obj) {
			loglevel.info(obj);
		},
		
		warn: function (obj) {
			loglevel.warn(obj);
		},
		
		error: function (obj) {
			loglevel.error(obj);
			if (config.getValue("showErrorsOnScreen")) {
				// TODO: also include a 'report to developer' kind of button to get feedback
				$().toastmessage("showToast", {
				    text     : errorMessage,
				    sticky   : true,
				    type     : "error",
				    position : "middle-center"
				});
			}
		},
		
		setLevel: function (level) {
			loglevel.setLevel(level);
		},

		noLogging: function () {
			loglevel.disableAll();
		},

		fullLogging: function () {
			loglevel.enableAll()
		},

	};


// End of require module.
});