"use strict";

// Start a new require module.
// All pages administration and logic.
define(["jquery", "config", "util"],
		function ($, config, util) {
	
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
			setTitleText(currentPage.title);
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
		
		getPageByName: function (pageName) {
			return pages[pageName];
		}
		
	};


// End of require module.
});