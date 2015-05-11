/*	
 * jQuery mmenu v5.0.4
 * @requires jQuery 1.7.0 or later
 *
 * mmenu.frebsite.nl
 *	
 * Copyright (c) Fred Heusschen
 * www.frebsite.nl
 *
 * Licensed under the MIT license:
 * http://en.wikipedia.org/wiki/MIT_License
 */
/*	
 * jQuery mmenu v5.0.4
 * @requires jQuery 1.7.0 or later
 *
 * mmenu.frebsite.nl
 *	
 * Copyright (c) Fred Heusschen
 * www.frebsite.nl
 *
 * Licensed under the MIT license:
 * http://en.wikipedia.org/wiki/MIT_License
 */


(function( $ ) {

	var _PLUGIN_	= 'mmenu',
		_VERSION_	= '5.0.4';


	//	Plugin already excists
	if ( $[ _PLUGIN_ ] )
	{
		return;
	}


	/*
		Class
	*/
	$[ _PLUGIN_ ] = function( $menu, opts, conf )
	{
		this.$menu	= $menu;
		this._api	= [ 'bind', 'init', 'update', 'setSelected', 'getInstance', 'openPanel', 'closePanel', 'closeAllPanels' ];
		this.opts	= opts;
		this.conf	= conf;
		this.vars	= {};
		this.cbck	= {};


		if ( typeof this.___deprecated == 'function' )
		{
			this.___deprecated();
		}

		this._initMenu();
		this._initAnchors();

		var $panels = this.$menu.children( this.conf.panelNodetype );
		
		this._initAddons();
		this.init( $panels );


		if ( typeof this.___debug == 'function' )
		{
			this.___debug();
		}

		return this;
	};

	$[ _PLUGIN_ ].version 	= _VERSION_;
	$[ _PLUGIN_ ].addons 	= {};
	$[ _PLUGIN_ ].uniqueId 	= 0;


	$[ _PLUGIN_ ].defaults 	= {
		extensions		: [],
		onClick			: {
//			close			: true,
//			blockUI			: null,
//			preventDefault	: null,
			setSelected		: true
		},
		slidingSubmenus	: true
	};

	$[ _PLUGIN_ ].configuration = {
		classNames			: {
			panel		: 'Panel',
			vertical	: 'Vertical',
			selected	: 'Selected',
			divider		: 'Divider',
			spacer		: 'Spacer'
		},
		clone				: false,
		openingInterval		: 25,
		panelNodetype		: 'ul, ol, div',
		transitionDuration	: 400
	};

	$[ _PLUGIN_ ].prototype = {

		init: function( $panels )
		{
			$panels = $panels.not( '.' + _c.nopanel );
			$panels = this._initPanels( $panels );

			this.trigger( 'init', $panels );
			this.trigger( 'update' );
		},

		update: function()
		{
			this.trigger( 'update' );
		},

		setSelected: function( $i )
		{
			this.$menu.find( '.' + _c.listview ).children().removeClass( _c.selected );
			$i.addClass( _c.selected );
			this.trigger( 'setSelected', $i );
		},
		
		openPanel: function( $panel )
		{
			var $l = $panel.parent();

			//	Vertical
			if ( $l.hasClass( _c.vertical ) )
			{
				var $sub = $l.parents( '.' + _c.subopened );
				if ( $sub.length )
				{
					return this.openPanel( $sub.first() );
				}
				$l.addClass( _c.opened );
			}

			//	Horizontal
			else
			{
				if ( $panel.hasClass( _c.current ) )
				{
					return;
				}

				var $panels = $(this.$menu).children( '.' + _c.panel ),
					$current = $panels.filter( '.' + _c.current );

				$panels
					.removeClass( _c.highest )
					.removeClass( _c.current )
					.not( $panel )
					.not( $current )
					.not( '.' + _c.vertical )
					.addClass( _c.hidden );

				if ( $panel.hasClass( _c.opened ) )
				{
					$current
						.addClass( _c.highest )
						.removeClass( _c.opened )
						.removeClass( _c.subopened );
				}
				else
				{
					$panel.addClass( _c.highest );
					$current.addClass( _c.subopened );
				}

				$panel
					.removeClass( _c.hidden )
					.addClass( _c.current );

				//	Without the timeout, the animation won't work because the element had display: none;
				setTimeout(
					function()
					{
						$panel
							.removeClass( _c.subopened )
							.addClass( _c.opened );
					}, this.conf.openingInterval
				);
			}
			this.trigger( 'openPanel', $panel );
		},

		closePanel: function( $panel )
		{
			var $l = $panel.parent();

			//	Vertical only
			if ( $l.hasClass( _c.vertical ) )
			{
				$l.removeClass( _c.opened );
				this.trigger( 'closePanel', $panel );
			}
		},

		closeAllPanels: function()
		{
			//	Vertical
			this.$menu.find( '.' + _c.listview )
				.children()
				.removeClass( _c.selected )
				.filter( '.' + _c.vertical )
				.removeClass( _c.opened );

			//	Horizontal
			var $pnls = this.$menu.children( '.' + _c.panel ),
				$frst = $pnls.first();

			this.$menu.children( '.' + _c.panel )
				.not( $frst )
				.removeClass( _c.subopened )
				.removeClass( _c.opened )
				.removeClass( _c.current )
				.removeClass( _c.highest )
				.addClass( _c.hidden );

			this.openPanel( $frst );
		},
		
		togglePanel: function( $panel )
		{
			var $l = $panel.parent();

			//	Vertical only
			if ( $l.hasClass( _c.vertical ) )
			{
				this[ $l.hasClass( _c.opened ) ? 'closePanel' : 'openPanel' ]( $panel );
			}
		},

		getInstance: function()
		{
			return this;
		},

		bind: function( event, fn )
		{
			this.cbck[ event ] = this.cbck[ event ] || [];
			this.cbck[ event ].push( fn );
		},

		trigger: function()
		{
			var that = this,
				args = Array.prototype.slice.call( arguments ),
				evnt = args.shift();

			if ( this.cbck[ evnt ] )
			{
				for ( e in this.cbck[ evnt ] )
				{
					this.cbck[ evnt ][ e ].apply( that, args );
				}
			}
		},

		_initMenu: function()
		{
			var that = this;

			//	Clone if needed
			if ( this.opts.offCanvas && this.conf.clone )
			{
				this.$menu = this.$menu.clone( true );
				this.$menu.add( this.$menu.find( '*' ) )
					.filter( '[id]' )
					.each(
						function()
						{
							$(this).attr( 'id', _c.mm( $(this).attr( 'id' ) ) );
						}
					);
			}

			//	Strip whitespace
			this.$menu.contents().each(
				function()
				{
					if ( $(this)[ 0 ].nodeType == 3 )
					{
						$(this).remove();
					}
				}
			);

			this.$menu
				.parent()
				.addClass( _c.wrapper );

			var clsn = [ _c.menu ];

			//	Add direction class
			if ( !this.opts.slidingSubmenus )
			{
				clsn.push( _c.vertical );
			}

			//	Add extensions
			this.opts.extensions = ( this.opts.extensions.length )
				? 'mm-' + this.opts.extensions.join( ' mm-' )
				: '';

			if ( this.opts.extensions )
			{
				clsn.push( this.opts.extensions );
			}

			this.$menu.addClass( clsn.join( ' ' ) );
		},

		_initPanels: function( $panels )
		{
			var that = this;

			//	Add List class
			this.__findAddBack( $panels, 'ul, ol' )
				.not( '.' + _c.nolistview )
				.addClass( _c.listview );

			var $lis = this.__findAddBack( $panels, '.' + _c.listview ).children();

			//	Refactor Selected class
			this.__refactorClass( $lis, this.conf.classNames.selected, 'selected' );

			//	Refactor divider class
			this.__refactorClass( $lis, this.conf.classNames.divider, 'divider' );

			//	Refactor Spacer class
			this.__refactorClass( $lis, this.conf.classNames.spacer, 'spacer' );

			//	Refactor Panel class
			this.__refactorClass( this.__findAddBack( $panels, '.' + this.conf.classNames.panel ), this.conf.classNames.panel, 'panel' );

			//	Create panels
			var $curpanels = $(),
				$oldpanels = $panels
					.add( this.__findAddBack( $panels, '.' + _c.listview ).children().children( this.conf.panelNodetype ) )
					.not( '.' + _c.nopanel );

			this.__refactorClass( $oldpanels, this.conf.classNames.vertical, 'vertical' );
			
			if ( !this.opts.slidingSubmenus )
			{
				$oldpanels.addClass( _c.vertical );
			}

			$oldpanels
				.each(
					function()
					{
						var $t = $(this),
							$p = $t;

						if ( $t.is( 'ul, ol' ) )
						{
							$t.wrap( '<div class="' + _c.panel + '" />' );
							$p = $t.parent();
						}
						else
						{
							$p.addClass( _c.panel );
						}

						var id = $t.attr( 'id' );
						$t.removeAttr( 'id' );
						$p.attr( 'id', id || that.__getUniqueId() );

						if ( $t.hasClass( _c.vertical ) )
						{
							$t.removeClass( that.conf.classNames.vertical );
							$p.add( $p.parent() ).addClass( _c.vertical );
						}

						$curpanels = $curpanels.add( $p );

						var $f = $p.children().first(),
							$l = $p.children().last();

						if ( $f.is( '.' + _c.listview ) )
						{
							$f.addClass( _c.first );
						}
						if ( $l.is( '.' + _c.listview ) )
						{
							$l.addClass( _c.last );
						}
					} 
				);

			var $allpanels = $('.' + _c.panel, this.$menu);

			//	Add open and close links to menu items
			$curpanels
				.each(
					function( i )
					{
						var $t = $(this),
							$l = $t.parent(),
							$a = $l.children( 'a, span' );

						if ( !$l.is( '.' + _c.menu ) && !$t.data( _d.parent ) )
						{
							$l.data( _d.sub, $t );
							$t.data( _d.parent, $l );

							if ( $l.parent().is( '.' + _c.listview ) )
							{
								//	Open link
								var id = $t.attr( 'id' ),
									$b = $( '<a class="' + _c.next + '" href="#' + id + '" data-target="#' + id + '" />' ).insertBefore( $a );
	
								if ( !$a.is( 'a' ) )
								{
									$b.addClass( _c.fullsubopen );
								}
							}

							//	Close link
							if ( !$l.hasClass( _c.vertical ) )
							{
								var $p = $l.closest( '.' + _c.panel );
								if ( $p.length )
								{
									var id = $p.attr( 'id' );
									$t.prepend( '<div class="' + _c.header + '"><a class="' + _c.btn + ' ' + _c.prev + '" href="#' + id + '" data-target="#' + id + '"></a><a class="' + _c.title + '">' + $a.text() + '</a></div>' );
									$t.addClass( _c.hasheader );
								}
							}
						}
					}
				);


			//	Add opened-classes to parents
			var $s = this.__findAddBack( $panels, '.' + _c.listview )
				.children( '.' + _c.selected )
				.removeClass( _c.selected )
				.last()
				.addClass( _c.selected );

			$s.add( $s.parentsUntil( '.' + _c.menu, 'li' ) )
				.filter( '.' + _c.vertical )
				.addClass( _c.opened )
				.end()
				.not( '.' + _c.vertical )
				.each(
					function()
					{
						$(this).parentsUntil( '.' + _c.menu, '.' + _c.panel )
							.not( '.' + _c.vertical )
							.first()
							.addClass( _c.opened )
							.parentsUntil( '.' + _c.menu, '.' + _c.panel )
							.not( '.' + _c.vertical )
							.first()
							.addClass( _c.opened )
							.addClass( _c.subopened );
					}
				);


			//	Add opened-classes to child
			$s.children( '.' + _c.panel )
				.not( '.' + _c.vertical )
				.addClass( _c.opened )
				.parentsUntil( '.' + _c.menu, '.' + _c.panel )
				.not( '.' + _c.vertical )
				.first()
				.addClass( _c.opened )
				.addClass( _c.subopened );


			//	Set current opened
			var $current = $allpanels.filter( '.' + _c.opened );
			if ( !$current.length )
			{
				$current = $curpanels.first();
			}
			$current
				.addClass( _c.opened )
				.last()
				.addClass( _c.current );


			//	Rearrange markup
			$curpanels
				.not( '.' + _c.vertical )
				.not( $current.last() )
				.addClass( _c.hidden )
				.end()
				.appendTo( this.$menu );
			
			return $curpanels;
		},

		_initAnchors: function()
		{
			var that = this;

			glbl.$body
				.on( _e.click + '-oncanvas',
					'a[href]',
					function( e )
					{
						var $t = $(this),
							fired 	= false,
							inMenu 	= that.$menu.find( $t ).length;

						//	Find behavior for addons
						for ( var a in $[ _PLUGIN_ ].addons )
						{
							if ( fired = $[ _PLUGIN_ ].addons[ a ].clickAnchor.call( that, $t, inMenu ) )
							{
								break;
							}
						}

						//	Open/Close panel
						if ( !fired && inMenu )
						{
							var _h = $t.attr( 'href' );

							if ( _h.length > 1 && _h.slice( 0, 1 ) == '#' )
							{
								var $h = $(_h, that.$menu);
								if ( $h.is( '.' + _c.panel ) )
								{
									fired = true;
									that[ $t.parent().hasClass( _c.vertical ) ? 'togglePanel' : 'openPanel' ]( $h );
								}
							}
						}

						if ( fired )
						{
							e.preventDefault();
						}


						//	All other anchors in lists
						if ( !fired && inMenu )
						{
							if ( $t.is( '.' + _c.listview + ' > li > a' )
								&& !$t.is( '[rel="external"]' ) 
								&& !$t.is( '[target="_blank"]' ) )
							{

								//	Set selected item
								if ( that.__valueOrFn( that.opts.onClick.setSelected, $t ) )
								{
									that.setSelected( $(e.target).parent() );
								}
	
								//	Prevent default / don't follow link. Default: false
								var preventDefault = that.__valueOrFn( that.opts.onClick.preventDefault, $t, _h.slice( 0, 1 ) == '#' );
								if ( preventDefault )
								{
									e.preventDefault();
								}
		
								//	Block UI. Default: false if preventDefault, true otherwise
								if ( that.__valueOrFn( that.opts.onClick.blockUI, $t, !preventDefault ) )
								{
									glbl.$html.addClass( _c.blocking );
								}

								//	Close menu. Default: true if preventDefault, false otherwise
								if ( that.__valueOrFn( that.opts.onClick.close, $t, preventDefault ) )
								{
									that.close();
								}
							}
						}
					}
				);
		},

		_initAddons: function()
		{
			//	Add add-ons to plugin
			for ( var a in $[ _PLUGIN_ ].addons )
			{
				$[ _PLUGIN_ ].addons[ a ].add.call( this );
				$[ _PLUGIN_ ].addons[ a ].add = function() {};
			}

			//	Setup adds-on for menu
			for ( var a in $[ _PLUGIN_ ].addons )
			{
				$[ _PLUGIN_ ].addons[ a ].setup.call( this );
			}
		},

		__api: function()
		{
			var that = this,
				api = {};

			$.each( this._api, 
				function( i )
				{
					var fn = this;
					api[ fn ] = function()
					{
						var re = that[ fn ].apply( that, arguments );
						return ( typeof re == 'undefined' ) ? api : re;
					}
				}
			);
			return api;
		},

		__valueOrFn: function( o, $e, d )
		{
			if ( typeof o == 'function' )
			{
				return o.call( $e[ 0 ] );
			}
			if ( typeof o == 'undefined' && typeof d != 'undefined' )
			{
				return d;
			}
			return o;
		},
		
		__refactorClass: function( $e, o, c )
		{
			return $e.filter( '.' + o ).removeClass( o ).addClass( _c[ c ] );
		},

		__findAddBack: function( $e, s )
		{
			return $e.find( s ).add( $e.filter( s ) );
		},
		
		__filterListItems: function( $i )
		{
			return $i
				.not( '.' + _c.divider )
				.not( '.' + _c.hidden );
		},
		
		__transitionend: function( $e, fn, duration )
		{
			var _ended = false,
				_fn = function()
				{
					if ( !_ended )
					{
						fn.call( $e[ 0 ] );
					}
					_ended = true;
				};
	
			$e.one( _e.transitionend, _fn );
			$e.one( _e.webkitTransitionEnd, _fn );
			setTimeout( _fn, duration * 1.1 );
		},
		
		__getUniqueId: function()
		{
			return _c.mm( $[ _PLUGIN_ ].uniqueId++ );
		}
	};


	/*
		jQuery plugin
	*/
	$.fn[ _PLUGIN_ ] = function( opts, conf )
	{
		//	First time plugin is fired
		initPlugin();

		//	Extend options
		opts = $.extend( true, {}, $[ _PLUGIN_ ].defaults, opts );
		conf = $.extend( true, {}, $[ _PLUGIN_ ].configuration, conf );

		return this.each(
			function()
			{
				var $menu = $(this);
				if ( $menu.data( _PLUGIN_ ) )
				{
					return;
				}
				var _menu = new $[ _PLUGIN_ ]( $menu, opts, conf );
				$menu.data( _PLUGIN_, _menu.__api() );
			}
		);
	};


	/*
		SUPPORT
	*/
	$[ _PLUGIN_ ].support = {
		touch: 'ontouchstart' in window || navigator.msMaxTouchPoints
	};


	//	Global variables
	var _c, _d, _e, glbl;

	function initPlugin()
	{
		if ( $[ _PLUGIN_ ].glbl )
		{
			return;
		}

		glbl = {
			$wndw : $(window),
			$html : $('html'),
			$body : $('body')
		};


		//	Classnames, Datanames, Eventnames
		_c = {};
		_d = {};
		_e = {};
		$.each( [ _c, _d, _e ],
			function( i, o )
			{
				o.add = function( c )
				{
					c = c.split( ' ' );
					for ( var d in c )
					{
						o[ c[ d ] ] = o.mm( c[ d ] );
					}
				};
			}
		);

		//	Classnames
		_c.mm = function( c ) { return 'mm-' + c; };
		_c.add( 'wrapper menu vertical panel nopanel current highest opened subopened header hasheader title btn prev next first last listview nolistview selected divider spacer hidden fullsubopen' );
		_c.umm = function( c )
		{
			if ( c.slice( 0, 3 ) == 'mm-' )
			{
				c = c.slice( 3 );
			}
			return c;
		};

		//	Datanames
		_d.mm = function( d ) { return 'mm-' + d; };
		_d.add( 'parent sub' );

		//	Eventnames
		_e.mm = function( e ) { return e + '.mm'; };
		_e.add( 'transitionend webkitTransitionEnd mousedown mouseup touchstart touchmove touchend click swipe keydown' );

		$[ _PLUGIN_ ]._c = _c;
		$[ _PLUGIN_ ]._d = _d;
		$[ _PLUGIN_ ]._e = _e;

		$[ _PLUGIN_ ].glbl = glbl;
	}


})( jQuery );
/*	
 * jQuery mmenu offCanvas addon
 * mmenu.frebsite.nl
 *
 * Copyright (c) Fred Heusschen
 */
/*	
 * jQuery mmenu offCanvas addon
 * mmenu.frebsite.nl
 *
 * Copyright (c) Fred Heusschen
 */


(function( $ ) {

	var _PLUGIN_ = 'mmenu',
		_ADDON_  = 'offCanvas';


	$[ _PLUGIN_ ].addons[ _ADDON_ ] = {

		//	setup: fired once per menu
		setup: function()
		{
			if ( !this.opts[ _ADDON_ ] )
			{
				return;
			}

			var that = this,
				opts = this.opts[ _ADDON_ ],
				conf = this.conf[ _ADDON_ ];

			glbl = $[ _PLUGIN_ ].glbl;


			//	Add methods to api
			this._api = $.merge( this._api, [ 'open', 'close', 'setPage' ] );


			//	Debug positioning
			if ( opts.position == 'top' || opts.position == 'bottom' )
			{
				opts.zposition = 'front';
			}


			//	Extend configuration
			if ( typeof conf.pageSelector != 'string' )
			{
				conf.pageSelector = '> ' + conf.pageNodetype;
			}


			glbl.$allMenus = ( glbl.$allMenus || $() ).add( this.$menu );


			//	Setup the menu
			this.vars.opened = false;
			
			var clsn = [ _c.offcanvas ];

			if ( opts.position != 'left' )
			{
				clsn.push( _c.mm( opts.position ) );
			}
			if ( opts.zposition != 'back' )
			{
				clsn.push( _c.mm( opts.zposition ) );
			}

			this.$menu
				.addClass( clsn.join( ' ' ) )
				.parent()
				.removeClass( _c.wrapper );


			//	Setup the page
			this.setPage( glbl.$page );


			//	Setup the UI blocker and the window
			this._initBlocker();
			this[ '_initWindow_' + _ADDON_ ]();


			//	Append to the body
			this.$menu[ conf.menuInjectMethod + 'To' ]( conf.menuWrapperSelector );
		},

		//	add: fired once per page load
		add: function()
		{
			_c = $[ _PLUGIN_ ]._c;
			_d = $[ _PLUGIN_ ]._d;
			_e = $[ _PLUGIN_ ]._e;

			_c.add( 'offcanvas slideout modal background opening blocker page' );
			_d.add( 'style' );
			_e.add( 'resize' );
		},

		//	clickAnchor: prevents default behavior when clicking an anchor
		clickAnchor: function( $a, inMenu )
		{
			if ( !this.opts[ _ADDON_ ] )
			{
				return false;
			}

			//	Open menu
			var id = this.$menu.attr( 'id' );
			if ( id && id.length )
			{
				if ( this.conf.clone )
				{
					id = _c.umm( id );
				}
				if ( $a.is( '[href="#' + id + '"]' ) )
				{
					this.open();
					return true;
				}
			}
			
			//	Close menu
			if ( !glbl.$page )
			{
				return;
			}
			var id = glbl.$page.attr( 'id' );
			if ( id && id.length )
			{
				if ( $a.is( '[href="#' + id + '"]' ) )
				{
					this.close();
					return true;
				}
			}

			return false;
		}
	};


	//	Default options and configuration
	$[ _PLUGIN_ ].defaults[ _ADDON_ ] = {
		position		: 'left',
		zposition		: 'back',
		modal			: false,
		moveBackground	: true
	};
	$[ _PLUGIN_ ].configuration[ _ADDON_ ] = {
		pageNodetype		: 'div',
		pageSelector		: null,
		menuWrapperSelector	: 'body',
		menuInjectMethod	: 'prepend'
	};


	//	Methods
	$[ _PLUGIN_ ].prototype.open = function()
	{alert("opening!");
		if ( this.vars.opened )
		{
			return;
		}

		var that = this;

		this._openSetup();

		//	Without the timeout, the animation won't work because the element had display: none;
		setTimeout(
			function()
			{
				that._openFinish();
			}, this.conf.openingInterval
		);
		this.trigger( 'open' );
	};

	$[ _PLUGIN_ ].prototype._openSetup = function()
	{
		var that = this;

		//	Close other menus
		this.closeAllOthers();

		//	Store style and position
		glbl.$page.data( _d.style, glbl.$page.attr( 'style' ) || '' );

		//	Trigger window-resize to measure height
		glbl.$wndw.trigger( _e.resize + '-offcanvas', [ true ] );

		var clsn = [ _c.opened ];

		//	Add options
		if ( this.opts[ _ADDON_ ].modal )
		{
			clsn.push( _c.modal );
		}
		if ( this.opts[ _ADDON_ ].moveBackground )
		{
			clsn.push( _c.background );
		}
		if ( this.opts[ _ADDON_ ].position != 'left' )
		{
			clsn.push( _c.mm( this.opts[ _ADDON_ ].position ) );
		}
		if ( this.opts[ _ADDON_ ].zposition != 'back' )
		{
			clsn.push( _c.mm( this.opts[ _ADDON_ ].zposition ) );
		}
		if ( this.opts.extensions )
		{
			clsn.push( this.opts.extensions );
		}
		glbl.$html.addClass( clsn.join( ' ' ) );

		//	Open
		setTimeout(function(){
            that.vars.opened = true;
        },this.conf.openingInterval);

		this.$menu.addClass( _c.current + ' ' + _c.opened );
	};

	$[ _PLUGIN_ ].prototype._openFinish = function()
	{
		var that = this;

		//	Callback
		this.__transitionend( glbl.$page,
			function()
			{
				that.trigger( 'opened' );
			}, this.conf.transitionDuration
		);

		//	Opening
		glbl.$html.addClass( _c.opening );
		this.trigger( 'opening' );
	};

	$[ _PLUGIN_ ].prototype.close = function()
	{
		if ( !this.vars.opened )
		{
			return;
		}

		var that = this;

		//	Callback
		this.__transitionend( glbl.$page,
			function()
			{
				that.$menu
					.removeClass( _c.current )
					.removeClass( _c.opened );

				glbl.$html
					.removeClass( _c.opened )
					.removeClass( _c.modal )
					.removeClass( _c.background )
					.removeClass( _c.mm( that.opts[ _ADDON_ ].position ) )
					.removeClass( _c.mm( that.opts[ _ADDON_ ].zposition ) );

				if ( that.opts.extensions )
				{
					glbl.$html.removeClass( that.opts.extensions );
				}

				//	Restore style and position
				glbl.$page.attr( 'style', glbl.$page.data( _d.style ) );

				that.vars.opened = false;
				that.trigger( 'closed' );

			}, this.conf.transitionDuration
		);

		//	Closing
		glbl.$html.removeClass( _c.opening );
		this.trigger( 'close' );
		this.trigger( 'closing' );
	};

	$[ _PLUGIN_ ].prototype.closeAllOthers = function()
	{
		glbl.$allMenus
			.not( this.$menu )
			.each(
				function()
				{
					var api = $(this).data( _PLUGIN_ );
					if ( api && api.close )
					{
						api.close();
					}
				}
			);
	}

	$[ _PLUGIN_ ].prototype.setPage = function( $page )
	{
		if ( !$page || !$page.length )
		{
			$page = $(this.conf[ _ADDON_ ].pageSelector, glbl.$body);
			if ( $page.length > 1 )
			{
				$page = $page.wrapAll( '<' + this.conf[ _ADDON_ ].pageNodetype + ' />' ).parent();
			}
		}

		$page.attr( 'id', $page.attr( 'id' ) || this.__getUniqueId() );
		$page.addClass( _c.page + ' ' + _c.slideout );
		glbl.$page = $page;

		this.trigger( 'setPage', $page );
	};

	$[ _PLUGIN_ ].prototype[ '_initWindow_' + _ADDON_ ] = function()
	{
		//	Prevent tabbing
		glbl.$wndw
			.off( _e.keydown + '-offcanvas' )
			.on( _e.keydown + '-offcanvas',
				function( e )
				{
					if ( glbl.$html.hasClass( _c.opened ) )
					{
						if ( e.keyCode == 9 )
						{
							e.preventDefault();
							return false;
						}
					}
				}
			);

		//	Set page min-height to window height
		var _h = 0;
		glbl.$wndw
			.off( _e.resize + '-offcanvas' )
			.on( _e.resize + '-offcanvas',
				function( e, force )
				{
					if ( force || glbl.$html.hasClass( _c.opened ) )
					{
						var nh = glbl.$wndw.height();
						if ( force || nh != _h )
						{
							_h = nh;
							glbl.$page.css( 'minHeight', nh );
						}
					}
				}
			);
	};

	$[ _PLUGIN_ ].prototype._initBlocker = function()
	{
		var that = this;

		if ( !glbl.$blck )
		{
			glbl.$blck = $( '<div id="' + _c.blocker + '" class="' + _c.slideout + '" />' );
			alert("glbl.$blck: " + glbl.$blck[0].id);
		}

		glbl.$blck
			.appendTo( glbl.$body )
			.off( _e.touchstart + '-offcanvas ' + _e.touchmove + '-offcanvas' )
			.on( _e.touchstart + '-offcanvas ' + _e.touchmove + '-offcanvas',
				function( e )
				{
				alert("touchstart");
					e.preventDefault();
					e.stopPropagation();
					glbl.$blck.trigger( _e.mousedown + '-offcanvas' );
				}
			)
			.off( _e.touchstart + '-offcanvas' )
			.on( _e.touchstart + '-offcanvas',
					function( e )
					{
				alert("touchstart v2");
				e.preventDefault();
				e.stopPropagation();
				glbl.$blck.trigger( _e.mousedown + '-offcanvas' );
					}
			)
			.off( _e.touchmove + '-offcanvas' )
			.on( _e.touchmove + '-offcanvas',
					function( e )
					{
				alert("touchmove v2");
				e.preventDefault();
				e.stopPropagation();
				glbl.$blck.trigger( _e.mousedown + '-offcanvas' );
					}
			)
			.off( _e.swipe + '-offcanvas ' + _e.swipe + '-offcanvas' )
			.on( _e.swipe + '-offcanvas ' + _e.swipe + '-offcanvas',
					function( e )
					{
				alert("swipe");
				e.preventDefault();
				e.stopPropagation();
				glbl.$blck.trigger( _e.mousedown + '-offcanvas' );
					}
			)
			.off( _e.mousedown + '-offcanvas' )
			.on( _e.mousedown + '-offcanvas',
				function( e )
				{
					e.preventDefault();
					if ( !glbl.$html.hasClass( _c.modal ) )
					{
						alert("mousedown");
						that.closeAllOthers();
						that.close();
					}
				}
			);
	};


	var _c, _d, _e, glbl;

})( jQuery );
/*	
 * jQuery mmenu autoHeight addon
 * mmenu.frebsite.nl
 *
 * Copyright (c) Fred Heusschen
 */
!function(t){var e="mmenu",i="autoHeight";t[e].addons[i]={setup:function(){if(this.opts.offCanvas){switch(this.opts.offCanvas.position){case"left":case"right":return}var n=this,o=this.opts[i];if(this.conf[i],h=t[e].glbl,"boolean"==typeof o&&o&&(o={height:"auto"}),"object"!=typeof o&&(o={}),o=this.opts[i]=t.extend(!0,{},t[e].defaults[i],o),"auto"==o.height){this.$menu.addClass(s.autoheight);var u=function(t){var e=this.$menu.children("."+s.current);_top=parseInt(e.css("top"),10)||0,_bot=parseInt(e.css("bottom"),10)||0,this.$menu.addClass(s.measureheight),t=t||this.$menu.children("."+s.current),t.is("."+s.vertical)&&(t=t.parents("."+s.panel).not("."+s.vertical).first()),this.$menu.height(t.outerHeight()+_top+_bot).removeClass(s.measureheight)};this.bind("update",u),this.bind("openPanel",u),this.bind("closePanel",u),this.bind("open",u),h.$wndw.off(a.resize+"-autoheight").on(a.resize+"-autoheight",function(){u.call(n)})}}},add:function(){s=t[e]._c,n=t[e]._d,a=t[e]._e,s.add("autoheight measureheight"),a.add("resize")},clickAnchor:function(){}},t[e].defaults[i]={height:"default"};var s,n,a,h}(jQuery);
/*	
 * jQuery mmenu backButton addon
 * mmenu.frebsite.nl
 *
 * Copyright (c) Fred Heusschen
 */
!function(o){var t="mmenu",n="backButton";o[t].addons[n]={setup:function(){if(this.opts.offCanvas){var i=this,e=this.opts[n];if(this.conf[n],a=o[t].glbl,"boolean"==typeof e&&(e={close:e}),"object"!=typeof e&&(e={}),e=o.extend(!0,{},o[t].defaults[n],e),e.close){var c="#"+i.$menu.attr("id");this.bind("opened",function(){location.hash!=c&&history.pushState(null,document.title,c)}),o(window).on("popstate",function(o){a.$html.hasClass(s.opened)?(o.stopPropagation(),i.close()):location.hash==c&&(o.stopPropagation(),i.open())})}}},add:function(){return window.history&&window.history.pushState?(s=o[t]._c,i=o[t]._d,e=o[t]._e,void 0):(o[t].addons[n].setup=function(){},void 0)},clickAnchor:function(){}},o[t].defaults[n]={close:!1};var s,i,e,a}(jQuery);
/*	
 * jQuery mmenu buttonbars addon
 * mmenu.frebsite.nl
 *
 * Copyright (c) Fred Heusschen
 */
!function(t){var n="mmenu",i="buttonbars";t[n].addons[i]={setup:function(){this.opts[i],this.conf[i],s=t[n].glbl,this.bind("init",function(n){this.__refactorClass(t("div",n),this.conf.classNames[i].buttonbar,"buttonbar"),t("."+a.buttonbar,n).each(function(){var n=t(this),i=n.children().not("input"),o=n.children().filter("input");n.addClass(a.buttonbar+"-"+i.length),o.each(function(){var n=t(this),a=i.filter('label[for="'+n.attr("id")+'"]');a.length&&n.insertBefore(a)})})})},add:function(){a=t[n]._c,o=t[n]._d,r=t[n]._e,a.add("buttonbar")},clickAnchor:function(){}},t[n].configuration.classNames[i]={buttonbar:"Buttonbar"};var a,o,r,s}(jQuery);
/*	
 * jQuery mmenu counters addon
 * mmenu.frebsite.nl
 *
 * Copyright (c) Fred Heusschen
 */
!function(t){var n="mmenu",e="counters";t[n].addons[e]={setup:function(){var c=this,o=this.opts[e];this.conf[e],s=t[n].glbl,"boolean"==typeof o&&(o={add:o,update:o}),"object"!=typeof o&&(o={}),o=this.opts[e]=t.extend(!0,{},t[n].defaults[e],o),this.bind("init",function(n){this.__refactorClass(t("em",n),this.conf.classNames[e].counter,"counter")}),o.add&&this.bind("init",function(n){n.each(function(){var n=t(this).data(a.parent);n&&(n.children("em."+i.counter).length||n.prepend(t('<em class="'+i.counter+'" />')))})}),o.update&&this.bind("update",function(){this.$menu.find("."+i.panel).each(function(){var n=t(this),e=n.data(a.parent);if(e){var s=e.children("em."+i.counter);s.length&&(n=n.children("."+i.listview),n.length&&s.html(c.__filterListItems(n.children()).length))}})})},add:function(){i=t[n]._c,a=t[n]._d,c=t[n]._e,i.add("counter search noresultsmsg")},clickAnchor:function(){}},t[n].defaults[e]={add:!1,update:!1},t[n].configuration.classNames[e]={counter:"Counter"};var i,a,c,s}(jQuery);
/*	
 * jQuery mmenu dividers addon
 * mmenu.frebsite.nl
 *
 * Copyright (c) Fred Heusschen
 */
!function(i){var e="mmenu",s="dividers";i[e].addons[s]={setup:function(){var n=this,a=this.opts[s];if(this.conf[s],l=i[e].glbl,"boolean"==typeof a&&(a={add:a,fixed:a}),"object"!=typeof a&&(a={}),a=this.opts[s]=i.extend(!0,{},i[e].defaults[s],a),this.bind("init",function(){this.__refactorClass(i("li",this.$menu),this.conf.classNames[s].collapsed,"collapsed")}),a.add&&this.bind("init",function(e){switch(a.addTo){case"panels":var s=e;break;default:var s=i(a.addTo,this.$menu).filter("."+d.panel)}i("."+d.divider,s).remove(),s.find("."+d.listview).not("."+d.vertical).each(function(){var e="";n.__filterListItems(i(this).children()).each(function(){var s=i.trim(i(this).children("a, span").text()).slice(0,1).toLowerCase();s!=e&&s.length&&(e=s,i('<li class="'+d.divider+'">'+s+"</li>").insertBefore(this))})})}),a.collapse&&this.bind("init",function(e){i("."+d.divider,e).each(function(){var e=i(this),s=e.nextUntil("."+d.divider,"."+d.collapsed);s.length&&(e.children("."+d.subopen).length||(e.wrapInner("<span />"),e.prepend('<a href="#" class="'+d.subopen+" "+d.fullsubopen+'" />')))})}),a.fixed){var o=function(e){e=e||this.$menu.children("."+d.current);var s=e.find("."+d.divider).not("."+d.hidden);if(s.length){this.$menu.addClass(d.hasdividers);var n=e.scrollTop()||0,t="";e.is(":visible")&&e.find("."+d.divider).not("."+d.hidden).each(function(){i(this).position().top+n<n+1&&(t=i(this).text())}),this.$fixeddivider.text(t)}else this.$menu.removeClass(d.hasdividers)};this.$fixeddivider=i('<ul class="'+d.listview+" "+d.fixeddivider+'"><li class="'+d.divider+'"></li></ul>').prependTo(this.$menu).children(),this.bind("openPanel",o),this.bind("init",function(e){e.off(t.scroll+"-dividers "+t.touchmove+"-dividers").on(t.scroll+"-dividers "+t.touchmove+"-dividers",function(){o.call(n,i(this))})})}},add:function(){d=i[e]._c,n=i[e]._d,t=i[e]._e,d.add("collapsed uncollapsed fixeddivider hasdividers"),t.add("scroll")},clickAnchor:function(i,e){if(this.opts[s].collapse&&e){var n=i.parent();if(n.is("."+d.divider)){var t=n.nextUntil("."+d.divider,"."+d.collapsed);return n.toggleClass(d.opened),t[n.hasClass(d.opened)?"addClass":"removeClass"](d.uncollapsed),!0}}return!1}},i[e].defaults[s]={add:!1,addTo:"panels",fixed:!1,collapse:!1},i[e].configuration.classNames[s]={collapsed:"Collapsed"};var d,n,t,l}(jQuery);
/*	
 * jQuery mmenu dragOpen addon
 * mmenu.frebsite.nl
 *
 * Copyright (c) Fred Heusschen
 */
!function(e){function t(e,t,n){return t>e&&(e=t),e>n&&(e=n),e}var n="mmenu",o="dragOpen";e[n].addons[o]={setup:function(){if(this.opts.offCanvas){var i=this,a=this.opts[o],p=this.conf[o];if(r=e[n].glbl,"boolean"==typeof a&&(a={open:a}),"object"!=typeof a&&(a={}),a=this.opts[o]=e.extend(!0,{},e[n].defaults[o],a),a.open){var d,f,c,u,h,l={},m=0,g=!1,v=!1,w=0,_=0;switch(this.opts.offCanvas.position){case"left":case"right":l.events="panleft panright",l.typeLower="x",l.typeUpper="X",v="width";break;case"top":case"bottom":l.events="panup pandown",l.typeLower="y",l.typeUpper="Y",v="height"}switch(this.opts.offCanvas.position){case"right":case"bottom":l.negative=!0,u=function(e){e>=r.$wndw[v]()-a.maxStartPos&&(m=1)};break;default:l.negative=!1,u=function(e){e<=a.maxStartPos&&(m=1)}}switch(this.opts.offCanvas.position){case"left":l.open_dir="right",l.close_dir="left";break;case"right":l.open_dir="left",l.close_dir="right";break;case"top":l.open_dir="down",l.close_dir="up";break;case"bottom":l.open_dir="up",l.close_dir="down"}switch(this.opts.offCanvas.zposition){case"front":h=function(){return this.$menu};break;default:h=function(){return e("."+s.slideout)}}var b=this.__valueOrFn(a.pageNode,this.$menu,r.$page);"string"==typeof b&&(b=e(b));var y=new Hammer(b[0],a.vendors.hammer);y.on("panstart",function(e){u(e.center[l.typeLower]),r.$slideOutNodes=h(),g=l.open_dir}).on(l.events+" panend",function(e){m>0&&e.preventDefault()}).on(l.events,function(e){if(d=e["delta"+l.typeUpper],l.negative&&(d=-d),d!=w&&(g=d>=w?l.open_dir:l.close_dir),w=d,w>a.threshold&&1==m){if(r.$html.hasClass(s.opened))return;m=2,i._openSetup(),i.trigger("opening"),r.$html.addClass(s.dragging),_=t(r.$wndw[v]()*p[v].perc,p[v].min,p[v].max)}2==m&&(f=t(w,10,_)-("front"==i.opts.offCanvas.zposition?_:0),l.negative&&(f=-f),c="translate"+l.typeUpper+"("+f+"px )",r.$slideOutNodes.css({"-webkit-transform":"-webkit-"+c,transform:c}))}).on("panend",function(){2==m&&(r.$html.removeClass(s.dragging),r.$slideOutNodes.css("transform",""),i[g==l.open_dir?"_openFinish":"close"]()),m=0})}}},add:function(){return"function"!=typeof Hammer||Hammer.VERSION<2?(e[n].addons[o].setup=function(){},void 0):(s=e[n]._c,i=e[n]._d,a=e[n]._e,s.add("dragging"),void 0)},clickAnchor:function(){}},e[n].defaults[o]={open:!1,maxStartPos:100,threshold:50,vendors:{hammer:{}}},e[n].configuration[o]={width:{perc:.8,min:140,max:440},height:{perc:.8,min:140,max:880}};var s,i,a,r}(jQuery);
/*	
 * jQuery mmenu fixedElements addon
 * mmenu.frebsite.nl
 *
 * Copyright (c) Fred Heusschen
 */
!function(i){var s="mmenu",a="fixedElements";i[s].addons[a]={setup:function(){if(this.opts.offCanvas){this.opts[a],this.conf[a],t=i[s].glbl;var d=function(i){var s=this.conf.classNames[a].fixed;this.__refactorClass(i.find("."+s),s,"fixed").appendTo(t.$body).addClass(n.slideout)};d.call(this,t.$page),this.bind("setPage",d)}},add:function(){n=i[s]._c,d=i[s]._d,e=i[s]._e,n.add("fixed")},clickAnchor:function(){}},i[s].configuration.classNames[a]={fixed:"Fixed"};var n,d,e,t}(jQuery);
/*	
 * jQuery mmenu footer addon
 * mmenu.frebsite.nl
 *
 * Copyright (c) Fred Heusschen
 */
!function(t){var e="mmenu",o="footer";t[e].addons[o]={setup:function(){var i=this.opts[o];if(this.conf[o],a=t[e].glbl,"boolean"==typeof i&&(i={add:i,update:i}),"object"!=typeof i&&(i={}),i=this.opts[o]=t.extend(!0,{},t[e].defaults[o],i),i.add){var s=i.content?i.content:i.title;t('<div class="'+n.footer+'" />').appendTo(this.$menu).append(s),this.$menu.addClass(n.hasfooter)}if(this.$footer=this.$menu.children("."+n.footer),i.update&&this.$footer&&this.$footer.length){var d=function(e){e=e||this.$menu.children("."+n.current);var s=t("."+this.conf.classNames[o].panelFooter,e).html()||i.title;this.$footer[s?"removeClass":"addClass"](n.hidden),this.$footer.html(s)};this.bind("openPanel",d),this.bind("init",function(){d.call(this,this.$menu.children("."+n.current))})}},add:function(){n=t[e]._c,i=t[e]._d,s=t[e]._e,n.add("footer hasfooter")},clickAnchor:function(){}},t[e].defaults[o]={add:!1,content:!1,title:"",update:!1},t[e].configuration.classNames[o]={panelFooter:"Footer"};var n,i,s,a}(jQuery);
/*	
 * jQuery mmenu header addon
 * mmenu.frebsite.nl
 *
 * Copyright (c) Fred Heusschen
 */
!function(e){var t="mmenu",a="header";e[t].addons[a]={setup:function(){var i=this.opts[a];if(this.conf[a],s=e[t].glbl,"boolean"==typeof i&&(i={add:i,update:i}),"object"!=typeof i&&(i={}),"undefined"==typeof i.content&&(i.content=["prev","title","next"]),i=this.opts[a]=e.extend(!0,{},e[t].defaults[a],i),i.add){if(i.content instanceof Array){for(var d=e("<div />"),h=0,l=i.content.length;l>h;h++)switch(i.content[h]){case"prev":case"next":case"close":d.append('<a class="'+n[i.content[h]]+" "+n.btn+'" href="#"></a>');break;case"title":d.append('<a class="'+n.title+'"></a>');break;default:d.append(i.content[h])}d=d.html()}else var d=i.content;e('<div class="'+n.header+'" />').prependTo(this.$menu).append(d),this.$menu.addClass(n.hasheader),this.bind("init",function(e){e.removeClass(n.hasheader)})}if(this.$header=this.$menu.children("."+n.header),i.update&&this.$header&&this.$header.length){var c=this.$header.find("."+n.title),o=this.$header.find("."+n.prev),f=this.$header.find("."+n.next),p=this.$header.find("."+n.close),u=function(e){e=e||this.$menu.children("."+n.current);var t=e.find("."+this.conf.classNames[a].panelHeader),s=e.find("."+this.conf.classNames[a].panelPrev),d=e.find("."+this.conf.classNames[a].panelNext),h=e.data(r.parent),l=t.html(),p=s.attr("href"),u=d.attr("href"),v=!1,m=s.html(),$=d.html();switch(l||(l=e.children("."+n.header).children("."+n.title).html()),l||(l=i.title),p||(p=e.children("."+n.header).children("."+n.prev).attr("href")),i.titleLink){case"anchor":var v=h?h.children("a").not("."+n.next).attr("href"):!1;break;case"panel":var v=p}c[v?"attr":"removeAttr"]("href",v),c[l?"removeClass":"addClass"](n.hidden),c.html(l),o[p?"attr":"removeAttr"]("href",p),o[p||m?"removeClass":"addClass"](n.hidden),o.html(m),f[u?"attr":"removeAttr"]("href",u),f[u||$?"removeClass":"addClass"](n.hidden),f.html($)};if(this.bind("openPanel",u),this.bind("init",function(){u.call(this,this.$menu.children("."+n.current))}),this.opts.offCanvas){var v=function(e){p.attr("href","#"+e.attr("id"))};v.call(this,s.$page),this.bind("setPage",v)}}},add:function(){n=e[t]._c,r=e[t]._d,i=e[t]._e,n.add("close")},clickAnchor:function(){}},e[t].defaults[a]={add:!1,title:"Menu",titleLink:"panel",update:!1},e[t].configuration.classNames[a]={panelHeader:"Header",panelNext:"Next",panelPrev:"Prev"};var n,r,i,s}(jQuery);
/*	
 * jQuery mmenu searchfield addon
 * mmenu.frebsite.nl
 *
 * Copyright (c) Fred Heusschen
 */
!function(e){function s(e){switch(e){case 9:case 16:case 17:case 18:case 37:case 38:case 39:case 40:return!0}return!1}var a="mmenu",n="searchfield";e[a].addons[n]={setup:function(){var o=this,d=this.opts[n],c=this.conf[n];r=e[a].glbl,"boolean"==typeof d&&(d={add:d,search:d}),"object"!=typeof d&&(d={}),d=this.opts[n]=e.extend(!0,{},e[a].defaults[n],d),this.bind("init",function(a){if(d.add){switch(d.addTo){case"menu":var n=this.$menu;break;case"panels":var n=a;break;default:var n=e(d.addTo,this.$menu).filter("."+t.panel)}n.each(function(){var s=e(this);if(!s.is("."+t.panel)||!s.is("."+t.vertical)){if(!s.children("."+t.search).length){var a=c.form?"form":"div",n=e("<"+a+' class="'+t.search+'" />');if(c.form&&"object"==typeof c.form)for(var l in c.form)n.attr(l,c.form[l]);n.append('<input placeholder="'+d.placeholder+'" type="text" autocomplete="off" />').prependTo(s),s.addClass(t.hassearch)}if(d.noResults&&(s.is("."+t.menu)&&(s=s.children("."+t.panel).first()),!s.children("."+t.noresultsmsg).length)){var i=s.children("."+t.listview);e('<div class="'+t.noresultsmsg+'" />').append(d.noResults)[i.length?"insertBefore":"prependTo"](i.length?i:s)}}})}d.search&&e("."+t.search,this.$menu).each(function(){var a=e(this);if("menu"==d.addTo)var n=e("."+t.panel,o.$menu),r=o.$menu;else var n=a.closest("."+t.panel),r=n;var c=a.children("input"),h=o.__findAddBack(n,"."+t.listview).children("li"),u=h.filter("."+t.divider),f=o.__filterListItems(h),p="> a",v=p+", > span",m=function(){var s=c.val().toLowerCase();n.scrollTop(0),f.add(u).addClass(t.hidden).find("."+t.fullsubopensearch).removeClass(t.fullsubopen).removeClass(t.fullsubopensearch),f.each(function(){var a=e(this),n=p;(d.showTextItems||d.showSubPanels&&a.find("."+t.next))&&(n=v),e(n,a).text().toLowerCase().indexOf(s)>-1&&a.add(a.prevAll("."+t.divider).first()).removeClass(t.hidden)}),d.showSubPanels&&n.each(function(){var s=e(this);o.__filterListItems(s.find("."+t.listview).children()).each(function(){var s=e(this),a=s.data(l.sub);s.removeClass(t.nosubresults),a&&a.find("."+t.listview).children().removeClass(t.hidden)})}),e(n.get().reverse()).each(function(s){var a=e(this),n=a.data(l.parent);n&&(o.__filterListItems(a.find("."+t.listview).children()).length?(n.hasClass(t.hidden)&&n.children("."+t.next).not("."+t.fullsubopen).addClass(t.fullsubopen).addClass(t.fullsubopensearch),n.removeClass(t.hidden).removeClass(t.nosubresults).prevAll("."+t.divider).first().removeClass(t.hidden)):"menu"==d.addTo&&(a.hasClass(t.opened)&&setTimeout(function(){o.openPanel(n.closest("."+t.panel))},1.5*(s+1)*o.conf.openingInterval),n.addClass(t.nosubresults)))}),r[f.not("."+t.hidden).length?"removeClass":"addClass"](t.noresults),this.update()};c.off(i.keyup+"-searchfield "+i.change+"-searchfield").on(i.keyup+"-searchfield",function(e){s(e.keyCode)||m.call(o)}).on(i.change+"-searchfield",function(){m.call(o)})})})},add:function(){t=e[a]._c,l=e[a]._d,i=e[a]._e,t.add("search hassearch noresultsmsg noresults nosubresults fullsubopensearch"),i.add("change keyup")},clickAnchor:function(){}},e[a].defaults[n]={add:!1,addTo:"menu",search:!1,placeholder:"Search",noResults:"No results found.",showTextItems:!1,showSubPanels:!0},e[a].configuration[n]={form:!1};var t,l,i,r}(jQuery);
/*	
 * jQuery mmenu sectionIndexer addon
 * mmenu.frebsite.nl
 *
 * Copyright (c) Fred Heusschen
 */
!function(e){var a="mmenu",r="sectionIndexer";e[a].addons[r]={setup:function(){var n=this,s=this.opts[r];this.conf[r],d=e[a].glbl,"boolean"==typeof s&&(s={add:s}),"object"!=typeof s&&(s={}),s=this.opts[r]=e.extend(!0,{},e[a].defaults[r],s),this.bind("init",function(a){if(s.add){switch(s.addTo){case"panels":var r=a;break;default:var r=e(s.addTo,this.$menu).filter("."+i.panel)}r.find("."+i.divider).closest("."+i.panel).addClass(i.hasindexer)}if(!this.$indexer&&this.$menu.children("."+i.hasindexer).length){this.$indexer=e('<div class="'+i.indexer+'" />').prependTo(this.$menu).append('<a href="#a">a</a><a href="#b">b</a><a href="#c">c</a><a href="#d">d</a><a href="#e">e</a><a href="#f">f</a><a href="#g">g</a><a href="#h">h</a><a href="#i">i</a><a href="#j">j</a><a href="#k">k</a><a href="#l">l</a><a href="#m">m</a><a href="#n">n</a><a href="#o">o</a><a href="#p">p</a><a href="#q">q</a><a href="#r">r</a><a href="#s">s</a><a href="#t">t</a><a href="#u">u</a><a href="#v">v</a><a href="#w">w</a><a href="#x">x</a><a href="#y">y</a><a href="#z">z</a><a href="##">#</a>'),this.$indexer.children().on(h.mouseover+"-searchfield "+i.touchmove+"-searchfield",function(){var a=e(this).attr("href").slice(1),r=n.$menu.children("."+i.current),h=r.find("."+i.listview),d=!1,s=r.scrollTop(),t=h.position().top+parseInt(h.css("margin-top"),10)+parseInt(h.css("padding-top"),10)+s;r.scrollTop(0),h.children("."+i.divider).not("."+i.hidden).each(function(){d===!1&&a==e(this).text().slice(0,1).toLowerCase()&&(d=e(this).position().top+t)}),r.scrollTop(d!==!1?d:s)});var d=function(e){n.$menu[(e.hasClass(i.hasindexer)?"add":"remove")+"Class"](i.hasindexer)};this.bind("openPanel",d),d.call(this,this.$menu.children("."+i.current))}})},add:function(){i=e[a]._c,n=e[a]._d,h=e[a]._e,i.add("indexer hasindexer"),h.add("mouseover touchmove")},clickAnchor:function(e){return e.parent().is("."+i.indexer)?!0:void 0}},e[a].defaults[r]={add:!1,addTo:"panels"};var i,n,h,d}(jQuery);
/*	
 * jQuery mmenu toggles addon
 * mmenu.frebsite.nl
 *
 * Copyright (c) Fred Heusschen
 */
!function(t){var e="mmenu",c="toggles";t[e].addons[c]={setup:function(){var n=this;this.opts[c],this.conf[c],l=t[e].glbl,this.bind("init",function(e){this.__refactorClass(t("input",e),this.conf.classNames[c].toggle,"toggle"),this.__refactorClass(t("input",e),this.conf.classNames[c].check,"check"),t("input."+s.toggle+", input."+s.check,e).each(function(){var e=t(this),c=e.closest("li"),i=e.hasClass(s.toggle)?"toggle":"check",l=e.attr("id")||n.__getUniqueId();c.children('label[for="'+l+'"]').length||(e.attr("id",l),c.prepend(e),t('<label for="'+l+'" class="'+s[i]+'"></label>').insertBefore(c.children("a, span").last()))})})},add:function(){s=t[e]._c,n=t[e]._d,i=t[e]._e,s.add("toggle check")},clickAnchor:function(){}},t[e].configuration.classNames[c]={toggle:"Toggle",check:"Check"};var s,n,i,l}(jQuery);