<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:set name="isFromListaOfferte" value="%{fromListaOfferte == 1}" />

<s:if test="%{offerteDistinte}">
	<c:set var="href" value="/ExtStr2/do/FrontEnd/GareTel/riepilogoOfferteDistinte.action"/>
	<s:if test="%{#isFromListaOfferte}">
		<c:set var="href" value="/ExtStr2/do/FrontEnd/GareTel/openGestioneListaOfferte.action"/>
	</s:if>
	<c:set var="hrefRettifica" value="/ExtStr2/do/FrontEnd/GareTel/rettificaOfferteDistinte.action"/>
</s:if>
<s:else> 
	<c:set var="href" value="/ExtStr2/do/FrontEnd/GareTel/openRiepilogoOfferta.action"/>
	<s:if test="%{#isFromListaOfferte}">
		<c:set var="href" value="/ExtStr2/do/FrontEnd/GareTel/openGestioneListaOfferte.action"/>
	</s:if>
	<c:set var="hrefRettifica" value="/ExtStr2/do/FrontEnd/GareTel/rettificaOfferta.action"/>
</s:else>

<c:if test="${operazione == inviaOfferta}">
	<s:if test="%{#isFromListaOfferte}">
		<c:choose>
			<c:when test="${annullamento}">
				<c:set var="title"><wp:i18n key='TITLE_PAGE_GARETEL_ANNULLAMENTO_INVIO'/></c:set>
				<c:set var="info"><p><wp:i18n key='LABEL_GARETEL_ANNULLAMENTO_INVIO'/></p></c:set>
			</c:when>
			<c:when test="${eliminazione}">
				<c:set var="title"><wp:i18n key='TITLE_PAGE_GARETEL_ELIMINAZIONE_INVIO'/></c:set>
				<c:set var="info"><p><wp:i18n key='LABEL_GARETEL_ELIMINAZIONE_INVIO'/></p></c:set>
			</c:when>
			<c:when test="${rettifica}">
				<c:set var="title"><wp:i18n key='TITLE_PAGE_GARETEL_RETTIFICA_INVIO'/></c:set>
				<c:set var="info">
					<p><wp:i18n key='LABEL_GARETEL_RETTIFICA_INVIO_1'/></p>
					<p><wp:i18n key='LABEL_GARETEL_RETTIFICA_INVIO_2'/></p>
					<p><wp:i18n key='LABEL_GARETEL_RETTIFICA_INVIO_3'/></p>
				</c:set>
			</c:when>
		</c:choose>
	</s:if>
	<s:else>
		<c:set var="title"><wp:i18n key='TITLE_PAGE_GARETEL_RETTIFICA_INVIO'/></c:set>
		<c:set var="info">
			<p><wp:i18n key='LABEL_GARETEL_RETTIFICA_INVIO_1'/></p>
			<p><wp:i18n key='LABEL_GARETEL_RETTIFICA_INVIO_2'/></p>
			<p><wp:i18n key='LABEL_GARETEL_RETTIFICA_INVIO_3'/></p>
		</c:set>
	</s:else>
</c:if>	
<c:if test="${operazione == presentaPartecipazione}">
	<s:if test="%{#isFromListaOfferte}">
		<c:choose>
			<c:when test="${annullamento}">
				<c:set var="title"><wp:i18n key='TITLE_PAGE_GARETEL_ANNULLAMENTO_PARTECIPAZIONE'/></c:set>
				<c:set var="info"><p><wp:i18n key='LABEL_GARETEL_ANNULLAMENTO_PARTECIPAZIONE'/></p></c:set>
			</c:when>
			<c:when test="${eliminazione}">
				<c:set var="title"><wp:i18n key='TITLE_PAGE_GARETEL_ELIMINAZIONE_PARTECIPAZIONE'/></c:set>
				<c:set var="info"><p><wp:i18n key='LABEL_GARETEL_ELIMINAZIONE_PARTECIPAZIONE'/></p></c:set>
			</c:when>
			<c:when test="${rettifica}">
				<c:set var="title"><wp:i18n key='TITLE_PAGE_GARETEL_RETTIFICA_PARTECIPAZIONE'/></c:set>
				<c:set var="info">
					<p><wp:i18n key='LABEL_GARETEL_RETTIFICA_PARTECIPAZIONE_1'/></p>
					<p><wp:i18n key='LABEL_GARETEL_RETTIFICA_PARTECIPAZIONE_2'/></p>
					<p><wp:i18n key='LABEL_GARETEL_RETTIFICA_PARTECIPAZIONE_3'/></p>
				</c:set>
			</c:when>
		</c:choose>
	</s:if>
	<s:else>
		<c:set var="title"><wp:i18n key='TITLE_PAGE_GARETEL_RETTIFICA_PARTECIPAZIONE'/></c:set>
		<c:set var="info">
			<p><wp:i18n key='LABEL_GARETEL_RETTIFICA_PARTECIPAZIONE_1'/></p>
			<p><wp:i18n key='LABEL_GARETEL_RETTIFICA_PARTECIPAZIONE_2'/></p>
			<p><wp:i18n key='LABEL_GARETEL_RETTIFICA_PARTECIPAZIONE_3'/></p>
		</c:set>
	</s:else>
</c:if>

<%--
reset=${reset}<br/>
annullamento=${annullamento}<br/>
rettifica=${rettifica}<br/>
 --%>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2>${title}</h2>
	
	${info}

	<div class="azioni">
		<wp:i18n key="LABEL_YES" var="valueYesButton" />
		<wp:i18n key="LABEL_NO" var="valueNoButton" />
		
		<form action="<wp:action path="${hrefRettifica}" />" method="post" class="azione">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			
			<div>
				<s:if test="!sendBlocked">
					<s:submit value="%{#attr.valueYesButton}" cssClass="button" />
				</s:if>
				<input type="hidden" name="codice" value="<s:property value="codice" />"/>
				<input type="hidden" name="operazione" value="<s:property value="operazione" />"/>
				<input type="hidden" name="progressivoOfferta" value="<s:property value="progressivoOfferta" />"/>
				<input type="hidden" name="ext" value="${param.ext}" />
				<input type="hidden" name="fromListaOfferte" value="<s:property value="fromListaOfferte" />" />
				<input type="hidden" name="progressivoLista" value="<s:property value="progressivoLista" />" />
			</div>
		</form>
		
		<form action="<wp:action path="${href}" />" method="post" class="azione">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			
			<div>
				<s:submit value="%{#attr.valueNoButton}" cssClass="button" />
				<input type="hidden" name="codice" value="<s:property value="codice" />"/>
				<input type="hidden" name="operazione" value="<s:property value="operazione" />"/>
				<input type="hidden" name="progressivoOfferta" value="<s:property value="progressivoOfferta" />"/>
				<input type="hidden" name="ext" value="${param.ext}" />
				<input type="hidden" name="fromListaOfferte" value="<s:property value="fromListaOfferte" />" />
				<input type="hidden" name="progressivoLista" value="<s:property value="progressivoLista" />" />				
			</div>
		</form>
	</div>
</div>