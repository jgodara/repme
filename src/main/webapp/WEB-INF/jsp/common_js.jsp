<!-- common libraries. required for every page-->
<script src="assets/lib/jquery/dist/jquery.min.js"></script>
<script src="assets/lib/jquery-pjax/jquery.pjax.js"></script>
<script src="assets/lib/bootstrap/bootstrap.min.js"></script>
<script src="assets/lib/widgster/widgster.js"></script>
<script src="assets/lib/underscore/underscore.js"></script>

<!-- common application js -->
<script src="assets/js/app.js"></script>
<script src="assets/js/settings.js"></script>


<script type="text/javascript">
	$(document).ready(function() {
		$('.repme-time-elapsed').each(function() {
			var createTime = new Date($(this).html());
			
			var diff = new Date() - createTime; //in ms
			diff /= 1000; //in s
			
			var unit = 'seconds';
			if (diff >= 60) {
				diff /= 60; //in m
				unit = 'm';
				
				if (diff >= 60) {
					diff /= 60; //in h
					
					unit = 'h';
					
					if (diff >= 24) {
						diff /= 24; //in d
						
						unit = 'd';
					}
				}
			}

			diff = Math.round(diff);
			$(this).html(diff + '' + unit + ' ago');
		});
	});
</script>