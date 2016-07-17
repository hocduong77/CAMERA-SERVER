<%@ include file="header.jsp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
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
				<a class="navbar-brand" href="https://ipcamlive.com/"><img src="<c:url value="/resources/login_files/index-logo.png"/>" alt="ipcamlive"></a>
			</div>
			<div id="navbar" class="navbar-collapse collapse">
									<ul class="nav navbar-nav navbar-right">
	<li><a href="<c:url value="/login.html" />">Login</a></li>
</ul>							</div><!--/.nav-collapse -->
		</div><!--/.container-fluid -->
	</nav>

	<div class="container">

		<div id="contentdiv" class="content">
					


<div class="login">
<c:set var="error" scope="session" value="${param.error}"/>
<c:if test="${error == true}">
	<div class="alert alert-danger collapse" role="alert" style="display: block;">
		<strong>Error! </strong>Invalid login name or password.
	</div>
</c:if>
	<div id="logindiv">
		<form action="j_spring_security_check"  method="post" id="loginform" class="form-signin">
			<h2 class="form-signin-heading">Please sign in</h2>
			<input input type='text' name='j_username' value='' class="form-control" placeholder="Login name">
			<input input type='password' name='j_password' class="form-control" placeholder="Password">
			<label class="checkbox">
				<input type="checkbox" id="rememberme" value="remember-me"> Remember me
			</label>
			<button id="okbutton" class="btn btn-large btn-default" data-loading-text="Loading..." type="submit">Login</button>
			<div class="register pull-right">
				<a href="<c:url value="/user/registration" />">Sign up</a><br>
				<a href="https://ipcamlive.com/sendpasswordresetlink">Forgotten password</a>
			</div>
		</form>

	</div>

</div>				
</div>

</div>

</div>

	<script src="<c:url value="/resources/login_files/bootstrap.min.js"/>"></script>


</body></html>