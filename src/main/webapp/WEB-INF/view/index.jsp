<%@ include file="header.jsp" %>

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
				<a class="navbar-brand" href="<c:url value="/"/>">
				<img src="<c:url value="/resources/index_files/index-logo.png"/>" alt="Camera Server">
				</a>
			</div>
			<div id="navbar" class="navbar-collapse collapse">
									<ul class="nav navbar-nav navbar-right">
	<li><a href="<c:url value="/login.html" />">Login</a></li>
</ul>							</div><!--/.nav-collapse -->
		</div><!--/.container-fluid -->
	</nav>

	<div class="container">

		<div id="contentdiv" class="content">
					
<script type="text/javascript">

	function findBootstrapEnvironment() {
		var envs = ['xs', 'sm', 'md', 'lg'];

		$el = $('<div>');
		$el.appendTo($('body'));

		for (var i = envs.length - 1; i >= 0; i--) {
			var env = envs[i];

			$el.addClass('hidden-'+env);
			if ($el.is(':hidden')) {
				$el.remove();
				return env
			}
		};
	}

	function setHeaderHeight() {
		env = findBootstrapEnvironment();
		if ( env == 'xs' ) {
			$('.index-head-bg').height( $('.register').offset().top+$('.register').outerHeight(true) + 16 );
		} else {
			$('.index-head-bg').height(450);
		}
	}

	$(document).ready(function() {
		setHeaderHeight();
	});

	$(window).bind('resize', function() {
		setHeaderHeight();
	});

</script>


<div class="container-fluid home">

	<div class="index-head-bg" style="height: 450px;"></div>

	<div class="texts"><h1>Cloud based video broadcasting<br>solution for IP cameras </h1></div>

	<div class="row">

		<div class="col-xs-12 col-sm-6 col-md-6 col-lg-6">

			<div class="bullets">
				<ul>
					<h2><li>Easiest way to embed live video in your web page!</li></h2>
					<h2><li>Generate time-lapse video online!</li></h2>
					<h3><li><a href="<c:url value="/howdoesitwork" />">How does it work?</a></li></h3>
					<li class="register"><a href="<c:url value="/user/registration" />" class="btn btn-danger btn-lg btn-block" role="button">Sign up FREE!</a></li>
				</ul>
				
			</div>
		</div>

		<div class="hidden-xs col-xs-1 col-sm-6 col-md-6 col-lg-6">
			<div class="devices"><img src="<c:url value="/resources/index_files/index-devices.png"/>" class="img-responsive"></div>
		</div>

	</div>


	<div class="home-content">


		<h3>Main features:</h3>
<applet code="school.camera.validation.service.HelloWorldApplet.class" width="320" height="120">
If your browser was Java-enabled, a "Hello, World"
message would appear here.
</applet>
		<div class="row">

			<div class="col-xs-12 col-xsp-6 col-sm-6 col-md-4 col-lg-4">
				<div class="popover">
					<h4 class="popover-title"><i class="icon-ok icon-white"></i> Display video on any platform</h4>
					<div class="popover-content">
						<p>Camera Server provides video player component compatible with most platforms (PC, MAC, mobile, tablet, ect.).</p>
					</div>
				</div>
			</div>

			<div class="col-xs-12 col-xsp-6 col-sm-6 col-md-4 col-lg-4">
				<div class="popover">
					<h4 class="popover-title"><i class="icon-ok icon-white"></i> Easy integration to web page</h4>
					<div class="popover-content">
						<p>Camera Server generates the necessary HTML snippet for embedding the video in the HTML page.</p>
					</div>
				</div>
			</div>


			<div class="col-xs-12 col-xsp-6 col-sm-6 col-md-4 col-lg-4">
				<div class="popover">
					<h4 class="popover-title"><i class="icon-ok icon-white"></i> ONVIF support</h4>
					<div class="popover-content">
						<p>Using ONVIF protocol it's easy to find the proper video stream of your camera. </p>
					</div>
				</div>
			</div>

			<div class="col-xs-12 col-xsp-6 col-sm-6 col-md-4 col-lg-4">
				<div class="popover">
					<h4 class="popover-title"><i class="icon-ok icon-white"></i> Unlimited viewers</h4>
					<div class="popover-content">
						<p>Camera Server can serve as many viewers as you have.</p>
					</div>
				</div>
			</div>

			<div class="col-xs-12 col-xsp-6 col-sm-6 col-md-4 col-lg-4">
				<div class="popover">
					<h4 class="popover-title"><i class="icon-ok icon-white"></i> HD Video stream support</h4>
					<div class="popover-content">
						<p>Camera Server can receive and display high resolution video streams (eg. Full HD).</p>
					</div>
				</div>
			</div>

			<div class="col-xs-12 col-xsp-6 col-sm-6 col-md-4 col-lg-4">
				<div class="popover">
					<h4 class="popover-title"><i class="icon-ok icon-white"></i> Privacy control</h4>
					<div class="popover-content">
						<p>Camera Server can blur the motion part of the image in order to make the faces unrecognizable on the video streams.</p>
					</div>
				</div>
			</div>

			<div class="col-xs-12 col-xsp-6 col-sm-6 col-md-4 col-lg-4">
				<div class="popover">
					<h4 class="popover-title"><i class="icon-ok icon-white"></i> Audio support</h4>
					<div class="popover-content">
						<p>Camera Server can also broadcast the audio together with the video.</p>
					</div>
				</div>
			</div>


			<div class="col-xs-12 col-xsp-6 col-sm-6 col-md-4 col-lg-4">
				<div class="popover">
					<h3 class="popover-title"><i class="icon-ok icon-white"></i> RTSP/H.264 support</h3>
					<div class="popover-content">
						<p>Camera Server supports RTSP video streaming protocol and can handle H.264 video compression.</p>
					</div>
				</div>
			</div>


		</div>

	</div>

	<div class="clearfix"></div>

</div>				</div>

	</div>

</div>


	<script src="<c:url value="/resources/index_files/bootstrap.min.js"/>"></script>


</body></html>