
$(document).ready(function(){
	$("input[type='file']").closest("form").find("input[type='submit']").hide();
	
	$("input[type='file']").on("change", function() {
		showProgessBar(1);
		$(this).closest("div").find(".invalidFilenameMsg").hide();
		
		var file = $(this)[0].files[0];	
		if( isValidFilename(file.name) ) {
			$(this).closest("form").find("input[type='submit']").trigger('click');
		} else {
			showProgessBar(0);
			$(this).closest("div").append('<div class="invalidFilenameMsg errors">' + invalidFilenameMsg + '</div>');	
		}
	});

	$("input[type='file']").on("blur", function() {
		showProgessBar(0);
	});
	
	if(typeof allowEmptyField == 'undefined'){
		$("input[type='text'].long-text").closest("form").find("input[type='file']").prop('disabled', true);
	}
	
	$("input[type='text'].long-text").on("keyup", function() {
		var btn = $(this).closest("form").find("input[type='file']");
		btn.prop('disabled', ($(this).val().length <= 0));
	});
	
	function isValidFilename(filename) {
		var reg = new RegExp("^[" + validFilenameChars + "]+$", "g");
		return (reg.test(filename));
	}
	
	// forza l'apertura/chiusura della progress bar di upload (se e' presente)
	function showProgessBar(value) {
		try {
			if($('#blockUImessage').length) {
				if(value == 1) {
					showBlockUIMessage();
				} else {
					hideBlockUIMessage();
				}
			}
		} catch(ex) {
		}
	}

});
