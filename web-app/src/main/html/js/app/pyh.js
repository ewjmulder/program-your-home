// Start a new require module.
define(["jquery", "mmenu", "rest", "handlebars", "hammer", "toast", "util", "pageJavascriptModules", "settings", "config"],
		function ($, mmenu, rest, Handlebars, Hammer, toast, util, pageJavascriptModules, settings, config) {
	
	// Save settings data in local variables for easier accessing.
	var SettingName = settings.SettingName;
	var SettingType = settings.SettingType;
	
	/////////////////////////////////////////
	// Program Your Home module variables  //
	/////////////////////////////////////////
	
	// The base URL where we can reach the Program Your Home server.
	var baseURL = "";
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
	var Page = function (name, templateName, menuName, title, isTopLevel, needsRefreshing, javascriptModule, iconName, restApiBase, resourceId, subPages) {
		pageIdCounter++;
		this.id = pageIdCounter;
		this.name = name;
		this.templateName = templateName;
		this.menuName = menuName;
		this.title = title;
		this.isTopLevel = isTopLevel;
		this.needsRefreshing = needsRefreshing;
		this.javascriptModule = javascriptModule;
		this.iconName = iconName;
		this.hasJavascriptModule = function () { return this.javascriptModule != null; };
		this.usesRestApi = function () { return restApiBase != null; };
		if (this.usesRestApi()) {
			if (resourceId == null) {
				this.restApiFunction = function () { return restApiBase.read(); };
			} else {
				this.restApiFunction = function () { return restApiBase.read(resourceId); };
			}
		}
		this.subPages = subPages != null ? subPages : [];
		
		// Create a div element for this page, that will be used to contain the DOM tree and show / hide the page.
		this.contentElement = $(document.createElement("span"));
		this.contentElement.attr("id", "content-page-" + this.id);
		// Default to a hidden page.
		this.contentElement.addClass("hidden-page");
		// Append the page div element (unwrapped raw DOM element) to the content.
		document.getElementById("content").appendChild(this.contentElement[0]);

		// The page is loaded if the div element has child elements. That means there was content added to it.
		this.isLoaded = function() { return this.contentElement.children().length > 0; };
		
		// Register this instance in the pages map and possibly the top level pages array.
		pages[name] = this;
		if (this.isTopLevel) {
			topLevelPages.push(this);
		}
	};
	
	// Create all available settings.
	//TODO: expand possible settings.
	settings.addSetting(SettingName.AUTO_REFRESH, "Auto refresh", SettingType.BOOLEAN, true);
	settings.addSetting(SettingName.SLIDING_SUBMENUS, "Sliding sub-menu's", SettingType.BOOLEAN, true);
	// TODO: make safer by using some sort of enum/list type and a drop down in the page.
	settings.addSetting(SettingName.HOME_PAGE, "Home page", SettingType.STRING, "activities");
	
	/////////////////////////////////////////
	// Program Your Home main functions    //
	/////////////////////////////////////////
	
	// Create a top level page with the given name as default for all naming and title properties.
	function createNoRefreshTopLevelPage(name) {
		createPageByName(name, false, false);
	};
	
	// Create a top level page from a module name, using that name for all naming and title properties.
	function createModuleTopLevelPages(modules) {
		modules.forEach(function (module) {
			createPageByName(module, true, true);
		});
	};
	
	function createPageByName(name, needsRefreshing, usesRest) {
		var nameCamelCase = util.capitalizeFirstLetter(name);
		var javascriptModule = pageJavascriptModules.getJavascriptModuleByPageName(name);
		new Page(name, name, nameCamelCase, nameCamelCase, true, needsRefreshing, javascriptModule, config.getValue("topLevelIconMap")[name], usesRest ? restClients[name] : null);
	}
	
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
	function createFailFunction(source, customErrorMessage) {
		return function (jqXHR, status, error) {
			if (error == "") {
				if (customErrorMessage != null) {
					showError(customErrorMessage);
				} else {
					showError(source + " could not be loaded.");
				}
			} else {
				if (customErrorMessage != null) {
					showError(customErrorMessage);
				} else {
					showError("Loading " + source + " failed with error: " + error);
				}
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
				pages[Module.DEVICES].subPages.push(new Page("device-" + devices[i].name, "device", devices[i].name, "Device - " + devices[i].name, false, false, null, config.getValue("deviceIconMap")[devices[i].id], restClients[Module.DEVICES], devices[i].id));
			}
			deviceLoading.resolve();
		})
	    .fail(createFailFunction("devices"))
	    .fail(deviceLoading.reject);
		//TODO: add more menu loading stuff
		return $.when(deviceLoading);
	};
	
	// Activate the menu, meaning invoking mmenu() on the html list that was dynamically build for that.
	function activateMenu() {
		// Override the Hammer default recognizers. This makes the mmenu code use these instead of the defaults.
		// - Only Pan and Press, no other fancy stuff needed.
		// - Big move margin and no minimum time for press, so selecting a menu item is 'forgiving'.
		Hammer.defaults.preset =
			[
			    [Hammer.Pan, {threshold: 25, direction: Hammer.DIRECTION_HORIZONTAL}],
			    [Hammer.Press, { threshold: 25, time: 0}]
            ];

		// Create a hammer listener on the menu.
		var hammer = new Hammer($("#menu")[0], {});
		// When a press is detected, programmatically click on the center of the press, effectively selecting an underlying menu item (if present).
		hammer.on("pressup", function (e) {
			$(document.elementFromPoint(e.center.x, e.center.y)).click();  
		});

		var $menu = $("#menu");
		$menu.mmenu({
			extensions			: [// Dark background with bright text instead of the other way around.
			          			   "theme-dark",
			          			   // Extend 'border line' between menu items all the way to the left.
			          			   "border-full",
			          			   // Display a set of icons always on screen for fast top level menu switching.
			          			   "iconbar",
			          			   // Wraps the menu item text into multiple lines if needed.
			          			   "multiline",
			          			   // Display a page shadow on the menu when the menu is activated.
			          			   "pageshadow"],
			// Whether to slide to the right into a separate submenu (true), or open the submenu below a menu item (false).
			slidingSubmenus		: settings.getSettingValue(SettingName.SLIDING_SUBMENUS),
			// If the menu is open and the browser 'back' button is pressed, it closes the menu instead of going back a page.
			backButton			: {
				close	: true
			},
			// Display a menu header, that defaults to 'Menu' at the top level, and changes when selecting submenus.
			header				: {
				add		: true,
				update	: true,
				title	: 'Menu'
			},
			// Display a fixed menu footer.
			footer: {
				add: 	true,
			    content: "(c) Erik Mulder",
			},
			// Allow the menu to be 'dragged open' from the left.
			dragOpen			: {
				maxStartPos	: 100,
				open		: true,
				pageNode	: $("#menu"),
				threshold	: 50
			}
		});

		// Get the mmenu API object.
		var mmenuApi = $menu.data("mmenu");
		// Set the menu item that is defined as the home page as the selected one.
		mmenuApi.setSelected($("#menu-" + pages[settings.getSettingValue(SettingName.HOME_PAGE)].id));		

		// Bind to the mousedown and touchstart events to be able to close the menu if needed.
		// This is needed to work around a bug on the Android browser, where the touchstart event is somehow
		// not coming through if the menu is opened. With this logic, we can swipe-close the menu like we like it. :)
		$("#body").off("mousedown touchstart").on("mousedown touchstart", function (e) {
			closeMenuIfOpenAndOnPage(e, mmenuApi);
		});
	};
	
	function closeMenuIfOpenAndOnPage(e, mmenuApi) {
		// The menu is open if the menu element has the css class mm-opened.
		var menuOpen = $('#menu').hasClass('mm-opened');
		// Find the closest element with an id of either menu, page or mm-blocker.
		// If that is the page (or maybe on the blocker, but not on the menu), the event was on the page.
		var notOnMenu = $(e.target).closest("#menu, #page, #mm-blocker")[0].id != "menu";
		if (menuOpen && notOnMenu) {
			mmenuApi.close();				
		}
	};
	
	// Show an error message to the user.
	function showError(errorMessage) {
		// TODO: also include a 'report to developer' kind of button to get feedback
		$().toastmessage("showToast", {
		    text     : errorMessage,
		    sticky   : true,
		    type     : "error",
		    position : "middle-center"
		});
	};
		
	// Load the page with the given name. This is the only function that should modify the currentPage variable.
	function loadPage(pageName) {
		if (currentPage == null || currentPage.name != pageName) {
			// If the 'old current page' exists, hide it.
			if (currentPage != null) {
				currentPage.contentElement.removeClass("current-page");
				currentPage.contentElement.addClass("hidden-page");
			} else {
				// First page load, remove loading text.
				$("#page-loading-content").addClass("hidden-page");
			}
			// Set the 'new current page'.
			currentPage = pages[pageName];
			// Show the 'new current page'.
			currentPage.contentElement.removeClass("hidden-page");
			currentPage.contentElement.addClass("current-page");
			// Update the on screen title.
			setTitle(currentPage.title);
			// Update the background color. Should be done on the content tag, so it fills the whole content area.
			if (currentPage.hasJavascriptModule()) {
				// Set the background color using the javascript module exposed property.
				$("#content").css("background-color", currentPage.javascriptModule.backgroundColor);
			} else {
				// Default to white.
				$("#content").css("background-color", "white");
			}
		}
		refreshCurrentPage();
	};
	
	// Refresh the current page.
	function refreshCurrentPage() {
		// TODO: Always provide a timestamp (millis) to the template? This can be used to create unique id's for DOM elements.
		// This can prevent clashes when reloading the same page. That seems to be an issue at the settings page. But not well reproducable.
		if (currentPage != null) {
			if (currentPage.usesRestApi()) {
				currentPage.restApiFunction().done(function (data) {
					refreshCurrentPageWithTemplate(currentPage.templateName, createTemplateDataFromCurrentPage(data));
				})
				.fail(createFailFunction("page " + currentPage.name));
			} else {
				//TODO: how to provide data input? -> for now just no data required (to be: static template page)
				refreshCurrentPageWithTemplate(currentPage.templateName, createTemplateDataFromCurrentPage({}));
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
	
	// Refresh the current page, using the templateData to feed the template.
	// Also take into account if the page loads for the first time or not.
	function refreshCurrentPageWithTemplate(templateName, templateData) {
		// 'unwrap' the data for direct usage.
		var data = templateData[currentPage.templateName];
		if (!currentPage.isLoaded()) {
			var pageDomTree = templates[templateName](templateData);
			// Put the DOM tree in the div element the current page.
			currentPage.contentElement.html(pageDomTree);
			if (currentPage.hasJavascriptModule()) {
				currentPage.javascriptModule.createPage(data);
			}
		} else {
			if (currentPage.hasJavascriptModule()) {
				currentPage.javascriptModule.refreshPage(data);
			}			
		}
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
			// Load the default page as defined in the home page setting.
			loadPage(settings.getSettingValue(SettingName.HOME_PAGE));
			// Refresh every so often to keep in sync with server state.
			// TODO: Alternative to reload every second: have websocket connection to server and reload only upon receiving a changed event (and ideally only if change is on current page)
			setInterval(function () {
				if (settings.getSettingValue(SettingName.AUTO_REFRESH) && currentPage.needsRefreshing) {
					refreshCurrentPage();
				}
			}, 1000);
		}, createFailFunction("menu pre-loading"));
	};

	// When the document becomes ready, we can start the application.
	$(document).ready(function () {
		// Search after the 'http://' part (length 7), find the first colon or slash from there gives us our base URL.
		var indexOfColon = window.location.href.indexOf(":", 7);
		var indexOfSlash = window.location.href.indexOf("/", 7);
		var sliceIndex = indexOfColon > -1 ? indexOfColon : indexOfSlash;
		// Take the same server on port 3737 as base URL for the Program Your Home server.
		baseURL = window.location.href.slice(0, sliceIndex) + ":3737/";

		// Before we start the application, we should make sure that the backend server is online and reachable.
		$.ajax({url: baseURL + "meta/status/ping", timeout: 3000}).then(function (pong) {
			// If we get a response, that's fine, not need to check the body.
			// TOOD: maybe more health checks upon boot time to check?
			start();
		}, createFailFunction(null, "Program Your Home backend server is not online."));
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
