requirejs.config({
	baseUrl: 'js/lib',
	paths: {
		// Libraries used in the application.
		jquery: 'jquery-2.1.1.min',
		mmenu: 'jquery.mmenu.min.all',
		jqrest: 'jquery.rest.min',
		handlebars: 'handlebars-v3.0.1.min',
		knockout: 'knockout.3.3.0.min',
		hammer: 'hammer-2.0.4.min',
		toast: 'jquery.toastmessage',
		stomp: 'stomp.min',
		sock: 'sockjs-1.0.0.min',
		loglevel: 'loglevel.min',

		// Config file, with the application specific configuration (other than the user specific settings)
		config: '../config',
		
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
		templates: '../app/templates',
		menu: '../app/menu',
		pages: '../app/pages',
		rest: '../app/rest',
		events: '../app/events',
		enums: '../app/enums',
		log: '../app/log',
		main: '../app/main'
	},
	shim: {
		'mmenu': {
			deps: ['jquery', 'hammer'],
            init: function ($, Hammer) {
            	// Set the internally used Hammer object in the scope of the module.
                this.Hammer = Hammer;
            }
		},
		'jqrest': {
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
		},
		// Expose SockJS constructor.
		'sock': {
			exports: 'SockJS'
		}
	}
});

// Start by loading the main app file.
require(['main']);
