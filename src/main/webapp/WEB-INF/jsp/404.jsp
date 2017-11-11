<%@ include file="common.jsp"%>


<!-- light-blue - v3.3.0 - 2016-03-08 -->

<!DOCTYPE html>
<html>
<head>
<title>RepMe - Your community reputation card.</title>
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
	<div class="error-page container">
		<main id="content" class="error-container" role="main">
		<div class="row">
			<div
				class="col-lg-4 col-sm-6 col-xs-10 col-lg-offset-4 col-sm-offset-3 col-xs-offset-1">
				<div class="error-container">
					<h1 class="error-code">404</h1>
					<p class="error-info">Opps, it seems that this page does not
						exist.</p>
					<p class="error-help mb">
						<a href="<c:url value="/"></c:url>">Home Page</a>
					</p>
				</div>
			</div>
		</div>
		</main>
	</div>


	<%@ include file="common_js.jsp"%>
</body>
</html>