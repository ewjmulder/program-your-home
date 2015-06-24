"use strict";

//Start a new require module.
define(["jquery", "BasePage", "enums", "util"],
		function ($, BasePage, enums, util) {

	function Lights() {
		BasePage.call(this, enums.EventTopic.HUE_LIGHTS);

		this.backgroundColor = "#CCCCCC";

		this.isOn = function (id) {
			return this.getResource(id).on;
		};

		this.updateResource = function (light) {
			var contentElement = document.getElementById("draw-light-" + light.id);
			if (light.type == "HUE_FULL_COLOR_BULB") {
				// Create a new image.
				var lightImage = new Image();
				
				$(lightImage).load(function() {
					// How much extra space we'll take around the light image (for drawing the glow).
					var extraWidth = lightImage.width / 2;
					var extraHeight = extraWidth / 2;
					
					// Canvas size is image size + extra space.
					var canvasWidth = lightImage.width + extraWidth;
					var canvasHeight = lightImage.height + extraHeight;
			
					// Create a canvas element dynamically, with the right size and add it to the DOM tree.
					var canvasElement = document.createElement('canvas');
					canvasElement.width = canvasWidth;
					canvasElement.height = canvasHeight;
					// Remove all current child elements.
					$(contentElement).empty();
					contentElement.appendChild(canvasElement);
			
					// Center image horizontally.
					var imageX = canvasWidth / 2 - lightImage.width / 2;
					// Put image at the bottom of the canvas.
					var imageY = extraHeight;
			
					// The location and size of the shining part of the bulb in the image, relative to the canvas top left corner.
					var shineX = imageX + 0;
					var shineY = imageY + 0;
					// Magix numbers, different per image.
					var shineWidth = lightImage.width;
					var shineHeight = 33;
			
					// Get the drawing context from the canvas.
					var context = canvasElement.getContext('2d');
					
					// Draw the image on the canvas.
					context.drawImage(lightImage, imageX, imageY);
	
					if (light.on) {
						// Get the pixel data from the canvas.
						// TODO: take smaller area
						var imgData = context.getImageData(0, 0, canvasWidth, canvasHeight);
				
						// The center of the light is in the middle of the shine area.
						var lightCenterX = shineX + shineWidth / 2;
						var lightCenterY = shineY + shineHeight / 2;	
				
						// The size of the elliptical glow.
						var ellipseHorizontalRadius = (shineWidth + extraWidth) / 2;
						var ellipseVerticalRadius = (shineHeight / 2) + extraHeight;
				
						// For every pixel in the shining area, calculate the right value.
						for (var y = shineY - extraHeight; y < shineY + shineHeight + extraHeight; y++) {
							for (var x = shineX - (extraWidth / 2); x < shineX + shineWidth + extraWidth; x++) {
								// TODO: move stuff to math util functions (in util module)
								// Adjecent side of the triangle.
								var distanceX = Math.abs(x - lightCenterX);
								// Opposite side of the triangle.
								var distanceY = Math.abs(y - lightCenterY);
								//TODO: consider not using sqrt, but plain x and y distance for speed
								// Hypothenuse (diagonal) side of the triangle.
								var distance = util.pythagoras(distanceX, distanceY);
								// Calculate the angle in the triangle according to the formula sin(angle) = opposite / hypothenuse
								var circleAngle = Math.asin(distanceY / distance);
								if (isNaN(circleAngle)) {
									// For a distance of 0, we'll take a very small (dummy) angle. The fraction will be 0 anyway.
									circleAngle = 0.001;
								}
								
								// Calculate the ellipse angle from the circle angle.
								// Special thanks to: http://stackoverflow.com/questions/17762077/how-to-find-the-point-on-ellipse-given-the-angle
								var ellipseAngle = Math.atan((ellipseHorizontalRadius * Math.tan(circleAngle)) / ellipseVerticalRadius);
				
								// The ellipse coordinates at this angle.
								var ellipseX = ellipseHorizontalRadius * Math.cos(ellipseAngle);
								var ellipseY = ellipseVerticalRadius * Math.sin(ellipseAngle);
								// The distance from the center to the edge of the ellipse at this angle.
								var ellipseDistance = util.pythagoras(ellipseX, ellipseY);
								
								// How far is the 'current pixel' from the center, relative to the edge of the ellipse.
								var distanceFraction = distance / ellipseDistance;
								
								// Calculate a dim factor that defines how fast or slow the glowing will fade.
								// A smaller value means closer to the center, so should be a brighter glow.
								var minValue = distanceFraction * distanceFraction;
								var maxValue = distanceFraction;
								// A small value for dim should be a less bright glow.
								var finalFraction = minValue + ((10000 - light.dim) / 10000) * (maxValue - minValue);
				
								// Decide if the current pixel is a background pixel based on the color value (should be all 0).
								var isBackgroundColor = imgData.data[(y * canvasWidth + x) * 4 + 0] == 0
										&& imgData.data[(y * canvasWidth + x) * 4 + 1] == 0
										&& imgData.data[(y * canvasWidth + x) * 4 + 2] == 0;
								// When we have a background pixel or are in or above the shining area,
								// and furthermore we're 'inside' the glow ellipse, draw a pixel with the right color and transparency.
								if ((isBackgroundColor || (!isBackgroundColor && y < imageY + shineHeight)) && distance <= ellipseDistance) {
									// TODO: let fraction influence the actual max (1) and min (0) of the alpha
									context.globalAlpha = 1 - finalFraction;
									if (isBackgroundColor) {
										// Dim some extra for the background, so the glow has an 'edge' after the bulb itself.
										context.globalAlpha = context.globalAlpha * 0.75;
									}
									// The color to use for the pixel is the color of the light.
									context.fillStyle = util.rgbToHex(light.colorRGB.red, light.colorRGB.green, light.colorRGB.blue);
									// Draw a pixel by filling a rectangle of size 1x1. This is the only (known) way that takes the globalAlpha into account.
									context.fillRect(x, y, 1, 1);
								} else {
									// If we do not meet the criteria specified above, this pixel stays as it is: either background or light bulb image.
								}
							}
						}
					} else {
						//TODO: darken / black & white to signal it's off?
					}
				}).attr({
					// Set the source at the end, so we'll be sure the load() function will be called.
					src: "img/hue-full-color-bulb-small.png",
				});
			} else {
				$(contentElement).html("TODO: support multiple light types!<br />");
			}
		};
	};
	
	// Return the 'singleton' object as external interface.
	return new Lights();


// End of require module.
});