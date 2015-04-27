// Start a new require module.
// Require all the pages, so we can expose them to other modules to be used.
// This construct is not very pretty, but it prevents 'littering' of the pyh module with lots of page related boilterplate code.
define(["util", "pageActivities", "pageLights", "pageDevices"],
		function (util, pageActivities, pageLights, pageDevices) {

	var self = this;
	this.pageActivities = pageActivities;
	this.pageLights = pageLights;
	this.pageDevices = pageDevices;
	
	////////////////////////////////////////////////////////////
	// Program Your Home page javascript modules wrapper util //
	////////////////////////////////////////////////////////////

	return {
		getJavascriptModuleByPageName: function (pageName) {
		    return self["page" + util.capitalizeFirstLetter(pageName)];
		}
	};

// End of require module.
});