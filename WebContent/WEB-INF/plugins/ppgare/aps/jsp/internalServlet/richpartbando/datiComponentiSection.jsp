<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>

<s:set var="sessionId">${param.sessionIdObj}</s:set>
<s:set var="invioOfferta">${sessionScope.dettPartecipGara.tipoEvento == 2}</s:set>
<s:set var="garaRistretta">${sessionScope.dettaglioGara.datiGeneraliGara.iterGara == 2}</s:set>

<s:hidden id="quotaRTIVisibile" name="quotaRTIVisibile" value='%{#session[#sessionId].rti && #sessionId == "dettPartecipGara"}' />
<s:hidden id="quotaVisibile" name="quotaVisibile" value='%{#session[#sessionId].rti && #sessionId == "dettPartecipGara"}' />

<script type="text/javascript">

var optPartitaIVALiberoProfessionista;
var codiciLiberoProfessionista;
var optPartitaIVAImpresaSociale;
var codiciImpresaSociale;

	<!--//--><![CDATA[//><!--
		$(document).ready(function() {
			
			<c:set var="cfg" value=""/>
			 <c:forEach items="${maps['checkPILiberoProf']}" var="currentItem">
				<c:set var="cfg" value="${currentItem.value}" />
			</c:forEach>

			optPartitaIVALiberoProfessionista = "<c:out value="${cfg}"/>";

			 <c:set var="codici" value="#"/> 
			 <c:forEach items="${maps['tipiImpresaLiberoProf']}" var="currentItem">
				<c:set var="codici" value="${codici}${currentItem.key}#" />
			</c:forEach>

			codiciLiberoProfessionista = "<c:out value="${codici}"/>"; 

			<c:set var="cfg" value=""/>
			<c:forEach items="${maps['checkPIImprSociale']}" var="currentItem">
				<c:set var="cfg" value="${currentItem.value}" />
			</c:forEach>

			optPartitaIVAImpresaSociale = "<c:out value="${cfg}"/>";

			<c:set var="codici" value="#"/> 
			<c:forEach items="${maps['tipiImpresaSociale']}" var="currentItem">
				<c:set var="codici" value="${codici}${currentItem.key}#" />
			</c:forEach>

			codiciImpresaSociale = "<c:out value="${codici}"/>"; 
			
			ivaRequired();
			
			// gestisci la selezione della tipologia 'Libero professionista'
			$('#tipoImpresa').on('change', function() {
				ivaRequired();
			});
		});
		
		function ivaRequired() {
			var labelIVA = $('#labelIVA');
			labelIVA.find('span').remove(".required-field");
			var codiceCombo = "#"+$('[id="tipoImpresa"] option:selected').val()+"#";
			if(codiciLiberoProfessionista.indexOf(codiceCombo) >= 0 && optPartitaIVALiberoProfessionista=="1" ){
				labelIVA.find('span').remove(".required-field");
			} else if(codiciImpresaSociale.indexOf(codiceCombo) >= 0 && optPartitaIVAImpresaSociale=="1" ){
				labelIVA.find('span').remove(".required-field");
			}else{
				labelIVA.append('<span class="required-field">*</span>');
			} 
		}
	//--><!]]>
</script>

<s:if test="%{#session[#sessionId].rti}">
	<c:set var="deleteSentence"><wp:i18n key='LABEL_QUESTION_CONFIRM_DEL_MANDANTE'/></c:set>
	<c:set var="skipLista"><wp:i18n key='LABEL_SKIP_MANDANTI'/></c:set>
	<c:set var="fieldsetTableTitle"><wp:i18n key='LABEL_ELENCO_MANDANTI'/></c:set>
	<c:set var="titoloTabella"><wp:i18n key='LABEL_ELENCO_MANDANTI'/></c:set>
	<c:set var="modifyTitle"><wp:i18n key='LABEL_MODIFICA_MANDANTE'/></c:set>
	<c:set var="deleteTitle"><wp:i18n key='LABEL_ELIMINA_MANDANTE'/></c:set>
	<wp:i18n key='LABEL_MANDANTE' var="labelMandante"/>
	<c:set var="obj" value="${fn:toLowerCase(attr.labelMandataria)}" />
</s:if>
<s:elseif test="%{#session[#sessionId].impresa.consorzio}">
	<c:set var="deleteSentence"><wp:i18n key='LABEL_QUESTION_CONFIRM_DEL_CONSORZIATA'/></c:set>
	<c:set var="skipLista"><wp:i18n key='LABEL_SKIP_CONSORZIATE'/></c:set>
	<c:set var="fieldsetTableTitle"><wp:i18n key='LABEL_ELENCO_CONSORZIATE'/></c:set>
	<c:set var="titoloTabella"><wp:i18n key='LABEL_ELENCO_CONSORZIATE'/></c:set>
	<c:set var="modifyTitle"><wp:i18n key='LABEL_MODIFICA_CONSORZIATE'/></c:set>
	<c:set var="deleteTitle"><wp:i18n key='LABEL_ELIMINA_CONSORZIATE'/></c:set>
	<wp:i18n key='LABEL_CONSORZIATA' var="labelConsorziata"/>
	<c:set var="obj" value="${fn:toLowerCase(attr.labelConsorziata)}" />
</s:elseif>
<s:else>
	<c:set var="deleteSentence"><wp:i18n key='LABEL_QUESTION_CONFIRM_DEL_COMPONENTE'/></c:set>
	<c:set var="skipLista"><wp:i18n key='LABEL_SKIP_COMPONENTE'/></c:set>
	<c:set var="fieldsetTableTitle"><wp:i18n key='LABEL_ELENCO_COMPONENTE'/></c:set>
	<c:set var="titoloTabella"><wp:i18n key='LABEL_ELENCO_COMPONENTE'/></c:set>
	<c:set var="modifyTitle"><wp:i18n key='LABEL_MODIFICA_COMPONENTE'/></c:set>
	<c:set var="deleteTitle"><wp:i18n key='LABEL_ELIMINA_COMPONENTE'/></c:set>
	<wp:i18n key='LABEL_COMPONENTE' var="labelComponente"/>
	<c:set var="obj" value="${fn:toLowerCase(attr.labelComponente)}" />
</s:else>

<s:if test="%{delete}">
	<s:set var="comp"  value="%{#session[#sessionId].componenti.get(idDelete)}"/>
	<p class="question">${deleteSentence} <s:property value="%{#comp.ragioneSociale}"/>?</p>
	<div class="azioni">
		<s:hidden name="idDelete" value="%{idDelete}"/>
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_confirm_delete.jsp" />
	</div>
</s:if>
<s:elseif test="%{confirmNoConsorziate}">
	<p class="question"><wp:i18n key='LABEL_QUESTION_CONTINUE_WITHOUT_CONSORZIATA'/></p>
	<%-- si inseriscono dei valori fittizi per far trovare i campi all'xml di validazione --%>
	<input type="hidden" name="ragioneSociale" />
	<input type="hidden" name="tipoImpresa" />
	<input type="hidden" name="nazione" value="*" />
	<input type="hidden" name="codiceFiscale" />
	<input type="hidden" name="partitaIVA" />
</s:elseif>
<s:else>

	<p class="noscreen">[ <a href="#" >${skipLista}</a> ]
	</p>
	<s:if test="%{#session[#sessionId].rti}">

		<fieldset>
			<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_MANDATARIA" /></legend>

			<div class="fieldset-row first-row">
				<div class="label">
					<label><wp:i18n key="LABEL_RAGIONE_SOCIALE" /> : </label>
				</div>
				<div class="element">
					<s:property value="%{#session[#sessionId].datiPrincipaliImpresa.ragioneSociale}" />
				</div>
			</div>

			<div class="fieldset-row">
				<div class="label">
					<label><wp:i18n key="LABEL_NAZIONE" /> : </label>
				</div>
				<div class="element">
					<s:property value="%{#session[#sessionId].datiPrincipaliImpresa.nazioneSedeLegale}"/>
				</div>
			</div>

			<div class="fieldset-row">
				<div class="label">
					<label><wp:i18n key="LABEL_CODICE_FISCALE" /> : </label>
				</div>
				<div class="element">
					<s:property value="%{#session[#sessionId].datiPrincipaliImpresa.codiceFiscale}"/>
				</div>
			</div>

			<div class="fieldset-row">
				<div class="label">
					<label><wp:i18n key="LABEL_PARTITA_IVA" /> :</label>
				</div>
				<div class="element">
					<s:property value="%{#session[#sessionId].datiPrincipaliImpresa.partitaIVA}"/>
				</div>
			</div>

			<s:if test='%{#sessionId == "dettPartecipGara"}'>
				<div id="divQuota" class="fieldset-row last-row">
					<div class="label">
						<label for="strQuotaRTI"><wp:i18n key="LABEL_QUOTA_PARTECIPAZIONE" /> : <span class="required-field">*</span></label>
					</div>
					<div class="element">
						<s:textfield name="strQuotaRTI" id="strQuotaRTI" value="%{quotaRTI != null ? quotaRTI : strQuotaRTI}" 
									maxlength="6" size="15" 
									readonly="%{!#session.dettPartecipGara.editRTI && !(#garaRistretta && #invioOfferta)}" 
									cssClass="%{#classBlocco}" 
									aria-required="true" />
					</div>
				</div>
			</s:if>

		</fieldset>
	</s:if>

	<s:if test="%{#session[#sessionId].componenti.size() >= 0 || #session[#sessionId].componentiRTI.size() >= 0}">

		<fieldset>
			<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span>${fieldsetTableTitle}</legend>
			<table class="wizard-table" summary="${titoloTabella}">
				<tr>
					<th scope="col"><wp:i18n key="LABEL_RAGIONE_SOCIALE" /></th>
					<th scope="col"><wp:i18n key="LABEL_CODICE_FISCALE" /></th>
					<th scope="col"><wp:i18n key="LABEL_PARTITA_IVA" /></th>
					<s:if test='%{#session[#sessionId].rti && #sessionId == "dettPartecipGara"}'>
						<th scope="col">Quota</th>
					</s:if>
					<th scope="col"><wp:i18n key="ACTIONS" /></th>
				</tr>

				<s:if test='%{#session[#sessionId].rti}'>
					<s:if test='%{#sessionId != "dettPartecipGara"}'>
						<s:iterator  value="%{#session[#sessionId].componentiRTI}" status="status">
							<c:if test="${status.index != 0}">
								<tr>
									<td><s:property value="%{ragioneSociale}"/></td>
									<td><s:property value="%{codiceFiscale}"/></td>
									<td><s:property value="%{partitaIVA}"/></td>
									<%-- 
									<s:if test='%{#session[#sessionId].rti && #sessionId == "dettPartecipGara"}'>
										<td><s:property value="%{quota}"/></td>
									</s:if> 
									--%>
									<td class="azioni">
										<ul>
											<c:choose>
												<c:when test="${skin == 'highcontrast' || skin == 'text'}">
													<li>
														<a href='<wp:action path="/ExtStr2/do/FrontEnd/${param.namespace}/modifyComponente.action"/>&amp;id=${status.index}&amp;ext=${param.ext}&amp;${tokenHrefParams}' 
															 title="${modifyTitle}">${modifyTitle}</a>
													</li>
													<li>
														<a href='<wp:action path="/ExtStr2/do/FrontEnd/${param.namespace}/confirmDeleteComponente.action"/>&amp;idDelete=${status.index}&amp;ext=${param.ext}&amp;${tokenHrefParams}' 
															 title="${deleteTitle}">${deleteTitle}</a>
													</li>
												</c:when>
												<c:otherwise>
													<li>
														<a href='<wp:action path="/ExtStr2/do/FrontEnd/${param.namespace}/modifyComponente.action"/>&amp;id=${status.index}&amp;ext=${param.ext}&amp;${tokenHrefParams}' 
															 title="${modifyTitle}" class="bkg modify"></a>
													</li>
													<li>
														<a href='<wp:action path="/ExtStr2/do/FrontEnd/${param.namespace}/confirmDeleteComponente.action"/>&amp;idDelete=${status.index}&amp;ext=${param.ext}&amp;${tokenHrefParams}' 
															 title="${deleteTitle}" class="bkg delete"></a>
													</li>
												</c:otherwise>
											</c:choose>
										</ul>
									</td>
								</tr>
							</c:if>
						</s:iterator>
					</s:if>
					<s:else>
						<s:iterator value="%{#session[#sessionId].componenti}" status="status">
							<tr>
								<td><s:property value="%{ragioneSociale}"/></td>
								<td><s:property value="%{codiceFiscale}"/></td>
								<td><s:property value="%{partitaIVA}"/></td>
								<td><s:property value="%{quota}"/></td>
								<td class="azioni">
									<ul>
										<c:choose>
											<c:when test="${skin == 'highcontrast' || skin == 'text'}">
												<li>
													<a href='<wp:action path="/ExtStr2/do/FrontEnd/${param.namespace}/modifyComponente.action"/>&amp;id=${status.index}&amp;ext=${param.ext}&amp;${tokenHrefParams}' 
														 title="${modifyTitle}">${modifyTitle}</a>
												</li>
												<li>
													<a href='<wp:action path="/ExtStr2/do/FrontEnd/${param.namespace}/confirmDeleteComponente.action"/>&amp;idDelete=${status.index}&amp;ext=${param.ext}&amp;${tokenHrefParams}' 
														 title="${deleteTitle}">${deleteTitle}</a>
												</li>
											</c:when>
											<c:otherwise>
												<li>
													<a href='<wp:action path="/ExtStr2/do/FrontEnd/${param.namespace}/modifyComponente.action"/>&amp;id=${status.index}&amp;ext=${param.ext}&amp;${tokenHrefParams}' 
														 title="${modifyTitle}" class="bkg modify"></a>
												</li>
												<li>
													<a href='<wp:action path="/ExtStr2/do/FrontEnd/${param.namespace}/confirmDeleteComponente.action"/>&amp;idDelete=${status.index}&amp;ext=${param.ext}&amp;${tokenHrefParams}' 
														 title="${deleteTitle}" class="bkg delete"></a>
												</li>
											</c:otherwise>
										</c:choose>
									</ul>
								</td>
							</tr>
						</s:iterator>
					</s:else>
				</s:if>
				<s:else>
					<s:iterator value="#session[#sessionId].componenti" status="status">
						<tr>
							<td><s:property value="%{ragioneSociale}"/></td>
							<td><s:property value="%{codiceFiscale}"/></td>
							<td><s:property value="%{partitaIVA}"/></td>
							<td class="azioni">
								<ul>
									<c:choose>
										<c:when test="${skin == 'highcontrast' || skin == 'text'}">
											<li>
												<a href='<wp:action path="/ExtStr2/do/FrontEnd/${param.namespace}/modifyComponente.action"/>&amp;id=${status.index}&amp;ext=${param.ext}&amp;${tokenHrefParams}' 
													 title="${modifyTitle}">${modifyTitle}</a>
											</li>
											<li>
												<a href='<wp:action path="/ExtStr2/do/FrontEnd/${param.namespace}/confirmDeleteComponente.action"/>&amp;idDelete=${status.index}&amp;ext=${param.ext}&amp;${tokenHrefParams}' 
													 title="${deleteTitle}">${deleteTitle}</a>
											</li>
										</c:when>
										<c:otherwise>
											<li>
												<a href='<wp:action path="/ExtStr2/do/FrontEnd/${param.namespace}/modifyComponente.action"/>&amp;id=${status.index}&amp;ext=${param.ext}&amp;${tokenHrefParams}' 
													 title="${modifyTitle}" class="bkg modify"></a>
											</li>
											<li>
												<a href='<wp:action path="/ExtStr2/do/FrontEnd/${param.namespace}/confirmDeleteComponente.action"/>&amp;idDelete=${status.index}&amp;ext=${param.ext}&amp;${tokenHrefParams}' 
													 title="${deleteTitle}" class="bkg delete"></a>
											</li>
										</c:otherwise>
									</c:choose>
								</ul>
							</td>
						</tr>
					</s:iterator>
				</s:else> 
			</table>
		</fieldset>
	</s:if>

	<p class="noscreen">[ <a href="#" id="formComponente"><wp:i18n key="SKIP_TO_FORM_BUTTONS"/></a> ]</p>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/mandatory_fields_message.jsp" />

	<fieldset>
		<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span>
			<c:choose>
				<c:when test="${empty id}">
					<wp:i18n key="LABEL_NUOVA" /> ${obj}
				</c:when>
				<c:otherwise>
					<wp:i18n key="BUTTON_EDIT" /> ${obj}
				</c:otherwise>
			</c:choose>
		</legend>

		<div class="fieldset-row first-row">
			<div class="label">
				<label for="ragioneSociale"><wp:i18n key="LABEL_RAGIONE_SOCIALE" /> : <span class="required-field">*</span></label>
			</div>
			<div class="element">
				<s:textfield name="ragioneSociale" id="ragioneSociale" value="%{ragioneSociale}" 
							 size="60" maxlength="2000" aria-required="true" />
			</div>
		</div>

		<div class="fieldset-row">
			<div class="label">
				<label for="tipoImpresa"><wp:i18n key="LABEL_TIPO_IMPRESA" /> : <span class="required-field">*</span></label>
			</div>
			<div class="element">
				<wp:i18n key="OPT_CHOOSE_TIPO_IMPRESA" var="headerValueTipoImpresa" />
				<s:select name="tipoImpresa" id="tipoImpresa" value="{tipoImpresa}" 
							list="maps['tipiImpresaIscrAlbo']"  headerKey="" headerValue="%{#attr.headerValueTipoImpresa}" 
							aria-required="true" >
				</s:select>
			</div>
		</div>

		<div class="fieldset-row">
			<div class="label">
				<label for="nazione"><wp:i18n key="LABEL_NAZIONE" /> : <span class="required-field">*</span></label>
			</div>
			<div class="element">
				<wp:i18n key="OPT_CHOOSE_NAZIONE" var="headerValueNazione" />
				<s:select list="maps['nazioni']" name="nazione" id="nazione" value="{nazione}"
							headerKey="" headerValue="%{#attr.headerValueNazione}" 
							aria-required="true" >
				</s:select>
			</div>
		</div>

		<div class="fieldset-row">
			<div class="label">
				<label for="codiceFiscale"><wp:i18n key="LABEL_CODICE_FISCALE" /> : <span class="required-field">*</span></label>
			</div>
			<div class="element">
				<s:textfield name="codiceFiscale" id="codiceFiscale" value="%{codiceFiscale}" 
								maxlength="16" size="20" aria-required="true" />
			</div>
		</div>

		<div class="fieldset-row <s:if test='%{!#session[#sessionId].rti || #sessionId == "dettIscrAlbo"}'>last-row</s:if>">
			<div class="label">
				<label id="labelIVA" for="partitaIVA"><wp:i18n key="LABEL_PARTITA_IVA" /> : <span class="required-field">*</span></label>
			</div>
			<div class="element">
				<s:textfield name="partitaIVA" id="partitaIVA" value="%{partitaIVA}" 
								maxlength="16" size="20" aria-required="true" />
			</div>
		</div>

		<s:if test='%{#session[#sessionId].rti && #sessionId == "dettPartecipGara"}'>
			<div class="fieldset-row last-row">
				<div class="label">
					<label for="strQuota"><wp:i18n key="LABEL_QUOTA_PARTECIPAZIONE" /> : <span class="required-field">*</span></label>
				</div>
				<div class="element">
					<s:textfield name="strQuota" id="strQuota" value="%{(quota != null || id == null) ? quota : strQuota}" 
									maxlength="6" size="15" aria-required="true" />
				</div>
			</div>
		</s:if>
		
		<div class="azioni">
			<c:choose>
				<c:when test="${empty id}">
					<s:if test="%{#session[#sessionId].rti}">
						<wp:i18n key="BUTTON_ADD" var="valueAddButton" />
						<wp:i18n key="TITLE_ADD_MANDANTE" var="titleAddButton" />
						<s:submit value="%{#attr.valueAddButton}" title="%{#attr.titleAddButton}" cssClass="button" method="insert"></s:submit>
					</s:if>
					<s:elseif test="%{#session[#sessionId].impresa.consorzio}">
						<wp:i18n key="BUTTON_ADD" var="valueAddButton" />
						<wp:i18n key="TITLE_ADD_CONSORZIATA" var="titleAddButton" />
						<s:submit value="%{#attr.valueAddButton}" title="%{#attr.titleAddButton}" cssClass="button" method="insert"></s:submit>
					</s:elseif>
					<s:else>
						<wp:i18n key="BUTTON_ADD" var="valueAddButton" />
						<wp:i18n key="TITLE_ADD_COMPONENTE" var="titleAddButton" />
						<s:submit value="%{#attr.valueAddButton}" title="%{#attr.titleAddButton}" cssClass="button" method="insert"></s:submit>
					</s:else>
				</c:when>
				<c:otherwise>
					<s:if test='%{#session[#sessionId].rti && #sessionId!="dettPartecipGara"}'>
						<wp:i18n key="BUTTON_REFRESH" var="valueRefreshButton" />
						<wp:i18n key="TITLE_REFRESH_COMPONENTE" var="titleRefreshButton" />
						<s:submit value="%{#attr.valueRefreshButton}" title="%{#attr.titleRefreshButton}" cssClass="button" method="save"></s:submit>
						<wp:i18n key="BUTTON_NEW" var="valueNewButton" />
						<wp:i18n key="TITLE_NEW_COMPONENTE" var="titleAddButton" />
						<s:submit value="%{#attr.valueNewButton}" title="%{#attr.titleNewButton}" cssClass="button" method="add"></s:submit>
					</s:if>
					<s:elseif test='%{#session[#sessionId].rti && #sessionId=="dettPartecipGara"}'>
						<wp:i18n key="BUTTON_REFRESH" var="valueRefreshButton" />
						<wp:i18n key="TITLE_REFRESH_MANDANTE" var="titleRefreshButton" />
						<s:submit value="%{#attr.valueRefreshButton}" title="%{#attr.titleRefreshButton}" cssClass="button" method="savePartecipazione"></s:submit>
						<wp:i18n key="BUTTON_NEW" var="valueNewButton" />
						<wp:i18n key="TITLE_NEW_MANDANTE" var="titleNewButton" />
						<s:submit value="%{#attr.valueNewButton}" title="%{#attr.titleNewButton}" cssClass="button" method="add"></s:submit>
					</s:elseif>
					<s:elseif test='%{#session[#sessionId].impresa.consorzio && #sessionId!="dettPartecipGara"}'>
						<wp:i18n key="BUTTON_REFRESH" var="valueRefreshButton" />
						<wp:i18n key="TITLE_REFRESH_CONSORZIATA" var="titleRefreshButton" />
						<s:submit value="%{#attr.valueRefreshButton}" title="%{#attr.titleRefreshButton}" cssClass="button" method="save"></s:submit>
						<wp:i18n key="BUTTON_NEW" var="valueNewButton" />
						<wp:i18n key="TITLE_NEW_CONSORZIATA" var="titleNewButton" />
						<s:submit value="%{#attr.valueNewButton}" title="%{#attr.titleNewButton}" cssClass="button" method="add"></s:submit>
					</s:elseif>
					<s:elseif test='%{#session[#sessionId].impresa.consorzio && #sessionId=="dettPartecipGara"}'>
						<wp:i18n key="BUTTON_REFRESH" var="valueRefreshButton" />
						<wp:i18n key="TITLE_REFRESH_CONSORZIATA" var="titleRefreshButton" />
						<s:submit value="%{#attr.valueRefreshButton}" title="%{#attr.titleRefreshButton}" cssClass="button" method="savePartecipazione"></s:submit>
						<wp:i18n key="BUTTON_NEW" var="valueNewButton" />
						<wp:i18n key="TITLE_NEW_CONSORZIATA" var="titleNewButton" />
						<s:submit value="%{#attr.valueNewButton}" title="%{#attr.titleNewButton}" cssClass="button" method="add"></s:submit>
					</s:elseif>
					<s:else>
						<wp:i18n key="BUTTON_REFRESH" var="valueRefreshButton" />
						<wp:i18n key="TITLE_REFRESH_COMPONENTE" var="titleRefreshButton" />
						<s:submit value="%{#attr.valueRefreshButton}" title="%{#attr.titleRefreshButton}" cssClass="button" method="save"></s:submit>
						<wp:i18n key="BUTTON_NEW" var="valueNewButton" />
						<wp:i18n key="TITLE_NEW_COMPONENTE" var="titleAddButton" />
						<s:submit value="%{#attr.valueNewButton}" title="%{#attr.titleNewButton}" cssClass="button" method="add"></s:submit>
					</s:else>
				</c:otherwise>
			</c:choose>
		</div>
	</fieldset>
</s:else>
