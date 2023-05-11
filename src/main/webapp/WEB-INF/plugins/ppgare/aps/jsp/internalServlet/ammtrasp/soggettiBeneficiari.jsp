<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>

<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/send_blockUI.jsp" />
<es:checkCustomization var="withAdvancedUI" objectId="UI-ADVANCED" attribute="DATATABLE" feature="ACT" />

<script src='<wp:resourceURL/>static/js/jquery.dataTables.min.js'></script>
<c:if test="${withAdvancedUI}" >
	<script>
	
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
<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">


	<h2><wp:i18n key="TITLE_PAGE_190_SOGGETTI_BENEFICIARI" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_PROSPETTO_AMM_APERTA"/>
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />

	<form action="<wp:action path="/ExtStr2/do/FrontEnd/AmmTrasp/searchSoggettiBeneficiari.action" />" method="post">
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
		
		<fieldset>
			<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_SEARCH_CRITERIA" /></legend>

			<div class="fieldset-row first-row first-row last-row">
				<div class="label">
					<label><wp:i18n key="LABEL_190_DATA_AFFIDAMENTO" />: </label>
				</div>
				<div class="element">
					<label><wp:i18n key="LABEL_DA_DATA" />: </label>
					<s:textfield name="model.dataAffidamentoDa" id="model.dataAffidamentoDa" cssClass="text" value="%{#session.formSearchAmmAperta.dataAffidamentoDa}" 
								maxlength="10" size="10" title="Data affidamento dal" />
					<label><wp:i18n key="LABEL_A_DATA" />: </label>
					<s:textfield name="model.dataAffidamentoA" id="model.dataAffidamentoA" cssClass="text" value="%{#session.formSearchAmmAperta.dataAffidamentoA}" 					
								maxlength="10" size="10" title="Data affidamento al" />
					(<wp:i18n key="LABEL_FORMATO_DATA" />)
				</div>
			</div>
			<div class="azioni">
				<wp:i18n key="BUTTON_SEARCH" var="valueSearchButton" />
				<s:submit value="%{#attr.valueSearchButton}" cssClass="button block-ui" />
			</div>
		</fieldset>

		<c:if test="${listaEsiti ne null}">

			<s:if test="%{listaEsiti.size() > 0}">
				<div class="list-summary">
					<wp:i18n key="SEARCH_RESULTS_INTRO" /> <s:property value="%{listaEsiti.size()}"/> <wp:i18n key="SEARCH_RESULTS_OUTRO" />.
				</div>
				<div class="table-container">
					<table id="tableEsiti" class="info-table">
						<caption style="display:none;"><wp:i18n key="LABEL_190_PROSPETTO_SOGGETTI_BENEFICIARI_SUMMARY" /></caption>
						<thead>
							<tr>
								<th scope="col"><wp:i18n key="LABEL_190_DATA_AFFIDAMENTO" /></th>
								<s:if test="%{showCigColumn == 1}">
									<th scope="col"><wp:i18n key="LABEL_CIG" /></th>
								</s:if>
								<th scope="col"><wp:i18n key="LABEL_190_OGGETTO" /></th>
								<th scope="col" style="min-width: 20em;"><wp:i18n key="LABEL_190_SOGGETTO_BENEFICIARIO" /></th>
								<th scope="col"><wp:i18n key="LABEL_CODICE_FISCALE_ABBR" /> / <wp:i18n key="LABEL_PARTITA_IVA_ABBR" /> <wp:i18n key="LABEL_190_SOGG_BENEFICIARIO" /></th>
								<th scope="col"><wp:i18n key="LABEL_190_IMPORTO_CORRISPOSTO" /></th>
								<th scope="col"><wp:i18n key="LABEL_190_NORMA_BASE_ATTRIBUZIONE" /></th>
								<th scope="col" style="min-width: 20em;"><wp:i18n key="LABEL_190_UFFICIO" /></th>
								<th scope="col"><wp:i18n key="LABEL_190_RESPONSABILE_PROCEDIMENTO_AMM" /></th>
								<th scope="col"><wp:i18n key="LABEL_190_MODALITA_INDIVIDUAZIONE_BENEFICIARIO" /></th>
								<th scope="col" style="min-width: 4em;"><wp:i18n key="LABEL_FILE_ALLEGATI" /></th>
							</tr>
						</thead>
						<tbody>		
							<s:iterator var="riga" value="listaEsiti">		
								<tr>
									<td class="date-content"><s:date name="dataAffidamento" format="dd/MM/yyyy" /></td>
									<s:if test="%{showCigColumn == 1}">
										<td><s:property value="cig" /></td>
									</s:if>
									<td><s:property value="oggetto" /></td>
									<td><s:property value="beneficiario" /></td>
									<td><s:property value="datiFiscali" /></td>
									<td class="money-content">
										<s:if test="%{importo != null}">
											<s:text name="format.money"><s:param value="importo"/></s:text>
										</s:if>
									</td>
									<td><s:property value="norma" /></td>
									<td><s:property value="ufficio" /></td>
									<td><s:property value="rup" /></td>
									<td><s:property value="modalita" /></td>
									<td class="azioni">
                                        <c:if test="${not empty url}">
										    <ul>
												<li>
													<s:url id="urlDownloadDocumenti" value="%{url}">
														<s:param name="urlPage">${currentPageUrl}</s:param>
														<s:param name="currentFrame">${param.currentFrame}</s:param>
													</s:url>											
													<c:choose>
														<c:when test="${skin == 'highcontrast' || skin == 'text'}">
															<s:a href="%{#urlDownloadDocumenti}" title="Scarica lo zip dei documenti allegati">Scarica</s:a>
														</c:when>
														<c:otherwise>
															<s:a href="%{#urlDownloadDocumenti}" title="Scarica lo zip dei documenti allegati" cssClass="bkg zip"></s:a>
														</c:otherwise>
													</c:choose>													
												</li>
										    </ul>
                                        </c:if>
									</td>
								</tr>
							</s:iterator>		
						</tbody>
					</table>
				</div>
				
				<s:url id="urlExport" namespace="/do/FrontEnd/AmmTrasp" action="exportSoggettiBeneficiari">
					<s:param name="last" value="1"></s:param>
				</s:url>
				<p>
					<a href='<s:property value="%{#urlExport}" />&amp;${tokenHrefParams}' class="important">
						<wp:i18n key="LINK_EXPORT_CSV" />
					</a>
				</p>
			</s:if>
		</c:if>
	</form>
</div>