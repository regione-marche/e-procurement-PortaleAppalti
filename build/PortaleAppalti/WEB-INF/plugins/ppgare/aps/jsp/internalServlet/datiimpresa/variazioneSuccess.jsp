<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2><wp:i18n key="TITLE_PAGE_RICH_VARIAZIONE_DATI_OE" /></h2>

	<p><wp:i18n key="LABEL_RICH_VARIAZIONE_SUCCESS_1" /></p>
	<p><wp:i18n key="LABEL_RICH_VARIAZIONE_SUCCESS_2" /></p>
	<p><wp:i18n key="LABEL_RICH_VARIAZIONE_SUCCESS_3" /></p>
	<p><wp:i18n key="LABEL_GREETINGS" /></p>

	<div class="azioni">
		<form action="<wp:url page="ppgare_impr_visdati"/>" method="post" class="azione">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			<div>
				<wp:i18n key="BUTTON_WIZARD_NEXT" var="valueNextButton" />
				<wp:i18n key="TITLE_RICH_VARIAZIONE_SUCCESS_BUTTON_NEXT" var="titleNextButton" />
				<s:submit value="%{#attr.valueNextButton}" title="%{#attr.titleNextButton}" cssClass="button" />
			</div>
		</form>
	</div>
</div>