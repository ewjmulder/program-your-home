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
		Object.keys(verbMap).forEach(function (httpMethod) {
			var verbArray = verbMap[httpMethod];
			verbArray.forEach(function (verbName) {
				restClient[resourceDefinition.name].addVerb(verbName, httpMethod);
			});
		});
		restClients[asKey(resourceDefinition)] = restClient[resourceDefinition.name];
	};

	function callVerb(resourceDefinition, resourceId, verb) {
		log.trace("Performing rest command with resourceDefinition: '" + asKey(resourceDefinition) + 
				"', resourceId: '" + resourceId + "', verb: '" + verb + "'.");
		var loading = $.Deferred();
		var client = restClients[asKey(resourceDefinition)];
		// The loader either loads a specific resource (if id provided) or all resources.
		var asyncCall = resourceId != null ? client[verb](resourceId) : client[verb]();
		asyncCall.done(function (result) {
			util.handleServiceResult(result, loading.reject, loading.resolve);
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