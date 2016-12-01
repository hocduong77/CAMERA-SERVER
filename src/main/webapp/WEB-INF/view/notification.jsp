<%@ include file="menu.jsp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<!-- saved from url=(0022)https://ipcamlive.com/ -->
<html lang="en"><head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	
	<title>Embedding IP Camera Live Video Stream in web page</title>
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<meta name="keywords" content="ip camera, network camera, embed ip camera, embed ip camera into webpage, embed ip camera stream in web page, embed network camera on website">
	<meta name="description" content="Video streaming solution for embedding your IP camera live video stream in web page.">
	<meta name="author" content="">
	 <script src="<c:url value="/resources/dashboard/jquery-1.11.2.min.js"/>"></script>
	<script src="<c:url value="/resources/dashboard/bootstrap.min.js"/>"></script>
	<script src="<c:url value="/resources/dashboard/script.js"/>"></script>
	<script src="<c:url value="/resources/dashboard/jquery.datetimepicker.js"/>"></script>
	<script src="<c:url value="/resources/dashboard/jquery.validate.min.js"/>"></script>
	<link href="<c:url value="/resources/dashboard/jquery.validate.css"/>" rel="stylesheet">
	<link href="<c:url value="/resources/dashboard/jquery.datetimepicker.css"/>" rel="stylesheet">
	<link href="<c:url value="/resources/dashboard/jquery-ui-1.10.0.custom.min.css"/>" rel="stylesheet">
	<link href="<c:url value="/resources/dashboard/skin.css"/>" rel="stylesheet"> 
	
	
	<link href="<c:url value="/resources/dashboard/jquery.fancybox.css?v=2.1.5"/>" rel="stylesheet"media="screen">
	
	<link href="<c:url value="/resources/dashboard/jquery.fancybox-buttons.css?v=1.0.5"/>" rel="stylesheet">

	<script src="<c:url value="/resources/dashboard/jquery-ui.js"/>"></script>
	
	<script src="<c:url value="/resources/dashboard/jquery.fancybox.js?v=2.1.5"/>"></script>
	<script src="<c:url value="/resources/dashboard/jquery.fancybox-buttons.js?v=1.0.5"/>"></script>
	
	
	<script>
		$( function() {
			$( "#date_from,#date_to" ).datepicker();
		} );
		$(document).ready(function() {
			$('.fancybox-buttons').fancybox({
				openEffect  : 'none',
				closeEffect : 'none',

				prevEffect : 'none',
				nextEffect : 'none',

				closeBtn  : false,

				helpers : {
					title : {
						type : 'inside'
					},
					buttons	: {}
				},

				afterLoad : function() {
					this.title = 'Image ' + (this.index + 1) + ' of ' + this.group.length + (this.title ? ' - ' + this.title : '');
				}
			});
		})
	</script>
</head>

<body>
<div class="container">
	<div class="content">
			<form:form modelAttribute="search" method="POST">
			<div class="search-content">
			<label class="pull-left">Seach date</label>
			<form:input id="date_from"  path="from" value="" type="text"  placeholder="From" />
			<form:input id="date_to" path="to" value="" type="text"  placeholder="To" />
			<!-- <input type="text" id="date_from">
			<input type="text" id="date_to"> -->
			<!--  <label class="pull-right">Camera</label>  -->
			<form:select path="alias" items="${cameraAlias}" class="pull-left btn-default select">					
			</form:select>	
			<input type="submit" class="btn btn-default">
		</div>
	</form:form>
		<div id="camerasdiv" class="table-responsive nonvisible" style="display: block;">
	<table id="camerastable" class="table table-striped">
		<thead>
			<tr>
				<th>Camera Name</th>
				<th>Time</th>
				<th>View Detail</th>
				<th></th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${notificationDtos}" var="notification">    
		<tr>
		<td id="camerastable_28209_alias"><c:out value="${notification.cameraName}"/></td>
		<td id="camerastable_28209_name" class="hidden-xs hidden-sm"><c:out value="${notification.time}"/></td>
		<td id="camerastable_28209_url" class="hidden-xs hidden-sm"><c:out value="${camera.cameraUrl}"/></td>
		<!-- <td id="camerastable_28209_enabled"><span class="glyphicon glyphicon-remove" aria-hidden="true"></span></td> -->
		<td class="rightcell"><a href="<c:url value="/notification/${notification.id}" />" class="btn btn-default" title="Camera page"></a></td></tr>
		</c:forEach>
		</tbody>
	</table>
</div>
	
	</div>
	</div>			
<!--popup-->
<div class="modal fade capture-modal" id="show_list" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel">
	<div class="modal-dialog" role="document">
		<div class="modal-content">
			<div class="modal-body text-center">
			<!-- 	<img src="images/img.jpg" alt=""> -->
			</div>	
		</div>
	</div>
</div>
</body>
</html>
