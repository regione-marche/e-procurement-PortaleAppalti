<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="s"   uri="/struts-tags" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>


<%-- Recupera le customizzazioni presenti --%>
<es:checkCustomization var="visGeneraDomandaIscrPDF" objectId="ISCRALBO-DOCUM" attribute="PDFDOMANDA" feature="VIS" />
<es:checkCustomization var="visGeneraDomandaRinnPDF" objectId="RINNALBO-DOCUM" attribute="PDFDOMANDA" feature="VIS" />
<es:checkCustomization var="visMarcaBollo" objectId="ISCRALBO-DOCUM" attribute="NUMSERIEBOLLODOMANDA" feature="VIS" />
<es:checkCustomization var="manMarcaBollo" objectId="ISCRALBO-DOCUM" attribute="NUMSERIEBOLLODOMANDA" feature="MAN" />

<wp:i18n key="OPT_CHOOSE_PROVINCIA" var="headerValueProvincia" />

<%-- Prepara il tipo di pagina in base al tipo di domanda 
	- AGGIORNAMENTO DI DATI O DOCUMENTI
	- DOMANDA DI RINNOVO
	- DOMANDA D'ISCRIZIONE 
--%>
<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/iscralbo/inc/initDomanda.jsp"/>

<%-- action per la gestione dei pulsanti "< Indietro" e "Avanti >" --%>
<c:set var="nextAction" value=""/>
<s:if test="%{#session.dettIscrAlbo != null}">
	<c:set var="nextAction" value="/ExtStr2/do/FrontEnd/IscrAlbo/processPageIscrAlboGeneraPdf.action"/>
</s:if>
<s:elseif test="%{#session.dettRinnAlbo != null}">
	<c:set var="nextAction" value="/ExtStr2/do/FrontEnd/IscrAlbo/processPageRinnovoGeneraPdf.action"/>
</s:elseif>

<%-- action per la generazione del PDF --%>
<s:if test="%{#iscrizione}">
	<s:url id="urlPdf" namespace="/do/FrontEnd/IscrAlbo" action="createDomandaPdf">
		<s:param name="urlPage">${currentPageUrl}</s:param>
		<s:param name="currentFrame">${param.currentFrame}</s:param>
	</s:url>
</s:if>
<s:if test="%{#rinnovo}">
	<s:url id="urlPdf" namespace="/do/FrontEnd/IscrAlbo" action="createDomandaRinnovoPdf">
		<s:param name="urlPage">${currentPageUrl}</s:param>
		<s:param name="currentFrame">${param.currentFrame}</s:param>
	</s:url>
</s:if>
<s:if test="%{#aggiornamentoDatiDoc}">
	<s:url id="urlPdf" namespace="/do/FrontEnd/IscrAlbo" action="createAggiornamentoIscrizionePdf">
		<s:param name="urlPage">${currentPageUrl}</s:param>
		<s:param name="currentFrame">${param.currentFrame}</s:param>
	</s:url>
</s:if>


<%-- setta la variabile sulla base della visualizzazione da customizzazione 
     e dal fatto che non si tratta di rinnovo --%>
<c:set var="visMarcaBollo" value="${visMarcaBollo && !sessionScope[sessionIdObj].rinnovoIscrizione}" />

<%--
DEBUG <br> 
iscrizione=<s:property value="%{#iscrizione}"/> <br>
rinnovo=<s:property value="%{#rinnovo}"/> <br>
aggiornamentoDatiDoc=<s:property value="%{#aggiornamentoDatiDoc}"/> <br>
visGeneraDomandaIscrPDF=${visGeneraDomandaIscrPDF} <br>
visGeneraDomandaRinnPDF=${visGeneraDomandaRinnPDF} <br>
--%>


<script type="text/javascript" src="<wp:resourceURL/>static/js/jquery.min.js"></script>
<script>
function removeInvalidAttribute() {
	let field = document.getElementById("serialNumberMarcaBollo");
	if (field != null && field.hasAttribute("aria-invalid")) {
		field.removeAttribute("aria-invalid");
	}
}
$(document).ready(function () {
	var visMarcaBollo = ${visMarcaBollo};
	var txtMarcaBollo = '[name="serialNumberMarcaBollo"]';
	var btnCreatePdf  = '#createPdf';
	
	// se js abilitato rimuovo l'avviso che per essere usabile la pagina
	// serve js abilitato
	$('#noJs').remove();

	// se PassOE è visibile disabilita il pulsante di generazione PDF
	if(visMarcaBollo == 1) {
		var value = $(txtMarcaBollo).val().trim();
		if(value == '') {
			$(btnCreatePdf).hide();
		}
	}
	
	// abilita il pulsante di generazione pdf solo se è stato inserito il MarcaBollo
	$(txtMarcaBollo).bind('input', function() {	
		if(visMarcaBollo == 1) { 
			var value = $(this).val().trim();
			if(value == '') {
				$(btnCreatePdf).hide();
			} else {
				$(btnCreatePdf).show();
			}
		}
	});
	
});
</script>


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">	
	<s:if test="%{#helper.tipologia == 2}">
		<c:set var="titolo"><wp:i18n key='TITLE_PAGE_ISCRALBO_MERCATO_ELETTRONICO'/></c:set>
	</s:if>
	<s:else>
		<c:set var="titolo"><wp:i18n key='TITLE_PAGE_ISCRALBO_ELENCO_OE'/></c:set>
	</s:else>

	<s:if test="%{#helper.rinnovoIscrizione}">
		<h2><wp:i18n key='TITLE_PAGE_ISCRALBO_RICHIESTA_RINNOVO_ISCRIZIONE'/> <c:out value="${titolo}"/></h2>
	</s:if>
	<s:elseif test="%{!#helper.aggiornamentoIscrizione}">
		<h2><wp:i18n key='TITLE_PAGE_ISCRALBO_RICHIESTA_ISCRIZIONE'/> <c:out value="${titolo}"/></h2>
	</s:elseif>
	<s:else>
		<h2><wp:i18n key='TITLE_PAGE_ISCRALBO_AGGIORNAMENTO_ISCRIZIONE'/> <c:out value="${titolo}"/></h2>
	</s:else>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/field_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_messages.jsp" />

	<jsp:include page="stepsIscrizione.jsp">
		<jsp:param name="sessionIdObj" value="${sessionIdObj}" />
	</jsp:include>
	
	<s:if test="%{#helper.componentiRTI.size == 0}">
		<s:if test="%{#helper.rinnovoIscrizione}">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
				<jsp:param name="keyMsg" value="BALLOON_RIN_ISCR_ALBO_SCARICA_DOMANDA" />
			</jsp:include>
		</s:if>
		<s:elseif test="%{!#helper.aggiornamentoIscrizione}">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
				<jsp:param name="keyMsg" value="BALLOON_ISCR_ALBO_SCARICA_DOMANDA"/>
			</jsp:include>
		</s:elseif>
		<s:else>
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
				<jsp:param name="keyMsg" value="BALLOON_AGG_ISCR_ALBO_SCARICA_DOMANDA"/>
			</jsp:include>
		</s:else>
	</s:if>
	<s:else>
		<s:if test="%{#helper.rinnovoIscrizione}">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
				<jsp:param name="keyMsg" value="BALLOON_RIN_ISCR_ALBO_SCARICA_DOMANDA_RTI" />
			</jsp:include>
		</s:if>
		<s:elseif test="%{!#helper.aggiornamentoIscrizione}">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
				<jsp:param name="keyMsg" value="BALLOON_ISCR_ALBO_SCARICA_DOMANDA_RTI"/>
			</jsp:include>
		</s:elseif>
		<s:else>
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
				<jsp:param name="keyMsg" value="BALLOON_AGG_ISCR_ALBO_SCARICA_DOMANDA_RTI"/>
			</jsp:include>
		</s:else>
	</s:else>
	
	<s:set var="imgCheck"><wp:resourceURL/>static/img/check.svg</s:set>
	
	<s:if test="%{listaImpreseVisible}">
		<fieldset>
			<legend><wp:i18n key="LABEL_PARTECIPANTI_RTI"/></legend>
			<table>
				<th style="min-width: 1em;"></th>
				<th><wp:i18n key="LABEL_RAGIONE_SOCIALE" /></th>
				<th><wp:i18n key="LABEL_FIRMATARIO" /></th>
				<th><wp:i18n key="ACTIONS" /></th>
				
				<s:iterator var="componente" value="%{#helper.componentiRTI}" status="status">
					
					<s:set name="key" value="%{#helper.componentiRTI.getFirmatario(#componente)}"/>
					
					<tr>
						<td class="azioni">
							<s:if test="%{#key.nominativo!=null}">
								<img class="resize-svg-16" src="${imgCheck}" title='<wp:i18n key="TITLE_FIRMATARIO_INSERITO_GENERAZIONE_PDF_PRONTA"/>'/>
							</s:if>
						</td>
						<td><s:property value="#componente.ragioneSociale"/></td>
						<td>
							<s:if test="%{#key.nominativo!=null}">
								<s:property value="%{#key.nominativo}"/>
							</s:if>
						</td>
						<td class="azioni">
							<s:if test="%{#status.index==0}">
								<s:if test="%{modificaFirmatarioVisible}">
									<c:if test="${sessionIdObj eq 'dettIscrAlbo'}">
										<a href='<wp:action path="/ExtStr2/do/FrontEnd/IscrAlbo/editFirmatarioMandataria.action"/>' class="bkg modify" title="<wp:i18n key="BUTTON_EDIT"/>">
										</a>
									</c:if>
									<c:if test="${sessionIdObj eq 'dettRinnAlbo'}">
										<a href='<wp:action path="/ExtStr2/do/FrontEnd/IscrAlbo/editRinnovoFirmatarioMandataria.action"/>' class="bkg modify" title="<wp:i18n key="BUTTON_EDIT"/>">
										</a>
									</c:if>
								</s:if>
							</s:if>
							<s:else>
								<c:if test="${sessionIdObj eq 'dettIscrAlbo'}">
									<a href='<wp:action path="/ExtStr2/do/FrontEnd/IscrAlbo/editFirmatarioMandante.action"/>&amp;id=${status.index}' class="bkg modify" title="<wp:i18n key="BUTTON_EDIT"/>">
									</a>
								</c:if>
								<c:if test="${sessionIdObj eq 'dettRinnAlbo'}">
									<a href='<wp:action path="/ExtStr2/do/FrontEnd/IscrAlbo/editRinnovoFirmatarioMandante.action"/>&amp;id=${status.index}' class="bkg modify" title="<wp:i18n key="BUTTON_EDIT"/>">
									</a>
								</c:if>	
							</s:else>
						</td>
					</tr>
				</s:iterator>
			</table>

			<%-- DOMANDA ISCRIZIONE --%>
			<s:if test="%{#iscrizione}">
				<c:if test="${visGeneraDomandaIscrPDF}">
		 			<s:if test="%{#helper.iscrizioneDomandaVisible && listaImpreseVisible && allFirmatariInseriti}">
						<form action="${urlPdf}" method="post">	
							<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
							
							<c:if test="${visMarcaBollo}">
								<div class="fieldset-row first-row last-row">
									<div class="label">
										<label for="serialNumberMarcaBollo"><wp:i18n key="LABEL_ISCRALBO_NUMERO_MARCA_BOLLO"/> : <span class="required-field">*</span></label>
									</div>
									<div class="element">
										<s:textfield name="serialNumberMarcaBollo" size="20" maxlength="14" />
										<div class="note">
											<c:choose>
												<c:when test="${manMarcaBollo}">
													<wp:i18n key="LABEL_NOTA_ND_NON_PREVISTO" />
												</c:when>
												<c:otherwise>
													<wp:i18n key="LABEL_NOTA_INDICARE_ND_SE_NON_PREVISTO" />
												</c:otherwise>
											</c:choose>
										</div>
									</div>
								</div>
							</c:if>
							<div class="azioni">
								<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_generate_pdf.jsp" >
									<jsp:param name="onclick" value="removeInvalidAttribute" />
								</jsp:include>
							</div>
						</form>
					</s:if>
				</c:if>
			</s:if>
			
			<%-- DOMANDA RINNOVO --%>
			<s:if test="%{#rinnovo}">
				<c:if test="${visGeneraDomandaRinnPDF}">
					<form action="${urlPdf}" method="post">
						<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
<%--	
						<c:if test="${visMarcaBollo}">
							<div class="fieldset-row first-row last-row">
								<div class="label">
									<label for="serialNumberMarcaBollo">Numero di serie marca da bollo : <span class="required-field">*</span></label>
								</div>
								<div class="element">
									<s:textfield name="serialNumberMarcaBollo" size="20" maxlength="14" />
									<div class="note"><wp:i18n key="LABEL_NOTA_INDICARE_ND_SE_NON_PREVISTO" /></div>
								</div>
							</div>
						</c:if>
--%>
						<div class="azioni">
							<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_generate_pdf.jsp" >
								<jsp:param name="onclick" value="removeInvalidAttribute" />
							</jsp:include>
						</div>
					</form>
				</c:if>
			</s:if>
			
			<%-- DOMANDA AGGIORNAMENTO --%>
			<s:if test="%{#aggiornamentoDatiDoc}">
				<c:if test="${visGeneraDomandaIscrPDF || visGeneraDomandaRinnPDF}">
					<form action="${urlPdf}" method="post">	
						<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
						
						<div class="azioni">
							<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_generate_pdf.jsp" >
								<jsp:param name="onclick" value="removeInvalidAttribute" />
							</jsp:include>
						</div>
					</form>
				</c:if>
			</s:if>
		</fieldset>	
	</s:if>
	<s:else>
		<fieldset>
			<s:if test="%{#iscrizione}">
				<legend><wp:i18n key="LABEL_ISCRALBO_SCARICA_RICHIESTA_ISCRIZIONE"/></legend>
			</s:if>
			<s:elseif test="%{#rinnovo}">
				<legend><wp:i18n key="LABEL_ISCRALBO_SCARICA_RICHIESTA_RINNOVO"/></legend>
			</s:elseif>
			<s:elseif test="%{#aggiornamentoDatiDoc}">
				<legend><wp:i18n key="LABEL_ISCRALBO_SCARICA_RICHIESTA_AGGIORNAMENTO"/></legend>
			</s:elseif>
		
			<form action="${urlPdf}" method="post"> 
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
				
				<div class="fieldset-row first-row <c:if test="${!visMarcaBollo}">last-row</c:if>">
					<div class="label">
						<label><wp:i18n key="LABEL_LISTA_SOGGETTI_DIRITTO_FIRMA"/> : <span class="required-field">*</span></label>
					</div>
					<div class="element">
						<ul class="list">
							<s:iterator var="firmatario" value="%{#helper.listaFirmatariMandataria}" status="status">													
								<li
									class='<s:if test="%{#status.first}">first</s:if> <s:if test="%{#status.last}">last</s:if>'>
									<input type="radio" name="firmatarioSelezionato"
										id='firmatarioSelezionato<s:property value="%{#status.index}"/>'
										value='<s:if test="%{#firmatario.lista != null}"><s:property value="#firmatario.lista" />-<s:property value="#firmatario.index" /></s:if>'
										<s:if test="%{(#status.count==1 && #helper.listaFirmatariMandataria.size==1) || 
									              	  (#status.index==#helper.idFirmatarioSelezionatoInLista) }"> checked="checked" 
									    </s:if> 
									    <s:if test="%{tipoQualificaCodifica != null}">
									    title="<s:property value="%{#firmatario.nominativo + ' (' + tipoQualificaCodifica.get(#status.index) + ')'}" />"
										</s:if>
										<s:else>
									    title="<s:property value="%{#firmatario.nominativo}" />"
										</s:else>
									/> 
									<s:if test="%{tipoQualificaCodifica != null}">
										<s:property value="%{#firmatario.nominativo + ' (' + tipoQualificaCodifica.get(#status.index) + ')'}" />
									</s:if>
									<s:else>
										<s:property value="%{#firmatario.nominativo}" />
									</s:else>
								</li>
							</s:iterator>
						</ul>
					</div>
				</div>
				
				<s:if test="%{!#aggiornamentoDatiDoc}">
					<c:if test="${visMarcaBollo}">
						<div class="fieldset-row last-row">
							<div class="label">
								<label for="serialNumberMarcaBollo"><wp:i18n key="LABEL_ISCRALBO_NUMERO_MARCA_BOLLO"/> : <span class="required-field">*</span></label>
							</div>
							<div class="element">
								<s:textfield name="serialNumberMarcaBollo" size="20" maxlength="14" />
								<div class="note">
									<c:choose>
										<c:when test="${manMarcaBollo}">
											<wp:i18n key="LABEL_NOTA_ND_NON_PREVISTO" />
										</c:when>
										<c:otherwise>
											<wp:i18n key="LABEL_NOTA_INDICARE_ND_SE_NON_PREVISTO" />											
										</c:otherwise>
									</c:choose>
								</div>
							</div>
						</div>
					</c:if>
				</s:if>

				<div class="azioni">
					<s:if test="%{!#helper.aggiornamentoIscrizione || #aggiornamentoDatiDoc}">
						<c:if test="${visGeneraDomandaIscrPDF || visGeneraDomandaRinnPDF}">
							<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_generate_pdf.jsp" >								
								<jsp:param name="title" value="TITLE_WIZARD_GENERA_PDF_ISCRIZIONE_ELENCO"/>
								<jsp:param name="onclick" value="removeInvalidAttribute" />
							</jsp:include>
						</c:if>
					</s:if>
				</div> 
			</form> 
		</fieldset>
	</s:else>
	<s:if test="%{ListaFirmatariMandatariaVisible}">
		<fieldset>
			<legend><wp:i18n key="LABEL_FIRMATARIO"/> <s:property value="%{#helper.datiPrincipaliImpresa.ragioneSociale}"/></legend>
			<c:if test="${sessionIdObj eq 'dettIscrAlbo'}">
				<c:set var="path" value="/ExtStr2/do/FrontEnd/IscrAlbo/saveFirmatarioMandataria.action"/>
			</c:if>
			<c:if test="${sessionIdObj eq 'dettRinnAlbo'}">
				<c:set var="path" value="/ExtStr2/do/FrontEnd/IscrAlbo/saveRinnovoFirmatarioMandataria.action"/>
			</c:if>
			<form action="<wp:action path='${path}'/>"  method="post">
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
				
				<p><wp:i18n key="LABEL_LISTA_SOGGETTI_DIRITTO_FIRMA"/> : </p>
				<ul class="list">
					<s:iterator var="firmatario" value="%{#helper.listaFirmatariMandataria}" status="status">
						<li
							class='<s:if test="%{#status.first}">first</s:if> <s:if test="%{#status.last}">last</s:if>'>
							<input type="radio" name="firmatarioSelezionato"
									id='firmatarioSelezionato<s:property value="%{#status.index}"/>'
									value='<s:if test="%{#firmatario.lista != null}"><s:property value="#firmatario.lista" />-<s:property value="#firmatario.index" /></s:if>'
									<s:if test="%{(#status.count==1 && #helper.listaFirmatariMandataria.size==1) || (#status.index==#helper.idFirmatarioSelezionatoInLista) }"> checked="checked"
									</s:if>
								    title="<s:property value="%{#firmatario.nominativo + ' (' + tipoQualificaCodifica.get(#status.index) + ')'}" />"
							 		/> <s:property value="%{#firmatario.nominativo + ' (' + tipoQualificaCodifica.get(#status.index) + ')'}" />
						</li>
					</s:iterator>
				</ul>
	
				<div class="azioni">
					<input type="submit" id="firmatarioMandataria" class="button" 
						value="<wp:i18n key="BUTTON_REFRESH"/>" title='<wp:i18n key="TITLE_AGGIORNA_FIRMATARIO_MANDANTE"/>' />
				</div>
			 </form> 
		</fieldset>
	</s:if>
	<s:elseif test="%{inputFirmatarioMandanteVisible}">
	
		<s:set name="key" value="%{getFirmatario(currentMandante)}"/>
		
		<c:if test="${sessionIdObj eq 'dettIscrAlbo'}">
			<c:set var="path" value="/ExtStr2/do/FrontEnd/IscrAlbo/saveFirmatarioMandante.action"/>
		</c:if>
		<c:if test="${sessionIdObj eq 'dettRinnAlbo'}">
			<c:set var="path" value="/ExtStr2/do/FrontEnd/IscrAlbo/saveRinnovoFirmatarioMandante.action"/>
		</c:if>
		
		<fieldset>
			<legend><wp:i18n key="LABEL_FIRMATARIO"/> <s:property value="%{currentMandante.ragioneSociale}"/></legend>

			<form action="<wp:action path='${path}'/>&amp;id=<s:property value='%{id}'/>"
					method="post">
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
				
				<div class="fieldset-row first-row">
					<div class="label">
						<label><wp:i18n key="LABEL_RAGIONE_SOCIALE" /> : </label>
					</div>
					<div class="element"><s:property value="%{currentMandante.ragioneSociale}"/></div>
				</div>
				<div class="fieldset-row">
					<div class="label">
						<label><wp:i18n key="LABEL_TIPO_IMPRESA" /> : </label>
					</div>
					<div class="element"><s:property value="%{tipoImpresaCodifica}"/></div>
				</div>
				
				<s:if test='%{currentMandante.ambitoTerritoriale == "2"}' >
					<!-- operatore estero/extra EU -->
					<div class="fieldset-row">
						<div class="label">
							<label><wp:i18n key="LABEL_IDENTIFICATIVO_FISCALE_ESTERO" /> : </label>
						</div>
						<div class="element"><s:property value="%{currentMandante.idFiscaleEstero}"/></div>
					</div>
				</s:if>
				<s:else>
					<!-- operatore italiano -->
					<div class="fieldset-row">
						<div class="label">
							<label><wp:i18n key="LABEL_CODICE_FISCALE" /> : </label>
						</div>
						<div class="element"><s:property value="%{currentMandante.codiceFiscale}"/></div>
					</div>
					<div class="fieldset-row">
						<div class="label">
							<label><wp:i18n key="LABEL_PARTITA_IVA" /> : </label>
						</div>
						<div class="element"><s:property value="%{currentMandante.partitaIVA}"/></div>
					</div>
				</s:else>

				<div class="fieldset-row">
					<div class="label">
						<label><wp:i18n key="LABEL_NOMINATIVO" /> : </label>
					</div>
					<div class="element">
						<div class="contents-group">
							<label for="cognome"><wp:i18n key="LABEL_COGNOME" /> : <span
								class="required-field">*</span></label>
							<s:set var="classBloccoCampo" value="" />
							<s:textfield name="cognome" id="cognome" value="%{#key.cognome}"
									size="20" maxlength="80" />
								<label for="nome"><wp:i18n key="LABEL_NOME" /> : <span class="required-field">*</span></label>
							<s:textfield name="nome" id="nome" value="%{#key.nome}" size="20"
									maxlength="80" />
						</div> 
					</div>
				</div>

				<div class="fieldset-row">
					<div class="label">
						<label><wp:i18n key="LABEL_DATI_NASCITA" /> : </label>
					</div>
					<div class="element">
						<div class="contents-group">
							<label for="dataNascita"><wp:i18n key="LABEL_DATI_NASCITA_NATO_IL" /> (<wp:i18n key="LABEL_FORMATO_DATA" />) : <span
								class="required-field">*</span></label>
							<s:textfield name="dataNascita" id="dataNascita"
								value="%{#key.dataNascita}" size="10" maxlength="10" />
							<label for="comuneNascita"> <wp:i18n key="LABEL_DATI_NASCITA_NATO_A" /> : <span
								class="required-field">*</span></label>
							<s:textfield name="comuneNascita" id="comuneNascita"
								value="%{#key.comuneNascita}" size="20" maxlength="32" />
						</div>
						<div class="contents-group">
							<label for="provinciaNascita"> <wp:i18n key="LABEL_PROVINCIA" /> :</label>
							<s:select list="maps['province']" name="provinciaNascita"
								id="provinciaNascita" value="%{#key.provinciaNascita}" headerKey=""
								headerValue="%{#attr.headerValueProvincia}" >
							</s:select>
						</div>
					</div>
				</div>
<%--				
				<div class="fieldset-row">
					<div class="label">
						<label for="sesso"><wp:i18n key="LABEL_SESSO" /> : </label>
					</div>
					<div class="element">
						<wp:i18n key="OPT_CHOOSE_SESSO" var="headerValueSesso" />
						<s:select list="maps['sesso']" name="sesso" id="sesso"
							value="%{#key.sesso}" headerKey="" headerValue="%{#attr.headerValueSesso}" >
						</s:select>
					</div>
				</div>
--%>
				<div class="fieldset-row">
					<div class="label">
						<label for="codiceFiscale"><wp:i18n key="LABEL_CODICE_FISCALE" /> : <span
							class="required-field">*</span></label>
					</div>
					<div class="element">
						<s:set var="classBloccoCampo" value="" />
						<s:if test="%{readonlyCodiceFiscale}">
							<s:set var="classBloccoCampo" value="%{'no-editable'}" />
						</s:if>
						<s:textfield name="codiceFiscale" id="codiceFiscale"
							value="%{#key.codiceFiscale}" size="20" maxlength="16"
							readonly="%{readonlyCodiceFiscale}"
							cssClass="%{#classBloccoCampo}" />
					</div>
				</div>

				<div class="fieldset-row">
					<div class="label">
						<label><wp:i18n key="LABEL_RESIDENZA"/> : </label>
					</div>
					<div class="element">
						<div class="contents-group">
							<label for="indirizzo"><wp:i18n key="LABEL_INDIRIZZO" /> : <span class="required-field">*</span></label>
							<s:textfield name="indirizzo" id="indirizzo" value="%{#key.indirizzo}"
								size="50" maxlength="100" />
							<label for="numCivico"><wp:i18n key="LABEL_NUM_CIVICO" /> : <span class="required-field">*</span></label>
							<s:textfield name="numCivico" id="numCivico" value="%{#key.numCivico}"
								size="4" maxlength="10" />
						</div>
						<div class="contents-group">
							<label for="cap"><wp:i18n key="LABEL_CAP" /> : <span class="required-field">*</span></label>
							<s:textfield name="cap" id="cap" value="%{#key.cap}" size="5"
								maxlength="5" />
							<label for="comune"><wp:i18n key="LABEL_COMUNE" /> : <span
								class="required-field">*</span></label>
							<s:textfield name="comune" id="comune" value="%{#key.comune}"
								size="30" maxlength="32" />
						</div>
						<div class="contents-group">
							<label for="provincia"><wp:i18n key="LABEL_PROVINCIA" /> :</label>
							<s:select list="maps['province']" name="provincia" id="provincia"
								value="%{#key.provincia}" headerKey=""
								headerValue="%{#attr.headerValueProvincia}" >
							</s:select>
							<div class="note"><wp:i18n key="LABEL_NOTA_PROVINCIA_SOLO_PER_ITALIA" /></div>
						</div>
						<div class="contents-group">
							<label for="nazione"><wp:i18n key="LABEL_NAZIONE" /> : <span
								class="required-field">*</span></label>
							<wp:i18n key="OPT_CHOOSE_NAZIONE" var="headerValueNazione" />
							<s:select list="maps['nazioni']" name="nazione" id="nazione" value="{#key.nazione}"
								headerKey="" headerValue="%{#attr.headerValueNazione}" ></s:select>
						</div>
					</div>
				</div>
				<s:if test="%{!mandanteLiberoProfessionista}">
					<div class="fieldset-row last-row">
						<div class="label">
							<label><wp:i18n key="LABEL_INCARICO" /> : </label>
						</div>
						<div class="element">
							<div class="contents-group">
								<label for="soggettoQualifica"><wp:i18n key="LABEL_QUALIFICA" /> : <span
									class="required-field">*</span></label>
								<wp:i18n key="OPT_CHOOSE_QUALIFICA" var="headerValueQualifica" />
								<wp:i18n key="OPT_GROUP_ALTRE_CARICHE" var="labelAltreCariche" />
								<s:select list="maps['tipiSoggetto']" name="soggettoQualifica" id="soggettoQualifica" value="%{#key.soggettoQualifica}"
									headerKey="" headerValue="%{#attr.headerValueQualifica}" >
									<s:optgroup label="%{#attr.labelAltreCariche}" list="maps['tipiAltraCarica']"></s:optgroup>
								</s:select>
							</div>
						</div>
					</div>
				</s:if>
				<div class="azioni">
					<wp:i18n key="TITLE_AGGIORNA_FIRMATARIO_MANDANTE" var="titleAggiornaFirmatarioMandante" />
					<input type="hidden" name="obbQualifica" value="${!mandanteLiberoProfessionista}" />
					<input type="submit" id="firmatarioMandante" value="<wp:i18n key="BUTTON_REFRESH" />"
						title="<s:property value='%{#attr.titleAggiornaFirmatarioMandante}'/>"
						class="button" />
				</div>
			</form>
		</fieldset>
	</s:elseif>

	<form action="<wp:action path='${nextAction}' />" method="post">
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
		
		<div class="azioni">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_previous.jsp" />
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_next.jsp" />
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_cancel.jsp" />
		</div>
	</form>
</div>