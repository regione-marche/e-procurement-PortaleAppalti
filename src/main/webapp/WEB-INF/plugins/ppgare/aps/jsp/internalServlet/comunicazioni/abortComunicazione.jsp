<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>


<s:set var="helper" value="%{#session['nuovaComunicazione']}"/>

<s:set var="rettifica" value="%{#helper.modelloRettifica}" />

<c:set var="titleKey" value="TITLE_PAGE_COMUNICAZIONI_ANNULLA_AGGIORNAMENTO" />

<s:if test="%{#rettifica}" >
	<c:set var="titleKey" value="TITLE_RETTIFICA_OFFERTA" />
	<c:set var="hrefConferma"><wp:action path="/ExtStr2/do/FrontEnd/Comunicazioni/cancelNuovaRettifica.action" /></c:set>	
	<c:choose>
		<c:when test="${sessionScope.page eq 'testoRettifica'}">
			<c:set var="href" value="/ExtStr2/do/FrontEnd/Comunicazioni/testoRettifica.action" />
		</c:when>
			<c:when test="${sessionScope.page eq 'documentiRettifica'}">
		<c:set var="href" value="/ExtStr2/do/FrontEnd/Comunicazioni/documentiRettifica.action" />
		</c:when>
		<c:when test="${sessionScope.page eq 'inviaRettifica'}">
			<c:set var="href" value="/ExtStr2/do/FrontEnd/Comunicazioni/inviaRettifica.action" />
		</c:when>
	</c:choose>
</s:if>
<s:elseif test="%{#helper.modello > 0}" >
	<c:set var="hrefConferma"><wp:action path="/ExtStr2/do/FrontEnd/Comunicazioni/cancelNuovoSoccorso.action" /></c:set>	
	<c:choose>
		<c:when test="${sessionScope.page eq 'testoSoccorso'}">
			<c:set var="href" value="/ExtStr2/do/FrontEnd/Comunicazioni/testoSoccorso.action" />
		</c:when>
			<c:when test="${sessionScope.page eq 'documentiSoccorso'}">
		<c:set var="href" value="/ExtStr2/do/FrontEnd/Comunicazioni/documentiSoccorso.action" />
		</c:when>
		<c:when test="${sessionScope.page eq 'inviaSoccorso'}">
			<c:set var="href" value="/ExtStr2/do/FrontEnd/Comunicazioni/inviaSoccorso.action" />
		</c:when>
	</c:choose>
</s:elseif>
<s:else>
	<c:set var="hrefConferma"><wp:action path="/ExtStr2/do/FrontEnd/Comunicazioni/cancelNuovaComunicazione.action" /></c:set>
	<c:choose>
		<c:when test="${sessionScope.page eq 'testoComunicazione'}">
			<c:set var="href" value="/ExtStr2/do/FrontEnd/Comunicazioni/testoComunicazione.action" />
		</c:when>
			<c:when test="${sessionScope.page eq 'documenti'}">
		<c:set var="href" value="/ExtStr2/do/FrontEnd/Comunicazioni/documenti.action" />
		</c:when>
		<c:when test="${sessionScope.page eq 'inviaComunicazione'}">
			<c:set var="href" value="/ExtStr2/do/FrontEnd/Comunicazioni/inviaComunicazione.action" />
		</c:when>
	</c:choose>	
</s:else>


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2><wp:i18n key="${titleKey}" /></h2>

	<p><wp:i18n key="LABEL_CANCEL_REGISTRAZIONE_COMUNICAZIONI_1" /></p>
	<p><wp:i18n key="LABEL_CANCEL_REGISTRAZIONE_COMUNICAZIONI_2" /></p>
	
	<div class="azioni">
		<form action="${hrefConferma}" method="post" class="azione">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			<div>
				<input type="hidden"  name="codice" value="${sessionScope.nuovaComunicazione.codice}" />
				<input type="hidden"  name="codice2" value="${sessionScope.nuovaComunicazione.codice2}" />
				<s:if test="%{!#rettifica}" >
					<input type="hidden"  name="nappal" value="${sessionScope.nuovaComunicazione.codice2}" />
				</s:if>
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_confirm_cancel.jsp" />
			</div>
		</form>
		
		<form action="<wp:action path="${href}" />" method="post" class="azione">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			<div>
				<wp:i18n key="BUTTON_BACK_TO_COMUNICAZIONE" var="valueBackLink"/>
				<s:submit value="%{#attr.valueBackLink}" cssClass="button" />
			</div>
		</form>
	</div>
</div>