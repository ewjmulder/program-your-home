"use strict";

// Start a new require module.
// Thin wrapper around the used logging framework.
define(["loglevel"],
		function (loglevel) {
	
	var errorCallback = null;
	
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
			if (errorCallback != null) {
				errorCallback(obj);
			}
		},
		
		setLevel: function (level) {
			loglevel.setLevel(level);
		},

		none: function () {
			loglevel.disableAll();
		},

		full: function () {
			loglevel.enableAll()
		},

		// Possibility to set an error callback to be informed when an error occurs.
		// NB: There is just one callback function possible. Calling the setter twice will override the previous value.
		setErrorCallback: function (theErrorCallback) {
			errorCallback = theErrorCallback;
		}
	};


// End of require module.
});