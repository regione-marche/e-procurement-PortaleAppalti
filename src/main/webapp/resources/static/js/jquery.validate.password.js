/*
 * jQuery validate.password plug-in 1.0
 *
 * http://bassistance.de/jquery-plugins/jquery-plugin-validate.password/
 *
 * Copyright (c) 2009 Jörn Zaefferer
 * Modified by Marco Perazzetta (2014)
 *
 * $Id$
 *
 * Dual licensed under the MIT and GPL licenses:
 *   http://www.opensource.org/licenses/mit-license.php
 *   http://www.gnu.org/licenses/gpl.html
 */
(function($) {
	
	// il set di caratteri speciali e' il seguente 
	//
	//		~#"$%&'()*+,-./:;<=>?!@[]^_\
	//
	// i caratteri speciali per le regex che necessitano escape sono
	//
	//		< > ( )	[ ] { }	\ ^	- =	$ !	| ? * +	.
	//
	// e quindi il set di caratteri con escapi diventa
	//
	//		~#"\$%&'\(\)\*\+,\-\./:;\<\=\>\?\!@\[\]\^_\\
	
	var LOWER = /[a-z]/,
		UPPER = /[A-Z]/,
		AT_LEAST_1_DIGITS = /(.*\d.*){1,}/,
		AT_LEAST_2_DIGITS = /(.*\d.*){2,}/,
		//DIGITS = /[0-9].*[0-9]/,
		//AT_LEAST_3_DIGITS = /(.*\d.*){3,}/,
		//SPECIAL = /[_.]/,
		//SPECIAL2 = /[_.&$@!-]/,
		SPECIAL = /[~#"\$%&'\(\)\*\+,\-\.\/:;\<\=\>\?\!@\[\]\^_\\]/,	// standard user
		SPECIAL2 = SPECIAL,												// admin
		SAME_CHAR = /^(.)\1+$/,
		//EXCLUDE_CHARS = /[^a-zA-Z_.0-9]/,
		//EXCLUDE_CHARS2 = /[^a-zA-Z_.0-9&$@!-]/;
		EXCLUDE_CHARS = /[^a-zA-Z0-9~#"\$%&'\(\)\*\+,\-\.\/:;\<\=\>\?\!@\[\]\^_\\]/,
		EXCLUDE_CHARS2 = EXCLUDE_CHARS;
		
	function rating(rate, message) {
		return {
			rate: rate,
			messageKey: message
		};
	}
	
	function uncapitalize(str) {
		return str.substring(0, 1).toLowerCase() + str.substring(1);
	}
	
	$.validator.passwordRating = function(password, usernameVal, minLength, maxLength, isAdmin) {
		
		var lower = LOWER.test(password),
			upper = UPPER.test(uncapitalize(password)),
			digit = AT_LEAST_2_DIGITS.test(password),
			//digits = AT_LEAST_3_DIGITS.test(password),
			special = SPECIAL.test(password);
		
		var excludeChars = EXCLUDE_CHARS;
		if(isAdmin != 0) {
			digit = AT_LEAST_1_DIGITS.test(password);
			special = special || SPECIAL2.test(password);
			excludeChars = EXCLUDE_CHARS2; 
		} 
		
		if (excludeChars.test(password)) {
			return rating(0, "wrong-chars");
		}
		if (!password || password.length < minLength) {
			return rating(0, "too-short");
		}
		if (password && password.length > maxLength) {
			return rating(0, "too-long");
		}
		if (usernameVal && password.toLowerCase().match(usernameVal.toLowerCase())) {
			return rating(0, "similar-to-username");
		}
		if (SAME_CHAR.test(password)) {
			return rating(1, "very-weak");
		}
		if (lower && upper && digit && special) {
			return rating(5, "very-strong");
		}
		if (lower && upper && digit || lower && digit && special || upper && digit && special || lower && upper && special) {
			return rating(4, "strong");
		}	
		if (lower && digit || upper && digit) {
			return rating(3, "good");
		}		
		return rating(2, "weak");
	}
	
	$.validator.passwordRating.messages = {
		"similar-to-username": msgErrorSimilarToUsername,
		"too-short": msgErrorTooShort,
		"too-long": msgErrorTooLong,
		"very-weak": msgErrorVeryWeak,
		"weak": msgErrorWeak,
		"good": msgGood,
		"strong": msgStrong,
		"very-strong": msgVeryStrong,
		"wrong-chars": msgErrorWrongChars
	}
	
	$.validator.addMethod("passwordPolicy", function(value, element, usernameField) {
		
		// use untrimmed value
		var password = element.value;
		
		// get username for comparison, if specified
		var usernameVal;
		var minLength = (usernameField[1]) ? usernameField[1] : 0;
		var maxLength = (usernameField[2]) ? usernameField[2] : 0;
		var isAdmin = (usernameField[3]) ? usernameField[3] : 0;

		if (usernameField[0] instanceof jQuery && typeof usernameField[0] != "boolean") {
			usernameVal = $(usernameField[0]).val();
		} else if (usernameField[0] instanceof jQuery && typeof usernameField[0] == "boolean") {
			usernameVal = undefined;
		} else {
			usernameVal = usernameField[0];
		}
		
		var rating = $.validator.passwordRating(password, usernameVal, minLength, maxLength, isAdmin);

		// update message for this field
		var meter = $(".password-meter", element.form);
		
		if (password.length > 0) {
			meter.show();
		} else {
			meter.hide();
		}
		
		meter.find(".password-meter-bar").removeClass().addClass("password-meter-bar").addClass("password-meter-" + rating.messageKey);
		var message = $.validator.passwordRating.messages[rating.messageKey];
		if (rating.messageKey == "too-short") {
			message = message.replace(/{\d}/g, minLength);
		}
		if (rating.messageKey == "too-long") {
			message = message.replace(/{\d}/g, maxLength);
		}
		meter.find(".password-meter-message").removeClass().addClass("password-meter-message").addClass("password-meter-message-" + rating.messageKey).text(message);
		
		// display process bar instead of error message	
		return rating.rate > 2 && rating.rate <= 5;
	}, " ");
	
	// manually add class rule, to make username param optional
	$.validator.classRuleSettings.password = { password: true };
	
})(jQuery);
