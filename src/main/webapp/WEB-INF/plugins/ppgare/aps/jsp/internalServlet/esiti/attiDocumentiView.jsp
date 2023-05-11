<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2><wp:i18n key="LABEL_ATTI_DOC_ART29" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
			<jsp:param name="keyMsg" value="BALLOON_ATTI_DOCUMENTI"/>
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
		
	<s:if test="%{attiDocumenti.length > 0}">
	<s:set var="elencoDocumentiAllegati" value="%{attiDocumenti}" />
		<div class="detail-section">
			<h3 class="detail-section-title">
				<span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_DOCUMENTS" />
			</h3>

			<div class="detail-row">
				
				<c:set var="dataPubblicazioneEsito"><s:date name="dettaglioEsito.datiGenerali.dataPubblicazione" format="dd/MM/yyyy" /></c:set>
	
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
		<a href="<wp:action path="/ExtStr2/do/FrontEnd/Esiti/view.action" />&amp;codice=${param.codice}&amp;ext=${param.ext}&amp;${tokenHrefParams}">
			<wp:i18n key="LINK_BACK_TO_PROCEDURE" />
		</a>
	</div>
</div>