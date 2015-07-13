"use strict";

// Start a new require module.
// Module that contains generic logic for creating and calling a REST service.
// Note: This module assumes that all rest responses are in the so called ServiceResult format,
// meaning the outer JSON response object contains the properties success (boolean), error (string) and payload (object).
// If success is true, the payload may contain a return value. If success if false, the error may contain an error message.
define(["jquery", "jqrest", "util", "config", "log"],
		function ($, jqrest, util, config, log) {
	
	// Map with all rest client name->(resourceName->client object) pairs.
	var restClients = {};
	
	function createRestClient(resourceDefinition, verbMap) {
		var restClient = new $.RestClient(config.getValue("serverUrl") + resourceDefinition.module + "/");
		restClient.add(resourceDefinition.name);
		Object.keys(verbMap).forEach(function (verbName) {
			var httpMethod = verbMap[verbName];
			restClient[resourceDefinition.name].addVerb(verbName, httpMethod);
		});
		restClients[asKey(resourceDefinition)] = restClient[resourceDefinition.name];
	};

	function callVerb(resourceDefinition, resourceId, verb) {
		var loading = $.Deferred();
		var client = restClients[asKey(resourceDefinition)];
		// The loader either loads a specific resource (if id provided) or all resources.
		var asyncCall = resourceId != null ? client[verb](resourceId) : client[verb]();
		asyncCall.done(function (result) {
			// We expect a result object with success (boolean), error (string) and payload (object).
			if (result.hasOwnProperty("success") && result.hasOwnProperty("error") && result.hasOwnProperty("payload")) {
				if (result.success) {
					// If successful, resolve the promise with the payload of the result.
					loading.resolve(result.payload);
				} else {
					// If the result was not successful, log that as an error,
					log.error("Rest command with resourceDefinition: '" + asKey(resourceDefinition) + 
							"', resourceId: '" + resourceId + "', verb: '" + verb + "' returned an error: " + result.error);
					// and reject the promise.
					loading.reject("Server error");
				}
			} else {
				log.error("Invalid server response, expected properties 'success', 'error' and 'payload'.");
				loading.reject("Invalid server response");				
			}
		})
	    .fail(util.createXHRFailFunction(resourceDefinition.name))
	    .fail(loading.reject);
		return loading.promise();
	};

	function asKey(resourceDefinition) {
		return resourceDefinition.module + "-" + resourceDefinition.name;
	}
	
	return {
		create: createRestClient,

		readAll: function (resourceDefinition) {
			return callVerb(resourceDefinition, null, "read");
		},
		
		read: function (resourceDefinition, resourceId) {
			return callVerb(resourceDefinition, resourceId, "read");
		},
		
		verb: callVerb
		
	};


// End of require module.
});