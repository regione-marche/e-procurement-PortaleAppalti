<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="es"
	uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld"%>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<es:getAppParam name="denominazioneStazioneAppaltanteUnica" var="stazAppUnica" scope = "page"/> 	

<div class="portgare-view">

	<h2><wp:i18n key="TITLE_PAGE_DETTAGLIO_CONTRATTO_LFS" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_DETTAGLIO_CONTRATTO_LFS"/>
	</jsp:include>



	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />

	<s:if test="! hasActionErrors()">		
		<div class="detail-section first-detail-section">
			<h3 class="detail-section-title"><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_STAZIONE_APPALTANTE" /></h3>

			<div class="detail-row">
				<label><wp:i18n key="LABEL_DENOMINAZIONE" /> : </label>
				<c:choose>
					<c:when test="${! empty stazAppUnica }">
						<s:set var="stazAppUnicaToStruts">${stazAppUnica}</s:set>
						<s:property value="stazAppUnicaToStruts" />
					</c:when>
					<c:otherwise>
						<s:property value="dettaglioContratto.stazioneAppaltante" />
					</c:otherwise>
				</c:choose>
			</div>
			<div class="detail-row">
				<label><wp:i18n key="LABEL_DIRETTORE_ESECUZIONE" /> : </label>
				<s:property value="dettaglioContratto.nominativoDirettoreTecni" />
				<s:property value="dettaglioContratto.nominativoDirettoreImpr" />
			</div>
			<div class="detail-row">
				<label><wp:i18n key="LABEL_COORDINATORE_SICUREZZA" /> : </label>
				<s:property value="dettaglioContratto.nominativoRespSicImpr" />
				<s:property value="dettaglioContratto.nominativoRespSicTecni" />
			</div>
		</div>
		
		<div class="detail-section">
			<h3 class="detail-section-title"><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_DATI_GENERALI" /></h3>
			<div class="detail-row">
				<label><wp:i18n key="LABEL_CODICE_CONTRATTO" /> : </label>
				<s:property value="dettaglioContratto.codice" />/<s:property value="dettaglioContratto.nappal" />  
			</div>
			<div class="detail-row">
				<label><wp:i18n key="LABEL_OGGETTO_CONTRATTO" /> : </label>
				<s:property value="dettaglioContratto.oggetto" /> 
			</div>
			<div class="detail-row">
				<label><wp:i18n key="LABEL_IMPORTO_CONTRATTO" /> : </label>
				<c:if test="${!empty (dettaglioContratto.importo)}">
					<s:text name="format.money"><s:param value="dettaglioContratto.importo"/></s:text> &euro;
				</c:if>
			</div>
			<div class="detail-row">
				<label><wp:i18n key="LABEL_DATA_CONTRATTO" /> : </label>
				<s:date name="dettaglioContratto.dataContratto" format="dd/MM/yyyy" />
				<s:property value="" /> 
			</div>
			<div class="detail-row">
				<label><wp:i18n key="LABEL_NUMERO_REPERTORIO" /> : </label>
				<s:property value="dettaglioContratto.numeroRepertorio" /> 
			</div>
			<div class="detail-row">
				<label><wp:i18n key="LABEL_FASE_CONTRATTO" /> : </label>
				<s:property value="dettaglioContratto.fase" /> 
			</div>
			<div class="detail-row">
				<label><wp:i18n key="LABEL_RIFERIMENTO_PROCEDURA" /> : </label>
				<s:property value="dettaglioContratto.ngara" /> 
			</div>
		</div>
		<c:if test="${sessionScope.currentUser != 'guest'}">
			<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/comunicazioni/sommarioComunicazioni.jsp" >
				<jsp:param name="entita" value="APPA"/>
			</jsp:include>
		</c:if>
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
			<a href="<wp:action path="/ExtStr2/do/FrontEnd/ContrattiLFS/searchContratti.action" />&amp;last=1">
				<wp:i18n key="LINK_BACK_TO_LIST" />
			</a>
		</c:otherwise>
	</c:choose>
	</div>
		
</div>