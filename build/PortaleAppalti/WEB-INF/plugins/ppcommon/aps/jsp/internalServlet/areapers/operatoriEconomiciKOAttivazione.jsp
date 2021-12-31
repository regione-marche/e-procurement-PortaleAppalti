<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags"%>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp">
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2><wp:i18n key="TITLE_PAGE_OP_KO_RIATTIVA" /></h2>
	
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_AREA_PERSONALE_ADMIN_OP_ECON_RIATTIVA" />
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/field_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
	
	<form action="<wp:action path="/ExtStr2/do/FrontEnd/AreaPers/sendMailAttivaOperatoreEconomico.action&amp;last=1" />" method="post">

		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/mandatory_fields_message.jsp" />

		<fieldset>
			<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_OP_KO_RIATTIVA_DESTINATARIO" /></legend>
	
			<div class="fieldset-row first-row last-row">
				<div class="label">
					<label for="emailAttivazione"><wp:i18n key="LABEL_EMAIL_ATTIVAZIONE" />: <span class="required-field">*</span></label>
				</div>
				<div class="element">
					<s:textfield id="emailAttivazione" name="emailAttivazione" value="%{emailAttivazione}" size="60" maxLength="60" aria-required="true"/>
				</div>
			</div>
		</fieldset>
		<div class="azioni">
			<s:hidden id="username" name="username" value="%{username}"/>
			<wp:i18n key="BUTTON_SEND" var="valueSendButton" />
			<s:submit value="%{#attr.valueSendButton}" cssClass="button"/>
		</div>
	</form>
	
	<div class="back-link">
		<a href='<wp:action path="/ExtStr2/do/FrontEnd/AreaPers/searchOperatoriEconomiciKO.action" />&amp;last=1'><wp:i18n key="LINK_BACK_TO_LIST" /></a>		
	</div> 
	
</div>

