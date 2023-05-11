<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>


<s:set name="isFromListaOfferte" value="%{fromListaOfferte == 1}" />
<c:set var="dtaOfferta"><s:date name="dataOfferta" format="dd/MM/yyyy" /></c:set>
<c:set var="dtaInvio"><s:date name="dataInvio" format="dd/MM/yyyy HH:mm:ss" /></c:set>

<!-- 
annullamento=${annullamento}<br/>
eliminazione=${eliminazione}<br/>
rettifica=${rettifica}<br/>
operazione=${operazione}<br/>
isFromListaOfferte=<s:property value="%{#isFromListaOfferte}"/><br/>
 -->

<c:if test="${operazione == inviaOfferta}">
	<s:if test="%{#isFromListaOfferte}">
		<c:choose>
			<c:when test="${annullamento}">
				<c:set var="title"><wp:i18n key='TITLE_PAGE_GARETEL_OFFERTA_ANNULLATA'/></c:set>
				<c:set var="info1">${successMessage}</c:set>
				<c:set var="info2"><wp:i18n key='LABEL_GARETEL_ANNULLA_OFFERTA_2'/></c:set>
			</c:when>
			<c:when test="${eliminazione}">
				<c:set var="title"><wp:i18n key='TITLE_PAGE_GARETEL_OFFERTA_ELIMINATA'/></c:set>
				<c:set var="info1">${successMessage}</c:set>
				<c:set var="info2"><wp:i18n key='LABEL_GARETEL_ANNULLA_OFFERTA_2'/></c:set>
			</c:when>
			<c:when test="${rettifica}">
				<c:set var="title"><wp:i18n key='TITLE_PAGE_GARETEL_OFFERTA_RETTIFICATA'/></c:set>
				<c:set var="info1">${successMessage}</c:set>
				<c:set var="info2"><wp:i18n key='LABEL_GARETEL_ANNULLA_OFFERTA_2'/></c:set>
			</c:when>
		</c:choose>
	</s:if>
	<s:else>
		<c:set var="title"><wp:i18n key='TITLE_PAGE_GARETEL_OFFERTA_ANNULLATA'/></c:set>
		<c:set var="info1">
			<wp:i18n key='LABEL_GARETEL_ANNULLA_OFFERTA_1'/> <s:date name="dataOfferta" format="dd/MM/yyyy" /> 
			<wp:i18n key='LABEL_EFFETTUATE_CON_SUCCESSO_IL'/> <s:date name="dataInvio" format="dd/MM/yyyy HH:mm:ss" />.
		</c:set>
		<c:set var="info2"><wp:i18n key='LABEL_GARETEL_ANNULLA_OFFERTA_2'/></c:set>
	</s:else>
</c:if>	

<c:if test="${operazione == presentaPartecipazione}">
	<s:if test="%{#isFromListaOfferte}">
		<c:choose>
			<c:when test="${annullamento}">
				<c:set var="title"><wp:i18n key='TITLE_PAGE_GARETEL_PARTECIPAZIONE_ANNULLATA'/></c:set>
				<c:set var="info1">${successMessage}</c:set>
				<c:set var="info2"><wp:i18n key='LABEL_GARETEL_ANNULLA_PARTECIPAZIONE_2'/></c:set>
			</c:when>
			<c:when test="${eliminazione}">
				<c:set var="title"><wp:i18n key='TITLE_PAGE_GARETEL_PARTECIPAZIONE_ELIMINATA'/></c:set>
				<c:set var="info1">${successMessage}</c:set>
				<c:set var="info2"><wp:i18n key='LABEL_GARETEL_ANNULLA_PARTECIPAZIONE_2'/></c:set>
			</c:when>
			<c:when test="${rettifica}">
				<c:set var="title"><wp:i18n key='TITLE_PAGE_GARETEL_PARTECIPAZIONE_RETTIFICATA'/></c:set>
				<c:set var="info1">${successMessage}</c:set>
				<c:set var="info2"><wp:i18n key='LABEL_GARETEL_ANNULLA_PARTECIPAZIONE_2'/></c:set>
			</c:when>
		</c:choose>
	</s:if>
	<s:else>
		<c:set var="title"><wp:i18n key='TITLE_PAGE_GARETEL_PARTECIPAZIONE_RETTIFICATA'/></c:set>
		<c:set var="info1">
			<wp:i18n key='LABEL_GARETEL_ANNULLA_PARTECIPAZIONE_1'/> <s:date name="dataOfferta" format="dd/MM/yyyy" /> 
			<wp:i18n key='LABEL_EFFETTUATE_CON_SUCCESSO_IL'/> <s:date name="dataInvio" format="dd/MM/yyyy HH:mm:ss" />.
		</c:set>
		<c:set var="info2"><wp:i18n key='LABEL_GARETEL_ANNULLA_PARTECIPAZIONE_2'/></c:set>
	</s:else>
</c:if>


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">
	
	<h2>${title}</h2>

	<p>
		${info1}
	</p>
	<p>
		${info2}
	</p>
	
	<s:if test="%{msgErrore != null}">
		<p><wp:i18n key="LABEL_MESSAGE_WARNING" />: <s:property value="%{msgErrore}"/></p>
	</s:if>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_messages.jsp" />

	<div class="back-link">
		<s:if test="%{#isFromListaOfferte}">
			<a href="<wp:action path='/ExtStr2/do/FrontEnd/GareTel/openGestioneListaOfferte.action'/>&amp;ext=${param.ext}&amp;codice=${codice}&amp;operazione=${operazione}&amp;${tokenHrefParams}">
				<wp:i18n key="LINK_BACK_TO_PROCEDURE" />
			</a>
		</s:if>
		<s:else>
			<a href="<wp:action path='/ExtStr2/do/FrontEnd/Bandi/view.action'/>&amp;ext=${param.ext}&amp;codice=${codice}&amp;operazione=${operazione}&amp;${tokenHrefParams}">
				<wp:i18n key="LINK_BACK_TO_PROCEDURE" />
			</a>
		</s:else>
	</div>
</div>