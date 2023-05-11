<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>

<script type="text/javascript" src='<wp:resourceURL/>static/js/jquery.treeview.js'></script>

<wp:headInfo type="CSS" info="jquery/treeview/jquery.treeview.css" />

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2><wp:i18n key="TITLE_PAGE_PREZZI_UNITARI_ESITO_IMPORT" /></h2>

	<p>
		<wp:i18n key="LABEL_PREZZI_UNITARI_PROCESSATI" />: <s:property value="result.inputRows"/>
	</p>
	<p>
		<s:if test="%{result.errorRows != null && !result.errorRows.isEmpty()}">
            <wp:i18n key="LABEL_PREZZI_UNITARI_IMPORTABILI" />: <s:property value="result.successRows"/>
		</s:if>
		<s:else>
		    <wp:i18n key="LABEL_PREZZI_UNITARI_IMPORTATI_CORRETTAMENTE" />: <s:property value="result.successRows"/>
		</s:else>
	</p>
	<s:if test="%{result.errorRows != null && !result.errorRows.isEmpty()}">
		<p>
			<wp:i18n key="LABEL_PREZZI_UNITARI_ERRATI" />: <s:property value="%{result.errorRows.size()}"/>
		</p>
		<ul id="prodottiErrati" class="filetree errors">
			<s:iterator var="errorRow" value="result.errorRows" status="status">
				<li class='<s:if test="%{#status.first}">first</s:if> <s:if test="%{#status.last}">last</s:if>'>
					<s:property value="%{formatI18nLabel('LABEL_PREZZO_UNITARIO_ALLA_RIGA', #errorRow.key + 1)}" />: 
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
		<form action="<wp:action path="/ExtStr2/do/FrontEnd/GareTel/openPageImportPrezziUnitari.action" />" method="post" class="azione">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			<div>
				<wp:i18n key="BUTTON_RICARICA" var="valueRicaricaButton" />
				<wp:i18n key="TITLE_BACK_TO_UPLOAD_XLS_PRODOTTI" var="titleRicaricaButton" />
				<s:submit value="%{#attr.valueRicaricaButton}" title="%{#attr.titleRicaricaButton}" cssClass="button" />
				<input type="hidden" name="codice" value='${codice}'/>
			</div>
		</form>
	</div>
	<div class="back-link">
		<a href='<wp:action path="/ExtStr2/do/FrontEnd/GareTel/openPageOffTelPrezziUnitari.action" />&amp;codice=${codice}&amp;${tokenHrefParams}'>
			<wp:i18n key="LINK_BACK_TO_PREZZI_UNITARI" />
		</a>
	</div>
</div>