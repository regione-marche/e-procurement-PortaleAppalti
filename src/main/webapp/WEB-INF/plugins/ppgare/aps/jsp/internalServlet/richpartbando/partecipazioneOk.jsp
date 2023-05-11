<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags"%>

<s:set var="partecipazione" value="%{#session.dettaglioOffertaGara.bustaPartecipazione.helper}"/>

<s:if test="%{#partecipazione.tipoEvento == 1}">
	<c:set var="titolo"><wp:i18n key='TITLE_PAGE_RICHPART_PRESENTA_PARTECIPAZIONE'/></c:set>
</s:if>
<s:elseif test="%{#partecipazione.tipoEvento == 2}">
	<c:set var="titolo"><wp:i18n key='TITLE_PAGE_RICHPART_PRESENTA_OFFERTA'/></c:set>
</s:elseif>
<s:else>
	<c:set var="titolo"><wp:i18n key='TITLE_PAGE_RICHPART_PRESENTA_DOCUMENTAZIONE'/></c:set>
</s:else>

<s:url id="urlPdf" namespace="/do/FrontEnd/RichPartBando" action="createPdfBarcode" />


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp">
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2>${titolo}</h2>

	<p><wp:i18n key='LABEL_RICHPART_COMPLETA_RICHIESTA_E_STAMPA_BARCODE'/></p>
	<p><wp:i18n key="LABEL_GREETINGS" /></p>
	
	<div class="azioni">
		<form action="${urlPdf}" method="post" class="azione">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			
			<div>
				<wp:i18n key="LABEL_STAMPA_CODICE_A_BARRE" var="valuePrintBarcodeButton" />
				<s:submit value="%{#attr.valuePrintBarcodeButton}" title="%{#attr.valuePrintBarcodeButton}" cssClass="button" />
			</div>
		</form>
	</div>
			
	<div class="back-link">
		<a href="<wp:action path="/ExtStr2/do/FrontEnd/RichPartBando/cancelPartecipazione.action" />&amp;codice=<s:property value="codice" />&amp;${tokenHrefParams}">
			<wp:i18n key="LINK_BACK_TO_PROCEDURE" />
		</a>
	</div>
</div>