<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<fmt:setBundle basename="messages" />
<%@ page session="true"%>
<%@ include file="header.jsp" %>
<html>

<head>
<link href="<c:url value="/resources/bootstrap.css" />" rel="stylesheet">
<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
<title><spring:message code="label.pages.home.title"></spring:message></title>
</head>
<body>
<!-- Static navbar -->
	<nav class="navbar navbar-inverse navbar-default">
		<div class="container-fluid">
			<div class="navbar-header">
				<button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
					<span class="sr-only">Toggle navigation</span>
					<span class="icon-bar"></span>
					<span class="icon-bar"></span>
					<span class="icon-bar"></span>
					<div class="menutext">MENU</div>
				</button>
				<a class="navbar-brand" href="https://ipcamlive.com/">
					<img src="<c:url value="/resources/index_files/index-logo.png"/>" alt="ipcamlive">
				</a>
			</div>
			<div id="navbar" class="navbar-collapse collapse">
									<ul class="nav navbar-nav navbar-right">
	<li><a href="<c:url value="/login.html" />">Login</a></li>
</ul>							</div><!--/.nav-collapse -->
		</div><!--/.container-fluid -->
	</nav>
	<div class="container">
		<div class="span12">
			<div id="success">
				<spring:message code="message.regSucc"></spring:message>
			</div>
			<a href="<c:url value="/login.html" />"><spring:message
					code="label.login"></spring:message></a>
		</div>
	</div>
</body>
</html>