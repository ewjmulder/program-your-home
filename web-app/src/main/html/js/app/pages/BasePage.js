"use strict";

// Start a new require module.
// Abstract BasePage 'class' with common page logic.
define(["pages", "events", "log"],
		function (pages, events, log) {

	// TODO: Implement a 'global' resource cache for all pages. This should include the type of resource and the id as the key.
	// Reason: no possibility to be out of sync between parent/child pages cache state (eg activities)
	// Currently this seems to cause no problems, so think through thoroughly before doign this,
	// since it might have undesired side effects, like how can you still know which resources are out of sync: than that should be global as well
	// Etc, this might snowball into too much. Conclusion: only fix if found to be really necessary because of bugs that cannot be resolved otherwise.
	
	return function BasePage(eventTopicResource) {
		var self = this;
		// Default null, will be set in the init.
		this.page = null;
		// Default background color, can be overridden by subclasses.
		this.backgroundColor = "white";
		this.resourceCache = {};
		this.resourceIdsOutOfSyncWithUI = [];

		this.getPage = function () { return self.page; };

		this.isCurrentPage = function () { return self.page.isCurrentPage(); };

		this.getResources = function () {
			return Object.keys(self.resourceCache).map(function (resourceId) {
				return self.resourceCache[resourceId];
			});
		};

		this.getResource = function (id) { return self.resourceCache[id]; };
		
		// Function that is called once in the lifetime of the object (before any others), but after the DOM is available.
		// When this function is called, the resources are already cached, so they can be retrieved through getResources().
		// Use the resource data only to get an id or another static piece of information and do not perform any regular, repeating update logic.
		this.initPage = function () { return "To be implemented in subclass"; };
		// Function that is called once for every time the page is shown (selected).
		this.showPage = function () { return "To be implemented in subclass"; };
		// Signals the value changed event of a resource. This function will always be called when the event is received to inform the
		// page of the state change. Typically, the implementation should not contain any UI updates (which should be done in updateUI), but just handle general state logic.
		this.resourceChanged = function (oldResourceValue, newResourceValue) { return "To be implemented in subclass"; };
		// Update the UI for the provided resource. This function will only be called when the page is currently displayed.
		// Note: this means the event happened in the past. This function is just for doing UI updates, any state logic should be handled in resourceChanged.
		this.updateUI = function (resource) { return "To be implemented in subclass"; };
		
		// Initialize logic. Must be called first, before any other function.
		// Used as separate function instead of constructor, because now we can create the page module object before we have the page or data.
		this.init = function (page, resources) {
			if (!(resources instanceof Array)) {
				// Wrap the single resource in an array of length 1, to keep the rest of the logic generic.
				resources = [resources];
			}
			this.page = page;
			this.fillCache(resources);
			this.subscribe(resources, eventTopicResource);
			this.initPage();
		}
		
		this.fillCache = function (resources) {
			resources.forEach(function (resource) {
				self.resourceCache[resource.id] = resource;
				self.resourceIdsOutOfSyncWithUI.push(resource.id);
			});
		};
		
		this.subscribe = function (resources, eventTopicResource) {
			resources.forEach(function (resource) {
				// Register to update on state change events for this resource.
				events.subscribeForObject(eventTopicResource(resource.id),
						function (valueChangedEvent) {
							log.debug("Event change for page " + self.page.name);
							var oldResourceValue = valueChangedEvent.oldValue;
							var newResourceValue = valueChangedEvent.newValue;
							// Always update the cache with the newly received value.
							self.resourceCache[newResourceValue.id] = newResourceValue;
							// Always call the resourceChanged function to inform about the event.
							self.resourceChanged(oldResourceValue, newResourceValue);
							// Only update the UI if this is the current page, otherwise mark as out of sync.
							if (self.page.isCurrentPage()) {
								self.updateUI(newResourceValue);
							} else {
								self.resourceIdsOutOfSyncWithUI.push(resource.id);
							}
						});
			});
		};
		
		this.show = function () {
			self.showPage();
			// Sync all out of sync resources when the page is shown.
			// This mechanism allows for 'lazy loading' of UI updates.
			self.resourceIdsOutOfSyncWithUI.forEach(function (resourceId) {
				self.updateUI(self.getResource(resourceId));
			});
			// And we're completely in sync again.
			self.resourceIdsOutOfSyncWithUI = [];
		};
	};

// End of require module.
});