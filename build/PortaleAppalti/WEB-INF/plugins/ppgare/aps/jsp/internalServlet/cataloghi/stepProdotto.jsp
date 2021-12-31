<%@ taglib prefix="wp"  uri="aps-core.tld" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="s"   uri="/struts-tags" %>

<s:set var="helperProdotto" value="%{#session['dettProdotto']}" />
<s:set var="helperArticolo" value="%{#session['dettProdotto'].articolo.dettaglioArticolo}" />
<%--
helperProdotto=<s:property value="%{#helperProdotto}"/><br/>
helperArticolo=<s:property value="%{#helperArticolo}"/><br/>
--%>

<s:if test="%{!#session.dettProdotto.aggiornamento}">
	<c:set var="codiceBalloon" value="BALLOON_WIZ_PRODOTTO_INS_PRODOTTO"/>
</s:if>
<s:else>
	<c:set var="codiceBalloon" value="BALLOON_WIZ_PRODOTTO_AGG_PRODOTTO"/>
</s:else>	


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<s:if test="%{!#session.dettProdotto.aggiornamento}">
		<h2><wp:i18n key="TITLE_PAGE_STEP_INSERISCI_PRODOTTO_CATALOGO" /></h2>
	</s:if>
	<s:else>
		<h2>
			<s:if test="%{!#session.dettProdotto.inCarrello}">
				<wp:i18n key="TITLE_PAGE_STEP_AGGIORNA_PRODOTTO_CATALOGO" />
			</s:if>
			<s:else>
				<wp:i18n key="TITLE_PAGE_STEP_AGGIORNA_PRODOTTO_CARRELLO" />
			</s:else>
		</h2>
	</s:else>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/field_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />

	<jsp:include page="stepsProdotto.jsp" />

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="${codiceBalloon}"/>
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/skip_form_buttons.jsp" />

	<form action="<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/processPageDefinizioneProdotto.action" />" method="post">
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
		
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/mandatory_fields_message.jsp" />

		<%-- variabili usate per controlli di pagina --%>
		<s:set var="bloccoEdit">${param.noEdit}</s:set>
		<c:if test="${param.noEdit}">
			<s:set var="classBlocco" value="%{'no-editable'}" />
		</c:if>
		<s:set var="bene" value="%{#helperArticolo.tipo == TIPO_PRODOTTO_BENE}"/>
		<s:set var="garanziaRichiesta" value="%{#helperArticolo.obbligoGaranzia}"/>
		<s:set var="prezzoPerUnitaMisura" value="%{#helperArticolo.prezzoUnitarioPer == TIPO_PREZZO_UNITA_DI_MISURA}"/>
		<%-- --%>

		<fieldset>
			<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_DATI_GENERALI_ARTICOLO" /></legend>

			<div class="fieldset-row first-row">
				<div class="label">
					<label><wp:i18n key="LABEL_TIPO_ARTICOLO" /> : </label>
				</div>
				<div class="element">
					<s:iterator value="maps['tipiArticolo']">
						<s:if test="%{key == #helperArticolo.tipo}">
							<s:property value="%{value}" />
						</s:if>
					</s:iterator>
				</div>
			</div>

			<div class="fieldset-row">
				<div class="label">
					<label><wp:i18n key="LABEL_DETTAGLIO_ARTICOLO_CODICE" /> : </label>
				</div>
				<div class="element">
					<s:property value="%{#helperArticolo.codice}" />
				</div>
			</div>

			<div class="fieldset-row">
				<div class="label">
					<label><wp:i18n key="LABEL_DETTAGLIO_ARTICOLO_DESCRIZIONE" /> : </label>
				</div>
				<div class="element">
					<s:property value="%{#helperArticolo.descrizione}" />
				</div>
			</div>

			<div class="fieldset-row">
				<div class="label">
					<label><wp:i18n key="LABEL_DESCRIZIONE_TECNICA" /> : </label>
				</div>
				<div class="element">
					<s:if test="%{#helperArticolo.descrizioneTecnica != null}">
						<s:property value="%{#helperArticolo.descrizioneTecnica}" />
					</s:if>
					<s:else>
						&nbsp;
					</s:else>
				</div>
			</div>

			<s:if test="%{#bene}">
				<div class="fieldset-row">
					<div class="label">
						<label for="coloreArticolo"><wp:i18n key="LABEL_COLORE_ARTICOLO" /> : </label>
					</div>
					<div class="element">
						<s:if test="%{#helperArticolo.colore != null}" >
							<s:property value="%{#helperArticolo.colore}" />
						</s:if>
						<s:else>
							&nbsp;
						</s:else>
					</div>
				</div>
			</s:if>

			<div class="fieldset-row last-row">
				<div class="label">
					<label><wp:i18n key="LABEL_NOTE" /> : </label>
				</div>
				<div class="element">
					<s:if test="%{#helperArticolo.note != null}" >
						<s:property value="%{#helperArticolo.note}" />
					</s:if>
					<s:else>
						&nbsp;
					</s:else>
				</div>
			</div>
		</fieldset>

		<fieldset>
			<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_DATI_GENERALI_PRODOTTO" /></legend>
			
			<div class="fieldset-row first-row">
				<div class="label">
					<label for="codiceProdottoFornitore"><wp:i18n key="LABEL_CODICE_PRODOTTO_FORNITORE" /> : <span class="required-field">*</span></label>
				</div>
				<div class="element">
					<s:textfield name="codiceProdottoFornitore" id="codiceProdottoFornitore" value="%{codiceProdottoFornitore}" 
								 maxlength="30" size="50" aria-required="true" />
				</div>
			</div>
			
			<s:if test="%{#bene}">
				<div class="fieldset-row">
					<div class="label">
						<label for="marcaProdottoProduttore"><wp:i18n key="LABEL_MARCA_PRODOTTO_PRODUTTORE" /> : <span class="required-field">*</span></label>
					</div>
					<div class="element">
						<s:textfield name="marcaProdottoProduttore" id="marcaProdottoProduttore" value="%{marcaProdottoProduttore}" 
									 maxlength="60" size="50" aria-required="true" />
					</div>
				</div>
				<div class="fieldset-row">
					<div class="label">
						<label for="codiceProdottoProduttore"><wp:i18n key="LABEL_CODICE_PRODOTTO_PRODUTTORE" /> : <span class="required-field">*</span></label>
					</div>
					<div class="element">
						<s:textfield name="codiceProdottoProduttore" id="codiceProdottoProduttore" value="%{codiceProdottoProduttore}" 
									 maxlength="30" size="20" aria-required="true" />
					</div>
				</div>
			</s:if>
				
			<div class="fieldset-row last-row">
				<div class="label">
					<label for="nomeCommerciale"><wp:i18n key="LABEL_NOME_COMMERCIALE_PRODOTTO" /> : <span class="required-field">*</span></label>
				</div>
				<div class="element">
						<s:textfield name="nomeCommerciale" id="nomeCommerciale" value="%{nomeCommerciale}" 
									 maxlength="250" size="50" aria-required="true" />
				</div>
			</div>
		</fieldset>

		<fieldset>
			<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_SPECIFICHE_PRODOTTO" /></legend>

			<div class='fieldset-row first-row <s:if test="%{!#bene && !#garanziaRichiesta}">last-row</s:if>'>
				<div class="label">
					<label for="descrizioneAggiuntiva"><wp:i18n key="LABEL_DESCRIZIONE_AGGIUNTIVA" /> : 
						<s:if test="%{#helperArticolo.obbligoDescrizioneAggiuntiva}">
							<span class="required-field">*</span>
						</s:if>
					</label>
				</div>
				<div class="element">
					<c:set var="requiredDescrizione">${helperArticolo.obbligoDescrizioneAggiuntiva}</c:set>
					<s:textarea name="descrizioneAggiuntiva" id="descrizioneAggiuntiva" value="%{descrizioneAggiuntiva}" 
									rows="3" cols="68" aria-required="${requiredDescrizione}" />
				</div>
			</div>

			<s:if test="%{#bene}">
				<div class='fieldset-row <s:if test="%{!#garanziaRichiesta}">last-row</s:if>'>
					<div class="label">
						<label for="dimensioni"><wp:i18n key="LABEL_DIMENSIONI" /> : 
							<s:if test="%{#helperArticolo.obbligoDimensioni}">
								<span class="required-field">*</span>
							</s:if>
						</label>
					</div>
					<div class="element">
						<c:set var="requiredDimensione">${helperArticolo.obbligoDimensioni}</c:set>					
						<s:textfield name="dimensioni" id="dimensioni" value="%{dimensioni}" 
									 maxlength="60" size="20" aria-required="${requiredDimensione}" />
					</div>
				</div>
			</s:if>
			
			<s:if test="%{#garanziaRichiesta}">
				<div class="fieldset-row last-row">
					<div class="label">
						<label for="garanzia"><wp:i18n key="LABEL_PERIODO_GARANZIA_DI_MESI" /> : 
							<span class="required-field">*</span>
						</label>
					</div>
					<div class="element">
						<s:textfield name="garanzia" id="garanzia" value="%{garanzia != null ? garanzia : DEFAULT_NUM_MESI_GARANZIA}" 
									 maxlength="3" size="20" aria-required="true" />
					</div>
				</div>
			</s:if>
		</fieldset>

		<fieldset>
			<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_PREZZI_QUANTITA" /></legend>

			<div class="fieldset-row first-row">
				<div class="label">
					<label><wp:i18n key="LABEL_UNITA_MISURA_DEL_PREZZO" /> : </label>
				</div>
				<div class="element">
					<s:iterator value="maps['tipiUnitaMisura']">
						<s:if test="%{key == #helperArticolo.unitaMisuraDetermPrezzo}">
							<s:property value="%{value}" />
						</s:if>
					</s:iterator>
				</div>
			</div>
			
			<div class="fieldset-row">
				<div class="label">
					<label for="prezzoUnitario"><wp:i18n key="LABEL_PREZZO" /> : <span class="required-field">*</span></label>
				</div>
				<div class="element">
					<div class="contents-group">
						<s:textfield name="prezzoUnitario" id="prezzoUnitario" value="%{prezzoUnitario}" 
												 maxlength="16" size="20" aria-required="true" />
						<s:if test="%{#helperArticolo.numDecimaliDetermPrezzo != null}">
							&nbsp;( <wp:i18n key="LABEL_MASSIMO" /> <s:property value="%{#helperArticolo.numDecimaliDetermPrezzo}" /> <wp:i18n key="LABEL_DECIMALI" /> )
						</s:if>
					</div>
					<s:if test="%{#helperProdotto.catalogo.datiGeneraliBandoIscrizione.prezziVisibili && migliorPrezzoAOE != null && migliorPrezzoAOE > 0}">
						<div class="contents-group">
							<wp:i18n key="LABEL_MIGLIOR_PREZZO_OFFERTO_PER_ARTICOLO" /> : <s:text name="format.money"><s:param value="%{migliorPrezzoAOE}"/></s:text>&nbsp;&euro;
						</div>
					</s:if>
					<div class="contents-group">
						<s:property value="%{prodottiCaricatiAOE}"/> <wp:i18n key="LABEL_PRODOTTI_INSERITI_PER_ARTICOLO_DA_ALTRI_OE" />
					</div>
				</div>
			</div>
					
			<s:if test="%{#prezzoPerUnitaMisura}">
				<div class="fieldset-row">
					<div class="label">
						<label for="quantitaUMPrezzo"><wp:i18n key="LABEL_NUM_UNITA_PREZZO_ACQUISTO" /> : <span class="required-field">*</span></label>
					</div>
					<div class="element">
						<s:textfield name="quantitaUMPrezzo" id="quantitaUMPrezzo" value="%{quantitaUMPrezzo}" 
									 maxlength="16" size="20" aria-required="true" />
					</div>
				</div>					
				<div class="fieldset-row">
					<div class="label">
						<label for="unitaMisuraAcquisto"><wp:i18n key="LABEL_UNITA_MISURA_ACQUISTO" /> : </label>
					</div>
					<div class="element">
						<s:iterator value="maps['tipiUnitaMisura']">
							<s:if test="%{key == #helperArticolo.unitaMisuraAcquisto}">
								<s:property value="%{value}" />
							</s:if>
						</s:iterator>
					</div>
				</div>						
			</s:if>

			<div class="fieldset-row">
				<div class="label">
					<label for="aliquotaIVA"><wp:i18n key="LABEL_ALIQUOTA_IVA" /> : <span class="required-field">*</span></label>
				</div>
				<div class="element">
					<wp:i18n key="OPT_CHOOSE_ALIQUOTA_IVA" var="headerValueAliquotaIva" />
					<s:select name="aliquotaIVA" id="aliquotaIVA" list="maps['aliquoteIVA']" value="%{aliquotaIVA}" 
							  headerKey="" headerValue="%{#attr.headerValueAliquotaIva}" 
							  aria-required="true" >
					</s:select>
				</div>
			</div>
			
			<s:if test="%{#helperArticolo.prezzoUnitarioPer != TIPO_PREZZO_PRODOTTO_SERVIZIO_PER_UM }">
				<div class="fieldset-row last-row">
					<div class="label">
						<label for="quantitaUMAcquisto"><wp:i18n key="LABEL_LOTTO_MINIMO_UNITA_MISURA" /> : 
							<s:if test="%{#helperArticolo.prezzoUnitarioPer != TIPO_PREZZO_CONFEZIONE }">
								<span class="required-field">*</span>
							</s:if>
						</label>
					</div>
					<div class="element">
						<s:if test="%{#helperArticolo.prezzoUnitarioPer != TIPO_PREZZO_CONFEZIONE}">
							<s:textfield name="quantitaUMAcquisto" id="quantitaUMAcquisto" value="%{quantitaUMAcquisto}" 
										 maxlength="16" size="20" aria-required="true" />
							<s:if test="%{#helperArticolo.quantitaMinimaUnitaAcquisto != null || #helperArticolo.quantitaMassimaUnitaAcquisto != null}">
								(
							</s:if>
							<s:if test="%{#helperArticolo.quantitaMinimaUnitaAcquisto != null}">
								<wp:i18n key="LABEL_INDICARE_MINIMO" /> <s:text name="format.money"><s:param value="%{#helperArticolo.quantitaMinimaUnitaAcquisto}"/></s:text>
							</s:if>
							<s:if test="%{#helperArticolo.quantitaMinimaUnitaAcquisto != null && #helperArticolo.quantitaMassimaUnitaAcquisto != null}">
								<wp:i18n key="LABEL_E" />
							</s:if>
							<s:elseif test="%{#helperArticolo.quantitaMassimaUnitaAcquisto != null}">
								<wp:i18n key="LABEL_INDICARE" /> 
							</s:elseif>
							<s:if test="%{#helperArticolo.quantitaMassimaUnitaAcquisto != null}">
								<wp:i18n key="LABEL_MASSIMO" /> <s:text name="format.money"><s:param value="%{#helperArticolo.quantitaMassimaUnitaAcquisto}"/></s:text>
							</s:if>
							<s:if test="%{#helperArticolo.quantitaMinimaUnitaAcquisto != null || #helperArticolo.quantitaMassimaUnitaAcquisto != null}">
								)
							</s:if>
						</s:if>
						<s:else>
							<s:property value="%{#helperArticolo.quantitaUnitaAcquisto}"/>
						</s:else>
					</div>
				</div>
			</s:if>
		</fieldset>

		<fieldset>
			<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_ALTRI_DATI" /></legend>

			<div class="fieldset-row first-row">
				<div class="label">
					<label for="tempoConsegna"><wp:i18n key="LABEL_TEMPO_CONSEGNA" /> : 
						<span class="required-field">*</span>
					</label>
				</div>
				<div class="element">
					<s:textfield name="tempoConsegna" id="tempoConsegna" value="%{tempoConsegna}" 
								 maxlength="7" size="20" aria-required="true" />&nbsp;
					( <wp:i18n key="LABEL_INDICARE_MASSIMO" /> <s:property value="%{#helperArticolo.tempoMaxConsegna}" />
					<s:iterator value="maps['tipiUnitaMisuraTempiConsegna']">
						<s:if test="%{key == #helperArticolo.unitaMisuraTempoConsegna}"><s:property value="%{value.toLowerCase()}"/></s:if>
					</s:iterator>
					)
				</div>
			</div>

			<div class="fieldset-row last-row">
				<div class="label">
					<label for="dataScadenzaOfferta"><wp:i18n key="LABEL_DATA_SCADEZA_OFFERTA" /> :
						<span class="required-field">*</span>
					</label>
				</div>
				<div class="element">
					<s:textfield name="dataScadenzaOfferta" id="dataScadenzaOfferta" value="%{dataScadenzaOfferta}" 
								 maxlength="10" size="20" aria-required="true" />
				</div>
			</div>
		</fieldset>

		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/skip_main_content_from_buttons.jsp" />

		<div class="azioni">
			<s:hidden name="tipoArticolo" value="%{#helperArticolo.tipo}"/>
			<s:hidden name="obbligoDescrizioneAggiuntiva" value="%{#helperArticolo.obbligoDescrizioneAggiuntiva}"/>
			<s:hidden name="obbligoDimensioni" value="%{#helperArticolo.obbligoDimensioni}"/>
			<s:hidden name="obbligoGaranzia" value="%{#garanziaRichiesta}"/>
			<s:hidden name="numDecPrezzo" value="%{#helperArticolo.numDecimaliDetermPrezzo}"/>
			<s:hidden name="prezzoUnitarioPer" value="%{#helperArticolo.prezzoUnitarioPer}"/>
			<s:hidden name="qtaMinDetPz" value="%{#helperArticolo.quantitaMinimaUnitaAcquisto}"/>
			<s:hidden name="qtaMaxDetPz" value="%{#helperArticolo.quantitaMassimaUnitaAcquisto}"/>
			<s:hidden name="tempoMaxConsegna" value="%{#helperArticolo.tempoMaxConsegna}"/>
			<s:if test="%{#session.dettProdotto.aggiornamento == null || !#session.dettProdotto.aggiornamento}">
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_previous.jsp" />
			</s:if>
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_next.jsp" />
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_cancel.jsp" />
			<input type="hidden" name="ext" value="${param.ext}" />
		</div>
	</form>
</div>