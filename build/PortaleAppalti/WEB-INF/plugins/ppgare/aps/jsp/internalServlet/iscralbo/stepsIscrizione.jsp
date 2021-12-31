<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>

<s:set var="sessionId">${param.sessionIdObj}</s:set>
<p class="noscreen"><wp:i18n key="LABEL_WIZARD_STEPS" />:</p>

<ol class="steps">
	<li>
		<c:if test="${sessionScope.page eq 'impresa'}">
			<div class="current-step">
		</c:if>
				<wp:i18n key='LABEL_DATI_ANAGRAFICI'/>
		<c:if test="${sessionScope.page eq 'impresa'}">
				<span class="noscreen"> (<wp:i18n key="LABEL_WIZARD_CURRENT_STEP" />)</span>
			</div>
		</c:if>
	</li>
	
	<c:if test="${(param.sessionIdObj eq 'dettIscrAlbo' && !sessionScope.dettIscrAlbo.aggiornamentoSoloDocumenti && !sessionScope.dettIscrAlbo.unicaStazioneAppaltante) || (param.sessionIdObj eq 'dettRinnAlbo' && !sessionScope.dettRinnAlbo.aggiornamentoSoloDocumenti && !sessionScope.dettRinnAlbo.unicaStazioneAppaltante)}">
		<li>
			<c:if test="${sessionScope.page eq 'sa'}">
				<div class="current-step">
			</c:if>
					<wp:i18n key='LABEL_STAZIONI_APPALTANTI'/>
			<c:if test="${sessionScope.page eq 'sa'}">
					<span class="noscreen"> (<wp:i18n key="LABEL_WIZARD_CURRENT_STEP" />)</span>
				</div>
			</c:if>
		</li>
	</c:if> 
	
	<s:if test="%{#session[#sessionId].stepNavigazione.contains(STEP_DENOMINAZIONE_RTI)}">
		<li>
			<c:if test="${sessionScope.page eq 'RTI'}">
				<div class="current-step">
			</c:if>
					<wp:i18n key='LABEL_FORMA_DI_PARTECIPAZIONE'/>
			<c:if test="${sessionScope.page eq 'RTI'}">
					<span class="noscreen"> (<wp:i18n key="LABEL_WIZARD_CURRENT_STEP" />)</span>
				</div>
			</c:if>
		</li>
	</s:if>

	<s:if test="%{#session[#sessionId].stepNavigazione.contains(STEP_DETTAGLI_RTI)}">
		<li id="step_componenti" style='display: <c:choose><c:when test="${sessionScope.dettIscrAlbo.rti || sessionScope.dettIscrAlbo.impresa.consorzio}">inline</c:when><c:otherwise>none</c:otherwise></c:choose>'>
			<c:if test="${sessionScope.page eq 'Componenti'}">
				<div class="current-step">
			</c:if>
					<c:choose>
						<c:when test="${sessionScope.dettIscrAlbo.rti}"><wp:i18n key='LABEL_COMPONENTI_RAGGRUPPAMENTO'/></c:when>
						<c:when test="${sessionScope.dettIscrAlbo.impresa.consorzio}"><wp:i18n key='LABEL_CONSORZIATE_ESECUTRICI'/></c:when>
					</c:choose>
			<c:if test="${sessionScope.page eq 'Componenti'}">
					<span class="noscreen"> (<wp:i18n key="LABEL_WIZARD_CURRENT_STEP" />)</span>
				</div>
			</c:if>
		</li>
	</s:if>

	<s:if test="%{#session[#sessionId].stepNavigazione.contains(STEP_SELEZIONE_CATEGORIE)}">
		<li>
			<c:if test="${sessionScope.page eq 'categorie'}">
				<div class="current-step">
			</c:if>
					<wp:i18n key='LABEL_SELEZIONE_CATEGORIE'/>
			<c:if test="${sessionScope.page eq 'categorie'}">
					<span class="noscreen"> (<wp:i18n key="LABEL_WIZARD_CURRENT_STEP" />)</span>
				</div>
			</c:if>
		</li>
		<li>
			<c:if test="${sessionScope.page eq 'riepilogoCategorie'}">
				<div class="current-step">
			</c:if>
					<wp:i18n key='LABEL_RIEPILOGO_CATEGORIE'/>
			<c:if test="${sessionScope.page eq 'riepilogoCategorie'}">
					<span class="noscreen"> (<wp:i18n key="LABEL_WIZARD_CURRENT_STEP" />)</span>
				</div>
			</c:if>
		</li>
	</s:if>

	<s:if test="%{#session[#sessionId].stepNavigazione.contains(STEP_SCARICA_ISCRIZIONE)}">
		<li id="step_generaPdfRichiesta">
			<c:if test="${sessionScope.page eq 'generaPdfRichiesta'}">
				<div class="current-step">
			</c:if>
				<s:if test="%{#session[#sessionId].rinnovoIscrizione}">
					<wp:i18n key='LABEL_ISCRALBO_SCARICA_DOMANDA_RINNOVO'/>
				</s:if>
				<s:elseif test="%{#session[#sessionId].aggiornamentoIscrizione}">
					<wp:i18n key='LABEL_ISCRALBO_SCARICA_DOMANDA_AGGIORNAMENTO'/> 
				</s:elseif>
				<s:else>
					<wp:i18n key='LABEL_ISCRALBO_SCARICA_DOMANDA_ISCRIZIONE'/>
				</s:else>
			<c:if test="${sessionScope.page eq 'generaPdfRichiesta'}">
					<span class="noscreen"> (<wp:i18n key="LABEL_WIZARD_CURRENT_STEP" />)</span>
				</div>
			</c:if>
		</li>
	</s:if>
	
	<li>
		<c:if test="${sessionScope.page eq 'documenti'}">
			<div class="current-step">
		</c:if>
				<wp:i18n key='LABEL_ISCRALBO_DOCUMENTAZIONE_RICHIESTA'/>
		<c:if test="${sessionScope.page eq 'documenti'}">
				<span class="noscreen"> (<wp:i18n key="LABEL_WIZARD_CURRENT_STEP" />)</span>
			</div>
		</c:if>
	</li>
	
	<li>
		<c:if test="${sessionScope.page eq 'riepilogo'}">
			<div class="current-step">
		</c:if>
				<wp:i18n key='LABEL_ISCRALBO_PRESENTAZIONE_DOMANDA'/>
		<c:if test="${sessionScope.page eq 'riepilogo'}">
				<span class="noscreen"> (<wp:i18n key="LABEL_WIZARD_CURRENT_STEP" />)</span>
			</div>
		</c:if>
	</li>

</ol>