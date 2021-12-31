<%@ taglib prefix="wp"  uri="aps-core.tld" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="s"   uri="/struts-tags" %>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>


<%-- Recupera le customizzazioni presenti --%>
<es:checkCustomization var="visPassoe" objectId="GARE-DOCUMOFFERTA" attribute="PASSOE" feature="VIS" />

<wp:i18n key="OPT_CHOOSE_PROVINCIA" var="headerValueProvincia" />

<s:set var="helper" value="%{#session['offertaEconomica']}" />


<script type="text/javascript" src="<wp:resourceURL/>static/js/jquery.min.js"></script>
<script>
$(document).ready(function () {	
	var visPassOE 	 = ${visPassoe};
	var txtPassOE 	 = '[name="passoe"]';
	var btnCreatePdf = '#createPdf';
	
	// se js abilitato rimuovo l'avviso che per essere usabile la pagina
	// serve js abilitato
	$('#noJs').remove();
	
	// se PassOE è visibile disabilita il pulsante di generazione PDF
	if(visPassOE == 1) {
		var value = $(txtPassOE).val().trim();
		if(value == '') {
			$(btnCreatePdf).hide();
		}
		synchHiddenInput($(txtPassOE));
	}
	
	// abilita il pulsante di generazione pdf solo se è stato inserito il PassOE
	//$('[name="passoe"]').change( function()
	//$('[name="passoe"]').on("change", function() {
	$(txtPassOE).bind('input', function() {	
		if(visPassOE == 1) { 
			var value = $(this).val().trim();
			if(value == '') {
				$(btnCreatePdf).hide();
			} else {
				$(btnCreatePdf).show();
			}
			synchHiddenInput($(this));
		}
	});

	// synchronize "hidden" input "passoe"
	function synchHiddenInput(txt) {
		$(txtPassOE).val( txt.val() );
	}

});
</script>


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2><wp:i18n key="TITLE_PAGE_GARETEL_OFFERTA_ECONOMICA" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/field_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />

	<jsp:include page="stepsOffertaTelematica.jsp" />
	
	<s:if test="%{#helper.componentiRTI.size == 0}">
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
			<jsp:param name="keyMsg" value="BALLOON_WIZ_SCARICA_OFFERTA_OFFERTA_TEL"/>
		</jsp:include>
	</s:if>
	<s:else>
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
			<jsp:param name="keyMsg" value="BALLOON_WIZ_SCARICA_OFFERTA_RTI_OFFERTA_TEL"/>
		</jsp:include>
	</s:else>
	
	<s:if test="%{#helper.datiModificati}">
		<div class="balloon">
			<div class="balloon-content balloon-alert">
				<wp:i18n key="LABEL_GARETEL_MODIFICHE_NON_SALVATE_A_DOCUMENTI" />
			</div>
		</div>
	</s:if>
	
	<s:set var="imgCheck"><wp:resourceURL/>static/img/check.svg</s:set>
	
	<s:if test="%{listaImpreseVisible}">
		<fieldset>
			<legend><wp:i18n key="LABEL_PARTECIPANTI_RTI" /></legend>
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
								<img class="resize-svg-16" src="${imgCheck}" title='<wp:i18n key="TITLE_FIRMATARIO_INSERITO_GENERAZIONE_PDF_PRONTA" />'/>
							</s:if>
						</td>
						<td>
							<s:property value="#componente.ragioneSociale"/>
						</td>
						<td>
							<s:if test="%{#key.nominativo!=null}">
								<s:property value="%{#key.nominativo}"/> 
							</s:if>
						</td>
						<td class="azioni">
							<s:if test="%{#status.index==0}">
								<s:if test="%{modificaFirmatarioVisible}">
									<a href='<wp:action path="/ExtStr2/do/FrontEnd/GareTel/editFirmatarioMandataria.action"/>&amp;${tokenHrefParams}' class="bkg modify" title='<wp:i18n key="BUTTON_EDIT" />'>
									</a>
								</s:if>
							</s:if>
							<s:else>
								<a href='<wp:action path="/ExtStr2/do/FrontEnd/GareTel/editFirmatarioMandante.action"/>&amp;id=${status.index}&amp;${tokenHrefParams}' class="bkg modify" title='<wp:i18n key="BUTTON_EDIT" />'>
								</a>
							</s:else>
						</td>
					</tr>
				</s:iterator>
			</table>
						
			<s:if test="%{allFirmatariInseriti && listaImpreseVisible}">
				
				<s:url id="urlPdfOffertaDownload" namespace="/do/FrontEnd/GareTel" action="createOffTelPdf">
					<s:param name="urlPage">${currentPageUrl}</s:param>
					<s:param name="currentFrame">${param.currentFrame}</s:param>
				</s:url>
				
				<form action="${urlPdfOffertaDownload}"  method="post">
					<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
					
					<c:if test="${visPassoe}">
						<div class="fieldset-row first-row last-row">
							<div class="label">
								<label for="passoe"><wp:i18n key="LABEL_GARETEL_PASSOE"/> : <span class="required-field">*</span></label>
							</div>
							<div class="element">
								<s:textfield name="passoe" size="30" maxlength="30" aria-required="true" />
								<div class="note"><wp:i18n key="LABEL_NOTA_PASSOE_INDICARE_ND_SE_NON_PREVISTO" /></div>
							</div>
						</div>
					</c:if>
					
					<div class="azioni">
						<input type="submit" id="createPdf" value='<wp:i18n key="BUTTON_WIZARD_GENERA_PDF_OFFERTA_ECONOMICA" />' 
							   title='<wp:i18n key="TITLE_GARETEL_GENERA_DF_OFFERTA_ECONOMICA" />' class="button" />
					</div>
				 </form>
			</s:if>
		</fieldset>	
	</s:if>
	<s:else>
		<fieldset>
			<legend><wp:i18n key="LABEL_GARETEL_STEP_SCARICA_OFFERTA" /></legend>
		
			<s:url id="urlPdfOffertaDownload" namespace="/do/FrontEnd/GareTel" action="createOffTelPdf">
				<s:param name="urlPage">${currentPageUrl}</s:param>
				<s:param name="currentFrame">${param.currentFrame}</s:param>
			</s:url>
	
			<form action="${urlPdfOffertaDownload}"  method="post">
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
				
				<label><wp:i18n key="LABEL_LISTA_SOGGETTI_DIRITTO_FIRMA"/> : </label>
				<ul class="list">
					<s:iterator var="firmatario" value="%{#helper.listaFirmatariMandataria}" status="status">
						<li
							class='<s:if test="%{#status.first}">first</s:if> <s:if test="%{#status.last}">last</s:if>'>
							<input type="radio" name="firmatarioSelezionato"
								   id='firmatarioSelezionato<s:property value="%{#status.index}"/>'
								   value='<s:if test="%{#firmatario.lista != null}"><s:property value="#firmatario.lista" />-<s:property value="#firmatario.index" /></s:if>'
								   <s:if test="%{(#status.count==1 && #helper.listaFirmatariMandataria.size==1) || (#status.index==#helper.idFirmatarioSelezionatoInLista) }"> checked="checked"
								   </s:if> 
								   <s:if test="%{tipoQualificaCodifica !=null}">
								   title="<s:property value="%{#firmatario.nominativo + ' (' + tipoQualificaCodifica.get(#status.index) + ')'}" />"
								   </s:if>
								   <s:else>
								   title="<s:property value="%{#firmatario.nominativo}" />"
								   </s:else>
								   /> 
							<s:if test="%{tipoQualificaCodifica !=null}">
								<s:property value="%{#firmatario.nominativo + ' (' + tipoQualificaCodifica.get(#status.index) + ')'}" />
							</s:if>
							<s:else>
								<s:property value="%{#firmatario.nominativo}" />
							</s:else>
						</li>
					</s:iterator>
				</ul>	
				
				<c:if test="${visPassoe}">
					<div class="fieldset-row first-row last-row">
						<div class="label">
							<label for="passoe"><wp:i18n key="LABEL_GARETEL_PASSOE"/> : <span class="required-field">*</span></label>
						</div>
						<div class="element">
							<s:textfield name="passoe" size="30" maxlength="30" aria-required="true" />
							<div class="note"><wp:i18n key="LABEL_NOTA_PASSOE_INDICARE_ND_SE_NON_PREVISTO" /></div>							
						</div>
					</div>
				</c:if>
				
				<div class="azioni">
					<input type="submit" id="createPdf" value='<wp:i18n key="BUTTON_WIZARD_GENERA_PDF_OFFERTA_ECONOMICA" />' 
						   title='<wp:i18n key="TITLE_GARETEL_GENERA_DF_OFFERTA_ECONOMICA" />' class="button" />
				</div>
			 </form> 
		</fieldset>
	</s:else>
	
	<s:if test="%{ListaFirmatariMandatariaVisible}">
		<fieldset>
			<legend><wp:i18n key="LABEL_FIRMATARIO" /> <s:property value="%{#session['dettAnagrImpresa'].datiPrincipaliImpresa.ragioneSociale}"/></legend>
	
			<form action="<wp:action path="/ExtStr2/do/FrontEnd/GareTel/saveFirmatarioMandataria.action"/>"  method="post">
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
							 	   />
							<s:property value="%{#firmatario.nominativo + ' (' + tipoQualificaCodifica.get(#status.index) + ')'}" />
						</li>
					</s:iterator>
				</ul>
	
				<input type="hidden" name="codice" value="<s:property value="%{#helper.codice}"/>"/>
				
				<div class="azioni">
					<input type="submit" id="firmatarioMandataria" value='<wp:i18n key="BUTTON_REFRESH" />' title='<wp:i18n key="TITLE_GARETEL_AGGIORNA_FIRMATARIO_MANDATARIA_RTI" />' class="button" />
				</div>
			 </form> 
		</fieldset>
	</s:if>
	<s:elseif test="%{inputFirmatarioMandanteVisible}">

		<s:set name="key" value="%{getFirmatario(currentMandante)}"/>
		 
		<fieldset>
			<legend><wp:i18n key="LABEL_FIRMATARIO" /> <s:property value="%{currentMandante.ragioneSociale}"/></legend>

			<form action="<wp:action path="/ExtStr2/do/FrontEnd/GareTel/saveFirmatarioMandante.action"/>&amp;id=<s:property value="%{id}"/>"
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

				<div class="fieldset-row">
					<div class="label">
						<label><wp:i18n key="LABEL_NOMINATIVO" /> : </label>
					</div>
					<div class="element">
						<div class="contents-group">
							<label for="cognome"><wp:i18n key="LABEL_COGNOME" /> : <span class="required-field">*</span></label>
							<s:set var="classBloccoCampo" value="" />
							<s:textfield name="cognome" id="cognome" value="%{#key.cognome}" size="20" maxlength="80" aria-required="true" />
							<label for="nome"><wp:i18n key="LABEL_NOME" /> : <span class="required-field">*</span></label>
							<s:textfield name="nome" id="nome" value="%{#key.nome}" size="20" maxlength="80" aria-required="true" />
						</div> 
					</div>
				</div>

				<div class="fieldset-row">
					<div class="label">
						<label><wp:i18n key="LABEL_DATI_NASCITA" /> : </label>
					</div>
					<div class="element">
						<div class="contents-group">
							<label for="dataNascita"><wp:i18n key="LABEL_DATI_NASCITA_NATO_IL" /> (<wp:i18n key="LABEL_FORMATO_DATA" />) : <span class="required-field">*</span></label>
							<s:textfield name="dataNascita" id="dataNascita" value="%{#key.dataNascita}" size="10" maxlength="10" aria-required="true" />
							<label for="comuneNascita"> <wp:i18n key="LABEL_DATI_NASCITA_NATO_A" /> : <span class="required-field">*</span></label>
							<s:textfield name="comuneNascita" id="comuneNascita" value="%{#key.comuneNascita}" size="20" maxlength="32" aria-required="true" />
						</div>
						<div class="contents-group">
							<label for="provinciaNascita"> <wp:i18n key="LABEL_PROVINCIA" /> :</label>
							<c:set var="lblProvNasc"><wp:i18n key="LABEL_PROVINCIA_NASCITA" /></c:set>
							<s:select list="maps['province']" name="provinciaNascita"
									id="provinciaNascita" value="%{#key.provinciaNascita}" headerKey=""
									headerValue="%{#attr.headerValueProvincia}" 
									aria-label="${lblProvNasc}" >
							</s:select>
						</div>
					</div>
				</div>
				
				<div class="fieldset-row">
					<div class="label">
						<label for="sesso"><wp:i18n key="LABEL_SESSO" /> : <span class="required-field">*</span></label>
					</div>
					<div class="element">
						<wp:i18n key="OPT_CHOOSE_SESSO" var="headerValueSesso" />
						<s:select list="maps['sesso']" name="sesso" id="sesso"
							value="%{#key.sesso}" headerKey="" headerValue="%{#attr.headerValueSesso}" aria-required="true" >
						</s:select>
					</div>
				</div>

				<div class="fieldset-row">
					<div class="label">
						<label for="codiceFiscale"><wp:i18n key="LABEL_CODICE_FISCALE" /> : <span class="required-field">*</span></label>
					</div>
					<div class="element">
						<s:set var="classBloccoCampo" value="" />
						<s:if test="%{readonlyCodiceFiscale}">
							<s:set var="classBloccoCampo" value="%{'no-editable'}" />
						</s:if>
						<s:textfield name="codiceFiscale" id="codiceFiscale"
							value="%{#key.codiceFiscale}" size="20" maxlength="16"
							readonly="%{readonlyCodiceFiscale}"
							cssClass="%{#classBloccoCampo}" aria-required="true" />
					</div>
				</div>

				<div class="fieldset-row">
					<div class="label">
						<label><wp:i18n key="LABEL_RESIDENZA" /> : </label>
					</div>
					<div class="element">
						<div class="contents-group">
							<label for="indirizzo"><wp:i18n key="LABEL_INDIRIZZO" /> : <span class="required-field">*</span></label>
							<s:textfield name="indirizzo" id="indirizzo" value="%{#key.indirizzo}" size="40" maxlength="60" aria-required="true" />
							<label for="numCivico"><wp:i18n key="LABEL_NUM_CIVICO" /> : <span class="required-field">*</span></label>
							<s:textfield name="numCivico" id="numCivico" value="%{#key.numCivico}" size="4" maxlength="10" aria-required="true" />
						</div>
						<div class="contents-group">
							<label for="cap"><wp:i18n key="LABEL_CAP" /> : <span class="required-field">*</span></label>
							<s:textfield name="cap" id="cap" value="%{#key.cap}" size="5" maxlength="5" aria-required="true" />
							<label for="comune"><wp:i18n key="LABEL_COMUNE" /> : <span class="required-field">*</span></label>
							<s:textfield name="comune" id="comune" value="%{#key.comune}" size="30" maxlength="32" aria-required="true" />
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
							<label for="nazione"><wp:i18n key="LABEL_NAZIONE" /> : <span class="required-field">*</span></label>
							<wp:i18n key="OPT_CHOOSE_NAZIONE" var="headerValueNazione" />
							<s:select list="maps['nazioni']" name="nazione" id="nazione" value="{#key.nazione}"
									  headerKey="" headerValue="%{#attr.headerValueNazione}" aria-required="true" >
							</s:select>
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
								<label for="soggettoQualifica"><wp:i18n key="LABEL_QUALIFICA" /> : <span class="required-field">*</span></label>
								<wp:i18n key="OPT_CHOOSE_QUALIFICA" var="headerValueQualifica" />
								<wp:i18n key="OPT_GROUP_ALTRE_CARICHE" var="labelAltreCariche" />
								<s:select list="maps['tipiSoggetto']" name="soggettoQualifica" id="soggettoQualifica" value="%{#key.soggettoQualifica}"
										  headerKey="" headerValue="%{#attr.headerValueQualifica}" aria-required="true" >
									<s:optgroup label="%{#attr.labelAltreCariche}" list="maps['tipiAltraCarica']"></s:optgroup>
								</s:select>
	 	
							</div>
						</div>
					</div>
				</s:if>
				
				<input type="hidden" name="codice" value="<s:property value="%{#helper.codice}"/>"/>
				<input type="hidden" name="obbQualifica" value="${!mandanteLiberoProfessionista}" />
				
				<div class="azioni">
					<input type="submit" id="firmatarioMandante" value='<wp:i18n key="BUTTON_REFRESH" />'
						   title='<wp:i18n key="TITLE_AGGIORNA_FIRMATARIO_MANDANTE" />'
						   class="button" />
				</div>
			</form>
		</fieldset>
	</s:elseif>

	<form action="<wp:action path="/ExtStr2/do/FrontEnd/GareTel/processPageOffTelScaricaOfferta.action" />" method="post">		
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
		
		<div class="azioni">
			<%-- sincronizza con il valore dell'input "passoe" via jquery (vedi script in testa) --%>
			<input type="hidden" name="codice" value="<s:property value="%{#helper.codice}"/>"/>
			<input type="hidden" name="passoe" />
		
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_previous.jsp" />
			
			<s:if test="%{!inputFirmatarioMandanteVisible}">
				<s:if test="%{#helper.datiModificati}">
					<wp:i18n key="BUTTON_SAVE" var="valueSaveButton" />
					<s:submit value="%{#attr.valueSaveButton}" title="%{#attr.valueSaveButton}" cssClass="button" method="save"></s:submit>
				</s:if>
			</s:if>

			<s:if test="%{!ListaFirmatariMandatariaVisible && !inputFirmatarioMandanteVisible}">
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_next.jsp" />
			</s:if>
			
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_cancel.jsp" />
		</div>
	</form>
</div>