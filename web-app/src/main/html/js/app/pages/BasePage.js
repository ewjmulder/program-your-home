"use strict";

// Start a new require module.
// Abstract BasePage 'class' with common page logic.
define(["pages", "events"],
		function (pages, events) {

	return function BasePage(eventTopicResource) {
		var self = this;
		// Default null, will be set in the init.
		this.page = null;
		// Default background color, can be overridden bu subclasses.
		this.backgroundColor = "white";
		this.resourceCache = {};
		
		this.getResource = function (id) { return this.resourceCache[id]; };
		
		this.updateResource = function (resource) { return "To be implemented in subclass"; };
		
		// Initialize logic. Must be called first, before any other function.
		// Used as separate function instead of constructor, because now we can create the page module object before we have the page or data.
		this.init = function (page, resources) {
			this.page = page;
			this.fillCache(resources);
			this.subscribe(resources, eventTopicResource);			
		}
		
		this.fillCache = function (resources) {
			resources.forEach(function (resource) {
				self.resourceCache[resource.id] = resource;
			});
		};
		
		this.subscribe = function (resources, eventTopicResource) {
			resources.forEach(function (resource) {
				// Register to update on state change events for this resource.
				events.subscribeForObject(eventTopicResource(resource.id),
						function (resource) {
							// Always update the cache with the newly received value.
							self.resourceCache[resource.id] = resource;
							// Only update the page display if this is the current page.
							if (self.page.isCurrentPage()) {
								self.updateResource(resource);
							}
						});
			});
		};
		
		//TODO: only update 'dirty' resources, keep track in flag. This prevents full redraw when nothing in cache has changed.
		this.show = function () {
			Object.keys(this.resourceCache).forEach(function (resourceId) {
				var resource = self.resourceCache[resourceId];
				self.updateResource(resource);
			});
		};
		
	}


// End of require module.
});