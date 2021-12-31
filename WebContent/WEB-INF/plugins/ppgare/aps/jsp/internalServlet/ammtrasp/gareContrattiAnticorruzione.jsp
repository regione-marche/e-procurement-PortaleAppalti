<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>

<es:checkCustomization var="visExportPdf" objectId="EXPORT" attribute="PDF" feature="VIS" />

<wp:headInfo type="CSS" info="jquery/jquery-ui/jquery-ui.css" />

<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/send_blockUI.jsp" />

<script type="text/javascript" src='<wp:resourceURL/>static/js/jquery.dataTables.min.js'></script>
<script type="text/javascript" src="<wp:resourceURL/>static/js/jquery-ui-1.12.1.min.js"></script>
<script type="text/javascript" src="<wp:resourceURL/>static/js/ppgare/dialogFullScreen.js"></script>
<script type="text/javascript" src="<wp:resourceURL/>static/js/jspdf.min.js"></script>
<script type="text/javascript" src="<wp:resourceURL/>static/js/jspdf.plugin.autotable.js"></script>
<script type="text/javascript">

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

<s:set var="searchForm" value="%{#session.formSearchAnticorruzione}" />

<div class="portgare-view">

	<h2><wp:i18n key="TITLE_PAGE_190_PROSPETTO_ANTICORRUZIONE" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
			<jsp:param name="keyMsg" value="BALLOON_PROSPETTO_ANTICORRUZIONE"/>
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
	
	<fieldset>
		<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_190_DATASET_XML" /></legend>
		<s:iterator var="adempimento" value="listaAdempimenti">
			<div class="fieldset-row">
				<div class="label">
					
					<s:if test="%{!#adempimento.stazioneAppaltante.isEmpty()}">
					 <s:property value="%{#adempimento.codiceFiscaleSA}"/> - <s:property value="%{#adempimento.stazioneAppaltante}"/>
					</s:if>
					<s:else>
					<wp:i18n key="LABEL_190_ADEMPIMENTO" /> <s:property value="%{#adempimento.annoRiferimento}"/>
					</s:else>
				</div>
				<div class="element">
					<s:if test="%{#adempimento.pubblicato ==1}">
						<a href='<s:property value="%{#adempimento.url+'/indice_dataset.xml'}" />' class="important">
							<wp:i18n key="LINK_190_VISUALIZZA_DATASET" />
						</a>
						<ul>
							<li class='first'>
								<wp:i18n key="LABEL_190_DATA_PRIMA_GENERAZIONE_XML" /> : <s:date name="#adempimento.dataPubblicazione" format="dd/MM/yyyy" />
							</li>
							<li>
								<wp:i18n key="LABEL_190_DATA_ULTIMO_AGGIORNAMENTO_XML" /> : <s:date name="#adempimento.dataAggiornamento" format="dd/MM/yyyy" />
							</li>
						</ul>
					</s:if>
					<s:else><wp:i18n key="LABEL_190_GENERAZIONE_XML" /></s:else>
				</div>
			</div>
		</s:iterator>
	</fieldset>
	
	<form action="<wp:action path="/ExtStr2/do/FrontEnd/AmmTrasp/searchAnticorruzione.action" />" method="post" >
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
		
		<fieldset>
			<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_SEARCH_CRITERIA" /></legend>

			<div class="fieldset-row first-row">
				<div class="label">
					<label ><wp:i18n key="LABEL_190_ANNO_RIFERIMENTO" />: </label>
				</div>
				<div class="element">
					<s:property value="%{#searchForm.anno}"/>
					<s:hidden name="model.anno" id="model.anno" value="%{#searchForm.anno}"/> 
				</div>
			</div>
			<div class="fieldset-row">
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
					<label for="model.proponente"><wp:i18n key="LABEL_190_STRUTTURA_PROPONENTE" />: </label>
				</div>
				<div class="element">
					<c:choose>
						<c:when test="${! empty stazioneAppaltante}">
<%-- 							<s:property value="%{descStazioneAppaltante}"/> --%>
							<s:property value="%{stazioneAppaltante}" />							
						</c:when>
						<c:otherwise>	
							<wp:i18n key="OPT_190_CHOOSE_STRUTTURA_PROPONENTE" var="headerValueProponente" />
							<s:select name="model.proponente" id="model.proponente" list="maps['stazioniAppaltantiL190']" 
									value="%{#searchForm.proponente}" 
									headerKey="" headerValue="%{#attr.headerValueProponente}" 
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
			<div class="fieldset-row">
				<div class="label">
					<label for="model.partecipante"><wp:i18n key="LABEL_PARTECIPANTE" />: </label>
				</div>
				<div class="element">
					<s:textfield name="model.partecipante" id="model.partecipante" cssClass="text" value="%{#searchForm.partecipante}" 
											 maxlength="50" size="30" />
				</div>
			</div>
			<div class="fieldset-row last-row">
				<div class="label">
					<label for="model.aggiudicatario"><wp:i18n key="LABEL_AGGIUDICATARIO" />: </label>
				</div>
				<div class="element">
					<s:textfield name="model.aggiudicatario" id="model.aggiudicatario" cssClass="text" value="%{#searchForm.aggiudicatario}" 
											 maxlength="50" size="30" />
				</div>
			</div>	
			<div class="azioni">
				<wp:i18n key="BUTTON_SEARCH" var="valueSearchButton" />
				<s:submit value="%{#attr.valueSearchButton}" cssClass="button block-ui"/>
			</div>
		</fieldset>

		<c:if test="${listaAppalti ne null}">

			<s:if test="%{listaAppalti.size() > 0}">
			
				<s:if test="%{dataUltimoAggiornamento != null}">					
					<div class="align-right important last-update-list">
						<wp:i18n key="LABEL_LAST_UPDATE" />	<s:date name="dataUltimoAggiornamento" format="dd/MM/yyyy" />
					</div>
				</s:if> 		
			
				<div class="list-summary">
					<wp:i18n key="SEARCH_RESULTS_INTRO" /> <s:property value="%{listaAppalti.size()}"/> <wp:i18n key="SEARCH_RESULTS_OUTRO" />.
				</div>
				<div class="table-container">
					<table id="tableBandi" summary='<wp:i18n key="LABEL_190_BANDI_CONTRATTI_AGGIUDICATI_SUMMARY" />' class="info-table source-popup">
						<thead>
							<tr>
								<th scope="col"><wp:i18n key="LABEL_CIG" /></th>
								<th scope="col" style="min-width: 20em;"><wp:i18n key="LABEL_190_STRUTTURA_PROPONENTE" /></th>
								<th scope="col" style="min-width: 20em;"><wp:i18n key="LABEL_190_OGGETTO" /></th>
								<th scope="col" style="min-width: 20em;"><wp:i18n key="LABEL_190_SCELTA_CONTRAENTE" /></th>
								<th scope="col" style="min-width: 20em;"><wp:i18n key="LABEL_190_ELENCO_OPERATORI_INVITATI" /></th>
								<th scope="col" style="min-width: 20em;"><wp:i18n key="LABEL_AGGIUDICATARIO" /></th>
								<th scope="col"><wp:i18n key="LABEL_190_IMPORTO_AGGIUDICAZIONE" /></th>
								<th scope="col" style="min-width: 12em;"><wp:i18n key="LABEL_190_TEMPI" /></th>
								<th scope="col"><wp:i18n key="LABEL_190_IMPORTO_SOMME_LIQUIDATE" /></th>
							</tr>
						</thead>
						<tbody>
							<s:iterator var="riga" value="listaAppalti">
								<tr>
									<td><s:property value="cig" /></td>
									<td><s:property value="codiceFiscaleProponente" /> - <s:property value="denominazioneProponente" /></td>
									<td><s:property value="oggetto" /></td>
									<td><s:property value="sceltaContraente" /></td>
									<td>
										<ul>
											<s:iterator var="operatore" value="partecipante">
												<li>
													<s:if test="%{#operatore.codiceFiscale != null}">
														<s:property value="%{#operatore.codiceFiscale}" />
													</s:if>
													<s:else>
														<s:property value="%{#operatore.idFiscaleEstero}" />
													</s:else> - <s:property value="%{#operatore.ragioneSociale}" />
													<s:if test="%{#operatore.ruolo != null}"> con ruolo <s:property value="%{#operatore.ruolo}" /></s:if>
												</li>
											</s:iterator>
										</ul>
									</td>
									<td>
										<ul>
											<s:iterator var="operatore" value="aggiudicatario">
												<li>
													<s:if test="%{#operatore.codiceFiscale != null}">
														<s:property value="%{#operatore.codiceFiscale}" />
													</s:if>
													<s:else>
														<s:property value="%{#operatore.idFiscaleEstero}" />
													</s:else> - <s:property value="%{#operatore.ragioneSociale}" />
													<s:if test="%{#operatore.ruolo != null}"> con ruolo <s:property value="%{#operatore.ruolo}" /></s:if>
												</li>
											</s:iterator>
										</ul>
									</td>
									<td class="money-content">
										<s:if test="%{importoAggiudicazione != null}">
											<s:text name="format.money"><s:param value="importoAggiudicazione"/></s:text>
										</s:if>
									</td>
									<td class="date-content">
										<s:if test="%{dataInizio != null}">
											<wp:i18n key="LABEL_190_INIZIO" />: <s:date name="dataInizio" format="dd/MM/yyyy" /><br/>
										</s:if>
										<s:if test="%{dataUltimazione != null}">
											<wp:i18n key="LABEL_190_ULTIMAZIONE" />: <s:date name="dataUltimazione" format="dd/MM/yyyy" />
										</s:if>
									</td>
									<td class="money-content">
										<s:if test="%{importoSommeLiquidate != null}">
											<s:text name="format.money"><s:param value="importoSommeLiquidate"/></s:text>
										</s:if>
									</td>
								</tr>
							</s:iterator>
						</tbody>
					</table>
				</div>

				<c:if test="${!(skin == 'highcontrast' || skin == 'text')}">
					<a href='javascript:' title='<wp:i18n key="LINK_VIEW_TABLE_DETAIL" />' class="bkg expand-fullscreen" id="openDialog">
					</a>
				</c:if>

				<s:url id="urlExport" namespace="/do/FrontEnd/AmmTrasp" action="exportAnticorruzione">
					<s:param name="last" value="1"></s:param>
				</s:url>

				<p>
					<a href='<s:property value="%{#urlExport}" />&amp;${tokenHrefParams}' class="important">
						<wp:i18n key="LINK_EXPORT_CSV" />
					</a>
				</p>
				<c:if test="${visExportPdf}">
				<p>
					<a href='javascript:' id="hrefExportPdf" class="important">
						<wp:i18n key="LINK_EXPORT_PDF" />
					</a>
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
	
	<div class="back-link">
		<a href="<wp:action path="/ExtStr2/do/FrontEnd/AmmTrasp/listaAdempimentiAnticorruzione.action"/>&amp;${tokenHrefParams}">
			<wp:i18n key="LINK_BACK_TO_YEAR" />
		</a>
	</div> 
	
</div>


<script type="text/javascript">

		$("#hrefExportPdf").on("click", function(){
	        var doc = new jsPDF('l');
			var elem = document.getElementById("tableBandi");	 		
	 		var res = doc.autoTableHtmlToJson(elem);    
	 		doc.autoTable(res.columns, res.data, {
	 			theme: 'grid',
	 			bodyStyles: {
	 				valign: 'top'
	 			},
	 			headStyles: {
	 				fontSize: 9,
	 				fontStyle: 'bold',
	 				halign: 'center',
	 				textColor: [0,0,0],
	 				fillColor: [167,191,217]
	 			},
	 			columnStyles: {
				    0: {cellWidth: 20},
				    1: {cellWidth: 35},
				    2: {cellWidth: 35},
				    3: {cellWidth: 35},
				    4: {cellWidth: 40},
				    5: {cellWidth: 40},
				    6: {cellWidth: 20, halign: 'right'},
				    7: {cellWidth: 20},
				    8: {cellWidth: 20, halign: 'right'}
				},
	 			styles: {
	 				fontStyle: 'normal',
	 				overflow: 'linebreak',
	 				cellPadding: 1, 
	 				fontSize: 8 
	 			}
	 		});
	        doc.save("export.pdf");
		});
</script>