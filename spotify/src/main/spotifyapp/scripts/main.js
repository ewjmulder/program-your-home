require([
  '$api/models',
], function(models) {
  'use strict';

  var CommandEnum = Object.freeze({PLAY: "PLAY", PAUSE: "PAUSE", STOP: "STOP", START: "START", SEARCH: "SEARCH"});
  var TypeEnum = Object.freeze({TRACK: "TRACK", ALBUM: "ALBUM", PLAYLIST: "PLAYLIST"});

  var connection;

	var init = function() {
		connection = new WebSocket('ws://127.0.0.1:8081');

		connection.onopen = function () {
			log("WebSocket connection succesfully opened.");
		};

		connection.onmessage = function(event) {
			log("Message received: " + event.data);
			var messageParts = event.data.split(" ");
			var command = messageParts[0];
			if (command == CommandEnum.START) {
				// TODO: check right amount of parameters, otherwise return messaqe with error
				// TODO: js unit tests for this behaviour?
				var type = messageParts[1];
				if (type == TypeEnum.TRACK) {
					var track = models.Track.fromURI(messageParts[2]);
					logStartTrack(track);
					models.player.playTrack(track);
				} else if (type == TypeEnum.ALBUM) {
					log("About to start album");
					models.player.playContext(models.Album.fromURI(messageParts[2]));
				} else if (type == TypeEnum.PLAYLIST) {
					var playlist = models.Playlist.fromURI(messageParts[2]);
					logStartPlaylist(playlist);
					models.player.playContext(playlist);
				}
			}
		};
	};

	var logStartTrack = function(track) {
		//TODO: artists is giving back an array, so doesn't work.
		//Solution: build part of the Spotify 'domain model' in JSON and fill it up, use that to send as response for the
		//websocket server.
		logMessage("Start track: ", track, ["artists", "name"]);
	};

	var logStartPlaylist = function(playlist) {
		logMessage("Start playlist: ", playlist, ["name"]);
	};

	var logMessage = function(message, object, properties) {
		if (properties.length == 0) {
			log(message);
		} else {
			var property = properties[0];
			object.load(property).done(function(object) {
				logMessage(message + " - " + object[property].decodeForHtml(), object, properties.slice(1, properties.length));
			});
		}
	};

	var log = function(message) {
		document.getElementById("log").value += "[" + formatDate(new Date()) + "] " + message + "\n";
	};

	var formatDate = function(date) {
		return date.toISOString().replace("T", " ").substring(0, 19);
	};

	log("Log of Program Your Home Spotify App.");
	init();
});
