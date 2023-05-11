<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="es"   uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>

<wp:headInfo type="CSS" info="jquery/jquery-ui/jquery-ui.css" />
<script type="text/javascript" src="<wp:resourceURL/>static/js/jquery-ui-1.12.1.min.js"></script>
<c:set var="showAccetta" value="false"/>
<c:set var="showRifiuta" value="false"/>
<c:set var="backLink" value="ordiniTutti"/>
<es:getAppParam name = "denominazioneStazioneAppaltanteUnica" var = "stazAppUnica" scope = "page"/> 	

<c:choose>
	<c:when test="${sessionScope.fromPage eq 'listordinidavalutare'}">
		<c:set var="showAccetta" value="${dettaglioOrdine.communicationType=='ORDER'}"/>
		<c:set var="showRifiuta" value="${dettaglioOrdine.communicationType=='ORDER'}"/>
		<c:set var="backLink" value="ordiniDaValutare"/>
	</c:when>
	<c:when test="${sessionScope.fromPage eq 'listordiniconfermati'}">
		<c:set var="backLink" value="ordiniConfermati"/>
	</c:when>
	<c:when test="${sessionScope.fromPage eq 'listordinitutti'}">
		<c:set var="backLink" value="ordiniTutti"/>
	</c:when>
</c:choose>

<%-- <c:catch var ="catchException">
The exception will be thrown inside the catch:<br>
<c:out value="${dettaglioOrdine.currentOrderStatus.id}"></c:out><br>
<c:out value="${dettaglioOrdine.currentOrderStatus}"></c:out><br>
dettaglioOrdine.currentOrderStatus.id.value: <c:out value="${dettaglioOrdine.currentOrderStatus.id.value}"></c:out><br>
dettaglioOrdine.currentOrderStatus.id.NUMBER_3: <c:out value="${dettaglioOrdine.currentOrderStatus.id.NUMBER_3}"></c:out><br>
dettaglioOrdine.currentOrderStatus.id.ordinal: <c:out value="${dettaglioOrdine.currentOrderStatus.id.ordinal}"></c:out><br>
NUMBER_3: <c:out value="${it.maggioli.eldasoft.nso.client.model.OrderStatus.IdEnum.NUMBER_3}"></c:out><br>
<c:out value="${dettaglioOrdine.currentOrderStatus.id eq 4}"></c:out>
<c:out value="${dettaglioOrdine.currentOrderStatus.id eq '4'}"></c:out><br>
</c:catch> 

<c:if test = "${catchException!=null}">
The exception is : ${catchException}<br><br>
There is an exception: ${catchException.message}<br>
</c:if>--%>
<c:set var="showFattWarn" value="${dettaglioOrdine.currentOrderStatus.id.value==4}"/>
<c:set var="showFattSection" value="${dettaglioOrdine.currentOrderStatus.id.value==7||dettaglioOrdine.currentOrderStatus.id.value==8}"/>
<wp:i18n key="BUTTON_CONFIRM" var="valueConfirmButton" />
<wp:i18n key="BUTTON_WIZARD_CANCEL" var="valueAnnullaButton" />

<%-- ******************************************************************************** --%>
<script type="text/javascript">
<!--//--><![CDATA[//><!--
	var showOrder = false;
	function visualizzaOrdine(visible) {
		//if($('#dettagliOrdine').is(':visible')){
		if(visible) {
			$("#dettagliOrdine").show();
		} else {
			$("#dettagliOrdine").hide();
		}
	}
	function showOrdine() {
		this.showOrder = !showOrder;
		if(showOrder) {
			$("#dettagliOrdine").show();
		} else {
			$("#dettagliOrdine").hide();
		}
	}
	
// apertura della pagina...
$(document).ready(function() {
	$("#dettagliOrdine").hide();
	
	
	$('#accept-dialog-modify').dialog({
		closeOnEscape: false,
		autoOpen: false,
		resizable: false,
		draggable: false,
		/*height: "80%",*/
		/* width: "90%", */
		modal: true,
		open: function(event, ui) {
			$(".ui-dialog-titlebar-close").hide();
			$(".ui-dialog-titlebar").hide();
	    },
	    close: function(event, ui) {
			$(".ui-dialog-titlebar-close").show(); //prevent erroneous setups
	    },
		buttons: {
			Conferma :{
				text: '<s:property value="%{#attr.valueConfirmButton}" />',
				click: function(event) {
					$( this ).dialog( "close" );
					$("#dataLimiteModifiche").val($("#dataLimiteModificheL").val());
					$('#formAccettazione').append('<input type="hidden" name="method:accetta" value="ok" />');
					$('#formAccettazione').submit();
					return true;
				}
			},
			Annulla:{
				text:'<s:property value="%{#attr.valueAnnullaButton}" />',
				click: function() {
					$( this ).dialog( "close" );
					return false;
				}
			}
		}
    });
	
});
function openDialog(){
	event.preventDefault();
	$('#accept-dialog-modify').dialog( "open" );
	return false;
}
//--><!]]>
</script>
<%-- ******************************************************************************** --%>


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp">
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2><wp:i18n key="TITLE_PAGE_EORDERS_DETTAGLIO_ORDINE" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_EORDERS_DETTAGLIO_ORDINE" />
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
		
	<div class="detail-section first-detail-section">
		<h3 class="detail-section-title">
			<span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_STAZIONE_APPALTANTE" />
		</h3>
		
		<div class="detail-row">
			<label><wp:i18n key="LABEL_DENOMINAZIONE" /> : </label>
			<c:choose>
				<c:when test="${! empty stazAppUnica }">
					<s:set var="stazAppUnicaToStruts">${stazAppUnica}</s:set>
					<s:property value="stazAppUnicaToStruts" />
				</c:when>
				<c:otherwise>
					<s:iterator value="maps['stazioniAppaltanti']">
								<s:if test="%{key == dettaglioOrdine.contractingAuthority}">
									<s:property value="%{value}" />
								</s:if>
					</s:iterator>
				</c:otherwise>
			</c:choose>
		</div>
	</div>

	<div class="detail-section">
		<h3 class="detail-section-title">
			<span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_DATI_GENERALI" />
		</h3>

		<div class="detail-row">
			<label><wp:i18n key="LABEL_NUMERO_ORDINE" /> :</label>
			<s:property value="dettaglioOrdine.orderCode" />
		</div>
		
		<div class="detail-row">
			<label><wp:i18n key="LABEL_CIG" /> :</label>
			<s:property value="dettaglioOrdine.cig" />
		</div>
		
		<div class="detail-row">
			<label><wp:i18n key="LABEL_RIFERIMENTO_PROCEDURA" /> : </label>
			<s:property value="dettaglioOrdine.tender" />
		</div>
		
<!-- ESISTE ???	 
		<div class="detail-row">
			<label><wp:i18n key="LABEL_TITOLO" /> :</label>
			<s:property value="dettaglioOrdine.oggetto" />
		</div>
-->
		
		<div class="detail-row">
			<label><wp:i18n key="LABEL_DATA_EMISSIONE" /> : </label>
			<s:date name="dettaglioOrdine.nsoReceptionDate" format="dd/MM/yyyy" />
		</div>
		
		<div class="detail-row">
			<label><wp:i18n key="LABEL_DATA_SCADENZA" /> : </label>
			<s:date name="dettaglioOrdine.expiryDate" format="dd/MM/yyyy" />
		</div>
		
		<div class="detail-row">
			<label><wp:i18n key="LABEL_EVENTUALE_RIFERIMENTO_ORDINE" /> : </label>
			<s:property value="dettaglioOrdine.parentOrder" />
		</div>
		
		<div class="detail-row">
			<label><wp:i18n key="LABEL_PUNTO_CONSEGNA" /> : </label>
			<s:property value="dettaglioOrdine.endpoint" />
		</div>
		
		<div class="detail-row">
			<label><wp:i18n key="LABEL_STATO" /> : </label>
			<s:property value="dettaglioOrdine.currentOrderStatus.description" />
			<!-- 
			<s:iterator value="maps['statiOrdineNso']">
				<s:if test="%{key == dettaglioOrdine.currentOrderStatus.id}">
					<s:property value="%{value}" />
				</s:if>
			</s:iterator>
			 -->
		</div>
	</div>

	<div class="detail-section">
		<h3 class="detail-section-title">
			<span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_ORDINE" />
		</h3>
		
		<a href="#" onclick="showOrdine()" title="<wp:i18n key="LINK_VIEW_ORDER" />" class='bkg <s:property value="cssClass"/>'>
 			<wp:i18n key="LINK_VIEW_ORDER" />
 		</a>
 		
		<%-- <div style="overflow-x: auto;"> --%>
		<div id="dettagliOrdine" style="overflow: auto;">
			${orderXml}
		</div>
	</div>
	
	<!-- ALLEGATI -->
	<s:if test="%{dettaglioOrdine.allegati.length > 0}">
		<div class="detail-section">
			<h3 class="detail-section-title">
				<span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_ALLEGATI_ORDINE" />
			</h3>
		</div>
		<s:set var="elencoDocumentiAllegati" value="%{dettaglioOrdine.allegati}" />
		<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/bandi/inc/iteratorDocAllegati.jsp">
			<jsp:param name="path" value="downloadDocumentoRiservato"/>
		</jsp:include>
	</s:if>

	<!-- COMUNICAZIONI -->
	<c:if test="${sessionScope.currentUser != 'guest'}">
		<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/comunicazioni/sommarioComunicazioni.jsp" >
			<jsp:param name="genere" value="4"/>
		</jsp:include>	
	</c:if>

	<%-- Accetta, Rifiuta --%>
	<c:if test="${showAccetta || showRifiuta}">
		<form id="formAccettazione" action="<wp:action path="/ExtStr2/do/FrontEnd/EOrders/confermaOrdine.action" />" method="post" >
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			<div class="azioni">
				<input type="hidden" name="id" value="${id}" />
				<input type="hidden" id="dataLimiteModifiche" name="dataLimiteModifiche" value="" />
				<c:if test="${showAccetta}">
					<wp:i18n key="BUTTON_ACCETTA" var="valueAccettaButton" />
					<%-- <s:submit value="%{#attr.valueAccettaButton}" title="%{#attr.valueAccettaButton}" onclick="openDialog();" cssClass="button" method="accetta"></s:submit> --%>
					<s:submit value="%{#attr.valueAccettaButton}" title="%{#attr.valueAccettaButton}" onclick="openDialog();" cssClass="button" ></s:submit>
				</c:if>
				<c:if test="${showRifiuta}">
					<wp:i18n key="BUTTON_RIFIUTA" var="valueRifiutaButton" />
					<s:submit value="%{#attr.valueRifiutaButton}" title="%{#attr.valueRifiutaButton}" cssClass="button" method="rifiuta"></s:submit>
				</c:if>
			</div>
		</form>
	</c:if>
	
	<%-- FATURAZIONE --%>
	<c:if test="${sessionScope.currentUser != 'guest' && (showFattWarn || showFattSection)}">
			<div class="detail-section last-detail-section">
				<h3 class="detail-section-title">
					<span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_EORDERS_FATTURAZIONE" />
				</h3>
				<c:if test="${showFattSection}">
				<div class="azioni">
						<form action="<wp:action path="/ExtStr2/do/FrontEnd/EOrders/archivioFatture.action" />" method="post" >
							<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
							<input type="hidden" name="id" value="${id}" />
							<input type="hidden" name="orderCode" value="${dettaglioOrdine.orderCode}" />
							<wp:i18n key="LABEL_EORDERS_FATT_ARCHIVIO" var="valueFATT_ARCHIVIOButton" />
							<s:submit value="%{#attr.valueFATT_ARCHIVIOButton}" title="%{#attr.valueFATT_ARCHIVIOButton}" cssClass="button" ></s:submit>
						</form>
				</div>
				<div class="azioni">
						<form action="<wp:action path="/ExtStr2/do/FrontEnd/EOrders/fattura.action" />" method="post" >
							<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
							<input type="hidden" name="id" value="${id}" />
							<input type="hidden" name="orderCode" value="${dettaglioOrdine.orderCode}" />
							<wp:i18n key="LABEL_EORDERS_FATT_CREA" var="valueFATT_CREAButton" />
							<s:submit value="%{#attr.valueFATT_CREAButton}" title="%{#attr.valueFATT_CREAButton}" cssClass="button" method="crea" ></s:submit>
							<wp:i18n key="LABEL_EORDERS_FATT_INVIO" var="valueFATT_INVIOButton" />
							<s:submit value="%{#attr.valueFATT_INVIOButton}" title="%{#attr.valueFATT_INVIOButton}" cssClass="button" method="upload"></s:submit>
						</form>
				</div>
				</c:if>
				<c:if test="${showFattWarn}">
					<span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_EORDERS_FATTURAZIONE_WARN" />
				</c:if>
			</div>
	</c:if>
		
	<div class="back-link">
		<a href="<wp:action path="/ExtStr2/do/FrontEnd/EOrders/${backLink}.action"/>&amp;last=1&amp;${tokenHrefParams}">
			<wp:i18n key="LINK_BACK_TO_LIST" />
		</a>
	</div>
<div id="accept-dialog-modify" title='<s:property value="%{#attr.valueAccettaButton}" />' style="display:none">
	<s:date name="dettaglioOrdine.expiryDate" var="dataLimiteModificheL" format="dd/MM/yyyy"/>
	<label><wp:i18n key="LABEL_DATA_LIMITE_MOD" /> : </label><s:textfield name="dataLimiteModificheL" id="dataLimiteModificheL" value="%{dataLimiteModificheL}" />
</div>
</div>