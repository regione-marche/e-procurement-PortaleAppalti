
$(document).ready(function(){
	
	if(screen.width < 768){
		if(document.getElementById("logo-main").children[0].children[0].src.includes("/appalti-contratti/")){
			document.getElementById("date-time").classList.add("responsive-banner-logo");
		}else{
			document.getElementById("date-time").classList.remove("responsive-banner-logo");
		}
		var balloonDiv = document.getElementsByClassName("balloon");
		var i;
		for (i = 0; i < balloonDiv.length; i++) {
			const div = document.createElement('div');
			div.className='balloon-expand-div'
			div.innerHTML = '<a class="espandi-balloon ballon-expand-text" href="javascript:void(0)" onclick="collassaEspandi(event,this)"></a>';
			balloonDiv[i].appendChild(div);
			
			
		}
		var balloonContent = document.getElementsByClassName("balloon-content");
		var i;
		for (i = 0; i < balloonContent.length; i++) {		
			balloonContent[i].classList.add("balloon-collapse");		
			
		}
	}else{
		document.getElementById("date-time").classList.remove("responsive-banner-logo");
	}
});

function collassaEspandi(event,element){
	var balloon=$(element).closest('.balloon').find('.balloon-content')	
	if ($(balloon).attr('class').includes('ballon-expand')) {		
		$(balloon).removeClass('ballon-expand');
		$(balloon).addClass('balloon-collapse');
		//element.textContent = '...';
		
		$(element).removeClass('ballon-collapse-text');
		
	} else {		
		//element.textContent = '';
		$(balloon).removeClass('balloon-collapse');
		$(balloon).addClass('ballon-expand');
		$(element).addClass('ballon-collapse-text');
		
		
		
	}
}