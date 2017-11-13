var codemap = {
	"access-key-donor": "label-primary",
	"donor": "label-default",
	"admin": "label-info",
	"owner": "label-success",
	"fraud": "label-warn",
	"banned": "label-error"
};

function asyncUserLoad(steamid32, jqe) {
	$.ajax({
		url: 'ajax/user/' + steamid32,
		type: 'GET',
		success: function(response) {
			if (response.success) {
				jqe.append('<li>Steam level of <strong>' + response.level + '</strong><li>');
				jqe.append('<li>Community Score <strong>' + response.score + '</strong><li>');
				for (var i = 0 ; i < response.badges.length ; i++) {
					jqe.append('<li><span class="label ' + codemap[response.badges[i].code + ''] + '">' + response.badges[i].name + '</span></li>');
				}
			}
		}
	});
}

function plusRep(steamid32) {
	post(1, steamid32);
}

function minusRep(steamid32) {
	post(2, steamid32);
}

function post(type, steamid32) {
	$.ajax({
		url: 'ajax/user/post/',
		data: 'type=' + type + '&userid=' + steamid32,
		type: 'POST',
		success: function(response) {
			if (response.success) {
				Messenger().post({
				    message: 'Your feedback has been recorded.',
				    type: 'success',
				    showCloseButton: true
				});
				jQuery('#badges').html('');
				asyncUserLoad(jQuery('#sid32').html(), jQuery('#badges'));
			} else {
				Messenger().post({
				    message: response.message,
				    type: 'error',
				    showCloseButton: true
				});
			}
		}
	});
}