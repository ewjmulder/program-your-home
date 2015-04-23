/*
**    ------------------------------------------------------------------------
**    Current Version 1.0.4
**    ------------------------------------------------------------------------
**    Created by: James Becker (http://www.james-becker.co.uk/)
**    Created on: 11-08-2011
**
**	  For change log and updates see http://www.bhivecanvas.com/
**
**	  Bug reports to: bugkiller@bhivecanvas.com

**    License Information:
**    -------------------------------------------------------------------------
**    Copyright (C) 2011 James Becker
**
**    This program is free software; you can redistribute it and/or modify it
**    under the terms of the GNU General Public License as published by the
**    Free Software Foundation; either version 2 of the License, or (at your
**    option) any later version.
**    
**    This program is distributed in the hope that it will be useful, but
**    WITHOUT ANY WARRANTY; without even the implied warranty of
**    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
**    General Public License for more details.
**    
**    You should have received a copy of the GNU General Public License along
**    with this program; if not, write to the Free Software Foundation, Inc.,
**    59 Temple Place, Suite 330, Boston, MA 02111-1307 USA or http://www.gnu.org/

**    What is bHive:
**    -------------------------------------------------------------------------
**
**    bHive has been produced to help in creating a stable framework to allow
**    developers easy access to the HTML <canvas> element. Examples are across
**	  the internet on alternative solutions to using the canvas element but
**    hopefully this framework will make it easier! The code below is all commented
**    please feel free to modify it to your project and I look forward to seeing
**    what you create with bHive! [enjoy - James :)]
**
**    Modifications and contributors:
**    -------------------------------------------------------------------------
**    I appreciate that I am not the best programmer in the world and as such
**    welcome additions to this framework. If you would like to be an official
**    contributor to this project, please email me at: forkme@bhivecanvas.com
**
*/

/**
 * Creates a new bHive instance
 * @constructor
 * @version all
 * @param {Object} construct Constructor object used to build the bHive runtime
 * @param {Integer} construct.width Width of the canvas in pixels
 * @param {Integer} construct.height Height of the canvas in pixels
 * @param {String} construct.domobject String identifier of the DOM holder object
 * @param {String} [construct.backgroundColor] Hex colour string for the background color of the canvas object
*/
function bHive(construct) {

	this.setStageDimensions(construct.width,construct.height);	// Setup the internal stage dimensions before anything else.
	this.setStageObject(construct.domobject);					// Create the canvas object and attach to DOM
	this.init();												// Call the initialisation function to get things moving

	for(var i in construct) {
		this[i] = construct[i];
	}

	if(construct.hasOwnProperty('backgroundColor')) {
		this.setStageColor(construct.backgroundColor);
	}
	
	if(construct.hasOwnProperty('globalCompositeOperation')) {
		this.setGlobalComposite(construct.globalCompositeOperation);
	}

};

bHive.fn = bHive.prototype = {

	stageTarget: null,							// This is target HTML DOM object to attach to
	stageObject: null,							// This is the canvas root object
	stage2d: null,								// This is the drawing context
	undef: 'undefined',							// undefined as a string
	defGA: 1.0,									// Standard global Alpha setting
	clearFrame: true,							// Boolean value to tell the main loop to clear the frame on loop
	debug: false,								// Debug mode to show certain items

	KEYUP: 38,									// Up Arrow Key
	KEYDOWN: 40,								// Down Arrow Key
	KEYLEFT: 37,								// Left Arrow Key
	KEYRIGHT: 39,								// Right Arrow Key
	SPACE: 32,									// Space bar
	KEYA: 65,									// A Key
	KEYS: 83,									// S Key
	KEYW: 87,									// W Key
	KEYD: 68,									// D Key
	KEYENTER: 13,								// Enter Key
	KEYESCAPE: 27,								// Escape Key

	_objects: [],								// Main repository of objects held by bHive
	_events: [],								// Main repository of for engine events
	_triggers: [],								// Storage for trigger functions
	_radians: Math.PI / 180,					// Conversion for angles into radians
	_nomouse: false,							// Boolean to set no mouse over canvas
	_mouseX: 0,									// X coordinate of the mouse
	_mouseY: 0,									// Y coordinate of the mouse
	_touchX: 0,									// X coordinate of the touch event
	_touchY: 0,									// Y coordinate of the touch event
	_stageHeight: 320,							// Canvas height [set via constructor not here!]
	_stageWidth: 256,							// Canvas width [set via constructor not here!]
	_frameRate: 33,								// Initially set to 33fps
	_requestAnimFrame: null,					// Holder for the RequestAnimationFrame function
	_fpsInterval: new Date().getTime(),			// FPS timer
	_fps: 0,									// Current FPS rate
	_fpscounter: 0,								// Frame counter
	_currentFrame: 0,							// Frame pointer
	_initialisedAt: null,						// Internal timedate for time functions
	_loopIdent: null,							// pointer for Timeout if force clear is needed
	_loopFunction: null,						// The on loop function, this is the main loop function set by the user.
	_gfxLibrary: [],							// Main internal array for holding the graphics
	_gfxErrorCount: 0,							// Overall error count for missing images
	_gfxLoaded: 0,								// Overall count for graphics loaded - Maybe removed
	_ready: false, 								// Used to tell bHive that all bitmaps are loaded and can be manipulated.
	_globalCompositeOperation: 'source-over',	// Compositing method - can be:	source-over, source-in, source-out,
												//								source-atop, destination-over, destination-in
												//								destination-out, destination-atop, lighter
												//								darker, copy, xor

	/**
	* @function
	* @private
	* @version all
	* @description Called when bHive is first created to setup all objects and bindings
	*/
	init: function() {
		//create the stage
		this.stageObject = document.createElement('canvas');
		this.stage2d = this.stageObject.getContext('2d');
		this.stageObject.height = this.sh = this._stageHeight;
		this.stageObject.width = this.sw = this._stageWidth;
		this.stage2d.globalAlpha = this.defGA; // Reset, just in case!
		var that = this;

		// ** Attach the listeners to the object and pass back a closure for linkage back to the object.

		// Mouse Move Handler
		this.bind(this.stageObject, 'mousemove', function(that) { return function(e) { e.preventDefault(); that.mouseMover(e,that); }; }(that), true);

		// Mouse Click Handler
		this.bind(this.stageObject, 'click', function(that) { return function(e) { e.preventDefault(); that.mouseClick(e,that); }; }(that), true);

		// Mouse Down Handler
		this.bind(this.stageObject, 'mousedown', function(that) { return function(e) { e.preventDefault(); that.mouseDown(e,that); }; }(that), true);

		// Mouse Up Handler
		this.bind(this.stageObject, 'mouseup', function(that) { return function(e) { e.preventDefault(); that.mouseUp(e,that); }; }(that), true);

		// Key Down Handler
		this.bind(window, 'keydown', function(that) { return function(e) { that.keyDown(e,that); }; }(that), true);

		// Key Up Handler
		this.bind(window, 'keyup', function(that) { return function(e) { that.keyUp(e,that); }; }(that), true);
		
		// Mobile Touch & Gestures
		// --------------------------------------
		// * Touch Start
		this.bind(this.stageObject, 'touchstart', function(that) { return function(e) { event.preventDefault(); that.touchStart(e,that); }; }(that), true);

		// * Touch Move
		this.bind(this.stageObject, 'touchmove', function(that) { return function(e) { event.preventDefault(); that.touchMove(e,that); }; }(that), true);

		// * Touch End
		this.bind(this.stageObject, 'touchend', function(that) { return function(e) { event.preventDefault(); that.touchEnd(e,that); }; }(that), true);

		// * Touch Cancel
		this.bind(this.stageObject, 'touchcancel', function(that) { return function(e) { event.preventDefault(); that.touchCancel(e,that); }; }(that), true);

		// * Gesture Start
		this.bind(this.stageObject, 'gesturestart', function(that) { return function(e) { event.preventDefault(); that.gestureStart(e,that); }; }(that), true);

		// * Gesture Move
		this.bind(this.stageObject, 'gesturemove', function(that) { return function(e) { event.preventDefault(); that.gestureMove(e,that); }; }(that), true);

		// * Gesture Start
		this.bind(this.stageObject, 'gestureend', function(that) { return function(e) { event.preventDefault(); that.gestureEnd(e,that); }; }(that), true);

		this.attachStage();
		this._initialisedAt = new Date();
	},
	
	/**
	* @function
	* @private
	* @version all
	* @description Bind events to objects
	*/
	bind: function(obj, type, callback, bubble) {
		if(document.addEventListener) {
			obj.addEventListener(type, callback, bubble);
		}
		else if(document.attachEvent) {
			obj.attachEvent("on" + type, callback, bubble);
		}
	},
	
	/**
	* @function
	* @private
	* @version all
	* @description Remove event bindings on an object
	*/
	unbind:  function(obj, type, callback) {
		if(document.removeEventListener) {
			obj.removeEventListener(type, callback, false);
		}
		else if(document.detachEvent) {
			obj.detachEvent("on" + type, callback, false);
		}
	},

	/**
	* @function
	* @public
	* @version all
	* @param {String} sEvent The event to bind to
	* @param {Function} fCallback The function to call when the event is fired
	* @description Attach an event listener to the canvas object for global user interaction
	*/
	addEventListener: function (sEvent, fCallback) {
		this._events[sEvent] = fCallback;
	},

	
	hideMouse: function() {
		this.stageObject.style.cursor = 'none';
	},
	
	showMouse: function() {
		this.stageObject.style.cursor = 'pointer';
	},

	/**
	* @function
	* @public
	* @version all
	* @param {Array} ary
	* @description Calculates size of an associative Array
	* @returns {Int} Size of array
	*/
	sizeof: function(ary) {
		var size = 0;
		// check that the constructor is a Array
		var regEx = /function (.{1,})\(/;
		
		for(var key in ary) {
			result = regEx.exec(ary[key].constructor.toString());
			if(result != null && result[1] == "Array") {
				size += this.sizeof(ary[key]);
			} else {
				size++;
			}
		}
			
		return size;
	},
	
	/**
	* @function
	* @private
	* @version all
	* @param {Event} e
	* @description Creates a MouseEvent object for cross browser mouse properties to be returned
	*/
	MouseEvent: function(e) {
		this.e = (e) ? e : window.event;
		this.x = (e.PageX) ? e.PageX : e.clientX;
		this.y = (e.PageY) ? e.PageY : e.clientY;
		this.target = (e.target) ? e.target : e.srcElement;
		this.button = (e.button) ? e.button : e.button;
	},

	/**
	* @function
	* @private
	* @version all
	* @param {Event} e
	* @description Creates an MouseWheelEvent object for cross browser mouse wheel properties to be used
	*/
	MouseWheelEvent: function(e) {
		this.e = (e) ? e : window.event;
		this.delta = (e.detail) ? e.detail * -1 : e.wheelDelta / 40;
	},
	
	/**
	* @function
	* @private
	* @version all
	* @param {Event} e
	* @description Creates a KeyEvent object for cross browser key properties to be used
	*/
	KeyEvent: function(e) {
		this.keyCode = (e.keyCode) ? e.keyCode : e.which;

		// Alt Keys
		this.altKey = (e.altKey) ? true : false;
		this.altLeft = (e.altLeft) ? true : false;

		// Control Keys
		this.ctrlKey = (e.ctrlKey) ? true : false;
		this.ctrlLeft = (e.ctrlLeft) ? true : false;
	
		// Shift Keys
		this.shiftKey = (e.shiftKey) ? true : false;
		this.shiftLeft = (e.shiftLeft) ? true : false;
	},
	
	/**
	* @function
	* @private
	* @version all
	* @description Main loop function for bHive which in turn calls the user assigned looping function
	*/
	mainController: function() {
		var that = this;
		var ctx = this.stage2d;
		var currentTime = (new Date().getTime() - that._fpsInterval) / 1000;

		that._currentFrame++;
		that._fpscounter++;

		this.despatchTriggers();
		
		if(currentTime > 1) {
			that._fps = Math.floor((that._fpscounter/currentTime)*10.0)/10.0;
			that._fpsInterval = new Date().getTime();
			that._fpscounter = 0;
		}
		
		if(that.clearFrame) {
			ctx.clearRect(0,0,that._stageWidth,that._stageHeight);
		}
		
		if(typeof this._loopFunction == 'function') {
			that._loopFunction.call(that);
		}

	},
	
	despatchTriggers: function() {
		var cf = this.getFrame();
		var now = new Date().getTime();
		var run = false;

		for(var i = 0, tl = this._triggers.length; i < tl; i++) {
			ct = this._triggers[i];
			
			if(ct.isTime) {
				if(now >= ct.runAt) {
					ct.func();
					run = true;
				}
			} else {
				if(cf >= ct.runAt) {
					ct.func();
					run = true;
				}
			}
			
			if(run) {
				this._triggers.splice(i,1);
				run = false;
			}
		}
	},
	
	/**
	* @function
	* @public
	* @version all
	* @description Add a timed function to the queue for processing
	* @returns {Int}
	*/
	trigger: function(sFunc, interval, isTime) {
		isTime = (isTime === undefined) ? true : isTime;
		
		var now = new Date().getTime();
		var curFrame = this.getFrame();
		var f = {};
		var runAt = 0;

		if(isTime) {
			runAt = now + interval;
		} else {
			runAt = curFrame + interval;
		}

		f.func = sFunc;
		f.runAt = runAt;
		f.isTime = isTime;
		
		this._triggers.push(f);
		return this._triggers.length-1;
	},

	/**
	* @function
	* @public
	* @version all
	* @description Returns Frames Per Second (FPS) that bHive is running at, handy for speed issues.
	* @returns {Int}
	*/
	getFPS: function() {
		return this._fps;
	},

	/**
	* @function
	* @public
	* @version all
	* @description Returns the current rate at which bHive is calling the setInterval method, default is 33.
	* @returns {Int}
	*/
	getFrameRate: function() {
		return this._frameRate;
	},

	/**
	* @function
	* @public
	* @version all
	* @description Returns the timestamp in milliseconds since bHive was created.
	* @returns {Timestamp} Javascript timestamp from Date object method Date.getTime()
	*/
	getSystemTime: function() {
		return this._initialisedAt.getTime();
	},

	/**
	* @function
	* @public
	* @version all
	* @description Returns the frame number that the bHive loop has reached.
	* @returns {Int}
	*/
	getFrame: function() {
		return this._currentFrame;
	},

	/**
	* @function
	* @private
	* @version all
	* @param {Object} obj
	* @description Returns the x,y position of an object on the canvas.
	* @returns {Array} Numerical array [0] = X, [1] = Y
	*/
	getPosition: function(obj) {
		var curLeft = curTop = 0;
		if(obj.offsetParent) {
			do {
				curLeft += obj.offsetLeft;
				curTop += obj.offsetTop;
			} while(obj = obj.offsetParent);
		} else {
			curLeft = obj.offsetLeft;
			curTop = obj.offsetTop;
		}
		return [curLeft,curTop];
	},
	
	/**
	* @function
	* @private
	* @version all
	* @param {Event} e Fired mouse event
	* @param {Object} that Reference to the main bHive object
	* @description Monitors the canvas for mouse movements, if user callbacks have been attached these are fired also.
	*/
	mouseMover: function(e,that) {
		this.engine = that;
		e = new this.engine.MouseEvent(e);

		var pos = this.engine.getPosition(this.stageObject);
		this._mouseX = e.x - pos[0];
		this._mouseY = e.y - pos[1];

		if(typeof this._events['onmousemove'] != this.undef) {
			this._events.onmousemove.apply(this,[{x: this._mouseX, y: this._mouseY}]);
		}
		
		for(var i in this._objects) {
			xIn = yIn = false;
			var obj = this._objects[i];
			
			if(obj instanceof bHive.Clip) {
				
				parentX = (typeof obj.parent != this.undef) ? obj.parent.x : 0;
				parentY = (typeof obj.parent != this.undef) ? obj.parent.y : 0;
				
				if(this._mouseX > parentX+obj.x && this._mouseX < parentX + obj.x + obj.width()) {
					xIn = true;
				}

				if(this._mouseY > parentY + obj.y && this._mouseY < parentY + obj.y + obj.height()) {
					yIn = true;
				}
				
				if((xIn && yIn) && !obj._mouseover) {
					if(typeof obj.events.onmouseover == "function") {
						obj.events.onmouseover.apply(obj,[{x: this._mouseX, y: this._mouseY}]);
					}
					obj._mouseover = true;
				}
				
				if((!xIn || !yIn) && obj._mouseover) {
					if(typeof obj.events.onmouseout == "function") {
						obj.events.onmouseout.apply(obj,[{x: this._mouseX, y: this._mouseY}]);
					}
					obj._mouseover = false;
				}
			}
		}
	},
	
	/**
	* @function
	* @private
	* @version all
	* @param {Event} e Fired mouse event
	* @param {Object} that Reference to the main bHive object
	* @description Monitors the canvas for mouse clicks, if user callbacks have been attached these are fired also.
	*/
	mouseClick: function(e,that) {
		this.engine = that;
		e = new this.engine.MouseEvent(e);
		xIn = yIn = false;
		
		if(typeof this._events['onclick'] != this.undef) {
			this._events.onclick.apply(this,[{x: this._mouseX, y: this._mouseY}]);
		}
		
		for(var i in this._objects) {
			xIn = yIn = false;
			var obj = this._objects[i];
			
			if(obj instanceof bHive.Clip) {
				
				parentX = (typeof obj.parent != this.undef) ? obj.parent.x : 0;
				parentY = (typeof obj.parent != this.undef) ? obj.parent.y : 0;
				
				if(this._mouseX > parentX+obj.x && this._mouseX < parentX + obj.x + obj.width()) {
					xIn = true;
				}

				if(this._mouseY > parentY + obj.y && this._mouseY < parentY + obj.y + obj.height()) {
					yIn = true;
				}
				
				if((xIn && yIn) && typeof obj.events.onclick == "function") {
					obj.events.onclick.apply(obj,[{x: this._mouseX, y: this._mouseY}]);
				}
			}
		}
	},
	
	/**
	* @function
	* @private
	* @version all
	* @param {Event} e Fired mouse event
	* @param {Object} that Reference to the main bHive object
	* @description Monitors the canvas for the mouse button being depressed, if user callbacks have been attached these are fired also.
	*/
	mouseDown: function(e,that) {
		this.engine = that;
		e = new this.engine.MouseEvent(e);
		xIn = yIn = false;
		
		if(typeof this._events['mousedown'] != this.undef) {
			this._events.mousedown.apply(this,[{x: this._mouseX, y: this._mouseY}]);
		}
		
		for(var i in this._objects) {
			var obj = this._objects[i];
			
			if(obj instanceof bHive.Clip) {
				//if(obj.visible) {
					// checking against mouse
					if(this._mouseX > obj.x && this._mouseX < obj.x + obj.width()) {
						xIn = true;
					}

					if(this._mouseY > obj.y && this._mouseY < obj.y + obj.height()) {
						yIn = true;
					}
					
					if(xIn && yIn && typeof obj.events.mousedown == "function") {
						obj.events.mousedown({ x: this._mouseX, y: this._mouseY, src: obj });
					}
				//}
			}
		}
	},

	/**
	* @function
	* @private
	* @version all
	* @param {Event} e Fired mouse event
	* @param {Object} that Reference to the main bHive object
	* @description Monitors the canvas for the mouse button being released, if user callbacks have been attached these are fired also.
	*/
	mouseUp: function(e,that) {
		this.engine = that;
		e = new this.engine.MouseEvent(e);
		xIn = yIn = false;
		
		if(typeof this._events['mouseup'] != this.undef) {
			this._events.mouseup.apply(this,[{x: this._mouseX, y: this._mouseY}]);
		}
		
		for(var i in this._objects) {
			var obj = this._objects[i];
			
			if(obj instanceof bHive.Clip) {
				//if(obj.visible) {
					// checking against mouse
					if(this._mouseX > obj.x && this._mouseX < obj.x + obj.width()) {
						xIn = true;
					}

					if(this._mouseY > obj.y && this._mouseY < obj.y + obj.height()) {
						yIn = true;
					}
					
					if(xIn && yIn && typeof obj.events.mouseup == "function") {
						obj.events.mouseup({ x: this._mouseX, y: this._mouseY, src: obj });
					}
				//}
			}
		}
	},

	/**
	* @function
	* @private
	* @version all
	* @param {Event} e Fired mouse event
	* @param {Object} that Reference to the main bHive object
	* @description Monitors the canvas for keys being depressed, if user callbacks have been attached these are fired also.
	*/
	keyDown: function(e,that) {
		this.engine = that;
		e = new this.engine.KeyEvent(e);

		if(typeof this._events['onkeydown'] != this.undef) {
			this._events.onkeydown.apply(this,[e]);
		}
		
		for(var i in this._objects) {
			var obj = this._objects[i];
			
			if(obj instanceof bHive.Clip) {
				if(typeof obj.events.onkeydown == "function") {
					obj.events.onkeydown(e);
				}
			}
		}
	},

	/**
	* @function
	* @private
	* @version all
	* @param {Event} e Fired mouse event
	* @param {Object} that Reference to the main bHive object
	* @description Monitors the canvas for keys being released, if user callbacks have been attached these are fired also.
	*/
	keyUp: function(e,that) {
		this.engine = that;
		e = new this.engine.KeyEvent(e);

		if(typeof this._events['onkeyup'] != this.undef) {
			this._events.onkeyup.apply(this,[e]);
		}
		
		for(var i in this._objects) {
			var obj = this._objects[i];
			
			if(obj instanceof bHive.Clip) {
				if(typeof obj.events.onkeyup == "function") {
					obj.events.onkeyup(e);
				}
			}
		}
	},
	
	/**
	* @function
	* @private
	* @version all
	* @param {Event} e Fired mouse event
	* @param {Object} that Reference to the main bHive object
	* @description Triggered when a touch event occurs
	*/
	touchStart: function(e,that) {
		this.engine = that;
		var touch = e.touches[0];
		xIn = yIn = false;
		
		var pos = this.engine.getPosition(this.stageObject);
		this._touchX = touch.pageX - pos[0];
		this._touchY = touch.pageY - pos[1];
		
		if(typeof this._events['touchstart'] != this.undef) {
			this._events.touchstart.apply(this,[{x: this._touchX, y: this._touchY}]);
		}
		
		for(var i in this._objects) {
			var obj = this._objects[i];
			
			if(obj instanceof bHive.Clip) {
				//if(obj.visible) {
					// checking against mouse
					if(this._touchX > obj.x && this._touchX < obj.x + obj.width()) {
						xIn = true;
					}

					if(this._touchY > obj.y && this._touchY < obj.y + obj.height()) {
						yIn = true;
					}
					
					if(xIn && yIn && typeof obj.events.touchstart == "function") {
						obj.events.touchstart({ x: this._touchX, y: this._touchY, src: obj });
					}
				//}
			}
		}
	},
		
	/**
	* @function
	* @private
	* @version all
	* @param {Event} e Fired mouse event
	* @param {Object} that Reference to the main bHive object
	* @description Monitors the canvas for touch movements.
	*/
	touchMove: function(e,that) {
		this.engine = that;
		var touch = e.touches[0];
		
		var pos = this.engine.getPosition(this.stageObject);
		this._touchX = touch.pageX - pos[0];
		this._touchY = touch.pageY - pos[1];

		if(typeof this._events['touchmove'] != this.undef) {
			this._events.touchmove.apply(this,[{x: this._touchX, y: this._touchY}]);
		}
		
		for(var i in this._objects) {
			xIn = yIn = false;
			var obj = this._objects[i];
			
			if(obj instanceof bHive.Clip) {
				
				parentX = (typeof obj.parent != this.undef) ? obj.parent.x : 0;
				parentY = (typeof obj.parent != this.undef) ? obj.parent.y : 0;
				
				if(this._touchX > parentX+obj.x && this._touchX < parentX + obj.x + obj.width()) {
					xIn = true;
				}

				if(this._touchY > parentY + obj.y && this._touchY < parentY + obj.y + obj.height()) {
					yIn = true;
				}
				
				if((xIn && yIn) && !obj._touched) {
					if(typeof obj.events.touchmove == "function") {
						obj.events.touchmove.apply(obj,[{x: this._touchX, y: this._touchY}]);
					}
					obj._touched = true;
				}
				
				if((!xIn || !yIn) && obj._touched) {
					if(typeof obj.events.touchout == "function") {
						obj.events.touchout.apply(obj,[{x: this._touchX, y: this._touchY}]);
					}
					obj._mouseover = false;
				}
			}
		}

	},
	
	/**
	* @function
	* @private
	* @version all
	* @param {Event} e Fired mouse event
	* @param {Object} that Reference to the main bHive object
	* @description Triggered when touch events end
	*/
	touchEnd: function(e, that) {
		this.engine = that;
		var touch = e.touches[0];
		
		var pos = this.engine.getPosition(this.stageObject);
		this._touchX = touch.pageX - pos[0];
		this._touchY = touch.pageY - pos[1];
		
		if(typeof this._events['touchend'] != this.undef) {
			this._events.touchend.apply(this,[{ x: this._touchX, y: this._touchY, src: obj }]);
		}
		
		for(var i in this._objects) {
			var obj = this._objects[i];
			
			if(obj instanceof bHive.Clip) {
				//if(obj.visible) {
					// checking against mouse
					if(this._touchX > obj.x && this._touchX < obj.x + obj.width()) {
						xIn = true;
					}

					if(this._touchY > obj.y && this._touchY < obj.y + obj.height()) {
						yIn = true;
					}
					
					if(xIn && yIn && typeof obj.events.touchend == "function") {
						obj.events.touchend({ x: this._touchX, y: this._touchY, src: obj });
					}
				//}
			}
		}
	},
	
	/**
	* @function
	* @private
	* @version all
	* @param {Event} e Fired mouse event
	* @param {Object} that Reference to the main bHive object
	* @description Triggered when gesture event occurs
	*/
	gestureStart: function(e, that) {
		this.engine = that;
	},

	/**
	* @function
	* @private
	* @version all
	* @param {Event} e Fired mouse event
	* @param {Object} that Reference to the main bHive object
	* @description Triggered when gesture movement occurs.
	*/
	gestureMove: function(e, that) {
		this.engine = that;
	},

	/**
	* @function
	* @private
	* @version all
	* @param {Event} e Fired mouse event
	* @param {Object} that Reference to the main bHive object
	* @description Triggered when gesture event ends
	*/
	gestureEnd: function(e, that) {
		this.engine = that;
	},

	/**
	* @function
	* @public
	* @version all
	* @param {String} scriptSrc Path to JavaScript file
	* @description Includes an external JavaScript file
	*/
	include: function(scriptSrc) {
		var script = document.createElement('script');
		script.src = scriptSrc;
		script.type = 'text/javascript';
		document.getElementsByTagName('head')[0].appendChild(script);
	},

	/**
	* @function
	* @public
	* @version all
	* @param {Int} iW Width in pixels
	* @param {Int} iH Height in pixels
	* @description Updates the dimensions of the canvas object to be <i>iW</i>, <i>iH</i>
	*/
	setStageDimensions: function(iW,iH) {
		this._stageHeight = iH;
		this._stageWidth = iW;
	},
	
	/**
	* @function
	* @public
	* @version all
	* @param {String} color HTML formatted color string
	* @description Updates the background color of the canvas element to be <i>color</i>
	*/
	setStageColor: function(color) {
		this._stageColor = color;
		this.stageObject.style.backgroundColor = color;
	},
	
	/**
	* @function
	* @public
	* @version all
	* @param {String} mode Global composite canvas string
	* @description Changes the Global Composite Operational mode of the canvas object.
	*/
	setGlobalComposite: function(mode) {
		this._globalCompositeOperation = mode;
		this.stage2d.globalCompositeOperation = mode;
	},

	/**
	* @function
	* @private
	* @version all
	* @param {Object|String} Reference to a DOM object or string indetifier of DOM object
	* @description Instructs bHive which DOM object to append the generated &lt;canvas&gt; element to.
	*/
	setStageObject: function(obj) {
		if(typeof obj == 'string') {
			var o = document.getElementById(obj);
			if(o !== this.undef) {
				this.stageTarget = o;
			} else {
			}
		} else {
			this.stageTarget = obj;
		}
	},
	
	/**
	* @function
	* @private
	* @version all
	* @description Appends the &lt;canvas&gt; to the DOM object.
	*/
	attachStage: function() {
		this.stageTarget.appendChild(this.stageObject);
	},
	
	/**
	* @function
	* @private
	* @version all
	* @param {Object} bHive object
	* @description Adds the new bHive object to the internal object stack
	* @returns {Object} Reference to object
	*/
	storeObject: function(obj) {
		var len = this._objects.length;
		this._objects.push(obj);
		return this._objects[len];
	},
	
	/**
	* @function
	* @public
	* @version all
	* @param {Object} construct for bHive.Clip class
	* @description Creates a bHive Clip object
	* @returns {Object} bHive Clip object
	*/
	createClip: function(construct) {
		var that = this;
		return this.storeObject(new bHive.Clip(construct, that));
	},
	
	/**
	* @function
	* @public
	* @version all
	* @param {Object} construct for bHive.Video class
	* @description Creates a bHive Video object
	* @returns {Object} bHive Video object
	*/
	createVideo: function(construct) {
		var that = this;
		return this.storeObject(new bHive.Video(construct, that));
	},

	/**
	* @function
	* @public
	* @version all
	* @param {Object} construct for bHive.Audio class
	* @description Creates a bHive Audio object
	* @returns {Object} bHive Audio object
	*/
	createSound: function(construct) {
		var that = this;
		return this.storeObject(new bHive.Sound(construct, that));
	},

	/**
	* @function
	* @public
	* @version all
	* @param {Object} construct for bHive.Sprite class
	* @description Creates a bHive Sprite object
	* @returns {Object} bHive Sprite object
	*/
	createSprite: function(construct) {
		var that = this;
		return this.storeObject(new bHive.Sprite(construct, that));
	},
	
	/**
	* @function
	* @public
	* @version all
	* @param {Object} construct for bHive.Bitmap class
	* @description Creates a bHive Bitmap object
	* @returns {Object} bHive Bitmap object
	*/
	createBitmap: function(construct) {
		var that = this;
		return this.storeObject(new bHive.Bitmap(construct, that));
	},
	
	/**
	* @function
	* @public
	* @version all
	* @param {Object} construct for bHive.Line class
	* @description Creates a bHive Line object
	* @returns {Object} bHive Line object
	*/
	createLine: function(construct) {
		var that = this;
		return this.storeObject(new bHive.Line(construct, that));
	},
	
	/**
	* @function
	* @public
	* @version all
	* @param {Object} construct for bHive.Text class
	* @description Creates a bHive Text object
	* @returns {Object} bHive Text object
	*/
	createText: function(construct) {
		var that = this;
		return this.storeObject(new bHive.Text(construct, that));
	},
	
	/**
	* @function
	* @public
	* @version all
	* @param {Object} construct for bHive.Shape class
	* @description Creates a bHive Shape object
	* @returns {Object} bHive Shape object
	*/
	createShape: function(construct) {
		var that = this;
		return this.storeObject(new bHive.Shape(construct, that));
	},
	
	/**
	* @function
	* @public
	* @version all
	* @param {Object} construct for bHive.Gradient class
	* @description Creates a bHive Gradient object
	* @returns {Object} bHive Gradient object
	*/
	createGradient: function(construct) {
		var that = this;
		return this.storeObject(new bHive.Gradient(construct, that));
	},
	
	/**
	* @function
	* @public
	* @version all
	* @param {Function} callback Reference to a user created function
	* @description Acts in a similar way to the &lt;body onload="...". Attaches a user function to bHive which is called every loop
	*/
	theLoop: function(callback) {
		var that = this;
		this._loopFunction = callback;

		this._requestAnimFrame = (function(){
			return 	window.requestAnimationFrame		||
					window.webkitRequestAnimationFrame	||
					window.mozRequestAnimationFrame		||
					window.oRequestAnimationFrame		||
					window.msRequestAnimationFrame;
		})();
		
		if(typeof this._requestAnimFrame != this.undef) {
			this._loopIdent = function(that) {
				return function() {
					func = window.requestAnimationFrame	||
					window.webkitRequestAnimationFrame	||
					window.mozRequestAnimationFrame		||
					window.oRequestAnimationFrame		||
					window.msRequestAnimationFrame;
					
					func( that._loopIdent );
					that.mainController();
				}
			}(that);
			
			this._loopIdent();
		} else {
			this._loopIdent = setInterval(function(that) {
				return function() {
					that.mainController();
				}
			}(that),this._frameRate);
		}
	},
	
	/**
	* @function
	* @private
	* @version all
	* @param {Object} obj Object reference
	* @description Calculates the rotated size of a bHive clip to set the correct width and height values
	* @returns {Object} Returns an object with the properties width and height
	*/
	getRotatedSize: function(obj) {
		radians = ( 2 * Math.PI * obj.rotation ) / 360;
		cosine = Math.cos(radians);
		sine = Math.sin(radians);
		
		objNH = obj.image.naturalHeight;
		objNW = obj.image.naturalWidth;
		
		point1_x = -objNH * sine;
		point1_y = objNH * cosine;
		point2_x = objNW * cosine - objNH * sine;
		point2_y = objNH * cosine + objNW * sine;
		point3_x = objNW * cosine;
		point3_y = objNW * sine;
		
		minx = Math.min(0,Math.min(point1_x,Math.min(point2_x, point3_x)));
		miny = Math.min(0,Math.min(point1_y,Math.min(point2_y, point3_y)));

		maxx = Math.max(point1_x,Math.max(point2_x,point3_x));
		maxy = Math.max(point1_y,Math.max(point2_y,point3_y));
		
		rotwidth = Math.round(maxx-minx);
		rotheight = Math.round(maxy-miny);
		
		return {width: rotwidth, height: rotheight};
	},
	
	/**
	* @function
	* @private
	* @version all
	* @param {String} hexColor HTML formatted colour string
	* @param {Int} aplha 0-100 value for the alpha level
	* @description Creates a rgba formatted color string
	* @returns {String} rgba formatted string <b>rgba(x,x,x,x)</b>
	*/
	hex2RGBa: function(hexColor,alpha) {
		var r = g = b = 0;
		var a = 1;

		if(alpha > 100) {
			alpha = 100;
		}
		else if(alpha < 0) {
			alpha = 0;
		}
		
		a = alpha / 100; // Gives the proper float values for rgba(...)
		
		hexColor = (hexColor.charAt(0)=='#') ? hexColor.substring(1,hexColor.length) : hexColor;
		
		if(hexColor.length == 3) {
			r = parseInt(hexColor.substring(0,1)+hexColor.substring(0,1),16);
			g = parseInt(hexColor.substring(1,2)+hexColor.substring(1,2),16);
			b = parseInt(hexColor.substring(2,3)+hexColor.substring(2,3),16);
		} else {
			r = parseInt(hexColor.substring(0,2),16);
			g = parseInt(hexColor.substring(2,4),16);
			b = parseInt(hexColor.substring(4,6),16);
		}
						
		return 'rgba(' + r + ', ' + g + ', ' + b + ', ' + a + ')';
	},
	
	/**
	* @function
	* @public
	* @version all
	* @param {Object} source_object Source object reference
	* @description Clones a passed bHive object
	* @returns {Object} New cloned object based on <i>source_object</i>
	*/
	clone: function( source_object ) {
		var that = this;
		function clone_object(){};
		clone_object.prototype = source_object;
		return this.storeObject(new clone_object());
	},
	
	/**
	* @function
	* @public
	* @version all
	* @param {Object} obj Source bHive object
	* @param {String} prop Property you want to tween
	* @param {Int} begin Starting value of the tween
	* @param {Int} finish End value of the tween
	* @param {Int} duration Duration of the tween
	* @param {Boolean} isTime If set to true (default) then tween to assumes <i>duration</i> is in seconds, false uses frames.
	* @description Creates a tween over a period of time or frames to affect the property of an object.
	* @returns {Object} Tween object
	*/
	Tween: function(obj, prop, begin, finish, duration, isTime) {
	
		if(!arguments.length) return false;
		
		var that = this;
		var tweener = new bHive.Tween();
		
		tweener.engine = that;
		tweener.obj = obj;
		tweener.prop = prop;
		tweener.begin = begin;
		tweener.position = begin;
		tweener.end = finish;
		tweener.duration = duration;
		tweener.isTime = isTime;
		tweener.start();
		
		return tweener;
	}
	
// End Prototype definition	
};

/**
 * Creates a new bHive.Tween instance
 * @constructor
 * @version all
*/
bHive.Tween = function () {
	this._date = new Date();
	this.events = [];
}

bHive.Tween.prototype = {
	engine: null,
	obj: null,
	prop: null,
	begin: 0,
	position: 0,
	end: 0,
	change: 0,
	duration: 0,
	isTime: true,
	isPlaying: false,
	looping: false,
	events: null,
	time: 0,
	func: function(t, b, c, d) { return c * t / d + b; },
	
	_startTime: 0,
	_time: 0,
	_oldTime: 0,
	_loopIdent: 0,
	_oldPos: 0,
	_date: null,
	
	start: function() {
		this.reset();
		this.startLoop();
		// Send Event
	},

	stop: function() {
		this.stopLoop();
	},
	
	startLoop: function() {
		var that = this;
		var step;
		
		if(!this.isTime) {
			step = this.engine._frameRate;
		} else {
			step = Math.round((this.duration*1000) / this.engine._frameRate);
		}

		this._loopIdent = setInterval(function(tweener) {
			return function() {
				tweener.updateTime();
			}
		}(that),step);

		this.isPlaying = true;
	},
	
	stopLoop: function() {
		this.isPlaying = false;
		clearInterval(this._loopIdent);
		if(typeof this.events.complete == "function") {
			this.events.complete();
		}
	},

	reset: function() {
		t = (isNaN(arguments[0])) ? 0 : parseInt(arguments[0]);

		this._time = t;
		this.change = this.end - this.begin;
		this.fixTime();
		this.updateFrame();
	},
	
	fixTime: function() {
		if(this.isTime) {
			var now = new Date().getTime();
			this._startTime = now - this._time * 1000;
		}			
	},
	
	updateTime: function() {
		this._oldTime = this._time;
		
		if(this.isTime) {
			var now = new Date().getTime();
			this.updateFrame((now - this._startTime)/1000);
		} else {
			this.updateFrame(this._time + 1);
		}
	},
	
	updateFrame: function(t) {
		if(t > this.duration) {
			if(this.looping) {
				// Looping code here
			} else {
				if(this.isTime) {
					this._time = this.duration;
					this.setPosition(this.getPosition(this._time));
				}
				this.stopLoop();
			}
		} else {
			this.setPosition(this.getPosition(t));
			
			if(!this.isTime) {
				this._time++;
			}
		}
	},
	
	setPosition: function(p) {
		if(typeof this.obj[this.prop] != this.engine.undef) {
			this.obj[this.prop] = this.position = p;
		}
	},
	
	getPosition: function(t) {
		//change * time / duration + begin;
		t = (typeof t == this.engine.undef) ? 0 : t;
		fr = this.func(t, this.begin, this.change, this.duration);
		return fr;
	},

	/**
	* @function
	* @public
	* @version all
	* @description To be implemented
	*/
	tweenTo: function() {
	},
	
	/**
	* @function
	* @public
	* @version all
	* @param {String} sEvent Name of event
	* @param {Function} fCallback Function to call when event is fired
	* @description Adds an event listener to the Tween object
	* @example
	* Tween = engine.Tween(myObject, "x", 100, 0, 2, true);
	* Tween.addEventListener('oncomplete', myFunction); 
	*/
	addEventListener: function (sEvent, fCallback) {
		this.events[sEvent] = fCallback;
	}
	
};


/**
 * Creates a new bHive.Bitmap instance
 * @constructor
 * @version all
*/
bHive.Bitmap = function (construct, parentObject) {
	var that = this;
	this.engine = parentObject;
	this.events = [];
	this.tiles = [];

	for(var i in construct) {
		this[i] = construct[i];
	}
	
	this.image = new Image();
	this.image.src = this.src;
	
	this.image.onload = (function(img) {
		return function() {
			img.width = img.image.naturalWidth;
			img.height = img.image.naturalHeight;
			if(typeof img.events.onload != img.engine.undef) {
				img.events.onload();
			}
		}
	})(that);
	
	this.image.onerror = (function(img) {
		return function() {
			console.log( "cannot load: " + img.image.src );
			if(typeof img.events.onerror != img.engine.undef) {
				img.events.onerror();
			}
		}
	})(that);
};

bHive.Bitmap.prototype = {
	engine: null,
	src: '',
	image: null,
	events: null,
	tiles: null,
	x: 0,
	y: 0,
	registration_x: 0,
	registration_y: 0,
	rotation: 0,
	visible: true,
	alpha: 100,
	x_scale: 100,
	y_scale: 100,
	
	/**
	* @function
	* @public
	* @version all
	* @param {String} sEvent Name of event
	* @param {Function} fCallback Function to call when event is fired
	* @description Adds an event listener to the Bitmpa object
	* @example
	* Bitmap = engine.createBitmap({constructor});
	* Bitmap.addEventListener('oncomplete', myFunction); 
	*/	
	addEventListener: function (sEvent, fCallback) {
		this.events[sEvent] = fCallback;
	},
	
	/**
	* @function
	* @public
	* @version all
	* @param {String} sEvent Name of event
	* @description Removes and event listener from the object
	* @example
	* Bitmap = engine.createBitmap({constructor});
	* Bitmap.addEventListener('oncomplete', myFunction); 
	* Bitmap.deleteEventListener('oncomplete'); 
	*/	
	deleteEventListener: function (sEvent) {
		if(typeof this.events[sEvent] != this.engine.undef) {
			delete this.events[sEvent];
		}
	},
	
	/**
	* @function
	* @public
	* @version all
	* @param {Int} sx X co-ordinate of first point
	* @param {Int} sy Y co-ordinate of first point
	* @param {Int} sWidth Width of the slice
	* @param {Int} sHeight Height of the slice
	* @param {Int} dx X co-ordinate of destination onto canvas
	* @param {Int} dy Y co-ordinate of destination onto canvas
	* @param {Int} dWidth Optional value to stretch the output width
	* @param {Int} dHeight Optional value to stretch the output height
	* @description Displays a portion of the bitmap onto the canvas
	*/	
	slice: function( sx, sy, sWidth, sHeight, dx, dy, dWidth, dHeight ) {
		var dWidth = (typeof dWidth == this.engine.undef) ? sWidth : dWidth;
		var dHeight = (typeof dHeight == this.engine.undef) ? sHeight : dHeight;
		
		this.engine.stage2d.drawImage(this.image, sx, sy, sWidth, sHeight, dx, dy, dWidth, dHeight);
	},
	
	/**
	* @function
	* @public
	* @version all
	* @param {String} ident string name to identify the tile
	* @param {Int} sx X co-ordinate of first point
	* @param {Int} sy Y co-ordinate of first point
	* @param {Int} sWidth Width of the slice
	* @param {Int} sHeight Height of the slice
	* @description Saves a portion of the bitmap to the tiles array for use later
	*/	
	addTile: function( ident, sx, sy, sWidth, sHeight ) {
		if(typeof ident == this.engine.undef) return false;
	
		var t = {};
		
		t.sx = sx;
		t.sy = sy;
		t.sWidth = sWidth;
		t.sHeight = sHeight;
		
		this.tiles[ident] = t;
	},
	
	/**
	* @function
	* @public
	* @version all
	* @param {String} ident string name to identify the tile
	* @param {Int} dx X co-ordinate of destination onto canvas
	* @param {Int} dy Y co-ordinate of destination onto canvas
	* @param {Int} dWidth Optional value to stretch the output width
	* @param {Int} dHeight Optional value to stretch the output height
	* @description Recalls a saved tile and displays it on the canvas at position dx,dy. You can optionally scale the tile to suit.
	*/	
	drawTile: function( ident, dx, dy, dWidth, dHeight ) {
		if(typeof this.tiles[ident] == this.engine.undef) return false;

		var t = this.tiles[ident];
		var dWidth = (typeof dWidth == this.engine.undef) ? t.sWidth : dWidth;
		var dHeight = (typeof dHeight == this.engine.undef) ? t.sHeight : dHeight;
		
		this.engine.stage2d.drawImage(this.image, t.sx, t.sy, t.sWidth, t.sHeight, dx, dy, dWidth, dHeight);
	},
	
	/**
	* @function
	* @public
	* @version all
	* @param {Int} parentX
	* @param {Int} parentY
	* @description Draws object to canvas
	* @example
	* Bitmap = engine.createBitmap({constructor});
	* Bitmap.draw(); 
	*/	
	draw: function(parentX, parentY) {
		parentX = (typeof parentX == this.engine.undef) ? 0 : parentX;
		parentY = (typeof parentY == this.engine.undef) ? 0 : parentY;
		
		if(this.visible) {
			if(typeof this.parent != this.engine.undef) {
				pA = this.parent.alpha;
				A = this.alpha
	
				if(pA < 100) {
					newGA = pA;
					
					if(A < 100) {
						// percentage of the parents
						percentage = pA * (A/100);
						newGA = pA - percentage;
					}
	
					this.engine.stage2d.globalAlpha = Math.abs(newGA) / 100;
				} else {
					if(A < 100) {
						this.engine.stage2d.globalAlpha = A / 100;
					}
				}
			} else {
				if(this.alpha < 100) {
					// Alter the global alpha to draw transparent
					this.engine.stage2d.globalAlpha = this.alpha / 100;
				}
			}
			
			// Scaling
			iW = this.image.naturalWidth;
			iH = this.image.naturalHeight;
			
			if(typeof this.parent != this.engine.undef) {
				if(this.x_scale != 100) {
					iW = iW * (this.x_scale / 100);
				}
				
				if(this.parent.x_scale != 100) {
					iW = iW * (this.parent.x_scale / 100);
				}
			} else {
				if(this.x_scale != 100) {
					iW = iW * (this.x_scale / 100);
				}
			}
			
			if(typeof this.parent != this.engine.undef) {
				if(this.y_scale != 100) {
					iH = iH * (this.y_scale / 100);
				}
				
				if(this.parent.y_scale != 100) {
					iH = iH * (this.parent.y_scale / 100);
				}
			} else {
				if(this.y_scale != 100) {
					iH = iH * (this.y_scale / 100);
				}
			}
			
			if(this.rotation != 0) {
				this.engine.stage2d.save();
				
				var tX = parentX + this.x;
				var tY = parentY + this.y;
	
				//tX = (this.registration_x != 0) ? tX + this.registration_x : tX;
				//tY = (this.registration_y != 0) ? tY + this.registration_y : tY;
				
				if(this.rotation > 360) {
					this.rotation = 0;
				}
				
				this.engine.stage2d.translate(tX, tY);
				this.engine.stage2d.rotate(this.rotation * this.engine._radians);
				this.engine.stage2d.drawImage(this.image, this.registration_x*-1, this.registration_y*-1,iW,iH);
				this.engine.stage2d.restore();
			} else {
				this.engine.stage2d.drawImage(this.image, parentX + this.x, parentY + this.y,iW,iH);
			}
			
			// Reset the global alpha to default.
			if(this.alpha < 100) {
				this.engine.stage2d.globalAlpha = this.engine.defGA;
			}
		} // END IF check for visibility
	} // End Draw function

};

/**
 * Creates a new bHive.Clip instance
 * @constructor
 * @version all
*/
bHive.Clip = function(construct, parentObject) {
	this.engine = parentObject;
	this._childObjects = [];
	this.events = [];
	
	for(var i in construct) {
		this[i] = construct[i];
	}
};

bHive.Clip.prototype = {
	id: null,
	events: null,
	visible: true,
	_childObjects: null,
	_mouseover: false,
	alpha: 100,
	x: 0,
	y: 0,
	x_scale: 100,
	y_scale: 100,
	
	/**
	* @function
	* @public
	* @version all
	* @param {String} sEvent Name of event
	* @param {Function} fCallback Function to call when event is fired
	* @description Adds an event listener to the Clip object
	* @example
	* Clip = engine.createClip({constructor});
	* Clip.addEventListener('onclick', myFunction); 
	*/	
	addEventListener: function (sEvent, fCallback) {
		this.events[sEvent] = fCallback;
	},

	/**
	* @function
	* @public
	* @version all
	* @param {Object} subObject Child object
	* @description Adds <i>subObject</i> to the Clip as child member
	* @example
	* Bitmap = engine.createBitmap({constructor});
	* Clip = engine.createClip({constructor});
	* Clip.add(Bitmap); 
	*/	
	add: function(subObject) {
		this._childObjects.push(subObject);
		var that = this;
		subObject.parent = that;
	},
	
	/**
	* @function
	* @public
	* @version all
	* @param {Int} parentX
	* @param {Int} parentY
	* @description Draws object to canvas
	* @example
	* Bitmap = engine.createBitmap({constructor});
	* Clip = engine.createClip({constructor});
	* Clip.add(Bitmap); 
	* Clip.draw();
	*/	
	draw: function(parentX, parentY) {
		parentX = (typeof parentX == this.engine.undef) ? 0 : parentX;
		parentY = (typeof parentY == this.engine.undef) ? 0 : parentY;

		if(this.visible) {
			for(var i in this._childObjects) {
				this._childObjects[i].draw(parentX + this.x, parentY + this.y);
			}
		}
	},
	
	point: function(x,y,ang) {
		var ang = 2 * Math.PI * ang / 360;
		var newx = x * Math.cos(ang) + y * Math.sin(ang);
		var newy = y * Math.cos(ang) - x * Math.sin(ang);
		
		return [newx,newy];
	},
	
	/**
	* @function
	* @public
	* @version all
	* @description Returns the width of a Clip object
	* @returns {Int} Width of object in pixels
	*/	
	width: function() {
	
		var Widths = [];
	
		// Check if the clip has child objects
		// if not then the width is always zero
		// so no need to process anything.
		if(this._childObjects.length > 0) {
			for(var co in this._childObjects) {
				var obj = this._childObjects[co];
				
				if(obj instanceof bHive.Bitmap) {
					if(obj.rotation > 0) {
						rotatedDimensions = engine.getRotatedSize(obj);
						Widths.push(rotatedDimensions.width + obj.x);
					} else {
						Widths.push(obj.image.naturalWidth + obj.x);
					}
				}
				else if(obj instanceof bHive.Clip && obj.visible) {
					Widths.push(obj.width() + obj.x);
				}
				else if(obj instanceof bHive.Shape && obj.visible) {
					if(obj.shape == "square") {
						Widths.push(obj.width + obj.x);
					}
					else if(obj.shape == "circle") {
						Widths.push(obj.x + (obj.radius*2));
					}
				}
			}

			return Math.max.apply(0,Widths);
		} else {
			return 0;
		}
		
	},
	
	/**
	* @function
	* @public
	* @version all
	* @description Returns the height of a Clip object
	* @returns {Int} Height of object in pixels
	*/	
	height: function() {
	
		var Heights = [];
		
		// Check if the clip has child objects
		// if not then the width is always zero
		// so no need to process anything.
		if(this._childObjects.length > 0) {
			for(var co in this._childObjects) {
				var obj = this._childObjects[co];
				
				if(obj instanceof bHive.Bitmap) {
					if(obj.rotation > 0) {
						rotatedDimensions = engine.getRotatedSize(obj);
						Heights.push(rotatedDimensions.height+obj.y);
					} else {
						Heights.push(obj.image.naturalHeight+obj.y);
					}
				}
				else if(obj instanceof bHive.Clip) {
					Heights.push(obj.height() + obj.y);
				}
				else if(obj instanceof bHive.Shape) {
					if(obj.shape == "square") {
						Heights.push(obj.height + obj.y);
					}
					else if(obj.shape == "circle") {
						Heights.push(obj.y + (obj.radius*2));
					}
				}
			}

			return Math.max.apply(0,Heights);
		} else {
			return 0;
		}

	}
}

/**
 * Creates a new bHive.Video instance
 * @constructor
 * @version all
*/
bHive.Video = function(construct, parentObject) {
	this.engine = parentObject;
	this.source = [];
	this.events = [];
	
	for(var i in construct) {
		this[i] = construct[i];
	}
	
	this.createDOMObject();
}

bHive.Video.prototype = {
	path: '',
	src: '',
	alpha: 100,
	x: 0,
	y: 0,
	x_scale: 100,
	y_scale: 100,
	rotation: 0,
	width: 0,
	height: 0,
	registration_x: 0,
	registration_y: 0,
	events: null,

	// Video specific properties
	formats: ['mp4','ogg','webm'],
	duration: 0,
	currentTime: 0,
	ended: false,
	muted: false,
	paused: false,
	format: "",
	looping: true,
	autoplay: true,
	controls: false,
	
	createDOMObject: function() {
		this.domvid = document.createElement('video');
		this.domvid.controls = this.controls;
		this.domvid.autoplay = this.autoplay;
		this.domvid.style.display = 'none';
		
		var dv = this.domvid;
		this.format = this.supportedFormat();
		
		if(this.format != "") {
			this.domvid.setAttribute("src",this.path + '/' + this.src + '.' + this.format);
			
			if(this.looping) {
				this.domvid.setAttribute("loop", "loop");
				this.engine.bind(this.domvid, 'ended', function(dv) { return function(e) { dv.play(); }; }(dv), true);
			}
			
			this.engine.bind(this.domvid, 'canplaythrough', function(dv) { return function(e) { console.log(dv.height); }; }(dv), true);
			document.getElementsByTagName('body')[0].appendChild(this.domvid);
		} else {
			return false;
		}
	},
	
	/**
	* @function
	* @private
	* @version all
	* @description Checks what format is best suited for the browser and returns.
	*/	
	supportedFormat: function() {
		var extension = "";

		for(var i = 0, l = this.formats.length; i < l; i++) {
			if(this.domvid.canPlayType("video/" + this.formats[i]) == "probably" || this.domvid.canPlayType("video/" + this.formats[i]) == "maybe") {
				extension = this.formats[i];
				break;
			}
		}
		
		return extension;
	},
	
	/**
	* @function
	* @public
	* @version all
	* @description Pauses the video playback
	*/	
	pause: function() {
		if(!this.domvid.paused) {
			this.paused = true;
			this.domvid.pause();
		}
	},
	
	/**
	* @function
	* @public
	* @version all
	* @description Start the video playback
	*/	
	play: function() {
		if(this.domvid.paused) {
			this.paused = false;
			this.domvid.play();
		}
	},
	
	/**
	* @function
	* @public
	* @version all
	* @param {Int} parentX
	* @param {Int} parentY
	* @description Draws object to canvas
	* @example
	* Video = engine.createVideo({constructor});
	* Video.draw();
	*/	
	draw: function(parentX, parentY) {
		parentX = (typeof parentX == this.engine.undef) ? 0 : parentX;
		parentY = (typeof parentY == this.engine.undef) ? 0 : parentY;

		if(typeof this.parent != this.engine.undef) {
			pA = this.parent.alpha;
			A = this.alpha

			if(pA < 100) {
				newGA = pA;
				
				if(A < 100) {
					// percentage of the parents
					percentage = pA * (A/100);
					newGA = pA - percentage;
				}

				this.engine.stage2d.globalAlpha = Math.abs(newGA) / 100;
			} else {
				if(A < 100) {
					this.engine.stage2d.globalAlpha = A / 100;
				}
			}
		} else {
			if(this.alpha < 100) {
				// Alter the global alpha to draw transparent
				this.engine.stage2d.globalAlpha = this.alpha / 100;
			}
		}

		// Scaling
		iW = this.width;
		iH = this.height;
		
		if(typeof this.parent != this.engine.undef) {
			if(this.x_scale != 100) {
				iW = iW * (this.x_scale / 100);
			}
			
			if(this.parent.x_scale != 100) {
				iW = iW * (this.parent.x_scale / 100);
			}
		} else {
			if(this.x_scale != 100) {
				iW = iW * (this.x_scale / 100);
			}
		}
		
		if(typeof this.parent != this.engine.undef) {
			if(this.y_scale != 100) {
				iH = iH * (this.y_scale / 100);
			}
			
			if(this.parent.y_scale != 100) {
				iH = iH * (this.parent.y_scale / 100);
			}
		} else {
			if(this.y_scale != 100) {
				iH = iH * (this.y_scale / 100);
			}
		}
		
		if(this.rotation != 0) {
			this.engine.stage2d.save();
			
			var tX = parentX + this.x;
			var tY = parentY + this.y;

			//tX = (this.registration_x != 0) ? tX + this.registration_x : tX;
			//tY = (this.registration_y != 0) ? tY + this.registration_y : tY;
			
			if(this.rotation > 360) {
				this.rotation = 0;
			}
			
			this.engine.stage2d.translate(tX, tY);
			this.engine.stage2d.rotate(this.rotation * this.engine._radians);
			this.engine.stage2d.drawImage(this.domvid, this.registration_x*-1, this.registration_y*-1,iW,iH);
			this.engine.stage2d.restore();
		} else {
			this.engine.stage2d.drawImage(this.domvid, parentX + this.x, parentY + this.y,iW,iH);
		}
		
		// Reset the global alpha to default.
		if(this.alpha < 100) {
			this.engine.stage2d.globalAlpha = this.engine.defGA;
		}
	},
	
	/**
	* @function
	* @public
	* @version all
	* @param {Int} x
	* @param {Int} y
	* @param {Int} w
	* @param {Int} h
	* @description Draws a segment of the video
	* @example
	* Video = engine.createVideo({constructor});
	* Video.slice(20, 20, 30, 30);
	*/	
	slice: function(x, y, w, h, destX, destY) {
		this.engine.stage2d.drawImage(this.domvid, x, y, x+w, y+h, destX, destY, w, h);
	}
}

/**
 * Creates a new bHive.Line instance
 * @constructor
 * @version all
*/
bHive.Line = function(construct, parentObject) {
	this.engine = parentObject;

	for(var i in construct) {
		this[i] = construct[i];
	}
};

bHive.Line.prototype = {
	weight: 1, // float value
	cap: 'butt', // Can be: butt, round, square
	corner: 'miter', // Can be: miter, round, bevel
	start: null,
	end: null,
	visible: true,
	controlpoints: null,
	color: 'rgba(0,0,0,1)',
	
	/**
	* @function
	* @public
	* @version all
	* @description Draws object to canvas
	* @example
	* Line = engine.createLine({constructor});
	* Line.draw();
	*/	
	draw: function() {
		if(this.start instanceof Array) {
			x1 = this.start[0];
			y1 = this.start[1];
		}
		else if(typeof this.start == "string") {
			switch(this.start) {
				case "mouse":
					x1 = this.engine._mouseX;
					y1 = this.engine._mouseY;
					break;
			}
		}
		else if(this.start instanceof Object) {
			x1 = this.start.x;
			y1 = this.start.y;
		}
		
		if(this.end instanceof Array) {
			x2 = this.end[0];
			y2 = this.end[1];
		}
		else if(typeof this.end == "string") {
			switch(this.end) {
				case "mouse":
					x2 = this.engine._mouseX;
					y2 = this.engine._mouseY;
					break;
			}
		}
		else if(this.start instanceof Object) {
			x2 = this.end.x;
			y2 = this.end.y;
		}

		if(this.color instanceof bHive.Gradient) {
			if(typeof this.color.dimensions == this.engine.undef) {
				// need to use the size of the object
				if(this.color.type == "linear") {
					// Linear Fill
					color = this.engine.stage2d.createLinearGradient(this.x, this.y, this.x + this.width, this.y + this.height);
				} else {
					// Raidal Fill
					color = this.engine.stage2d.createRadialGradient(this.x+(this.width/2), this.y+(this.height/2), 0, this.x+(this.width/2), this.y+(this.height/2), this.width/2);
				}
			} else {
				// we have overriding dimensions to alter the gradient
				if(this.color.type == "linear") {
					// Linear Fill
					color = this.engine.stage2d.createLinearGradient(this.color.dimensions[0], this.color.dimensions[1], this.color.dimensions[2], this.color.dimensions[3]);
				} else {
					// Raidal Fill
					color = this.engine.stage2d.createRadialGradient(this.color.dimensions[0], this.color.dimensions[1], this.color.dimensions[2], this.color.dimensions[3], this.color.dimensions[4], this.color.dimensions[5]);
				}
			}

			for(var i = 0, tc = this.color.colors.length; i < tc; i++) {
				color.addColorStop(this.color.stops[i], this.color.colors[i]);
			}

		} else {
			color = this.color;
		}
		
		this.engine.stage2d.save();
		this.engine.stage2d.beginPath();
		this.engine.stage2d.lineWidth = this.weight;
		this.engine.stage2d.lineCap = this.cap;
		this.engine.stage2d.lineJoin = this.corner;
		this.engine.stage2d.strokeStyle = color;

		if(this.controlpoints == null ) {
			// Type is straight
			this.engine.stage2d.moveTo(x1,y1);
			this.engine.stage2d.lineTo(x2,y2);
		}
		else if(this.controlpoints.length == 1) {
			// Quadratic
			this.engine.stage2d.moveTo(x1,y1);
			this.engine.stage2d.quadraticCurveTo(this.controlpoints[0].x,this.controlpoints[0].y,x2,y2);
		}
		else if(this.controlpoints.length == 2) {
			// Bezier
			this.engine.stage2d.moveTo(x1,y1);
			this.engine.stage2d.quadraticCurveTo(this.controlpoints[0].x,this.controlpoints[0].y,this.controlpoints[1].x,this.controlpoints[1].y,x2,y2);
		}
		this.engine.stage2d.stroke();
		this.engine.stage2d.restore();
	}
};

/**
 * Creates a new bHive.Text instance
 * @constructor
 * @version all
*/
bHive.Text = function (construct, parentObject) {
	this.engine = parentObject;
	
	for(var i in construct) {
		this[i] = construct[i];
	}
};

bHive.Text.prototype = {
	text: null,
	x: 0,
	y: 0,
	align: 'top',
	color: 'rgb(0, 0, 0)',
	outline: 'rgb(255,0,0)',
	visible: true,
	font: '10px Arial',
	style: 'filled',
	
	/**
	* @function
	* @public
	* @version all
	* @param {Int} parentX
	* @param {Int} parentY
	* @description Draws object to canvas
	* @example
	* Text = engine.createText({constructor});
	* Text.draw();
	*/	
	draw: function(parentX, parentY) {
		parentX = (typeof parentX == this.engine.undef) ? 0 : parentX;
		parentY = (typeof parentY == this.engine.undef) ? 0 : parentY;

		this.engine.stage2d.textBaseline = this.align;
		this.engine.stage2d.font = this.font;
		
		if(this.style == "stroked") {
			this.engine.stage2d.fillStyle = this.color;
			this.engine.stage2d.strokeText(this.text, parentX + this.x, parentY + this.y);
		}
		else if(this.style == "both") {
			this.engine.stage2d.fillStyle = this.color;
			this.engine.stage2d.fillText(this.text, parentX + this.x, parentY + this.y);
			this.engine.stage2d.strokeStyle = this.outline;	
			this.engine.stage2d.strokeText(this.text, parentX + this.x, parentY + this.y);
		}
		else {
			this.engine.stage2d.strokeStyle = this.color;	
			this.engine.stage2d.fillText(this.text, parentX + this.x, parentY + this.y);
		}
	},
	
};

/**
 * Creates a new bHive.Shape instance
 * @constructor
 * @version all
*/
bHive.Shape = function(construct, parentObject) {
	this.engine = parentObject;
	
	for(var i in construct) {
		this[i] = construct[i];
	}
};

bHive.Shape.prototype = {
	shape: 'square',
	style: 'filled', // Can be: filled, stroke
	x: 0,
	y: 0,
	width: 0,
	height: 0,
	radius: 0,
	backgroundColor: 'rgba(0, 0, 0, 1)',
	strokeColor: 'rgba(0, 0, 0, 1)',
	strokeWeight: 1,
	alpha: 100,
	visible: true,
	close: true,
	
	/**
	* @function
	* @public
	* @version all
	* @param {Int} parentX
	* @param {Int} parentY
	* @description Draws object to canvas
	* @example
	* Shape = engine.createShape({constructor});
	* Shape.draw();
	*/	
	draw: function(parentX, parentY) {
		parentX = (typeof parentX == this.engine.undef) ? 0 : parentX;
		parentY = (typeof parentY == this.engine.undef) ? 0 : parentY;

		var ctx = this.engine.stage2d;
		var PI_2 = Math.PI * 2;
	
		if(this.backgroundColor instanceof bHive.Gradient) {
			// need to use the size of the object
			if(this.backgroundColor.type == "linear") {
				// Linear Fill
				var h = (this.shape == "circle") ? this.radius*2 : this.height;
				var w = (this.shape == "circle") ? this.radius*2 : this.width;
				var b = (this.backgroundColor.dir > 360) ? (this.backgroundColor.dir%360) : this.backgroundColor.dir;
				var rad = Math.PI/180;
				
				point1 = { x: this.x, y: this.y };
				point2 = { x: this.x, y: this.y + h };
				
				if(b == 45 || b == 90 || b == 135 || b == 180 || b == 225 || b == 270) {
					// Common angles where really no Maths is needed.
					switch(b) {
						case 45:
							point1.x = this.x;
							point1.y = this.y;
							point2.x = this.x + w;
							point2.y = this.y + h;
							break;
						case 90:
							point1.x = this.x;
							point1.y = this.y;
							point2.x = this.x + w;
							point2.y = this.y;
							break;
						case 135:
							point1.x = this.x;
							point1.y = this.y + h;
							point2.x = this.x + w;
							point2.y = this.y;
							break;
						case 180:
							point1.x = this.x;
							point1.y = this.y + h;
							point2.x = this.x;
							point2.y = this.y;
							break;
						case 225:
							point1.x = this.x + w;
							point1.y = this.y + h;
							point2.x = this.x;
							point2.y = this.y;
							break;
						case 270:
							point1.x = this.x + w;
							point1.y = this.y;
							point2.x = this.x;
							point2.y = this.y;
							break;
					}
				}
				else if(b > 0) {
					box_ang = (b % 90); // Find the boxed version of the angle
					
					if(b <= 90) {
						source_x = ( Math.tan(b*Math.PI/180) ) * h;
						source_y = w / ( Math.tan(b*Math.PI/180) );
	
						point1.x = this.x;
						point1.y = this.y;
						
						point2.x = (this.x + source_x > this.x + w) ? this.x + w : this.x + source_x;
						point2.y = (this.x + source_x > this.x + w) ? this.y + source_y : this.y + h;
					}
					else if(b > 91 && b <= 180) {
						source_x = h / ( Math.tan(box_ang*Math.PI/180) );
						source_y = ( Math.tan(box_ang*Math.PI/180) ) * w;
						
						point1.x = (this.y + source_y > this.y + h) ? this.x + source_x : this.x;
						point1.y = (this.y + source_y > this.y + h) ? this.y + h : this.y + source_y;

						point2.x = this.x + w;
						point2.y = this.y;
					}
					else if(b > 180 && b <= 270) {
						source_x = ( Math.tan(box_ang*Math.PI/180) ) * h;
						source_y = w / ( Math.tan(box_ang*Math.PI/180) );

						point1.x = (this.x + source_x > this.x + w) ? this.x + w : this.x + source_x;
						point1.y = (this.x + source_x > this.x + w) ? this.y + source_y : this.y + h;

						point2.x = this.x;
						point2.y = this.y;
					}
					else if(b > 270 && b <= 359) {
						source_x = h / ( Math.tan(box_ang*Math.PI/180) );
						source_y = ( Math.tan(box_ang*Math.PI/180) ) * w;

						point1.x = this.x + w;
						point1.y = this.y;
						
						point2.x = (this.y + source_y > this.y + h) ? this.x + source_x : this.x;
						point2.y = (this.y + source_y > this.y + h) ? this.y + h : this.y + source_y;
					}
				}
				
				bgColor = this.engine.stage2d.createLinearGradient(point1.x, point1.y, point2.x, point2.y);
			} else {
				// Raidal Fill
				var h = (this.shape == "circle") ? this.radius*2 : this.height;
				var w = (this.shape == "circle") ? this.radius*2 : this.width;
				var o = (this.shape == "circle") ? this.radius : this.width;
				
				bgColor = this.engine.stage2d.createRadialGradient(this.x+(w/2), this.y+(h/2), 0, this.x+(w/2), this.y+(h/2), o);
			}

			for(var i = 0, tc = this.backgroundColor.colors.length; i < tc; i++) {
				bgColor.addColorStop(this.backgroundColor.stops[i], this.backgroundColor.colors[i]);
			}
		} else {
			if(this.backgroundColor.indexOf('#') != -1) {
				bgColor = this.engine.hex2RGBa(this.backgroundColor,this.alpha);
			} else {
				bgColor = this.backgroundColor;
			}
		}
		
		ctx.save();

		switch(this.shape) {
			case 'square':
				if(this.style == 'filled') {
					ctx.fillStyle = bgColor;
					ctx.fillRect(parentX+this.x,parentY+this.y,this.width,this.height);
				} else {
					ctx.lineWidth = 1;
					ctx.strokeStyle = bgColor;
					ctx.strokeRect(parentX+this.x,parentY+this.y,this.width,this.height);
				}
				break;
			case 'circle':
				var center_x = parentX+this.x+this.radius;
				var center_y = parentY+this.y+this.radius;
				
				if(this.style == 'filled') {
					ctx.beginPath();
					ctx.fillStyle = bgColor;
					ctx.arc(center_x,center_y,this.radius,0,PI_2,true);
					ctx.fill();
				} else {
					ctx.beginPath();
					ctx.strokeStyle = bgColor;
					ctx.arc(center_x,center_y,this.radius,0,PI_2,true);
					ctx.stroke();
				}
				break;
			case 'poly':
				ctx.beginPath();

				var pts = this.points
				var num_pts = pts.length;
				
				var maxWidth = 0;
				var maxHeight = 0;

				if(num_pts>1) {
					ctx.moveTo(parentX+this.x+pts[0].x,parentY+this.y+pts[0].y);
					
					for(i = 1; i < num_pts; i++) {
						maxWidth = Math.max(maxWidth,pts[i].x);
						maxHeight = Math.max(maxHeight,pts[i].y);
						ctx.lineTo(parentX+this.x+pts[i].x,parentY+this.y+pts[i].y);
					}
					
					this.width = maxWidth;
					this.height = maxHeight;
				}

				if(this.style == 'filled') {
					ctx.fillStyle = bgColor;
					ctx.fill();
				} else {
					if(this.close) {
						ctx.closePath();
					}
					
					ctx.strokeStyle = bgColor;
					ctx.stroke();
				}
				break;
			case 'elipse':
				ctx.beginPath();

				ctx.moveTo((parentX+this.x)+(this.width/2), parentY+this.y);

				ctx.bezierCurveTo(
					((parentX+this.x)+(this.width/2)) + this.width/2,
					((parentY+this.y) + (this.height/2)) - this.height/2,
					((parentX+this.x)+(this.width/2)) + this.width/2,
					((parentY+this.y) + (this.height/2)) + this.height/2,
					((parentX+this.x)+(this.width/2)),
					((parentY+this.y) + (this.height/2)) + this.height/2
				);
		
				ctx.bezierCurveTo(
					((parentX+this.x)+(this.width/2)) - this.width/2,
					((parentY+this.y) + (this.height/2)) + this.height/2,
					((parentX+this.x)+(this.width/2)) - this.width/2,
					((parentY+this.y) + (this.height/2)) - this.height/2,
					((parentX+this.x)+(this.width/2)),
					((parentY+this.y) + (this.height/2)) - this.height/2
				);
				
				if(this.style == 'filled') {
					ctx.fillStyle = bgColor;
					ctx.fill();
				} else {
					ctx.strokeStyle = bgColor;
					ctx.stroke();
				}
				break;
		} // End SWITCH
		
		ctx.restore();
	}
};

/**
 * Creates a new bHive.Sprite instance
 * @constructor
 * @version all
*/
bHive.Sprite = function(construct, parentObject) {
	var that = this;
	this.engine = parentObject;
	this.events = [];
	
	for(var i in construct) {
		this[i] = construct[i];
	}
	
	if(this.src instanceof bHive.Bitmap) {
		this.image = this.src.image;
		this.framecount = this.framedata.length;
	} else {
		this.image = new Image();
		this.image.src = this.src;
		this.image.onload = (function(img) {
			return function() {
				if(typeof img.events.onload != img.engine.undef) {
					img.events.onload();
				}
				that.framecount = that.framedata.length;
			}
		})(that);
	}
};

bHive.Sprite.prototype = {
	src: '',
	width: 0,
	height: 0,
	buffer: null,
	framedata: null,
	events: null,
	image: null,
	alpha: 100,
	rotation: 0,
	visible: true,
	frame: 0,
	framecount: 0,
	framespeed: 0,
	frametimer: 0,
	registration_x: 0,
	registration_y: 0,
	x_scale: 100,
	y_scale: 100,
	playing: true,
	
	/**
	* @function
	* @public
	* @version all
	* @description Creates a hidden canvas object for future expansion of this class, not currently used.
	*/	
	createBuffer: function() {
		this.buffer = document.createElement('canvas');
		this.buffer.width = this.width;
		this.buffer.height = this.height;
		document.getElementsByTagName('body')[0].appendChild(this.buffer);
	},
	
	/**
	* @function
	* @public
	* @version all
	* @param {String} sEvent
	* @param {Function} fCallback
	* @description adds an Event Listener function to a particular event
	*/	
	addEventListener: function (sEvent, fCallback) {
		this.events[sEvent] = fCallback;
	},

	/**
	* @function
	* @public
	* @version all
	* @param {Int} parentX
	* @param {Int} parentY
	* @description Draws object to canvas
	* @example
	* Sprite = engine.createSprite({constructor});
	* Sprite.draw();
	*/	
	draw: function(parentX, parentY) {
		parentX = (typeof parentX == this.engine.undef) ? 0 : parentX;
		parentY = (typeof parentY == this.engine.undef) ? 0 : parentY;
		
		if(typeof this.parent != this.engine.undef) {
			pA = this.parent.alpha;
			A = this.alpha

			if(pA < 100) {
				newGA = pA;
				
				if(A < 100) {
					// percentage of the parents
					percentage = pA * (A/100);
					newGA = pA - percentage;
				}

				this.engine.stage2d.globalAlpha = Math.abs(newGA) / 100;
			} else {
				if(A < 100) {
					this.engine.stage2d.globalAlpha = A / 100;
				}
			}
		} else {
			if(this.alpha < 100) {
				// Alter the global alpha to draw transparent
				this.engine.stage2d.globalAlpha = this.alpha / 100;
			}
		}
		
		// Scaling
		iW = this.framedata[this.frame].frame.w;
		iH = this.framedata[this.frame].frame.h;
		
		if(typeof this.parent != this.engine.undef) {
			if(this.x_scale != 100) {
				iW = iW * (this.x_scale / 100);
			}
			
			if(this.parent.x_scale != 100) {
				iW = iW * (this.parent.x_scale / 100);
			}
		} else {
			if(this.x_scale != 100) {
				iW = iW * (this.x_scale / 100);
			}
		}
		
		
		if(typeof this.parent != this.engine.undef) {
			if(this.y_scale != 100) {
				iH = iH * (this.y_scale / 100);
			}
			
			if(this.parent.y_scale != 100) {
				iH = iH * (this.parent.y_scale / 100);
			}
		} else {
			if(this.y_scale != 100) {
				iH = iH * (this.y_scale / 100);
			}
		}
		
		// Slicing using the overloaded canvas drawImage
		// params are: image, sx, sy, sWidth, sHeight, dx, dy, dWidth, dHeight
		var sx = this.framedata[this.frame].frame.x;
		var sy = this.framedata[this.frame].frame.y;
		var sWidth = this.framedata[this.frame].frame.w;
		var sHeight = this.framedata[this.frame].frame.h;
		var dx = this.x;
		var dy = this.y;
		var dWidth = iW;
		var dHeight = iH;

		if(this.rotation != 0) {
			this.engine.stage2d.save();
			
			var tX = parentX + this.x;
			var tY = parentY + this.y;

			//tX = (this.registration_x != 0) ? tX + this.registration_x : tX;
			//tY = (this.registration_y != 0) ? tY + this.registration_y : tY;
			
			if(this.rotation > 360) {
				this.rotation = 0;
			}
			
			this.engine.stage2d.translate(tX, tY);
			this.engine.stage2d.rotate(this.rotation * this.engine._radians);
			this.engine.stage2d.drawImage(this.image, sx, sy, sWidth, sHeight, this.registration_x*-1, this.registration_y*-1, dWidth, dHeight);
			this.engine.stage2d.restore();
		} else {
			this.engine.stage2d.drawImage(this.image,sx,sy,sWidth,sHeight,dx,dy,dWidth,dHeight);
		}
		
		if(this.playing) {
			this.nextTimedFrame();
		}
		
		// Reset the global alpha to default.
		if(this.alpha < 100) {
			this.engine.stage2d.globalAlpha = this.engine.defGA;
		}
	},
	
	/**
	* @function
	* @public
	* @version all
	* @description Advanced the frame counter to the next frame according the the frame timer
	*/	
	nextTimedFrame: function() {
		if(this.frametimer == 0) {
			this.frame++;
			
			if(this.frame > this.framecount-1) {
				this.frame = 0;
			}
	
			this.frametimer = this.framespeed;
		} else {
			if(this.frametimer > 0) {
				this.frametimer--;
			}
		}
	},

	/**
	* @function
	* @public
	* @version all
	* @description Advances the frame counter to the next frame regardless of the frame timer
	*/	
	nextFrame: function() {
		this.frame++;

		if(this.frame > this.framecount-1) {
			this.frame = 0;
		}
	},
	
	/**
	* @function
	* @public
	* @version all
	* @param {Int} frame Integer frame number, 0 being first frame
	* @description Advances the frame counter to the specified frame
	*/	
	gotoFrame: function(frame) {
		this.frame = (frame > this.framecount-1 || frame < 0) ? 0 : frame;
	},
	
	/**
	* @function
	* @public
	* @version all
	* @description Stops play back of an animation
	*/	
	stop: function() {
		this.playing = false;
	},
	
	/**
	* @function
	* @public
	* @version all
	* @description Starts play of an animation
	*/	
	play: function() {
		this.playing = true;
	},

	/**
	* @function
	* @public
	* @version all
	* @description Sets the frame rate for the sprite animation
	*/	
	setFrameSpeed: function(speed) {
		this.framespeed = (speed > 0) ? speed : 1;
	},
	
	/**
	* @function
	* @public
	* @version all
	* @description Returns the current frame rate of the sprite animation
	*/	
	getFrameSpeed: function() {
		return(this.framespeed);
	},

};

/**
 * Creates a new bHive.Gradient instance
 * @constructor
 * @version all
*/
bHive.Gradient = function(construct, parentObject) {
	this.engine = parentObject;
	this.colors = [];
	this.stops = [];
	
	for(var i in construct) {
		this[i] = construct[i];
	}
};

bHive.Gradient.prototype = {
	engine: null,
	dir: 0,
	colors: null,
	stops: null
};

/**
 * Creates a new bHive.Audio instance
 * @constructor
 * @version all
*/
bHive.Sound = function(construct, parentObject) {
	this.engine = parentObject;
	this.events = [];
	
	for(var i in construct) {
		this[i] = construct[i];
	}
	
	this.createDOMObject();
};

bHive.Sound.prototype = {
	path: '',
	src: '',
	events: null,
	engine: null,

	// Video specific properties
	formats: ['mp3','ogg','wav'],
	duration: 0,
	currentTime: 0,
	ended: false,
	muted: false,
	paused: false,
	format: "",
	looping: true,
	autoplay: false,
	controls: false,

	addEventListener: function (sEvent, fCallback) {
		this.events[sEvent] = fCallback;
	},
	
	deleteEventListener: function(sEvent) {
		if(typeof this.events[sEvent] != this.engine.undef) {
			delete this.events[sEvent];
			console.log("deleted");
		}
	},

	createDOMObject: function() {
		this.domAudio = document.createElement('audio');
		this.domAudio.controls = this.controls;
		this.domAudio.autoplay = this.autoplay;
		this.domAudio.style.display = 'none';
		
		var da = this;
		this.format = this.supportedFormat();
		
		if(this.format != "") {
			this.domAudio.setAttribute("src",this.path + '/' + this.src + '.' + this.format);
			
			if(this.looping) {
				this.domAudio.setAttribute("loop", "loop");
			}
			
			this.engine.bind(this.domAudio, 'ended', function(da) {
				return function(e) {
					if(typeof da.events.onended != da.engine.undef) {
						// Fire the event
						da.events.onended();
					}
					
					if(da.looping) {
						da.play();
					}
				};
			}(da), true);

			// Can play when enough data is down
			this.engine.bind(this.domAudio, 'canplay', function(da) {
				return function() {
					if(typeof da.events.oncanplay != da.engine.undef) {
						da.events.oncanplay({ obj: da, event: 'canplay' });
					}
				};
			}(da), true);

			// Can play when all data is loaded.
			// Wont fire if autoplay is set to false on Firefox
			this.engine.bind(this.domAudio, 'canplaythrough', function(da) {
				return function() {
					if(typeof da.events.canplaythrough != da.engine.undef) {
						da.events.oncanplaythough({ obj: da, event: 'canplaythrough' });
					}
				};
			}(da), true);

			document.getElementsByTagName('body')[0].appendChild(this.domAudio);
		} else {
			return false;
		}
	},
	
	/**
	* @function
	* @public
	* @version all
	* @description Returns the length of the audio object
	*/	
	getDuration: function() {
		return this.domAudio.duration;
	},
	
	/**
	* @function
	* @public
	* @version all
	* @description Returns the current play time of the audio object
	*/	
	getCurrentTime: function() {
		return this.domAudio.currentTime;
	},

	/**
	* @function
	* @public
	* @version all
	* @description Sets the currentTime to a new position
	*/	
	setCurrentTime: function(pos) {
		var len = this.getDuration();
		var t = pos;
		
		if(t > len || t < 0) {
			return false;
		} else {
			this.domAudio.currentTime = t;
		}
	},

	/**
	* @function
	* @public
	* @version all
	* @description Returns the current volume level of the audio object
	*/	
	getVolume: function() {
		return this.domAudio.volume;
	},
	
	/**
	* @function
	* @public
	* @version all
	* @description Sets the volume to desired level range: 0 - 100
	*/	
	setVolume: function(volume) {
		v = volume / 100;
		this.domAudio.volume = v;
	},
	
	/**
	* @function
	* @public
	* @version all
	* @description Pauses playback at the <em>currentTime</em>
	*/	
	pause: function() {
		if(!this.domAudio.paused) {
			this.domAudio.pause();
			return true;
		} else {
			return false;
		}
	},
	
	/**
	* @function
	* @public
	* @version all
	* @description Starts play from <em>currentTime</em>
	*/	
	play: function() {
		if(this.domAudio.paused) {
			this.domAudio.play();
			return true;
		} else {
			return false;
		}
	},
	
	/**
	* @function
	* @public
	* @version all
	* @description Stops playback and returns the currentTime to 0
	*/	
	stop: function() {
		this.domAudio.currentTime = 0;
		if(this.pause()) {
			return true;
		} else {
			return false;
		}
	},
	

	/**
	* @function
	* @private
	* @version all
	* @description Checks what format is best suited for the browser and returns.
	*/	
	supportedFormat: function() {
		var extension = "";

		for(var i = 0, l = this.formats.length; i < l; i++) {
			if(this.domAudio.canPlayType("audio/" + this.formats[i]) == "probably" || this.domAudio.canPlayType("audio/" + this.formats[i]) == "maybe") {
				extension = this.formats[i];
				break;
			}
		}
		
		return extension;
	},
};
