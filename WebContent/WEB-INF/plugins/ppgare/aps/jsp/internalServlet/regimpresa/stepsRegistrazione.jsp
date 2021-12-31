<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>

<es:checkCustomization var="showIndirizzi" objectId="IMPRESA-INDIRIZZI" attribute="STEP" feature="VIS" />
<es:checkCustomization var="showDatiUlteriori" objectId="IMPRESA-DATIULT" attribute="STEP" feature="VIS" />


<p class="noscreen"><wp:i18n key="LABEL_WIZARD_STEPS" />:</p>
<ol class="steps">
	<li>
		<c:if test="${sessionScope.page eq 'impr'}">
			<div class="current-step">
		</c:if>
				<wp:i18n key="LABEL_EDIT_DATI_OE_STEP_DATI_PRINCIPALI" />
		<c:if test="${sessionScope.page eq 'impr'}">
				<span class="noscreen"> (<wp:i18n key="LABEL_WIZARD_CURRENT_STEP" />)</span>
			</div>
		</c:if>
	</li>
	<input type="hidden" name="showIndirizzi" value="${showIndirizzi}" />
	<c:if test="${showIndirizzi}">
		<li>
			<c:if test="${sessionScope.page eq 'indir'}">
				<div class="current-step">
			</c:if>
					<wp:i18n key="LABEL_EDIT_DATI_OE_STEP_ALTRI_INDIRIZZI" />
			<c:if test="${sessionScope.page eq 'indir'}">
					<span class="noscreen"> (<wp:i18n key="LABEL_WIZARD_CURRENT_STEP" />)</span>
				</div>
			</c:if>
		</li>
	</c:if>
	<li>
		<c:if test="${sessionScope.page eq 'sogg' or sessionScope.page eq 'altri-dati'}">
			<div class="current-step">
		</c:if>
				<wp:i18n key="LABEL_EDIT_DATI_OE_STEP_ALTRI_DATI_ANAGRAFICI" />
		<c:if test="${sessionScope.page eq 'sogg' or sessionScope.page eq 'altri-dati'}">
				<span class="noscreen"> (<wp:i18n key="LABEL_WIZARD_CURRENT_STEP" />)</span>
			</div>
		</c:if>
	</li>
	<input type="hidden" name="showDatiUlteriori" value="${showDatiUlteriori}" />
	<c:if test="${showDatiUlteriori}">
		<li>
			<c:if test="${sessionScope.page eq 'impr-ult'}">
				<div class="current-step">
			</c:if>
					<wp:i18n key="LABEL_EDIT_DATI_OE_STEP_DATI_ULTERIORI" />
			<c:if test="${sessionScope.page eq 'impr-ult'}">
					<span class="noscreen"> (<wp:i18n key="LABEL_WIZARD_CURRENT_STEP" />)</span>
				</div>
			</c:if>
		</li>
	</c:if>
	<li>
		<c:if test="${sessionScope.page eq 'user'}">
			<div class="current-step">
		</c:if>
				<wp:i18n key="LABEL_REGISTRA_OE_STEP_UTENZA" />
		<c:if test="${sessionScope.page eq 'user'}">
				<span class="noscreen"> (<wp:i18n key="LABEL_WIZARD_CURRENT_STEP" />)</span>
			</div>
		</c:if>
	</li>
	<li>
		<c:if test="${sessionScope.page eq 'fine'}">
			<div class="current-step">
		</c:if>
				<wp:i18n key="LABEL_EDIT_DATI_OE_STEP_RIEPILOGO" />
		<c:if test="${sessionScope.page eq 'fine'}">
				<span class="noscreen"> (<wp:i18n key="LABEL_WIZARD_CURRENT_STEP" />)</span>
			</div>
		</c:if>
	</li>
</ol>