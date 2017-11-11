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
	<%@ include file="nav.jsp"%>
	<div class="wrap">
		<%@ include file="header.jsp"%>
		<div class="content container">
			<h2 class="page-title">
				<a href='<c:url value="/users/${user.steamid32}"></c:url>'>${user.name}</a>
			</h2>
			<div class="row">
				<div class="col-lg-8">
					<div class="col-lg-6">
						<img alt="${user.name}" src="${user.fullImage}">
					</div>
					<div class="col-lg-6">
						<h3>
							<ul id="badges">
								<li>Steam Level of <strong>${user.level}</strong></li>
							</ul>
						</h3>
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