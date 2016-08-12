<!DOCTYPE html>
<!-- saved from url=(0022)https://ipcamlive.com/ -->
<html lang="en"><head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	
	<title>Embedding IP Camera Live Video Stream in web page</title>
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<meta name="keywords" content="ip camera, network camera, embed ip camera, embed ip camera into webpage, embed ip camera stream in web page, embed network camera on website">
	<meta name="description" content="Video streaming solution for embedding your IP camera live video stream in web page.">
	<meta name="author" content="">
	<link rel="shortcut icon" href="https://ipcamlive.com/tpls/images/ipcamlive_favicon_128.png">
	<link href="./css/bootstrap.min.css" rel="stylesheet">
	<!--[if lt IE 9]>
	<script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
	<script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
	<![endif]-->
	<link href="./css/jquery-ui-1.10.0.custom.min.css" rel="stylesheet">
	<link href="./css/skin.css" rel="stylesheet">
	<link rel="stylesheet" type="text/css" href="./css/jquery.fancybox.css?v=2.1.5" media="screen" />
	<link href="./css/helpers/jquery.fancybox-buttons.css?v=1.0.5" rel="stylesheet">

	<script src="./js/jquery-1.11.2.min.js"></script>
	<script src="./js/jquery-ui.js"></script>
	<script src="./js/bootstrap.min.js"></script>
	<script type="text/javascript" src="./js/jquery.fancybox.js?v=2.1.5"></script>
	<script type="text/javascript" src="./css/helpers/jquery.fancybox-buttons.js?v=1.0.5"></script>
	<script src="./js/script.js"></script>

	<script>
		$( function() {
			$( "#date_from,#date_to" ).datepicker();
		} );
		$(document).ready(function() {
			$('.fancybox-buttons').fancybox({
				openEffect  : 'none',
				closeEffect : 'none',

				prevEffect : 'none',
				nextEffect : 'none',

				closeBtn  : false,

				helpers : {
					title : {
						type : 'inside'
					},
					buttons	: {}
				},

				afterLoad : function() {
					this.title = 'Image ' + (this.index + 1) + ' of ' + this.group.length + (this.title ? ' - ' + this.title : '');
				}
			});
		})
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
				<a class="navbar-brand" href="https://ipcamlive.com/"><img src="./images/index-logo.png" alt="ipcamlive"></a>
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
				</ul>
			</div>
			<!--/.nav-collapse -->
		</div><!--/.container-fluid -->
	</nav>
	<div class="content">
		<div class="search-content">
			<label class="pull-left">Seach date</label><input type="text" id="date_from">
			<input type="text" id="date_to">
			<input type="submit" class="btn btn-default">
		</div>
		<div class="seach-result">
			<div class="row">
				<!-- list img -->
				<div class="col-sm-3">
					<div class="list-item list-img">
						<a class="fancybox-buttons" data-fancybox-group="button">
							<img src="http://localhost:8080/images/08_11_2016_00_20_19.jpeg" alt=""></a>
						<span class="icon-img"></span>
					</div>
				</div>
				<div class="col-sm-3">
					<div class="list-item list-img">
						<a class="fancybox-buttons" data-fancybox-group="button">
							<img src="http://localhost:8080/images/08_11_2016_00_20_19.jpeg" alt=""></a>
						<span class="icon-img"></span>
					</div>
				</div>
				<div class="col-sm-3">
					<div class="list-item list-img">
						<a class="fancybox-buttons" data-fancybox-group="button">
							<img src="http://localhost:8080/images/08_11_2016_00_20_19.jpeg" alt=""></a>
						<span class="icon-img"></span>
				</div>
				</div>
				<div class="col-sm-3">
					<div class="list-item list-img">
						<a class="fancybox-buttons" data-fancybox-group="button">
							<img src="http://localhost:8080/images/08_11_2016_00_20_19.jpeg" alt=""></a>
						<span class="icon-img"></span>
					</div>
				</div>
				
<!--popup-->
<div class="modal fade capture-modal" id="show_list" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel">
	<div class="modal-dialog" role="document">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
				<h4 class="modal-title" id="myModalLabel">Capture</h4>
			</div>
			<div class="modal-body text-center">
				<img src="images/img.jpg" alt="">
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
				<button type="button" class="btn btn-primary">Save</button>
			</div>
		</div>
	</div>
</div>
</body>
</html>