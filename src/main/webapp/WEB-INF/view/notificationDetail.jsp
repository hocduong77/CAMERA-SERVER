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
	<video width="400" controls>
  	<source src="${video}">
	</video>
	</div>

<div class="seach-result">
		
			<div class="row">
				<!-- list img -->
				<c:forEach items="${images}" var="image">    
				<div class="col-sm-3">
					<div class="list-item list-img">
						<a class="fancybox-buttons" data-fancybox-group="button">
							<img src="${image}" alt=""></a>
						<span class="icon-img"></span>
					</div>
				</div>
				</c:forEach>
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