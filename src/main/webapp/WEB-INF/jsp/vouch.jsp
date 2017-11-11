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
				<a
					href='<c:url value="/users/${vouchPost.owner.steamid32}"></c:url>'>${vouchPost.owner.name}</a>
				<small>requested vouches for</small> <a
					href='<c:url value="/users/${vouchPost.evictor.steamid32}"></c:url>'>${vouchPost.evictor.name}</a>
			</h2>
			<div class="row">
				<div class="col-lg-8">
					<div class="row">
						<div class="col-lg-6">
							<img src="${vouchPost.owner.fullImage}">
							<h3>${vouchPost.owner.name}</h3>
							<ul id="own-badges">
								<li>Steam Level of <strong>${vouchPost.owner.level}</strong></li>
							</ul>
						</div>
						<div class="col-lg-6">
							<img src="${vouchPost.evictor.fullImage}">
							<h3>${vouchPost.evictor.name}</h3>
							<ul id="evic-badges">
								<li>Steam Level of <strong>${vouchPost.evictor.level}</strong></li>
							</ul>
						</div>
					</div>
					<hr>
					<div class="row">
						<div class="col-lg-12">
							<div id="progressInfo" class="info"></div>
							<div class="progress progress-large">
								<div id="vouchPBar" class="progress-bar progress-bar-success"
									style="width: 0%;"></div>
							</div>
						</div>
					</div>
					<hr>
					<div class="row">
						<div class="col-lg-12">
							<section class="widget" id="shares-widget"
								data-widgster-load="server/php/widgets/shares.php">
								<header class="ui-sortable-handle">
									<h5>
										<span class="fw-semi-bold">Users who vouched for
											${vouchPost.evictor.name}</span>
									</h5>
								</header>
								<div id="vouchers" class="body no-padding"></div>
							</section>
						</div>
					</div>
					<hr>
					<c:if test="${s_SessionDetails.loggedIn}">
						<div class="row">
							<div class="col-lg-12">
								<section class="widget">
									<header>
										<h5>Vouch for ${vouchPost.evictor.name}</h5>
									</header>
									<div class="body">
										<form role="form">
											<fieldset>
												<div class="row">
													<div class="col-sm-8">
														<div class="form-group">
															<label id="label-100" for="segmented-dropdown"></label>
															<div class="input-group">
																<input id="segmented-dropdown"
																	class="form-control input-lg" type="text">
																<div class="input-group-btn">
																	<button class="btn btn-info input-lg" tabindex="-1">Vouch</button>
																</div>
															</div>
															<span class="help-block">Vouching is permanent and
																irreversible. If you vouch for
																${vouchPost.evictor.name}, the data that you've provided
																us would be made visible to the public.</span>
														</div>
													</div>
												</div>
											</fieldset>
										</form>
									</div>
								</section>
							</div>
						</div>
					</c:if>
				</div>
				<%@ include file="widgets.jsp"%>
			</div>
		</div>
	</div>

	<%@ include file="common_js.jsp"%>
	<script type="text/javascript" src="assets/js/user.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			$.ajax({
				url : 'ajax/vouches/${vouchPost.vouchid}',
				type : 'GET',
				success : function(response) {
					//response = JSON.parse( response );
					if (response.success) {
						for (var i = 0 ; i < response.vouchers.length ; i++) {
							var voucher = response.vouchers[i];
							var html = '<div class="list-group list-group-lg">';
							html += '<a href="users/' + voucher.steamid32 + '" class="list-group-item"> <span class="thumb-sm pull-left mr"> <img class="img-circle" src="' + voucher.imageUrl +'" ';
							html += 'alt="' + voucher.name + '">';
							html += '</span>';
							html += ' <h5 class="no-margin"> <strong>' + voucher.name + '</strong> ';								
								<c:choose>
									<c:when test="${vouchPost.uom eq 1}">html += 'for the amount of $' + voucher.vouched;</c:when>
									<c:otherwise>html += 'for ' + voucher.vouched + ' keys ';</c:otherwise>
								</c:choose>
								
							html += '</h5> <small class="text-muted repme-time-elapsed"> </small>';
							html += '</a>';
							html += '</div>';
							jQuery('#vouchers').append(html);
						}
						
						jQuery('#label-100').append('Vouches worth ');								
						<c:choose>
							<c:when test="${vouchPost.uom eq 1}">jQuery('#label-100').append("$" + (${vouchPost.amount} - response.amtDone));</c:when>
							<c:otherwise>jQuery('#label-100').append((${vouchPost.amount} - response.amtDone) + ' keys');</c:otherwise>
						</c:choose>
						jQuery('#label-100').append(' required to reach 100% vouch rate.');			
						
						jQuery('#progressInfo').append((response.amtDone / ${vouchPost.amount}) * 100);
						jQuery('#progressInfo').append('% vouches done.');
						
						jQuery('#vouchPBar').css('width', ((response.amtDone / ${vouchPost.amount}) * 100) + '%');
					}
				}
			});
		});
		
		asyncUserLoad('${vouchPost.owner.steamid32}', jQuery('#own-badges'));
		asyncUserLoad('${vouchPost.evictor.steamid32}', jQuery('#evic-badges'));
	</script>
</body>
</html>