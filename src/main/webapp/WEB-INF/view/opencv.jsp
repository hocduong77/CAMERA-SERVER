<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<!-- <html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>security system</title>
</head>
<body>
	<hr>
	<applet 
		CODEBASE="http://localhost:8080/camera-server/resources/resourceapplet"
		code="HelloWorldAppletnew.class" archive="HelloWorldAppletnew.jar">
		width="1000" height="500"> If your browser was Java-enabled, a "Hello,
		World" message would appear here. </applet>
	<hr>
</body>
</html>
 -->


<html>
<title>The Hello, World Applet</title>
<hr>
<applet name="Applet_1"
	CODEBASE="http://localhost:8080/camera-server/resources/resourceapplet"
	code="camera.class" width="1000" height="500">
	<param name="rtpPort" value="6699" />
	<param name="separate_jvm" value="true">
</applet>
<hr>
<applet name="Applet_2"
	CODEBASE="http://localhost:8080/camera-server/resources/resourceapplet"
	code="camera.class" width="1000" height="500">
	<param name="rtpPort" value="8207" />
	<param name="separate_jvm" value="true">
</applet>
<hr>
</html>