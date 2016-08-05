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
				
				<th></th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${cameras}" var="camera">    
		<tr><td id="camerastable_28209_alias"><c:out value="${camera.alias}"/></td><td id="camerastable_28209_name" class="hidden-xs hidden-sm"><c:out value="${camera.name}"/></td><td id="camerastable_28209_url" class="hidden-xs hidden-sm"><c:out value="${camera.cameraUrl}"/></td><td id="camerastable_28209_enabled"><span class="glyphicon glyphicon-remove" aria-hidden="true"></span></td><td class="rightcell"><a href="<c:url value="/setting/${camera.cameraId}" />" class="btn btn-default" title="Camera page"><span class="glyphicon glyphicon-cog" aria-hidden="true"></span></a></td></tr>
		</c:forEach>
		</tbody>
	</table>
</div>

<button id="newcamerabutton" class="btn btn-success nonvisible" style="display: inline-block;">New camera</button><br><br>				</div>

	</div>

</div>



</body></html>