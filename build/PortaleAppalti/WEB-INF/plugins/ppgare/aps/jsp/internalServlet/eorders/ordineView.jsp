<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<c:set var="showAccetta" value="false"/>
<c:set var="showRifiuta" value="false"/>
<c:set var="backLink" value="ordiniTutti"/>
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


<%-- ******************************************************************************** --%>
<script type="text/javascript">
<!--//--><![CDATA[//><!--
	
	function visualizzaOrdine(visible) {
		//if($('#dettagliOrdine').is(':visible')){
		if(visible) {
			$("#dettagliOrdine").show();
		} else {
			$("#dettagliOrdine").hide();
		}
	}
	
// apertura della pagina...
$(document).ready(function() {
	$("#dettagliOrdine").hide();
});	
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
			<s:iterator value="maps['stazioniAppaltanti']">
				<s:if test="%{key == dettaglioOrdine.contractingAuthority}">
					<s:property value="%{value}" />
				</s:if>
			</s:iterator>
		</div>
	</div>

	<div class="detail-section">
		<h3 class="detail-section-title">
			<span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_DATI_GENERALI" />
		</h3>

		<div class="detail-row">
			<label><wp:i18n key="LABEL_NUMERO_ORDINE" /> :</label>
			<s:property value="dettaglioOrdine.orderId" />
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
			<s:property value="dettaglioOrdine.id" />
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
		
		<a href="#" onclick="visualizzaOrdine(true)" title="<wp:i18n key="LINK_VIEW_ORDER" />" class='bkg <s:property value="cssClass"/>'>
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
	<form action="<wp:action path="/ExtStr2/do/FrontEnd/EOrders/confermaOrdine.action" />" method="post" >
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
		<div class="azioni">
			<input type="hidden" name="id" value="${id}" />
			
			<c:if test="${showAccetta}">
				<wp:i18n key="BUTTON_ACCETTA" var="valueAccettaButton" />
				<s:submit value="%{#attr.valueAccettaButton}" title="%{#attr.valueAccettaButton}" cssClass="button" method="accetta"></s:submit>
			</c:if>
			<c:if test="${showRifiuta}">
				<wp:i18n key="BUTTON_RIFIUTA" var="valueRifiutaButton" />
				<s:submit value="%{#attr.valueRifiutaButton}" title="%{#attr.valueRifiutaButton}" cssClass="button" method="rifiuta"></s:submit>
			</c:if>
		</div>
	</form>
		
	<div class="back-link">
		<a href="<wp:action path="/ExtStr2/do/FrontEnd/EOrders/${backLink}.action"/>&amp;last=1&amp;${tokenHrefParams}">
			<wp:i18n key="LINK_BACK_TO_LIST" />
		</a>
	</div>
</div>