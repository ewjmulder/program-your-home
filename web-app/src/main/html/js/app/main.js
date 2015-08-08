"use strict";

// Start a new require module.
define(["jquery", "api", "enums", "templates", "pages", "menu", "util", "log", "settings", "config", "toast"],
		function ($, api, enums, templates, pages, menu, util, log, settings, config, toast) {
	
	var EMPTY_DATA_FUNCTION = util.immediatePromiseFunction([]);
	
	// Save enum types from modules in local variables for easier accessing.
	var Resource = enums.Resource;
	var EventTopic = enums.EventTopic;
	var SettingName = enums.SettingName;
	var SettingType = settings.SettingType;

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
		var activityLoading = $.Deferred();
		api.getActivities().done(function (activities) {
			activities.forEach(function (activity) {
				var dataFunction = function () { return api.getActivity(activity.id); };
				pages.createSubPage(Resource.ACTIVITIES.name, "activity-" + activity.name, activity.name, config.getValue("activityIconMap")[activity.id], "Activity - " + activity.name, dataFunction);
			});
			activityLoading.resolve();
		}).fail(activityLoading.reject);
		var deviceLoading = $.Deferred();
		api.getDevices().done(function (devices) {
			devices.forEach(function (device) {
				var dataFunction = function () { return api.getDevice(device.id); };
				pages.createSubPage(Resource.DEVICES.name, "device-" + device.name, device.name, config.getValue("deviceIconMap")[device.id], "Device - " + device.name, dataFunction);
			});
			deviceLoading.resolve();
		}).fail(deviceLoading.reject);
		return $.when(activityLoading, deviceLoading).promise();
	};
	
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
			createTopLevelPageByName(resource.name, function () { return api.getResources(resource); });
		});
	};
	
	
	/////////////////////////////////////////////
	// Program Your Home document ready logic  //
	/////////////////////////////////////////////
	
	function start() {
		// Disable image dragging, we want to have full control over moving over images.
/*
		$(document).on("dragstart", function(e) {
		     if (e.target.nodeName.toUpperCase() == "IMG") {
		         return false;
		     }
		});
*/		
		//TODO: create generic page for sensors - then use activeModules variable again.
		createResourceTopLevelPages([Resource.ACTIVITIES, Resource.LIGHTS, Resource.DEVICES]);
		createStaticTopLevelPage("settings");
		createStaticTopLevelPage("about");
		
		var templateNames = pages.all().map(function (page) {
			return page.moduleName;
		});
		templateNames.push("activity");
		templateNames.push("device");
		templateNames.push("menu");
		
		$.when(templates.load(templateNames), loadDerivedMenuItems()).done(function() {
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
		})
		.fail(util.createDeferredFailFunction("menu pre-loading"));
	};
	
	// When the document becomes ready, we can start the application.
	$(document).ready(function () {
		initLogging();
		// Before we start the application, we should connect the api to the backend.
		api.connect().done(start)
		.fail(util.createXHRFailFunction(null, "Server connection not successful, cannot start the client application."));
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

	// Main module is for bootstrapping and initializing. It does not have a public API.
	return {};

		
// End of require module.
});
