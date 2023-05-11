<%@ taglib prefix="wp"  uri="aps-core.tld" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="s"   uri="/struts-tags" %>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>

<!-- OBSOLETO <s:set name="helper" value="%{#session['offertaEconomica']}" /> -->
<s:set var="bustaEconomica" value="%{#session.dettaglioOffertaGara.bustaEconomica}" />
<s:set var="helper" value="%{#bustaEconomica.helper}" />
<c:set var="chelper" value="${sessionScope.dettaglioOffertaGara.bustaEconomica.helper}" />
<es:checkCustomization var="withAdvancedUI" objectId="UI-ADVANCED" attribute="DATATABLE" feature="ACT" />

<c:set var="maxDimensioneDescrizione" value="20" />
<c:set var="maxDecimaliRibasso"><s:property value="%{#helper.numDecimaliRibasso}" /></c:set>

<s:set name="prezzoUnitario" value="%{#helper.prezzoUnitario}" />
<s:set name="importoUnitario" value="%{#helper.importoUnitario}" />
<s:set name="importoUnitarioStringaNotazioneStandard" value="%{#helper.importoUnitarioStringaNotazioneStandard}" />
<s:set name="ribassoPercentuale" value="%{#helper.ribassoPercentuale}" />
<s:set name="totaleOffertaPrezziUnitari" value="%{#helper.totaleOffertaPrezziUnitari}" />
<s:set name="attributiAggiuntivi" value="%{#helper.attributiAggiuntivi}" />
<c:if test="${empty totaleOffertaPrezziUnitari}">
	<s:set name="totaleOffertaPrezziUnitari" value="0" />
</c:if>
<s:set name="ribassiPesati" value="%{#helper.gara.tipoRibasso != null && #helper.gara.tipoRibasso == 3}" />
<s:set name="selezioneVociAttiva" value="%{#helper.gara.iterGara != null && #helper.gara.iterGara == 8}" />
<c:set var="itergara8"><s:property value="%{#helper.gara.iterGara != null && #helper.gara.iterGara == 8}"/></c:set>
<s:set var="codiceTitolo" value="%{#bustaEconomica.codiceLotto}" />



<wp:headInfo type="CSS" info="jquery/jquery-ui/jquery-ui.css" />
<link href="<wp:resourceURL/>static/css/parsley.css" rel="stylesheet"></link>
<script src="<wp:resourceURL/>static/js/jquery-ui-1.12.1.min.js"></script>
<script src="<wp:resourceURL/>static/js/jquery.dataTables.min.js"></script>

<script>
$(document).ready(function() {
	
	var isUIAdvanced 	 = ${withAdvancedUI};
	var DECIMALI_RIBASSO = ${maxDecimaliRibasso};
	var DECIMALI_MAX	 = 5;
	var itergara8 	 	 = ${itergara8};
	
	// se js abilitato rimuovo l'avviso che per essere usabile la pagina
	// serve js abilitato
	$('#noJs').remove();
	
	$('[name="discriminante"]').on("click", function(){
 		$("#azioneMain").attr("name", "method:undo");
 		$("#formViewPrezziUnitari").submit();
	});

	// abilita/disabilita gli input della form
	if(itergara8) {
		$('[name="voceSelezionata"]').each(function( index ) {
			abilitaVoceSelezionata($(this)); 
		});
	}
	
	// le descrizioni piu' lunghe di maxDimensioneDescrizione vengono
	// troncate
	// ma si aggiunge un'icona per attivare la visualizzazione della
	// descrizione per esteso
	$('a[id^="viewdesc_"],a[id^="editdesc_"]').on("click", function(){
		var id = $(this).attr('id').substring(9);
	    if ($(this).attr('class') == 'expand-info') {
	    	$(this).attr('class', 'collapse-info');
	    	$(this).attr('title', 'Comprimi la descrizione');
	    	$(this).text($('#fulldesc_'+id).val());
	    }
	    else {
	    	$(this).attr('class', 'expand-info');
	    	$(this).attr('title', 'Espandi la descrizione');
	    	$(this).text($('#fulldesc_'+id).val().substring(0,${maxDimensioneDescrizione}));
	    }
	});
	
	// al cambio di un prezzo lo si valida, si calcola eventualmente
	// l'importo e si aggiorna il totale
	$('[name="prezzoUnitario"]').on("change", 
		function() {
			prezzoUnitarioChange($(this));
		}
	);
	
	// gestione dell'evento "change" per il prezzoUnitario  
	function prezzoUnitarioChange(txtPrezzo) {
		var id = txtPrezzo.attr('id');
		var indice = id.substring(id.indexOf("_") + 1);
		var quantita = $("#quantita_"+indice).val();
		var importoText = "";
		var importo = "";
		txtPrezzo.next("div").empty();
		var prezzo = txtPrezzo.val();
		// controllo il formato del campo sia cifre e punto decimale
		if (prezzo != ".") {
			if(!txtPrezzo.hasClass("parsley-error")){
				// in caso positivo aggiorno l'importo
				var attr = txtPrezzo.attr('aria-invalid');
				if (typeof attr !== 'undefined' && attr !== false)
					txtPrezzo.removeAttr("aria-invalid");

				if (prezzo == "") {
					// ho sbiancato il prezzo, sbianco l'importo
					importo = "";
					importoText = "";
				} else {
					// ho valorizzato/aggiornato il prezzo, calcolo l'importo
					prezzo = parseFloat(txtPrezzo.val());
					txtPrezzo.val(prezzo);
					importo = roundNumber(prezzo * quantita, DECIMALI_MAX);
					importoText = importo;
				}
			} else
				txtPrezzo.attr("aria-invalid", "true");
		} else {
			// in caso negativo aggiungo il messaggio
			// txtPrezzo.next("div").append();
			txtPrezzo.next("div").append("<wp:i18n key='LABEL_FORMATO_DECIMALE_NON_VALIDO'/>");
			txtPrezzo.attr("aria-invalid", "true");
		}
		// aggiorno la colonna importo (oppure sbianco in caso di errore)
		$("#viewImporto_"+indice).html(importoText);
		$("#editImporto_"+indice).val(importo);
		// ricalcolo il totale
		calcolaTotaleEdit();
		// visualizzo l'alert per salvare le modifiche
		showSalvaModifiche();
	}
	
	// al cambio di un ribasso percentuale lo si valida, si calcola eventualmente
	// il ribasso pesato e il prezzo unitario e si aggiorna il totale
	$('[name="ribassoPercentuale"]').on("change", 
		function() {
			var id = $(this).attr('id');
			var indice = id.substring(id.indexOf("_") + 1);
			var peso = $("#peso_"+indice).val();
			var ribassoPesatoText = "";
			var ribassoPesato = "";
			var prezzoUnitarioText = "";
			var prezzoUnitario = "";
			$(this).next("div").empty();
			var ribassoPercentuale = $(this).val();
			// controllo il formato del campo sia cifre e punto decimale
			if (ribassoPercentuale != ".") {
				if(!$("#ribassoPercentuale_"+indice).hasClass("parsley-error")){
					// in caso positivo aggiorno l'importo
					if (ribassoPercentuale == "") {
						// ho sbiancato il ribasso pesato
						ribassoPesatoText = "";
						ribassoPesato = "";
						prezzoUnitarioText = "";
						prezzoUnitario = "";
					} else {
						// ho valorizzato/aggiornato il ribasso percentuale, calcolo
						// il ribasso pesato 
						ribassoPercentuale = parseFloat($(this).val());
						$(this).val(ribassoPercentuale);
						var ribassoPesato = roundNumber(peso * ribassoPercentuale / 100, DECIMALI_RIBASSO);
						ribassoPesatoText = ribassoPesato;
						
						var prezzoBase = parseFloat($("#prezzoBase_"+indice).val());
						prezzoUnitario = roundNumber(prezzoBase * (1 - ribassoPercentuale/100), DECIMALI_MAX);
						prezzoUnitarioText = prezzoUnitario;
					}
				}
			} else {
				// in caso negativo aggiungo il messaggio
				// $(this).next("div").append();
				$(this).next("div").append("<wp:i18n key='LABEL_FORMATO_DECIMALE_NON_VALIDO'/>");
			}
			// aggiorno la colonna ribasso pesato (oppure sbianco in caso di errore)
			if( $("#ribassoPercentuale_"+indice) != null) {
				$("#viewRibassoPesato_"+indice).html(ribassoPesatoText);
				$("#ribassoPesato_"+indice).val(ribassoPesato);
				$("#viewPrezzoUnitario_"+indice).html(prezzoUnitarioText);
				$("#prezzoUnitario_"+indice).val(prezzoUnitario);
			}
			// genero l'evento per ricalcolare il prezzo unitario
			prezzoUnitarioChange($("#prezzoUnitario_"+indice));
		}
	);

	// visualizzo l'alert per salvare le modifiche
	function showSalvaModifiche() { 
		if ($("#alertModifiche").hasClass("noscreen")) {
			$("#alertModifiche").removeClass("noscreen");
			//$('#tableEditVociDettaglio').dataTable({
			//	destroy: true,
			//	"sScrollX": "99%",
			//	scrollY: altezzaDialog - (220 + 91), 
			//	"scrollCollapse": true
			//});
			//// dimensiono la tabella al massimo spazio disponibile (tolgo spazio del balloon)
			//$('#tableEditVociDettaglio').parent('.dataTables_scrollBody').css('height', (altezzaDialog - (220 + 91)) + 'px');
			resizeTableEditVociDettaglio();
		}
	}
	
	// ridimensiono la tabella al massimo spazio disponibile
	function resizeTableEditVociDettaglio() {
		//var tableHeight = altezzaDialog - 220;
		var errHeight = $("#alertModifiche").outerHeight();
	    //var dlgHeight = $('.ui-dialog").height();
	    var dlgHeight = $("#dialogEditVoci").innerHeight();
	    var dlgColObblHeight = $("#alertInputObbligatorie").outerHeight();
	    var dlgTitleHeight = $(".dataTables_scrollHead").outerHeight();
	    var dlgBtnHeight = $(".ui-dialog-buttonpane").outerHeight();
	    var totImportoHeight = $("#dialogTotaleImportoEdit").outerHeight();
	    var tableHeight = dlgHeight - dlgColObblHeight - dlgTitleHeight - dlgBtnHeight - totImportoHeight - 10;
//alert(dlgHeight + " " + tableHeight + " " + errHeight + " " + totImportoHeight);
		if ( !$("#alertModifiche").hasClass("noscreen") ) {
			//var tableHeight = altezzaDialog - (220 + 91);
			tableHeight = tableHeight - errHeight;
		}

		if (isUIAdvanced) {
            //scrollY: tableHeight,
            $('#tableEditVociDettaglio').dataTable({
                destroy: true,
                "sScrollX": "99%",
                scrollY: tableHeight,
                "scrollCollapse": true
            });
            // dimensiono la tabella al massimo spazio disponibile (tolgo spazio del balloon)
            $('#tableEditVociDettaglio').parent('.dataTables_scrollBody').css('height', (tableHeight) + 'px');
		}
	}

	function calcolaTotaleEdit() {
		var totale = 0;
		$('[name="importo"]').each(function( index ) {
			var importo = $(this).val();
			if (importo != "") {
				if(itergara8) {
					var checked = $("#voceSelezionata_"+index).prop("checked");
					if(checked) {
						totale += importo*1; // la moltiplicazione converte in double
					}
				} else {
					totale += importo*1; // la moltiplicazione converte in double
				}
			}
		});
		totale = roundNumber(totale, DECIMALI_MAX);
		$("#totaleImportoEdit").html(totale + "&nbsp;&euro;");
	}
	
	function roundNumber(value, decimals) {
		var n = Math.pow(10.0, decimals);
		//var a = Math.trunc(value);		// IE non riconosce Math.trunc() !!!
		var a = (isNaN(value) ? NaN : (value > 0 ? Math.floor(value) : Math.ceil(value)));
		a = a + Math.round((value - a) * n) / n;
		return Math.round(a * n) / n;
	}	
	
    // --- validazione WCAG 2.1 ---
	function fixAriaUIDialog(value) {
		//$(".noscreen").attr("aria-hidden", value);
		//$(".viewport-container").attr("aria-hidden", value);
		//$(".ui-widget-overlay").attr("aria-hidden", value);
		//$(".ui-front").attr("aria-hidden", value);

    }
	
	// variabile generale perche' in caso di edit dei dati compare un
	// promemoria, e quindi si rigenera il datatable piu' piccolo di 100px
	if (isUIAdvanced) {
        var altezzaDialog;
        $.extend($.fn.dataTable.defaults, {
            "paging": false,
            "ordering": false,
            "info": false,
            "searching" : false
        });


	    // applico la datatable alla lista di riepilogo prodotti
		$('#tableViewVociDettaglio').dataTable({
			scrollY: 300,
			scrollX: true,
			scrollCollapse: true
		});
	}
	
    // al click sul bottone modifica si apre la dialog dimensionata
	// opportunamente e
    // si visualizza all'interno la tabella con i dati da editare
    $("body").on( "click", "#modifica", 
       	function() {
    		// si dimensiona la dialog centrale e 100px piu' piccola
			// rispetto alla dimensione del browser
			var larghezzaSchermo = $(window).width();
			var altezzaSchermo = $(window).height();
		    var larghezzaDialog = larghezzaSchermo - 100;
		    altezzaDialog = altezzaSchermo - 100;
		    $( "#dialogEditVoci" ).dialog({
		    	title: "<wp:i18n key='LABEL_VOCI_SOGGETTE_RIBASSO'/>",
		    	autoOpen: true,
		    	closeOnEscape: false,
		    	width: larghezzaDialog,
		    	height: altezzaDialog,
		    	show: {
		    		effect: "blind",
		    		duration: 500
		        },
		        hide: {
		        	effect: "blind",
		        	duration: 500
		        },
		        modal: true,
		        resizable: false,
				focusCleanup: true,
				cache: false,
		        buttons:  
		        	{
		        		"<wp:i18n key='BUTTON_SAVE'/>": function() {
	    		    		// annullo le modifiche e ricarico la pagina principale
	    		    		$("#azioneDialog").attr("name", "method:save");
	    		    		$("#formEditPrezziUnitari").submit();
	    		    		fixAriaUIDialog("false");
	    		    	},
	    		    	"<wp:i18n key='BUTTON_WIZARD_CANCEL'/>": function() {
	    		    		// annullo le modifiche e ricarico la pagina principale
	    		    		$("#azioneDialog").attr("name", "method:undo");
	    		    		$('#formEditPrezziUnitari').off('submit.Parsley');
	    		    		$("#formEditPrezziUnitari").submit();
	    		    		fixAriaUIDialog("false");
	    		    	}
		          }
		    });
		    $(".ui-dialog-titlebar").hide();
			
			// si attiva la datatable interna (ad ogni click e apertura
			// dialog la si rigenera per ovviare ai
		    // problemi emersi di dimensionamento colonne e di allineamento
			// tra testata e corpo tabella)
		    //var tableHeight = altezzaDialog - 220;
		    //$('#tableEditVociDettaglio').dataTable({
			//	destroy: true,
			//	"sScrollX": "99%",
			//	scrollY: tableHeight,
			//	"scrollCollapse": true
			//});
			//// dimensiono il corpo della tabella al massimo spazio disponibile
			//// (tolgo spazio del balloon)
			//$('#tableEditVociDettaglio').parent('.dataTables_scrollBody').css('height', (tableHeight) + 'px');
			resizeTableEditVociDettaglio();

			// fix validazione WCAG 2.1 criterio ARIA-UI 
			fixAriaUIDialog("true");

            //Rimosso per segnalazione wcag
            $("div.dataTables_scrollFootInner").remove()
    	}
    );
    
	// al cambio del check "offerta?", si sbianca e si disabilita la riga
	function abilitaVoceSelezionata(obj) {
		var id = obj.attr('id');
		var indice = id.substring(id.indexOf("_") + 1);
		var checked = obj.prop("checked");
		if(!checked) {
			//$("#dataConsegnaOfferta_"+indice).val("");
			//$("#tipo_"+indice).val("");
			//$("#note_"+indice).val("");
			//$("#viewPrezzoUnitario_"+indice).html("0");
			//$("#prezzoUnitario_"+indice).val("0");
		}
//		$("#dataConsegnaOfferta_"+indice).prop("disabled", !checked);
		$("#tipo_"+indice).prop("disabled", !checked);
		$("#note_"+indice).prop("disabled", !checked);
		$("#viewPrezzoUnitario_"+indice).prop("disabled", !checked);
		$("#prezzoUnitario_"+indice).prop("disabled", !checked);
	}
	
	$('[name="voceSelezionata"]').on("change", 
		function() {
			abilitaVoceSelezionata($(this));
			calcolaTotaleEdit();
		}
	);

});
</script>


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-list">

	<h2><wp:i18n key="TITLE_PAGE_GARETEL_OFFERTA_ECONOMICA" /> [${codiceTitolo}]</h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/field_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/warning_javascript.jsp" />

	<jsp:include page="stepsOffertaTelematica.jsp" />

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_WIZ_PREZZI_UNITARI_OFFERTA_TEL"/>
	</jsp:include>
	
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/skip_form_buttons.jsp" />

    <form
        action="<wp:action path="/ExtStr2/do/FrontEnd/GareTel/variazionePrezziUnitari.action" />&amp;${tokenHrefParams}&amp;codice=${codice}"
        method="post"
        id="Id_Form_Modifica_Excel" >
        <jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
    </form>


	<form action="<wp:action path="/ExtStr2/do/FrontEnd/GareTel/processPageOffTelPrezziUnitari.action" />" method="post" id="formViewPrezziUnitari">
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
		
		<fieldset>
			<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_OFFERTA_PREZZI" /></legend>
			
			<s:if test="%{#helper.vociDettaglioNoRibasso.size() > 0}">
				<div class="fieldset-row first-row last-row special">
					<div class="label">
						<label><wp:i18n key="LABEL_VISUALIZZA_VOCI" /> : </label>
					</div>
					<div class="element">
						<input type="radio" value="${VOCI_SOGGETTE_RIBASSO}" name="discriminante" aria-label='<wp:i18n key="LABEL_VOCI_SOGGETTE_RIBASSO" />'
							<s:if test="%{discriminante == VOCI_SOGGETTE_RIBASSO}">checked="checked"</s:if> /> <wp:i18n key="LABEL_VOCI_SOGGETTE_RIBASSO" /> 
						&nbsp;&nbsp;&nbsp;&nbsp;
						<input type="radio" value="${VOCI_NON_SOGGETTE_RIBASSO}" name="discriminante" aria-label='<wp:i18n key="LABEL_VOCI_NON_SOGGETTE_RIBASSO" />'
							<s:if test="%{discriminante == VOCI_NON_SOGGETTE_RIBASSO}">checked="checked"</s:if> /> <wp:i18n key="LABEL_VOCI_NON_SOGGETTE_RIBASSO" /> 
					</div>
				</div>
			</s:if>

			<div class="table-container">
			
				<table id="tableViewVociDettaglio" class="info-table" >
					<caption style="display:none;">Tabella riepilogo lavorazioni e forniture di dettaglio per l'offerta prezzi unitari</caption>
					<thead>
						<tr>
							<s:if test="%{#selezioneVociAttiva}">
								<th scope="col"><wp:i18n key="LABEL_LAVORAZIONE_SELEZIONATA" /></th>
							</s:if>
							<th scope="col"><wp:i18n key="LABEL_VOCE" /></th>
							<th scope="col"><wp:i18n key="LABEL_DESCRIZIONE" /></th>
							<th scope="col"><wp:i18n key="LABEL_DESCRIZIONE_ESTESA" /></th>
							<th scope="col"><wp:i18n key="LABEL_UNITA_MISURA" /></th>
							<th scope="col"><wp:i18n key="LABEL_QUANTITA" /></th>
							<c:if test="${discriminante==VOCI_SOGGETTE_RIBASSO}">
								<s:if test="%{#helper.gara.tipoRibasso==3}">
									<th scope="col"><wp:i18n key="LABEL_PREZZO_BASE" /></th>
									<th scope="col"><wp:i18n key="LABEL_PESO" /></th>
									<th scope="col"><wp:i18n key="LABEL_RIBASSO_PERC" /></th>
									<th scope="col"><wp:i18n key="LABEL_RIBASSO_PESATO" /></th>
								</s:if>
							</c:if>
							<th scope="col"><wp:i18n key="LABEL_PREZZO" /></th>
							<th scope="col"><wp:i18n key="LABEL_IMPORTO" /></th>
						</tr>
					</thead>
					<tbody>	
						<s:iterator value="vociDettaglio" status="stat">
							<tr>
								<s:if test="%{#selezioneVociAttiva}">
									<td>
									    <label for="voceSelezionata_no_edit_<s:property value='%{#stat.index}' />"><wp:i18n key="LABEL_LAVORAZIONE_SELEZIONATA" /></label>
										<input type="checkbox" disabled="disabled" id="voceSelezionata_no_edit_<s:property value='%{#stat.index}' />" <s:if test='%{checkVoceSelezionata[#stat.index]}'>checked="checked"</s:if> />
									</td>
								</s:if>
								<td><s:property value="codice" /></td>
								<td><s:property value="voce" /></td>
								<td>
									<c:choose>
										<c:when test="${fn:length(descrizione) > maxDimensioneDescrizione}">
											<a class="expand-info" id="viewdesc_${stat.index}" title="Espandi la descrizione"><c:out value="${fn:substring(descrizione, 0, maxDimensioneDescrizione)}" />
											</a>
											<input type="hidden" id="fulldesc_${stat.index}" value="<s:property value="descrizione" />" />
										</c:when>
										<c:otherwise>
											<s:property value="descrizione" />
										</c:otherwise>
									</c:choose>
								</td>
								<td><s:property value="unitaMisura" /></td>
								<td><s:text name="format.numberdec"><s:param value="quantita"/></s:text></td>
								<c:if test="${discriminante == VOCI_SOGGETTE_RIBASSO}">
									<s:if test="%{#helper.gara.tipoRibasso==3}">
										<td><s:text name="format.money5dec"><s:param value="prezzoUnitarioBaseGara"/></s:text>&nbsp;&euro;</td>
										<td><s:text name="format.numberdec"><s:param value="peso"/></s:text></td>
										<td><s:if test="%{ribassoPercentuale != null}"><s:text name="format.numberdec"><s:param value="ribassoPercentuale"/></s:text>&nbsp;%</s:if></td>
										<td><s:if test="%{ribassoPercentuale != null && ribassoPesato != null}"><s:text name="format.numberdec"><s:param value="ribassoPesato"/></s:text>&nbsp;%</s:if></td>
									</s:if>
								</c:if>
								<td>
									<c:choose>
										<c:when test="${discriminante == VOCI_SOGGETTE_RIBASSO}">
											<s:if test="%{#prezzoUnitario[#stat.index] != null}">
												<s:text name="format.money5dec"><s:param value="%{#prezzoUnitario[#stat.index]}"/></s:text>&nbsp;&euro;
											</s:if>
										</c:when>
										<c:otherwise>
											<s:if test="%{prezzoUnitario != null}">
												<s:text name="format.money5dec"><s:param value="%{prezzoUnitario}"/></s:text>&nbsp;&euro;
											</s:if>
										</c:otherwise>
									</c:choose>
								</td>
								<td>
									<c:choose>
										<c:when test="${discriminante == VOCI_SOGGETTE_RIBASSO}">
											<s:if test="%{#importoUnitario[#stat.index] != null}">
												<s:text name="format.money5dec"><s:param value="%{#importoUnitario[#stat.index]}"/></s:text>&nbsp;&euro;
											</s:if>
										</c:when>
										<c:otherwise>
											<s:if test="%{importoUnitario != null}">
												<s:text name="format.money5dec"><s:param value="%{importoUnitario}"/></s:text>&nbsp;&euro;
											</s:if>
										</c:otherwise>
									</c:choose>
								</td>
							</tr>
						</s:iterator>
					</tbody>
				</table>
				
				<s:if test="%{discriminante != VOCI_NON_SOGGETTE_RIBASSO}">
					<div class="back-link">
						<strong>
						<wp:i18n key="LABEL_IMPORTO_TOTALE" /> : <span id="totaleImportoView"><s:text name="format.money5dec"><s:param value="%{#totaleOffertaPrezziUnitari}"/></s:text>&nbsp;&euro;</span>
						</strong>
					</div>
				</s:if>
			</div>
		</fieldset>

		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/skip_main_content_from_buttons.jsp" />
		
		<div class="azioni">
			<input type="hidden" name="codice" value="<s:property value="%{#helper.codice}"/>"/>
			<input type="hidden" id="azioneMain" value="Azione"/>
			
			<s:if test="%{discriminante != VOCI_NON_SOGGETTE_RIBASSO}">
				<input type="button" value="<wp:i18n key='BUTTON_EDIT' />" title="<wp:i18n key='BUTTON_EDIT' />" class="button" id="modifica" />
                <input type="submit"
                    value="<wp:i18n key='LABEL_MODIFICA_DA_EXCEL' />"
                    title="<wp:i18n key='LABEL_MODIFICA_DA_EXCEL' />"
                    class="button"
                    id="modifica_excel"
                    form="Id_Form_Modifica_Excel"
                />
			</s:if>
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_next.jsp" />
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_cancel.jsp" />
		</div>
	</form>

</div>


<%-- Form modale di compilazione dati --%>
<s:if test="%{discriminante != VOCI_NON_SOGGETTE_RIBASSO}">
	<div id="dialogEditVoci" style="display: none;"	>
		<div id="listaVociContainer" style="width: 100%;">
			
			<div id="alertModifiche" class="balloon noscreen">
				<div class="balloon-content balloon-alert">
					<wp:i18n key="LABEL_MODIFICHE_NON_ANCORA_SALVATE" />
				</div>
			</div>
			
			<span id="alertInputObbligatorie"><wp:i18n key="LABEL_COLONNE_OGGLIGATORIE" /></span>
			<form action="<wp:action path="/ExtStr2/do/FrontEnd/GareTel/processPageOffTelPrezziUnitari.action" />" 
					method="post" id="formEditPrezziUnitari" >
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
				
				<table id="tableEditVociDettaglio" class="info-table">
					<caption style="display:none;">Tabella di modifica lavorazioni e forniture di dettaglio per l'offerta prezzi unitari</caption>
					<thead>
						<tr>
							<s:if test="%{#selezioneVociAttiva}">
								<th scope="col"><wp:i18n key="LABEL_LAVORAZIONE_SELEZIONATA" /></th>
							</s:if>
							
							<th scope="col"><wp:i18n key="LABEL_VOCE" /></th>
							<th scope="col"><wp:i18n key="LABEL_DESCRIZIONE" /></th>
							<th scope="col"><wp:i18n key="LABEL_DESCRIZIONE_ESTESA" /></th>
							<s:iterator value="%{#attributiAggiuntivi}">
								<c:set var="title_trunk" value="${fn:substring(descrizione, 0, 20)}"/>
								<c:set var="title_trunk" value="${fn:replace(title_trunk, ' ', '&nbsp;')}"/>
								<c:if test="${(fn:length(descrizione) > 20)}">
									<c:set var="title_trunk" value="${title_trunk}..." />
								</c:if>
								<th scope="col" title="<s:property value="descrizione" />">${title_trunk}<s:if test="obbligatorio"> (*)</s:if></th>
							</s:iterator>
							<s:if test="%{#helper.gara.tipoAppalto == APPALTO_LAVORI}"><th scope="col"><wp:i18n key="LABEL_A_CORPO_O_MISURA" /></th></s:if>

							<s:if test="%{visualizzaDataConsegna}">
								<th scope="col"><wp:i18n key="LABEL_DATA_CONSEGNA_RICHIESTA" /></th>
							</s:if>
							<s:if test="%{#selezioneVociAttiva}">
								<%--
								<th scope="col"><wp:i18n key="LABEL_DATA_CONSEGNA_OFFERTA" /> (*)</th> 
								--%> 
								<th scope="col"><wp:i18n key="LABEL_TIPO" /> (*)</th>
								<th scope="col"><wp:i18n key="LABEL_NOTE" /></th>
							</s:if>
							
							<th scope="col"><wp:i18n key="LABEL_UNITA_MISURA" /></th>
							<th scope="col"><wp:i18n key="LABEL_QUANTITA" /></th>
							<c:if test="${discriminante == VOCI_SOGGETTE_RIBASSO}">
								<s:if test="%{#ribassiPesati}">
									<s:if test="%{visualizzaPrezzoUnitario}">
										<th scope="col"><wp:i18n key="LABEL_PREZZO_BASE" /></th>
									</s:if>
									<th scope="col"><wp:i18n key="LABEL_PESO" /></th>
									<th scope="col"><wp:i18n key="LABEL_RIBASSO_PERC" /> (*)</th>
									<th scope="col"><wp:i18n key="LABEL_RIBASSO_PESATO" /></th>
								</s:if>
								<s:else>
									<s:if test="%{visualizzaPrezzoUnitario}">
										<th scope="col"><wp:i18n key="LABEL_PREZZO_BASE" /></th>
									</s:if>
								</s:else>
							</c:if>
							<th scope="col"><wp:i18n key="LABEL_PREZZO" /><s:if test="%{not #ribassiPesati}"> (*)</s:if></th>
							<th scope="col"><wp:i18n key="LABEL_IMPORTO" /></th>
						</tr>
					</thead>
					<tbody>
						<c:set var="lblRibassoPerc"><wp:i18n key="LABEL_RIBASSO_PERC" /></c:set>
						<c:set var="lblPrezzo"><wp:i18n key="LABEL_PREZZO" /></c:set>
						<c:set var="lblDataConsegnaOfferta"><wp:i18n key="LABEL_DATA_CONSEGNA_OFFERTA" /></c:set>
						<c:set var="lblTipo"><wp:i18n key="LABEL_TIPO" /></c:set>
						<c:set var="lblNote"><wp:i18n key="LABEL_NOTE" /></c:set>
						
						<s:iterator value="vociDettaglio" status="stat" var="item">
							<tr>
								<s:if test="%{#selezioneVociAttiva}">
									<td>
                                        <label for="voceSelezionata_<s:property value='%{#stat.index}' />"><wp:i18n key="LABEL_LAVORAZIONE_SELEZIONATA" /></label>
										<s:checkbox name="voceSelezionata" id="voceSelezionata_%{#stat.index}"
													value="%{checkVoceSelezionata[#stat.index]}" fieldValue="%{#stat.index}"/>										
									</td>
								</s:if>
								
								<td><s:property value="codice" /></td>
								<td><s:property value="voce" /></td>
								<td>
									<c:choose>
										<c:when test="${fn:length(descrizione) > maxDimensioneDescrizione}">
											<a class="expand-info" id="editdesc_${stat.index}" title='<wp:i18n key="TITLE_ESPANDI_DESCRIZIONE" />'>
												<c:out value="${fn:substring(descrizione, 0, maxDimensioneDescrizione)}" />
											</a>
										</c:when>
										<c:otherwise>
											<s:property value="descrizione" />
										</c:otherwise>
									</c:choose>
								</td>
								
								<s:iterator value="%{#attributiAggiuntivi}" var="attr">
									<s:set var="tipoValidatore" value="tipo"/>
									<s:if test="%{#tipoValidatore == TIPO_VALIDATORE_DATA}">
										<%-- Data --%>
										<td><input type="text" id="<s:property value="codice" />_<s:property value="%{#stat.index}" />" name="<s:property value="codice" />" data-parsley-trigger="change" placeholder="GG/MM/AAAA" data-parsley-pattern="^(3[01]|[12][0-9]|0[1-9])/(1[0-2]|0[1-9])/[0-9]{4}$" value="<s:property value="%{#helper.valoreAttributiAgg[codice][#stat.index]}"/>"
													aria-label="${descrizione} ${voce}" /></td>
								  	</s:if>
									<s:elseif test="%{#tipoValidatore == TIPO_VALIDATORE_IMPORTO or #tipoValidatore == TIPO_VALIDATORE_NUMERO}">
										<%-- Importo o Numero --%>
										<%-- ^\d{0,15}(\.\d{0,2})?$  --%>
										<c:set var="maxDecimali" value="${fn:split(formato,'.')[0]}" /><c:set var="maxDopoVirgola" value="${fn:split(formato,'.')[1]}" />																			
										<td><input type="text" id="<s:property value="codice" />_<s:property value="%{#stat.index}" />" name="<s:property value="codice" />" data-parsley-trigger="change" data-parsley-pattern="<c:out value="^\d{0," /><c:out value="${maxDecimali}"/><c:out value="}"/><c:if test="${maxDopoVirgola == null}"><c:out value="$"/></c:if><c:if test="${maxDopoVirgola != null}"><c:out value="(\.\d{0," /><c:out value="${maxDopoVirgola}" /><c:out value="})?$" /></c:if>" value="<s:property value="%{#helper.valoreAttributiAgg[codice][#stat.index]}"/>"
													aria-label="${descrizione} ${voce}" /></td>
									</s:elseif>
									<s:elseif test="%{#tipoValidatore == TIPO_VALIDATORE_TABELLATO}">
										<%-- Tabellato --%>
										<td>
											<wp:i18n key="OPT_CHOOSE_VALORE" var="headerValueCodice" />
											<s:select list="maps[codice]" id="%{codice +'_'+ #stat.index}" name="%{codice}" value="%{#helper.valoreAttributiAgg[codice][#stat.index]}" headerKey="" headerValue="%{#attr.headerValueCodice}"
													aria-label="${descrizione} ${voce}" />
										</td>
									</s:elseif>
									<s:elseif test="%{#tipoValidatore == TIPO_VALIDATORE_NOTE}">
										<%-- Note --%>
										<td><textarea id="<s:property value="codice" />_<s:property value="%{#stat.index}" />" name="<s:property value="codice" />" data-parsley-trigger="change" data-parsley-maxlength="<s:property value="formato"/>" rows="3" cols="20" 
													aria-label="${descrizione} ${voce}" maxlength="2000" ><s:property value="%{#helper.valoreAttributiAgg[codice][#stat.index]}"/></textarea></td>
									</s:elseif>
									<s:elseif test="%{#tipoValidatore == TIPO_VALIDATORE_FLAG}">
										<%-- Flag S/N --%>
										<td>
											<s:select list="maps['sino']" id="%{codice +'_'+ #stat.index}" name="%{codice}" value="%{#helper.valoreAttributiAgg[codice][#stat.index]}" headerKey=""  headerValue="--" 
													aria-label="${descrizione} ${voce}" />
										</td>
									</s:elseif>
									<s:elseif test="%{#tipoValidatore == TIPO_VALIDATORE_STRINGA}">
										<%-- Stringa --%>
										<td><input type="text" id="<s:property value="codice" />_<s:property value="%{#stat.index}" />" name="<s:property value="codice" />" data-parsley-trigger="change" data-parsley-maxlength="<s:property value="formato"/>" value="<s:property value="%{#helper.valoreAttributiAgg[codice][#stat.index]}"/>" data-parsley-trim-value="true"
													aria-label="${descrizione} ${voce}" /></td>
									</s:elseif>
									<s:else>
										<%-- ERRORE!! --%>
										<td><input type="text" name="<s:property value="codice" />" 
													aria-label="${descrizione} ${voce}" /></td>
									</s:else>
								</s:iterator>
								
								<s:if test="%{#helper.gara.tipoAppalto == APPALTO_LAVORI}">
									<td><s:property value="tipoClassificazione" /></td>
								</s:if>

								<s:if test="%{visualizzaDataConsegna}">
									<td>
										<s:date name="dataConsegnaRichiesta" format="dd/MM/yyyy" />
									</td>
								</s:if>
								<s:if test="%{#selezioneVociAttiva}">
									<%--
									<td>
										<input type="text" id="dataConsegnaOfferta_<s:property value="%{#stat.index}" />" name="dataConsegnaOfferta" 
												data-parsley-trigger="change" placeholder="GG/MM/AAAA" data-parsley-pattern="^(3[01]|[12][0-9]|0[1-9])/(1[0-2]|0[1-9])/[0-9]{4}$"
												value="<s:date name='dataConsegnaOfferta' format='dd/MM/yyyy' />"
												size="10" maxlength="10"
												aria-label="${lblDataConsegnaOfferta}" />
									</td>
									--%>
									<td>
										<wp:i18n key="OPT_CHOOSE_VALORE" var="headerValueCodice" />
										<s:select list="maps['tipologiaProdotto']" id="tipo_%{#stat.index}" name="tipo" 
												value="%{tipo}" 
												headerKey="" headerValue="%{#attr.headerValueCodice}"
												aria-label="${lblTipo}" />
									</td>
									<td>
										<s:textarea id="note_%{#stat.index}" name="note" 
													value="%{note}" 
													rows="2" cols="15"
                                                    maxlength="2000"
													aria-label="${lblNote}"/>
									</td>
								</s:if>
								
								<td><s:property value="unitaMisura" /></td>
								
								<td>
									<s:text name="format.numberdec"><s:param value="quantita"/></s:text>
									<s:hidden name="quantita" id="%{'quantita_' + #stat.index}" value="%{quantita}"></s:hidden>
								</td>
								
								<s:if test="%{#ribassiPesati}">
									<%-- RIBASSI PESATI --%>
									<s:set name="prezzoUni" value=""/>
									<s:if test="%{ribassoPercentuale != null}">
										<s:set name="prezzoUni" value="%{prezzoUnitarioBaseGara * (1 - ribassoPercentuale / 100)}"/>
									</s:if>
									
									<s:if test="%{visualizzaPrezzoUnitario}">
										<td>
											<s:if test="%{prezzoUnitarioBaseGara != null}"><s:text name="format.money5dec"><s:param value="prezzoUnitarioBaseGara"/></s:text>&nbsp;&euro;</s:if>
											<s:hidden name="prezzoBase" id="%{'prezzoBase_' + #stat.index}" value="%{prezzoUnitarioBaseGara}"></s:hidden>
										</td>
									</s:if>
									<td>
										<s:text name="format.numberdec"><s:param value="peso"/></s:text>
										<s:hidden name="peso" id="%{'peso_' + #stat.index}" value="%{peso}"></s:hidden>
									</td>
									<td>
										<input type="text" id="ribassoPercentuale_<s:property value='%{#stat.index}' />" name="ribassoPercentuale" 
												data-parsley-trigger="change" data-parsley-pattern="<c:out value="^\d{0,11}"/><c:if test="${maxDecimaliRibasso == null}"><c:out value="$"/></c:if><c:if test="${maxDecimaliRibasso != null}"><c:out value="(\.\d{0," /><c:out value="${maxDecimaliRibasso}" /><c:out value="})?$" /></c:if>"												
												data-parsley-ispercent="<c:if test="${maxDecimaliRibasso == null}">0</c:if><c:if test="${maxDecimaliRibasso != null}">${maxDecimaliRibasso}</c:if>"
												value="<s:property value='%{ribassoPercentualeString[#stat.index]}'/>" 
												aria-required="true"
												aria-label="${lblRibassoPerc}" />
									</td>
									<td>
										<span id="viewRibassoPesato_${stat.index}"><s:if test="%{ribassoPesato != null}"><s:text name="format.numberdec"><s:param value="ribassoPesato"/></s:text></s:if></span>&nbsp;%
										<s:hidden name="ribassoPesato" id="%{'ribassoPesato_' + #stat.index}" value="%{ribassoPesato}"></s:hidden>
									</td>
									<td>
										<span id="viewPrezzoUnitario_${stat.index}"><s:if test="%{#prezzoUni != null}"><s:text name="format.money5dec"><s:param value="prezzoUni"/></s:text></s:if></span>&nbsp;&euro;
										<s:hidden name="prezzoUnitario" id="%{'prezzoUnitario_' + #stat.index}" value="%{prezzoUni}"></s:hidden>
									</td>
								</s:if>
								<s:else>
									<s:if test="%{visualizzaPrezzoUnitario}">
										<td>
											<s:if test="%{prezzoUnitarioBaseGara != null}"><s:text name="format.money5dec"><s:param value="prezzoUnitarioBaseGara"/></s:text>&nbsp;&euro;</s:if>
											<s:hidden name="prezzoBase" id="%{'prezzoBase_' + #stat.index}" value="%{prezzoUnitarioBaseGara}"></s:hidden>
										</td>
									</s:if>
									<td>
										<c:set var="requiredPrezzo"><s:property value="%{not #ribassiPesati}"/></c:set>
										<s:textfield value="%{prezziUnitariString[#stat.index]}" name="prezzoUnitario" id="%{'prezzoUnitario_' + #stat.index}" 
											data-parsley-trigger="change" data-parsley-pattern="^\d{0,11}(\.\d{0,5})?$" data-parsley-type="number" 
											aria-required="${requiredPrezzo}" 
											aria-label="${lblPrezzo}" />
									</td>
								</s:else>

								<td>
									<span id="viewImporto_${stat.index}"><c:if test="${not empty importoUnitario[stat.index]}"><s:property value="%{#importoUnitarioStringaNotazioneStandard[#stat.index]}" /></c:if></span>
									<input type="hidden" name="importo" id="editImporto_${stat.index}" value="${chelper.importoUnitario[stat.index]}"/>&nbsp;&euro;
								</td>
							</tr>
						</s:iterator>
					</tbody>
				</table>
				<div id="dialogTotaleImportoEdit" class="back-link" >
					<strong>
					<wp:i18n key="LABEL_IMPORTO_TOTALE" /> : <span id="totaleImportoEdit"><s:text name="format.money5dec"><s:param value="totaleOffertaPrezziUnitari"/></s:text>&nbsp;&euro;</span>
					</strong>
				</div>
				
				<input type="hidden" name="codice" value="<s:property value="%{#helper.codice}"/>"/>
				<input type="hidden" name="method:undo" id="azioneDialog" value="Azione"/>
			</form>
		</div>
	</div>
</s:if>


<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/parsley_validators.jsp" />

<script type="text/javascript">
  $('#formEditPrezziUnitari').parsley();  
</script>
