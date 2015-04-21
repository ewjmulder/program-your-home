// Start a new require module.
define({

	/////////////////////////////////////////
	// Program Your Home utility functions //
	/////////////////////////////////////////

	// Whether or not the array contains the item.
	contains: function(array, item) {
		return $.inArray(item, array) != -1;
	},

	// Capitalize the first letter of the given string.
	capitalizeFirstLetter: function(string) {
	    return string.charAt(0).toUpperCase() + string.slice(1);
	},
	
	// The identity function, just always return the argument 'as is'.
	identity: function (obj) {
		return obj;
	}

// End of require module.
});