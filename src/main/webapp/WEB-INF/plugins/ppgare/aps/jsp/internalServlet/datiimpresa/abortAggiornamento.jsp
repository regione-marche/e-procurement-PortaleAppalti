<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2><wp:i18n key="TITLE_PAGE_ABORT_EDIT_DATI_OE" /></h2>

	<p><wp:i18n key="LABEL_ABORT_EDIT_DATI_OE_1" /></p>
	<p><wp:i18n key="LABEL_ABORT_EDIT_DATI_OE_2" /></p>
	
	<div class="azioni">
		<form action="<wp:action path="/ExtStr2/do/FrontEnd/DatiImpr/cancelAggiornamento.action" />" method="post" class="azione">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			<div>
				<input type="hidden" name="ext" value="${param.ext}" />
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_confirm_cancel.jsp" />
			</div>
		</form>
		<c:choose>
			<c:when test="${sessionScope.page eq 'impr'}">
			<c:set var="href" value="/ExtStr2/do/FrontEnd/DatiImpr/openPageDatiPrincImpresa.action" />
			</c:when>
			<c:when test="${sessionScope.page eq 'indir'}">
			<c:set var="href" value="/ExtStr2/do/FrontEnd/DatiImpr/openPageIndirizziImpresa.action" />
			</c:when>
			<c:when test="${sessionScope.page eq 'altri-dati'}">
			<c:set var="href" value="/ExtStr2/do/FrontEnd/DatiImpr/openPageAltriDatiAnagrImpresa.action" />
			</c:when>
			<c:when test="${sessionScope.page eq 'sogg'}">
			<c:set var="href" value="/ExtStr2/do/FrontEnd/DatiImpr/openPageSoggettiImpresa.action" />
			</c:when>
			<c:when test="${sessionScope.page eq 'impr-ult'}">
			<c:set var="href" value="/ExtStr2/do/FrontEnd/DatiImpr/openPageDatiUltImpresa.action" />
			</c:when>
			<c:when test="${sessionScope.page eq 'fine'}">
			<c:set var="href" value="/ExtStr2/do/FrontEnd/DatiImpr/openPageRiepilogo.action" />
			</c:when>
		</c:choose>
		<form action="<wp:action path="${href}" />" method="post" class="azione">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			<div>
				<input type="hidden" name="ext" value="${param.ext}" />
				<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/datiimpresa/inc/button_edit_datiimpresa.jsp" />
			</div>
		</form>
	</div>
</div>