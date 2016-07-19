<%@ include file="menu.jsp"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<div class="container">

	<div id="contentdiv" class="content">



		<div class="page-header">
			<h1>Test new video stream</h1>
		</div>

		<div id="alert_placeholder"></div>

		<div id="cameradiv" class="nonvisible" style="display: block;">
			<p>To add a new camera, just enter the URL of the RTSP/HTTP video
				stream. Example:</p>
			<pre id="codediv">rtsp://user:pass@mydomain.com:554/h264
http://user:pass@mydomain.com/mjpeg</pre>
			<p>
				<b>ONVIF Camera Discovery Tool:</b> If you do not know the proper
				URL of your stream <a
					href="https://ipcamlive.com/discovercameraprofiles">this tool</a>
				can help you to discover the capabilities of your camera using ONVIF
				protocol.
			</p>
			<p>
				<b>How-to video:</b> If you need assistance for adding your video
				stream, you can watch <a href="http://youtu.be/x_GFrOYfqqU"
					target="_blank">this video</a> which goes through the necessary
				streps.
			</p>

			<p>
				You can find useful information about adding new video stream on our
				<a href="https://ipcamlive.com/faqs">FAQ</a> page.
			</p>

			<br>

			<form action="https://ipcamlive.com/streamtest#" method="post"
				id="cameraform" class="form-horizontal" novalidate="novalidate">
				<div id="urldiv" class="form-group">
					<label id="urltextdiv" class="col-sm-2 control-label">URL
						of your camera:</label>
					<div class="col-sm-4">
						<input type="text" id="urledit" name="urledit"
							class="form-control">
					</div>
				</div>
				<div class="form-group">
					<div class="col-sm-offset-2 col-sm-4">
						<input type="submit" value="Test" id="submitbutton"
							class="btn btn-primary pull-left">
						<div id="formprocessdiv" class="loading pull-left"
							style="display: none;"></div>
					</div>
				</div>
			</form>
		</div>

		<br>
		<div id="videodiv" class="nonvisible">
			<table class="table table-striped">
				<tbody>
					<tr>
						<td class="streamtestvideo">
							<div id="playerdiv" class="streamtestvideo"></div>
						</td>
						<td>
							<p></p>
							<h4>Stream info:</h4> <b>Protocol:</b> <span id="protocoltext"></span><br>
							<b>Compression:</b> <span id="compressiontext"></span><br> <b>Resolution:</b>
							<span id="resolutiontext"></span><br> <b>Frame rate:</b> <span
							id="frameratetext"></span><br> <b>Bandwidth:</b> <span
							id="bandwidthtext"></span><br>
							<p></p> <br>

							<button id="addbutton" class="btn btn-success pull-left">Add
								camera</button>
							<div id="addprocessdiv" class="loading pull-left"
								style="display: none;"></div>
						</td>
					</tr>
				</tbody>
			</table>
		</div>
	</div>

</div>

</div>

<footer class="footer">
	<div class="container">
		<p class="credit pull-right hidden-xs">
			<a href="https://ipcamlive.com/streamtest#">Back to top</a>
		</p>
		<p class="credit">
			Copyright © 2016 IPCamLive Inc. · <a
				href="https://ipcamlive.com/contact">Contact the IPCamLive team</a>
			· <a href="https://ipcamlive.com/howdoesitwork">How does it work?</a>·
			<a href="https://ipcamlive.com/pricing">Pricing</a>· <a
				href="https://ipcamlive.com/faqs">FAQs</a>
		</p>
	</div>
</footer>



</body>
</html>