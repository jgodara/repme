/*! light-blue - v3.3.0 - 2016-03-08 */
		(function() {
			var a, b, c, d = {}.hasOwnProperty, e = function(a, b) {
				function c() {
					this.constructor = a
				}
				for ( var e in b)
					d.call(b, e) && (a[e] = b[e]);
				return c.prototype = b.prototype, a.prototype = new c,
						a.__super__ = b.prototype, a
			};
					a = jQuery,
					c = '<div class="messenger-spinner">\n    <span class="messenger-spinner-side messenger-spinner-side-left">\n        <span class="messenger-spinner-fill"></span>\n    </span>\n    <span class="messenger-spinner-side messenger-spinner-side-right">\n        <span class="messenger-spinner-fill"></span>\n    </span>\n</div>',
					b = function(b) {
						function d() {
							return d.__super__.constructor.apply(this,
									arguments)
						}
						return e(d, b), d.prototype.template = function(b) {
							var e;
							return e = d.__super__.template.apply(this,
									arguments), e.append(a(c)), e
						}, d
					}(window.Messenger.Message),
					window.Messenger.themes.air = {
						Message : b
					}
		}).call(this);
		$(function() {
			function a() {
				$(".widget").widgster();
				var a = "air";
				$.globalMessenger({
					theme : a
				}), Messenger.options = {
					theme : a
				};
				var b = [ "bottom", "right" ], c = $(".location-selector"), d = function() {
					for (var c = "messenger-fixed", d = 0; d < b.length; d++)
						c += " messenger-on-" + b[d];
					$.globalMessenger({
						extraClasses : c,
						theme : a
					}), Messenger.options = {
						extraClasses : c,
						theme : a
					}
				};
			}
			a()
		});