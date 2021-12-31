<%@ taglib prefix="wp" uri="aps-core.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<c:if test="${sessionScope.currentUser != 'guest'}">
</c:if>


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp">
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>
	
<div class="portgare-view">

	<h2><wp:i18n key='TITLE_PAGE_ASTA_STORIA_RILANCI'/></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_ASTA_STORIA_RILANCI" />
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />

	<div class="detail-section first-detail-section">
		<h3 class="detail-section-title">
			<span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key='LABEL_RILANCI_ASTA'/> 
		</h3>
	
		<div class="detail-row">
			<s:set var="elencoRilanci" value="%{listaRilanci}" />
			<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/aste/inc/iteratorRilanci.jsp">
				<jsp:param name="tipoOfferta" value="${dettaglioAsta.tipoOfferta}" />
			</jsp:include>
		</div>
	</div>
		
	<div class="back-link">
		<c:if test="${fromPage == 'classifica'}">
			<a href="<wp:action path="/ExtStr2/do/FrontEnd/Aste/classifica.action"/>&amp;codice=${param.codice}&amp;codiceLotto=${param.codiceLotto}&amp;${tokenHrefParams}">
				<wp:i18n key='LINK_BACK_TO_DETTAGLIO_ASTA'/>
			</a>
		</c:if>
		<c:if test="${fromPage == 'riepilogo'}">
			<a href="<wp:action path="/ExtStr2/do/FrontEnd/Aste/riepilogo.action"/>&amp;codice=${param.codice}&amp;codiceLotto=${param.codiceLotto}&amp;${tokenHrefParams}">
				<wp:i18n key='LINK_BACK_TO_DETTAGLIO_ASTA'/>
			</a>
		</c:if>
	</div>
</div>