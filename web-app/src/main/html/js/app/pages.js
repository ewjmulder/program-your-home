"use strict";

// Start a new require module.
// All pages administration and logic.
define(["jquery", "templates", "util"],
		function ($, templates, util) {
	
	// The current page that is on screen.
	var currentPage;
	// Map with all page name->object pairs.
	var pages = {};
	// Page id counter, could be seen as a db-like sequence to set a unique id for every page.
	var pageIdCounter = 0;
	// Array with all top level pages. Top level is the first level of the menu.
	var topLevelPages = [];

	// Definition of a Page class that represents both a menu entry and a content page.
	var Page = function (name, menuName, iconName, title, isTopLevel, dataFunction, javascriptModule) {
		pageIdCounter++;
		// Unique id for this page. Can be used as a safe identifier (no special chars) in the DOM etc.
		this.id = pageIdCounter;
		// Page name, for internal reference use only. Must be unique as well.
		this.name = name;
		// The name of the template for the page contents.
		this.templateName = extractTemplateName(name);
		// The name of this page as it should be displayed in a menu.
		this.menuName = menuName;
		// The name of the icon to use for this page.
		this.iconName = iconName;
		// The title of this page as it should be displayed when this page is active.
		this.title = title;
		// Whether or not this is a top level page (meaning it should be in the top level menu items)
		this.isTopLevel = isTopLevel;
		// The array of sub pages for this page.
		this.subPages = [];
		// The function to be called to get the input data for this page.
		this.dataFunction = dataFunction;
		// The javascript module that contains the dynamic page logic.
		this.javascriptModule = javascriptModule;
		
		// Create a span element for this page, that will be used to contain the DOM tree and show / hide the page.
		this.contentElement = $(document.createElement("span"));
		this.contentElement.attr("id", "content-page-" + this.id);
		// Default to a hidden page.
		this.contentElement.addClass("hidden-page");
		// Append the page div element (unwrapped raw DOM element) to the content.
		document.getElementById("content").appendChild(this.contentElement[0]);

		// The page is loaded if the div element has child elements. That means there was content added to it.
		this.isLoaded = function() { return this.contentElement.children().length > 0; };
		
		// Whether or not this page is the currently shown page.
		this.isCurrentPage = function() { return this === currentPage; };
		
		// Register this instance in the pages map and possibly the top level pages array.
		pages[name] = this;
		if (this.isTopLevel) {
			topLevelPages.push(this);
		}
	};

	// Extract the template name from the page name. The template name is the same as the page name,
	// except when the page name contains a '-', then the template name is the page name up until the '-'.
	function extractTemplateName(name) {
		var templateName = name;
		var indexOfDash = name.indexOf("-");
		if (indexOfDash > -1) {
			templateName = name.substring(indexOfDash);
		}
		return templateName;
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
		}
	};
	
	// Load the page with the given name. Loading means processing the template with the required input data.
	// This function should be called only once per page. After the initial loading, all updates should
	// happen based on (state change) events coming in from the server.
	function loadPage(page) {
		page.dataFunction().done(function (data) {
			loadPageWithTemplate(page, createTemplateDataFromPage(page, data));
		})
		.fail(util.createFailFunction("page " + page.name));
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
		var pageDomTree = templates.get(page.templateName)(templateData);
		// Put the DOM tree in the content element of the page.
		page.contentElement.html(pageDomTree);
		page.javascriptModule.createPage(data);
	};
	
	
	return {
		createTopLevelPage: function (name, menuName, iconName, title, dataFunction, javascriptModule) {
			new Page(name, menuName, iconName, title, true, dataFunction, javascriptModule);
		},
		
		createSubPage: function (parentPageName, subPageName, menuName, iconName, title, dataFunction, javascriptModule) {
			pages[parentPageName].subPages.push(new Page(subPageName, menuName, iconName, title, false, dataFunction, javascriptModule));
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
		},

		// Show a page.
		show: function (pageName) {
			return showPage(pageName);
		},

		//FIXME: use smarter hierarchy / dependencies
		// Create the 'createPage' function for a resource page. That is, a page that
		// displays a collection of resources, indexed by id.
		createPageFunctionForResources: function (pageName, cacheMap, eventTopicResource, updatePageFunction, subscribeFunction) {
			return function (resources) {
				resources.forEach(function (resource) {
					// Register to update on state change events for this resource.
					subscribeFunction(eventTopicResource(resource.id),
							function (resource) {
								// Always update the cache with the newly received value.
								cacheMap[resource.id] = resource;
								// Only update the page display if this is the current page.
								if (pages[pageName].isCurrentPage()) {
									updatePageFunction(resource);
								}
							});
				});
			};			
		}


	};


// End of require module.
});