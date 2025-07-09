<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>

<c:set var="dataPubblicazioneBando"><s:date name="dettaglioGara.datiGeneraliGara.dataPubblicazione" format="dd/MM/yyyy" /></c:set>

<wp:i18n key="LABEL_MERCATO_ELETTRONICO" var="mepa" />

<c:choose>
	<c:when test="${avvisiComunicazioniAtti != null && avvisiComunicazioniAtti}">
		<c:set var="titolo"><wp:i18n key="LABEL_ALTRI_DOCUMENTI" /></c:set>
		<c:set var="balloon">BALLOON_ALTRI_DOCUMENTI</c:set>
		<c:set var="backUrl"><wp:action path="/ExtStr2/do/FrontEnd/Avvisi/viewAvvisoComunicazioneAtto.action" />&amp;codice=${param.codice}&amp;ext=${param.ext}</c:set>
		<c:set var="backLink">Torna all'avviso</c:set>
	</c:when>
	<c:when test="${dettaglioAvviso != null}">
		<c:set var="titolo"><wp:i18n key="LABEL_ALTRI_DOCUMENTI" /></c:set>
		<c:set var="balloon">BALLOON_ALTRI_DOCUMENTI</c:set>
		<c:set var="backUrl"><wp:action path="/ExtStr2/do/FrontEnd/Avvisi/view.action" />&amp;codice=${param.codice}&amp;ext=${param.ext}</c:set>
		<c:set var="backLink">Torna all'avviso</c:set>
	</c:when>
	<c:when test="${dettaglioIscrizione != null}">
		<c:set var="titolo"><wp:i18n key="LABEL_ALTRI_DOCUMENTI" /></c:set>
		<c:set var="balloon">BALLOON_ALTRI_DOCUMENTI</c:set>
		<c:choose>
			<c:when test="${param.entita == mepa}">
				<c:set var="backUrl"><wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/viewIscrizione.action" />&amp;codice=${param.codice}&amp;ext=${param.ext}</c:set>
				<c:set var="backLink"><wp:i18n key="LINK_BACK_TO_ISCRIZIONE" /></c:set>
			</c:when>
			<c:otherwise>
				<c:set var="backUrl"><wp:action path="/ExtStr2/do/FrontEnd/Bandi/viewIscrizione.action" />&amp;codice=${param.codice}&amp;ext=${param.ext}</c:set>
				<c:set var="backLink"><wp:i18n key="LINK_BACK_TO_ELENCO" /></c:set>
			</c:otherwise>
		</c:choose> 
	</c:when>
	<c:otherwise>
		<c:set var="titolo"><wp:i18n key="LABEL_ALTRI_ATTI_DOCUMENTI" /></c:set>
		<c:set var="balloon">BALLOON_ALTRI_ATTI_DOCUMENTI</c:set>
		<c:set var="backUrl"><wp:action path="/ExtStr2/do/FrontEnd/Bandi/view.action" />&amp;codice=${param.codice}&amp;ext=${param.ext}</c:set>
		<c:set var="backLink"><wp:i18n key="LINK_BACK_TO_PROCEDURE" /></c:set>
	</c:otherwise>
</c:choose>
	

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2>${titolo}</h2>

	<c:choose>
		<c:when test="${dettaglioAvviso != null || dettaglioIscrizione != null}">
			<div class="balloon">
				<div class="balloon-content balloon-info">
					<wp:i18n key="BALLOON_ALTRI_DOCUMENTI" />
				</div>
			</div>
		</c:when>
		<c:otherwise>
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
				<jsp:param name="keyMsg" value="${balloon}"/>
			</jsp:include>
		</c:otherwise>
	</c:choose>
	
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
		
	<s:if test="%{attiDocumenti.length > 0}">
	<s:set var="elencoDocumentiAllegati" value="%{attiDocumenti}" />
		<div class="detail-section">
			<h3 class="detail-section-title">
				<span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_DOCUMENTS" />
			</h3>

			<div class="detail-row">
				<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/bandi/inc/iteratorDocAllegati.jsp">
					<jsp:param name="path" value="downloadDocumentoPubblico"/>
					<jsp:param name="dataPubblicazione" value="DATA_FITTIZIA_SEMPRE_VISIBILE" />
				</jsp:include>
			</div>
		</div>
	</s:if>
	<s:else>
		<wp:i18n key="LABEL_NO_DOCUMENT" />
	</s:else>
		
	<div class="back-link">
		<a href="${backUrl}">
			${backLink}
		</a>
	</div>
</div>