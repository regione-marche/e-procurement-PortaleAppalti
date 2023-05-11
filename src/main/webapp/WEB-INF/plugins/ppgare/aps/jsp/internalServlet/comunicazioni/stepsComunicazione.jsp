<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>

<s:set var="sessionId">nuovaComunicazione</s:set>

<p class="noscreen"><wp:i18n key="LABEL_WIZARD_STEPS" />:</p>
<ol class="steps">
	<s:if test="%{#session[#sessionId].stepNavigazione.contains(STEP_TESTO_COMUNICAZIONE)}">
		<li>
			<s:if test="%{#session['page'] == STEP_TESTO_COMUNICAZIONE}">
				<div class="current-step">
			</s:if>
					<wp:i18n key="LABEL_COMUNICAZIONI_STEP_TESTO_COMUNICAZIONE" />
			<s:if test="%{#session['page'] == STEP_TESTO_COMUNICAZIONE}">
					<span class="noscreen">(<wp:i18n key="LABEL_WIZARD_CURRENT_STEP" />)</span>
				</div>
			</s:if>
		</li>
	</s:if>
	
	<li>
		<s:if test="%{#session['page'] == STEP_DOCUMENTI}">
			<div class="current-step">
		</s:if>
				<wp:i18n key="LABEL_COMUNICAZIONI_STEP_ALLEGATI" />
		<s:if test="%{#session['page'] == STEP_DOCUMENTI}">
				<span class="noscreen">(<wp:i18n key="LABEL_WIZARD_CURRENT_STEP" />)</span>
			</div>
		</s:if>
	</li>
	
	<li>
		<s:if test="%{#session['page'] == STEP_INVIO_COMUNICAZIONE}">
			<div class="current-step">
		</s:if>
				<wp:i18n key="LABEL_COMUNICAZIONI_STEP_RIEPILOGO" />
		<s:if test="%{#session['page'] == STEP_INVIO_COMUNICAZIONE}">
				<span class="noscreen">(<wp:i18n key="LABEL_WIZARD_CURRENT_STEP" />)</span>
			</div>
		</s:if>
	</li>
	
</ol>