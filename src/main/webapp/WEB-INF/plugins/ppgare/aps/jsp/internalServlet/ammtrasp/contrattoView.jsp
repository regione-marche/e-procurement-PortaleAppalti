<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>

<es:checkCustomization var="visImportoContratto" objectId="AMMTRASP-RIEPCONTRATTI" attribute="IMPCONTRATTO" feature="VIS" />


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>
								
<div class="portgare-view">

	<h2><wp:i18n key="TITLE_PAGE_190_DETTAGLIO_CONTRATTO" /></h2>
	
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
 		<jsp:param name="keyMsg" value="BALLOON_AMMTRASP_CONTRATTO_SCHEDA"/>	
	</jsp:include>
	
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
 
	<fieldset>
		<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="TITLE_PAGE_DETTAGLIO_GARA" /></legend>
	
		<div class="fieldset-row first-row">
			<div class="label">
				<label><wp:i18n key="LABEL_CIG" /> : </label>
			</div>
			<div class="element">
				<span>
					<s:property value="model.cig"/>
				</span>
			</div>
		</div>
		
		<div class="fieldset-row">
			<div class="label">
				<label><wp:i18n key="LABEL_190_OGGETTO" /> : </label>
			</div>
			<div class="element">
				<span>
					<s:property value="model.oggetto"/>
				</span>
			</div>
		</div>

		<div class="fieldset-row">
			<div class="label">
				<label><wp:i18n key="LABEL_190_STRUTTURA_PROPONENTE" /> : </label>
			</div>
			<div class="element">
				<span>
					<s:property value="model.codicefiscaleProponente"/> - <s:property value="model.denominazioneProponente"/>
				</span>
			</div>
		</div>	
			
		<div class="fieldset-row">
			<div class="label">
				<label><wp:i18n key="LABEL_190_SCELTA_CONTRAENTE" /> : </label>
			</div>
			<div class="element">
				<span>
					<s:property value="model.sceltaContraente"/>
				</span>
			</div>
		</div>
		 
		<div class="fieldset-row">
			<div class="label">
				<label><wp:i18n key="LABEL_ELENCO_OPERATORI" /> : </label>
			</div>
			<div class="element">
				<s:if test="%{model.elencoOperatoriInvitati.size() > 0}">
					<table class="light-table bordered-table">
						<thead>
							<tr>
								<th><wp:i18n key="LABEL_RAGIONE_SOCIALE" /></th>
								<th><wp:i18n key="LABEL_CODICE_FISCALE" /></th>
								<th><wp:i18n key="LABEL_PARTITA_IVA" /></th>
								<th><wp:i18n key="LABEL_RTI" /></th>
							</tr>
						</thead>
						<tbody>
							<s:iterator value="model.elencoOperatoriInvitati" var="operatore">
								<tr>
									<td><s:property value="#operatore.ragioneSociale"/></td>
									<td><s:property value="#operatore.codiceFiscale"/></td>
									<td><s:property value="#operatore.partitaIva"/></td>
									<td>
										<s:if test="%{#operatore.componentiRti.length > 0}">
											<ul>
												<s:iterator value="#operatore.componentiRti" var="item">
													<li>
														<s:property value="#item.ragioneSociale"/>
														
														<c:if test="${ not empty item.codiceFiscale }">	
															&nbsp;<wp:i18n key="LABEL_CODICE_FISCALE_ABBR" /> <s:property value="#item.codiceFiscale"/>
														</c:if>
														<c:if test="${ not empty item.partitaIva }">
															&nbsp;<wp:i18n key="LABEL_PARTITA_IVA_ABBR" /> <s:property value="#item.partitaIva"/>
														</c:if>
														<c:if test="${ item.mandataria }">	
															<wp:i18n key="LABEL_MANDATARIA" var="labelMandataria"/>
															&nbsp;<strong>(${fn:toLowerCase(attr.labelMandataria)})</strong>
														</c:if>
													</li>
												</s:iterator>
											</ul>
										</s:if>
									</td>
								</tr>
							</s:iterator>
						</tbody>
					</table>
				</s:if>
				<s:else><wp:i18n key="LABEL_NOT_DEFINED" /></s:else>
			</div>
		</div>

		<div class="fieldset-row">
			<div class="label">
				<label><wp:i18n key="LABEL_AGGIUDICATARIO" /> : </label>
			</div>
			<div class="element">
				
				<s:if test="model.pivaAggiudicataria.length>=2">
					<table class="light-table bordered-table">
							<thead>
								<tr>
									<th><wp:i18n key="LABEL_RAGIONE_SOCIALE" /></th>
									<th><wp:i18n key="LABEL_CODICE_FISCALE" /></th>
									<th><wp:i18n key="LABEL_PARTITA_IVA" /></th>
									<th><wp:i18n key="LABEL_RTI" /></th>
								</tr>
							</thead>
							<tbody>
								<s:iterator value="model.ragioneSocialeAggiudicataria" status="status"> 
									<tr>
	 									<td><s:property value="model.ragioneSocialeAggiudicataria[#status.index]"/></td>
										<td><s:property value="model.codiceFiscaleAggiudicataria[#status.index]"/></td>
										<td><s:property value="model.pivaAggiudicataria[#status.index]"/></td>
										<td>
											<ul>
												<s:iterator value="model.rtiAggiudicataria[#status.index].componentiRti" status="s">
													<li>
														<s:property value="model.rtiAggiudicataria[#status.index].componentiRti[#s.index].ragioneSociale"/>&nbsp;
														<wp:i18n key="LABEL_CODICE_FISCALE_ABBR" /> <s:property value="model.rtiAggiudicataria[#status.index].componentiRti[#s.index].codiceFiscale"/>&nbsp;
														<wp:i18n key="LABEL_PARTITA_IVA_ABBR" /> <s:property value="model.rtiAggiudicataria[#status.index].componentiRti[#s.index].partitaIva"/>&nbsp;
														<s:if test="model.rtiAggiudicataria[#status.index].componentiRti[#s.index].mandataria">
															<wp:i18n key="LABEL_MANDATARIA" var="labelMandataria"/>
															&nbsp;<strong>(${fn:toLowerCase(attr.labelMandataria)})</strong>
														</s:if>
													</li>
												</s:iterator>
											</ul>
										</td>
									</tr>
								</s:iterator>
							</tbody>
						</table>
					</s:if> 
				<s:else>	
					<s:if test="%{model.rtiAggiudicataria[0].componentiRti.length > 0}">
					<strong><wp:i18n key="LABEL_RTI_AGGIUDICATARIA" /> : </strong>
					<ul><li><s:property value="model.ragioneSocialeAggiudicataria[0]"/></li></ul>
					<br/>
					<strong><wp:i18n key="LABEL_COMPONENTI" />:</strong>
						<ul>
							<s:iterator value="model.rtiAggiudicataria[0].componentiRti" var="item" status="state">
								<li>
									<s:property value="#item.ragioneSociale"/>
									<c:if test="${ not empty item.codiceFiscale }">	
										&nbsp;<wp:i18n key="LABEL_CODICE_FISCALE_ABBR" /> <s:property value="#item.codiceFiscale"/>
									</c:if>
									<c:if test="${ not empty item.partitaIva }">
										&nbsp;<wp:i18n key="LABEL_PARTITA_IVA_ABBR" /> <s:property value="#item.partitaIva"/>
									</c:if>
									<c:if test="${ item.mandataria }">
										<wp:i18n key="LABEL_MANDATARIA" var="labelMandataria"/>
										&nbsp;<strong>(${fn:toLowerCase(attr.labelMandataria)})</strong>
									</c:if>
								</li>
							</s:iterator>
						</ul>
					</s:if>	
					<s:else>
						<s:property value="model.ragioneSocialeAggiudicataria"/>&nbsp;
						<s:if test="%{model.codiceFiscaleAggiudicataria[0] != null}">C.F. <s:property value="model.codiceFiscaleAggiudicataria"/></s:if>&nbsp;
						<s:if test="%{model.pivaAggiudicataria[0] != null}">P.IVA <s:property value="model.pivaAggiudicataria"/></s:if>
					</s:else>
				</s:else>
			</div>
		</div>
		
		<div class="fieldset-row">
			<div class="label">
				<label><wp:i18n key="LABEL_190_IMPORTO_AGGIUDICAZIONE" /> : </label>
			</div>
			<div class="element">
				<span>
					<c:if test="${ not empty model.importoAggiudicazione }">
						<s:text name="format.money"><s:param value="model.importoAggiudicazione"/></s:text>
					</c:if>
				</span>
			</div>
		</div>

		<c:if test="${visImportoContratto}">
			<div class="fieldset-row">
				<div class="label">
					<label><wp:i18n key="LABEL_190_IMPORTO_CONTRATTO" /> : </label>
				</div>
				<div class="element">
					<span>
						<c:if test="${ not empty model.importoContratto }">
							<s:text name="format.money"><s:param value="model.importoContratto"/></s:text>
						</c:if>
					</span>
				</div>
			</div>
		</c:if>
	
		<div class="fieldset-row">
			<div class="label">
				<label><wp:i18n key="LABEL_190_TEMPI" /> : </label>
			</div>
			<div class="element">
				<span>
					<c:if test="${ not empty model.dataInizio }">
						<wp:i18n key="LABEL_190_INIZIO" /> <s:date name="model.dataInizio" format="dd/MM/yyyy"/>
						<br/>
					</c:if>
					<c:if test="${ not empty model.dataUltimazione }">
						<wp:i18n key="LABEL_190_ULTIMAZIONE" /> <s:date name="model.dataUltimazione" format="dd/MM/yyyy"/>
					</c:if>
			    </span>
			</div>
		</div>
		
		<div class="fieldset-row">
			<div class="label">
				<label><wp:i18n key="LABEL_190_IMPORTO_SOMME_LIQUIDATE" /> : </label>
			</div>
			<div class="element">
				<span>
					<c:if test="${ not empty model.importoSommeLiquidate }">
						<s:text name="format.money"><s:param value="model.importoSommeLiquidate"/></s:text>
					</c:if>
				</span>
			</div>
		</div>
		
		<c:if test="${not empty model.importoAggiudicazione and not empty model.importoSommeLiquidate}">
		<div class="fieldset-row">
			<div class="label">
				<label><wp:i18n key="LABEL_IMPORTO_SCOSTAMENTO" /> : </label>
			</div>
			<div class="element">
				<span>
					<s:set var="importoScostamento" value="%{model.importoAggiudicazione - model.importoSommeLiquidate}"/>
					<s:text name="format.money"><s:param value="%{#importoScostamento}"/></s:text>
				</span>
			</div>
		</div>
		</c:if>

	</fieldset>

	<div class="back-link">
		<a href='<wp:action path="/ExtStr2/do/FrontEnd/AmmTrasp/searchContratti.action" />&amp;last=1'>
			<wp:i18n key="LINK_BACK_TO_LIST" />
		</a>
	</div> 
</div>