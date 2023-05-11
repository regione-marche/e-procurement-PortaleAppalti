<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>

<p class="noscreen"><wp:i18n key="LABEL_WIZARD_STEPS" />:</p>
<ol class="steps">
	<li>
		<c:choose>
			<c:when test="${(empty sessionScope.page) || (sessionScope.page eq 'fatt-dg') }">
				<div class="current-step">
					<wp:i18n key="LABEL_EORDERS_FATT_STEP_DATI_GENERALI" />
					<span class="noscreen"> (<wp:i18n key="LABEL_WIZARD_CURRENT_STEP" />)</span>
				</div>			
			</c:when>
			<c:otherwise>
				<wp:i18n key="LABEL_EORDERS_FATT_STEP_DATI_GENERALI" />
			</c:otherwise>
		</c:choose>
	</li>
	<li>
		<c:choose>
			<c:when test="${sessionScope.page eq 'fatt-dl'}">
				<div class="current-step">
					<wp:i18n key="LABEL_EORDERS_FATT_STEP_DATI_LINEE" />
					<span class="noscreen"> (<wp:i18n key="LABEL_WIZARD_CURRENT_STEP" />)</span>
				</div>			
			</c:when>
			<c:otherwise>
				<wp:i18n key="LABEL_EORDERS_FATT_STEP_DATI_LINEE" />
			</c:otherwise>
		</c:choose>
	</li>
	<li>
		<c:choose>
			<c:when test="${sessionScope.page eq 'fine'}">
				<div class="current-step">
					<wp:i18n key="LABEL_EDIT_DATI_OE_STEP_RIEPILOGO" />
					<span class="noscreen"> (<wp:i18n key="LABEL_WIZARD_CURRENT_STEP" />)</span>
				</div>			
			</c:when>
			<c:otherwise>
				<wp:i18n key="LABEL_EDIT_DATI_OE_STEP_RIEPILOGO" />
			</c:otherwise>
		</c:choose>
	</li>
</ol>