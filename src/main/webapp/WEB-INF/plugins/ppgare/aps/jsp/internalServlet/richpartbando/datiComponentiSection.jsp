<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>

<s:set var="sessionId">${param.sessionIdObj}</s:set>

<s:set var="isIscrizione" value="false" />
<s:set var="isPartecipazione" value="false" />
<s:set var="iscrizione" value=""/>

<%-- elenco operatore --%>
<c:if test="${param.sessionIdObj == 'dettIscrAlbo'}">
	<s:set var="isIscrizione" value="true" />
	<s:set var="iscrizione" value="%{#session[#sessionId]}"/>
</c:if>

<%-- offerta di gara --%>
<c:if test="${param.sessionIdObj == 'dettaglioOffertaGara'}">
	<s:set var="isPartecipazione" value="true" />
</c:if>

<s:set var="partecipazione" value="%{#session[#sessionId].bustaPartecipazione.helper}"/>
<s:set var="nessunaPrequalifica" value="%{#session[#sessionId].domandaPartecipazione == null}"/>

<s:if test="%{#isIscrizione}">
	<s:set var="helper" value="%{#session[#sessionId]}" />
	<s:set var="dettGara" value="%{#session[#sessionId].dettaglioGara}"/>
</s:if>
<s:else>
	<s:set var="helper" value="%{#session[#sessionId].bustaPartecipazione.helper}" />
	<s:set var="dettGara" value="%{#session[#sessionId].dettaglioGara}"/>
</s:else>

<s:set var="invioOfferta" value="%{#partecipazione.tipoEvento == 2}" />
<s:set var="garaRistretta" value="%{#dettGara.datiGeneraliGara.iterGara == 2 || #dettGara.datiGeneraliGara.iterGara == 4}" />

<s:hidden id="quotaRTIVisibile" name="quotaRTIVisibile" value='%{#helper.rti && #isPartecipazione}' />
<s:hidden id="quotaVisibile" name="quotaVisibile" value='%{#helper.rti && #isPartecipazione}' />
<s:hidden id="readOnly" name="readOnly" value="%{readOnly}" />

<s:if test="%{!readOnly}">
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

			// gestisci la selezione della tipologia 'Libero professionista'
			ivaRequired();
			$('#tipoImpresa').on('change', function() {
				ivaRequired();
			});

			// gestisci la selezione ambito territoriale italia/estero
			viewIdFiscaleEstero(false);
			$('#ambitoTerritoriale').on('change', function() {
				viewIdFiscaleEstero(true);
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

		function viewIdFiscaleEstero(changeValori) {
			var ambitoTerritoriale = "#"+$('[id="ambitoTerritoriale"] option:selected').val()+"#";
			if(ambitoTerritoriale == "#1#") {
				$('[id="nazione"] option[value="Italia"]').attr("disabled", false);
				if (changeValori) {
					$('[id="nazione"]').val("Italia");
					$('[id="idFiscaleEstero"]').val("");
				}
				$('.clsAmbitoTerIT').show();
				$('.clsAmbitoTerEE').hide();
				$('[id="codiceFiscale"]').prop('maxLength', 16);
				$('[id="partitaIVA"]').prop('maxLength', 16);
				$('[id="idFiscaleEstero"]').prop('maxLength', 16);
			} else if(ambitoTerritoriale == "#2#"){
				//$('[id="nazione"]').val("");
				$('[id="nazione"] option[value="Italia"]').attr("disabled", true);
				if (changeValori) {
					$('[id="nazione"]').val("");
					$('[id="codiceFiscale"]').val("");
					$('[id="partitaIVA"]').val("");
				}
				$('.clsAmbitoTerIT').hide();
				$('.clsAmbitoTerEE').show();
				$('[id="codiceFiscale"]').prop('maxLength', 30);
				$('[id="partitaIVA"]').prop('maxLength', 30);
				$('[id="idFiscaleEstero"]').prop('maxLength', 30);
			}else{
				$('[id="nazione"]').val("");
				$('[id="nazione"] option[value="Italia"]').attr("disabled", false);
				$('[id="codiceFiscale"]').val("");
				$('[id="partitaIVA"]').val("");
				$('[id="idFiscaleEstero"]').val("");
				$('.clsAmbitoTerIT').hide();
				$('.clsAmbitoTerEE').hide();
				$('[id="codiceFiscale"]').prop('maxLength', 16);
				$('[id="partitaIVA"]').prop('maxLength', 16);
				$('[id="idFiscaleEstero"]').prop('maxLength', 16);
			}
		}
	//--><!]]>
	</script>
</s:if>

<s:if test="%{#helper.rti}">
	<c:set var="deleteSentence"><wp:i18n key='LABEL_QUESTION_CONFIRM_DEL_MANDANTE'/></c:set>
	<c:set var="skipLista"><wp:i18n key='LABEL_SKIP_MANDANTI'/></c:set>
	<c:set var="fieldsetTableTitle"><wp:i18n key='LABEL_ELENCO_MANDANTI'/></c:set>
	<c:set var="titoloTabella"><wp:i18n key='LABEL_ELENCO_MANDANTI'/></c:set>
	<c:set var="modifyTitle"><wp:i18n key='LABEL_MODIFICA_MANDANTE'/></c:set>
	<c:set var="deleteTitle"><wp:i18n key='LABEL_ELIMINA_MANDANTE'/></c:set>
	<wp:i18n key='LABEL_MANDANTE' var="labelMandante"/>
	<c:set var="obj" value="${fn:toLowerCase(attr.labelMandataria)}" />
</s:if>
<s:elseif test="%{#helper.impresa.consorzio}">
	<c:set var="deleteSentence"><wp:i18n key='LABEL_QUESTION_CONFIRM_DEL_CONSORZIATA'/></c:set>
	<c:set var="skipLista"><wp:i18n key='LABEL_SKIP_CONSORZIATE_ESECUTRICI'/></c:set>
	<c:set var="fieldsetTableTitle"><wp:i18n key='LABEL_ELENCO_CONSORZIATE'/></c:set>
	<c:set var="titoloTabella"><wp:i18n key='LABEL_ELENCO_CONSORZIATE'/></c:set>
	<c:set var="modifyTitle"><wp:i18n key='LABEL_MODIFICA_CONSORZIATA'/></c:set>
	<c:set var="deleteTitle"><wp:i18n key='LABEL_ELIMINA_CONSORZIATA'/></c:set>
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
	<s:set var="comp" value="%{#helper.componenti.get(idDelete)}"/>
	<p class="question">${deleteSentence} <s:property value="%{#comp.ragioneSociale}"/>?</p>
	<div class="azioni">
		<s:hidden name="idDelete" value="%{idDelete}"/>
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_confirm_delete.jsp" />
	</div>
</s:if>
<s:elseif test="%{confirmNoConsorziate}">
	<p class="question">
		<s:if test="%{#invioOfferta}">
			<wp:i18n key='LABEL_QUESTION_CONTINUE_WITHOUT_CONSORZIATA_OFF'/>
		</s:if>
		<s:else>
			<wp:i18n key='LABEL_QUESTION_CONTINUE_WITHOUT_CONSORZIATA'/>
		</s:else> 
	</p>
	<%-- si inseriscono dei valori fittizi per far trovare i campi all'xml di validazione --%>
	<input type="hidden" name="ragioneSociale" />
	<input type="hidden" name="tipoImpresa" />
	<input type="hidden" name="nazione" value="" />
	<input type="hidden" name="codiceFiscale" />
	<input type="hidden" name="partitaIVA" />
	<input type="hidden" name="ambitoTerritoriale" value="1" />	
</s:elseif>
<s:else>

	<p class="noscreen">[ <a href="#" >${skipLista}</a> ]
	</p>	
	<s:if test="%{#helper.rti}">

		<fieldset>
			<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_MANDATARIA" /></legend>

			<div class="fieldset-row first-row">
				<div class="label">
					<label><wp:i18n key="LABEL_RAGIONE_SOCIALE" /> : </label>
				</div>
				<div class="element">
					<s:property value="%{#helper.datiPrincipaliImpresa.ragioneSociale}" />
				</div>
			</div>

			<div class="fieldset-row">
				<div class="label">
					<label><wp:i18n key="LABEL_AMBITO_TERRITORIALE" /> : </label>
				</div>
				<div class="element">
					<c:set var="lblAmbitoTerr"><wp:i18n key="LABEL_AMBITO_TERRITORIALE" /></c:set>
					<s:iterator value="maps['ambitoTerritoriale']">
						<s:if test="%{key == #helper.datiPrincipaliImpresa.ambitoTerritoriale}">
							<s:property value="%{value}" />
						</s:if>
					</s:iterator>
				</div>
			</div>
	
			<s:if test='%{#helper.datiPrincipaliImpresa.ambitoTerritoriale == "1"}' >
				<%-- impresa Italia --%>
				<div class="fieldset-row">
					<div class="label">
						<label><wp:i18n key="LABEL_NAZIONE" /> : </label>
					</div>
					<div class="element">
						<s:property value="%{#helper.datiPrincipaliImpresa.nazioneSedeLegale}"/>
					</div>
				</div>
	
				<div class="fieldset-row">
					<div class="label">
						<label><wp:i18n key="LABEL_CODICE_FISCALE" /> : </label>
					</div>
					<div class="element">
						<s:property value="%{#helper.datiPrincipaliImpresa.codiceFiscale}"/>
					</div>
				</div>
	
				<div class="fieldset-row">
					<div class="label">
						<label><wp:i18n key="LABEL_PARTITA_IVA" /> :</label>
					</div>
					<div class="element">
						<s:property value="%{#helper.datiPrincipaliImpresa.partitaIVA}"/>
					</div>
				</div>
			</s:if>
			<s:elseif test='%{#helper.datiPrincipaliImpresa.ambitoTerritoriale == "2"}' >
				<%-- impresa Estero --%>
				<div class="fieldset-row">
					<div class="label">
						<label><wp:i18n key="LABEL_NAZIONE" /> : </label>
					</div>
					<div class="element">
						<s:property value="%{#helper.datiPrincipaliImpresa.nazioneSedeLegale}"/>
					</div>
				</div>
				
				<div class="fieldset-row">
					<div class="label">
						<label><wp:i18n key="LABEL_IDENTIFICATIVO_FISCALE_ESTERO" /> : </label>
					</div>
					<div class="element">
						<%-- <s:property value="%{#helper.datiPrincipaliImpresa.idFiscaleEstero}"/> ??? --%>
						<s:property value="%{#helper.datiPrincipaliImpresa.codiceFiscale}"/> 
					</div>
				</div>
			</s:elseif>
<%-- 
isPartecipazione=<s:property value="%{#isPartecipazione}" /><br/>
dettIscrizione=<s:property value="%{#dettIscrizione}" /><br/> 
dettGara=<s:property value="%{#dettGara}" /><br/>
partecipazione.tipoEvento=<s:property value="%{#partecipazione.tipoEvento}" /><br/>
dettGara.iterGara=<s:property value="%{#dettGara.datiGeneraliGara.iterGara}" /><br/>
partecipazione.editRTI=<s:property value="%{#partecipazione.editRTI}" /><br/> 
garaRistretta=<s:property value="%{#garaRistretta}" /><br/>
invioOfferta=<s:property value="%{#invioOfferta}" /><br/>
nessunaPrequalifica=<s:property value="%{#nessunaPrequalifica}" /><br/>
--%>
			<s:if test='%{#isPartecipazione}'>
				<div id="divQuota" class="fieldset-row last-row">
					<div class="label">
						<label for="strQuotaRTI"><wp:i18n key="LABEL_QUOTA_PARTECIPAZIONE" /> : <span class="required-field">*</span></label>
					</div>
					<div class="element">

						<%-- per le gare ristrette la quota NON e' editabile --%>
							<s:textfield name="strQuotaRTI" id="strQuotaRTI" value="%{quotaRTI != null ? quotaRTI : strQuotaRTI}"
										 maxlength="6" size="15"
										 readonly="%{(!#partecipazione.editRTI && (#invioOfferta && #garaRistretta && #nessunaPrequalifica)) || readOnly}"
										 cssClass="%{#classBlocco}"
										 aria-required="true" />
					</div>
				</div>
			</s:if>

		</fieldset>
	</s:if>

	<s:if test="%{#helper.componenti.size() >= 0 || #helper.componentiRTI.size() >= 0}">
		<%-- verifica se esiste almeno un componente con idFiscaleEstero --%>
		<s:set var="idFiscaliEsteroPresenti" value="0" />
		<s:iterator value="%{#helper.componentiRTI}" status="status">
			<s:if test='%{idFiscaleEstero != null}'>
				<s:set var="idFiscaliEsteroPresenti" value="1" />
			</s:if>
		</s:iterator>
		<s:iterator value="#helper.componenti" status="status">
			<s:if test='%{idFiscaleEstero != null}'>
				<s:set var="idFiscaliEsteroPresenti" value="1" />
			</s:if>
		</s:iterator>

		<fieldset>
			<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span>${fieldsetTableTitle}</legend>
			<table class="wizard-table" summary="${titoloTabella}">
				<tr>
					<th scope="col"><wp:i18n key="LABEL_RAGIONE_SOCIALE" /></th>
					<th scope="col"><wp:i18n key="LABEL_CODICE_FISCALE" /></th>
					<th scope="col"><wp:i18n key="LABEL_PARTITA_IVA" /></th>
					<s:if test='%{#idFiscaliEsteroPresenti}'>
						<th scope="col"><wp:i18n key="LABEL_IDENTIFICATIVO_FISCALE_ESTERO" /></th>
					</s:if>
					<s:if test='%{#helper.rti && #isPartecipazione}'>
						<th scope="col">Quota</th>
					</s:if>
					<s:if test="%{!readOnly}">
						<th scope="col"><wp:i18n key="ACTIONS" /></th>
					</s:if>
				</tr>

				<s:if test='%{#helper.rti && !#isPartecipazione}'>
					<s:iterator  value="%{#helper.componentiRTI}" status="status">
						<c:if test="${status.index != 0}">
								<td><s:property value="%{ragioneSociale}"/></td>
								<td><s:property value="%{codiceFiscale}"/></td>
								<td><s:property value="%{partitaIVA}"/></td>
								<s:if test='%{#idFiscaliEsteroPresenti}'>
									<td><s:property value="%{idFiscaleEstero}"/></td>
								</s:if>
								<%-- 
								<s:if test='%{#helper.rti && #isPartecipazione}'>
									<td><s:property value="%{quota}"/></td>
								</s:if> 
								--%>
								<s:if test="{!readOnly}">
									<td class="azioni">
										<ul>
											<c:choose>
												<c:when test="${skin == 'highcontrast' || skin == 'text'}">
													<li>
														<a href='<wp:action path="/ExtStr2/do/FrontEnd/${param.namespace}/modifyComponente.action"/>&amp;id=${status.index}&amp;ext=${param.ext}'
															 title="${modifyTitle}">${modifyTitle}</a>
													</li>
													<li>
														<a href='<wp:action path="/ExtStr2/do/FrontEnd/${param.namespace}/confirmDeleteComponente.action"/>&amp;idDelete=${status.index}&amp;ext=${param.ext}'
															 title="${deleteTitle}">${deleteTitle}</a>
													</li>
												</c:when>
												<c:otherwise>
													<li>
														<a href='<wp:action path="/ExtStr2/do/FrontEnd/${param.namespace}/modifyComponente.action"/>&amp;id=${status.index}&amp;ext=${param.ext}'
															 title="${modifyTitle}" class="bkg modify"></a>
													</li>
													<li>
														<a href='<wp:action path="/ExtStr2/do/FrontEnd/${param.namespace}/confirmDeleteComponente.action"/>&amp;idDelete=${status.index}&amp;ext=${param.ext}'
															 title="${deleteTitle}" class="bkg delete"></a>
													</li>
												</c:otherwise>
											</c:choose>
										</ul>
									</td>
								</s:if>
							</tr>
						</c:if>
					</s:iterator>
				</s:if>
				<s:else>
					<s:iterator value="%{#helper.componenti}" status="status">
						<tr>
							<td><s:property value="%{ragioneSociale}"/></td>
							<td><s:property value="%{codiceFiscale}"/></td>
							<td><s:property value="%{partitaIVA}"/></td>
							<s:if test='%{#idFiscaliEsteroPresenti}'>
								<td><s:property value="%{idFiscaleEstero}"/></td>
							</s:if>
							<s:if test='%{#helper.rti && #isPartecipazione}'>
								<td><s:property value="%{quota}"/></td>
							</s:if>
							<s:if test="%{!readOnly}">
								<td class="azioni">
									<ul>
										<c:choose>
											<c:when test="${skin == 'highcontrast' || skin == 'text'}">
												<li>
													<a href='<wp:action path="/ExtStr2/do/FrontEnd/${param.namespace}/modifyComponente.action"/>&amp;id=${status.index}&amp;ext=${param.ext}'
														 title="${modifyTitle}">${modifyTitle}</a>
												</li>
												<li>
													<a href='<wp:action path="/ExtStr2/do/FrontEnd/${param.namespace}/confirmDeleteComponente.action"/>&amp;idDelete=${status.index}&amp;ext=${param.ext}'
														 title="${deleteTitle}">${deleteTitle}</a>
												</li>
											</c:when>
											<c:otherwise>
												<li>
													<a href='<wp:action path="/ExtStr2/do/FrontEnd/${param.namespace}/modifyComponente.action"/>&amp;id=${status.index}&amp;ext=${param.ext}'
														 title="${modifyTitle}" class="bkg modify"></a>
												</li>
												<li>
													<a href='<wp:action path="/ExtStr2/do/FrontEnd/${param.namespace}/confirmDeleteComponente.action"/>&amp;idDelete=${status.index}&amp;ext=${param.ext}'
														 title="${deleteTitle}" class="bkg delete"></a>
												</li>
											</c:otherwise>
										</c:choose>
									</ul>
								</td>
							</s:if>
						</tr>
					</s:iterator>					
				</s:else>
			</table>
		</fieldset>
	</s:if>

	<p class="noscreen">[ <a href="#" id="formComponente"><wp:i18n key="SKIP_TO_FORM_BUTTONS"/></a> ]</p>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/mandatory_fields_message.jsp" />
	<s:if test="%{!readOnly}">
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
					<label for="ambitoTerritoriale"><wp:i18n key="LABEL_AMBITO_TERRITORIALE" /> : <span class="required-field">*</span></label>
				</div>
				<div class="element">
					<s:select list="maps['ambitoTerritoriale']" name="ambitoTerritoriale" id="ambitoTerritoriale" value="%{ambitoTerritoriale}"
								headerKey="" aria-required="true" >
					</s:select>
				</div>
			</div>

			<%-- ambito territoriale impresa Italia --%>
			<div class="fieldset-row clsAmbitoTerIT">
				<div class="label">
					<label for="codiceFiscale"><wp:i18n key="LABEL_CODICE_FISCALE" /> : <span class="required-field">*</span></label>
				</div>
				<div class="element">
					<s:textfield name="codiceFiscale" id="codiceFiscale" value="%{codiceFiscale}"
									maxlength="16" size="20" aria-required="true" />
				</div>
			</div>

			<div class="fieldset-row clsAmbitoTerIT <s:if test='%{!#helper.rti || #isIscrizione}'>last-row</s:if> ">
				<div class="label">
					<label id="labelIVA" for="partitaIVA"><wp:i18n key="LABEL_PARTITA_IVA" /> : <span class="required-field">*</span></label>
				</div>
				<div class="element">
					<s:textfield name="partitaIVA" id="partitaIVA" value="%{partitaIVA}"
									maxlength="16" size="20" aria-required="true" />
				</div>
			</div>

			<%-- ambito territoriale impresa Estero --%>
			<div class="fieldset-row clsAmbitoTerEE">
				<div class="label">
					<label for="nazione"><wp:i18n key="LABEL_NAZIONE" /> : <span class="required-field">*</span></label>
				</div>
				<div class="element">
					<wp:i18n key="OPT_CHOOSE_NAZIONE" var="headerValueNazione" />
					<s:select list="maps['nazioni']" name="nazione" id="nazione" value="%{nazione}"
								headerKey="" headerValue="%{#attr.headerValueNazione}"
								aria-required="true" >
					</s:select>
				</div>
			</div>

			<div class="fieldset-row clsAmbitoTerEE">
				<div class="label">
					<label><wp:i18n key="LABEL_IDENTIFICATIVO_FISCALE_ESTERO" /> : <span class="required-field">*</span></label>
				</div>
				<div class="element">
					<s:textfield name="idFiscaleEstero" id="idFiscaleEstero" value="%{idFiscaleEstero}"
									maxlength="30" size="20" aria-required="true" />
				</div>
			</div>

			<s:if test='%{#helper.rti && #isPartecipazione}'>
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
						<s:if test="%{#helper.rti}">
							<wp:i18n key="BUTTON_ADD" var="valueAddButton" />
							<wp:i18n key="TITLE_ADD_MANDANTE" var="titleAddButton" />
							<s:submit value="%{#attr.valueAddButton}" title="%{#attr.titleAddButton}" cssClass="button" method="insert"></s:submit>
						</s:if>
						<s:elseif test="%{#helper.impresa.consorzio}">
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
						<s:if test='%{#helper.rti && #isIscrizione}'>
							<wp:i18n key="BUTTON_REFRESH" var="valueRefreshButton" />
							<wp:i18n key="TITLE_REFRESH_COMPONENTE" var="titleRefreshButton" />
							<s:submit value="%{#attr.valueRefreshButton}" title="%{#attr.titleRefreshButton}" cssClass="button" method="save"></s:submit>
							<wp:i18n key="BUTTON_NEW" var="valueNewButton" />
							<wp:i18n key="TITLE_NEW_COMPONENTE" var="titleAddButton" />
							<s:submit value="%{#attr.valueNewButton}" title="%{#attr.titleNewButton}" cssClass="button" method="add"></s:submit>
						</s:if>
						<s:elseif test='%{#helper.rti && #isPartecipazione}'>
							<wp:i18n key="BUTTON_REFRESH" var="valueRefreshButton" />
							<wp:i18n key="TITLE_REFRESH_MANDANTE" var="titleRefreshButton" />
							<s:submit value="%{#attr.valueRefreshButton}" title="%{#attr.titleRefreshButton}" cssClass="button" method="savePartecipazione"></s:submit>
							<wp:i18n key="BUTTON_NEW" var="valueNewButton" />
							<wp:i18n key="TITLE_NEW_MANDANTE" var="titleNewButton" />
							<s:submit value="%{#attr.valueNewButton}" title="%{#attr.titleNewButton}" cssClass="button" method="add"></s:submit>
						</s:elseif>
						<s:elseif test='%{#helper.impresa.consorzio && #isIscrizione}'>
							<wp:i18n key="BUTTON_REFRESH" var="valueRefreshButton" />
							<wp:i18n key="TITLE_REFRESH_CONSORZIATA" var="titleRefreshButton" />
							<s:submit value="%{#attr.valueRefreshButton}" title="%{#attr.titleRefreshButton}" cssClass="button" method="save"></s:submit>
							<wp:i18n key="BUTTON_NEW" var="valueNewButton" />
							<wp:i18n key="TITLE_NEW_CONSORZIATA" var="titleNewButton" />
							<s:submit value="%{#attr.valueNewButton}" title="%{#attr.titleNewButton}" cssClass="button" method="add"></s:submit>
						</s:elseif>
						<s:elseif test='%{#helper.impresa.consorzio && #isPartecipazione}'>
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
	</s:if>
</s:else>
