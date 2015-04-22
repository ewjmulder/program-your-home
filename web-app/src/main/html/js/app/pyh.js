// Start a new require module.
define("pyh", ["jquery", "mmenu", "rest", "handlebars", "util"],
		function ($, mmenu, rest, Handlebars, util) {
	
	/////////////////////////////////////////
	// Program Your Home module variables  //
	/////////////////////////////////////////
	
	// Map with all page name->object pairs.
	var pages = {};
	// Page id counter, could be seen as a sequence to set a unique id for every page.
	var pageIdCounter = 0;
	// Array with all top level pages. Top level is the first level of the menu.
	var topLevelPages = [];
	// Map with all template name->object pairs.
	var templates = {};
	// The current page that is on screen.
	var currentPage;
	// Map with all setting name->object pairs.
	var settings = {};
	// Map with all rest client name->object pairs.
	var restClients = {};
	// The modules that are activated in the menu.
	var activeModules = [];
	
	
	/////////////////////////////////////////
	// Program Your Home class definitions //
	/////////////////////////////////////////
	
	// Enum-like definition of all Program Your Home modules.
	var Module = Object.freeze({
		ACTIVITIES: "activities",
		LIGHTS: "lights",
		DEVICES: "devices"
	});
	
	// Definition of a Page class that represents both a menu entry and a content page.
	var Page = function (name, templateName, menuName, title, isTopLevel, shouldAutoRefresh, restApiBase, resourceId, subPages) {
		pageIdCounter++;
		this.id = pageIdCounter;
		this.name = name;
		this.templateName = templateName;
		this.menuName = menuName;
		this.title = title;
		this.isTopLevel = isTopLevel;
		this.shouldAutoRefresh = shouldAutoRefresh;
		this.usesRestApi = function () { return restApiBase != null; };
		if (this.usesRestApi()) {
			if (resourceId == null) {
				this.restApiFunction = function () { return restApiBase.read(); };
			} else {
				this.restApiFunction = function () { return restApiBase.read(resourceId); };
			}
		}
		this.subPages = subPages != null ? subPages : [];
		
		// Register this instance in the pages map and possibly the top level pages array.
		pages[name] = this;
		if (this.isTopLevel) {
			topLevelPages.push(this);
		}
	};
	
	// Enum-like definition of all possible Setting type.
	var SettingType = Object.freeze({
		STRING: {name: "string", parseFunction: util.identity},
		BOOLEAN: {name: "boolean", parseFunction: $.parseJSON},
	})
	
	// Definition of a Setting class that represents one changeable setting of the application.
	var Setting = function (name, displayName, type, defaultValue) {
		var self = this;
		this.name = name;
		this.displayName = displayName;
		this.type = type;
		this.defaultValue = defaultValue;
		this.value = function () {
			var value = localStorage.getItem(self.name);
			if (value == null) {
				value = self.defaultValue;
				localStorage.setItem(self.name, value);
			} else {
				value = self.type.parseFunction(value);
			}
			return value;
		}();
		
		this.resetToDefault = function() {
			this.setNewValue(this.defaultValue);
		};
		
		// Two ways to set a new value for this setting, while also saving the value in the local storage.
		// Always use one of these functions to set a new value, instead of directly accessing the property,
		// otherwise the new value won't be saved over time.
		// The difference between the two is: one accepts only String values and will call the parse function,
		// the other only accepts a value of the right type.
		this.setNewValueFromString = function (valueString) {
			if ((typeof valueString) != "string") {
				throw "Error while setting new value from string for setting: '" + this.name + "': the value type: '" + (typeof value) + "' is not a string.";
			}
			this.setNewValue(this.type.parseFunction(valueString));
		}
		this.setNewValue = function (value) {
			if ((typeof value) != type.name) {
				throw "Error while setting new value for setting: '" + this.name + "': the value type: '" + (typeof value) + "' did not match the setting type: '" + this.type.name +"'.";
			}
			this.value = value;
			localStorage.setItem(this.name, this.value);
		};
		
		// Register this instance in the settings map.
		settings[name] = this;
	};
	
	// Create all available settings.
	//TODO: expand possible settings.
	new Setting("autoRefresh", "Auto refresh", SettingType.BOOLEAN, true);
	new Setting("slidingSubmenus", "Sliding sub-menu's", SettingType.BOOLEAN, true);
	
	/////////////////////////////////////////
	// Program Your Home main functions    //
	/////////////////////////////////////////
	
	// Create a top level page with the given name as default for all naming and title properties.
	function createNoRefreshTopLevelPage(name) {
		new Page(name, name, util.capitalizeFirstLetter(name), util.capitalizeFirstLetter(name), true, false);
	};
	
	// Create a top level page from a module name, using that name for all naming and title properties.
	function createModuleTopLevelPages(modules) {
		modules.forEach(function (module) {
			new Page(module, module, util.capitalizeFirstLetter(module), util.capitalizeFirstLetter(module), true, true, restClients[module]);
		});
	};
	
	// Create a function that handles the result of an api call.
	function createApiDoneFunction() {
		return function (result) {
			if (result.success) {
				refreshCurrentPage();
			} else {
				showError("Rest api call returned an error: " + result.error);
			}
		};
	};
	
	// Create a function that handles the result of a async function failure.
	// The source is a small description of what the async function was loading.
	function createFailFunction(source) {
		return function (jqXHR, status, error) {
			if (error == "") {
				showError(source + " could not be loaded.");					
			} else {
				showError("Loading " + source + " failed with error: " + error);
			}
		};
	};
	
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
		    .fail(createFailFunction("template " + templateName))
		    .fail(templateLoading.reject);
		});
		return templateLoading;
	}
	
	// Load the extra information that is needed to build the complete menu.
	function loadDerivedMenuItems() {
		var deviceLoading = $.Deferred();
		restClients[Module.DEVICES].read().done(function (devices) {
			// TODO: you might want to add class="ui-link" to the <a>, that is done somewhere (in jquery (ui)) already for the other <a>'s.
			for (var i = 0; i < devices.length; i++) {
				pages[Module.DEVICES].subPages.push(new Page("device-" + devices[i].name, "device", devices[i].name, "Device - " + devices[i].name, false, false, restClients[Module.DEVICES], devices[i].id));
			}
			deviceLoading.resolve();
		})
	    .fail(createFailFunction("devices"))
	    .fail(deviceLoading.reject);
		//TODO: add more menu loading stuff
		return $.when(deviceLoading);
	};
	
	// Activate the menu, meaning invoking mmenu() on the html list that was build as a basis for that.
	function activateMenu() {
		//TODO: Use iconbar extension? (http://mmenu.frebsite.nl/documentation/extensions/iconbar.html)
		//TODO: check possible usage of all extensions and add-ons! (http://mmenu.frebsite.nl/documentation/addons/)
		//TODO: Current menu item should be highlighted -> API.setSelected
		var $menu = $("#menu");
		$menu.mmenu({
			extensions			: ["theme-dark"],
			slidingSubmenus		: settings["slidingSubmenus"].value,
			header				: {
				add		: true,
				update	: true,
				title	: 'Menu'
			}
		});
		// Set the first menu item as the selected one. This assumes the default current page is the first menu item.
		var mmenuApi = $menu.data("mmenu");
		mmenuApi.setSelected($menu.find("li").first());
	};
	
	// Show an error message to the user.
	function showError(errorMessage) {
		// TODO: nicer error 'popup' -> use mmenu (http://mmenu.frebsite.nl/demo/index.html?demo=menu-popup) or some other nice jQuery (dozens of options) solution
		// TODO: also include a 'report to developer' kind of button to get feedback
		alert("Error occured: " + errorMessage);
	};
		
	// Load the page with the given name.
	function loadPage(pageName) {
		currentPage = pages[pageName];
		refreshCurrentPage();
		setTitle(currentPage.title);
	};
	
	// Refresh the current page.
	function refreshCurrentPage() {
		// TODO: Always provide a timestamp (millis) to the template? This can be used to create unique id's for DOM elements.
		// This can prevent clashes when reloading the same page. That seems to be an issue at the settings page. But not well reproducable.
		if (currentPage != null) {
			if (currentPage.usesRestApi()) {
				currentPage.restApiFunction().done(function (data) {
					setContentWithTemplate(currentPage.templateName, createTemplateDataFromCurrentPage(data));
				})
				.fail(createFailFunction("page " + currentPage.name));
			} else {
				//TODO: how to provide data input? -> for now just no data required (to be: static template page)
				setContentWithTemplate(currentPage.templateName, {});
			}
		}
	};
	
	// Create a template data input object based on the current page.
	function createTemplateDataFromCurrentPage(data) {
		var templateData = {};
		// Use the template name as property name to feed the template with.
		templateData[currentPage.templateName] = data; 
		return templateData;
	};
	
	// Set the content area with the given template.
	function setContentWithTemplate(templateName, templateData) {
		var contentHtml = templates[templateName](templateData);
		$("#content").html(contentHtml);
	};
	
	// Set the title with the given text.
	function setTitle(title) {
		$("#title").html(title);
	};
	
	// Toggle a rest resource: set it to on if currently off and vice versa.
	function toggleRestResource(module, onVerb, offVerb, id, currentlyOn) {
		var verbToUse = currentlyOn ? offVerb : onVerb;
		restClients[module][verbToUse](id).done(createApiDoneFunction()).fail(createFailFunction("rest api"));
	}

	
	/////////////////////////////////////////////
	// Program Your Home document ready logic  //
	/////////////////////////////////////////////
	
	function start() {
		// Search after the 'http://' part (length 7), find the first colon or slash from there gives us our base URL.
		var indexOfColon = window.location.href.indexOf(":", 7);
		var indexOfSlash = window.location.href.indexOf("/", 7);
		var sliceIndex = indexOfColon > -1 ? indexOfColon : indexOfSlash;
		// Take the same server on port 3737 as base URL for the Program Your Home server.
		var baseURL = window.location.href.slice(0, sliceIndex) + ":3737/";
	
		//TODO: The server can provide a list of activated modules and the UI can enable/disable pages based on that info.
		//TODO: define module / page filter based on API response from server that tells us what modules are available.
		activeModules = [Module.ACTIVITIES, Module.LIGHTS, Module.DEVICES];
	
		if (util.contains(activeModules, Module.ACTIVITIES)) {
			var restClientMain = new $.RestClient(baseURL + "main/");
			restClientMain.add("activities");
			restClientMain.activities.addVerb("start", "GET");
			restClientMain.activities.addVerb("stop", "GET");
			restClients[Module.ACTIVITIES] = restClientMain.activities;
		}
	
		if (util.contains(activeModules, Module.LIGHTS)) {
			var restClientHue = new $.RestClient(baseURL + "hue/");
			restClientHue.add("lights");
			restClientHue.lights.addVerb("on", "GET");
			restClientHue.lights.addVerb("off", "GET");
			restClients[Module.LIGHTS] = restClientHue.lights;
		}
	
		if (util.contains(activeModules, Module.DEVICES)) {
			restClientIr = new $.RestClient(baseURL + "ir/");
			restClientIr.add("devices");
			restClients[Module.DEVICES] = restClientIr.devices;
		}
		
		createModuleTopLevelPages(activeModules);
		createNoRefreshTopLevelPage("settings");
		createNoRefreshTopLevelPage("about");
		
		var templateNames = Object.keys(pages).map(function (pageName) {
			return pages[pageName].templateName;
		});
		templateNames.push("device");
		templateNames.push("menu");
	
		$.when(loadTemplates(templateNames), loadDerivedMenuItems()).then(function() {
			// To enable recursive template inclusion, used for sub pages.
			Handlebars.registerPartial("menu", templates["menu"]);
			// Load the main menu with the menu definition that we now know is available after the pre-loading.
			$("#menu").html(templates["menu"]({ pages: topLevelPages }));
			// Also, connect all menu click events to loading the appropriate page.
			Object.keys(pages).forEach(function (pageName) {
				$("#menu-link-" + pages[pageName].id).click(function () {
					loadPage(pageName);
					// Do not return false here, since that interferes with mmenu handling the situation for us.
				});
			});
			// Now we're ready to activate the actual menu on the page.
			activateMenu();
			// Load the default page.
			loadPage("activities");
			// Refresh every so often to keep in sync with server state.
			// TODO: Alternative to reload every second: have websocket connection to server and reload only upon receiving a changed event (and ideally only if change is on current page)
			setInterval(function () {
				if (settings["autoRefresh"].value && currentPage.shouldAutoRefresh) {
					refreshCurrentPage();
				}
			}, 1000);
		}, createFailFunction("menu pre-loading"));
	};

	// When the document becomes ready, we can start the application.
	$(document).ready(function () {
		start();
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
		},
		
		getSettings: function () {
			return settings;
		},
		SettingType: SettingType
	};

		
// End of require module.
});