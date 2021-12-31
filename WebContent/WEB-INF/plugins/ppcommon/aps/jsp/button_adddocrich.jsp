<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<c:choose>
	<c:when test="${param.formato==DOCUMENTO_FORMATO_FIRMATO || param.formato==1}">
		<wp:i18n key="BUTTON_ATTACH_FILE_FIRMATO" var="valueAttachButton" />
		<wp:i18n key="TITLE_ATTACH_FILE_FIRMATO" var="titleAttachButton" />
	</c:when>	
		
	<c:when test="${param.formato==DOCUMENTO_FORMATO_PDF || param.formato==2}">
		<wp:i18n key="BUTTON_ATTACH_FILE_PDF" var="valueAttachButton" />
		<wp:i18n key="TITLE_ATTACH_FILE_PDF" var="titleAttachButton" />
	</c:when>
	
	<c:when test="${param.formato==DOCUMENTO_FORMATO_EXCEL || param.formato==4}">
		<wp:i18n key="BUTTON_ATTACH_FILE_EXCEL" var="valueAttachButton" />
		<wp:i18n key="TITLE_ATTACH_FILE_EXCEL" var="titleAttachButton" />
	</c:when>
	
	<c:otherwise>
		<wp:i18n key="BUTTON_ATTACH_FILE" var="valueAddDocRichButton" />
		<wp:i18n key="TITLE_ATTACH_FILE" var="titleAddDocRichButton" />
	</c:otherwise>	
</c:choose>
<s:submit value="%{#attr.valueAddDocRichButton}" title="%{#attr.titleAddDocRichButton}" cssClass="button" method="addDocRich" />
