/*
 * script generico che consente di agganciare un dialog modale che va a pieno schermo sulla dimensione 
 * del browser riportando la pagina all'inizio, per clonare dei dati presenti in un'area della videata 
 * (ad esempio un datatable) in una schermata piu' ampia.
 * 
 * PREREQUISITI:
 * 1. includere jquery-ui e relativi css
 * 2. avere un elemento con class "source-popup" da cui riprendere i dati da presentare in popup
 * 3. avere un elemento con id="openDialog" per agganciare, al click l'apertura della finestra modale
 */

$(document).ready(function() {
	
	// si crea il div in cui inserire i dati del dialog
	var myDiv = $("<div/>");
	myDiv.hide();
	
	// si clonano i dati presenti
	var sourceCloned = $(".source-popup").clone();
	sourceCloned.appendTo(myDiv);
	clearChildrenId(myDiv);
	
	/*var altezzaDialog;*/
	
    // al click sul bottone modifica si apre la dialog dimensionata
	// opportunamente e
    // si visualizza all'interno la tabella con i dati da editare
    $("body").on("click", "#openDialog", 
       	function() {
   		
    		// si dimensiona la dialog centrale e 100px piu' piccola
			// rispetto alla dimensione del browser
    		window.scrollTo(0, 0);
			var larghezzaSchermo = $(window).width();
			var altezzaSchermo = $(window).height();
		    var larghezzaDialog = larghezzaSchermo - 100;

			var myButtons = {};
			myButtons[buttonCloseDialog] = function() { $(this).dialog('close'); };

			/*altezzaDialog = altezzaSchermo - 100;*/
		    $(myDiv).dialog({
		    	autoOpen: true,
		    	closeOnEscape: true,
		    	width: larghezzaDialog,
		    	/*height: altezzaDialog,*/
		    	show: {
		    		effect: "blind",
		    		duration: 500
		        },
		        hide: {
		        	effect: "blind",
		        	duration: 500
		        },
		        modal: true,
		        resizable: true,
				focusCleanup: true,
				cache: false,
				buttons: myButtons  
		    });
		    $(".ui-dialog-titlebar").hide();
    	}
    );
});

/*
 * Permette di clonare una struttura, con l'attenzione di clonare gli id valorizzati 
 * inserendo il prefisso "clone" nel nuovo id dell'elemento clonato
 */
function clearChildrenId(obj) {
    obj.children().each(function(i) {
    	if (!(typeof $(this).attr("id") === "undefined")) { /* i 3 uguali sono corretti */
        	$(this).attr("id", "clone" + $(this).attr("id"));
    	}
    	clearChildrenId($(this));
    });
}
