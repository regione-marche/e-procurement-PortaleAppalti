<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2><wp:i18n key="TITLE_PAGE_CANCEL_REGISTRAZIONE_OE" /></h2>

	<p><wp:i18n key="LABEL_CANCEL_REGISTRAZIONE_OE_1" /></p>
	<p><wp:i18n key="LABEL_CANCEL_REGISTRAZIONE_OE_2" /></p>

	<div class="azioni">
		<form action="<wp:action path="/ExtStr2/do/FrontEnd/RegistrImpr/cancelRegistrazione.action" />" method="post" class="azione">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			<div>
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_confirm_cancel.jsp" />
			</div>
		</form>
		<c:choose>
			<c:when test="${sessionScope.page eq 'impr'}">
				<c:set var="href" value="/ExtStr2/do/FrontEnd/RegistrImpr/openPageDatiPrincImpresa.action" />
			</c:when>
			<c:when test="${sessionScope.page eq 'indir'}">
				<c:set var="href" value="/ExtStr2/do/FrontEnd/RegistrImpr/openPageIndirizziImpresa.action" />
			</c:when>
			<c:when test="${sessionScope.page eq 'altri-dati'}">
				<c:set var="href" value="/ExtStr2/do/FrontEnd/RegistrImpr/openPageAltriDatiAnagrImpresa.action" />
			</c:when>
			<c:when test="${sessionScope.page eq 'sogg'}">
				<c:set var="href" value="/ExtStr2/do/FrontEnd/RegistrImpr/openPageSoggettiImpresa.action" />
			</c:when>
			<c:when test="${sessionScope.page eq 'impr-ult'}">
				<c:set var="href" value="/ExtStr2/do/FrontEnd/RegistrImpr/openPageDatiUltImpresa.action" />
			</c:when>
			<c:when test="${sessionScope.page eq 'user'}">
				<c:set var="href" value="/ExtStr2/do/FrontEnd/RegistrImpr/openPageUtenza.action" />
			</c:when>
			<c:when test="${sessionScope.page eq 'fine'}">
				<c:set var="href" value="/ExtStr2/do/FrontEnd/RegistrImpr/openPageRiepilogo.action" />
			</c:when>
		</c:choose>
		<form action="<wp:action path="${href}" />" method="post" class="azione">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			<div>
				<wp:i18n key="BUTTON_BACK_TO_REGISTRAZIONE_OE" var="valueBackButton" />
				<s:submit value="%{#attr.valueBackButton}" cssClass="button" />
			</div>
		</form>
	</div>
</div>