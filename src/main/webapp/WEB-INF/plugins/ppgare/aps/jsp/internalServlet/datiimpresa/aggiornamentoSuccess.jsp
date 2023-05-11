<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

<h2><wp:i18n key="TITLE_PAGE_EDIT_DATI_OE" /></h2>

	<p><wp:i18n key="LABEL_EDIT_DATI_OE_SUCCESS_1" /></p>
	<p><wp:i18n key="LABEL_EDIT_DATI_OE_SUCCESS_2" /></p>

	<div class="azioni">
		<form action="<wp:action path="/ExtStr2/do/FrontEnd/DatiImpr/backAfterAggiornamento.action" />" method="post" class="azione">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			<div>
				<input type="hidden" name="ext" value="${param.ext}" />
				<wp:i18n key="BUTTON_WIZARD_NEXT" var="valueNextButton" />
				<wp:i18n key="TITLE_EDIT_DATI_OE_SUCCESS_BUTTON_NEXT" var="titleNextButton" />
				<s:submit value="%{#attr.valueNextButton}" title="%{#attr.titleNextButton}" cssClass="button" />
			</div>
		</form>
	</div>
</div>