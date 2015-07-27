"use strict";

// Start a new require module.
//Start a new require module.
define(["jquery", "log"],
		function ($, log) {
	
	String.prototype.format = function() {
	    var str = this;
	    var i = arguments.length;
	    while (i--) {
	        str = str.replace(new RegExp('\\{' + i + '\\}', 'gm'), arguments[i]);
	    }
	    return str;
	};
	
	return {
		//////////////////////////////////////////////////
		// Program Your Home basic JS utility functions //
		//////////////////////////////////////////////////
	
		// Whether or not the array contains the item.
		contains: function(array, item) {
			return $.inArray(item, array) != -1;
		},
	
		// Capitalize the first letter of the given string.
		capitalizeFirstLetter: function (inputString) {
		    return inputString.charAt(0).toUpperCase() + inputString.substring(1);
		},
		
		// No-operation function, does nothing. Can be provided as 'empty' callback parameter.
		noop: function () {
			
		},
		
		// The identity function, just always return the argument 'as is'.
		identity: function (arg) {
			return arg;
		},

		// Return a function that results a promise that will immediately resolve with the provided argument.
		// Note: The returned function will always return the exact same (object equals) promise.
		immediatePromiseFunction: function (arg) {
			var promise = $.Deferred();
			promise.resolve(arg);
			return function () { return promise };
		},
	
		// String.format() for Javascript.
		// http://stackoverflow.com/questions/610406/javascript-equivalent-to-printf-string-format
		format: function (input) {
		    var args = Array.prototype.slice.call(arguments, 1);
		    return input.replace(/{(\d+)}/g, function (match, number) { 
		    	return typeof args[number] != 'undefined' ? args[number] : match;
		    });
	  	},
		
		pythagoras: function (a, b) {
			return Math.sqrt(a * a + b * b);
		},
		
		componentToHex: function (c) {
		    var hex = c.toString(16);
		    return hex.length == 1 ? "0" + hex : hex;
		},
	
		rgbToHex: function (r, g, b) {
		    return "#" + this.componentToHex(r) + this.componentToHex(g) + this.componentToHex(b);
		},
	
		hexToRgb: function (hex) {
			if (hex.charAt(0) == '#') {
				hex = hex.substring(1);
			}
		    var bigint = parseInt(hex, 16);
		    var r = (bigint >> 16) & 255;
		    var g = (bigint >> 8) & 255;
		    var b = bigint & 255;
	
		    return [r, g, b];
		},
	
		
		//////////////////////////////////////////////
		// Program Your Home more complex functions //
		//////////////////////////////////////////////
			
		// Create a function that handles the result of a async function failure.
		// The source is a small description of what the async function was loading.
		createXHRFailFunction: function (source, customErrorMessage) {
			return function (jqXHR, status, error) {
				if (error == "") {
					if (customErrorMessage != null) {
						log.error(customErrorMessage);
					} else {
						log.error(source + " could not be loaded.");
					}
				} else {
					if (customErrorMessage != null) {
						log.error(customErrorMessage);
					} else {
						log.error("Loading " + source + " failed with error: " + error);
					}
				}
			};
		},

		// Create a function that handles the result of a async function failure.
		// The source is a small description of what the async function was loading.
		createDeferredFailFunction: function (source, customErrorMessage) {
			return function (error) {
				if (customErrorMessage != null) {
					log.error(customErrorMessage);
				} else {
					log.error("Processing " + source + " failed with error: " + error);
				}
			};
		},

		handleServiceResult: function (result, errorCallback, successCallback) {
			// We expect a result object with success (boolean), error (string) and payload (object).
			if (result.hasOwnProperty("success") && result.hasOwnProperty("error") && result.hasOwnProperty("payload")) {
				if (result.success) {
					successCallback(result.payload);
				} else {
					// If the result was not successful, log that as an error.
					log.error("Server response contained an error: '" + result.error + "'.");
					errorCallback("Server error");
				}
			} else {
				log.error("Invalid server response, expected properties 'success', 'error' and 'payload'.");
				errorCallback("Invalid server response");		
			}
		},
		
	}

// End of require module.
});