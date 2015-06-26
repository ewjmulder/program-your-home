"use strict";

define(["jquery", "BasePage", "settings", "knockout"],
		function ($, BasePage, settingsModule, ko) {

	function Settings() {
		BasePage.call(this);

		// Save settings data in local variables for easier accessing.
		var SettingType = settingsModule.SettingType;
		var settings = settingsModule.settings;
	
		var $settingsForm;
		var settingsViewModel;

		this.showPage = function () {
			init();
			doBind();
		};
		
		function SettingsViewModel() {
			Object.keys(settings).forEach(function (settingName) {
				var setting = settings[settingName];
				var settingType = setting.type;
				if (settingType == SettingType.STRING) {
					$settingsForm.append(setting.displayName + ": <input data-bind='value: " +  settingName + "' /><br />");
				} else if (settingType == SettingType.BOOLEAN) {
					$settingsForm.append(setting.displayName + ": <input type='checkbox' data-bind='checked: " +  settingName + "' /><br />");
				}
			});
	
			// A function to (re)create the settings fields.
			// Recreation can be useful for rebinding after the settings have changed by another source than the user input.
			this.createSettingsFields = function () {
				Object.keys(settings).forEach(function (settingName) {
					var setting = settings[settingName];
					settingsViewModel[settingName] = ko.observable(setting.value);
					settingsViewModel[settingName].subscribe(function (newValue) {
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
				Object.keys(settings).forEach(function (settingName) {
					settings[settingName].resetToDefault();
				});
				doBind();
			});
			
			// When the reload button is clicked, just set the browser url to the current url.
			$("#reload-app-button").click(function() {
				var appUrl = document.location.href;
				if (appUrl.indexOf("#") > -1) {
					appUrl = appUrl.substring(0, appUrl.indexOf("#"));
				}
				document.location.href = appUrl;
			});
		};
	
		function doBind() {
			// Create (or override) the settings fields on the view model object.
			settingsViewModel.createSettingsFields();
			// Unwrap the native DOM element, that is the first element in the jQuery array-like wrapper object.
			var domSettingsForm = $settingsForm[0];
			// Clear any existing bindings in the settings form.
			ko.cleanNode(domSettingsForm);
			// Tell knockout to apply the bindings to the form.
			ko.applyBindings(settingsViewModel, domSettingsForm);
		};
	};

	
	// Return the 'singleton' object as external interface.
	return new Settings();


// End of require module.	
});