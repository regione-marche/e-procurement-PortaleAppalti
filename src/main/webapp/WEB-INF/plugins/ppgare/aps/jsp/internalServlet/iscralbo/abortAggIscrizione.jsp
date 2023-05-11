<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>

<%-- Prepara il tipo di pagina in base al tipo di domanda 
	- AGGIORNAMENTO DI DATI O DOCUMENTI
	- DOMANDA DI RINNOVO
	- DOMANDA D'ISCRIZIONE 
--%>
<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/iscralbo/inc/initDomanda.jsp"/>

<s:if test="%{#session.dettIscrAlbo.tipologia == 2}">
	<c:set var="titolo"><wp:i18n key="TITLE_PAGE_ISCRALBO_MERCATO_ELETTRONICO"/></c:set>
	<c:set var="dettaglio"><wp:i18n key="LABEL_BANDO"/></c:set>
	<c:set var="lista"><wp:i18n key="LABEL_AL_MERCATO_ELETTRONICO"/></c:set>
</s:if>
<s:else>
	<c:set var="titolo"><wp:i18n key="TITLE_PAGE_ISCRALBO_ELENCO_OE"/></c:set>
	<c:set var="dettaglio"><wp:i18n key="LABEL_BANDO"/></c:set>
	<c:set var="lista"><wp:i18n key="LABEL_ALL_ELENCO"/></c:set>
</s:else>


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2><wp:i18n key='TITLE_PAGE_ISCRALBO_ANNULLA_AGGIORNAMENTO'/> <c:out value="${titolo}"/></h2>

	<p><wp:i18n key='LABEL_CANCEL_AGGIORNAMENTO_ISCRALBO_1'/> <c:out value="${lista}"/>.</p>
	<p><wp:i18n key='LABEL_CANCEL_AGGIORNAMENTO_ISCRALBO_2'/> <c:out value="${dettaglio}"/> <wp:i18n key='LABEL_CANCEL_AGGIORNAMENTO_ISCRALBO_3'/>.</p>

	<div class="azioni">
		<form action="<wp:action path="/ExtStr2/do/FrontEnd/IscrAlbo/cancelIscrizione.action" />" method="post" class="azione">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			
			<div>
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_confirm_cancel.jsp" />
				<input type="hidden" name="codice" value="<s:property value="codice" />"/>
				<input type="hidden" name="ext" value="${param.ext}" />
			</div>
		</form>
		
		<c:choose>
			<c:when test="${sessionScope.page eq 'impresa'}">
				<c:set var="href" value="/ExtStr2/do/FrontEnd/IscrAlbo/openPageIscrAlboImpresa.action" />
			</c:when>
			<c:when test="${sessionScope.page eq 'sa'}">
				<c:set var="href" value="/ExtStr2/do/FrontEnd/IscrAlbo/openPageSA.action" />
			</c:when>
			<c:when test="${sessionScope.page eq 'RTI'}">
				<c:set var="href" value="/ExtStr2/do/FrontEnd/IscrAlbo/openPageIscrAlboRTI.action" />
			</c:when>
			<c:when test="${sessionScope.page eq 'Componenti'}">
				<c:set var="href" value="/ExtStr2/do/FrontEnd/IscrAlbo/openPageIscrAlboComponenti.action" />
			</c:when>
			<c:when test="${sessionScope.page eq 'categorie'}">
				<c:set var="href" value="/ExtStr2/do/FrontEnd/IscrAlbo/openPageIscrAlboCategorie.action" />
			</c:when>
			<c:when test="${sessionScope.page eq 'riepilogoCategorie'}">
				<c:set var="href" value="/ExtStr2/do/FrontEnd/IscrAlbo/openPageIscrAlboRiepilogoCategorie.action" />
			</c:when>
			<c:when test="${sessionScope.page eq 'documenti'}">
				<c:set var="href" value="/ExtStr2/do/FrontEnd/IscrAlbo/openPageIscrAlboDocumenti.action" />
			</c:when>
			<c:when test="${sessionScope.page eq 'riepilogo'}">
				<c:set var="href" value="/ExtStr2/do/FrontEnd/IscrAlbo/openPageIscrAlboRiepilogo.action" />
			</c:when>
			<c:when test="${sessionScope.page eq 'generaPdfRichiesta'}">
				<c:if test = "${sessionIdObj eq 'dettIscrAlbo'}">
					<c:set var="href" value="/ExtStr2/do/FrontEnd/IscrAlbo/openPageIscrAlboGeneraPdfRichiesta.action" />
				</c:if>
				<c:if test = "${sessionIdObj eq 'dettRinnAlbo'}">
					<c:set var="href" value="/ExtStr2/do/FrontEnd/IscrAlbo/openPageRinnovoGeneraPdfRichiesta.action" />
				</c:if>
			</c:when>
			<c:when test="${sessionScope.page eq 'openQC'}">
					<c:set var="href" value="/ExtStr2/do/FrontEnd/IscrAlbo/openQC.action" />
			</c:when>
			<c:when test="${sessionScope.page eq 'openQCSummary' and fromQFormChanged}">
					<c:set var="href" value="/ExtStr2/do/FrontEnd/IscrAlbo/openQCSummaryToView.action" />
			</c:when>
			<c:when test="${sessionScope.page eq 'openQCSummary' and not fromQFormChanged}">
					<c:set var="href" value="/ExtStr2/do/FrontEnd/IscrAlbo/openQCSummary.action" />
			</c:when>
		</c:choose>
		<form action="<wp:action path="${href}" />" method="post" class="azione">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			
			<div>
				<input type="hidden" name="ext" value="${param.ext}" />
				<wp:i18n key='BUTTON_WIZARD_BACK_TO_AGGIORNAMENTO_ISCRALBO' var="valueBackToUpdateButton"/>
				<s:submit value="%{#attr.valueBackToUpdateButton}" cssClass="button" />
			</div>
		</form>
	</div>
</div>