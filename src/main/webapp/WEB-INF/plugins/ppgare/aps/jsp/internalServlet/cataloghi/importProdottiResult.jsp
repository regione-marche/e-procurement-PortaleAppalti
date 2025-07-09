<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>

<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/jquery_treeview.jsp" />

<script type="text/javascript">
<!--//--><![CDATA[//><!--
	$(document).ready(function() {

		// tutti gli alberi compressi
		$(".filetree").treeview({collapsed: true});
		
	});
//--><!]]>
</script>


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">
	
	<h2><wp:i18n key="TITLE_PAGE_CATALOGHI_ESITO_IMPORT" /></h2>

	<p>
		<wp:i18n key="LABEL_PRODOTTI_PROCESSATI" />: <s:property value="result.inputRows"/>
	</p>
	<p>
		<wp:i18n key="LABEL_PRODOTTI_IMPORTATI_CORRETTAMENTE" />: <s:property value="result.successRows"/>
	</p>
	<s:if test="%{result.skippedRows != null && !result.skippedRows.isEmpty()}">
		<p>
			<wp:i18n key="LABEL_PRODOTTI_SCARTATI" />: <s:property value="%{result.skippedRows.size()}"/>
		</p>
		<ul id="prodottiScartati" class="filetree errors">
			<s:iterator var="skippedRow" value="result.skippedRows" status="status">
				<li class='<s:if test="%{#status.first}">first</s:if> <s:if test="%{#status.last}">last</s:if>'>
					<wp:i18n key="LABEL_PRODOTTO_ALLA_RIGA" /> <s:property value="%{#skippedRow.key + 1}" /> : <s:property value="%{#skippedRow.value}" /><br/>
				</li>
			</s:iterator>
		</ul>
	</s:if>
	<s:if test="%{result.errorRows != null && !result.errorRows.isEmpty()}">
		<p>
			<wp:i18n key="LABEL_PRODOTTI_ERRATI" />: <s:property value="%{result.errorRows.size()}"/>
		</p>
		<ul id="prodottiErrati" class="filetree errors">
			<s:iterator var="errorRow" value="result.errorRows" status="status">
				<li class='<s:if test="%{#status.first}">first</s:if> <s:if test="%{#status.last}">last</s:if>'>
					<wp:i18n key="LABEL_PRODOTTO_ALLA_RIGA" /> <s:property value="%{#errorRow.key + 1}" />
					<ul id='errorRow<s:property value="%{#errorRow.key}"/>'>
						<s:iterator var="errore" value="#errorRow.value" status="statusErrore">
							<li class='<s:if test="%{#statusErrore.first}">first</s:if> <s:if test="%{#statusErrore.last}">last</s:if>'>
								<s:property value="%{errore}" />
							</li>
						</s:iterator>
					</ul>
				</li>
			</s:iterator>
		</ul>
	</s:if>
	<div class="azioni">
		<form action="<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/openPageImportProducts.action" />" method="post" class="azione">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			<div>
				<wp:i18n key="BUTTON_RICARICA" var="valueRicaricaButton" />
				<wp:i18n key="TITLE_BACK_TO_UPLOAD_XLS_PRODOTTI" var="titleRicaricaButton" />
				<s:submit value="%{#attr.valueRicaricaButton}" title="%{#attr.titleRicaricaButton}" cssClass="button" />
				<input type="hidden" name="catalogo" value='<s:property value="catalogo"/>'/>
				<input type="hidden" name="ext" value='${param.ext}'/>
			</div>
		</form>
	</div>
	<div class="back-link">
		<a href='<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/openGestioneProdotti.action" />&amp;catalogo=<s:property value="%{catalogo}"/>&amp;ext=${param.ext}'>
			<wp:i18n key="LINK_BACK_TO_GESTIONE_PRODOTTI" />
		</a>
	</div>
</div>