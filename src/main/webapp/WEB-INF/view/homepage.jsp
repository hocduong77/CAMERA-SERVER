<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<!-- saved from url=(0022)https://.com/ -->
<html lang="en"><head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	
	<title>Embedding IP Camera Live Video Stream in web page</title>
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<meta name="keywords" content="ip camera, network camera, embed ip camera, embed ip camera into webpage, embed ip camera stream in web page, embed network camera on website">
	<meta name="description" content="Video streaming solution for embedding your IP camera live video stream in web page.">
	<meta name="author" content="">	
	
	<link href="<c:url value="/resources/dashboard/bootstrap.min.css"/>" rel="stylesheet">
	<!-- <link href="./css/bootstrap.min.css" rel="stylesheet"> -->
	<!--[if lt IE 9]>
	<script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
	<script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
	<![endif]-->
	<script src="<c:url value="/resources/dashboard/jquery-1.11.2.min.js"/>"></script>
	<script src="<c:url value="/resources/dashboard/bootstrap.min.js"/>"></script>
	<script src="<c:url value="/resources/dashboard/script.js"/>"></script>
	
	<link href="<c:url value="/resources/dashboard/jquery-ui-1.10.0.custom.min.css"/>" rel="stylesheet">
	<link href="<c:url value="/resources/dashboard/skin.css"/>" rel="stylesheet">
	
	<!-- <script src="./js/jquery-1.11.2.min.js"></script>
	<script src="./js/bootstrap.min.js"></script>
	<script src="./js/script.js"></script>
	<link href="./css/jquery-ui-1.10.0.custom.min.css" rel="stylesheet">
	<link href="./css/skin.css" rel="stylesheet"> -->
</head>

<body>
<div class="container">
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
				<a class="navbar-brand" href="<c:url value="/homepage" />"><img src="<c:url value="/resources/dashboard/index-logo.png"/>" alt="cameraserver"></a>
			</div>
			<div id="navbar" class="navbar-collapse collapse">
				<ul class="nav navbar-nav">
						<li class=""><a href="<c:url value="/homepage" />">Dashboard</a></li>
						<li><a href="<c:url value="/cameras" />">Cameras</a></li>
						<li><a href="<c:url value="/videos" />">Images</a></li>
						<li><a href="<c:url value="/images" />">Videos</a></li>		
				</ul>
				<ul class="nav navbar-nav navbar-right">
					<li class="dropdown">
						<a href="/userprofile" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">hoc duong <span class="caret"></span></a>
						<ul class="dropdown-menu" role="menu">
							<li><a href="<c:url value="/Profile" />">Profile</a></li>	
							<li><a href="<c:url value="/changepassword" />">Changepassword</a></li>	
							<li><a href="<c:url value="/j_spring_security_logout" />">Logout</a></li>	
						</ul>
					</li>
				</ul>
			</div>
			<!--/.nav-collapse -->
		</div><!--/.container-fluid -->
	</nav>
	<div class="content">
		<div class="row">
		<c:forEach items="${cameras}" var="camera" varStatus="status">
		<c:choose>
		<c:when test="${status.index == 0}">
			<div class="col-xs-12 margin-bottom">
				<div class="video-content text-center">
				<video id="video" src="<c:out value="${camera.streamUrl}"/>" type="video/ogg; codecs=theora" autoplay="autoplay" alt="" />
					 <%-- <img src="<c:url value="/resources/dashboard/img.png"/>" alt="">  --%>
					<div class="video-controls">
						<a href="javascript:;" class="play-stop curr-play">Play</a>
						<button type="button" class="capture" data-toggle="modal" data-target=".capture-modal">Capture</button>
						<button class="record">Record</button>
					</div>
				</div>
			</div>
		</c:when>
		<c:when test="${status.index % 2 == 1}">
			<div class="col-sm-6 margin-bottom padding-right">
				<div class="video-content text-center">
					<video id="video" src="<c:out value="${camera.streamUrl}"/>" type="video/ogg; codecs=theora" autoplay="autoplay" alt="" />
					<div class="video-controls">
						<a href="javascript:;" class="play-stop curr-play">Play</a>
						<button type="button" class="capture" data-toggle="modal" data-target=".capture-modal">Capture</button>
						<button class="record">Record</button>
					</div>
				</div>
			</div>
			</c:when>
			<c:otherwise>
			<div class="col-sm-6 margin-bottom padding-left">
				<div class="video-content text-center">
					<video id="video" src="<c:out value="${camera.streamUrl}"/>" type="video/ogg; codecs=theora" autoplay="autoplay" alt="" />
					<div class="video-controls">
						<a href="javascript:;" class="play-stop curr-play">Play</a>
						<button type="button" class="capture" data-toggle="modal" data-target=".capture-modal">Capture</button>
						<button class="record">Record</button>
					</div>
				</div>
			</div> 
			</c:otherwise>
			</c:choose>
			</c:forEach>
		</div>
	</div>
</div>
<!--popup-->
<div class="modal fade capture-modal" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel">
	<div class="modal-dialog" role="document">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
				<h4 class="modal-title" id="myModalLabel">Capture</h4>
			</div>
			<div class="modal-body text-center">
				<img src="<c:url value="/resources/dashboard/img.png"/>" alt="">
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
				<button type="button" class="btn btn-primary">Save</button>
			</div>
		</div>
	</div>
</div>
</body>
</html>