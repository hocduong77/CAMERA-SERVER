<%@ include file="header.jsp"%>
<%-- <%@ page contentType="text/html;charset=UTF-8" language="java"%> --%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>

<script
	src="<c:url value="/resources/dashboard/jquery.datetimepicker.js"/>"></script>
<link
	href="<c:url value="/resources/dashboard/jquery.datetimepicker.css"/>"
	rel="stylesheet">

<div class="container">

	<div id="contentdiv" class="content">
		<script type="text/javascript">
			var readonlymode = 0;
			var cameraid = '28209';
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
						<li><a href="<c:url value="/ListPort" />">Port</a></li>
					</ul>
					<ul class="nav navbar-nav navbar-right">
						<li><a href="<c:url value="/j_spring_security_logout" />">Logout</a></li>

					</ul>
				</div>
				<!--/.nav-collapse -->
			</div>
			<!--/.container-fluid -->
		</nav>
		

		<h2>Add Gateway</h2>


		<div id="alert_placeholder"></div>

		<div id="loadingdiv" class="loading nonvisible" style="display: none;"></div>

		<div id="cameradiv" class="camerasettings nonvisible"
			style="display: block;">

			<form:form modelAttribute="gateway" method="POST"
				class="form-horizontal" novalidate="novalidate" enctype="utf8">
				<div id="enableddiv" class="form-group">
					<div class="col-sm-offset-2 col-sm-4">
						<div class="checkbox">
							<%-- <label id="enabledtextdiv"> <form:checkbox path="enabled" />
								Camera enabled
							</label>  --%>
							<label id="enabledtextdiv"class="alert alert-error" /> ${mess}
							</label>
						</div>
					</div>
				</div>

				<div id="aliasdiv" class="form-group">
					<label id="aliastextdiv" class="col-sm-2 control-label">Gateway IP:</label>
					<div class="col-sm-4">
						<form:input path="gatewayIP" value="" type="text"
							class="form-control valid" placeholder="gateway IP" />
						<!-- <input type="text" name="aliasedit" id="aliasedit" class="form-control valid" value="Alias"> -->
					</div>
				</div>
				<div id="namediv" class="form-group">
					<label id="nametextdiv" class="col-sm-2 control-label">User email:</label>
					<div class="col-sm-4">
						<form:select path="userEmail" items="${userEmails}" class="pull-left btn-default select">					
						</form:select>
					</div>
				</div>			
				<div class="form-group">
					<div class="col-sm-offset-2 col-sm-4">
						<input type="submit" value="Add"
							class="btn btn-primary pull-left" />
						<div id="formprocessdiv" class="loading pull-left"
							style="display: none;"></div>
					</div>
				</div>
							
			</form:form>

		</div>
		<script
			src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
		</body>
		</html>