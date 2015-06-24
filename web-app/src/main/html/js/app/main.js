"use strict";

// Start a new require module.
define(["jquery", "events", "enums", "templates", "pages", "menu", "rest", "util", "log", "settings", "config", "toast"],
		function ($, events, enums, templates, pages, menu, rest, util, log, settings, config, toast) {
	
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
	// The title object that contains all information to be displayed in the title.
	var title = {};

	
	/////////////////////////////////////////
	// Program Your Home class definitions //
	/////////////////////////////////////////
	
	
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
		rest.readAll(Resource.DEVICES).done(function (devices) {
			// TODO: you might want to add class="ui-link" to the <a>, that is done somewhere (in jquery (ui)) already for the other <a>'s.
			for (var i = 0; i < devices.length; i++) {
				var dataFunction = function () { return rest.read(Resource.DEVICES, devices[i].id); };
				pages.createSubPage(Resource.DEVICES.name, "device-" + devices[i].name, devices[i].name, config.getValue("deviceIconMap")[devices[i].id], "Device - " + devices[i].name, dataFunction);
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
		createTopLevelPageByName(name, util.functionReturn({}));
	};
	
	// Create a top level page from a module name, using that name for all naming and title properties.
	function createResourceTopLevelPages(resources) {
		resources.forEach(function (resource) {
			createTopLevelPageByName(resource.name, function () { return rest.readAll(resource); });
		});
	};
	
	function showPage(page) {
		// Do this first, since it's async and will require time. Better start it early then.s
		pages.show(page);
		// Update the on screen title.
		//FIXME: sep title module/template or what?
		//setTitleText(currentPage.title);
	}
	
	//FIXME: before it will work:
	// - pages contains restClients ref: refactor to use loadPage function as param.
	// - solution for title handling
	// - don't expose rest clients in rest!
	
	
	/////////////////////////////////////////////
	// Program Your Home document ready logic  //
	/////////////////////////////////////////////
	
	function start() {
		//TODO: The server can provide a list of activated modules and the UI can enable/disable pages based on that info.
		//TODO: define module / page filter based on API response from server that tells us what modules are available.
		//TODO: per module, there might also be a 'meta' availability, for instance for sensors which ones are available + what their props are
		// Some defaults will be provided as types: sun degree, temperature, humidity, sound level, light intensity, etc. + 'free format'
		// Actually, is there any way you could display a non standard sensor but just the data value? (but might still be useful of course)
		activeResources = [Resource.ACTIVITIES, Resource.LIGHTS, Resource.DEVICES, Resource.SUN_DEGREE];
	
		createRestIfResourceActive(Resource.ACTIVITIES, {"start": "GET", "stop": "GET"});
		createRestIfResourceActive(Resource.LIGHTS, {"on": "GET", "off": "GET"});
		createRestIfResourceActive(Resource.DEVICES, {});
		//TODO: maybe actually not use a REST client here? Just a URL call could work
		//Otherwise: do use a REST client with a real JSON response, including e.g. time, value and direction (+ speed)
		// Yes, the last one is much cooler! :)
		createRestIfResourceActive(Resource.SUN_DEGREE, {});
		
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
			
			events.subscribeForObject(EventTopic.SUN_DEGREE_STATE, function (sunDegreeState) {
				//TODO: do something with state (display direction and degree)
				//title.sunDegree = sunDegree;
				//updateTitle();
            });
		}, util.createFailFunction("menu pre-loading"));
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
		$.ajax({url: config.getValue("serverUrl") + "meta/status/ping", timeout: 3000}).then(function (pong) {
			// If we get a response, that's fine, not need to check the body.
			// TOOD: maybe more health checks upon boot time to check?
			start();
		}, util.createFailFunction(null, "Program Your Home backend server is not online."));
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
		}
	};

		
// End of require module.
});
