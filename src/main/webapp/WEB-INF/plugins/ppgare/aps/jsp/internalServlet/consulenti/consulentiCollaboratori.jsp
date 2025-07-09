<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="es"   uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>

<s:set name="helper" value="%{#session.consulentiCollaboratori}" />

<es:getAppParam name = "denominazioneStazioneAppaltanteUnica" var = "stazAppUnica" scope = "page"/> 	
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
			
			$('#tableConsulentiCollaboratori').dataTable({
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

	<h2><wp:i18n key="TITLE_PAGE_CONSULENTI_COLLABORATORI" /></h2>
	<p>
		<wp:i18n key="BALLOON_CONSULENTI_COLLABORATORI" />
	</p>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
	
	<s:url id="urlDownloadAllegato" namespace="/do/FrontEnd/Consulenti" action="downloadDocumenti" />

	<%-- MASCHERA DI RICERCA --%>
	<form class="form-ricerca" action="<wp:action path="/ExtStr2/do/FrontEnd/Consulenti/find.action" />" method="post" >
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
		
		<fieldset >
			<legend>
				<wp:i18n key="LABEL_SEARCH_CRITERIA" />
			</legend>
			<div class="fieldset-row  first-row">
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
									value="%{#helper.stazioneAppaltante}" 
									headerKey="" headerValue="%{#attr.headerValueStazioneAppaltante}" 
									cssStyle="width: 100%;" >
							</s:select>
						</c:otherwise>
					</c:choose>
				</div>
			</div>
			<div class="fieldset-row">
				<div class="label">
					<label for="model.soggettoPercettore"><wp:i18n key="LABEL_SOGGETTO_PERCETTORE" /> : </label>
				</div>
				<div class="element">
					<s:textfield name="model.soggettoPercettore" id="model.soggettoPercettore" value="%{#helper.soggettoPercettore}" maxLength="100" cssClass="text" style="width: 100%"/>
				</div>
			</div>
			<div class="fieldset-row">
				<div class="label"><wp:i18n key="LABEL_DATA_PROVVEDIMENTO" /> : </div>
				<div class="element">
					<wp:i18n key="LABEL_DATA_PROVVEDIMENTO" var="headerValueData"/>
					<wp:i18n key="LABEL_DA_DATA" var="headerValueDal" />
					<wp:i18n key="LABEL_A_DATA" var="headerValueAl" />
					<label><wp:i18n key="LABEL_DA_DATA" />: </label>
					<s:textfield name="model.dataDa" id="model.dataDa" cssClass="text"
								value="%{#helper.dataDa}"   
								title="%{#attr.headerValueData} %{#attr.headerValueDal}"
								maxLength="10" size="10" />
					<label><wp:i18n key="LABEL_A_DATA" />: </label>
					<s:textfield name="model.dataA" id="model.dataA" cssClass="text"
								value="%{#helper.dataA}"   
								title="%{#attr.headerValueData} %{#attr.headerValueAl}" 
								maxLength="10" size="10" />
					(<wp:i18n key="LABEL_FORMATO_DATA" />)
				</div>
			</div>
			<div class="fieldset-row">
				<div class="label">
					<label for="model.ragioneIncarico"><wp:i18n key="LABEL_RAGIONE_INCARICO" /> : </label>
				</div>
				<div class="element">
					<s:textfield name="model.ragioneIncarico" id="model.ragioneIncarico" value="%{#helper.ragioneIncarico}" cssClass="text" />
				</div>
			</div>
			<div class="fieldset-row last-row">
				<div class="label"><wp:i18n key="LABEL_COMPENSO_PREVISTO" /> : </div>
				<div class="element">
					<wp:i18n key="LABEL_COMPENSO_PREVISTO" var="headerValueCompenso"/>
					<wp:i18n key="LABEL_A_PARTIRE_DA" var="headerValueDa" />
					<wp:i18n key="LABEL_FINO_A" var="headerValueA" />
					<label><wp:i18n key="LABEL_A_PARTIRE_DA" />: </label>
					<s:textfield name="model.compensoPrevistoDa" id="model.compensoPrevistoDa" 
								value="%{#helper.compensoPrevistoDa}" 
								title="%{#attr.headerValueCompenso} %{#attr.headerValueDa}"
								maxLength="10" size="10" cssClass="text" />
					<label><wp:i18n key="LABEL_FINO_A" />: </label>
					<s:textfield name="model.compensoPrevistoA" id="model.compensoPrevistoA" 
								value="%{#helper.compensoPrevistoA}" 
								title="%{#attr.headerValueCompenso} %{#attr.headerValueA}"
								maxLength="10" size="10" cssClass="text" /> 
					(&euro;)
				</div>
			</div>
			
			<div class="azioni">
				<wp:i18n key="BUTTON_SEARCH" var="valueSearchButton" />
				<s:submit value="%{#attr.valueSearchButton}" cssClass="button"></s:submit>
			</div>
		</fieldset>
		
		<!-- LISTA RISULTATI RICERCA -->
		<c:if test="${lista ne null}">
		 	<s:if test="%{lista.size() > 0}">
				<div class="table-container">
					<table id="tableConsulentiCollaboratori" class="info-table">
					<caption style="display:none;">Tabella consulenti e collaboratori</caption>
						<thead>
							<tr>
								<th scope="col"><wp:i18n key="LABEL_SOGGETTO_PERCETTORE" /></th>
								<th scope="col"><wp:i18n key="LABEL_NUMERO_PROVVEDIMENTO" /></th>
								<th scope="col"><wp:i18n key="LABEL_DATA_PROVVEDIMENTO" /></th>
								<th scope="col"><wp:i18n key="LABEL_RAGIONE_INCARICO" /></th>
								<th scope="col"><wp:i18n key="LABEL_OGGETTO_PRESTAZIONE" /></th>
								<th scope="col"><wp:i18n key="LABEL_COMPENSO_PREVISTO" /></th>
								<th scope="col"><wp:i18n key="LABEL_PARTE_VARIABILE_COMPENSO" /></th>
								<th scope="col"><wp:i18n key="LABEL_INIZIO_INCARICO" /></th>
								<th scope="col"><wp:i18n key="LABEL_FINE_INCARICO" /></th>
								<th scope="col"><wp:i18n key="LABEL_TIPO_PROCEDURA" /></th>
								<th scope="col"><wp:i18n key="LABEL_NUM_PARTECIPANTI" /></th>
								<th scope="col"><wp:i18n key="LABEL_STAZIONE_APPALTANTE" /></th>
								<th scope="col"><wp:i18n key="LABEL_FILE_ALLEGATI" /></th>
							</tr>
						</thead>
						<tbody>
							<wp:i18n key="LABEL_SCARICA_ALLEGATO" var="valueScaricaAllegato" />
							<s:iterator var="riga" value="lista">
								<tr>
									<td><s:property value="soggettoPercettore" /></td>
									<td><s:property value="protocollo" /></td>
									<td><s:date name="data" format="dd/MM/yyyy" /></td>
									<td><s:property value="ragioneIncarico" /></td>
									<td><s:property value="oggettoPrestazione" /></td>
									<td class="money-content">
										<s:if test="%{compensoPrevisto != null}">
											<s:text name="format.money"><s:param value="compensoPrevisto" /></s:text>
										</s:if>
									</td>
									<td><s:property value="parteVariabile" /></td>
									<td class="date-content"><s:date name="dataDa" format="dd/MM/yyyy" /></td>
									<td class="date-content"><s:date name="dataA" format="dd/MM/yyyy" /></td>
									<td><s:property value="tipoProcedura" /></td>
									<td><s:property value="numeroPartecipanti" /></td>
									<td>
										<c:choose>
											<c:when test="${! empty stazAppUnica }">
												<s:set var="stazAppUnicaToStruts">${stazAppUnica}</s:set>
												<s:property value="stazAppUnicaToStruts" />
											</c:when>
											<c:otherwise>
												<s:property value="stazioneAppaltante" />
											</c:otherwise>
										</c:choose>
									</td>
									<td class="azioni">
										<c:if test="${not empty url}">
											<c:choose>
												<c:when test="${skin == 'highcontrast' || skin == 'text'}">
													<s:a href="%{#urlDownloadAllegato}?codice=%{codice}&codiceSoggetto=%{codiceSoggetto}" title="%{#attr.valueScaricaAllegato}">
														<wp:i18n key="LABEL_SCARICA_ALLEGATO" />
													</s:a>
												</c:when>
												<c:otherwise>
													<s:a href="%{#urlDownloadAllegato}?codice=%{codice}&codiceSoggetto=%{codiceSoggetto}" cssClass="bkg zip" title="%{#attr.valueScaricaAllegato}">
													</s:a>
												</c:otherwise>
											</c:choose>
										</c:if>
									</td> 
								</tr>
							</s:iterator>
						</tbody>
					</table>
				</div>
				<s:url id="urlExport" namespace="/do/FrontEnd/Consulenti" action="export">
					<s:param name="last" value="1"></s:param>
				</s:url>
				<p>
					<a href='<s:property value="%{#urlExport}" />' class="important">
						<wp:i18n key="LINK_EXPORT_CSV" />
					</a>
				</p>
			</s:if>
			<s:else>
				<div class="list-summary">
					<wp:i18n key="SEARCH_NOTHING_FOUND" />
				</div>
			</s:else>
			<br/>
		</c:if>
		
		<p>
			<wp:i18n key="LABEL_RIFERIMENTI_NORMATIVI" /><br/>
			<span class="important"><wp:i18n key="LABEL_DLG_33_2013_ART_15" /></span>
		</p> 
	</form>	
</div>

