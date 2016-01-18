requirejs.config({
	baseUrl: 'js/lib',
	paths: {
		// Libraries used in the application.
		jquery: 'jquery-2.1.1.min',
		mmenu: 'jquery.mmenu.min.all',
		jqrest: 'jquery.rest.min',
		ajaxQueue: 'jquery.ajaxQueue.min',
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
		BasePage: '../app/pages/BasePage',
		pageActivities: '../app/pages/Activities',
		pageLights: '../app/pages/Lights',
		pageDevices: '../app/pages/Devices',
		pageSettings: '../app/pages/Settings',
		pageAbout: '../app/pages/About',
		pageActivity: '../app/pages/Activity',
		pageDevice: '../app/pages/Device',

		// Main application modules.
		util: '../app/util',
		settings: '../app/settings',
		templates: '../app/templates',
		menu: '../app/menu',
		pages: '../app/pages',
		rest: '../app/rest',
		events: '../app/events',
		enums: '../app/enums',
		log: '../app/log',
		api: '../app/api',
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
		'ajaxQueue': {
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

// We'll allow ourselves one global variable here, so all the page template code doesn't need to use require() boilerplate to call an api function.
var api = null;
require(['api'], function (apiModule) { api = apiModule; });

// Now start the application by loading the main app file.
require(['main']);

