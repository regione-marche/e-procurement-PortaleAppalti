<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags"%>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>

<%-- 
${ssoLoginUrl}<br/>
${ssoLogoutUrl}<br/>
--%>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp">
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2><wp:i18n key="TITLE_PAGE_DISABILITA_ACCESSO_CON" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_AREA_PERSONALE_DISABILITA_ACCESSO_CON" />
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/field_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_messages.jsp" />
	
	<fieldset>
		<legend><wp:i18n key="TITLE_PAGE_DISABILITA_ACCESSO_CON" /></legend>
		<div class="fieldset-row">	
			<div class="label">
				<label><wp:i18n key="LABEL_LINKED_USER_CF" /></label>
			</div>
			<div class="element">
				<span>${cfAssociato}</span>	
			</div>
		</div>
	</fieldset>
		<div class="azioni">
			<c:set var="urlDisabilita"><wp:info key="systemParam" paramName="applicationBaseURL" />do/FrontEnd/AreaPers/unlinkAccountSSO.action</c:set>
			<form action="<wp:action path="/ExtStr2/do/FrontEnd/AreaPers/unlinkAccountSSO.action" />" method="post">
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
				<input type="submit" value="<wp:i18n key="LABEL_UNLINK_ACCOUNT" />" class="button" title="Disassocia l'utenza di SSO"/>
			</form>
		
 	</div>
</div>