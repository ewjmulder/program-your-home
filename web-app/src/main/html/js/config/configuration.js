//TODO: Make into an example file.
//TODO: Consider to do as much as possible through a UI of some sort

// Start a new require module.
define([], function () {

	var configMap = [];
	var deviceIconMap = [];
	deviceIconMap[1] = "server";
	deviceIconMap[2] = "desktop";
	deviceIconMap[3] = "desktop";
	deviceIconMap[4] = "desktop";
	deviceIconMap[5] = "desktop";
	
	configMap["deviceIconMap"] = deviceIconMap;
	
	return {
		getValue: function (name) {
			return configMap[name];
		}
	};

	
// End of require module.
});
