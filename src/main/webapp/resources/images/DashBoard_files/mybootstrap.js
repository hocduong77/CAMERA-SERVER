$(function() {

	bootstrap_alert = function() {}

	bootstrap_alert.error = function(message) {
		$('#alert_placeholder').html('<div class="alert alert-danger collapse" role="alert"><span>'+message+'</span></div>');
		$(".alert").fadeIn();
	}

	bootstrap_alert.success = function(message) {
		$('#alert_placeholder').html('<div class="alert alert-success collapse" role="alert"><span>'+message+'</span></div>');
		$(".alert").fadeIn();
	}

	bootstrap_alert.info = function(message) {
		$('#alert_placeholder').html('<div class="alert alert-info collapse" role="alert"><span>'+message+'</span></div>');
		$(".alert").fadeIn();
	}

	bootstrap_alert.clear = function(message) {
		$(".alert").fadeOut();
	}


});