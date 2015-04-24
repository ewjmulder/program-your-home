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
	},
	
	pythagoras: function (a, b) {
		return Math.sqrt(a * a + b * b);
	},
	
	componentToHex: function (c) {
	    var hex = c.toString(16);
	    return hex.length == 1 ? "0" + hex : hex;
	},

	rgbToHex: function (r, g, b) {
	    return "#" + this.componentToHex(r) + this.componentToHex(g) + this.componentToHex(b);
	},

	hexToRgb: function (hex) {
		if (hex.charAt(0) == '#') {
			hex = hex.substring(1);
		}
	    var bigint = parseInt(hex, 16);
	    var r = (bigint >> 16) & 255;
	    var g = (bigint >> 8) & 255;
	    var b = bigint & 255;

	    return [r, g, b];
	}


// End of require module.
});