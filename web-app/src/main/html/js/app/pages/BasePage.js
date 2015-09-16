"use strict";

// Start a new require module.
// Abstract BasePage 'class' with common page logic.
define(["pages", "events", "log"],
		function (pages, events, log) {

	// Internally used resource cache definition. This is 'global' for all subpages,
	// so they will share a mutual resource cache and resource state cannot be out of sync between the different pages.
	// This is required to keep sane and predictable behavior when pages forward to each other based on a certain resource state.
	function ResourceCache() {
		var self = this;
		
		this.cache = {};
		this.pageResources = {};
		this.updateSubscriptions = {};

		// Each page should call this function for every resource it's interested in.
		// This function will also make sure to register for update events for this resource.
		this.enableCacheFor = function (page, eventTopicResource, resourceId) {
			// If the cache does not contain an entry yet for this event topic resource, create it
			// and initialize it empty. Otherwise it's already present, so enabled.
			if (self.cache[eventTopicResource] == null) {
				self.cache[eventTopicResource] = {};
			}
			// Currently, there is no known value for this resource, so initialize it to null.
			self.cache[eventTopicResource][resourceId] = null;
			
			// If the page is not listed yet, initialize it.
			if (self.pageResources[page.name] == null) {
				self.pageResources[page.name] = {};
			}
			// If the page / eventTopicResource combination is not known yet, initialize it with an empty array.
			if (self.pageResources[page.name][eventTopicResource] == null) {
				self.pageResources[page.name][eventTopicResource] = [];
			}
			// Register the interest of this page / eventTopicResource combination for the given resource id.
			self.pageResources[page.name][eventTopicResource].push(resourceId);
			
			// If this eventTopicResource is not listed for subscriptions yet, initialize it.
			if (self.updateSubscriptions[eventTopicResource] == null) {
				self.updateSubscriptions[eventTopicResource] = {};
			}
			// If the subscription for this resource does not yet exist, do subscribe for update events.
			if (self.updateSubscriptions[eventTopicResource][resourceId] == null) {
				// Initialize to an empty array, where the individual page callbacks can be stored.
				self.updateSubscriptions[eventTopicResource][resourceId] = [];
				// Do the actual event subscription.
				events.subscribeForObject(eventTopicResource(resourceId),
						function (valueChangedEvent) {
							var oldResourceValue = valueChangedEvent.oldValue;
							var newResourceValue = valueChangedEvent.newValue;
							log.debug("ResourceCache received changed event on eventTopicResource: '" + eventTopicResource(newResourceValue.id) + "'.");
							// Always update the cache with the newly received value.
							self.addToCache(eventTopicResource, newResourceValue);
							// Call all registered page callbacks for this update event.
							self.updateSubscriptions[eventTopicResource][resourceId].forEach(function (callback) {
								callback(oldResourceValue, newResourceValue);
							});
						});
			}
			

		};

		// Add a resource of a certain eventTopicResource to the cache.
		// This must be the latest possible value known to all pages.
		// In practice that means it should be just received from a server call or event.
		this.addToCache = function (eventTopicResource, resource) {
			self.cache[eventTopicResource][resource.id] = resource;
		};
		
		// Add a resource of a certain eventTopicResource from the cache.
		this.getFromCache = function (eventTopicResource, resourceId) {
			return self.cache[eventTopicResource][resourceId];
		};

		// Get an array of resources from the cache.
		this.getAllFromCache = function (eventTopicResource) {
			return Object.keys(self.cache[eventTopicResource]).map(function (resourceId) {
				return self.cache[eventTopicResource][resourceId];
			});
		};

		// Get an array of resources of that a certain page is interested in from the cache.
		this.getAllFromCacheForPage = function (page, eventTopicResource) {
			return self.pageResources[page.name][eventTopicResource].map(function (resourceId) {
				return self.cache[eventTopicResource][resourceId];
			});
		};
		
		// Register a callback to be called upon getting an update event for a certain eventTopicResource / resourceId combination.
		this.registerForUpdates = function (eventTopicResource, resourceId, callback) {
			self.updateSubscriptions[eventTopicResource][resourceId].push(callback);
		};

	};
	
	// Shared resource cache variable (essentially sortof a singleton) for all pages.
	var resourceCache = new ResourceCache();
	
	return function BasePage(eventTopicResource) {
		var self = this;
		// Default null, will be set in the init.
		this.page = null;
		// Default background color, can be overridden by subclasses.
		this.backgroundColor = "white";
		this.resourceIdsOutOfSyncWithUI = [];

		this.getPage = function () { return self.page; };

		this.isCurrentPage = function () { return self.page.isCurrentPage(); };

		this.getResources = function () { return resourceCache.getAllFromCacheForPage(self.page, eventTopicResource); };

		this.getResource = function (id) { return resourceCache.getFromCache(eventTopicResource, id); };
		
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
			this.subscribe(resources);
			this.initPage();
		}
		
		this.fillCache = function (resources) {
			resources.forEach(function (resource) {
				resourceCache.enableCacheFor(self.page, eventTopicResource, resource.id);
				resourceCache.addToCache(eventTopicResource, resource);
				self.resourceIdsOutOfSyncWithUI.push(resource.id);
			});
		};
		
		//TODO: move the actual subscription of the event to the cache and register the page function as a callback.
		
		this.subscribe = function (resources) {
			resources.forEach(function (resource) {
				// Register to update on state change events for this resource.
				resourceCache.registerForUpdates(eventTopicResource, resource.id, function (oldResourceValue, newResourceValue) {
					log.debug("Changed event received for eventTopicResource: '" +
							eventTopicResource(newResourceValue.id) + "' and page: '" + self.page.name + "'.");
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