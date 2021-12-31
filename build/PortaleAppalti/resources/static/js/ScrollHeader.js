
	window.onscroll = function() {scrollFunction()};

	function scrollFunction() {
		
		if (document.body.scrollTop > 60 || document.documentElement.scrollTop > 60) {	  
			if(screen.width > 768){					 
					document.getElementById("header").style.display = "none";
					document.getElementById("htmlprebanner").style.display = "none";
					document.getElementById("htmlpostbanner").style.display = "none";
					/*document.getElementById("menu").style.position = "fixed";
					document.getElementById("menu").style.zIndex = "99999";	
					document.getElementById("menu").style.maxWidth = "1160px";
					document.getElementById("menu").style.boxShadow = "0 12px 14px -10px black";
					
					document.getElementById("date-time").style.paddingLeft= "55px";
					document.getElementById("date-time").style.backgroundImage= "url(../../img/logo-aec.png)";
					document.getElementById("date-time").style.backgroundRepeat= "no-repeat";				
					document.getElementById("date-time").style.backgroundSize= "contain";*/
					
					document.getElementById("menu").classList.remove("base-menu-ancorato");	
					document.getElementById("date-time").classList.remove("base-icona-menu-ancorato");	
					
					document.getElementById("menu").classList.add("menu-ancorato");	
					document.getElementById("date-time").classList.add("icona-menu-ancorato");	
			}
			else {
					document.getElementById("header").style.display = "none";
				}
		}else{
			document.getElementById("header").style.display = "block";
			document.getElementById("htmlprebanner").style.display = "block";
			document.getElementById("htmlpostbanner").style.display = "block";
			document.getElementById("menu").classList.remove("menu-ancorato");	
			document.getElementById("date-time").classList.remove("icona-menu-ancorato");
			
			document.getElementById("menu").classList.add("base-menu-ancorato");	
			document.getElementById("date-time").classList.add("base-icona-menu-ancorato");	
			
			/*document.getElementById("menu").style.position = "relative";
			document.getElementById("menu").style.zIndex = "0";
			document.getElementById("menu").style.boxShadow = "none";
		
			document.getElementById("date-time").style.paddingLeft= "3px";
			document.getElementById("date-time").style.backgroundImage= "none";
			document.getElementById("date-time").style.backgroundRepeat= "none";		
			document.getElementById("date-time").style.backgroundSize= "none";*/
		}
		
		
	 
	  
	}
