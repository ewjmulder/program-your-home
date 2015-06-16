//FIXME: fix use strict here
//"use strict";

// Start a new require module.
define(["jquery", "settings", "knockout"],
		function ($, settings, ko) {

	// Save settings data in local variables for easier accessing.
	var SettingType = settings.SettingType;
	var settingMap = settings.getSettingsMap();

	var $settingsForm;
	var settingsViewModel;
	
	function SettingsViewModel() {
		Object.keys(settingMap).forEach(function (settingName) {
			var setting = settingMap[settingName];
			var settingType = setting.type;
			if (settingType == SettingType.STRING) {
				$settingsForm.append(setting.displayName + ": <input data-bind='value: " +  settingName + "' /><br />");
			} else if (settingType == SettingType.BOOLEAN) {
				$settingsForm.append(setting.displayName + ": <input type='checkbox' data-bind='checked: " +  settingName + "' /><br />");
			}
		});

		// A function to (re)create the settings fields.
		// Recreation can be usefull for rebinding after the settings have changed by another source than the user input.
		this.createSettingsFields = function () {
			Object.keys(settingMap).forEach(function (settingName) {
				var setting = settingMap[settingName];
			    this[settingName] = ko.observable(setting.value);
				this[settingName].subscribe(function (newValue) {
				    setting.setNewValue(newValue);
				});
			});
		};
	};

	function init() {
		$settingsForm = $("#settingsForm");

		// Create one instance of the settings view model.
		settingsViewModel = new SettingsViewModel();
		
		// When the reset button is clicked, rest all settings and re-bind the form.
		$("#settings-reset-button").click(function() {
			Object.keys(settingMap).forEach(function (settingName) {
				settingMap[settingName].resetToDefault();
			});
			doBind();
		});
		
		// When the reload button is clicked, just set the browser url to the current url.
		$("#reload-app-button").click(function() {
			document.location.href = document.location.href;
		});
	}

	function doBind() {
		// Create (or override) the settings fields on the view model object.
		settingsViewModel.createSettingsFields();
		// Unwrap the native DOM element, that is the first element in the jQuery array-like wrapper object.
		var domSettingsForm = $settingsForm[0];
		// Clear any existing bindings in the settings form.
		ko.cleanNode(domSettingsForm);
		// Tell knockout to apply the bindings to the form.
		ko.applyBindings(settingsViewModel, domSettingsForm);
	}
	
	
	return {
		backgroundColor: "white",
		createPage: function () {
			init();
			doBind();
		},
		refreshPage: function () {
			doBind();
		}
	};


// End of require module.	
});