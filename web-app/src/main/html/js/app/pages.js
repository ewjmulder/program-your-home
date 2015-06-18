"use strict";

// Start a new require module.
// All pages administration and logic.
define(["jquery", "config", "util", "pageJavascriptModules"],
		function ($, config, util, pageJavascriptModules) {
	
	// Map with all page name->object pairs.
	var pages = {};
	// Page id counter, could be seen as a sequence to set a unique id for every page.
	var pageIdCounter = 0;
	// Array with all top level pages. Top level is the first level of the menu.
	var topLevelPages = [];

	// Definition of a Page class that represents both a menu entry and a content page.
	var Page = function (name, templateName, menuName, title, isTopLevel, javascriptModule, iconName, restApiBase, resourceId, subPages) {
		pageIdCounter++;
		this.id = pageIdCounter;
		this.name = name;
		this.templateName = templateName;
		this.menuName = menuName;
		this.title = title;
		this.isTopLevel = isTopLevel;
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
		
		// Create a span element for this page, that will be used to contain the DOM tree and show / hide the page.
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
	
	function createPageByName(name, usesRest) {
		var nameCamelCase = util.capitalizeFirstLetter(name);
		var javascriptModule = pageJavascriptModules.getJavascriptModuleByPageName(name);
		new Page(name, name, nameCamelCase, nameCamelCase, true, javascriptModule, config.getValue("topLevelIconMap")[name], usesRest ? restClients[name] : null, null, []);
	}

	// Show the page with the given name. This is the only function that should modify the currentPage variable.
	function showPage(pageName) {
		if (currentPage == null || currentPage.name != pageName) {
			// If the 'old current page' exists, hide it.
			if (currentPage != null) {
				currentPage.contentElement.removeClass("current-page");
				currentPage.contentElement.addClass("hidden-page");
			} else {
				// This is the first page load, so remove the 'loading' text.
				$("#page-loading-content").addClass("hidden-page");
			}
			// Set the 'new current page'.
			currentPage = pages[pageName];
			// Load the page if it's shown for the first time (one time action only).
			if (!currentPage.isLoaded()) {
				loadPage(currentPage);
			}
			// Show the 'new current page'.
			currentPage.contentElement.removeClass("hidden-page");
			currentPage.contentElement.addClass("current-page");
			// Update the on screen title.
			//FIXME: sep title module/template or what?
			//setTitleText(currentPage.title);
			// Update the background color. Should be done on the content tag, so it fills the whole content area.
			if (currentPage.hasJavascriptModule()) {
				// Set the background color using the javascript module exposed property.
				$("#content").css("background-color", currentPage.javascriptModule.backgroundColor);
			} else {
				// Default to white.
				$("#content").css("background-color", "white");
			}
		}
	};
	
	// Load the page with the given name. Loading means processing the template with the required input data.
	// This function should be called only once per page. After the initial loading, all updates should
	// happen based on (state change) events coming in from the server.
	function loadPage(page) {
		// TODO: less specific, maybe some load function property?
		if (page.usesRestApi()) {
			page.restApiFunction().done(function (data) {
				loadPageWithTemplate(page, createTemplateDataFromPage(page, data));
			})
			.fail(util.createFailFunction("page " + page.name));
		} else {
			//TODO: how to provide data input? -> for now just no data required (to be: static template page)
			loadPageWithTemplate(page, createTemplateDataFromPage(page, {}));
		}
	};
	
	// Create a template data input object based on the provided page.
	function createTemplateDataFromPage(page, data) {
		var templateData = {};
		// Use the template name as property name to feed the template with.
		//TODO: assumption is that the template requires an 'outer' object with a named prop to start traversal.
		//TODO: try out if you can feed the (eg) collection directly and then using the 'this' keyword (or similar) in the handlebars template
		templateData[page.templateName] = data; 
		return templateData;
	};
	
	// Load the provided page, using the templateData to feed the template.
	function loadPageWithTemplate(page, templateData) {
		// 'unwrap' the data for direct usage.
		var data = templateData[page.templateName];
		var pageDomTree = templates[templateName](templateData);
		// Put the DOM tree in the content element of the page.
		page.contentElement.html(pageDomTree);
		if (page.hasJavascriptModule()) {
			page.javascriptModule.createPage(data);
		}
	};
	
	return {
		// Create a top level page with the given name as default for all naming and title properties.
		createStaticTopLevelPage: function (name) {
			createPageByName(name, false);
		},
		
		// Create a top level page from a module name, using that name for all naming and title properties.
		createModuleTopLevelPages: function (modules) {
			modules.forEach(function (module) {
				createPageByName(module, true);
			});
		},
		
		createSubPage: function (parentPageName, subPageName, templateName, menuName, title, javascriptModule, iconName, restApiBase, resourceId) {
			pages[parentPageName].subPages.push(subPageName, templateName, menuName, title, false, javascriptModule, iconName, restApiBase, resourceId, []);
		},

		// Array with all pages.
		all: function () {
			return Object.keys(pages).map(function (pageName) {
			    return pages[pageName];
			});
		},

		// Array with all top level pages.
		allTopLevel: function () {
			return topLevelPages;
		},
		
		// Find the page with the given name.
		byName: function (pageName) {
			return pages[pageName];
		}
		
	};


// End of require module.
});