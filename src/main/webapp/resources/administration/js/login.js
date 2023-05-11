window.addEvent('domready', function(){
	
	var usernameElement = $pick($('username'));
	if (usernameElement != null) {
		usernameElement.focus();		
	}
	
	var fadingElement = $pick($('fieldset_space'));
	if (fadingElement != null) {
		var durationTime = 700;
		fadingElement.set('tween', {
			duration: durationTime,
			onComplete: function(){
				fadingElement.set('tween', {
					duration: durationTime,
					onComplete: function(){}
				}); 
				fadingElement.tween('border-color','#FE9900', '#DEDEDE');
			}
		});
		fadingElement.tween('border-color','#DEDEDE','#FE9900');
	}
});