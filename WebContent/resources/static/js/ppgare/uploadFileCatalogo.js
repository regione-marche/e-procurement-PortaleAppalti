$(document).ready(function(){	

	// trova tutte i form che contengono un tag <input type=submit> e nascondili
	var submitButtons = $("input[type='file']").closest("form").find("input[type='submit'].block-ui");
	submitButtons.hide();
	
	$("input[type='file']").on("change", (function() {
		$(this).closest("div").find(".invalidFilenameMsg").hide();
		
		var file = $(this)[0].files[0];	
		if( isValidFilename(file.name) ) {
			// esegui il "click" del "submit button" associato al controllo <input type=file>
			var inputFile = $(this);
			var submitBtn = inputFile.closest("form").find("input[type='submit'].block-ui");
			submitBtn.trigger('click');
		} else {
			$(this).closest("div").append('<div class="invalidFilenameMsg errors">' + invalidFilenameMsg + '</div>');	
		}
	}));
	
	function isValidFilename(filename) {
		var reg = new RegExp("^[" + validFilenameChars + "]+$", "g");
		return (reg.test(filename));
	}
	
});
