<%@ include file="menu.jsp"%>
<%-- <%@ page contentType="text/html;charset=UTF-8" language="java"%> --%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>

<script
	src="<c:url value="/resources/dashboard/jquery.datetimepicker.js"/>"></script>
<link
	href="<c:url value="/resources/dashboard/jquery.datetimepicker.css"/>"
	rel="stylesheet">

<div class="container">

	<div id="contentdiv" class="content">
		<script type="text/javascript">
			var readonlymode = 0;
			var cameraid = '28209';
		</script>


		<script type="text/javascript">
			var $servicetypeid = "";
			var $logofilename = "";
			var loadingtimeout;

			$(document)
					.ready(
							function() {
								$('#formprocessdiv').hide();
								$('#logouploadprocessdiv').hide();

								jQuery.validator.addMethod("nospace", function(
										value, element) {
									return value.indexOf(" ") < 0
											&& value != "";
								}, "No space please and don't leave it empty.");

								jQuery.validator
										.addMethod("noextrachars", function(
												value, element) {
											return value.match(/^[a-z0-9]+$/);
										},
												"Only lowercase letters and numbers allowed.");

								jQuery.validator
										.addMethod(
												"url",
												function(value, element) {
													var url = /^((http:\/\/)|(rtsp:\/\/)){1}.+$/i;

													return (url.test(value));
												},
												"Enter a valid RTSP or HTTP stream URL.");

								$('#cameraform')
										.submit(
												function() {
													if ($('#cameraform')
															.valid()) {
														$('#submitbutton')
																.hide();
														$('#formprocessdiv')
																.show();

														var logoenabled = Number($(
																'#logoenabledcheckbox')
																.prop('checked'));

														var data = {
															cameraid : cameraid,
															alias : $(
																	'#aliasedit')
																	.val(),
															name : $(
																	'#nameedit')
																	.val(),
															url : $('#urledit')
																	.val(),
															servicetypeid : $(
																	'#servicetypeselect')
																	.val(),

															logoenabled : logoenabled,
															logofilename : logofilename,
															logopos : $(
																	'#logoposselect')
																	.val(),

															continuousstreamingenabled : Number($(
																	'#continuousstreamingcheckbox')
																	.prop(
																			'checked')),
															publicpagedisabled : Number($(
																	'#publicpagedisabledcheckbox')
																	.prop(
																			'checked')),
															audioenabled : Number($(
																	'#audiocheckbox')
																	.prop(
																			'checked')),

															timelapseenabled : Number($(
																	'#timelapsecheckbox')
																	.prop(
																			'checked')),
															timelapsemodeid : $(
																	'#timelapsetypeselect')
																	.val(),
															timezone : $(
																	'#timezoneselect')
																	.val(),

															privacycontrolenabled : Number($(
																	'#privacycontrolcheckbox')
																	.prop(
																			'checked')),

															domainlockenabled : Number($(
																	'#domainlockenabledcheckbox')
																	.prop(
																			'checked')),
															domainlockurl : $(
																	'#domainlockurledit')
																	.val(),

															timeshiftenabled : Number($(
																	'#timeshiftenabledcheckbox')
																	.prop(
																			'checked')),
															timeshift : $(
																	'#timeshiftdeltaselect')
																	.val(),

															operationtimewindowenabled : Number($(
																	'#operationtimewindowenabledcheckbox')
																	.prop(
																			'checked')),
															operationtimewindowfrom : $(
																	'#operationtimewindowfromedit')
																	.val(),
															operationtimewindowto : $(
																	'#operationtimewindowtoedit')
																	.val(),

															enabled : Number($(
																	'#enabledcheckbox')
																	.prop(
																			'checked'))
														};

														$
																.get(
																		'ajax/modifycamera.php',
																		data,
																		function(
																				data) {
																			$(
																					'#submitbutton')
																					.show();
																			$(
																					'#formprocessdiv')
																					.hide();

																			if (data.result == 'ok') {
																				bootstrap_alert
																						.success('Camera succesfully updated.');
																			} else {
																				if (data.errcode == -2) {
																					bootstrap_alert
																							.error('Alias is already taken.');
																				} else if (data.errcode == -3) {
																					bootstrap_alert
																							.error('You cannot update your cameras because your account is disabled.');
																				} else if (data.errcode == -4) {
																					bootstrap_alert
																							.error('For paying services, first please, give your billing information <a href="accountsettings">here</a>.');
																				} else if (data.errcode == -5) {
																					bootstrap_alert
																							.error('For paying services, first please, pay your outstanding invoice <a href="finance">here</a>.');
																				} else if (data.errcode == -6) {
																					bootstrap_alert
																							.error('You cannot modify your overlay logo as long as your first payment has not arrived.');
																				} else if (data.errcode == -7) {
																					bootstrap_alert
																							.error('Please upload your logo first.');
																				} else {
																					bootstrap_alert
																							.error('Cannot update the settings of the camera.');
																				}
																			}
																		},
																		'json');
													}

													return false;
												});

								$('#cameraform')
										.validate(
												{
													rules : {
														aliasedit : {
															required : true,
															noextrachars : true,
															minlength : 4
														},
														nameedit : {
															required : true,
															minlength : 4
														},
														urledit : {
															required : true,
															url : true
														},
														domainlockurledit : {
															required : '#domainlockenabledcheckbox:checked',
															hostname : true
														},
														operationtimewindowfromedit : {
															required : '#operationtimewindowenabledcheckbox:checked'
														},
														operationtimewindowtoedit : {
															required : '#operationtimewindowenabledcheckbox:checked'
														}
													}
												});

								$(
										"#logoenabledcheckbox, #timelapsecheckbox, #domainlockenabledcheckbox, #timeshiftenabledcheckbox, #timeshiftenabledcheckbox, #operationtimewindowenabledcheckbox")
										.click(function() {
											ShowFeatures();
										});

								$(
										'#operationtimewindowfromedit, #operationtimewindowtoedit')
										.datetimepicker({
											datepicker : false,
											format : 'H:i',
											step : 60
										});

								loadingtimeout = setTimeout(function() {
									$('#loadingdiv').show();
								}, 500);
							});
		</script>



		<h2>Camera settings</h2>


		<div id="alert_placeholder"></div>

		<div id="loadingdiv" class="loading nonvisible" style="display: none;"></div>

		<div id="cameradiv" class="camerasettings nonvisible"
			style="display: block;">

			<form:form modelAttribute="camera" method="POST"
				class="form-horizontal" novalidate="novalidate" enctype="utf8">
				<div id="enableddiv" class="form-group">
					<div class="col-sm-offset-2 col-sm-4">
						<div class="checkbox">
							<label id="enabledtextdiv"> <form:checkbox path="enabled" />
								Camera enabled
							</label> <label id="enabledtextdiv"> <form:label
									class="alert alert-error" path="enabled" /> ${mess}
							</label>
						</div>
					</div>
				</div>

				<div id="aliasdiv" class="form-group">
					<label id="aliastextdiv" class="col-sm-2 control-label">Alias:</label>
					<div class="col-sm-4">
						<form:input path="alias" value="" type="text"
							class="form-control valid" placeholder="Alias" />
						<!-- <input type="text" name="aliasedit" id="aliasedit" class="form-control valid" value="Alias"> -->
					</div>
				</div>
				<div id="namediv" class="form-group">
					<label id="nametextdiv" class="col-sm-2 control-label">Name:</label>
					<div class="col-sm-4">
						<form:input path="name" value="" type="text"
							class="form-control valid" placeholder="name" />
						<!-- <input type="text" name="nameedit" id="nameedit" class="form-control valid" value="Name"> -->
					</div>
				</div>
				<div id="urldiv" class="form-group">
					<label id="urltextdiv" class="col-sm-2 control-label">URL:</label>
					<div class="col-sm-4">
						<form:input path="cameraUrl" value="" type="text"
							class="form-control valid" placeholder="URL" />
						<!-- <input type="text" name="urledit" id="urledit" class="form-control valid" value="URL"> -->
					</div>
				</div>

				<div id="operationtimewindowenableddiv" class="form-group">
					<label id="securitytextdiv" class="col-sm-2 control-label">Security:</label>
					<div class="col-sm-1">
						<form:checkbox class="form-control valid" id="security_enable"
							path="security" />
					</div>
				</div>
				<div id="operationtimewindowenableddiv" class="form-group">
					<label id="securitytextdiv" class="col-sm-2 control-label">Security
						Management:</label>
					<div class="col-sm-4 ">
						<form:select path="securityId" items="${users}"
							class="form-control">
						</form:select>
					</div>
				</div>
				<br>

				<div id="operationtimewindowenableddiv" class="form-group"
					style="display: block;">
					<label id="securitytextdiv" class="col-sm-2 control-label">Enable
						Record:</label>
					<div class="col-sm-1">
						<form:checkbox class="form-control valid"
							onclick="recordEnable(this)" id="record_enable" path="record" />
					</div>

				</div>

				<div id="operationtimewindowenableddivv" class="form-group"
					style="display: block;">
					<label id="securitytextdiv" class="col-sm-2 control-label">Repeat
						Every Day:</label>
					<div class="col-sm-1">
						<form:checkbox class="form-control valid"
							onclick="recRepeat(this)" id="recordRepeat" path="recordRepeat" />
					</div>
				</div>
				<div id="operationtimewindowfromdiv" class="form-group"
					style="display: block;">

					<label id="nametextdiv" class="col-sm-2 control-label">Start
						Time:</label>
					<div class="col-sm-4">
						<form:input path="recordSchedule" value="" type="text"
							class="form-control valid" placeholder="hh:mm" />
					</div>

				</div>
				<div id="operationtimewindowtodiv" class="form-group"
					style="display: block;">

					<label id="nametextdiv" class="col-sm-2 control-label">Stop Time:</label>
					<div class="col-sm-4">
						<form:input path="recordTime" value="" type="text"
							class="form-control valid" placeholder="hh:mm" />
					</div>
				</div>
				<div id="operationtimewindowtodiv" class="form-group"
					style="display: block;">

					<label id="operationtimewindowtotextdiv"
						class="col-sm-2 control-label">From:&nbsp;</label>
					<div class="col-sm-4 ">
						<form:input path="recordFrom" id="record_from_input"
							class="form-control" value="" type="text" placeholder="dd-MM-yyyy" />
					</div>
				</div>

				<div id="operationtimewindowtodiv" class="form-group"
					style="display: block;">

					<label id="operationtimewindowtotextdiv"
						class="col-sm-2 control-label">to:&nbsp;</label>
					<div class="col-sm-4 ">
						<form:input path="recordTo" id="record_to_input"
							class="form-control" value="" type="text" placeholder="dd-MM-yyyy" />
					</div>
				</div>

				<div class="form-group">
					<div class="col-sm-offset-2 col-sm-4">
						<input type="submit" value="Update"
							class="btn btn-primary pull-left" />
						<div id="formprocessdiv" class="loading pull-left"
							style="display: none;"></div>
					</div>
				</div>

			</form:form>

		</div>
		<script
			src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
		<script>
			$(document)
					.ready(
							function() {
								console.log("on loaddd");
								if ($('#capture_enable').is(':checked')) {
									console.log("on checked");
								} else {
									console.log("not checked");
									$('#captureTime').prop('disabled', true);
									$('#captureRepeat').prop('disabled', true);
									document
											.getElementById("capture_from_input").readOnly = true;
									document.getElementById("capture_to_input").readOnly = true;
								}

								if ($('#record_enable').is(':checked')) {
									console.log("on checked");
								} else {
									console.log("not checked");
									$('#recordTime').prop('disabled', true);
									$('#recordRepeat').prop('disabled', true);
									document.getElementById("recordSchedule").readOnly = true;
									document
											.getElementById("record_from_input").readOnly = true;
									document.getElementById("record_to_input").readOnly = true;
								}

								if ($('#recordRepeat').is(':checked')) {
									document
											.getElementById("record_from_input").readOnly = true;
									document.getElementById("record_to_input").readOnly = true;
								}
								if ($('#captureRepeat').is(':checked')) {
									document
											.getElementById("capture_from_input").readOnly = true;
									document.getElementById("capture_to_input").readOnly = true;
								}

							});
		</script>
		<script>
			//capture_enable
			//recordEnable
			function recordEnable(chkbox) {
				var visSetting = (chkbox.checked) ? "visible" : "hidden";
				if (!chkbox.checked) {

					console.log("not checked");
					$('#recordTime').prop('disabled', true);
					$('#recordRepeat').prop('disabled', true);
					document.getElementById("recordSchedule").readOnly = true;
					document.getElementById("record_from_input").readOnly = true;
					document.getElementById("record_to_input").readOnly = true;
				} else {
					console.log("not checked");
					if ($('#recordRepeat').is(':checked')) {
						document.getElementById("record_from_input").readOnly = true;
						document.getElementById("record_to_input").readOnly = true;
					} else {
						document.getElementById("record_from_input").readOnly = false;
						document.getElementById("record_to_input").readOnly = false;
					}

					$('#recordTime').prop('disabled', false);
					$('#recordRepeat').prop('disabled', false);
					document.getElementById("recordSchedule").readOnly = false;

				}
			}

			function captureEnable(chkbox) {
				var visSetting = (chkbox.checked) ? "visible" : "hidden";
				if (!chkbox.checked) {
					console.log("not checked");
					$('#captureTime').prop('disabled', true);
					$('#captureRepeat').prop('disabled', true);
					document.getElementById("capture_from_input").readOnly = true;
					document.getElementById("capture_to_input").readOnly = true;
				} else {
					console.log("not checked");
					if ($('#captureRepeat').is(':checked')) {
						document.getElementById("capture_from_input").readOnly = true;
						document.getElementById("capture_to_input").readOnly = true;
					} else {
						document.getElementById("capture_from_input").readOnly = false;
						document.getElementById("capture_to_input").readOnly = false;
					}
					$('#captureTime').prop('disabled', false);
					$('#captureRepeat').prop('disabled', false);

				}
			}
			function capRepeat(chkbox) {
				var visSetting = (chkbox.checked) ? "visible" : "hidden";
				if (chkbox.checked) {
					console.log("checked");
					document.getElementById("capture_from_input").readOnly = true;
					document.getElementById("capture_to_input").readOnly = true;
				} else {
					console.log("not checked");
					document.getElementById("capture_from_input").readOnly = false;
					document.getElementById("capture_to_input").readOnly = false;
				}
			}

			function recRepeat(chkbox) {
				var visSetting = (chkbox.checked) ? "visible" : "hidden";
				if (chkbox.checked) {
					console.log("checked");
					document.getElementById("record_from_input").readOnly = true;
					document.getElementById("record_to_input").readOnly = true;
				} else {
					console.log("not checked");
					document.getElementById("record_from_input").readOnly = false;
					document.getElementById("record_to_input").readOnly = false;
				}
			}
		</script>

		</body>
		</html>