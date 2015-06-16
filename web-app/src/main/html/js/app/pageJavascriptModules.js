"use strict";

// Start a new require module.
// Require all the pages, so we can expose them to other modules to be used.
// This construct is not very pretty, but it prevents 'littering' of the pyh module with lots of page related boilterplate code.
define(["util", "pageActivities", "pageLights", "pageDevices", "pageSettings", "pageAbout"],
		function (util, pageActivities, pageLights, pageDevices, pageSettings, pageAbout) {

	//TODO: find a nicer solution for this!
	var pageHolder = {};
	pageHolder.pageActivities = pageActivities;
	pageHolder.pageLights = pageLights;
	pageHolder.pageDevices = pageDevices;
	pageHolder.pageSettings = pageSettings;
	pageHolder.pageAbout = pageAbout;
	
	////////////////////////////////////////////////////////////
	// Program Your Home page javascript modules wrapper util //
	////////////////////////////////////////////////////////////

	return {
		getJavascriptModuleByPageName: function (pageName) {
		    return pageHolder["page" + util.capitalizeFirstLetter(pageName)];
		}
	};

// End of require module.
});