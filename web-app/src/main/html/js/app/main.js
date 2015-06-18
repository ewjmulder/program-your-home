"use strict";

// Start a new require module.
define(["jquery", "templates", "pages", "menu", "rest", "toast", "util", "settings", "config"],
		function ($, templates, pages, menu, rest, toast, util, settings, config) {
	
	// Save enum types from modules in local variables for easier accessing.
	var SettingType = settings.SettingType;
	
	/////////////////////////////////////////
	// Program Your Home module variables  //
	/////////////////////////////////////////
	
	// The current page that is on screen.
	var currentPage;
	// The modules that are activated in the menu.
	var activeModules = [];
	// The title object that contains all information to be displayed in the title.
	var title = {};

	
	/////////////////////////////////////////
	// Program Your Home class definitions //
	/////////////////////////////////////////
	
	// Enum-like definition of all Program Your Home modules.
	var Module = Object.freeze({
		ACTIVITIES: "activities",
		LIGHTS: "lights",
		DEVICES: "devices",
		SENSORS: "sensors"
	});
	
	// Enum-like definition of all possible setting names. So essentially the settings that are available.
	var SettingName = Object.freeze({
		SLIDING_SUBMENUS: "slidingSubmenus",
		HOME_PAGE: "homePage"
	});

	// Enum-like definition of all possible event topics.
	var EventTopic = Object.freeze({
		SUN_DEGREE: "/topic/event/sunDegree"
	});

	var Title = function () {
		this.text = "Program Your Home";
		this.sunDegree = "";
	}
	title = new Title();
	
	// Create all available settings.
	//TODO: expand possible settings.
	settings.addSetting(SettingName.SLIDING_SUBMENUS, "Sliding sub-menu's", SettingType.BOOLEAN, true);
	// TODO: make safer by using some sort of enum/list type and a drop down in the page.
	settings.addSetting(SettingName.HOME_PAGE, "Home page", SettingType.STRING, "activities");
	
	/////////////////////////////////////////
	// Program Your Home main functions    //
	/////////////////////////////////////////
	
	// Load the extra information that is needed to build the complete menu.
	function loadDerivedMenuItems() {
		var deviceLoading = $.Deferred();
		rest.readAll().done(function (devices) {
			// TODO: you might want to add class="ui-link" to the <a>, that is done somewhere (in jquery (ui)) already for the other <a>'s.
			for (var i = 0; i < devices.length; i++) {
				pages[Module.DEVICES].subPages.push(new Page("device-" + devices[i].name, "device", devices[i].name, "Device - " + devices[i].name, false, null, config.getValue("deviceIconMap")[devices[i].id], restClients[Module.DEVICES], devices[i].id));
			}
			deviceLoading.resolve();
		});
		//TODO: add more menu loading stuff
		return $.when(deviceLoading/*, moreLoading*/);
	};
	
	// Set the title with the given text.
	function setTitleText(titleText) {
		title.text = titleText;
		updateTitle();
	};

	// Update the title with the current data in the title object.
	function updateTitle() {
		//TODO: only display if sun degree sensor is available on server
		//TODO: make a handlebars template out of this
		$("#title-left").html("lefty");
		$("#title-middle").html(title.text);
		$("#title-right").html("<i class='fa fa-sun-o'></i> " + title.sundegree + "&deg;");
	}
	
	// Toggle a rest resource: set it to on if currently off and vice versa.
	function toggleRestResource(module, resourceName, onVerb, offVerb, id, currentlyOn) {
		var verbToUse = currentlyOn ? offVerb : onVerb;
		rest.verb(module, resourceName, id, verbToUse);
	}

	
	/////////////////////////////////////////////
	// Program Your Home document ready logic  //
	/////////////////////////////////////////////
	
	function start() {
		//TODO: The server can provide a list of activated modules and the UI can enable/disable pages based on that info.
		//TODO: define module / page filter based on API response from server that tells us what modules are available.
		//TODO: per module, there might also be a 'meta' availability, for instance for sensors which ones are available + what their props are
		// Some defaults will be provided as types: sun degree, temperature, humidity, sound level, light intensity, etc. + 'free format'
		// Actually, is there any way you could display a non standard sensor but just the data value? (but might still be useful of course)
		activeModules = [Module.ACTIVITIES, Module.LIGHTS, Module.DEVICES, Module.SENSORS];
	
		createRestIfModuleActive(Module.ACTIVITIES, "main", {"activities": {"start": "GET", "stop", "GET"}});
		createRestIfModuleActive(Module.LIGHTS, "hue", {"lights": {"on": "GET", "off", "GET"}});
		createRestIfModuleActive(Module.DEVICES, "ir", {"devices": {}});
		//TODO: maybe actually not use a REST client here? Just a URL call could work
		//Otherwise: do use a REST client with a real JSON response, including e.g. time, value and direction (+ speed)
		createRestIfModuleActive(Module.SENSORS, "sensors", {"sunDegree": {}});
		
		//TODO: create generic page for sensors - then use activeModules variable again.
		pages.createModuleTopLevelPages([Module.ACTIVITIES, Module.LIGHTS, Module.DEVICES]);
		createStaticTopLevelPage("settings");
		createStaticTopLevelPage("about");
		
		var templateNames = pages.all().map(function (page) {
			return page.templateName;
		});
		templateNames.push("device");
		templateNames.push("menu");
		
		$.when(templates.load(templateNames), loadDerivedMenuItems()).then(function() {
			// To enable recursive template inclusion, used for sub pages.
			Handlebars.registerPartial("menu", templates.get("menu"));
			// Load the main menu DOM tree with the menu definition that we now know is available after the pre-loading.
			$("#menu").html(templates["menu"]({ "pages": pages.allTopLevel() }));
			// Also, connect all menu click events to showing the appropriate page.
			pages.all().forEach(function (page) {
				$("#menu-link-" + page.id).click(function () {
					pages.showPage(page.name);
					// Do not return false here, since that interferes with mmenu handling the situation for us.
				});
			});
			// Now we're ready to activate the actual menu on the page.
			// We do this by calling the activateMenu function on the menu module, thereby providing the home page id.
			menu.activateMenu();
			// Show the starting page as defined in the home page setting.
			pages.showPage(settings.getSettingValue(SettingName.HOME_PAGE));
			
			events.subscribe(EventTopic.SUN_DEGREE, function (sunDegree) {
				title.sunDegree = sunDegree;
				updateTitle();
            });
		}, util.createFailFunction("menu pre-loading"));
	};
	
	function createRestIfModuleActive(module, prefix, resourceMap) {
		if (util.contains(activeModules, module)) {
			rest.create(module, prefix, resourceMap);
		}
	}
	
	// When the document becomes ready, we can start the application.
	$(document).ready(function () {
		log.setLevel(config.getValue("logLevel"));
		// Before we start the application, we should make sure that the backend server is online and reachable.
		$.ajax({url: config.getValue("serverUrl") + "meta/status/ping", timeout: 3000}).then(function (pong) {
			// If we get a response, that's fine, not need to check the body.
			// TOOD: maybe more health checks upon boot time to check?
			start();
		}, util.createFailFunction(null, "Program Your Home backend server is not online."));
	});
	
	////////////////////////////////////////////////
	// Program Your Home module exposed functions //
	////////////////////////////////////////////////

	return {
		// Start or stop an activity.
		toggleActivity: function (id, currentlyActive) {
			toggleRestResource(Module.ACTIVITIES, "start", "stop", id, currentlyActive);
		},
		// Switch a light on or off.
		toggleLight: function (id, currentlyOn) {
			toggleRestResource(Module.LIGHTS, "on", "off", id, currentlyOn);
		}
	};

		
// End of require module.
});