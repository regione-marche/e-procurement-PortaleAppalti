<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags"%>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp">
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2><wp:i18n key="TITLE_PAGE_SBLOCCO_UTENZA_AUTONOMO" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_AREA_PERSONALE_SBLOCCO_UTENZA_AUTONOMO" />
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/field_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_messages.jsp" />
	
	<form action="<wp:action path="/ExtStr2/do/FrontEnd/AreaPers/sbloccoUtenzaAutonomo.action" />" method="post">

		<fieldset>
			<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_SBLOCCA_ACCOUNT" /></legend>

			<div class="fieldset-row first-row">
				<div class="label">
					<label for="username"><wp:i18n key="USERNAME" />: </label>
				</div>
				<div class="element">
					${username}					
				</div>
			</div>	
			<div class="azioni">
				<wp:i18n key="BUTTON_RIPRISTINA" var="valueButtonRipristina" />
				<s:submit value="%{#attr.valueButtonRipristina}" cssClass="button"/>
				<%-- 
				<input type="hidden" id="username" name="username" value="${username}" />
				--%>
			</div>
		</fieldset>
	</form>
	
</div>