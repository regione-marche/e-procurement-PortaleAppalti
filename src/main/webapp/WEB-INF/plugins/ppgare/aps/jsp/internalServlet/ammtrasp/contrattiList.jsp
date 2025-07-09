<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>

<es:checkCustomization var="visImportoContratto" objectId="AMMTRASP-RIEPCONTRATTI" attribute="IMPCONTRATTO" feature="VIS" />
<es:checkCustomization var="visExportCSV" objectId="AMMTRASP-RIEPCONTRATTI" attribute="EXPORT" feature="VIS" />
<es:checkCustomization var="withAdvancedUI" objectId="UI-ADVANCED" attribute="DATATABLE" feature="ACT" />

<s:set var="searchForm" value="%{#session.formSearchAmmTrasp}" />

<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/send_blockUI.jsp" />
<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/jquery_ui.jsp" />
<script src="<wp:resourceURL/>static/js/ppgare/dialogFullScreen.js"></script>
<script src='<wp:resourceURL/>static/js/jquery.dataTables.min.js'></script>

<c:if test="${withAdvancedUI}" >
	<script>
	
		var buttonCloseDialog = '<wp:i18n key="BUTTON_CLOSE" />';
	
		$(document).ready(function() {
		
			$.extend($.fn.dataTable.defaults, {
				"paging": false,
			    "ordering": false,
			    "info": false,
			    "searching" : false
			});
			
			$('#tableBandi').dataTable({
				scrollX: true,
				scrollY: "25em",
				scrollCollapse: true
			});
		});
	</script>
</c:if>

<es:getAppParam name = "denominazioneStazioneAppaltanteUnica" var = "stazAppUnica" scope = "page"/> 	


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<c:set var="skin" value=""/>
<c:if test="${! empty param.skin}">
	<c:set var="skin" value="${param.skin}"/>
</c:if>
<c:if test="${empty skin}">
	<c:set var="skin" value="${cookie.skin.value}"/>
</c:if>

<div class="portgare-list">
	<h2><wp:i18n key="TITLE_PAGE_190_RIEPILOGO_CONTRATTI" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_AMMTRASP_CONTRATTI_LISTA"/>
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />

	<form class="form-ricerca" action="<wp:action path="/ExtStr2/do/FrontEnd/AmmTrasp/searchContratti.action" />" method="post">
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
		
		<%-- ********** FILTRI ********** --%>
		<fieldset>
			<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_SEARCH_CRITERIA" /></legend>

			<div class="fieldset-row first-row">
				<div class="label">
					<label for="model.cig"><wp:i18n key="LABEL_CIG" />: </label>
				</div>
				<div class="element">
					<s:textfield name="model.cig" id="model.cig" cssClass="text" value="%{#searchForm.cig}" 
											 maxlength="10" size="10" />
				</div>
			</div>	
			<div class="fieldset-row">
				<div class="label">
					<label for="model.stazioneAppaltante"><wp:i18n key="LABEL_STAZIONE_APPALTANTE" /> : </label>
				</div>
				<div class="element">
					<c:choose>
						<c:when test="${! empty stazAppUnica }">
							<s:set var="stazAppUnicaToStruts">${stazAppUnica}</s:set>
							<s:property value="stazAppUnicaToStruts" />
						</c:when>
						<c:when test="${! empty stazioneAppaltante}">
							<s:property value="%{descStazioneAppaltante}"/>
						</c:when>
						<c:otherwise>
							<wp:i18n key="OPT_CHOOSE_STAZIONE_APPALTANTE" var="headerValueStazioneAppaltante" />
							<s:select name="model.stazioneAppaltante" id="model.stazioneAppaltante" list="maps['stazioniAppaltanti']" 
									value="%{#searchForm.stazioneAppaltante}" 
									headerKey="" headerValue="%{#attr.headerValueStazioneAppaltante}" 
									cssStyle="width: 100%;" >
							</s:select>
						</c:otherwise>
					</c:choose>
				</div>
			</div>
			<div class="fieldset-row">
				<div class="label">
					<label for="model.oggetto"><wp:i18n key="LABEL_190_OGGETTO" />: </label>
				</div>
				<div class="element">
					<s:textfield name="model.oggetto" id="model.oggetto" cssClass="text" value="%{#searchForm.oggetto}" 
											 maxlength="50" size="30" />
				</div>
			</div>	
<%--
			<div class="fieldset-row">
				<div class="label">
					<label for="model.tipoAppalto"><wp:i18n key="LABEL_TIPO_APPALTO" />: </label>
				</div>
				 <div class="element">
				 	<wp:i18n key="OPT_CHOOSE_TIPO_APPALTO" var="headerValueTipoAppalto" />
					<s:select name="model.tipoAppalto" id="model.tipoAppalto" list="maps['tipiAppalto']" value="%{#searchForm.tipoAppalto}" 
	 										headerKey="" headerValue="%{#attr.headerValueTipoAppalto}" tabindex="4">		
					</s:select>
				</div> 
			</div>	
--%>
			<div class="fieldset-row">
				<div class="label">
					<label for="model.partecipante"><wp:i18n key="LABEL_PARTECIPANTE" />: </label>
				</div>
				<div class="element">
					<s:textfield name="model.partecipante" id="model.partecipante" cssClass="text" value="%{#searchForm.partecipante}" 
											 maxlength="50" size="30" />
				</div>
			</div>
			<div class="fieldset-row">
				<div class="label">
					<label for="model.aggiudicatario"><wp:i18n key="LABEL_AGGIUDICATARIO" />: </label>
				</div>
				<div class="element">
					<s:textfield name="model.aggiudicatario" id="model.aggiudicatario" cssClass="text" value="%{#searchForm.aggiudicatario}" 
											 maxlength="50" size="30" />
				</div>
			</div>	
			<div class="fieldset-row last-row">
				<div class="label">
					<label><wp:i18n key="LABEL_DATA_PUBBLICAZIONE_ESITO" />: </label>
				</div>
				<div class="element">
					<wp:i18n key="LABEL_DATA_PUBBLICAZIONE_ESITO" var="headerValueDataPubblicazioneEsito"/>
					<wp:i18n key="LABEL_DA_DATA" var="headerValueDa"/>
					<wp:i18n key="LABEL_A_DATA" var="headerValueA"/>
					<label><wp:i18n key="LABEL_DA_DATA" />: </label>
					<s:textfield name="model.dataDa" id="model.dataDa" cssClass="text" value="%{#searchForm.dataDa}" 
								maxlength="10" size="10" title="%{#attr.headerValueDataPubblicazioneEsito} %{#attr.headerValueDa}" />
					<label><wp:i18n key="LABEL_A_DATA" />: </label>
					<s:textfield name="model.dataA" id="model.dataA" cssClass="text" value="%{#searchForm.dataA}" 
								maxlength="10" size="10" title="%{#attr.headerValueDataPubblicazioneEsito} %{#attr.headerValueA}" />
					(<wp:i18n key="LABEL_FORMATO_DATA" />)
				</div>
			</div>
			
			<div class="azioni">
				<wp:i18n key="BUTTON_SEARCH" var="valueSearchButton" />
				<s:submit value="%{#attr.valueSearchButton}" cssClass="button block-ui"/>
			</div>
		</fieldset>
		
		<%-- link BDNCP --%>
		<div style="text-align: center;">
			<s:set var="codiceSA" value="%{#searchForm.stazioneAppaltante}"/>
			<c:choose>
				<c:when test="${! empty stazAppUnica }">
					<s:set var="codiceSA">${stazAppUnica}</s:set>
				</c:when>
				<c:when test="${! empty stazioneAppaltante}">
					<s:set var="codiceSA">${stazioneAppaltante}</s:set>
				</c:when>
			</c:choose>
			
			<s:set var="descrSA" value=""/>
			<s:iterator var="item" value="maps['stazioniAppaltanti']">
				<s:if test="%{#item.key == #codiceSA}">
					<s:set var="descrSA" value="#item.value"/>
				</s:if>
			</s:iterator>
			
			<s:if test='%{#codiceSA != null && #codiceSA != ""}'>
				<span>
					<s:property value="descrSA" /> - 
					<a href='${URL_BDNCP_DETTAGLIO_SA}<s:property value="%{getCodiceFiscaleSA(#codiceSA)}"/>' title='<wp:i18n key="LINK_VIEW_DETAIL" />' target='_blank'>
						<wp:i18n key="LABEL_LINK_BDNCP"/>
					</a>
				</span>
				<p/>
			</s:if>
		</div>
		
		<%-- ********** LISTA ********** --%>
		<c:if test="${listaContratti ne null}">
			<s:if test="%{listaContratti.size() > 0}">
			
				<div class="list-summary">
					<wp:i18n key="SEARCH_RESULTS_INTRO" /> <s:property value="%{listaContratti.size()}"/> <wp:i18n key="SEARCH_RESULTS_OUTRO" />.
				</div>
	 			<div class="table-container">
					<table id="tableBandi" class="info-table source-popup">
						<caption style="display:none;">Tabella Singole Procedure</caption>
						<thead>
							<tr>
								<th scope="col"><wp:i18n key="LABEL_CIG" /></th>
								<th scope="col"><wp:i18n key="LABEL_BDNCP" /></th>
								<th scope="col" style="min-width: 20em;"><wp:i18n key="LABEL_STAZIONE_APPALTANTE" /></th>
								<th scope="col" style="min-width: 20em;"><wp:i18n key="LABEL_190_OGGETTO" /></th>
								<th scope="col" style="min-width: 20em;"><wp:i18n key="LABEL_190_SCELTA_CONTRAENTE" /></th>
								<th scope="col" style="min-width: 20em;"><wp:i18n key="LABEL_AGGIUDICATARIO" /></th>
								<th scope="col"><wp:i18n key="LABEL_190_IMPORTO_AGGIUDICAZIONE" /></th>
								<c:if test="${visImportoContratto}">
									<th scope="col"><wp:i18n key="LABEL_190_IMPORTO_CONTRATTO" /></th>
								</c:if>
								<th scope="col" style="min-width: 12em;"><wp:i18n key="LABEL_190_TEMPI" /></th>
								<th scope="col"><wp:i18n key="LABEL_190_IMPORTO_SOMME_LIQUIDATE" /></th>
								<th scope="col"><wp:i18n key="LABEL_IMPORTO_SCOSTAMENTO" /></th>
							</tr>
						</thead>
						<tbody>
							<s:iterator var="riga" value="listaContratti">
								<tr>
									<td>
										<strong>
										<a href='<wp:action path="/ExtStr2/do/FrontEnd/AmmTrasp/viewContratto.action" />&amp;codice=<s:property value="%{codice}"/>'
										   title='<wp:i18n key="LINK_VIEW_DETAIL" />' >
											<s:property value="cig" /> 
										</a>
										</strong>
									</td>
									<td>
										<a href='${URL_BDNCP_DETTAGLIO_CIG}<s:property value="cig" />' title='<wp:i18n key="LINK_VIEW_DETAIL" />' 
											class="bkg detail-very-big" target='_blank'>
										</a>
									</td>
									<td>
										<s:if test="%{strutturaProponenteCF != null}">
											<s:property value="strutturaProponenteCF" /> - 
										</s:if>
										<c:choose>
											<c:when test="${! empty stazAppUnica }">
												<s:set var="stazAppUnicaToStruts">${stazAppUnica}</s:set>
												<s:property value="stazAppUnicaToStruts" />
											</c:when>
											<c:otherwise>
												<s:property value="strutturaProponenteDenominazione" />
											</c:otherwise>
										</c:choose>
									</td>
									<td><s:property value="oggetto" /></td>
									<td><s:property value="sceltaContraente" /></td>
 									<td>
 										<s:iterator value="aggiudicatario" status="status">
 											<s:property value="aggiudicatario[#status.index]" />
 										</s:iterator>
 									</td>
									<td class="money-content">
										<c:if test="${not empty importoAggiudicazione}">
											<s:text name="format.money"><s:param value="importoAggiudicazione"/></s:text>
										</c:if>
									</td>  
									<c:if test="${visImportoContratto}">
										<td class="money-content">
											<c:if test="${not empty importoContratto}">
												<s:text name="format.money"><s:param value="importoContratto"/></s:text>
											</c:if>
										</td>
									</c:if>
									<td class="date-content">
										<c:if test="${not empty dataInizio}">
											<!-- inizio: <s:date name="#riga.dataInizio" format="dd/MM/yyyy" /> -->
											<wp:i18n key="LABEL_190_INIZIO" />: <s:date name="dataInizio" format="dd/MM/yyyy"/>
											<br/> 
										</c:if>
										<c:if test="${not empty dataUltimazione}">
											<!-- ultimazione: <s:date name="#riga.dataUltimazione" format="dd/MM/yyyy" /> -->
											<wp:i18n key="LABEL_190_ULTIMAZIONE" />: <s:date name="dataUltimazione" format="dd/MM/yyyy"/>
										</c:if>
									</td>
									<td class="money-content">
										<c:if test="${not empty importoSommeLiquidate}">
											<s:text name="format.money"><s:param value="importoSommeLiquidate"/></s:text>
										</c:if> 
									</td>
									<td class="money-content">
										<c:if test="${not empty importoAggiudicazione and not empty importoSommeLiquidate}">
											<s:set var="importoScostamento" value="%{importoAggiudicazione - importoSommeLiquidate}"/>
											<s:text name="format.money"><s:param value="%{#importoScostamento}"/></s:text>
										</c:if>
									</td>  
								</tr>
							</s:iterator>
						</tbody>
					</table>
				</div>

				<c:if test="${!(skin == 'highcontrast' || skin == 'text') && withAdvancedUI}">
					<a href='javascript:' title="<wp:i18n key="LINK_VIEW_TABLE_DETAIL" />" class="bkg expand-fullscreen" id="openDialog"></a>
				</c:if>

                <c:if test="${visExportCSV}">
                    <s:url id="urlExport" namespace="/do/FrontEnd/AmmTrasp" action="exportContratti">
                        <s:param name="last" value="1"></s:param>
                    </s:url>

                    <p>
                        <a href='<s:property value="%{#urlExport}" />' class="important"><wp:i18n key="LINK_EXPORT_CSV" /></a>
                    </p>
				</c:if>
			</s:if>
			<s:else>
				<div class="list-summary">
					<wp:i18n key="SEARCH_NOTHING_FOUND" />.
				</div>
			</s:else>
		</c:if>

	</form>	

</div>
