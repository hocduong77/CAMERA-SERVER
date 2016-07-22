<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<!-- saved from url=(0022)https://ipcamlive.com/ -->
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<title>Embedding IP Camera Live Video Stream in web page -
	IPCamLive.com</title>
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="keywords"
	content="ip camera, network camera, embed ip camera, embed ip camera into webpage, embed ip camera stream in web page, embed network camera on website">
<meta name="description"
	content="Video streaming solution for embedding your IP camera live video stream in web page.">
<meta name="author" content="">

<link href="<c:url value="/resources/DashBoard_files/bootstrap.css"/>" rel="stylesheet">
<link href="<c:url value="/resources/DashBoard_files/bootstrap.min.css"/>" rel="stylesheet">
<!--[if lt IE 9]>
	<script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
	<script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
	<![endif]-->
<link href="<c:url value="/resources/DashBoard_files/jquery-ui-1.10.0.custom.min.css"/>" rel="stylesheet">
<link href="<c:url value="/resources/DashBoard_files/jquery.validate.css"/>" rel="stylesheet">
<link href="<c:url value="/resources/DashBoard_files/jquery.datetimepicker.css"/>" rel="stylesheet">
<link href="<c:url value="/resources/DashBoard_files/skin.css"/>" rel="stylesheet">

<!-- download this image -->
<link rel="shortcut icon"  href="<c:url value="/resources/DashBoard_files/ipcamlive_favicon_128.png"/>">

<script async="" src="<c:url value="/resources/DashBoard_files/analytics.js"/>"></script>

<script src="<c:url value="/resources/DashBoard_files/jquery-1.11.2.min.js"/>"></script>
<script src="<c:url value="/resources/DashBoard_files/jquery-ui-1.10.0.custom.min.js"/>"></script>
<script src="<c:url value="/resources/DashBoard_files/mybootstrap.js"/>"></script>
<script src="<c:url value="/resources/DashBoard_files/md5.js"/>"></script>
<script src="<c:url value="/resources/DashBoard_files/highcharts.js"/>"></script>
<script src="<c:url value="/resources/DashBoard_files/exporting.js"/>"></script>
<script src="<c:url value="/resources/DashBoard_files/data.js"/>"></script>
<script src="<c:url value="/resources/DashBoard_files/jquery.validate.min.js"/>"></script>
<script src="<c:url value="/resources/DashBoard_files/jquery.datetimepicker.js"/>"></script>
<script src="<c:url value="/resources/DashBoard_files/ajaxfileupload.js"/>"></script>

<script type="text/javascript" src="<c:url value="/resources/DashBoard_files/cookie.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/DashBoard_files/he.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/DashBoard_files/xutils.js"/>"></script>

</head>

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
					<a class="navbar-brand" href="<c:url value="/homepage"/>"><img
						src="<c:url value="/resources/DashBoard_files/index-logo.png"/>" alt="ipcamlive"></a>
				</div>
				<div id="navbar" class="navbar-collapse collapse">
					<ul class="nav navbar-nav">
						<li class="active"><a href="<c:url value="/homepage" />">Dashboard</a></li>
						<li><a href="<c:url value="/cameras" />">Cameras</a></li>
						<li><a href="<c:url value="/accountstatus" />">Account</a></li>
						<li><a href="<c:url value="/finance" />">Finance</a></li>				
					</ul>
					<ul class="nav navbar-nav navbar-right">
						<li class="dropdown"><a
							href="<c:url value="/j_spring_security_logout" />" class="dropdown-toggle"
							data-toggle="dropdown" role="button" aria-expanded="false">Logout <span class="caret"></span>
						</a>
							<ul class="dropdown-menu" role="menu">
							<li><a href="<c:url value="/Profile" />">Profile</a></li>	
							<li><a href="<c:url value="/changepassword" />">Changepassword</a></li>	
							<li><a href="<c:url value="/j_spring_security_logout" />">Logout</a></li>								
							</ul></li>
					</ul>
				</div>
				<!--/.nav-collapse -->
			</div>
			<!--/.container-fluid -->
		</nav>

		
