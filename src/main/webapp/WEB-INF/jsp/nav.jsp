<%@page import="java.util.Calendar"%>
<div class="logo">
	<h4>
		<a href="<c:url value="/" ></c:url>">Rep<strong>Me</strong></a>
	</h4>
</div>
<nav id="sidebar" class="sidebar nav-collapse collapse">

	<h5 class="sidebar-nav-title">Monthly Goals</h5>
	<!-- A place for sidebar notifications & alerts -->
	<div class="sidebar-alerts">
		<div class="alert fade in">
			<span class="text-white fw-semi-bold">Expenses Coverage</span> <br>
			<small>Current Donations: <span class="text-white fw-semi-bold">5$</span></small>
			<small>Monthly Target: <span class="text-white fw-semi-bold">20$</span></small>
			<div class="progress progress-xs mt-xs mb-0">
				<div class="progress-bar progress-bar-gray-light" style="width: 20%"></div>
			</div>
			<small><a href="<c:url value="/donatorList"></c:url>">List of all donators</a></small>
			<small><a href="<c:url value="/donate"></c:url>">Donate now</a></small>
		</div>
	</div>
	<hr>
	<div class="sidebar-alerts">
		<div class="alert fade in">
			Current time on server is<span class="text-white fw-semi-bold">
				<%=new java.util.Date()%></span> <br>
			<br> <small>All timezones on server are <%=Calendar.getInstance().getTimeZone().getDisplayName()%></small>
		</div>
	</div>
</nav>