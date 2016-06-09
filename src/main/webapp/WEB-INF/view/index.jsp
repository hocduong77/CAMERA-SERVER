<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="true"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<html lang="en"><head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	
	<title>Embedding IP Camera Live Video Stream in web page - IPCamLive.com</title>
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<meta name="keywords" content="ip camera, network camera, embed ip camera, embed ip camera into webpage, embed ip camera stream in web page, embed network camera on website">
	<meta name="description" content="Video streaming solution for embedding your IP camera live video stream in web page.">
	<meta name="author" content="">

	<link href="./index_files/bootstrap.min.css" rel="stylesheet">
	<!--[if lt IE 9]>
	<script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
	<script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
	<![endif]-->

	<link href="./index_files/jquery-ui-1.10.0.custom.min.css" rel="stylesheet">
	<link href="./index_files/jquery.validate.css" rel="stylesheet">
	<link href="./index_files/jquery.datetimepicker.css" rel="stylesheet">
	<link href="./index_files/skin.css" rel="stylesheet">

	<link rel="shortcut icon" href="https://ipcamlive.com/tpls/images/ipcamlive_favicon_128.png">

	<script async="" src="./index_files/analytics.js"></script><script src="./index_files/jquery-1.11.2.min.js"></script>
	<script src="./index_files/jquery-ui-1.10.0.custom.min.js"></script>

	<script src="./index_files/mybootstrap.js"></script>

	<script src="./index_files/md5.js"></script>

	<script src="./index_files/highcharts.js"></script>
	<script src="./index_files/exporting.js"></script>
	<script src="./index_files/data.js"></script>

	<script src="./index_files/jquery.validate.min.js"></script>
	<script src="./index_files/jquery.datetimepicker.js"></script>
	
	<script src="./index_files/ajaxfileupload.js"></script>

	<script type="text/javascript" src="./index_files/cookie.js"></script>

	<script type="text/javascript" src="./index_files/he.js"></script>

	<script type="text/javascript" src="./index_files/xutils.js"></script>

	
	<script>
		(function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
			(i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
			m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
		})(window,document,'script','//www.google-analytics.com/analytics.js','ga');

		ga('create', 'UA-46297675-2', 'ipcamlive.com');
		ga('send', 'pageview');

	</script>
	

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
				<a class="navbar-brand" href="https://ipcamlive.com/"><img src="./index_files/index-logo.png" alt="ipcamlive"></a>
			</div>
			<div id="navbar" class="navbar-collapse collapse">
									<ul class="nav navbar-nav">
	<li class="active"><a href="https://ipcamlive.com/main">Dashboard</a></li>
	<li><a href="https://ipcamlive.com/cameras">Cameras</a></li>
	<li><a href="https://ipcamlive.com/accountstatus">Account</a></li>
	<li><a href="https://ipcamlive.com/finance">Finance</a></li>
</ul>
<ul class="nav navbar-nav navbar-right">
	<li class="dropdown">
		<a href="https://ipcamlive.com/userprofile" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">hoc duong <span class="caret"></span></a>
		<ul class="dropdown-menu" role="menu">
			<li><a href="https://ipcamlive.com/userprofile">Profile</a></li>
			<li><a href="https://ipcamlive.com/changepassword">Change password</a></li>
			<li><a href="https://ipcamlive.com/logout">Logout</a></li>
		</ul>
	</li>
</ul>							</div><!--/.nav-collapse -->
		</div><!--/.container-fluid -->
	</nav>

	<div class="container">

		<div id="contentdiv" class="content">
					
<script type="text/javascript">

var loadingtimeout;

$(document).ready(function() {
	$.get('ajax/getdashboard.php', {}, function(data){
		clearTimeout(loadingtimeout);
		
		$('#loadingdiv').hide();
		$('#dashboarddiv').fadeIn();
	
		if (data.account.accounttype == 'D')
			bootstrap_alert.error('Your account and your cameras are disabled. Please complete your account information <a href="accountsettings">here</a>.');
	
		var cameras = data.cameras;
	
		var activecameras = 0;
		var onlinecameras = 0;
		for (var i = 0; i < cameras.length; i++) {
			if (cameras[i].enabled == true)
				activecameras++;
			if (cameras[i].streamavailable == true)
				onlinecameras++;
		}
	
		$('#activecamerastext').text(activecameras);
		$('#onlinecamerastext').text(onlinecameras);
	
		var xaxis = [];
		var values = [];
		var l = data.camerasstat.length;
		for (var i = 0; i < l; i++) {
	
			var daysstat = data.camerasstat[i].days;
	
			var stat = [];
	
			var ll = daysstat.length;
			for (var j = 0; j < ll; j++) {
				if (i == 0)
					xaxis.push(daysstat[j].date);
	
				stat.push(parseInt(daysstat[j].count));
			}
	
			values.push({name: data.camerasstat[i].name, data: stat});
		}
	
		$('#charts').highcharts({
	        title: {
	            text: 'View statistics',
	            x: -20 //center
	        },
	        xAxis: {
	            categories: xaxis
	        },
	        yAxis: {
	            title: {
	                text: 'Viewers'
	            },
	            plotLines: [{
	                value: 0,
	                width: 1,
	                color: '#808080'
	            }]
	        },
	        legend: {
	            layout: 'vertical',
	            align: 'right',
	            verticalAlign: 'middle',
	            borderWidth: 0
	        },
	        series: values
	    });
	}, "json");
	
	loadingtimeout = setTimeout(function() {
		$('#loadingdiv').show();
	}, 500);
});
</script>


<div id="alert_placeholder"></div>

<div class="page-header">
	<h1>Dashboard</h1>
</div>

<div id="loadingdiv" class="loading nonvisible" style="display: none;"></div>

<div id="dashboarddiv" class="nonvisible" style="display: block;">
	<ul class="list-group col-sm-3">
		<li class="list-group-item">
			<span class="badge"><span id="activecamerastext">0</span></span>
			Active cameras
		</li>
		<li class="list-group-item">
			<span class="badge"><span id="onlinecamerastext">0</span></span>
			Online cameras
		</li>
	</ul>
</div>

<div class="clearfix"></div>

<div id="charts" class="dashboardcharts hidden-xs" data-highcharts-chart="0"><div class="highcharts-container" id="highcharts-0" style="position: relative; overflow: hidden; width: 1140px; height: 400px; text-align: left; line-height: normal; z-index: 0; -webkit-tap-highlight-color: rgba(0, 0, 0, 0); font-family: &#39;Lucida Grande&#39;, &#39;Lucida Sans Unicode&#39;, Verdana, Arial, Helvetica, sans-serif; font-size: 12px;"><svg version="1.1" xmlns="http://www.w3.org/2000/svg" width="1140" height="400"><desc>Created with Highcharts 3.0.9</desc><defs><clippath id="highcharts-1"><rect fill="none" x="0" y="0" width="944" height="328"></rect></clippath></defs><rect rx="5" ry="5" fill="#FFFFFF" x="0" y="0" width="1140" height="400"></rect><path fill="none" d="M 53 204.5 L 997 204.5" stroke="#808080" stroke-width="1"></path><g class="highcharts-button" style="cursor:default;" title="Chart context menu" stroke-linecap="round" transform="translate(1106,10)"><title>Chart context menu</title><rect rx="2" ry="2" fill="white" x="0.5" y="0.5" width="24" height="22" stroke="none" stroke-width="1"></rect><path fill="#E0E0E0" d="M 6 6.5 L 20 6.5 M 6 11.5 L 20 11.5 M 6 16.5 L 20 16.5" stroke="#666" stroke-width="3" zIndex="1"></path><text x="0" y="13" style="font-family:&quot;Lucida Grande&quot;, &quot;Lucida Sans Unicode&quot;, Verdana, Arial, Helvetica, sans-serif;font-size:12px;color:black;fill:black;" zIndex="1"></text></g><g class="highcharts-grid" zIndex="1"></g><g class="highcharts-grid" zIndex="1"><path fill="none" d="M 53 204.5 L 997 204.5" stroke="#C0C0C0" stroke-width="1" zIndex="1" opacity="1"></path></g><g class="highcharts-axis" zIndex="2"><path fill="none" d="M 187.5 368 L 187.5 373" stroke="#C0D0E0" stroke-width="1" opacity="1"></path><path fill="none" d="M 322.5 368 L 322.5 373" stroke="#C0D0E0" stroke-width="1" opacity="1"></path><path fill="none" d="M 457.5 368 L 457.5 373" stroke="#C0D0E0" stroke-width="1" opacity="1"></path><path fill="none" d="M 591.5 368 L 591.5 373" stroke="#C0D0E0" stroke-width="1" opacity="1"></path><path fill="none" d="M 726.5 368 L 726.5 373" stroke="#C0D0E0" stroke-width="1" opacity="1"></path><path fill="none" d="M 861.5 368 L 861.5 373" stroke="#C0D0E0" stroke-width="1" opacity="1"></path><path fill="none" d="M 997.5 368 L 997.5 373" stroke="#C0D0E0" stroke-width="1" opacity="1"></path><path fill="none" d="M 52.5 368 L 52.5 373" stroke="#C0D0E0" stroke-width="1"></path><path fill="none" d="M 53 368.5 L 997 368.5" stroke="#C0D0E0" stroke-width="1" zIndex="7" visibility="visible"></path></g><g class="highcharts-axis" zIndex="2"><text x="28.046875" y="204" style="font-family:&quot;Lucida Grande&quot;, &quot;Lucida Sans Unicode&quot;, Verdana, Arial, Helvetica, sans-serif;font-size:12px;color:#4d759e;font-weight:bold;fill:#4d759e;" zIndex="7" text-anchor="middle" transform="translate(0,0) rotate(270 28.046875 204)" visibility="visible"><tspan x="28.046875">Viewers</tspan></text></g><g class="highcharts-series-group" zIndex="3"><g class="highcharts-series" visibility="visible" zIndex="0.1" transform="translate(53,40) scale(1 1)" clip-path="url(#highcharts-1)"><path fill="none" d="M 67.42857142857143 164 L 202.28571428571428 164 L 337.14285714285717 164 L 472 164 L 606.8571428571429 164 L 741.7142857142858 164 L 876.5714285714286 164" stroke="#2f7ed8" stroke-width="2" zIndex="1" stroke-linejoin="round" stroke-linecap="round"></path><path fill="none" d="M 57.42857142857143 164 L 67.42857142857143 164 L 202.28571428571428 164 L 337.14285714285717 164 L 472 164 L 606.8571428571429 164 L 741.7142857142858 164 L 876.5714285714286 164 L 886.5714285714286 164" stroke-linejoin="round" visibility="visible" stroke-opacity="0.0001" stroke="rgb(192,192,192)" stroke-width="22" zIndex="2" class=" highcharts-tracker" style=""></path></g><g class="highcharts-markers highcharts-tracker" visibility="visible" zIndex="0.1" transform="translate(53,40) scale(1 1)" clip-path="none" style=""><path fill="#2f7ed8" d="M 876 160 C 881.328 160 881.328 168 876 168 C 870.672 168 870.672 160 876 160 Z"></path><path fill="#2f7ed8" d="M 741 160 C 746.328 160 746.328 168 741 168 C 735.672 168 735.672 160 741 160 Z"></path><path fill="#2f7ed8" d="M 606 160 C 611.328 160 611.328 168 606 168 C 600.672 168 600.672 160 606 160 Z"></path><path fill="#2f7ed8" d="M 472 160 C 477.328 160 477.328 168 472 168 C 466.672 168 466.672 160 472 160 Z"></path><path fill="#2f7ed8" d="M 337 160 C 342.328 160 342.328 168 337 168 C 331.672 168 331.672 160 337 160 Z"></path><path fill="#2f7ed8" d="M 202 160 C 207.328 160 207.328 168 202 168 C 196.672 168 196.672 160 202 160 Z"></path><path fill="#2f7ed8" d="M 67 160 C 72.328 160 72.328 168 67 168 C 61.672 168 61.672 160 67 160 Z"></path></g></g><text x="550" y="25" style="font-family:&quot;Lucida Grande&quot;, &quot;Lucida Sans Unicode&quot;, Verdana, Arial, Helvetica, sans-serif;font-size:16px;color:#274b6d;fill:#274b6d;" text-anchor="middle" class="highcharts-title" zIndex="4"><tspan x="550">View statistics</tspan></text><g class="highcharts-legend" zIndex="7" transform="translate(1007,187)"><g zIndex="1"><g><g class="highcharts-legend-item" zIndex="1" transform="translate(8,3)"><path fill="none" d="M 0 11 L 16 11" stroke="#2f7ed8" stroke-width="2"></path><path fill="#2f7ed8" d="M 8 7 C 13.328 7 13.328 15 8 15 C 2.6719999999999997 15 2.6719999999999997 7 8 7 Z"></path><text x="21" y="15" style="font-family:&quot;Lucida Grande&quot;, &quot;Lucida Sans Unicode&quot;, Verdana, Arial, Helvetica, sans-serif;font-size:12px;cursor:pointer;color:#274b6d;fill:#274b6d;" text-anchor="start" zIndex="2"><tspan x="21">566c66068ae0c</tspan></text></g></g></g></g><g class="highcharts-axis-labels" zIndex="7"><text x="120.42857142857143" y="382" style="font-family:&quot;Lucida Grande&quot;, &quot;Lucida Sans Unicode&quot;, Verdana, Arial, Helvetica, sans-serif;font-size:11px;color:#666;cursor:default;fill:#666;" text-anchor="middle" opacity="1"><tspan x="120.42857142857143">2016-05-30</tspan></text><text x="255.28571428571428" y="382" style="font-family:&quot;Lucida Grande&quot;, &quot;Lucida Sans Unicode&quot;, Verdana, Arial, Helvetica, sans-serif;font-size:11px;color:#666;cursor:default;fill:#666;" text-anchor="middle" opacity="1"><tspan x="255.28571428571428">2016-05-31</tspan></text><text x="390.14285714285717" y="382" style="font-family:&quot;Lucida Grande&quot;, &quot;Lucida Sans Unicode&quot;, Verdana, Arial, Helvetica, sans-serif;font-size:11px;color:#666;cursor:default;fill:#666;" text-anchor="middle" opacity="1"><tspan x="390.14285714285717">2016-06-01</tspan></text><text x="525" y="382" style="font-family:&quot;Lucida Grande&quot;, &quot;Lucida Sans Unicode&quot;, Verdana, Arial, Helvetica, sans-serif;font-size:11px;color:#666;cursor:default;fill:#666;" text-anchor="middle" opacity="1"><tspan x="525">2016-06-02</tspan></text><text x="659.8571428571429" y="382" style="font-family:&quot;Lucida Grande&quot;, &quot;Lucida Sans Unicode&quot;, Verdana, Arial, Helvetica, sans-serif;font-size:11px;color:#666;cursor:default;fill:#666;" text-anchor="middle" opacity="1"><tspan x="659.8571428571429">2016-06-03</tspan></text><text x="794.7142857142858" y="382" style="font-family:&quot;Lucida Grande&quot;, &quot;Lucida Sans Unicode&quot;, Verdana, Arial, Helvetica, sans-serif;font-size:11px;color:#666;cursor:default;fill:#666;" text-anchor="middle" opacity="1"><tspan x="794.7142857142858">2016-06-04</tspan></text><text x="929.5714285714286" y="382" style="font-family:&quot;Lucida Grande&quot;, &quot;Lucida Sans Unicode&quot;, Verdana, Arial, Helvetica, sans-serif;font-size:11px;color:#666;cursor:default;fill:#666;" text-anchor="middle" opacity="1"><tspan x="929.5714285714286">2016-06-05</tspan></text></g><g class="highcharts-axis-labels" zIndex="7"><text x="45" y="207.5" style="font-family:&quot;Lucida Grande&quot;, &quot;Lucida Sans Unicode&quot;, Verdana, Arial, Helvetica, sans-serif;font-size:11px;color:#666;cursor:default;fill:#666;" text-anchor="end" opacity="1"><tspan x="45">0</tspan></text></g><g class="highcharts-tooltip" zIndex="8" style="cursor:default;padding:0;white-space:nowrap;" transform="translate(0,-999)"><rect rx="3" ry="3" fill="none" x="0.5" y="0.5" width="16" height="16" fill-opacity="0.85" isShadow="true" stroke="black" stroke-opacity="0.049999999999999996" stroke-width="5" transform="translate(1, 1)"></rect><rect rx="3" ry="3" fill="none" x="0.5" y="0.5" width="16" height="16" fill-opacity="0.85" isShadow="true" stroke="black" stroke-opacity="0.09999999999999999" stroke-width="3" transform="translate(1, 1)"></rect><rect rx="3" ry="3" fill="none" x="0.5" y="0.5" width="16" height="16" fill-opacity="0.85" isShadow="true" stroke="black" stroke-opacity="0.15" stroke-width="1" transform="translate(1, 1)"></rect><rect rx="3" ry="3" fill="rgb(255,255,255)" x="0.5" y="0.5" width="16" height="16" fill-opacity="0.85"></rect><text x="8" y="21" style="font-family:&quot;Lucida Grande&quot;, &quot;Lucida Sans Unicode&quot;, Verdana, Arial, Helvetica, sans-serif;font-size:12px;color:#333333;fill:#333333;" zIndex="1"></text></g><text x="1130" y="395" style="font-family:&quot;Lucida Grande&quot;, &quot;Lucida Sans Unicode&quot;, Verdana, Arial, Helvetica, sans-serif;font-size:9px;cursor:pointer;color:#909090;fill:#909090;" text-anchor="end" zIndex="8"><tspan x="1130">Highcharts.com</tspan></text></svg></div></div>
				</div>

	</div>

</div>

	<footer class="footer">
	<div class="container">
		<p class="credit pull-right hidden-xs"><a href="https://ipcamlive.com/#">Back to top</a></p>
		<p class="credit">Copyright © 2016 IPCamLive Inc. ·
			<a href="https://ipcamlive.com/contact">Contact the IPCamLive team</a> ·
			<a href="https://ipcamlive.com/howdoesitwork">How does it work?</a>·
			<a href="https://ipcamlive.com/pricing">Pricing</a>·
			<a href="https://ipcamlive.com/faqs">FAQs</a>
		</p>
	</div>
</footer>
	<script src="./index_files/bootstrap.min.js"></script>


</body></html>