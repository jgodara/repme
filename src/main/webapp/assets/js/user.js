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
				jqe.append('<li>Community Score <strong>' + response.score + '</strong><li>');
				for (var i = 0 ; i < response.badges.length ; i++) {
					jqe.append('<li><span class="label ' + codemap[response.badges[i].code + ''] + '">' + response.badges[i].name + '</span></li>');
				}
			}
		}
	});
}