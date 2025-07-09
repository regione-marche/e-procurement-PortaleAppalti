<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>

<c:set var="backLink" value="ordiniTutti"/>
<es:checkCustomization var="withAdvancedUI" objectId="UI-ADVANCED" attribute="DATATABLE" feature="ACT" />

<link href="<wp:resourceURL/>static/css/parsley.css" rel="stylesheet"></link>
<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/jquery_ui.jsp" />
<script type="text/javascript" src="<wp:resourceURL/>static/js/jquery.dataTables.min.js"></script>

<%-- ******************************************************************************** --%>
<wp:i18n key="BUTTON_FATT_LINE_POP_CANCEL" var="valueCancelBtnPupup" />
<wp:i18n key="BUTTON_FATT_LINE_POP_CONFIRM" var="valueConfirmBtnPupup" />

<script type="text/javascript">
<!--//--><![CDATA[//><!--
// apertura della pagina...
var lineDiagTable = null;
$(document).ready(function() {
	
	var isUIAdvanced = ${withAdvancedUI}
	if (isUIAdvanced) {
		$.extend($.fn.dataTable.defaults, {
			"paging": false,
			"ordering": false,
			"info": false,
			"searching" : false
		});
	
		$('#tableViewLinee').DataTable();
		lineDiagTable = $('#tableEditLinee').DataTable();
	}
	
	$('#lines-dialog-modify').dialog({
		closeOnEscape: false,
		autoOpen: false,
		resizable: false,
		draggable: false,
		/*height: "80%",*/
		width: "90%",
		modal: true,
		open: function(event, ui) {
			console.log("Opened");
			$(".ui-dialog-titlebar-close").hide();
			if (isUIAdvanced) {
				$('#tableEditLinee').DataTable();
			}
	    },
	    close: function(event, ui) {
			$(".ui-dialog-titlebar-close").show(); //prevent erroneous setups
			console.log("Closed");
	    },
		buttons: {
			Conferma :{
				text: '<s:property value="%{#attr.valueConfirmBtnPupup}" />',
				click: function(event) {
					//chiamare il salva effettivo
					$('#saveLinee').submit();
					$( this ).dialog( "close" );
				}
			},
			Annulla:{
				id:"nso-dialog-send-annulla",
				text:'<s:property value="%{#attr.valueCancelBtnPupup}" />',
				click: function() {
					$('#reload').submit();
					$( this ).dialog( "close" );
				}
			}
		}
    });
});

function openDialog(){
	$('#lines-dialog-modify').dialog( "open" );
}

function deleteLine(index){
	lineDiagTable.table().row('#line-internal-'+index).remove().draw( false );
}

//--><!]]>
</script>
<%-- ******************************************************************************** --%>


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp">
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2>
		<wp:i18n key="TITLE_PAGE_EORDERS_FATTURA_CREA" />&nbsp;<s:property value="orderCode"/>
	</h2>
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
	<jsp:include page="stepsFatture.jsp" />
	
	
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
			<jsp:param name="keyMsg" value="BALLOON_EORDERS_FATTURA_LINEEORDINE" />
		</jsp:include>
		
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/skip_form_buttons.jsp" />
	
		<div class="detail-section first-detail-section">
			<h3 class="detail-section-title">
				<span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_FATTURA_LINEEORDINE" />
			</h3>
		</div>
	<div class="detail-section">
		<c:catch var="exceptionOrders">
			
			<table id="tableViewLinee" summary="Tabella di visualizzazione delle linee d'ordine" class="info-table">
						<thead>
							<tr>
								<th scope="col"><wp:i18n key="LABEL_INDEX" /></th>
								<th scope="col"><wp:i18n key="LABEL_DESCRIZIONE" /></th>
								<th scope="col"><wp:i18n key="LABEL_QUANTITA" /></th>
								<th scope="col"><wp:i18n key="LABEL_PREZZO_UNITARIO" /></th>
								<%-- <th scope="col"><wp:i18n key="LABEL_PREZZO_TOTALE" /></th> --%>
							</tr>
						</thead>
						<tbody>
						<s:iterator var="line" value="dettaglioLinee">
								<tr>
									<td>
										<s:property value="numeroLinea" />
									</td>
									<td>
										<s:property value="descrizione" />
									</td>
									<td>
										<s:property value="quantita" />
									</td>
									<td>
										<%-- <s:property value="prezzoUnitario" /> --%>
										<s:text name="format.money">
										    <s:param name="value" value="prezzoUnitario"/>
										</s:text>
									</td>
									<%-- <td>
										<s:property value="prezzoTotale" />
										<s:text name="format.money">
										    <s:param name="value" value="prezzoUnitario"/>
										</s:text>
									</td> --%>
								</tr>
						</s:iterator>
					</tbody>
				</table>
				<wp:i18n key="LABEL_EORDERS_FATT_LINE_MOD" var="valueModLineButton" />
				<br><input type="button" class="button" onclick="openDialog();" value='<s:property value="%{#attr.valueModLineButton}" />' title='<s:property value="%{#attr.valueModLineButton}" />' />
		</c:catch>
		<c:if test = "${exceptionOrders!=null}">
		There is an exception: ${exceptionOrders.message}<br>
		The exception is : ${exceptionOrders}<br><br>
		</c:if>
		<form id="nextAction" action="<wp:action path="/ExtStr2/do/FrontEnd/EOrders/elaboraLinee.action" />" method="post" >
			<div class="azioni">
				<input type="hidden" name="id" value="${id}" />
				<input type="hidden" name="idFatt" value="${idFatt}" />
				<input type="hidden" name="orderCode" value="${orderCode}" />
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_previous.jsp" />
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_last.jsp" />
			</div>
		</form>
	</div>

	<div class="detail-section">
		<form id="reload" action="<wp:action path="/ExtStr2/do/FrontEnd/EOrders/elaboraLinee.action" />" method="post" >
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			<input type="hidden" name="id" value="${id}" />
			<input type="hidden" name="idFatt" value="${idFatt}" />
			<input type="hidden" name="orderCode" value="${orderCode}" />
			<input type="hidden" name="method:elaboraLinee" value="${idFatt}" />
			<%-- <div class="detail-row">
				<wp:i18n key="LABEL_EORDERS_FATT_CREALINEE" var="valueFATT_CREALINEEButton" />
				<s:submit value="%{#attr.valueFATT_CREALINEEButton}" title="%{#attr.valueFATT_CREALINEEButton}" cssClass="button" method="elaboraLinee"></s:submit>
			</div> --%>
		</form>
	</div>
</div>

<div id="lines-dialog-modify" title="<wp:i18n key="LABEL_FATTURA_LINEEORDINE_EDIT" />" style="display:none">
	<form id="saveLinee" action="<wp:action path="/ExtStr2/do/FrontEnd/EOrders/saveLinee.action" />" method="post" >
 		<table id="tableEditLinee" summary="Tabella di visualizzazione delle linee d'ordine" class="info-table">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			<thead>
				<tr>
					<th scope="col"><wp:i18n key="LABEL_INDEX" /></th>
					<th scope="col"><wp:i18n key="LABEL_DESCRIZIONE" /></th>
					<th scope="col"><wp:i18n key="LABEL_QUANTITA" /></th>
					<th scope="col"><wp:i18n key="LABEL_PREZZO_UNITARIO" /></th>
					<th scope="col"><wp:i18n key="LABEL_RITENUTA" /></th>
					<th scope="col"><wp:i18n key="LABEL_NATURA" /></th>
					<th scope="col"><wp:i18n key="LABEL_ESIGIVA" /></th>
					<th scope="col"><wp:i18n key="LABEL_RIFNORM" /></th>
					<th scope="col"><wp:i18n key="LABEL_AZIONI" /></th>
				</tr>
			</thead>
			<tbody>
				<s:iterator var="line" value="dettaglioLinee" status="lineStatus">
					
					<tr id='line-internal-<s:property value="numeroLinea" />'>
						<td>
							<s:property value="numeroLinea" />
							<s:hidden name="numeroLinea" value="%{numeroLinea}"></s:hidden>
						</td>
						<td>
							<s:property value="descrizione" />
						</td>
						<td>
							<s:textfield name="quantita" value="%{quantita}"></s:textfield>
						</td>
						<td>
							<s:textfield name="prezzoUnitario" value="%{prezzoUnitario}"></s:textfield>
						</td>
						<td>
							<select name="ritenuta">
								<option value="">&nbsp;</option>
								<option value="SI" <s:if test="ritenuta">selected</s:if>>SI</option>
							</select>
						</td>
						<td>
							<select name="natura">
								<option value="" ></option>
								<option value="N1"   <s:if test="natura.value == 'N1'">selected</s:if> >escluse ex art. 15</option>
								<option value="N2"   <s:if test="natura.value == 'N2'">selected</s:if> >non soggette  (codice non più valido a partire dal primo gennaio 2021)</option>
								<option value="N2.1" <s:if test="natura.value == 'N2.1'">selected</s:if> >non soggette ad IVA ai sensi degli artt. da 7 a 7-septies del DPR 633/72</option>
								<option value="N2.2" <s:if test="natura.value == 'N2.2'">selected</s:if> >non soggette - altri casi</option>
								<option value="N3"   <s:if test="natura.value == 'N3'">selected</s:if> >non imponibili  (codice non più valido a partire dal primo gennaio 2021)</option>
								<option value="N3.1" <s:if test="natura.value == 'N3.1'">selected</s:if> >non imponibili - esportazioni</option>
								<option value="N3.2" <s:if test="natura.value == 'N3.2'">selected</s:if> >non imponibili - cessioni intracomunitarie</option>
								<option value="N3.3" <s:if test="natura.value == 'N3.3'">selected</s:if> >non imponibili - cessioni verso San Marino</option>
								<option value="N3.4" <s:if test="natura.value == 'N3.4'">selected</s:if> >non imponibili - operazioni assimilate alle cessioni all'esportazione</option>
								<option value="N3.5" <s:if test="natura.value == 'N3.5'">selected</s:if> >non imponibili - a seguito di dichiarazioni d'intento</option>
								<option value="N3.6" <s:if test="natura.value == 'N3.6'">selected</s:if> >non imponibili - altre operazioni che non concorrono alla formazione del plafond</option>
								<option value="N4"   <s:if test="natura.value == 'N4'">selected</s:if> >esenti</option>
								<option value="N5"   <s:if test="natura.value == 'N5'">selected</s:if> >regime del margine / IVA non esposta in fattura</option>
								<option value="N6"   <s:if test="natura.value == 'N6'">selected</s:if> title="inversione contabile (per le operazioni in reverse charge ovvero nei casi di autofatturazione per acquisti extra UE di servizi ovvero per importazioni di beni nei soli casi previsti)  (codice non più valido a partire dal primo gennaio 2021)" >inversione contabile (codice non più valido a partire dal primo gennaio 2021)</option>
								<option value="N6.1" <s:if test="natura.value == 'N6.1'">selected</s:if> >inversione contabile - cessione di rottami e altri materiali di recupero</option>
								<option value="N6.2" <s:if test="natura.value == 'N6.2'">selected</s:if> >inversione contabile - cessione di oro e argento puro</option>
								<option value="N6.3" <s:if test="natura.value == 'N6.3'">selected</s:if> >inversione contabile - subappalto nel settore edile</option>
								<option value="N6.4" <s:if test="natura.value == 'N6.4'">selected</s:if> >inversione contabile - cessione di fabbricati</option>
								<option value="N6.5" <s:if test="natura.value == 'N6.5'">selected</s:if> >inversione contabile - cessione di telefoni cellulari</option>
								<option value="N6.6" <s:if test="natura.value == 'N6.6'">selected</s:if> >inversione contabile - cessione di prodotti elettronici</option>
								<option value="N6.7" <s:if test="natura.value == 'N6.7'">selected</s:if> >inversione contabile - prestazioni comparto edile e settori connessi</option>
								<option value="N6.8" <s:if test="natura.value == 'N6.8'">selected</s:if> >inversione contabile - operazioni settore energetico</option>
								<option value="N6.9" <s:if test="natura.value == 'N6.9'">selected</s:if> >inversione contabile - altri casi</option>
								<option value="N7"   <s:if test="natura.value == 'N7'">selected</s:if> title="IVA assolta in 
								altro stato UE (vendite a distanza ex art. 40 c. 3 e 4 e art. 41 c. 1 lett. b,  
								DL 331/93; prestazione di servizi di telecomunicazioni, tele-radiodiffusione ed elettronici 
								ex art. 7-sexies lett. f, g, art. 74-sexies 
								DPR 633/72)">IVA assolta in altro stato UE </option>
							</select>
						</td>
						<td>
						<s:set var="esigibilitaIva" value="" scope="page" ></s:set>
						<s:set var="riferimentoNormativo" value="" scope="page" ></s:set>
						<s:iterator var="datGest" value="altriDatiGestionali">
							<s:if test="tipoDato == 'Esig.IVA'" >
								<s:set var="esigibilitaIva" value="#datGest.riferimentoTesto" />
							</s:if>
							<s:if test="tipoDato == 'Rif.Norm.'" >
								<s:set var="riferimentoNormativo" value="#datGest.riferimentoTesto" />
							</s:if>
						</s:iterator>
							<select name="esigibilitaIva">
								<option value="" ></option>
								<option value="I" <s:if test="#esigibilitaIva.equalsIgnoreCase('I')">selected</s:if> >[I]: IVA ad esigibilità immediata</option>
								<option value="D" <s:if test="#esigibilitaIva.equalsIgnoreCase('D')">selected</s:if> >[D]: IVA ad esigibilità differita</option>
								<option value="S" <s:if test="#esigibilitaIva.equalsIgnoreCase('S')">selected</s:if> >[S]: scissione dei pagamenti</option>
							</select>
						</td>
						<td>
							<s:textfield name="riferimentoNormativo" value="%{riferimentoNormativo}"></s:textfield>
						</td>
						<td>
							<wp:i18n key="LABEL_EORDERS_FATT_DELLINE" var="valueLabelDeleteButton" />
							<input type="button" class="button" onclick='deleteLine(<s:property value="numeroLinea" />)' value='<s:property value="%{#attr.valueLabelDeleteButton}" />' />
						</td>
					</tr>
				</s:iterator>
			</tbody>
		</table>
		<input type="hidden" name="id" value="${id}" />
		<input type="hidden" name="idFatt" value="${idFatt}" />
		<input type="hidden" name="orderCode" value="${orderCode}" />
		<input type="hidden" name="method:saveLinee" value="${idFatt}" />
 	</form>
</div>