"use strict";

// Start a new require module.
// Module that contains generic logic for creating and calling a REST service.
define(["jquery", "jqrest", "util"],
		function ($, jqrest, util) {
	
	// Map with all rest client name->(resourceName->client object) pairs.
	var restClients = {};
	
	createRestService: function (clientName, prefix, resourceMap) {
		var restClient = new $.RestClient(config.getValue("serverUrl") + prefix + "/");
		resourcesMap.foreach(function (resourceName) {
			restClient.add(resourceName);
			resourcesMap[resourceName].foreach(function (verbName) {
				httpMethod = resourcesMap[resourceName][verbName];
				restClient[resourceName].addVerb(verbName, httpMethod);
			});
			restClients[clientName] = restClient;
		});
	}

	function callVerb(clientName, resourceName, resourceId, verb) {
		var loading = $.Deferred();
		var client = restClients[clientName][resourceName];
		// The loader either loads a specific resource (if id provided) or all resources.
		var asyncCall = resourceId != null ? client[verb](resourceId) : client[verb]();
		asyncCall.done(function (result) {
			if (!result.success && result.error) {
				// If the result was not successful, but does contain an error property, log that as an error,
				log.error("Rest command with clientName: '" + clientName + "', resourceName: '" + resourceName +
						"', resourceId: '" + resourceId + "', verb: '" + verb + "' returned an error: " + result.error);
				// and reject the promise.
				loading.reject;
			} else {
				// Otherwise, resolve the promise with the result.
				loading.resolve(result);
			}
		})
	    .fail(util.createFailFunction(resourceName))
	    .fail(loading.reject);
		loading.done(createDoneFunction(clientName, resourceName, resourceId, verb));
		return loading;
	}

	return {
		create: function (clientName, prefix, resourceMap) {
			createRestClient(clientName, prefix, resourceMap);
		},

		readAll: function (clientName, resourceName) {
			return callVerb(clientName, resourceName, null, "read");
		},
		
		read: function (clientName, resourceName, resourceId) {
			return callVerb(clientName, resourceName, resourceId, "read");
		},
		
		verb: function (clientName, resourceName, resourceId, verb) {
			return callVerb(clientName, resourceName, resourceId, verb);
		}		
	};


// End of require module.
});