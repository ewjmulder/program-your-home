requirejs.config({
	baseUrl: 'js/lib',
	paths: {
		// Libraries used in the application.
		jquery: 'jquery-2.1.1.min',
		mmenu: 'jquery.mmenu.min.all',
		rest: 'jquery.rest.min',
		handlebars: 'handlebars-v3.0.1.min',
		knockout: 'knockout.3.3.0.min',
		hammer: 'hammer-2.0.4.min',
		toast: 'jquery.toastmessage',

		// Config file, with the application specific configuration (other than the user specific settings)
		config: '../config/configuration',
		
		// Separate script files for the pages, naming convention is: "page" + util.capitalizeFirstLetter(pageName)
		pageActivities: '../app/pages/activities',
		pageLights: '../app/pages/lights',
		pageDevices: '../app/pages/devices',
		pageSettings: '../app/pages/settings',
		pageAbout: '../app/pages/about',

		// Main application modules.
		pageJavascriptModules: "../app/pageJavascriptModules",
		util: '../app/util',
		settings: '../app/settings',
		pyh: '../app/pyh'
	},
	shim: {
		'mmenu': {
			deps: ['jquery', 'hammer'],
            init: function ($, Hammer) {
            	// Set the internally used Hammer object in the scope of the module.
                this.Hammer = Hammer;
            }
		},
		'rest': {
			deps: ['jquery']
		},
		'toast': {
			deps: ['jquery'],
		},
		// Handlebars uses an 'old school' global object, so we need to expose that.
		'handlebars': {
			exports: 'Handlebars'
		},
		// Expose Hammer constructor.
		'hammer': {
			exports: 'Hammer'
		}
	}
});

// Start by loading the main app file.
require(['pyh']);
