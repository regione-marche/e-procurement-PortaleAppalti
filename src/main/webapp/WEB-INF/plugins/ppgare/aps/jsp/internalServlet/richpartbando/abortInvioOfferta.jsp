<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2><wp:i18n key='TITLE_PAGE_RICHPART_ANNULLA_INVIO_OFFERTA'/></h2>

	<p><wp:i18n key='LABEL_RICHPART_ANNULLA_INVIO_OFFERTA_1'/></p>
	<p><wp:i18n key='LABEL_RICHPART_ANNULLA_INVIO_OFFERTA_2'/></p>

	<div class="azioni">
		<form action="<wp:action path="/ExtStr2/do/FrontEnd/RichPartBando/cancelPartecipazione.action" />" method="post" class="azione">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			
			<s:hidden name="codice" value="%{codice}" />
			<s:hidden name="operazione" value="%{operazione}" />
			<s:hidden name="progressivoOfferta" value="%{progressivoOfferta}" />
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_confirm_cancel.jsp" />
		</form>
		
		<c:choose>
			<c:when test="${sessionScope.page eq 'impresa'}">
				<c:set var="href" value="/ExtStr2/do/FrontEnd/RichPartBando/openPageImpresa.action" />
			</c:when>
			<c:when test="${sessionScope.page eq 'rti'}">
				<c:set var="href" value="/ExtStr2/do/FrontEnd/RichPartBando/openPageRTI.action" />
			</c:when>
			<c:when test="${sessionScope.page eq 'componenti'}">
				<c:set var="href" value="/ExtStr2/do/FrontEnd/RichPartBando/openPageComponentiClear.action" />
			</c:when>
			<c:when test="${sessionScope.page eq 'avvalimento'}">
				<c:set var="href" value="/ExtStr2/do/FrontEnd/RichPartBando/openPageAvvalimento.action" />
			</c:when>
			<c:when test="${sessionScope.page eq 'lotti'}">
				<c:set var="href" value="/ExtStr2/do/FrontEnd/RichPartBando/openPageLotti.action" />
			</c:when>
			<c:when test="${sessionScope.page eq 'fine'}">
				<c:set var="href" value="/ExtStr2/do/FrontEnd/RichPartBando/openPageRiepilogo.action" />
			</c:when>
		</c:choose>
		
		<form action="<wp:action path="${href}" />" method="post" class="azione">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			
			<div>
				<wp:i18n key='BUTTON_WIZARD_BACK_TO_INVIO_OFFERTA' var='valueButtonBackTo' />
				<s:submit value="%{#attr.valueButtonBackTo}" cssClass="button" />
			</div>
		</form>
	</div>
</div>