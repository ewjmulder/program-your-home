requirejs.config({
	baseUrl: 'js/lib',
	paths: {
		// Libraries used in the application.
		jquery: 'jquery-2.1.1.min',
		mmenu: 'jquery.mmenu.min.all',
		rest: 'jquery.rest.min',
		handlebars: 'handlebars-v3.0.1.min',
		knockout: 'knockout.3.3.0.min',

		// Separate script files for the pages, naming convention is: "page" + util.capitalizeFirstLetter(pageName)
		pageActivities: '../app/pages/activities',
		pageLights: '../app/pages/lights',
		pageDevices: '../app/pages/devices',

		// Main application modules.
		pageJavascriptModules: "../app/pageJavascriptModules",
		util: '../app/util',
		pyh: '../app/pyh'
	},
	shim: {
		// Specify that the jQuery plugins depend on jQuery.
		'mmenu': {
			deps: ['jquery']
		},
		'rest': {
			deps: ['jquery']
		},
		// Handlebars uses an 'old school' global object, so we need to expose that.
		'handlebars': {
			exports: 'Handlebars'
		}
	}
});

// Start by loading the main app file.
require(['pyh']);
