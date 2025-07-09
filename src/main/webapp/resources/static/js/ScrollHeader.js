	
    html = document.documentElement;

	window.addEventListener('load', function () {
		document.getElementById("date-time").classList.add("base-icona-menu-ancorato");
		document.getElementById("menu").classList.add("base-menu-ancorato");
	})

	window.onscroll = function() {scrollFunction()};


	
	function scrollFunction() {
		var headerHeight = document.getElementById("header").clientHeight;
		var menuHeight = document.getElementById("menu").clientHeight;
		var viewportHeight = window.innerHeight;
		var scrollHeight = document.body.scrollHeight
		
		if (scrollHeight > viewportHeight + 2*(menuHeight + headerHeight)) {
			if ((document.body.scrollTop > menuHeight + headerHeight) || (document.documentElement.scrollTop > menuHeight + headerHeight)) {	  			
				if (screen.width > 768){
					document.getElementById("header").style.display = "none";
					document.getElementById("htmlprebanner").style.display = "none";
					document.getElementById("htmlpostbanner").style.display = "none";
					document.getElementById("style-sub-menu").style.display = "none";
					if (document.getElementById("search-form") != null) {
						document.getElementById("search-form").style.display = "none";
					}				
					document.getElementById("menu").classList.remove("base-menu-ancorato");	
					if (document.getElementById("date-time") != null) {
						document.getElementById("date-time").classList.remove("base-icona-menu-ancorato");
						document.getElementById("date-time").classList.add("icona-menu-ancorato");							
					} else {
						document.getElementById("menu").classList.add("icona-menu-ancorato");						
					}
					document.getElementById("menu").classList.add("menu-ancorato");	
																						
				} else {
					document.getElementById("header").style.display = "none";
				}
			} else {
				document.getElementById("header").style.display = "block";
				document.getElementById("htmlprebanner").style.display = "block";
				document.getElementById("htmlpostbanner").style.display = "block";
				document.getElementById("style-sub-menu").style.display = "block";
				if (document.getElementById("search-form") != null) {
					document.getElementById("search-form").style.display = "block";
				}
				
				document.getElementById("menu").classList.remove("menu-ancorato");	
				if (document.getElementById("date-time") != null) {
					document.getElementById("date-time").classList.remove("icona-menu-ancorato");								
					document.getElementById("date-time").classList.add("base-icona-menu-ancorato");				
				} else {
					document.getElementById("menu").classList.remove("icona-menu-ancorato");		
				}
				document.getElementById("menu").classList.add("base-menu-ancorato");	
			}
		} else {
			if (document.getElementById("header").style.display == "none") {
				document.getElementById("header").style.display = "block";
				document.getElementById("htmlprebanner").style.display = "block";
				document.getElementById("htmlpostbanner").style.display = "block";
				document.getElementById("style-sub-menu").style.display = "block";
				if (document.getElementById("search-form") != null) {
					document.getElementById("search-form").style.display = "block";				
				}
				document.getElementById("menu").classList.remove("menu-ancorato");	
				if (document.getElementById("date-time") != null) {
					document.getElementById("date-time").classList.remove("icona-menu-ancorato");								
					document.getElementById("date-time").classList.add("base-icona-menu-ancorato");	
				} else {
					document.getElementById("menu").classList.remove("icona-menu-ancorato");		
				}
				document.getElementById("menu").classList.add("base-menu-ancorato");	
			}			
		}	 
	}
