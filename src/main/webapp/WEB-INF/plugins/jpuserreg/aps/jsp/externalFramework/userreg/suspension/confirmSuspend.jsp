<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>
<%@ taglib prefix="wpsa" uri="apsadmin-core.tld" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<h2><wp:i18n key="jpuserreg_SUSPENDING_CONFIRM_MSG"/></h2>

<p>You will be logged out and redirect to home page</p>

<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/field_errors.jsp" />
<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />

<form method="post" action="<wp:url />">
	<p>
		<wpsf:hidden name="actionPath" value="/ExtStr2/do/jpuserreg/UserReg/suspend.action" />
	</p>
	<p>
		<label for="password"><wp:i18n key="jpuserreg_PASSWORD"/> <abbr title="(<wp:i18n key="jpuserreg_REQUIRED"/>)">*</abbr></label><br />
		<wpsf:password name="password" required="true" id="password" maxlength="20" />
	</p>
	<div class="azioni">
		<input type="submit" value="<wp:i18n key="jpuserreg_SUSPEND"/>" />
	</div>
</form>