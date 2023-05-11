<%@ taglib prefix="wp" uri="aps-core.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<c:set var="backLink" value="ordiniTutti" />


<%-- ******************************************************************************** --%>
<script type="text/javascript">
<!--//--><![CDATA[//><!--
// apertura della pagina...
$(document).ready(function() {
	
});	

//--><!]]>
</script>
<%-- ******************************************************************************** --%>


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp">
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2>
		<wp:i18n key="TITLE_PAGE_EORDERS_FATTURA_CREA" />&nbsp;<s:property value="orderCode"/>
	</h2>
	
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
	<jsp:include page="stepsFatture.jsp" />

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_EORDERS_FATTURA_RIEPILOGO" />
	</jsp:include>


	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/skip_form_buttons.jsp" />
	<form action="<wp:action path="/ExtStr2/do/FrontEnd/EOrders/datiRiepilogo.action" />"  method="post">
	<div class="detail-section first-detail-section">
		<h3 class="detail-section-title">
			<span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span>
			<wp:i18n key="LABEL_FATTURA_RIEPILOGO" />
		</h3>
		<fieldset>
				<legend>
					<span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span>
					<wp:i18n key="LABEL_FATTURA_RIEPILOGO" />
				</legend>
				<div class="fieldset-row first-row">
				<div class="label"><label for="codiceFattura"><wp:i18n	key="LABEL_FATT_CIG" /></label>:</div><div class="element"><s:property value="fatture.cig" /></div>
				</div>
				<div class="fieldset-row">
				<div class="label"><label for="codiceFattura"><wp:i18n	key="LABEL_FATT_CODICEDESTINATARIO" /></label>:</div><div class="element"><s:property value="fatture.codiceDestinatario" /></div>
				</div>
				<div class="fieldset-row">
				<div class="label"><label for="codiceFattura"><wp:i18n	key="LABEL_FATT_CODIMPFORNITORE" /></label>:</div><div class="element"><s:property value="fatture.codimpFornitore" /></div>
				</div>
				<div class="fieldset-row">
				<div class="label"><label for="codiceFattura"><wp:i18n	key="LABEL_FATT_CREATEDDATE" /></label>:</div><div class="element"><s:text name="format.date.withtime"><s:param name="value" value="fatture.createdDate" /></s:text></div>
				</div>
				<div class="fieldset-row">
				<div class="label"><label for="codiceFattura"><wp:i18n	key="LABEL_FATT_DATAFATTURA" /></label>:</div><div class="element"><s:text name="format.date.withtime"><s:param name="value" value="fatture.dataFattura" /></s:text></div>
				</div>
				<div class="fieldset-row"><div class="label">
				<label for="codiceFattura"><wp:i18n	key="LABEL_FATT_ID" /></label>:</div><div class="element"><s:property value="fatture.id" /></div>
				</div>
				<div class="fieldset-row">
				<div class="label"><label for="codiceFattura"><wp:i18n	key="LABEL_FATT_IDNSOWSORDINI" /></label>:</div><div class="element"><s:property value="fatture.idNsoWsOrdini" /></div>
				</div>
				<div class="fieldset-row">
				<div class="label"><label for="codiceFattura"><wp:i18n	key="LABEL_FATT_IDORDINE" /></label>:</div><div class="element"><s:property value="fatture.idOrdine" /></div>
				</div>
				<div class="fieldset-row">
				<div class="label"><label for="codiceFattura"><wp:i18n	key="LABEL_FATT_IDUSER" /></label>:</div><div class="element"><s:property value="fatture.iduser" /></div>
				</div>
				<div class="fieldset-row">
				<div class="label"><label for="codiceFattura"><wp:i18n	key="LABEL_FATT_NUMEROFATTURA" /></label>:</div><div class="element"><s:property value="fatture.numeroFattura" /></div>
				</div>
				<%-- <div class="fieldset-row">
				<div class="label"><label for="codiceFattura"><wp:i18n	key="LABEL_FATT_PLAINFILECONTENTS" /></label>:</div><div class="element"><s:property value="fatture.plainFileContents" />
					<br>
					<s:property value="fatturaXMLContent"/>
					</div>
				</div> --%>
				<div class="fieldset-row">
				<div class="label"><label for="codiceFattura"><wp:i18n	key="LABEL_FATT_PLAINFILENAME" /></label>:</div><div class="element"><s:property value="fatture.plainFileName" /></div>
				</div>
				<div class="fieldset-row">
				<div class="label"><label for="codiceFattura"><wp:i18n	key="LABEL_FATT_PROGINVIO" /></label>:</div><div class="element"><s:property value="fatture.progressivoInvio" /></div>
				</div>
				<%-- <div class="fieldset-row">
				<div class="label"><label for="codiceFattura"><wp:i18n	key="LABEL_FATT_SIGNEDFILECONTENTS" /></label>:</div><div class="element"><s:property value="fatture.signedFileContents" /></div>
				</div>
				<div class="fieldset-row">
				<div class="label"><label for="codiceFattura"><wp:i18n	key="LABEL_FATT_SIGNEDFILENAME" /></label>:</div><div class="element"><s:property value="fatture.signedFileName" /></div>
				</div> --%>
				<div class="fieldset-row">
				<div class="label"><label for="codiceFattura"><wp:i18n	key="LABEL_FATT_STATUSSDI" /></label>:</div><div class="element"><s:property value="fatture.statusSdi" /></div>
				</div>
				<div class="fieldset-row">
				<div class="label"><label for="codiceFattura"><wp:i18n	key="LABEL_FATT_STATUSWS" /></label>:</div><div class="element"><s:property value="fatture.statusWs" /></div>
				</div>
				<div class="fieldset-row">
				<div class="label"><label for="codiceFattura"><wp:i18n	key="LABEL_FATT_TOTRIEPILOGOIMPONIBILEIMPORTO" /></label>:</div><div class="element"><s:text name="format.money"><s:param name="value" value="fatture.totRiepilogoImponibileImporto" /></s:text></div>
				</div>
				<div class="fieldset-row">
				<div class="label"><label for="codiceFattura"><wp:i18n	key="LABEL_FATT_TOTRIEPILOGOIMPOSTA" /></label>:</div><div class="element"><s:text name="format.money"><s:param name="value" value="fatture.totRiepilogoImposta" /></s:text></div>
				</div>
				<div class="fieldset-row">
				<div class="label"><label for="codiceFattura"><wp:i18n	key="LABEL_FATT_TOTRIEPILOGOTOTALE" /></label>:</div><div class="element"><s:text name="format.money"><s:param name="value" value="fatture.totRiepilogoTotale" /></s:text></div>
				</div>
				<div class="fieldset-row">
				<div class="label"><label for="codiceFattura"><wp:i18n	key="LABEL_FATT_UPDATEDDATE" /></label>:</div><div class="element"><s:text name="format.date.withtime"><s:param name="value" value="fatture.updatedDate" /></s:text></div>
				</div>

		</fieldset>
	</div>
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
		<input type="hidden" name="id" value="${id}" />
		<input type="hidden" name="idFatt" value="${idFatt}" />
		<input type="hidden" name="orderCode" value="${orderCode}" />
		<div class="azioni">
			<s:url id="urlExport" namespace="/do/FrontEnd/EOrders" action="downloadXml" escapeAmp="false" >
				<s:param name="id" value="id"></s:param>
				<s:param name="idFatt" value="idFatt"></s:param>
				<s:param name="orderCode" value="orderCode"></s:param>
				<s:param name="fileName" value="fatture.plainFileName"></s:param>
			</s:url>
			<a href='<s:property value="%{#urlExport}" />' class="button">DOWNLOAD XML</a>
		</div>
		<div class="azioni">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_previous.jsp" />
			<wp:i18n key="BUTTON_FATT_BACK_ORDER" var="valueBackOrderButton" />
			<s:submit value="%{#attr.valueBackOrderButton}" title="%{#attr.valueBackOrderButton}" cssClass="button" method="cancel"></s:submit>
		</div>
	</form>
	<%-- <div class="detail-section">
		<form action="<wp:action path="/ExtStr2/do/FrontEnd/EOrders/datiRiepilogo.action" />" method="post">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			<input type="hidden" name="id" value="${id}" />
			<input type="hidden" name="idFatt" value="${idFatt}" />
			<input type="hidden" name="orderCode" value="${orderCode}" />
			<s:submit value="RICARICA" method="riepilogo" cssClass="button"></s:submit>
		</form>
	</div> --%>
	
	
</div>