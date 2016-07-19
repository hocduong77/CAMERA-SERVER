<%@ include file="menu.jsp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
	<div class="container">

		<div id="contentdiv" class="content">
					
<script type="text/javascript">

var isCamerasLoaded = false;
var loadingtimeout;

$(document).ready(function() {
	$('#newcamerabutton').click(function() {
		document.location.href='addcamera';
	});

	LoadCameras();

	loadingtimeout = setTimeout(function() {
		$('#loadingdiv').show();
	}, 500);
});


</script>


<div id="alert_placeholder"></div>

<div class="page-header camerapage-header">
	<h1>Cameras</h1>
</div>

<div id="loadingdiv" class="loading nonvisible" style="display: none;"></div>

<div id="camerasdiv" class="table-responsive nonvisible" style="display: block;">
	<table id="camerastable" class="table table-striped">
		<thead>
			<tr>
				<th>Alias</th>
				<th>Name</th>
				<th class="hidden-xs hidden-sm">Url</th>
				<th>Enabled</th>
				<th>Online</th>
				<th></th>
			</tr>
		</thead>
		<tbody><tr><td id="camerastable_28209_alias">566c66068ae0c</td><td id="camerastable_28209_name" class="hidden-xs hidden-sm">566c66068ae0c</td><td id="camerastable_28209_url" class="hidden-xs hidden-sm">rtsp://113.162.208.78:554/live.sdp</td><td id="camerastable_28209_enabled"><span class="glyphicon glyphicon-remove" aria-hidden="true"></span></td><td id="camerastable_28209_streamavailable"><span class="glyphicon glyphicon-remove" aria-hidden="true"></span></td><td class="rightcell"><a href="https://ipcamlive.com/camerapage?id=28209" class="btn btn-default" title="Camera page"><span class="glyphicon glyphicon-cog" aria-hidden="true"></span></a></td></tr></tbody>
	</table>
</div>

<button id="newcamerabutton" class="btn btn-success nonvisible" style="display: inline-block;">New camera</button><br><br>				</div>

	</div>

</div>

	<footer class="footer">
	<div class="container">
		<p class="credit pull-right hidden-xs"><a href="https://ipcamlive.com/cameras#">Back to top</a></p>
		<p class="credit">Copyright © 2016 IPCamLive Inc. ·
			<a href="https://ipcamlive.com/contact">Contact the IPCamLive team</a> ·
			<a href="https://ipcamlive.com/howdoesitwork">How does it work?</a>·
			<a href="https://ipcamlive.com/pricing">Pricing</a>·
			<a href="https://ipcamlive.com/faqs">FAQs</a>
		</p>
	</div>
</footer>
	<script src="./cameramenu_files/bootstrap.min.js"></script>


</body></html>