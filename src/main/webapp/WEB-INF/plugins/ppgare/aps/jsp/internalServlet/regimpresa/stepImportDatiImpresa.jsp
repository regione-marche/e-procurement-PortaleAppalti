<%@ taglib prefix="wp" uri="aps-core.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<c:set var="info" value=""/>
<c:set var="balloon" value=""/>
<c:choose>
	<c:when test="${tipoImport eq 'PORTALE'}">
		<c:set var="info" value="LABEL_IMPORT_OE_INFO_MXML"/>
		<c:set var="balloon" value="BALLOON_REG_IMPRESA_FROM_PORTALE"/>
	</c:when>
	<c:when test="${tipoImport eq 'DGUE'}">
		<c:set var="info" value="LABEL_IMPORT_OE_INFO_DGUE_XML"/>
		<c:set var="balloon" value="BALLOON_REG_IMPRESA_FROM_DGUE"/>
	</c:when>
	<c:when test="${tipoImport eq 'MICHELANGELO'}">
		<c:set var="info" value="LABEL_IMPORT_OE_INFO_MICHELANGELO"/>
		<c:set var="balloon" value="BALLOON_REG_IMPRESA_SEARCH_CODICE_FISCALE"/>
	</c:when>
</c:choose>


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp">
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">
	<h2><wp:i18n key="TITLE_PAGE_IMPORT_OE" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/field_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="${balloon}" />
	</jsp:include>
 	
	<form
		action="<wp:action path="/ExtStr2/do/FrontEnd/RegistrImpr/processPageImportImpresa.action" />"
		enctype="multipart/form-data"  method="post">
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
		
		<p class="instructions">
			<wp:i18n key="${info}" />
		</p>
		
		<c:choose>
			<c:when test="${tipoImport eq 'MICHELANGELO'}">					
				<p>
					<span class="noscreen"><label for="codiceFiscale"><wp:i18n key="LABEL_CODICE_FISCALE" /></label> : </span>					 
					<s:textfield name="codiceFiscale" id="codiceFiscale" value="%{codiceFiscale}" maxlength="16" size="20" aria-required="true" />
				</p>
			</c:when>
			<c:otherwise>
				<p>
					<span class="noscreen"><label for="xmlImport"><wp:i18n key="LABEL_FILE" /></label> : </span>
					<input type="file" name="xmlImport" id="xmlImport" size="40" cssClass="block-ui" accept=".xml" />&nbsp;
				</p>		
			</c:otherwise>
		</c:choose>
		
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/skip_main_content_from_buttons.jsp" />
		<div class="azioni">
			<input type="hidden" name="tipoImport" value="<s:property value="%{tipoImport}"/>" />			
			<c:choose>
				<c:when test="${tipoImport eq 'MICHELANGELO'}">					
					<wp:i18n key="BUTTON_IMPORT_OE_IMPORTA" var="valueImportButton" />
					<wp:i18n key="TITLE_IMPORT_OE_IMPORTA" var="titleImportButton" />					
					<s:submit value="%{#attr.valueImportButton}" title="%{#attr.titleImportButton}" cssClass="button" method="importMichelangelo"></s:submit>
				</c:when>
				<c:otherwise>
					<wp:i18n key="BUTTON_IMPORT_OE_IMPORTA" var="valueImportButton" />
					<wp:i18n key="TITLE_IMPORT_OE_IMPORTA" var="titleImportButton" />
					<s:submit value="%{#attr.valueImportButton}" title="%{#attr.titleImportButton}" cssClass="button" method="importFile"></s:submit>
				</c:otherwise>
			</c:choose>			
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_cancel.jsp" />
		</div>
	</form>
</div>


