<%@ taglib prefix="wp"  uri="aps-core.tld" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="s"   uri="/struts-tags" %>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>

<link href="<wp:resourceURL/>static/css/parsley.css" rel="stylesheet"></link>
<es:checkCustomization var="withAdvancedUI" objectId="UI-ADVANCED" attribute="DATATABLE" feature="ACT" />

<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/jquery_ui.jsp" />
<script src="<wp:resourceURL/>static/js/jquery.dataTables.min.js"></script>

<%-- Parametri da passare a questa jsp 
	
		vociDettaglio			<= QUESTO E' L'UNICO DA PASSARE ESTERNAMENTE!!!
		vociDettaglioAction
		vociDettaglioSummary
		vociDettaglioColonneVisibili
		vociDettaglioTitoliColonne
		vociDettaglioTipiColonne
		vociDettaglioColonneEditabili
		vociDettaglioColQta
		vociDettaglioColPrz
		vociDettaglioColImp
		discriminante		    1=VOCI_SOGGETTE_RIBASSO | 2=VOCI_NON_SOGGETTE_RIBASSO
		hiddenInput[...]

	Esempio di chiamata alla jsp
			
		<c:set var="vociDettaglio" scope="request" value="${...}"/>
		<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/aste/inc/dialogPrezziUnitari.jsp" />
			<jsp:param name="vociDettaglioSummary" value="..." />
			...
		</jsp:include> 
--%>

<c:set var="formatoInteger" value="15.0"/>
<c:set var="formatoDouble" value="15.5"/>	        <%-- sempre 5 decimali --%>
<c:set var="formatoString" value="20"/>
<c:set var="formatoString2" value="60"/>

<c:set var="maxDimensioneDescrizione" value="20" />
<s:set name="totaleOffertaPrezziUnitari" value="0" />
<c:if test="${empty totaleOffertaPrezziUnitari}">
	<s:set name="totaleOffertaPrezziUnitari" value="0" />
</c:if>

<c:set var="visibleColumns" value="${fn:split(param.vociDettaglioColonneVisibili, ',')}"/>
<c:set var="columnTitles" value="${fn:split(param.vociDettaglioTitoliColonne, ',')}"/>
<c:set var="columnFormats" value="${fn:split(param.vociDettaglioTipiColonne, ',')}" />
<c:set var="enabledColumns" value=""/>
<c:if test="${not empty param.vociDettaglioColonneEditabili}" >
	<c:set var="enabledColumns" value="${fn:split(param.vociDettaglioColonneEditabili, ',')}"/>
</c:if>


<div id="dialogEditVoci" style="display: none;">
	<form action="${param.vociDettaglioAction}" method="post" id="formEditPrezziUnitari">
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
		
		<div id="listaVociContainer" style="width: 100%">
			<div id="alertModifiche" class="balloon noscreen">
				<div class="balloon-content balloon-alert">
					<wp:i18n key="LABEL_MODIFICHE_NON_ANCORA_SALVATE" />
				</div>
			</div>
		
			<span><wp:i18n key="LABEL_COLONNE_OGGLIGATORIE" /></span>
			
			<table id="tableEditVociDettaglio" summary="${param.vociDettaglioSummary}" class="info-table">
				<thead>
					<c:forEach var="row" items="${vociDettaglio}" begin="0" end="0">
						<tr>
							<c:forEach var="colname" items="${visibleColumns}" varStatus="i">
								<c:catch>
									<th scope="col">
		             					${columnTitles[i.index]}
		             				</th>
		             			</c:catch>
			         		</c:forEach>
						</tr>
					</c:forEach>
				</thead>
				
				<!-- TIPO_VALIDATORE_DATA 	 	= 1 -->
				<!-- TIPO_VALIDATORE_IMPORTO 	= 2 -->
				<!-- TIPO_VALIDATORE_TABELLATO 	= 3 -->
				<!-- TIPO_VALIDATORE_NOTE 		= 4 -->
				<!-- TIPO_VALIDATORE_NUMERO 	= 5 -->
				<!-- TIPO_VALIDATORE_FLAG 		= 6 -->
				<!-- TIPO_VALIDATORE_STRINGA 	= 7 -->	
				<tbody>	
					<s:set var="rowIndex" value="-1"/>
					<c:forEach var="row" items="${vociDettaglio}" varStatus="stat">
						<s:set var="rowIndex" value="%{#rowIndex + 1}"/>
						<s:set var="inputIndex">${stat.index}</s:set>
						
						<%-- NB: verifica se la voce � soggetta a       --%>
						<%-- soloSicurezza o nonSoggettoRibasso         --%>
						<c:set var="rowReadonly" value="false"/>
						<c:if test="${row['soloSicurezza'] != null}">
							<c:if test="${row['soloSicurezza']}">
								<c:set var="rowReadonly" value="true"/>
							</c:if>
						</c:if>
						<c:if test="${row['nonSoggettoRibasso'] != null}">
							<c:if test="${row['nonSoggettoRibasso']}">
								<c:set var="rowReadonly" value="true"/>
							</c:if>
						</c:if>
			
						<c:if test="${!rowReadonly}">
							
							<tr>
								<s:set var="colIndex" value="-1"/>
								<c:forEach var="colname" items="${visibleColumns}" varStatus="i">
									<c:catch>
										<s:set var="sColname">${colname}</s:set>
				 						<s:set var="sRow" value="%{vociDettaglio[#rowIndex]}"/>
				 						<s:set var="inputValueS" value="%{#sRow[#sColname]}"/>
				 						<s:set var="tipoValidatore">${columnFormats[i.index]}</s:set>
										
										<s:set var="inputName">input_${colname}</s:set>
				 						<c:set var="inputValue" value="${row[colname]}"/>
				 						<c:set var="inputClass" value=""/>
	<!--  									20/01/2017 HA SMESSO DI FUNZIONARE...  -->
	<!--   								    PERCHE'?????????????????????   -->
	<%-- 			 						<c:forTokens var="cls" items="${inputValue.class.name}" delims="." > --%>
	<%-- 										<c:set var="inputClass" value="${fn:toLowerCase(cls)}"/> --%>
	<%-- 									</c:forTokens> --%>
				 						<c:set var="classname"><s:property value="#inputValueS.class.name"/></c:set>
				 						<c:forTokens var="cls" items="${classname}" delims="." >
											<c:set var="inputClass" value="${fn:toLowerCase(cls)}"/>
										</c:forTokens>
	
										<c:set var="inputEnabled" value="false"/>
										<c:if test="${enabledColumns != ''}" >
											<c:forEach var="item" items="${enabledColumns}">
												<c:if test="${fn:toLowerCase(item) eq fn:toLowerCase(colname)}" >
													<c:set var="inputEnabled" value="true"/>
												</c:if>	
											</c:forEach>
										</c:if>
										
				       					<%-- cerca nella tabella le colonne associate al dialog --%>
				       					<%-- e ricollega l'input al dialog                      --%>
				      					<c:if test="${fn:toLowerCase(colname) == fn:toLowerCase(param.vociDettaglioColQta)}" >
				      						<s:set var="inputName">quantita</s:set>
				      					</c:if>
				      					<c:if test="${fn:toLowerCase(colname) == fn:toLowerCase(param.vociDettaglioColPrz)}" >
				      						<s:set var="inputName">prezzoUnitario</s:set>
				      					</c:if>
				      					<c:if test="${fn:toLowerCase(colname) == fn:toLowerCase(param.vociDettaglioColImp)}" >
				      						<s:set var="inputName">viewImporto</s:set>
				      					</c:if>
		             					
			           					<%-- inserisci le colonne della tabella --%>
										<td <s:if test="%{#tipoValidatore == 2 || #tipoValidatore == 5}">class="money-content"</s:if>>
											<c:choose>
												<%-- voci soloSicurezza e nonSoggettoRibasso --%>
												<c:when test="${rowReadonly && inputEnabled}">
		          									<div id="<s:property value="%{inputName}" />2_<s:property value="%{inputIndex}" />" 
														name="<s:property value="%{inputName}" />2">
														${inputValue}
														<input type="hidden" 
															id="<s:property value="%{inputName}" />_<s:property value="%{inputIndex}" />" 
															name="<s:property value="%{inputName}" />" 
															value="${inputValue}"
														/>
													</div>
												</c:when>
												<%-- tutte le altre voci --%>
												<c:otherwise>
							              			<s:if test="%{#tipoValidatore == 1}"> 		<!-- TIPO_VALIDATORE_DATA = 1 -->
					  									<c:choose>
				             								<c:when test="${inputEnabled}">
																<input type="text" 
																	id="<s:property value="%{inputName}" />_<s:property value="%{inputIndex}" />" 
																	name="<s:property value="%{inputName}" />" 
																	data-parsley-trigger="change" placeholder="GG/MM/AAAA" 
																	data-parsley-pattern="^(3[01]|[12][0-9]|0[1-9])/(1[0-2]|0[1-9])/[0-9]{4}$" 
																	value="${inputValue}"
																	aria-label="${columnTitles[i.index]} ${inputIndex}"
																	/>
															</c:when>
															<c:otherwise>
																<div id="<s:property value="%{inputName}" />_<s:property value="%{inputIndex}" />" 
																	name="<s:property value="%{inputName}" />">
																	${inputValue}
																</div>
															</c:otherwise>
														</c:choose>
													</s:if>
													<s:elseif test="%{#tipoValidatore == 2 || #tipoValidatore == 5}"> 	<!-- TIPO_VALIDATORE_IMPORTO = 2,  TIPO_VALIDATORE_NUMERO = 5-->
					 									<c:set var="formato" value="${formatoInteger}"/>
				          								<c:if test="${inputClass == 'double'}"><c:set var="formato" value="${formatoDouble}"/></c:if>
														<%-- ^\d{0,15}(\.\d{0,2})?$  --%>
														<c:set var="maxDecimali" value="${fn:split(formato,'.')[0]}" />
														<c:set var="maxDopoVirgola" value="${fn:split(formato,'.')[1]}" />
														<c:choose>
				             								<c:when test="${inputEnabled}">
																<input type="text" 
																	id="<s:property value="%{#inputName}" />_<s:property value="%{inputIndex}" />" 
																	name="<s:property value="%{#inputName}" />" 
																	data-parsley-trigger="change" 
																	data-parsley-pattern="<c:out value="^\d{0," /><c:out value="${maxDecimali}"/><c:out value="}"/><c:if test="${maxDopoVirgola == null}"><c:out value="$"/></c:if><c:if test="${maxDopoVirgola != null}"><c:out value="(\.\d{0," /><c:out value="${maxDopoVirgola}" /><c:out value="})?$" /></c:if>" 
																	value="${inputValue}"
																	aria-label="${columnTitles[i.index]} ${inputIndex}"
																	/>
															</c:when>
															<c:otherwise>
																<div id="<s:property value="%{inputName}" />_<s:property value="%{inputIndex}" />" 
																	name="<s:property value="%{inputName}" />">		
																	<s:text name="format.money"><s:param value="%{inputValueS}" /></s:text>
																	<s:if test="%{#tipoValidatore == 2}"> &euro;</s:if>
																</div>
															</c:otherwise>
														</c:choose>
													</s:elseif>
													<s:elseif test="%{#tipoValidatore == 3}"> 	<!-- TIPO_VALIDATORE_TABELLATO = 3 -->
				          								<%-- DA FARE !!! --%>
				          								<c:choose>
				             								<c:when test="${inputEnabled}">
						<%-- 			               			<s:select list="maps[codice]"  --%>
						<!--  		 								id="%{inputName +'_'+ inputIndex}"  -->
						<!--  		 								name="%{inputName}"  --> 
						<%--  		 								value="<s:property value='%{inputValueS}'/>"   --%>
						<!--  		 								headerKey=""  -->
						<!--  		 								headerValue="-- Scegli un valore --"/>  -->
															</c:when>
															<c:otherwise>
																<div id="<s:property value="%{inputName}" />_<s:property value="%{inputIndex}" />" 
																	name="<s:property value="%{inputName}" />">
																	${inputValue}
																</div>
															</c:otherwise>
														</c:choose>
													</s:elseif>
													<s:elseif test="%{#tipoValidatore == 4}"> 	<!-- TIPO_VALIDATORE_NOTE = 4 -->
				          								<c:choose>
				             								<c:when test="${inputEnabled}">
						          								<textarea 
																	id="<s:property value="%{inputName}" />_<s:property value="%{inputIndex}" />" 
																	name="<s:property value="%{inputName}" />" 
																	data-parsley-trigger="change" 
																	data-parsley-maxlength="${formatoString2}" 
																	rows="3" cols="20"
                                                                    maxlength="2000"
																	aria-label="${columnTitles[i.index]} ${inputIndex}" >
																	${inputValue}
																</textarea>
															</c:when>
															<c:otherwise>
																<div id="<s:property value="%{inputName}" />_<s:property value="%{inputIndex}" />" 
																	name="<s:property value="%{inputName}" />">
																	${inputValue}
																</div>
															</c:otherwise>
														</c:choose>
													</s:elseif>
													<s:elseif test="%{#tipoValidatore == 6}"> 	<!-- TIPO_VALIDATORE_FLAG = 6 -->
				          								<c:choose>
				             								<c:when test="${inputEnabled}">
					          									<s:select list="maps['sino']" 
																	id="%{inputName} +'_'+ inputIndex}" 
																	name="%{inputName}"	
																	headerKey=""  headerValue="--"
																	value="<s:property value='%{inputValueS}'/>" 
																	aria-label="${columnTitles[i.index]} ${inputIndex}"	
																	/>	 
															</c:when>
															<c:otherwise>
																<div id="<s:property value="%{inputName}" />_<s:property value="%{inputIndex}" />" 
																	name="<s:property value="%{inputName}" />">
																	${inputValue}
																</div>
															</c:otherwise>
														</c:choose>
													</s:elseif>
													<s:elseif test="%{#tipoValidatore == 7}"> 	<!-- TIPO_VALIDATORE_STRINGA = 7 -->
				          								<c:choose>
				             								<c:when test="${inputEnabled}">
						          								<input type="text" 
																	id="<s:property value="%{inputName}" />_<s:property value="%{inputIndex}" />" 
																	name="<s:property value="%{inputName}" />" 
																	data-parsley-trigger="change" 
																	data-parsley-maxlength="${formatoString}"
																	data-parsley-trim-value="true" 
																	value="${inputValue}" 
																	aria-label="${columnTitles[i.index]} ${inputIndex}"	
																	/>
															</c:when>
															<c:otherwise>
																<div id="<s:property value="%{inputName}" />_<s:property value="%{inputIndex}" />" 
																	name="<s:property value="%{inputName}" />">
																	${inputValue}
																</div>
															</c:otherwise>
														</c:choose>
													</s:elseif>
													<s:else>
				          								<%-- ERRORE!! --%>
				          								<c:choose>
				             								<c:when test="${inputEnabled}">
					          									<input type="text" name="${inputName}" />
					          								</c:when>
															<c:otherwise>
																<div id="<s:property value="%{inputName}" />_<s:property value="%{inputIndex}" />" 
																	name="<s:property value="%{inputName}" />">
																	${inputValue}
																</div>
															</c:otherwise>
														</c:choose>
													</s:else>
												</c:otherwise>
											</c:choose>
										</td>
			          				</c:catch>
				          		</c:forEach>
							</tr>
							
						</c:if>	
					</c:forEach>
				</tbody>
			</table>
	
			<div class="back-link">
				<strong>Importo totale : 
					<span id="totaleImportoEdit">
						<s:property value="%{#totaleOffertaPrezziUnitari}"/>&nbsp;&euro;
					</span>
				</strong>
			</div>
		</div>	
		
	
		<%-- HIDDEN INPUT PARAMETERS --%>
		<input type="hidden" name="method:undo" id="azioneDialog" value="Azione"/>
		
		<%-- hidden input parameters --%>
		<c:if test="${param.codice != null}">
			<input type="hidden" name="codice" value="${param.codice}"/>
		</c:if>
		<c:if test="${param.codiceLotto != null}">
			<input type="hidden" name="codiceLotto" value="${param.codiceLotto}"/>
		</c:if>
			
	</form>
</div>




<%-- ********************************************************************** --%>
<%-- SCRIPT GESTIONE OPERAZIONI SUL FORM (modifica, undo, save)             --%>
<%-- ********************************************************************** --%>
<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/parsley_validators.jsp" />

<script>
	$("#formEditPrezziUnitari").parsley();
	window.Parsley.setLocale('it');
</script>

<script>
$(document).ready(function() {
    // se js abilitato rimuovo l'avviso che per essere usabile la pagina
    // serve js abilitato
    $('#noJs').remove();

    // imposta il numero dei decimali
    var withAdvancedUI = ${withAdvancedUI}
    var decimali = 5;
    var moltiplicatoreDecimali = 1.0 * Math.pow(10, decimali);

    calcolaTotaleEdit();

//	// le descrizioni piu' lunghe di maxDimensioneDescrizione vengono
//	// troncate
//	// ma si aggiunge un'icona per attivare la visualizzazione della
//	// descrizione per esteso
//	$('a[id^="viewdesc_"],a[id^="editdesc_"]').click(function(){
//		var id = $(this).attr('id').substring(9);
//	    if ($(this).attr('class') == 'expand-info') {
//	    	$(this).attr('class', 'collapse-info');
//	    	$(this).attr('title', 'Comprimi la descrizione');
//	    	$(this).text($('#fulldesc_'+id).val());
//	    }
//	    else {
//	    	$(this).attr('class', 'expand-info');
//	    	$(this).attr('title', 'Espandi la descrizione');
//	    	$(this).text($('#fulldesc_'+id).val().substring(0,${maxDimensioneDescrizione}));
//	    }
//	});

    $(".money").on("change", function() {
        var importo = $(this).val();
    });

    $('[name="prezzoUnitario"]').on("change", function() {
        doOnChange(this);
    });

    $('[name="quantita"]').on("change", function() {
        doOnChange(this);
    });

    function doOnChange(obj) {
        var id = $(obj).attr('id');
        var i = id.lastIndexOf('_');
        var indice = id.substring(i+1);
        var importoText = "";
        var importo = "";
        $(obj).next("div").empty();
        var quantita = getQuantita(indice);
        var prezzo = getPrezzoUnitario(indice);
        // controllo il formato del campo sia cifre e punto decimale
        if (prezzo != ".") {
            if(!$("#"+id).hasClass("parsley-error")){
                // in caso positivo aggiorno l'importo
                if (prezzo == "" || quantita == "") {
                    // ho sbiancato il prezzo, sbianco l'importo
                    importo = "";
                    importoText = "";
                } else {
                    // ho valorizzato/aggiornato il prezzo, calcolo
                    // l'importo
                    //quantita = parseFloat(getQuantita(indice));
                    //prezzo = parseFloat(getPrezzoUnitario(indice));
                    quantita = getQuantita(indice);
                    prezzo = getPrezzoUnitario(indice);
//					setQuantita(indice, quantita);
//					setPrezzoUnitario(indice, prezzo);
                    importo = Math.round(prezzo * quantita * moltiplicatoreDecimali) / moltiplicatoreDecimali;
                    importoText = importo + "&nbsp;&euro;";
                }
            }
        } else {
            // in caso negativo aggiungo il messaggio
            // $(this).next("div").append();
            $(obj).next("div").append("<wp:i18n key='LABEL_FORMATO_DECIMALE_NON_VALIDO'/>");
        }
        // aggiorno la colonna importo con quanto calcolato (oppure
        // sbianco in caso di errore)
        $("#viewImporto_"+indice).html(importoText);
//		  $("#editImporto_"+indice).val(importo);
        // ricalcolo il totale
        calcolaTotaleEdit();
        // visualizzo l'alert per salvare le modifiche
        if ($("#alertModifiche").hasClass("noscreen")) {
            $("#alertModifiche").removeClass("noscreen");
            if (withAdvancedUI) {
                $('#tableEditVociDettaglio').dataTable({
                    destroy: true,
                    "sScrollX": "99%",
                    scrollY: altezzaDialog - (220 + 91),
                    "scrollCollapse": true
                });
            }
            // dimensiono la corpo tabella al massimo spazio disponibile
            // (tolgo spazio del balloon)
            $('#tableEditVociDettaglio').parent('.dataTables_scrollBody').css('height', (altezzaDialog - (220 + 91)) + 'px');
        }
        formatPrezziUnitari();
    }


    function formatPrezziUnitari(){

        var prezziUnitari = $('[name="prezzoUnitario"]');

        for (var i = 0; i<prezziUnitari.length; i++){
            if(prezziUnitari[i].value.includes('E')){
                prezziUnitari[i].value = parseFloat(Number.parseFloat(prezziUnitari[i].value).toFixed(5));
            }
        }

    }

    function calcolaTotaleEdit() {
        var totale = 0;
        $('[name="prezzoUnitario"]').each(function( index ) {
            var importo = getPrezzoUnitario(index);
            var quantita = getQuantita(index);
            try {
                if(importo != "") {
                    totale += importo * quantita; // la moltiplicazione lo rende numero
                }
            } catch(err) {}
        });
        totale = Math.round(totale * moltiplicatoreDecimali) / moltiplicatoreDecimali;
        $("#totaleImportoEdit").html(totale + "&nbsp;&euro;");
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
    var altezzaDialog;

    if (withAdvancedUI) {
        $.extend($.fn.dataTable.defaults, {
            "paging": false,
            "ordering": false,
            "info": false,
            "searching": false
        } );

        // applico la datatable alla lista di riepilogo prodotti
        $('#tableViewVociDettaglio').dataTable({
            scrollY: 300
        });
    }
    // al click sul bottone modifica si apre la dialog dimensionata
    // opportunamente e
    // si visualizza all'interno la tabella con i dati da editare
    $("body").on("click", "#modifica", function() {
        // si dimensiona la dialog centrale e 100px piu' piccola
        // rispetto alla dimensione del browser
        var larghezzaSchermo = $(window).width();
        var altezzaSchermo = $(window).height();
        var larghezzaDialog = larghezzaSchermo - 100;
        altezzaDialog = altezzaSchermo - 100;

        $( "#dialogEditVoci" ).dialog({
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
            buttons: {
                "<wp:i18n key='BUTTON_SAVE'/>": function() {
                    $("#azioneDialog").attr("name", "method:save");
                    $("#formEditPrezziUnitari").submit();
                    fixAriaUIDialog("false");
                },
                "<wp:i18n key='BUTTON_WIZARD_CANCEL'/>" : function() {
                    // annullo le modifiche e ricarico la pagina principale
                    $("#azioneDialog").attr("name", "method:undo");
                    $("#formEditPrezziUnitari").off('submit.Parsley');
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
        if (withAdvancedUI) {
            $('#tableEditVociDettaglio').dataTable({
                destroy: true,
                "sScrollX": "99%",
                scrollY: altezzaDialog - 220,
                "scrollCollapse": true
            });
        }

        // dimensiono il corpo tabella al massimo spazio disponibile
        // (tolgo spazio del balloon)
        $('#tableEditVociDettaglio').parent('.dataTables_scrollBody').css('height', (altezzaDialog - 220) + 'px');
        formatPrezziUnitari();

        // fix validazione WCAG 2.1 criterio ARIA-UI
        fixAriaUIDialog("true");
    });

    ////////////////////////////////////////////////////////////////////////////
    // getter/setter per quantita, prezzoUnitario

    function getQuantita(index) {
        return toFloat("#quantita_"+index);
    }

    function getPrezzoUnitario(index) {
        return toFloat("#prezzoUnitario_"+index);
    }

    function toFloat(key) {
        var value = "";
        try {
            value = $(key).val();
            if(value == "" || value == null) {
                value = $(key).text();
            }

            // cerca il separatore decimale e riformatta il valore come 1234.0
            var a = value;
            var b = "0";
            for(var i = value.length-1; i >= 0; i--) {
                if(value[i] == '.' || value[i] == ',') {
                    a = value.substr(0, i);
                    b = value.substr(i+1, value.length-1);
                    break;
                }
            }
            a = a.replace(/[^0-9]+/g, "");  // sostituisci tutto ci� che non � [^0-9] con ""
            b = b.replace(/[^0-9]+/g, "");
            value = a + "." + b;
        } catch(err) {
            value = "0";
        }
        return value;
    }

});
</script>
