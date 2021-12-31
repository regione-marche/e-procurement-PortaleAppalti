<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>

<s:set var="sessionId">offertaTecnica</s:set>

<p class="noscreen"><wp:i18n key="LABEL_WIZARD_STEPS" />:</p>
<ol class="steps">

	<li>
		<s:if test="%{#session['page'] == STEP_OFFERTA}">
			<div class="current-step">
		</s:if>
				<wp:i18n key="LABEL_GARETEL_STEP_CRITERI_VALUTAZIONE" />
		<s:if test="%{#session['page'] == STEP_OFFERTA}">
				<span class="noscreen">(<wp:i18n key="LABEL_WIZARD_CURRENT_STEP" />)</span>
			</div>
		</s:if>
	</li>
	
	<li>
		<s:if test="%{#session['page'] == STEP_SCARICA_OFFERTA}">
			<div class="current-step">
		</s:if>
				<wp:i18n key="LABEL_GARETEL_STEP_SCARICA_OFFERTA" />
		<s:if test="%{#session['page'] == STEP_SCARICA_OFFERTA}">
				<span class="noscreen">(<wp:i18n key="LABEL_WIZARD_CURRENT_STEP" />)</span>
			</div>
		</s:if>
	</li>
	
	<li>
		<s:if test="%{#session['page'] == STEP_DOCUMENTI}">
			<div class="current-step">
		</s:if>
				<wp:i18n key="LABEL_DOCUMENTS" />
		<s:if test="%{#session['page'] == STEP_DOCUMENTI}">
				<span class="noscreen">(<wp:i18n key="LABEL_WIZARD_CURRENT_STEP" />)</span>
			</div>
		</s:if>
	</li>
	
</ol>