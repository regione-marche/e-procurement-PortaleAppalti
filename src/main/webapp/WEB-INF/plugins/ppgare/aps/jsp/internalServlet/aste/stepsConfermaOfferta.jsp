<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>

<%--
<c:set var="rti" value="${sessionScope.dettaglioAsta.rti}" />
--%>
 
<p class="noscreen"><wp:i18n key="LABEL_WIZARD_STEPS" />:</p>
<ol class="steps">
	<li>
		<c:if test="${sessionScope.page eq 'datioperatore'}">
			<div class="current-step">
		</c:if>
				<wp:i18n key="LABEL_DATI_ANAGRAFICI" />
		<c:if test="${sessionScope.page eq 'datioperatore'}">
				<span class="noscreen"> (<wp:i18n key="LABEL_WIZARD_CURRENT_STEP" />)</span>
			</div>
		</c:if>
	</li>
	<li>
		<c:if test="${sessionScope.page eq 'datiofferta'}">
			<div class="current-step">
		</c:if>
				<wp:i18n key="LABEL_DATI_RIEPILOGATIVI_ULTIMA_OFFERTA" />
		<c:if test="${sessionScope.page eq 'datiofferta'}">
				<span class="noscreen"> (<wp:i18n key="LABEL_WIZARD_CURRENT_STEP" />)</span>
			</div>
		</c:if>
	</li>	
	<li>
		<c:if test="${sessionScope.page eq 'firmatari'}">
			<div class="current-step">
		</c:if>				
				<wp:i18n key="LABEL_GARETEL_STEP_SCARICA_OFFERTA" />
		<c:if test="${sessionScope.page eq 'firmatari'}">
				<span class="noscreen"> (<wp:i18n key="LABEL_WIZARD_CURRENT_STEP" />)</span>
			</div>
		</c:if>
	</li>	
<%-- 	<c:if test='${sessionScope.dettOffertaAsta.stepNavigazioneAbilitato["generapdf"]}' >	 --%>
<!-- 		<li> -->
<%-- 			<c:if test="${sessionScope.page eq 'generapdf'}"> --%>
<!-- 				<div class="current-step"> -->
<%-- 			</c:if>				 --%>
<!-- 					Generazione pdf -->
<%-- 			<c:if test="${sessionScope.page eq 'generapdf'}"> --%>
<!-- 					<span class="noscreen"> (<wp:i18n key="LABEL_WIZARD_CURRENT_STEP" />)</span> -->
<!-- 				</div> -->
<%-- 			</c:if> --%>
<!-- 		</li> -->
<%-- 	</c:if> --%>
	<c:if test='${sessionScope.dettOffertaAsta.stepNavigazioneAbilitato["uploadpdf"]}' >		
		<li>
			<c:if test="${sessionScope.page eq 'uploadpdf'}">
				<div class="current-step">
			</c:if>
					<wp:i18n key="LABEL_DOCUMENTS" />
			<c:if test="${sessionScope.page eq 'uploadpdf'}">
					<span class="noscreen"> (<wp:i18n key="LABEL_WIZARD_CURRENT_STEP" />)</span>
				</div>
			</c:if>
		</li>
	</c:if>	
	<li>
		<c:if test="${sessionScope.page eq 'riepilogo'}">
			<div class="current-step">
		</c:if>
				<wp:i18n key="LABEL_RIEPILOGO" />
		<c:if test="${sessionScope.page eq 'riepilogo'}">
				<span class="noscreen"> (<wp:i18n key="LABEL_WIZARD_CURRENT_STEP" />)</span>
			</div>
		</c:if>
	</li>
</ol>
