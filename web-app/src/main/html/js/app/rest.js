"use strict";

// Start a new require module.
// Module that contains generic logic for creating and calling a REST service.
define(["jquery", "jqrest", "util", "config"],
		function ($, jqrest, util, config) {
	
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

	function callVerbNoParam(resourceDefinition, resourceId, verb) {
		return callVerb(resourceDefinition, resourceId, verb, null);
	}

	function callVerb(resourceDefinition, resourceId, verb, verbParam) {
		var loading = $.Deferred();
		var client = restClients[asKey(resourceDefinition)];
		// The loader either calls the verb on a specific resource (if id provided) or all resources.
		// Optionally, a param can be provided when calling a verb on a specific resource.
		var asyncCall = resourceId != null ? (verbParam != null ? client[verb](resourceId, verbParam) : client[verb](resourceId)) : client[verb]();
		asyncCall.done(function (result) {
			if (!result.success && result.error) {
				// If the result was not successful, but does contain an error property, log that as an error,
				log.error("Rest command with resourceDefinition: '" + resourceDefinition + 
						"', resourceId: '" + resourceId + "', verb: '" + verb + "' returned an error: " + result.error);
				// and reject the promise.
				loading.reject();
			} else {
				// Otherwise, resolve the promise with the result.
				loading.resolve(result);
			}
		})
	    .fail(util.createFailFunction(resourceDefinition.name))
	    .fail(loading.reject);
		return loading;
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
		
		verb: callVerbNoParam,
		
		verbParam: callVerb
		
	};


// End of require module.
});