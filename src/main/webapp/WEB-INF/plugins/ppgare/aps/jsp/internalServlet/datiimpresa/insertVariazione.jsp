<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>

<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/send_blockUI.jsp" />

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2><wp:i18n key="TITLE_PAGE_RICH_VARIAZIONE_DATI_OE" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/field_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_RICH_VARIAZIONE_DATI_IMPR"/>
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />

	<form action="<wp:action path="/ExtStr2/do/FrontEnd/DatiImpr/richVariazione.action" />" method="post">
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
		
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/mandatory_fields_message.jsp" />

		<fieldset>
			<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_RICH_VARIAZIONE" /></legend>
	
			<div class="fieldset-row first-row last-row">
				<div class="label">
					<label for="descrizione"><wp:i18n key="LABEL_RICH_VARIAZIONE_DESCRIZIONE" /> (<wp:i18n key="LABEL_RICH_VARIAZIONE_DESCRIZIONE_LUNGHEZZA" />): <span class="required-field">*</span></label>
				</div>
				<div class="element">
					<s:textarea name="descrizione" id="descrizione" value="%{descrizione}" rows="5" cols="68" aria-required="true" />
				</div>
			</div>
		</fieldset>
	
		<div class="azioni">
			<s:if test="!sendBlocked">
				<wp:i18n key="BUTTON_RICH_VARIAZIONE_INVIA" var="valueInviaVariazioneButton" />
				<wp:i18n key="TITLE_RICH_VARIAZIONE_INVIA" var="titleInviaVariazioneButton" />
				<s:submit value="%{#attr.valueInviaVariazioneButton}" title="%{#attr.titleInviaVariazioneButton}" cssClass="button block-ui" method="send"></s:submit>
			</s:if>
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_cancel.jsp" />
		</div>
	</form>
</div>