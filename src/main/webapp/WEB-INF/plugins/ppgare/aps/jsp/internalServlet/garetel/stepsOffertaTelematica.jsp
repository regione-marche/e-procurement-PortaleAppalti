<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>

<s:set var="page" value="%{#session['page']" />
<s:set var="sessionId">offertaEconomica</s:set>
<!-- OBSOLETO <s:set var="helper" value="%{#session[#sessionId]}" /> -->
<s:set name="helper" value="%{#session['dettaglioOffertaGara'].bustaEconomica.helper}" />


<p class="noscreen"><wp:i18n key="LABEL_WIZARD_STEPS" />:</p>
<ol class="steps">

	<s:if test="%{#helper.stepNavigazione.contains(STEP_PREZZI_UNITARI)}">
		<li>
			<s:if test="%{#page == STEP_PREZZI_UNITARI}">
				<div class="current-step">
			</s:if>
					<wp:i18n key="LABEL_GARETEL_STEP_PREZZI_UNITARI" />
			<s:if test="%{#page == STEP_PREZZI_UNITARI}">
					<span class="noscreen">(<wp:i18n key="LABEL_WIZARD_CURRENT_STEP" />)</span>
				</div>
			</s:if>
		</li>
	</s:if>
	
	<li>
		<s:if test="%{#page == STEP_OFFERTA}">
			<div class="current-step">
		</s:if>
				<wp:i18n key="LABEL_GARETEL_STEP_OFFERTA" />
		<s:if test="%{#page == STEP_OFFERTA}">
				<span class="noscreen">(<wp:i18n key="LABEL_WIZARD_CURRENT_STEP" />)</span>
			</div>
		</s:if>
	</li>
	
	<li>
		<s:if test="%{#page == STEP_SCARICA_OFFERTA}">
			<div class="current-step">
		</s:if>
				<wp:i18n key="LABEL_GARETEL_STEP_SCARICA_OFFERTA" />
		<s:if test="%{#page == STEP_SCARICA_OFFERTA}">
				<span class="noscreen">(<wp:i18n key="LABEL_WIZARD_CURRENT_STEP" />)</span>
			</div>
		</s:if>

	</li>
	<li>
		<s:if test="%{#page == STEP_DOCUMENTI}">
			<div class="current-step">
		</s:if>
				<wp:i18n key="LABEL_DOCUMENTS" />
		<s:if test="%{#page == STEP_DOCUMENTI}">
				<span class="noscreen">(<wp:i18n key="LABEL_WIZARD_CURRENT_STEP" />)</span>
			</div>
		</s:if>
	</li>
</ol>