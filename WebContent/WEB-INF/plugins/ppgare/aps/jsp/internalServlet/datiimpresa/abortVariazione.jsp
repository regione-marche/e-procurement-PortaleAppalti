<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2><wp:i18n key="TITLE_PAGE_ABORT_RICH_VARIAZIONE_DATI_OE" /></h2>

	<p><wp:i18n key="LABEL_ABORT_RICH_VARIAZIONE_DATI_OE_1" /></p>
	<p><wp:i18n key="LABEL_ABORT_RICH_VARIAZIONE_DATI_OE_2" /></p>

	<div class="azioni">
		<form action="<wp:url page="ppgare_impr_visdati"/>" method="post" class="azione">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			<div>
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_confirm_cancel.jsp" />
			</div>
		</form>
		<form action="<wp:action path="/ExtStr2/do/FrontEnd/DatiImpr/reloadVariazione.action" />" method="post" class="azione">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			<div>
				<input type="hidden" name="descrizione" value="<s:property value="descrizione" />"/>
				<wp:i18n key="BUTTON_ABORT_RICH_VARIAZIONE_BACK" var="valueBackButton" />
				<s:submit value="%{#attr.valueBackButton}" cssClass="button" />
			</div>
		</form>
	</div>
</div>