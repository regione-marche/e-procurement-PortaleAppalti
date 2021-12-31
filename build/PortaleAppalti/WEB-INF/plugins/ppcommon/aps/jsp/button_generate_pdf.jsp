<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<!--
	Parametri utilizzabili per questo pulsante:
	 	tipo	(opzionale) tipologia di busta pr la quale generare il pdf ('TEC'|'ECO')
	 	onclick (opzionale) evento da agganciare all'onclick del pulsante
 -->
 
<wp:i18n key="BUTTON_WIZARD_GENERA_PDF" var="valueGenPdfButton" />
<wp:i18n key="TITLE_WIZARD_GENERA_PDF" var="titleGenPdfButton" />

<c:if test="${param.tipo=='TEC'}">
	<wp:i18n key="BUTTON_WIZARD_GENERA_PDF_VALUTAZIONE_TECNICA" var="valueGenPdfButton" />
	<wp:i18n key="TITLE_WIZARD_GENERA_PDF_VALUTAZIONE_TECNICA" var="titleGenPdfButton" />
</c:if>

<c:if test="${param.tipo=='ECO'}">
	<wp:i18n key="BUTTON_WIZARD_GENERA_PDF_OFFERTA_ECONOMICA" var="valueGenPdfButton" />
	<wp:i18n key="TITLE_WIZARD_GENERA_PDF_OFFERTA_ECONOMICA" var="titleGenPdfButton" />	 
</c:if>

<input type="submit" id="createPdf" class="button" value="${valueGenPdfButton}" title="${titleGenPdfButton}"
	<c:if test="${! empty param.onclick}"> onclick="${param.onclick}" </c:if> 
/>