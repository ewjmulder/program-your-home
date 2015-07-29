"use strict";

// Start a new require module.
// Provides an API for the rest of the application to receive events from the server.
// Sort of an event broker for the client side, hiding the actual implementation framework and technology.
define(["jquery", "stomp", "sock", "config", "util", "log"],
		function ($, stomp, SockJS, config, util, log) {
	
	// Promise that keep track of whether the connection has been established. This is to prevent
	// 'race conditions', where one subscribe is triggering the connect and the next subscribe comes in,
	// but the connection was not established yet.
	var connected = $.Deferred();
	// Client for STOMP communication with the server (over a websocket).
	var stompClient = null;

    function connect(callback) {
        var socket = new SockJS(config.getValue("serverUrl") + "/websocket");
        stompClient = Stomp.over(socket);
        // Connect to the server over the websocket. Note the use of the empty object {} here.
        // This means no (additional) headers are sent, so also no login/account info is provided.
        stompClient.connect({},
	    		function (frame) {
        			connected.resolve();
	    			log.info("Successfully connected to server websocket.");
	    		},
	    		function (error) {
	    			connected.reject("Connection error");
	    			log.error("Error occured while connecting to websocket: " + error);
	    		});
    };

    function subscribe(eventTopic, callback, preprocessor) {
    	// If not connected, perform the connect logic (will be done asynchonously).
    	if (stompClient == null) { connect(); }
    	// Always make conditional on connected promise.
    	// This will wait if not connected yet or continue directly if already resolved.
    	$.when(connected).done(function () {
            stompClient.subscribe(eventTopic, function (message) {
        		callback(preprocessor(message.body));
            });
            log.debug("Subscription created for topic: '" + eventTopic + "'.");
    	});
    };
    
	return {
		// Subscribe for plain text: the message body string is not modified.
		subscribeForText: function (eventTopic, callback) {
			subscribe(eventTopic, callback, util.identity);
		},
		
		// Subscribe for an object: parse the message body string as JSON.
		subscribeForObject: function (eventTopic, callback) {
			subscribe(eventTopic, callback, JSON.parse);
		}	
	};


// End of require module.
});