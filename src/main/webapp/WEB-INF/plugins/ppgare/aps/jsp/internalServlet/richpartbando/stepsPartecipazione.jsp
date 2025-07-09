<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>

<c:set var="partecipazione" value="${sessionScope.dettaglioOffertaGara.bustaPartecipazione.helper}"/>

<c:set var="rti" value="${partecipazione.rti}" />
<c:set var="consorzio" value="${partecipazione.impresa.consorzio}" />

<%--
<c:set var="garaRistretta" value="${partecipazione.iterGara == 2}" />
<c:set var="domandaPartecipazione" value="${partecipazione.tipoEvento == 1}" />
<c:set var="esecutrici" value="${(consorzio && !garaRistretta) || 
                                 (consorzio && !(garaRistretta && domandaPartecipazione))}" />			                 	

consorzio=${consorzio}<br/>
garaRistretta=${garaRistretta}<br/>
domandaPartecipazione=${domandaPartecipazione}<br/>
esecutrici=${esecutrici}<br/>
--%>
 
<p class="noscreen"><wp:i18n key="LABEL_WIZARD_STEPS" />:</p>
<ol class="steps">

	<li>
		<c:if test="${sessionScope.page eq 'impresa'}">
			<div class="current-step">
		</c:if>
				<wp:i18n key="LABEL_DATI_ANAGRAFICI" />
		<c:if test="${sessionScope.page eq 'impresa'}">
				<span class="noscreen"> (<wp:i18n key="LABEL_WIZARD_CURRENT_STEP" />)</span>
			</div>
		</c:if>
	</li>
	
	<c:if test='${partecipazione.stepNavigazioneAbilitato["rti"]}' >
		<li>
			<c:if test="${sessionScope.page eq 'rti'}">
				<div class="current-step">
			</c:if>
					<wp:i18n key="LABEL_FORMA_DI_PARTECIPAZIONE" />
			<c:if test="${sessionScope.page eq 'rti'}">
					<span class="noscreen"> (<wp:i18n key="LABEL_WIZARD_CURRENT_STEP" />)</span>
				</div>
			</c:if>
		</li>
	</c:if>	
	
	<c:if test='${partecipazione.stepNavigazioneAbilitato["componenti"]}' >
		<li id="step_componenti" <c:if test="${!(rti || consorzio)}">style='display: none'</c:if>>
			<c:if test="${sessionScope.page eq 'componenti'}">
				<div class="current-step">
			</c:if>
			<c:choose>
				<c:when test="${rti}"><wp:i18n key="LABEL_COMPONENTI_RAGGRUPPAMENTO" /></c:when>
				<c:when test="${consorzio}"><wp:i18n key="LABEL_CONSORZIATE_ESECUTRICI" /></c:when>
			</c:choose>
			<c:if test="${sessionScope.page eq 'componenti'}">
					<span class="noscreen"> (<wp:i18n key="LABEL_WIZARD_CURRENT_STEP" />)</span>
				</div>
			</c:if>
		</li>
	</c:if>	
	
	<c:if test='${partecipazione.stepNavigazioneAbilitato["avvalimento"]}' >
		<li>
			<c:if test="${sessionScope.page eq 'avvalimento'}">
				<div class="current-step">
			</c:if>
					<wp:i18n key="LABEL_AVVALIMENTO" />
			<c:if test="${sessionScope.page eq 'avvalimento'}">
					<span class="noscreen"> (<wp:i18n key="LABEL_WIZARD_CURRENT_STEP" />)</span>
				</div>
			</c:if>
		</li>
	</c:if>	
	
	<c:if test='${partecipazione.stepNavigazioneAbilitato["lotti"]}' >
		<li>
			<c:if test="${sessionScope.page eq 'lotti'}">
				<div class="current-step">
			</c:if>
					<wp:i18n key="LABEL_LOTTI" />
			<c:if test="${sessionScope.page eq 'lotti'}">
					<span class="noscreen"> (<wp:i18n key="LABEL_WIZARD_CURRENT_STEP" />)</span>
				</div>
			</c:if>
		</li>	
	</c:if>
	
	<li>
		<c:if test="${sessionScope.page eq 'fine'}">
			<div class="current-step">
		</c:if>
				<wp:i18n key="LABEL_RIEPILOGO" />
		<c:if test="${sessionScope.page eq 'fine'}">
				<span class="noscreen"> (<wp:i18n key="LABEL_WIZARD_CURRENT_STEP" />)</span>
			</div>
		</c:if>
	</li>
	
</ol>
