<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>

<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/send_blockUI.jsp" />

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2><wp:i18n key="TITLE_PAGE_CATALOGHI_IMPORTA_VARIAZIONE_PREZZI" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/field_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_WIZ_IMPORT_VARIAZIONE_PREZZI_SCADENZE"/>
	</jsp:include>

	<form action="<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/importVariazionePrezziScadenze.action" />" method="post" enctype="multipart/form-data" id="importForm">
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
		<p>
			<wp:i18n key="LABEL_FILE_PRODOTTI_DA_AGGIORNARE" /> :
			<input type="hidden" name="ext" value="${param.ext}" />
			<input type="file" id="allegato" name="allegato" size="20" aria-label='<wp:i18n key="LABEL_FILE_PRODOTTI_DA_AGGIORNARE" />'/>
		</p>
		<p>
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/internalServlet/fileupload/infoUploadFile.jsp" />
		</p>
		<div class="azioni">
			<input type="hidden" name="ext" value="${param.ext}" />
			<input type="hidden" name="catalogo" value='<s:property value="%{catalogo}"/>' />
			<wp:i18n key="BUTTON_IMPORTA" var="valueImportaButton" />
			<wp:i18n key="TITLE_IMPORTA" var="titleImportaButton" />
			<s:submit value="%{#attr.valueImportaButton}" title="%{#attr.titleImportaButton}" cssClass="button block-ui" method="load"></s:submit>
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_cancel.jsp" />
		</div>
	</form>
</div>