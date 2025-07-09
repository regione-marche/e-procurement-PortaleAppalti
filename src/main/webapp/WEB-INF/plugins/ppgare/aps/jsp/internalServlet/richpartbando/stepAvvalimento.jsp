<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>

<s:set var="partecipazione" value="%{#session.dettaglioOffertaGara.bustaPartecipazione.helper}"/>

<s:if test="%{#partecipazione.tipoEvento == 1}">
	<c:set var="titolo"><wp:i18n key='TITLE_PAGE_RICHPART_PRESENTA_PARTECIPAZIONE'/></c:set>
</s:if>
<s:elseif test="%{#partecipazione.tipoEvento == 2}">
	<c:set var="titolo"><wp:i18n key='TITLE_PAGE_RICHPART_PRESENTA_OFFERTA'/></c:set>
</s:elseif>
<s:else>
	<c:set var="titolo"><wp:i18n key=""/></c:set>
</s:else>

<c:set var="codiceTitolo" value="${partecipazione.getIdBando()}"/>


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2>${titolo} [${codiceTitolo}]</h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/field_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_messages.jsp" />

	<jsp:include page="stepsPartecipazione.jsp" />

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_INVIO_OFFERTA_DETTAGLIO_AVVALIMENTO"/>
	</jsp:include>

	<form action="<wp:action path="/ExtStr2/do/FrontEnd/RichPartBando/processPageAvvalimento.action" />" method="post">
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
		
		<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/richpartbando/datiAvvalimentoSection.jsp">
			<jsp:param name="namespace" value="RichPartBando" />
			<jsp:param name="sessionIdObj" value="dettaglioOffertaGara" />
		</jsp:include>

		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/skip_main_content_from_buttons.jsp" />

		<s:if test="%{!delete}">
			<div class="azioni">
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_previous.jsp" />
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_next.jsp" />
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_cancel.jsp" />
				<s:hidden name="id" value="%{id}"/>
				<s:hidden name="idDelete" value="%{idDelete}"/>
				<input type="hidden" name="ext" value="${param.ext}" />
			</div>
		</s:if>
	</form>
</div>