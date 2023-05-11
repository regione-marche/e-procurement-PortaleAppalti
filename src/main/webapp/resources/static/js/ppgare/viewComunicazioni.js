$(document).ready(function(){
	//si fissa a 5 il numero massimo di comunicazioni visualizzate in apertura della pagina
	var maxListaComunicazioni = 5;
	
	// allo startup tutte le comunicazioni vengono semplificate visualizzando i 
	// primi maxListaComunicazioni e aggiugendo un elemento per l'attivazione 
	// della visualizzazione degli elementi successivi
	$("ul.list-comunicazioni").each(function( index ) {
		var selected = $(this).children().length;
		if (selected > maxListaComunicazioni) {
			$(this).children().each(function (i) {
				if (i == maxListaComunicazioni) {
					$(this).before("<li class='showhidden" + index + "'><span class='title'>Visualizza tutte le altre comunicazioni (" + (selected - maxListaComunicazioni) + ")</span></li>");
				}
				if (i >= maxListaComunicazioni) {
					$(this).addClass('noscreen hidden' + index);
				}
			});
		}
	});

	// al click sull'elemento aggiunto lo elimino e ripresento i rimanenti 
	// elementi che erano stati nascosti
	$("li[class^=showhidden]").on("click", function(){
		var indice = $(this).attr('class').substring(10);
		$('li.hidden' + indice).removeClass('noscreen');
		$(this).remove();
	});
});
