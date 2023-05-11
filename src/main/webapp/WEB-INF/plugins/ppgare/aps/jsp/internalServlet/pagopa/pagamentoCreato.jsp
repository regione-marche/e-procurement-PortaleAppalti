<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<wp:headInfo type="CSS" info="jquery/jquery-ui/jquery-ui.css" />
<script type="text/javascript" src="<wp:resourceURL/>static/js/jquery-ui-1.12.1.min.js"></script>

<%-- ******************************************************************************** --%>
<script type="text/javascript">
<!--//--><![CDATA[//><!--
	
	
// apertura della pagina...
$(document).ready(function() {
	console.log("PagoPA","Pagina caricata correttamente.");
	
});

//--><!]]>
</script>
<%-- ******************************************************************************** --%>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp">
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">
<c:catch var="exceptionPagoPa">
	<h2><wp:i18n key="TITLE_PAGE_PAGOPA_DETT" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_PAGOPA_DETT" />
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_messages.jsp" />
		
	<form action="<wp:action path="/ExtStr2/do/FrontEnd/PagoPA/nuovoPagamento.action" />" method="post">
		<fieldset>
		<div class="fieldset-row first-row">
			<div class="label">
				<label for="model.codiceGara"><wp:i18n key="LABEL_PAGOPA_GARA_CODICE" />: </label>
			</div>
			<div class="element">
				<s:property value="model.codiceGara"/>
			</div>
		</div>
		<div class="fieldset-row first-row">
			<div class="label">
				<label for="model.titoloGara"><wp:i18n key="LABEL_PAGOPA_GARA_TITOLO" />: </label>
			</div>
			<div class="element">
				<s:property value="model.titoloGara"/>
			</div>
		</div>
		<div class="fieldset-row">
			<div class="label">
				<label for="model.causale"><wp:i18n key="LABEL_PAGOPA_CAUSALE" />: </label>
			</div>
			<div class="element">
				<s:property value="%{tipiCausalePagamento[model.causale]}"/>
			</div>
		</div>
		<div class="fieldset-row">
			<div class="label">
				<label for="model.importo"><wp:i18n key="LABEL_PAGOPA_IMPORTO" />: </label>
			</div>
			<div class="element">
				<s:property value="model.importo"/>
			</div>
		</div>
		<%-- <div class="fieldset-row">
			<div class="label">
				<label for="model.idDebito"><wp:i18n key="LABEL_PAGOPA_IDDEBITO" />: </label>
			</div>
			<div class="element">
				<s:property value="model.idDebito"/>
			</div>
		</div> --%>
		<%-- <div class="fieldset-row">
			<div class="label">
				<label for="model.idRata"><wp:i18n key="LABEL_PAGOPA_IDRATA" />: </label>
			</div>
			<div class="element">
				<s:hidden name="model.idRata" value="%{model.idRata}" id="model.idRata" class="text" />
				<s:property value="model.idRata"/>
			</div>
		</div> --%>
		<%-- <div class="fieldset-row">
			<div class="label">
				<label for="model.dataInizioValidita"><wp:i18n key="LABEL_PAGOPA_DATAINIZIOVAL" />: </label>
			</div>
			<div class="element">
				<s:date name="model.dataInizioValidita" format="dd/MM/yyyy HH:mm:ss" />
			</div>
		</div> --%>
		<div class="fieldset-row">
			<div class="label">
				<label for="model.iuv"><wp:i18n key="LABEL_PAGOPA_IUV" />: </label>
			</div>
			<div class="element">
				<s:property value="model.iuv"/>
			</div>
		</div>
		<div class="fieldset-row">
			<div class="label">
				<label for="model.dataPagamento"><wp:i18n key="LABEL_PAGOPA_DATAPAG" />: </label>
			</div>
			<div class="element">
				<s:date name="model.dataPagamento" format="dd/MM/yyyy HH:mm:ss" />
			</div>
		</div>
		<%-- <div class="fieldset-row">
			<div class="label">
				<label for="model.dataScadenza"><wp:i18n key="LABEL_PAGOPA_DATASCAD" />: </label>
			</div>
			<div class="element">
				<s:date name="model.dataScadenza" format="dd/MM/yyyy HH:mm:ss" />
			</div>
		</div> --%>
		<%-- <div class="fieldset-row">
			<div class="label">
				<label for="model.dataFineValidita"><wp:i18n key="LABEL_PAGOPA_DATAFINEVAL" />: </label>
			</div>
			<div class="element">
				<s:date name="model.dataFineValidita" format="dd/MM/yyyy HH:mm:ss" />
			</div>
		</div> --%>
		<s:hidden id="model.id" name="model.id" value="%{model.id}" />
		
		</fieldset>
		<div class="azioni">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			<s:if test="%{model.stato==3}">
				<wp:i18n key="LABEL_PAGOPA_SCRIC" var="scaricaRicevuta"/>
				<wp:url page="ppgare_pagopa_effettuati" var="urlErrori" />
				<s:hidden id="urlErrori" name="urlErrori" value="%{#attr.urlErrori}" />
				<s:url var="urlRicevutaDownload" namespace="/do/FrontEnd/PagoPA" action="scaricaRicevuta" >
					<s:param name="model.id" value="%{model.id}"></s:param>
				</s:url>
				<a href='<s:property value="%{#attr.urlRicevutaDownload}" />' class="button" ><s:property value="%{#attr.scaricaRicevuta}"/></a>
				<%-- <s:submit  value="%{#attr.scaricaRicevuta}" title="%{#attr.scaricaRicevuta}" cssClass="button" action="scaricaRicevuta" ></s:submit> --%>
			</s:if>
			<s:elseif test="%{model.pagamentoAbilitato}">
				<%-- <jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_cancel.jsp" /> --%>
				
				<wp:i18n key="LABEL_PAGOPA_ESPAG" var="eseguipagamento"/>
				<s:submit value="%{#attr.eseguipagamento}" title="%{#attr.eseguipagamento}" cssClass="button" method="eseguipagamento"></s:submit>
			</s:elseif>
		</div>		
	</form>

	<div class="back-link">
		<a href="<wp:url page="ppcommon_area_personale" />?${tokenHrefParams}">
			<wp:i18n key="LINK_BACK_TO_AREAPERSONALE" />
		</a>
	</div>
</c:catch>
<c:if test="${exceptionPagoPa!=null}">
There is an exception: ${exceptionPagoPa.message}<br>
The exception is : ${exceptionPagoPa}<br>
	<br>
</c:if>
</div>