<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>

<!-- OBSOLETO <c:set var="offtec" value="${sessionScope.offertaTecnica}" /> -->
<c:set var="offtec" scope="request" value="${sessionScope['dettaglioOffertaGara'].bustaTecnica.helper}"/>
<c:set var="page" value="${sessionScope.page}"/>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2><wp:i18n key='TITLE_PAGE_GARETEL_ANNULLA_INSERIMENTO'/></h2>

	<p><wp:i18n key='LABEL_GARETEL_CANCEL_INSERIMENTO_TEC_1'/></p>
	<p>
	   <%-- NB: utilizza lo stesso messaggio dell'offerta economica --%>
	   <wp:i18n key='LABEL_GARETEL_CANCEL_INSERIMENTO_ECO_2'/>
	</p>
	
	<div class="azioni">
		<form action="<wp:action path="/ExtStr2/do/FrontEnd/GareTel/cancelOffTec.action" />" method="post" class="azione">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			
			<div>
				<input type="hidden" name="codice" value="${offtec.gara.codice}" />
				<input type="hidden" name="operazione" value="${inviaOfferta}"/>
				<input type="hidden" name="progressivoOfferta" value="${offtec.progressivoOfferta}" />
				<input type="hidden" name="tipoBusta" value="2"/>
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_confirm_cancel.jsp" />
			</div>
		</form>
		
		<c:choose>
			<c:when test="${page eq 'offerta'}">
				<c:set var="href" value="/ExtStr2/do/FrontEnd/GareTel/openPageOffTecOfferta.action" />
			</c:when>
			<c:when test="${page eq 'scaricaOfferta'}">
				<c:set var="href" value="/ExtStr2/do/FrontEnd/GareTel/openPageOffTecScaricaOfferta.action" />
			</c:when>
			<c:when test="${page eq 'documenti'}">
				<c:set var="href" value="/ExtStr2/do/FrontEnd/GareTel/openPageOffTecDocumenti.action" />
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