<%-- <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<html>
<head>
<link href="<c:url value="/resources/bootstrap.css" />" rel="stylesheet">
</head>
<body>
	<div class="container">
		<div class="span12">
			<h1>This is the landing page for the admin</h1>
			<sec:authorize access="hasRole('ROLE_USER')">
		This text is only visible to a user
		<br />
			</sec:authorize>
			<sec:authorize access="hasRole('ROLE_ADMIN')">
		This text is only visible to an admin
		<br />
			</sec:authorize>
			<a href="<c:url value="/j_spring_security_logout" />"><spring:message
					code="label.pages.logout"></spring:message></a> <a
				href="<c:url value="/admin.html" />"><spring:message
					code="label.pages.admin"></spring:message></a>
		</div>
	</div>
</body>

</html> --%>

<%@ include file="header.jsp"%>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<%@ page session="false"%>
<body>

	<div class="container">

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

		<div class="container">

			<div id="contentdiv" class="content">

				<div class="login">
					<div id="alert_placeholder"></div>

					<div id="registerdiv">
						<form:form modelAttribute="user" method="post" id="registerform"
							class="form-signin" novalidate="novalidate" enctype="utf8">
							<h2 class="form-signin-heading">Create Security</h2>

							<form:input path="firstName" value="" type="text"
								id="firstnameedit" name="firstnameedit" class="form-control"
								placeholder="First name" />
							<form:input path="lastName" value="" type="text"
								id="lastnameedit" name="lastnameedit" class="form-control"
								placeholder="Last name" />
							<form:input path="password" value="" type="password"
								id="passwordedit" name="passwordedit" class="form-control"
								placeholder="Password" />
							<form:input path="matchingPassword" value="" type="password"
								id="confirm_passwordedit" name="confirm_passwordedit"
								class="form-control" placeholder="Re-type password" />
							<form:input path="email" value="" type="text" id="emailedit"
								name="emailedit" class="form-control" placeholder="Email" />
							<button id="okbutton" class="btn btn-large btn-default"
								data-loading-text="Loading..." type="submit">Create</button>
							<%-- <div class="register pull-right">
			<a href="<c:url value="/login.html" />">Login</a>
			</div> --%>
						</form:form>

					</div>
				</div>
			</div>

		</div>

	</div>


	<script
		src="<c:url value="/resources/registration_files/bootstrap.min.js"/>"></script>


</body>
</html>
