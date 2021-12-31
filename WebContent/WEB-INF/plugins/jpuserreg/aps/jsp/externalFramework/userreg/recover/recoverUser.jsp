<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_jpuserreg_REACTIVATION_PASSWORD_LOST_MSG"/>
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/field_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
	
	<h2><wp:i18n key="jpuserreg_REACTIVATION_PASSWORD_LOST_MSG"/></h2>
	
	<form method="post" action="<wp:action path="/ExtStr2/do/jpuserreg/UserReg/recoverFromUsername.action"/>">
		<p>
			<label for="usernameRecover"><wp:i18n key="jpuserreg_USERNAME"/></label>
			<br/>
			<s:textfield name="username" id="usernameRecover" maxlength="20" />
		</p>
		<div class="azioni">
			<input type="submit" value="<wp:i18n key="jpuserreg_SEND"/>" class="button" 
					aria-label="<wp:i18n key="jpuserreg_REACTIVATION_PASSWORD_LOST_MSG"/>" />
		</div>
	</form>
	<br/>
	<br/>
	<h2><wp:i18n key="jpuserreg_REACTIVATION_USERNAME_LOST_MSG"/></h2>

	<form method="post" action="<wp:action path="/ExtStr2/do/jpuserreg/UserReg/recoverFromEmail.action" />">
		<p>
			<label for="email"><wp:i18n key="jpuserreg_EMAIL"/></label>
			<br/>
			<s:textfield name="email" id="email" />
		</p>
		<div class="azioni">
			<input type="submit" value="<wp:i18n key="jpuserreg_SEND"/>" class="button" 
					aria-label="<wp:i18n key="jpuserreg_REACTIVATION_USERNAME_LOST_MSG"/>" />
		</div>
	</form>
</div>