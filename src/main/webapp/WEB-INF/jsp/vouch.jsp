<%@ include file="common.jsp"%>


<!-- light-blue - v3.3.0 - 2016-03-08 -->

<!DOCTYPE html>
<html>
<head>
<title>RepMe - Vouch for ${vouchPost.evictor.name}.</title>
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
				<a href='<c:url value="/users/${vouchPost.owner.steamid32}"></c:url>'>${vouchPost.owner.name}</a> 
				<small>requested vouches for</small> <a href='<c:url value="${vouchPost.evictor.steamid32}"></c:url>'>${vouchPost.evictor.name}</a>
			</h2>
			<div class="row">
				<div class="col-lg-8">
					<div class="row">
						<div class="col-lg-6">
							<img src="${vouchPost.owner.fullImage}"> <h3>${vouchPost.owner.name}</h3> 
							<ul>
								<li>Steam Level of <strong>${vouchPost.owner.level}</strong></li>
								<li>Community Score of <strong>105</strong></li>
								<li>Special Reputation Score of <strong>20</strong></li>
								<li><span class="label label-default">Community Donor</span></li>
								<li><span class="label label-warning">Fraud Warning</span></li>
							</ul>
						</div>
						<div class="col-lg-6">
							<img src="${vouchPost.evictor.fullImage}">
							<h3>${vouchPost.evictor.name}</h3> 
							<ul>
								<li>Steam Level of <strong>${vouchPost.evictor.level}</strong></li>
								<li>Community Score of <strong>105</strong></li>
								<li>Special Reputation Score of <strong>20</strong></li>
								<li><span class="label label-default">Community Donor</span></li>
								<li><span class="label label-info">Community Admin</span></li>
							</ul> 
						</div>
					</div>
				</div>
				<%@ include file="widgets.jsp"%>
			</div>
		</div>
	</div>

	<%@ include file="common_js.jsp"%>
</body>
</html>