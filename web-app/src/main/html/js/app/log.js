"use strict";

// Start a new require module.
// Thin wrapper around the used logging framework.
define(["loglevel"],
		function (loglevel) {
	
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