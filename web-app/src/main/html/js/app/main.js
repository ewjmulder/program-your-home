"use strict";

// Start a new require module.
define(["jquery", "events", "enums", "templates", "pages", "menu", "rest", "util", "log", "settings", "config", "toast"],
		function ($, events, enums, templates, pages, menu, rest, util, log, settings, config, toast) {
	
	var EMPTY_DATA_FUNCTION = util.immediatePromiseFunction([]);
	
	// Save enum types from modules in local variables for easier accessing.
	var Module = enums.Module;
	var Resource = enums.Resource;
	var EventTopic = enums.EventTopic;
	var SettingName = enums.SettingName;
	var SettingType = settings.SettingType;
	
	/////////////////////////////////////////
	// Program Your Home module variables  //
	/////////////////////////////////////////
	
	// The resources that are activated in the menu. Most probably filtered by what is available on the server and maybe other filters.
	var activeResources = [];

	
	/////////////////////////////////////////
	// Program Your Home class definitions //
	/////////////////////////////////////////
	
	// Create all available settings.
	settings.addSetting(SettingName.SLIDING_SUBMENUS, "Sliding sub-menu's", SettingType.BOOLEAN, true);
	// TODO: make safer by using some sort of enum/list type and a drop down in the page.
	settings.addSetting(SettingName.HOME_PAGE, "Home page", SettingType.STRING, "activities");
	
	/////////////////////////////////////////
	// Program Your Home main functions    //
	/////////////////////////////////////////
	
	// Load the extra information that is needed to build the complete menu.
	function loadDerivedMenuItems() {
		var deviceLoading = $.Deferred();
		rest.readAll(Resource.DEVICES).done(function (devices) {
			devices.forEach(function (device) {
				var dataFunction = function () { return rest.read(Resource.DEVICES, device.id); };
				pages.createSubPage(Resource.DEVICES.name, "device-" + device.name, device.name, config.getValue("deviceIconMap")[device.id], "Device - " + device.name, dataFunction);
			});
			deviceLoading.resolve();
		});
		return $.when(deviceLoading/*, moreLoading*/);
	};
	
	// Toggle a rest resource: set it to on if currently off and vice versa.
	function toggleRestResource(resource, onVerb, offVerb, id, currentlyOn) {
		var verbToUse = currentlyOn ? offVerb : onVerb;
		rest.verb(resource, id, verbToUse);
	}

	function createTopLevelPageByName(name, dataFunction) {
		var nameCamelCase = util.capitalizeFirstLetter(name);
		pages.createTopLevelPage(name, nameCamelCase, config.getValue("topLevelIconMap")[name], nameCamelCase, dataFunction);
	};

	// Create a top level page with the given name as default for all naming and title properties.
	function createStaticTopLevelPage(name) {
		createTopLevelPageByName(name, EMPTY_DATA_FUNCTION);
	};
	
	// Create a top level page from a module name, using that name for all naming and title properties.
	function createResourceTopLevelPages(resources) {
		resources.forEach(function (resource) {
			createTopLevelPageByName(resource.name, function () { return rest.readAll(resource); });
		});
	};
	
	
	/////////////////////////////////////////////
	// Program Your Home document ready logic  //
	/////////////////////////////////////////////
	
	function start() {
		//TODO: The server can provide a list of activated modules and the UI can enable/disable pages based on that info.
		//TODO: define module / page filter based on API response from server that tells us what modules are available.
		//TODO: per module, there might also be a 'meta' availability, for instance for sensors which ones are available + what their props are
		// Some defaults will be provided as types: sun degree, temperature, humidity, sound level, light intensity, etc. + 'free format'
		// Actually, is there any way you could display a non standard sensor but just the data value? (but might still be useful of course)
		activeResources = [Resource.ACTIVITIES, Resource.LIGHTS, Resource.DEVICES, Resource.SUN_DEGREE, Resource.MOUSE];
	
		// TODO: put the API definition in the sep. module 'api'. Creating rest resources should still be triggered from inside main.
		createRestIfResourceActive(Resource.ACTIVITIES, {"start": "GET", "stop": "GET"});
		createRestIfResourceActive(Resource.LIGHTS, {"on": "GET", "off": "GET"});
		createRestIfResourceActive(Resource.DEVICES, {"volume/up": "GET", "volume/down": "GET", "volume/mute": "GET"});
		createRestIfResourceActive(Resource.SUN_DEGREE, {});
		createRestIfResourceActive(Resource.MOUSE, {"moveAbsolute": "GET"});
		
		//TODO: create generic page for sensors - then use activeModules variable again.
		createResourceTopLevelPages([Resource.ACTIVITIES, Resource.LIGHTS, Resource.DEVICES]);
		createStaticTopLevelPage("settings");
		createStaticTopLevelPage("about");
		
		var templateNames = pages.all().map(function (page) {
			return page.moduleName;
		});
		templateNames.push("device");
		templateNames.push("menu");
		
		$.when(templates.load(templateNames), loadDerivedMenuItems()).then(function() {
			// To enable recursive template inclusion, used for sub pages.
			templates.enableRecursion("menu");
			// Load the main menu DOM tree with the menu definition that we now know is available after the pre-loading.
			$("#menu").html(templates.get("menu")({ "pages": pages.allTopLevel() }));
			// Also, connect all menu click events to showing the appropriate page.
			pages.all().forEach(function (page) {
				$("#menu-link-" + page.id).click(function () {
					pages.show(page.name);
					// Do not return false here, since that interferes with mmenu handling the situation for us.
				});
			});
			// Now we're ready to activate the actual menu on the page.
			// We do this by calling the activateMenu function on the menu module, thereby providing the home page id.
			menu.activate();
			// Show the starting page as defined in the home page setting.
			pages.show(settings.getSettingValue(SettingName.HOME_PAGE));
			
			events.subscribeForObject(EventTopic.SUN_DEGREE_STATE, function (sunDegreeChangedEvent) {
				//TODO: do something with state (display direction and degree)
            });
		}, util.createDeferredFailFunction("menu pre-loading"));
	};
	
	function createRestIfResourceActive(resource, verbMap) {
		if (util.contains(activeResources, resource)) {
			rest.create(resource, verbMap);
		}
	}
	
	// When the document becomes ready, we can start the application.
	$(document).ready(function () {
		initLogging();
		// Before we start the application, we should make sure that the backend server is online and reachable.
		$.ajax({url: config.getValue("serverUrl") + "meta/status/ping", timeout: 3000,
				headers: {"Pyh-Basic-Authentication-Username": "user", "Pyh-Basic-Authentication-Password": "password"}}).then(function (pong) {
			// If we get a response, that's fine, not need to check the body.
			// TODO: maybe more health checks upon boot time to check?
			start();
		}, util.createXHRFailFunction(null, "Program Your Home backend server is not online."));
	});
	
	function initLogging() {
		if (config.getValue("showErrorsOnScreen")) {
			log.setErrorCallback(showErrorMessage);
		}
		log.setLevel(config.getValue("logLevel"));		
	}

	function showErrorMessage(error) {
		// TODO: also include a 'report to developer' kind of button to get feedback
		$().toastmessage("showToast", {
		    text     : error,
		    sticky   : true,
		    type     : "error",
		    position : "middle-center"
		});
	}

	////////////////////////////////////////////////
	// Program Your Home module exposed functions //
	////////////////////////////////////////////////

	return {
		// Start or stop an activity.
		toggleActivity: function (id, isActive) {
			toggleRestResource(Resource.ACTIVITIES, "start", "stop", id, isActive);
		},
		// Switch a light on or off.
		toggleLight: function (id, isOn) {
			toggleRestResource(Resource.LIGHTS, "on", "off", id, isOn);
		},
		// TODO: put this endless list in a sep. module: api.
		volumeUpDevice: function(deviceId) {
			rest.verb(Resource.DEVICES, deviceId, "volume/up");
		},
		volumeDownDevice: function(deviceId) {
			rest.verb(Resource.DEVICES, deviceId, "volume/down");
		},
		volumeMuteDevice: function(deviceId) {
			rest.verb(Resource.DEVICES, deviceId, "volume/mute");
		},
		//TODO: this is no rest at all! use simple $.get() for this and sanitize URL
		// Also consider not using RestController on backend? (although default JSON is nice)
		// Can still use that as result object for 'RPC' over HTTP
		moveMouseAbsolute: function(x, y) {
			rest.verbParam(Resource.MOUSE, 1, "moveAbsolute", x + "," + y);
		}
	};

		
// End of require module.
});
