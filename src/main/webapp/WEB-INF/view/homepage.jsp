<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ include file="menu.jsp" %>
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

	<script src="<c:url value="/resources/dashboard/jquery-1.11.2.min.js"/>"></script>
	<script src="<c:url value="/resources/dashboard/bootstrap.min.js"/>"></script>
	<script src="<c:url value="/resources/dashboard/script.js"/>"></script>
	
	<link href="<c:url value="/resources/dashboard/jquery-ui-1.10.0.custom.min.css"/>" rel="stylesheet">
	<link href="<c:url value="/resources/dashboard/skin.css"/>" rel="stylesheet">
	

</head>

<body>
<div class="container">
	
	<div class="content">
		<div class="row">
		<c:forEach items="${cameras}" var="camera" varStatus="status">
		<c:choose>
		<c:when test="${status.index == 0}">
			<div class="col-xs-12 margin-bottom">
				<div class="video-content text-center">
				<%-- <video id="video" src="<c:out value="${camera.streamUrl}"/>" type="video/ogg; codecs=theora" autoplay="autoplay" alt="" >									
				</video> --%>
					<applet name="${camera.cameraId}"
					CODEBASE="http://localhost:8080/camera-server/resources/resourceapplet"
					code="camera.class" width="320" height="240">
					<param name="rtpPort" value="${camera.port}" />
					<param name="separate_jvm" value="true">
					</applet>
				<div class="video-controls">
						<a href="javascript:;" class="play-stop curr-play">Play</a>
						<button id="${camera.cameraId}" type="button" onclick="capture(this.id)" class="capture" >Capture</button>
						<!-- <button class="record">Record</button> -->
					</div>	
				</div>
				
			</div>
		</c:when>
		<c:when test="${status.index % 2 == 1}">
			<div class="col-sm-6 margin-bottom padding-right">
				<div class="video-content text-center">
					<%-- <video id="video" src="<c:out value="${camera.streamUrl}"/>" type="video/ogg; codecs=theora" autoplay="autoplay" alt="" >
					</video> --%>
					<applet name="${camera.cameraId}"
					CODEBASE="http://localhost:8080/camera-server/resources/resourceapplet"
					code="camera.class" width="320" height="240">
					<param name="rtpPort" value="${camera.port}" />
					<param name="separate_jvm" value="true">
					</applet>
					<div class="video-controls">
						<a href="javascript:;" class="play-stop curr-play">Play</a>
						<button id="${camera.cameraId}" type="button" onclick="capture(this.id)" class="capture" >Capture</button>
						<!-- <button class="record">Record</button> -->
					</div>
				</div>
			</div>
			</c:when>
			<c:otherwise>
			<div class="col-sm-6 margin-bottom padding-left">
				<div class="video-content text-center">
					<%-- <video id="video" src="<c:out value="${camera.streamUrl}"/>" type="video/ogg; codecs=theora" autoplay="autoplay" alt="" >
					</video> --%>
					<applet name="${camera.cameraId}"
					CODEBASE="http://localhost:8080/camera-server/resources/resourceapplet"
					code="camera.class" width="320" height="240">
					<param name="rtpPort" value="${camera.port}" />
					<param name="separate_jvm" value="true">
					</applet>
					<div class="video-controls">
						 <a href="javascript:;" class="play-stop curr-play">Play</a> 
						<button id="${camera.cameraId}" type="button" onclick="capture(this.id)" class="capture" >Capture</button>
						<!-- <button class="record">Record</button> -->
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
<div id="myModal"  class="modal fade capture-modal" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel">
	<div class="modal-dialog" role="document">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
				<h4 class="modal-title" id="myModalLabel">Capture</h4>
			</div>
			<div class="modal-body text-center">
				<img id="my_image" src="<c:url value="/resources/dashboard/img.png"/>" alt="">
			</div>
			<!-- <div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
				<button type="button" class="btn btn-primary">Save</button>
			</div> -->
		</div>
	</div>
</div>
<script>
function capture(cameraId) {
	console.log("cameraId" + cameraId);
	$.ajax({
		type: "post",
		url : 'capture' , 
		data:'cameraId=' + cameraId ,
		success : function(data) {
			var str = data;
			console.log("data " +data);
			$("#my_image").attr("src",data);
			$('#myModal').modal('show');
		}
	});
}
</script>
</body>
</html>