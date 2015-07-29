"use strict";

// Start a new require module.
// Handles all template loading logic.
define(["jquery", "handlebars", "util", "log"],
		function ($, Handlebars, util, log) {

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
		    		log.error("Error while compiling template: " + error);
		    		templateLoading.reject("Template error");
		    	}
		    })
		    .fail(templateLoading.reject)
		    .fail(util.createXHRFailFunction("template " + templateName));
		});
		return templateLoading.promise();
	}
	
	return {
		load: function (names) {
			return loadTemplates(names);
		},
		
		enableRecursion: function(name) {
			Handlebars.registerPartial(name, templates[name]);
		},
	
		get: function (name) {
			return templates[name];
		}
	};


// End of require module.
});