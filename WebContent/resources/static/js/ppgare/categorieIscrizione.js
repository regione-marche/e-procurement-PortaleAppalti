
$(document).ready(function() {	
	// Nella pagina dove verrà utilizzato questo script
	// inserire nello script della pagina le seguenti dichiarazioni var
	//	var labelCoprimiCategorie = '...';
	//	var labelEspandiCategorie = '...';
	
	// gestisci l'espansione/contrazione degli alberi in base al click
	$('[id^="title_"]').on("click", function() {
		if ($(this).attr('class') == 'expand') {
			$('ul[id^="tree_' + this.id + '"]').fadeIn('slow');
			$(this).attr('class', 'collapse');
			$(this).attr('title', labelCoprimiCategorie);
		}
		else {
			$('ul[id^="tree_' + this.id + '"]').fadeOut('slow');
			$(this).attr('class', 'expand');
			$(this).attr('title', labelEspandiCategorie);
		}
	});
	
	// tutti gli alberi compressi
	 var $treeview =  $(".filetree").treeview({
		collapsed: collapseTree
	});
	
	var mercatoElettronico = $("#mercatoElettronico").val();
	if (mercatoElettronico) {
		$("span", $treeview).unbind("click.treeview");
	}

	// inserisci i contatori occorrenze sui nodi
	$("span.folder").each(function(index) {
		var count = $(this).parent().find("span.file").length;
		$(this).append(" <em>[" + count + "]</em>");
	});
});