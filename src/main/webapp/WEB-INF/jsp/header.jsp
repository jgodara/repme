<header class="page-header">
	<div class="navbar">
		<ul class="nav navbar-nav navbar-right pull-right">
			<li class="visible-phone-landscape"><a href="#"
				id="search-toggle"> <i class="fa fa-search"></i>
			</a></li>

			<c:choose>
				<c:when test="${s_SessionDetails.loggedIn}">
					<li class="dropdown"><a href="#" title="8 support tickets"
						class="dropdown-toggle" data-toggle="dropdown"> <i
							class="glyphicon glyphicon-globe"></i> <span class="count">1</span>
					</a>
						<ul id="support-menu" class="dropdown-menu support" role="menu">
							<li role="presentation"><a href="#" class="support-ticket">
									<div class="picture">
										<span class="label label-important"><i
											class="fa fa-bell-o"></i></span>
									</div>
									<div class="details">Check out this awesome ticket</div>
							</a></li>
						</ul></li>
					<li class="divider"></li>
					<li class="dropdown"><a
						href="<c:url value="/users/${s_SessionDetails.userId}"></c:url>"
						title="Account" id="account" class="dropdown-toggle"
						data-toggle="dropdown"> <i class="glyphicon glyphicon-user"></i>
					</a>
					<li class="dropdown"><a
						href='<c:url value="/steamLogout"></c:url>'><i
							class="glyphicon glyphicon-off"></i></a></li>
				</c:when>
				<c:otherwise>
					<li class="drowpdown"><a
						href='<c:url value="/steamLogin"></c:url>' id="settings" title=""
						data-toggle="popover" data-placement="bottom"
						data-original-title="Login with Steam" target="popup"
						onclick="window.open('<c:url value="/steamLogin"></c:url>','popup','width=600,height=600'); return false;"><i
							class="fa fa-steam"></i> </a></li>
				</c:otherwise>
			</c:choose>

		</ul>
		<form id="search-form" action="<c:url value="/search"></c:url>"
			class="navbar-form pull-right" role="search" style="height: 0px;">
			<input type="search" name="q" class="form-control search-query"
				placeholder="Search...">
		</form>
	</div>
</header>