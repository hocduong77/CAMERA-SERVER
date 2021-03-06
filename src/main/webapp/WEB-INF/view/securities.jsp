<%@ include file="header.jsp"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<div class="container">

	<div id="contentdiv" class="content">

		<script type="text/javascript">
			var isCamerasLoaded = false;
			var loadingtimeout;

			$(document).ready(function() {
				$('#newcamerabutton').click(function() {
					document.location.href = 'console';
				});

				LoadCameras();

				loadingtimeout = setTimeout(function() {
					$('#loadingdiv').show();
				}, 500);
			});
		</script>

<!-- Static navbar -->
		<nav class="navbar navbar-inverse navbar-default">
			<div class="container-fluid">
				<div class="navbar-header">
					<button type="button" class="navbar-toggle collapsed"
						data-toggle="collapse" data-target="#navbar" aria-expanded="false"
						aria-controls="navbar">
						<span class="sr-only">Toggle navigation</span> <span
							class="icon-bar"></span> <span class="icon-bar"></span> <span
							class="icon-bar"></span>
						<div class="menutext">MENU</div>
					</button>
					<a class="navbar-brand" href="<c:url value="/homepage" />"><img
						src="<c:url value="/resources/dashboard/index-logo.png"/>"
						alt="cameraserver"></a>
				</div>
				<div id="navbar" class="navbar-collapse collapse">
					<ul class="nav navbar-nav">
						<li class=""><a href="<c:url value="/securities" />">Security</a></li>
						<li><a href="<c:url value="/gateway" />">Gateway</a></li>
						<li><a href="<c:url value="/port" />">Port</a></li>
					</ul>
					<ul class="nav navbar-nav navbar-right">
						<li><a href="<c:url value="/j_spring_security_logout" />">Logout</a></li>

					</ul>
				</div>
				<!--/.nav-collapse -->
			</div>
			<!--/.container-fluid -->
		</nav>
		
		<div id="alert_placeholder"></div>

		<div class="page-header camerapage-header">
			<h1>Securities</h1>
		</div>

		<div id="loadingdiv" class="loading nonvisible" style="display: none;"></div>

		<div id="camerasdiv" class="table-responsive nonvisible"
			style="display: block;">
			<table id="camerastable" class="table table-striped">
				<thead>
					<tr>
						<th>First Name</th>
						<th>Last Name</th>
						<th class="hidden-xs hidden-sm">email</th>
						<th></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${users}" var="user">
						<tr>
							<td id="camerastable_28209_alias"><c:out
									value="${user.firstName}" /></td>
							<td id="camerastable_28209_name" class="hidden-xs hidden-sm"><c:out
									value="${user.lastName}" /></td>
							<td id="camerastable_28209_url" class="hidden-xs hidden-sm"><c:out
									value="${user.email}" /></td>
						<%-- 	<td id="camerastable_28209_enabled"><span
								class="glyphicon glyphicon-remove" aria-hidden="true"></span></td>
							<td class="rightcell"><a href="<c:url value="/setting/" />"
								class="btn btn-default" title="Camera page"><span
									class="glyphicon glyphicon-cog" aria-hidden="true"></span></a></td> --%>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>

		<a href="console" class="btn btn-success nonvisible"
			style="display: inline-block;">New security</a><br>
		<br>
	</div>

</div>

</div>



</body>
</html>