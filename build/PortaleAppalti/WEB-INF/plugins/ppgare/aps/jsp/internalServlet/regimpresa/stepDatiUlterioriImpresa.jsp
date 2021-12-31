<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2><wp:i18n key="TITLE_PAGE_REGISTRA_OE" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/field_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />

	<jsp:include page="stepsRegistrazione.jsp" />

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_REG_IMPRESA_DATI_ULT_IMPRESA"/>
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/skip_form_buttons.jsp" />

	<form action="<wp:action path="/ExtStr2/do/FrontEnd/RegistrImpr/processPageDatiUltImpresa.action" />" method="post">
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />

		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/mandatory_fields_message.jsp" />

		<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/datiimpresa/datiUlterioriImpresaSection.jsp">
			<jsp:param name="noEdit" value="false"/>
			<jsp:param name="registrazione" value="true"/>
		</jsp:include>
	
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/skip_main_content_from_buttons.jsp" />

		<div class="azioni">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_previous.jsp" />
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_next.jsp" />
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_cancel.jsp" />
		</div>
	</form>
</div>