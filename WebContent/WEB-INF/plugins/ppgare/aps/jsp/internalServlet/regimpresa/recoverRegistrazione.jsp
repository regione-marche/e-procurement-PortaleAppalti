<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2><wp:i18n key="TITLE_PAGE_RECOVER_REGISTRAZIONE_OE" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/field_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_messages.jsp" />

	<p><wp:i18n key="LABEL_RECOVER_REGISTRAZIONE_OE" /></p>
	
	<div class="azioni">
		<wp:i18n key="BUTTON_CONFIRM" var="valueConfirmButton" />
		<wp:i18n key="BUTTON_WIZARD_CANCEL" var="valueAnnullaButton" />
		
		<form action="<wp:action path="/ExtStr2/do/FrontEnd/RegistrImpr/confirmRecoverRegistrazione.action" />" method="post" class="azione">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			
			<div>
				<s:submit value="%{#attr.valueConfirmButton}" cssClass="button block-ui" />
				<input type="hidden" name="codiceFiscale" value="${codiceFiscale}" />
				<input type="hidden" name="partitaIVA" value="${partitaIVA}" />
				<input type="hidden" name="emailRecapito" value="${emailRecapito}" />
				<input type="hidden" name="emailPECRecapito" value="${emailPECRecapito}" />
			</div>
		</form>
		<form action="<wp:action path="/ExtStr2/do/FrontEnd/RegistrImpr/cancelRecoverRegistrazione.action" />" method="post" class="azione">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			
			<div>
				<s:submit value="%{#attr.valueAnnullaButton}" cssClass="button" />
			</div>
		</form>
	</div>
</div>