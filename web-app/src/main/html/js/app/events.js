"use strict";

//TODO: much more modularization in pyh code. Ideas:
// init: base URL, ping, check available modules (also todo on server), all other once logic (load templates?)
// page(s): page class + logic
// pyh: anything left here?
// + refactor out pageJavascriptModules


// Start a new require module.
// Provides an API for the rest of the application to receive events from the server.
// Sort of an event broker for the client side, hiding the actual implementation framework and technology.
define(["jquery", "stomp", "sock", "config", "util", "log"],
		function ($, stomp, sock, config, util, log) {
	
	// Client for STOMP communication with the server (over a websocket).
	var stompClient = null;

    function connect(callback) {
        var socket = new SockJS(config.getValue("serverUrl") + "/websocket");
        stompClient = Stomp.over(socket);
        // Connect to the server over the websocket. Note the use of the empty object {} here. This means no (additional) headers are sent, so also no login/account info is provided.
        stompClient.connect({},
	    		function (frame) {
        			callback();
	    			log.info("Successfully connected to websocket.");
	    		},
	    		function (error) {
	    			log.error("Error occured while connecting to websocket: " + error);
	    		});
    };

    function subscribe(eventTopic, callback, preprocessor) {
    	var doSubscribe = function () {
            stompClient.subscribe(eventTopic, function (message) {
        		callback(preprocessor(message.body));
            });    		
            log.debug("Subscription created for topic: '" + eventTopic + "'.");
    	};
    	// If the client is null     (so not connected)    : connect first and then do the subscribe action.
    	// If the client is not null (so already connected): immediately do the subscribe action.
    	stompClient == null ? connect(doSubscribe) : doSubscribe();
    };
    
	return {
		EventTopic: EventTopic,
		
		subscribeForText: function (eventTopic, callback) {
			subscribe(eventTopic, callback, util.identity);
		},
		
		subscribeForObject: function (eventTopic, callback) {
			subscribe(eventTopic, callback, JSON.parse);
		}	
	};


// End of require module.
});