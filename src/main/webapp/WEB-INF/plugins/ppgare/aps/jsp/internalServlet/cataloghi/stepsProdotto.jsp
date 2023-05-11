<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>

<p class="noscreen"><wp:i18n key="LABEL_WIZARD_STEPS" />:</p>
<ol class="steps">
	<c:if test="${!sessionScope.dettProdotto.aggiornamento}">
		<li>
			<c:if test="${sessionScope.page eq 'articolo'}">
				<div class="current-step">
			</c:if>
					<wp:i18n key="LABEL_CATALOGHI_STEP_SELEZIONE_ARTICOLO" />
			<c:if test="${sessionScope.page eq 'articolo'}">
					<span class="noscreen">(<wp:i18n key="LABEL_WIZARD_CURRENT_STEP" />)</span>
				</div>
			</c:if>
		</li>
	</c:if>
	<li>
		<c:if test="${sessionScope.page eq 'prodotto'}">
			<div class="current-step">
		</c:if>
				<wp:i18n key="LABEL_CATALOGHI_STEP_DEFINIZIONE_PRODOTTO" />
		<c:if test="${sessionScope.page eq 'prodotto'}">
				<span class="noscreen">(<wp:i18n key="LABEL_WIZARD_CURRENT_STEP" />)</span>
			</div>
		</c:if>
	</li>
	<li>
		<c:if test="${sessionScope.page eq 'doc'}">
			<div class="current-step">
		</c:if>
				<wp:i18n key="LABEL_CATALOGHI_STEP_ALLEGATI" />
		<c:if test="${sessionScope.page eq 'doc'}">
				<span class="noscreen">(<wp:i18n key="LABEL_WIZARD_CURRENT_STEP" />)</span>
			</div>
		</c:if>
	</li>
	<li>
		<c:if test="${sessionScope.page eq 'fine'}">
			<div class="current-step">
		</c:if>
				<wp:i18n key="LABEL_CATALOGHI_STEP_RIEPILOGO" />
		<c:if test="${sessionScope.page eq 'fine'}">
				<span class="noscreen">(<wp:i18n key="LABEL_WIZARD_CURRENT_STEP" />)</span>
			</div>
		</c:if>
	</li>
</ol>