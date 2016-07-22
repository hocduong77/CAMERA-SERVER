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
	
	//$('#urledit').val(url);

	$('#cameraform').submit(function() {
		var cameraurl = $('#urledit').val();
		console.log("url " + cameraurl);
		$.ajax({
			type: "post",
			url : 'test' , 
			data:'url=' + cameraurl ,
			success : function(data) {
				var str = data;
				console.log("data" +data);
				$('#videodiv').show();
		 	 	$('#playerdiv').html(data);
			}
		});
	
		

		return false;
	});

	$('#addbutton').button().click(function() {
		$('#addbutton').hide();
		$('#addprocessdiv').show();
		var url = $('#urledit').val();
		console.log("addbutton url " + url);
		
		$.ajax({								
			url : 'saveCamera/' + url ,
			success : function(data) {
				var str = data;
				console.log("data" +data);
				$('#videodiv').show();
		 	 	$('#playerdiv').html(data);
			}
		});
/* 		var data = {
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
		}, 'json'); */

		return false;
	});

 });


</script>


<div class="page-header">
	<h1>Test new video stream</h1>
</div>

<div id="alert_placeholder"></div>

<div id="cameradiv" class="nonvisible" style="display: block;">
	<p>To add a new camera, just enter the URL of the RTSP/HTTP video stream. Example:</p>
		<pre id="codediv">rtsp://user:pass@mydomain.com:554/h264
http://user:pass@mydomain.com/mjpeg</pre>


	<br>

	<form action="/" method="post" id="cameraform" class="form-horizontal" novalidate="novalidate">
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
				
				<!-- <button id="addbutton" class="btn btn-success pull-left">Add camera</button> -->
				
			</td>
		</tr>
		<tr>
				<form action="<c:url value="/saveCamera" />" method="get" id="cameraform" class="form-horizontal" novalidate="novalidate">
				<input type="submit" value="Add camera"  class="btn btn-primary pull-left">
				<div id="addprocessdiv" class="loading pull-left" style="display: none;"></div>
				</form>
		</tr>
	</tbody></table>
</div>				
</div>

</div>

</body></html>