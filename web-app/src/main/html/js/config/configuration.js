//TODO: Also make an example file.
//TODO: consider using a .properties file or plain json file and some javascript parsing logic.

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

	var topLevelIconMap = [];
	topLevelIconMap["activities"] = "star";
	topLevelIconMap["lights"] = "lightbulb-o";
	topLevelIconMap["devices"] = "hdd-o";
	topLevelIconMap["settings"] = "gears";
	topLevelIconMap["about"] = "info-circle";
	
	configMap["topLevelIconMap"] = topLevelIconMap;

	return {
		getValue: function (name) {
			return configMap[name];
		}
	};

	
// End of require module.
});
