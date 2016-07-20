<%@ include file="menu.jsp"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


	<div class="container">

		<div id="contentdiv" class="content">
					<script type="text/javascript">
	var readonlymode = 0;
	var token = '89a75ef8';
	var url = '';
</script>


<script type="text/javascript">
var cameraid = -1;
var alias = "";
var connecttime = 0;

$(document).ready(function() {
	jQuery.validator.addMethod("url", function(value, element) {
		var url = /^((http:\/\/)|(rtsp:\/\/)){1}.+$/i;

		return (url.test(value));
	}, "Enter a valid RTSP or HTTP stream URL.");

	$('#cameradiv').fadeIn();

	$('#formprocessdiv').hide();
	$('#addprocessdiv').hide();
	
	$('#urledit').val(url);

	$('#cameraform').submit(function() {

		console.log("submitbutton");
		$('#videodiv').show();
 	 	$('#playerdiv').html('<iframe width="700" height="500" src="https://www.youtube.com/embed/An2a1_Do_fc" frameborder="0" allowfullscreen></iframe>');

		return false;
	});

	$('#addbutton').button().click(function() {
		$('#addbutton').hide();
		$('#addprocessdiv').show();

		var data = {
			cameraid: cameraid,
			state: 'A'
		};

		$.get('ajax/modifycamerastate.php', data, function(data){
			if (data.result == 'ok') {
				document.location.href='newcameraservicetype?id=' + cameraid;
			} else {
				$('#addbutton').show();
				$('#addprocessdiv').add();

				bootstrap_alert.error('Cannot add new camera.');
			}
		}, 'json');

		return false;
	});

 });




function WaitForStreamBuffered() {
	setTimeout( function() {
 		log('WaitForStreamBuffered: check');
		$.get('ajax/getcamerastreamstate.php', {cameraalias: alias}, function(data) {
			if ((typeof data.streaminfo != 'undefined') && (data.streaminfo.live.segmentcount >= 3)) {
	 				log('WaitForStreamBuffered: embed');
	 				
	 				var aspectratio = data.streaminfo.video.width / data.streaminfo.video.height;
					var width = 700;
					var height = width / aspectratio; 
	 				
	 	 			$('#playerdiv').html('<iframe src="player/player.php?alias={0}&autoplay=1&disablevideofit=1&token={1}" width="{2}px" height="{3}px"/>'.format(alias, token, width, height));

	 	 			CheckStreamInfo();

	 	 			// Close the stream after 4 min
					setTimeout( function() {
						document.location.href='streamtest';
					}, 4 * 60 * 1000);
	 		} else {
	 			connecttime += 1000;

	 			if (connecttime < 60000) {
	 				WaitForStreamBuffered();
	 			} else {
	 				$('#videodiv').hide();
	 				$('#submitbutton').show();
	 				$('#formprocessdiv').hide();

	 				bootstrap_alert.error('Video stream cannot be started. Read more <a href="faqs">here</a>.');
	 			}
			}
 		}, 'json');
 	}, 1000);
}


</script>


<div class="page-header">
	<h1>Test new video stream</h1>
</div>

<div id="alert_placeholder"></div>

<div id="cameradiv" class="nonvisible" style="display: block;">
	<p>To add a new camera, just enter the URL of the RTSP/HTTP video stream. Example:</p>
		<pre id="codediv">rtsp://user:pass@mydomain.com:554/h264
http://user:pass@mydomain.com/mjpeg</pre>
	<p><b>ONVIF Camera Discovery Tool:</b> If you do not know the proper URL of your stream <a href="https://ipcamlive.com/discovercameraprofiles">this tool</a> can help you to discover the capabilities of your camera using ONVIF protocol.</p>
	<p><b>How-to video:</b> If you need assistance for adding your video stream, you can watch <a href="http://youtu.be/x_GFrOYfqqU" target="_blank">this video</a> which goes through the necessary streps.</p>

	<p>You can find useful information about adding new video stream on our <a href="https://ipcamlive.com/faqs">FAQ</a> page.</p>

	<br>

	<form action="https://ipcamlive.com/streamtest#" method="post" id="cameraform" class="form-horizontal" novalidate="novalidate">
		<div id="urldiv" class="form-group">
			<label id="urltextdiv" class="col-sm-2 control-label">URL of your camera:</label>
			<div class="col-sm-4">
				<input type="text" id="urledit" name="urledit" class="form-control error"><label for="urledit" class="error">This field is required.</label>
			</div>
		</div>
		<div class="form-group">
			<div class="col-sm-offset-2 col-sm-4">
				<input type="submit" value="Test" id="submitbutton" class="btn btn-primary pull-left">
				<div id="formprocessdiv" class="loading pull-left" style="display: none;"></div>
			</div>
		</div>
	</form>
</div>

<br>
<div id="videodiv" class="nonvisible">
	<table class="table table-striped">
		<tbody><tr>
			<td class="streamtestvideo">
				<div id="playerdiv" class="streamtestvideo"></div>
			</td>
			<td>
				<br>

				<button id="addbutton" class="btn btn-success pull-left">Add camera</button>
				<div id="addprocessdiv" class="loading pull-left" style="display: none;"></div>
			</td>
		</tr>
	</tbody></table>
</div>				
</div>

</div>

</body></html>