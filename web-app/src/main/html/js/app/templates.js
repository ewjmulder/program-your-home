"use strict";

// Start a new require module.
// Handles all template loading logic.
define(["jquery", "handlebars"],
		function ($, Handlebars) {

	// Map with all template name->object pairs.
	var templates = {};

	// Load the templates with the given names.
	function loadTemplates(templateNames) {
		var templateLoading = $.Deferred();
		var numberOfTemplatesLoaded = 0;
		templateNames.forEach(function (templateName) {
		    $.get("/templates/" + templateName + ".hbs.html", function (contents) {
		    	try {
			    	templates[templateName] = Handlebars.compile(contents);
			    	// Force execution of template to pre-check for any errors.
			    	templates[templateName]({});
			    	numberOfTemplatesLoaded++;
			    	if (numberOfTemplatesLoaded == templateNames.length) {
			    		templateLoading.resolve();
			    	}
		    	} catch (error) {
		    		showError("Error while compiling template: " + error);
		    		templateLoading.reject();
		    	}
		    })
		    .fail(util.createFailFunction("template " + templateName))
		    .fail(templateLoading.reject);
		});
		return templateLoading;
	}
	
	return {
		load: function (templateNames) {
			return loadTemplates(templateNames);
		},
	
		get: function (name) {
			return templates[name];
		}
	};


// End of require module.
});