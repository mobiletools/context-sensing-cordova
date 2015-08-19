if (!window.CustomEvent) {
	var CustomEvent;

	CustomEvent = function(event, params) {
		var evt;
		params = params || {
			bubbles : false,
			cancelable : false,
			detail : undefined
		};
		evt = document.createEvent("CustomEvent");
		evt.initCustomEvent(event, params.bubbles, params.cancelable,
				params.detail);
		return evt;
	};

	CustomEvent.prototype = window.Event.prototype;

	window.CustomEvent = CustomEvent;
}

function triggerEvent(evt, target) {
	target = target || document;
	var event = new CustomEvent(evt, {
		bubbles : true,
		cancelable : true
	});
	target.dispatchEvent(event);

}

function logMsg(msg, $el, printTime) {
	if ($el) {
		$el.show();
		if (printTime) { msg = '<div class="msg-time">Last Updated: ' + new Date().toLocaleString() + "</div>" + msg; }
		$el.html(msg);
	} else {
		$("#logger").html(msg)
	}
}

function dump(data) {
	var result = "";

	if (data instanceof Array) {
		for (var i = 0; i < data.length; i++) {
			result += dump(data[i]);
		}
	} else if (data instanceof Object) {
		for ( var field in data) {
			result += "<div>" + field + ": " + dump(data[field]) + "</div>";
		}
	} else {
		result += data;
	}

	return result;
}

var app = {
	// Application Constructor
	initialize : function() {
		this.bindEvents();
	},
	// Bind Event Listeners
	//
	// Bind any events that are required on startup. Common events are:
	// 'load', 'deviceready', 'offline', and 'online'.
	bindEvents : function() {
		document.addEventListener('deviceready', this.onDeviceReady, false);
		document.addEventListener('contextready', $.proxy(this.onContextReady,
				this), false);
	},
	// deviceready Event Handler
	//
	// The scope of 'this' is the event. In order to call the 'receivedEvent'
	// function, we must explicitly call 'app.receivedEvent(...);'
	onDeviceReady : function() {
		intel.context.start(function() {
			triggerEvent("contextready");
		}, function(res) {
			alert("Error starting daemon " + res);
		})
	},

	supportedProviders : [ "LOCATION", "TERMINAL_CONTEXT",
			"ACTIVITY_RECOGNITION", "INSTANT_ACTIVITY", "AUDIO", "PEDOMETER",
			"BATTERY", "NETWORK", "DEVICE_POSITION", "TAPPING", "SHAKING",
			"GESTURE_FLICK", "GESTURE_EAR_TOUCH" ],

	onContextReady : function() {
		document.getElementById("daemon").style.display = "none";
		document.getElementById("sensors").style.display = "block";

		/*
		 * Render each providers' dom element, and listen for checkbox events to
		 * enable/disable provider
		 */
		for (var i = 0; i < this.supportedProviders.length; i++) {
			var provider = this.supportedProviders[i];
			createProviderView(provider);
		}
	},
	_enableSensing : function(type, success, err) {
		intel.context.enableSensing(type, success, err);
	},
	disableSensing : function(type, success, error) {
		intel.context.disableSensing(type, success, error);
	},
	_getItem : function(type, success, err) {
		intel.context.getItem(type, success, err);
	},
	stop : function() {
		intel.context.stop();
	},
};

var bindProviderEvents = function($container, contextType) {
	/*
	 * Events for enabling/disabling providers
	 */
	var $checkbox = $container.find('.sensing-toggle');
	var $msgBox = $container.find('.provider-msg-box')
	$checkbox.on('change', function() {
		/*
		 * Enable the sensing provider when the checkbox is checked, Disable the
		 * sensing provider when the checkbox is unchecked.
		 */
		if ($checkbox.is(':checked')) {
			app._enableSensing(contextType, function(data) {
				logMsg(dump(data), $msgBox, true)
			}, function(data) {
				logMsg("<div>" + dump(data) + "</div>", $msgBox)
			});
		} else {
			app.disableSensing(contextType, function(data) {
				$msgBox.empty().hide();
			}, function(data) {
				logMsg("<div>" + dump(data) + "</div>", $msgBox)
			});
		}
	});

	/*
	 * Events for /getitem
	 */
	var $getItemBtn = $container.find('.get-item');
	$getItemBtn.on("click", function() {
		app._getItem(contextType, function(data) {
			logMsg(dump(data), $msgBox, true)
		}, function(data) {
			logMsg("<div>" + dump(data) + "</div>", $msgBox)
		});
	});
}

var renderProviderView = function(type) {
	return $(
		'<div class="provider-container">' +
			'<button class="btn get-item">Get Item</button>' +
			'<label><input class="sensing-toggle" type="checkbox"> ' + type + '</label>' +
			'<div class="provider-msg-box"></div>' +
		'</div>'
	);
}

var createProviderView = function(type) {
	$el = renderProviderView(type);
	bindProviderEvents($el, type);
	$("#sensors").append($el);
}

app.initialize();