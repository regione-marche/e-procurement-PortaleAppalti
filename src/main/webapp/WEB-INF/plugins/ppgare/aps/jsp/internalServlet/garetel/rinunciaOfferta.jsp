<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<c:set var="href" value="/ExtStr2/do/FrontEnd/GareTel/rinunciaOfferta.action"/>


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2><wp:i18n key='TITLE_PAGE_GARETEL_RINUNCIA_OFFERTA'/></h2>
	
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/field_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
	
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_RINUNCIA_OFFERTA"/>
	</jsp:include>
 	
	<form action="<wp:action path="${href}" />" method="post" >
		<fieldset>
			<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_GARETEL_RINUNCIA_OFFERTA" /></legend>
			
			<div class="fieldset-row">
				<div class="label">
					<label for="motivazione"><wp:i18n key="LABEL_MOTIVAZIONE_RINUNCIA" /> : <span class="required-field">*</span>
					</label>
				</div>
				<div class="element">
					<s:textarea name="motivazione" id="motivazione" value="%{motivazione}" rows="5" cols="68" maxlength="2000" aria-required="true" />
				</div>
			</div>
		</fieldset>
		
		<div class="azioni">
			<wp:i18n key="BUTTON_DETTAGLIO_GARA_RINUNCIA_OFFERTA" var="valueRinunciaButton" />
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			<input type="hidden" name="codice" value="${codice}"/>
			<s:submit value="%{#attr.valueRinunciaButton}" cssClass="button" />
		</div>
	</form>
	
	<div class="back-link">
		<a href="<wp:action path="/ExtStr2/do/FrontEnd/Bandi/view.action" />&amp;codice=${codice}&amp;ext=${param.ext}">
			<wp:i18n key="LINK_BACK_TO_PROCEDURE" />
		</a>
	</div>
</div>