<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="es"
	uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld"%>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>


<div class="portgare-view">

	<h2><wp:i18n key="TITLE_PAGE_DETTAGLIO_STIPULA" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_DETTAGLIO_STIPULA"/>
	</jsp:include>



	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />

	<s:if test="! hasActionErrors()">		
		<div class="detail-section first-detail-section">
			<h3 class="detail-section-title"><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_STAZIONE_APPALTANTE" /></h3>

			<div class="detail-row">
				<label><wp:i18n key="LABEL_DENOMINAZIONE" /> : </label>
				<s:property value="dettaglioStipula.descSa" />
			</div>
			<div class="detail-row">
				<label><wp:i18n key="LABEL_RUP" /> : </label>
				<s:property value="dettaglioStipula.descRup" />
			</div>
		</div>
		
		<div class="detail-section">
			<h3 class="detail-section-title"><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_DATI_GENERALI" /></h3>
			<div class="detail-row">
				<label><wp:i18n key="LABEL_OGGETTO_CONTRATTO" /> : </label>
				<s:property value="dettaglioStipula.oggetto" /> 
			</div>
			<div class="detail-row">
				<label><wp:i18n key="LABEL_IMPORTO_CONTRATTO" /> : </label>
				<c:if test="${!empty (dettaglioStipula.importo)}">
					<s:text name="format.money"><s:param value="dettaglioStipula.importo"/></s:text> &euro;
				</c:if>
			</div>
			<c:if test="${!empty (dettaglioStipula.contatore)}">
				<div class="detail-row">
					<label><wp:i18n key="LABEL_NUMERO_CONTRATTO" /> : </label>
					<s:property value="dettaglioStipula.contatore" /> 
				</div>
			</c:if>
			<c:if test="${!empty (dettaglioStipula.codicedec)}">
				<div class="detail-row">
					<label><wp:i18n key="LABEL_NUMERO_REPERTORIO" /> : </label>
					<s:property value="dettaglioStipula.codicedec" /> 
				</div>
			</c:if>
			<c:if test="${!empty (dettaglioStipula.dataPubblicazione)}">
				<div class="detail-row">
					<label><wp:i18n key="LABEL_DATA_STIPULA" /> : </label>
					<s:property value="dettaglioStipula.dataPubblicazione" /> 
				</div>
			</c:if>
			<div class="detail-row">
				<label><wp:i18n key="LABEL_COD_STIPULA" /> : </label>
				<s:property value="dettaglioStipula.codiceStipula" /> 
			</div>
			<div class="detail-row">
				<label><wp:i18n key="LABEL_CONTRACT_MANAGER" /> : </label>
				<s:property value="dettaglioStipula.assegnatario" /> 
			</div>
			<div class="detail-row">
				<label><wp:i18n key="LABEL_RIFERIMENTO_PROCEDURA" /> : </label>
				<s:property value="dettaglioStipula.codiceGara" /> 
			</div>
			<div class="detail-row">
				<label><wp:i18n key="LABEL_STATO" /> : </label>
				<s:property value="dettaglioStipula.descStato"/>
					
			</div>
		</div>
		<c:if test="${sessionScope.currentUser != 'guest'}">
			<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/comunicazioni/sommarioComunicazioni.jsp" >
				<jsp:param name="entita" value="G1STIPULA"/>
			</jsp:include>
		</c:if>

		<div class="azioni">
			<form action="<wp:action path="/ExtStr2/do/FrontEnd/Contratti/openPageDocumentiStipule.action" />" method="post" class="azione">
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
				<div>
					<input type="submit" value="<wp:i18n key="LABEL_DOCUMENTI" />" class="button" title="Aggiorna i documenti delle stipule">
					<input type="hidden" name="codice" value="${dettaglioStipula.id}"> 
					<input type="hidden" name="codiceStipula" value="${dettaglioStipula.codiceStipula}"> 
					<input type="hidden" name="statoStipula" value="${dettaglioStipula.stato}"> 
				</div>
			</form>

		</div>
	</s:if>

	<div class="back-link">
	<c:choose>	
		<c:when test="${sessionScope.fromPage != null && 
		                (sessionScope.fromPage eq 'openPageDettaglioComunicazioneInviata' ||
		                 sessionScope.fromPage eq 'openPageDettaglioComunicazioneRicevuta')}">
		
			<a href="<wp:action path="/ExtStr2/do/FrontEnd/Comunicazioni/${sessionScope.fromPage}.action"/>&amp;fromStipula=1&amp;idComunicazione=${idComunicazione}&amp;idDestinatario=${idDestinatario}" >
				<c:choose>
					<c:when test="${sessionScope.fromPage eq 'openPageDettaglioComunicazioneInviata'}">
						<wp:i18n key="LINK_BACK_TO_COMMUNICATIONS_SENT" />
					</c:when>
					<c:when test="${sessionScope.fromPage eq 'openPageDettaglioComunicazioneRicevuta'}">
						<wp:i18n key="LINK_BACK_TO_COMMUNICATIONS_RECEIVED" />
					</c:when>
				</c:choose>	
			</a>
		</c:when>
		<c:otherwise>
			<a href="<wp:action path="/ExtStr2/do/FrontEnd/Contratti/searchStipulaContratti.action" />&amp;last=1">
				<wp:i18n key="LINK_BACK_TO_LIST" />
			</a>
		</c:otherwise>
	</c:choose>
	</div>
		
</div>