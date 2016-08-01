<%@ include file="menu.jsp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

	<div class="container">

		<div id="contentdiv" class="content">
					<script type="text/javascript">
	var readonlymode = 0;
	var cameraid='28209';
</script>


<script type="text/javascript">

var $servicetypeid = "";
var $logofilename = "";
var loadingtimeout;

$(document).ready(function() {
	$('#formprocessdiv').hide();
	$('#logouploadprocessdiv').hide();
	
	jQuery.validator.addMethod("nospace", function(value, element) {
			return value.indexOf(" ") < 0 && value != "";
		}, "No space please and don't leave it empty.");
	
	jQuery.validator.addMethod("noextrachars", function(value, element) {
		return value.match(/^[a-z0-9]+$/);
	}, "Only lowercase letters and numbers allowed.");
	
	jQuery.validator.addMethod("url", function(value, element) {
		var url = /^((http:\/\/)|(rtsp:\/\/)){1}.+$/i;
	
		return (url.test(value));
	}, "Enter a valid RTSP or HTTP stream URL.");
	
	
	
	
	$('#cameraform').submit(function() {
		if ($('#cameraform').valid()) {
			$('#submitbutton').hide();
			$('#formprocessdiv').show();
	
			var logoenabled = Number($('#logoenabledcheckbox').prop('checked'));
			
			var data = {
				cameraid: cameraid,
				alias: $('#aliasedit').val(),
				name: $('#nameedit').val(),
				url: $('#urledit').val(),
				servicetypeid: $('#servicetypeselect').val(),

				logoenabled: logoenabled,
				logofilename: logofilename,
				logopos: $('#logoposselect').val(),
	
				continuousstreamingenabled: Number($('#continuousstreamingcheckbox').prop('checked')),
				publicpagedisabled: Number($('#publicpagedisabledcheckbox').prop('checked')),
				audioenabled: Number($('#audiocheckbox').prop('checked')),
	
				timelapseenabled: Number($('#timelapsecheckbox').prop('checked')),
				timelapsemodeid: $('#timelapsetypeselect').val(),
				timezone: $('#timezoneselect').val(),
	
				privacycontrolenabled: Number($('#privacycontrolcheckbox').prop('checked')),
	
				domainlockenabled: Number($('#domainlockenabledcheckbox').prop('checked')),
				domainlockurl: $('#domainlockurledit').val(),
	
				timeshiftenabled: Number($('#timeshiftenabledcheckbox').prop('checked')),
				timeshift: $('#timeshiftdeltaselect').val(),
	
				operationtimewindowenabled: Number($('#operationtimewindowenabledcheckbox').prop('checked')),
				operationtimewindowfrom: $('#operationtimewindowfromedit').val(),
				operationtimewindowto: $('#operationtimewindowtoedit').val(),
	
				enabled: Number($('#enabledcheckbox').prop('checked'))
			};
	
			$.get('ajax/modifycamera.php', data, function(data){
				$('#submitbutton').show();
				$('#formprocessdiv').hide();
	
				if (data.result == 'ok') {
					bootstrap_alert.success('Camera succesfully updated.');
				} else {
					if (data.errcode == -2) {
						bootstrap_alert.error('Alias is already taken.');
					} else if (data.errcode == -3) {
						bootstrap_alert.error('You cannot update your cameras because your account is disabled.');
					} else if (data.errcode == -4) {
						bootstrap_alert.error('For paying services, first please, give your billing information <a href="accountsettings">here</a>.');
					} else if (data.errcode == -5) {
						bootstrap_alert.error('For paying services, first please, pay your outstanding invoice <a href="finance">here</a>.');
					} else if (data.errcode == -6) {
						bootstrap_alert.error('You cannot modify your overlay logo as long as your first payment has not arrived.');
					} else if (data.errcode == -7) {
						bootstrap_alert.error('Please upload your logo first.');
					} else {
						bootstrap_alert.error('Cannot update the settings of the camera.');
					}
				}
			}, 'json');
		}
	
		return false;
	});
	
	$('#cameraform').validate({
		rules: {
			aliasedit: {
				required: true,
				noextrachars: true,
				minlength: 4
			},
			nameedit: {
				required: true,
				minlength: 4
			},
			urledit: {
				required: true,
				url: true
			},
			domainlockurledit: {
				required: '#domainlockenabledcheckbox:checked',
				hostname: true
			},
			operationtimewindowfromedit: {
				required: '#operationtimewindowenabledcheckbox:checked'
			},
			operationtimewindowtoedit: {
				required: '#operationtimewindowenabledcheckbox:checked'
			}
		}
	});
	
	
	
	$("#logoenabledcheckbox, #timelapsecheckbox, #domainlockenabledcheckbox, #timeshiftenabledcheckbox, #timeshiftenabledcheckbox, #operationtimewindowenabledcheckbox").click(function(){
		ShowFeatures();
	});
	
	$('#operationtimewindowfromedit, #operationtimewindowtoedit').datetimepicker({
		datepicker:false,
		format:'H:i',
		step:60
	});
	
	
	
	loadingtimeout = setTimeout(function() {
		$('#loadingdiv').show();
	}, 500);
});



</script>



<h2>Camera settings</h2>

<div id="alert_placeholder"></div>

<div id="loadingdiv" class="loading nonvisible" style="display: none;"></div>

<div id="cameradiv" class="camerasettings nonvisible" style="display: block;">
	<form action="https://ipcamlive.com/camera?id=28209#" method="post" id="cameraform" class="form-horizontal" novalidate="novalidate">

			<div id="enableddiv" class="form-group">
				<div class="col-sm-offset-2 col-sm-4">
					<div class="checkbox">
						<label id="enabledtextdiv">
							<input type="checkbox" name="enabledcheckbox" id="enabledcheckbox"> Camera enabled
						</label>
					</div>
				</div>
			</div>

			<div id="aliasdiv" class="form-group">
				<label id="aliastextdiv" class="col-sm-2 control-label">Alias:</label>
				<div class="col-sm-4">
					<input type="text" name="aliasedit" id="aliasedit" class="form-control valid" value="Alias">
				</div>
			</div>
			<div id="namediv" class="form-group">
				<label id="nametextdiv" class="col-sm-2 control-label">Name:</label>
				<div class="col-sm-4">
					<input type="text" name="nameedit" id="nameedit" class="form-control valid" value="Name">
				</div>
			</div>
			<div id="urldiv" class="form-group">
				<label id="urltextdiv" class="col-sm-2 control-label">URL:</label>
				<div class="col-sm-4">
					<input type="text" name="urledit" id="urledit" class="form-control valid" value="URL">
				</div>
			</div>

			<br>

			<div id="operationtimewindowenableddiv" class="form-group" style="display: block;">
				<div class="col-sm-offset-2 col-sm-4">
					<div class="checkbox">
						<label id="operationtimewindowenabledtextdiv">
							<input type="checkbox" name="operationtimewindowenabledcheckbox" id="operationtimewindowenabledcheckbox"> Capture Schedule
						</label>
					</div>
				</div>
				<div class="col-sm-4">
					<div class="checkbox">
						<label id="operationtimewindowenabledtextdiv" style="margin-left:50px;">
							<input type="checkbox" name="operationtimewindowenabledcheckbox" id="operationtimewindowenabledcheckbox"> Record Schedule
						</label>
					</div>
				</div>
			</div>
			<div id="operationtimewindowfromdiv" class="form-group" style="display: block;">
				<label id="operationtimewindowfromtextdiv" class="col-sm-2 control-label">Time:&nbsp;</label>
				<div class="col-sm-4">
					<select name="timeshiftdeltaselect" id="timeshiftdeltaselect" class="form-control">
						<option value="60">1 min</option>
						<option value="300">5 min</option>
						<option value="600">10 min</option>
						<option value="900">15 min</option>
						<option value="1800">30 min</option>
						<option value="3600">1 hour</option>
						<option value="7200">2 hours</option>
						<option value="14400">4 hours</option>
						<option value="28800">8 hours</option>
						<option value="43200">12 hours</option>
						<option value="86400">24 hours</option>
					</select>
				</div>
				<label id="operationtimewindowfromtextdiv" class="control-label text-left right-content">From:&nbsp;</label>
				<div class="col-sm-4">
					<input type="text" name="operationtimewindowfromedit" id="operationtimewindowfromedit" class="form-control timepicker valid">
				</div>
			</div>
			<div id="operationtimewindowtodiv" class="form-group" style="display: block;">
				<label id="operationtimewindowtotextdiv" class="col-sm-2 control-label">&nbsp;</label>
				<div class="col-sm-4">
					<!-- <input type="text" name="operationtimewindowtodit" id="operationtimewindowtoedit" class="form-control timepicker valid"> -->
				</div>
				<label id="operationtimewindowtotextdiv" class="control-label right-content">To:&nbsp;</label>
				<div class="col-sm-4">
					<input type="text" name="operationtimewindowtodit" id="operationtimewindowtoedit" class="form-control timepicker valid">
				</div>
			</div>

			<div class="form-group">
				<div class="col-sm-offset-2 col-sm-4">
					<input onclick="myFunction()" type="submit" value="Update" id="submitbutton" class="btn btn-primary pull-left">
					<div id="formprocessdiv" class="loading pull-left" style="display: none;"></div>
				</div>
			</div>

	</form>

</div>				
<script>
function myFunction() {


console.log($('#operationtimewindowfromedit').val());
console.log($('#operationtimewindowtoedit').val());
console.log($('#timeshiftdeltaselect').val());
}
</script>
</body>
</html>