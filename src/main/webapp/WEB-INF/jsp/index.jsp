<%@ include file="common.jsp"%>


<!-- light-blue - v3.3.0 - 2016-03-08 -->

<!DOCTYPE html>
<html>
<head>
<title>RepMe - Your community reputation card.</title>

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
				Dashboard <small>The home page</small>
			</h2>
			<div class="row">
				<div class="col-lg-8">
					<section class="widget" id="shares-widget"
						data-widgster-load="server/php/widgets/shares.php">
						<header class="ui-sortable-handle">
							<h5>
								<span class="fw-semi-bold">Recent Vouches</span>
							</h5>
						</header>
						<div class="body no-padding">

							<c:forEach var="vouch" items="${recentVouches}">
								<div class="list-group list-group-lg">
									<a href='<c:url value="/vouches/${vouch.vouchid}"></c:url>'
										class="list-group-item"> <span
										class="thumb-sm pull-left mr"> <img class="img-circle"
											src="${vouch.owner.imageUrl}" alt="${vouch.owner.name}">
									</span>
										<h5 class="no-margin">
											<strong>${vouch.owner.name}</strong> asked vouches for <strong>${vouch.evictor.name}</strong>
										</h5> <small class="text-muted repme-time-elapsed">${vouch.createTime}</small>
										<span class="badge badge-success">For <c:choose>
												<c:when test="${vouch.uom} eq 1">${vouch.amount} Keys</c:when>
												<c:otherwise>$ ${vouch.amount}</c:otherwise>
											</c:choose></span>
									</a>
								</div>
							</c:forEach>
						</div>
					</section>
				</div>
				<%@ include file="widgets.jsp"%>
			</div>
		</div>
	</div>

	<%@ include file="common_js.jsp"%>
</body>
</html>