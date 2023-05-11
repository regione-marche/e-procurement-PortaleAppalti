<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>

<!-- OBSOLETO <c:set var="offeco" value="${sessionScope.offertaEconomica}" /> -->
<c:set var="offeco" value="${sessionScope.dettaglioOffertaGara.bustaEconomica.helper}" />
<c:set var="page" value="${sessionScope.page}" />

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2><wp:i18n key='TITLE_PAGE_GARETEL_ANNULLA_INSERIMENTO'/></h2>

	<p><wp:i18n key='LABEL_GARETEL_CANCEL_INSERIMENTO_ECO_1'/></p>
	<p><wp:i18n key='LABEL_GARETEL_CANCEL_INSERIMENTO_ECO_2'/></p>
	
	<div class="azioni">
		<form action="<wp:action path="/ExtStr2/do/FrontEnd/GareTel/cancelOffTel.action" />" method="post" class="azione">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			
			<div>
				<input type="hidden" name="codice" value="${offeco.gara.codice}" />
				<input type="hidden" name="operazione" value="${inviaOfferta}"/>
				<input type="hidden" name="progressivoOfferta" value="${offeco.progressivoOfferta}" />
				<input type="hidden" name="tipoBusta" value="3"/>
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_confirm_cancel.jsp" />
			</div>
		</form>
		
		<c:choose>
			<c:when test="${page eq 'prezziUnitari'}">
				<c:set var="href" value="/ExtStr2/do/FrontEnd/GareTel/openPageOffTelPrezziUnitari.action" />
			</c:when>
			<c:when test="${page eq 'offerta'}">
				<c:set var="href" value="/ExtStr2/do/FrontEnd/GareTel/openPageOffTelOfferta.action" />
			</c:when>
			<c:when test="${page eq 'scaricaOfferta'}">
				<c:set var="href" value="/ExtStr2/do/FrontEnd/GareTel/openPageOffTelScaricaOfferta.action" />
			</c:when>
			<c:when test="${page eq 'documenti'}">
				<c:set var="href" value="/ExtStr2/do/FrontEnd/GareTel/openPageOffTelDocumenti.action" />
			</c:when>
		</c:choose>
		
		<form action="<wp:action path="${href}" />" method="post" class="azione">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			
			<div>
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_back_to_edit.jsp" />
			</div>
		</form>
	</div>
</div>