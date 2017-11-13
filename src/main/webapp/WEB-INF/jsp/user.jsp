<%@ include file="common.jsp"%>


<!-- light-blue - v3.3.0 - 2016-03-08 -->

<!DOCTYPE html>
<html>
<head>
<title>RepMe - ${user.name}.</title>
<base href='<c:url value="/"></c:url>'>
<link href="assets/css/application.min.css" rel="stylesheet">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="description" content="">
<meta name="author" content="">
<script>
	/* yeah we need this empty stylesheet here. It's cool chrome & chromium fix
	   chrome fix https://code.google.com/p/chromium/issues/detail?id=167083
	              https://code.google.com/p/chromium/issues/detail?id=332189
	 */
</script>
</head>
<body>
	<div id="sid32" style="display: none !important;">${user.steamid32}</div>
	<%@ include file="nav.jsp"%>
	<div class="wrap">
		<%@ include file="header.jsp"%>
		<div class="content container">
			<h2 class="page-title">${user.name}</h2>
			<div class="row">
				<div class="col-lg-8">
					<div class="row">
						<div class="col-lg-6">
							<img alt="${user.name}" src="${user.fullImage}">
						</div>
						<div class="col-lg-6">
							<h3>
								<ul id="badges">
								</ul>
							</h3>
						</div>
					</div>
					<hr>
					<div class="row">
						<div class="col-lg-12">
							<div class="widget text-align-center">
								<p>
									<span class="label label-default"><a style="color: white;" href="https://steamcommunity.com/profiles/${user.steamid32}" target="_blank"><i class="fa fa-steam"></i> Steam Profile</a></span>
									<span class="label label-info"><a style="color: white;" href="https://repme.github.io?r=u_${user.steamid32}" target="_blank">Short URL</a></span>
									<span class="label label-success"><a style="color: white;" href="javascript:plusRep('${user.steamid32}');"><i class="fa fa-plus"></i> rep</a></span>
									<span class="label label-danger"><a style="color: white;" href="javascript:minusRep('${user.steamid32}');"><i class="fa fa-minus"></i> rep</a></span>
								</p>
							</div>
						</div>
					</div>
				</div>
				<%@ include file="widgets.jsp"%>
			</div>
		</div>
	</div>

	<%@ include file="common_js.jsp"%>
	<script type="text/javascript" src="assets/js/user.js"></script>
	<script type="text/javascript">
		asyncUserLoad('${user.steamid32}', jQuery('#badges'));
	</script>
</body>
</html>