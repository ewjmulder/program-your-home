// Start a new require module.
define(["jquery", "util"],
		function ($, util) {
	
	// Map with all setting name->object pairs.
	var settings = {};

	// Enum-like definition of all possible Setting type.
	var SettingType = Object.freeze({
		STRING: {name: "string", parseFunction: util.identity},
		BOOLEAN: {name: "boolean", parseFunction: $.parseJSON},
	});

	// Enum-like definition of all possible Setting names.
	var SettingName = Object.freeze({
		AUTO_REFRESH: "autoRefresh",
		SLIDING_SUBMENUS: "slidingSubmenus",
		HOME_PAGE: "homePage"
	});

	//TODO: Create a spearate settings module, so we don't require circular dependencies.
	// Definition of a Setting class that represents one changeable setting of the application.
	var Setting = function (name, displayName, type, defaultValue) {
		var self = this;
		this.name = name;
		this.displayName = displayName;
		this.type = type;
		this.defaultValue = defaultValue;
		this.value = function () {
			var value = localStorage.getItem(self.name);
			if (value == null) {
				value = self.defaultValue;
				localStorage.setItem(self.name, value);
			} else {
				value = self.type.parseFunction(value);
			}
			return value;
		}();
		
		this.resetToDefault = function() {
			this.setNewValue(this.defaultValue);
		};
		
		// Two ways to set a new value for this setting, while also saving the value in the local storage.
		// Always use one of these functions to set a new value, instead of directly accessing the property,
		// otherwise the new value won't be saved over time.
		// The difference between the two is: one accepts only String values and will call the parse function,
		// the other only accepts a value of the right type.
		this.setNewValueFromString = function (valueString) {
			if ((typeof valueString) != "string") {
				throw "Error while setting new value from string for setting: '" + this.name + "': the value type: '" + (typeof value) + "' is not a string.";
			}
			this.setNewValue(this.type.parseFunction(valueString));
		}
		this.setNewValue = function (value) {
			if ((typeof value) != type.name) {
				throw "Error while setting new value for setting: '" + this.name + "': the value type: '" + (typeof value) + "' did not match the setting type: '" + this.type.name +"'.";
			}
			this.value = value;
			localStorage.setItem(this.name, this.value);
		};
		
		// Register this instance in the settings map.
		settings[name] = this;
	};


	return {
		SettingName: SettingName,
		SettingType: SettingType,

		// TODO: Type check on SettingName, SettingValue (and name not already in map
		addSetting: function (name, displayName, type, defaultValue) {
			new Setting(name, displayName, type, defaultValue);
		},
		
		getSettingsMap: function () {
			return settings;
		},

		getSetting: function (settingName) {
			return settings[settingName];
		},

		// TODO: Type check on SettingName
		getSettingValue: function (settingName) {
			return settings[settingName].value;
		}
	};


// End of require module.
});