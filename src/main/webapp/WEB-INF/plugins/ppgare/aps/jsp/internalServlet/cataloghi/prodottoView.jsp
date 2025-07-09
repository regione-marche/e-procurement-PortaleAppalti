<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>


<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/send_blockUI.jsp" />

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<s:if test="%{inCarrello}">
		<s:if test="%{PRODOTTO_INSERITO.equals(statoProdotto)}">
			<h2><wp:i18n key="TITLE_PAGE_PRODOTTO_INSERITO_CATALOGO" /></h2>
		</s:if>
		<s:elseif test="%{PRODOTTO_MODIFICATO.equals(statoProdotto)}">
			<h2><wp:i18n key="TITLE_PAGE_PRODOTTO_MODIFICATO_CATALOGO" /></h2>
		</s:elseif>
		<s:elseif test="%{PRODOTTO_ELIMINATO.equals(statoProdotto)}">
			<h2><wp:i18n key="TITLE_PAGE_PRODOTTO_ELIMINATO_CATALOGO" /></h2>
		</s:elseif>
		<s:else>
			<h2><wp:i18n key="TITLE_PAGE_NUOVO_PRODOTTO_BOZZA_CATALOGO" /></h2>
		</s:else>
	</s:if>
	<s:else>
		<h2><wp:i18n key="TITLE_PAGE_DETTAGLIO_PRODOTTO_CATALOGO" /></h2>
	</s:else>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_DETTAGLIO_PRODOTTO"/>
	</jsp:include>
		
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
		
	<s:if test="%{reinsert}">
		<p class="question">
			<wp:i18n key="LABEL_REINSERIRE_PRODOTTO_A_CATALOGO" />
		</p>
		<div class="azioni">
			<form action="<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/reinsertProdotto.action" />" method="post" class="azione">
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
				<div>
					<input type="hidden" name="ext" value="${param.ext}" />
					<input type="hidden" name="prodottoId" value="${prodottoId}" />
					<wp:i18n key="LABEL_YES" var="valueButtonYes" />
					<wp:i18n key="TITLE_CONFIRM_DEL_YES" var="titleButtonYes" />
					<s:submit value="%{#attr.valueButtonYes}" title="%{#attr.titleButtonYes}" cssClass="button block-ui"></s:submit>
				</div>
			</form>
			<form action="<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/cancelReinsertProdotto.action" />" method="post" class="azione">
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
				<div>
					<input type="hidden" name="ext" value="${param.ext}" />
					<input type="hidden" name="prodottoId" value="${prodottoId}" />
					<wp:i18n key="LABEL_NO" var="valueButtonNo" />
					<wp:i18n key="BUTTON_WIZARD_CANCEL" var="titleButtonNo" />
					<s:submit value="%{#attr.valueButtonNo}" title="%{#attr.titleButtonNo}" cssClass="button block-ui"></s:submit>
				</div>
			</form>
		</div>
	</s:if>
	<s:elseif test="%{undo}">
		<p class="question">
			<s:if test="%{PRODOTTO_INSERITO.equals(prodotto.statoProdotto)}">
				<wp:i18n key="LABEL_CATALOGHI_ANNULLARE_INSERIMENTO" />
			</s:if>
			<s:elseif test="%{PRODOTTO_MODIFICATO.equals(prodotto.statoProdotto)}">
				<wp:i18n key="LABEL_CATALOGHI_ANNULLARE_MODIFICA" />
			</s:elseif>
			<s:elseif  test="%{PRODOTTO_ELIMINATO.equals(prodotto.statoProdotto)}">
				<wp:i18n key="LABEL_CATALOGHI_ANNULLARE_CANCELLAZIONE" />
			</s:elseif>
			<s:else>
				<wp:i18n key="LABEL_CATALOGHI_ELIMINARE_BOZZA" />
			</s:else>
			<s:if test="%{TIPO_PRODOTTO_BENE==prodotto.articolo.dettaglioArticolo.tipoProdotto}">
				<wp:i18n key="LABEL_DEL_PRODOTTO" /> &quot;<s:property value="%{prodotto.nomeProdotto}" />&quot; ?
			</s:if>
			<s:else>
				<wp:i18n key="LABEL_DEL_SERVIZIO" /> &quot;<s:property value="%{prodotto.articolo.dettaglioArticolo.descrizione}" />&quot; ?
			</s:else>
		</p>
		<div class="azioni">
			<form action="<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/undoProdotto.action" />" method="post" class="azione">
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
				<div>
					<input type="hidden" name="ext" value="${param.ext}" />
					<input type="hidden" name="prodottoId" value="${prodotto.index}" />
					<input type="hidden" name="statoProdotto" value="${prodotto.statoProdotto}" />
					<input type="hidden" name="inCarrello" value="${prodotto.inCarrello}" />
					<wp:i18n key="LABEL_YES" var="valueButtonYes" />
					<wp:i18n key="TITLE_CONFIRM_DEL_YES" var="titleButtonYes" />
					<s:submit value="%{#attr.valueButtonYes}" title="%{#attr.titleButtonYes}" cssClass="button block-ui"></s:submit>
				</div>
			</form>
			<form action="<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/cancelUndoProdotto.action" />" method="post" class="azione">
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
				<div>
					<input type="hidden" name="ext" value="${param.ext}" />
					<input type="hidden" name="prodottoId" value="${prodotto.index}" />
					<input type="hidden" name="statoProdotto" value="${prodotto.statoProdotto}" />
					<input type="hidden" name="inCarrello" value="${prodotto.inCarrello}" />
					<input type="hidden" name="fromDetail" value="${fromDetail}"/>
					<wp:i18n key="LABEL_NO" var="valueButtonNo" />
					<wp:i18n key="BUTTON_WIZARD_CANCEL" var="titleButtonNo" />
					<s:submit value="%{#attr.valueButtonNo}" title="%{#attr.titleButtonNo}" cssClass="button block-ui"></s:submit>
				</div>
			</form>
		</div>
	</s:elseif>
	<s:else>
		<div class="detail-section first-detail-section">
			<h3 class="detail-section-title"><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_DATI_GENERALI" /></h3>

			<div class="detail-row">
				<label><wp:i18n key="LABEL_TIPO_ARTICOLO" /> : </label>
				<s:iterator value="maps['tipiArticolo']">
					<s:if test="%{key == prodotto.articolo.dettaglioArticolo.tipo}"><s:property value="%{value}"/></s:if>
				</s:iterator>
			</div>

			<div class="detail-row">
				<label><wp:i18n key="LABEL_DETTAGLIO_ARTICOLO_CODICE" /> : </label>
				<s:property value="%{prodotto.articolo.dettaglioArticolo.codice}" />
			</div>

			<div class="detail-row">
				<label><wp:i18n key="LABEL_DETTAGLIO_ARTICOLO_DESCRIZIONE" /> : </label>
				<s:property value="%{prodotto.articolo.dettaglioArticolo.descrizione}" />
			</div>

			<div class="detail-row">
				<label><wp:i18n key="LABEL_DESCRIZIONE_TECNICA" /> : </label>
				<s:property value="%{prodotto.articolo.dettaglioArticolo.descrizioneTecnica}" />
			</div>

			<s:if test="%{prodotto.articolo.dettaglioArticolo.certificazioniRichieste != null}">
				<div class="detail-row">
						<label><wp:i18n key="LABEL_DETTAGLIO_ARTICOLO_CERTIFICAZIONI_RICH" /> : </label>
						<s:property value="%{prodotto.articolo.dettaglioArticolo.certificazioniRichieste}" />
				</div>
			</s:if>

			<s:if test="%{prodotto.articolo.dettaglioArticolo.facSimileCertificazioni != null 
						&& prodotto.articolo.dettaglioArticolo.facSimileCertificazioni.length > 0}">
				<div class="detail-row">
					<label><wp:i18n key="ETTAGLIO_ARTICOLO_FACSIMILE_CERTIFICAZIONI" /> : </label>
					<s:set var="elencoDocumentiAllegati" value="%{prodotto.articolo.dettaglioArticolo.facSimileCertificazioni}"/>
					<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/bandi/inc/iteratorDocAllegati.jsp">
						<jsp:param name="path" value="downloadDocumentoPubblico"/>
					</jsp:include>
				</div>
			</s:if>

			<s:if test="%{TIPO_PRODOTTO_BENE == prodotto.articolo.dettaglioArticolo.tipo}">
				<div class="detail-row">
					<label><wp:i18n key="LABEL_COLORE_ARTICOLO" /> : </label>
					<s:property value="%{prodotto.articolo.dettaglioArticolo.note" />
				</div>
			</s:if>
		</div>

		<div class="detail-section">
			<h3 class="detail-section-title"><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_DATI_GENERALI_PRODOTTO" /></h3>

			<div class="detail-row">
				<label><wp:i18n key="LABEL_CODICE_PRODOTTO_FORNITORE" /> : </label>
				<s:property value="%{prodotto.dettaglioProdotto.codiceProdottoFornitore}" />
			</div>

			<s:if test="%{TIPO_PRODOTTO_BENE == prodotto.articolo.dettaglioArticolo.tipo}">			
				<div class="detail-row">
					<label><wp:i18n key="LABEL_MARCA_PRODOTTO_PRODUTTORE" /> : </label>
					<s:property value="%{prodotto.dettaglioProdotto.marcaProdottoProduttore}" />
				</div>
				
				<div class="detail-row">
					<label><wp:i18n key="LABEL_CODICE_PRODOTTO_PRODUTTORE" /> : </label>
					<s:property value="%{prodotto.dettaglioProdotto.codiceProdottoProduttore}" />
				</div>
			</s:if>

			<div class="detail-row">
				<label><wp:i18n key="LABEL_NOME_COMMERCIALE" /> : </label>
				<s:property value="%{prodotto.dettaglioProdotto.nomeCommerciale}" />
			</div>
		</div>

		<div class="detail-section">
			<h3 class="detail-section-title"><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_SPECIFICHE_PRODOTTO" /></h3>

			<div class="detail-row">
				<label><wp:i18n key="LABEL_IMMAGINE" /> : </label>
				<wp:i18n key="LABEL_SCARICA_IMMAGINE" var="valueScaricaImmagine" />
				<s:if test="%{inCarrello && prodotto.documenti.immagine != null}">
					<s:url id="urlDownloadImmagine" namespace="/do/FrontEnd/Cataloghi" action="downloadAllegatoImmagine" />
					<c:choose>
						<c:when test="${skin == 'highcontrast' || skin == 'text'}">
							<s:a href="%{#urlDownloadImmagine}?statoProdotto=%{statoProdotto}&amp;prodottoId=%{prodottoId}" title="%{#attr.valueScaricaImmagine}">
								<wp:i18n key="LABEL_SCARICA_IMMAGINE" />
							</s:a>
						</c:when>
						<c:otherwise>
								<s:a href="%{#urlDownloadImmagine}?statoProdotto=%{statoProdotto}&amp;prodottoId=%{prodottoId}" title="%{#attr.valueScaricaImmagine}" cssClass="bkg img">
									<s:property value="%{prodotto.documenti.immagineFileName}"/>
								</s:a>
						</c:otherwise>
					</c:choose>
				</s:if>
				<s:elseif test="%{prodotto.dettaglioProdotto.immagine != null}">
					<s:url id="urlFileDownload" namespace="/do/FrontEnd/DocDig" action="downloadDocumentoRiservato" />
				<%-- SIC	<s:url id="urlFileDownload" namespace="/do/FrontEnd/DocDig" action="download" /> --%>
					<c:choose>
						<c:when test="${skin == 'highcontrast' || skin == 'text'}">
							<s:a href="%{#urlFileDownload}?id=%{prodotto.dettaglioProdotto.immagine.id}" title="%{#attr.valueScaricaImmagine}">
								<wp:i18n key="LABEL_SCARICA_IMMAGINE" />
							</s:a>
						</c:when>
						<c:otherwise>
							<wp:i18n key="LABEL_DOWNLOAD_FILE" var="titleScaricaFile" />
							<s:a href="%{#urlFileDownload}?id=%{prodotto.dettaglioProdotto.immagine.id}" title="%{#attr.titleScaricaFile}" cssClass="bkg img">
								<s:property value="%{prodotto.dettaglioProdotto.immagine.nomefile}" />
							</s:a>
						</c:otherwise>
					</c:choose>
				</s:elseif>
				<s:else>
					<wp:i18n key="LABEL_NO_IMMAGINE" />
				</s:else>
			</div>

			<div class="detail-row">
				<label><wp:i18n key="LABEL_DESCRIZIONE_AGGIUNTIVA" /> : </label>
				<s:property value="%{prodotto.dettaglioProdotto.descrizioneAggiuntiva}" />
			</div>

			<s:if test="%{prodotto.articolo.dettaglioArticolo.tipo == TIPO_PRODOTTO_BENE}">
				<div class="detail-row">
					<label><wp:i18n key="LABEL_DIMENSIONI" /> : </label>
					<s:property value="%{prodotto.dettaglioProdotto.dimensioni}" />
				</div>
			</s:if>

			<div class="detail-row">
				<label><wp:i18n key="LABEL_DETTAGLIO_ARTICOLO_CERTIFICAZIONI_RICH" /> : </label>
				<wp:i18n key="LABEL_SCARICA_CERTIFICAZIONE_RICH" var="valueScaricaCertificazione"/>
				<div class="detail-subrow">
					<s:if test="%{inCarrello && prodotto.documenti.certificazioniRichieste.size() > 0}">
						<s:url id="urlDownloadCertificazione" namespace="/do/FrontEnd/Cataloghi" action="downloadAllegatoCertificazione" />
						<ul class="list">
							<s:iterator value="%{prodotto.documenti.certificazioniRichiesteFileName}" var="certificazione" status="statusCert" >
								<li class='<s:if test="%{#statusCert.first}">first</s:if> <s:if test="%{#statusCert.last}">last</s:if>'>
									<c:choose>
										<c:when test="${skin == 'highcontrast' || skin == 'text'}">
											<s:a href="%{#urlDownloadCertificazione}?statoProdotto=%{statoProdotto}&amp;prodottoId=%{prodottoId}&amp;id=%{#statusCert.index}" 
												 title="%{#attr.valueScaricaCertificazione}">
												<wp:i18n key="LABEL_SCARICA_CERTIFICAZIONE_RICH" />
											</s:a>
										</c:when>
										<c:otherwise>
											<s:a href="%{#urlDownloadCertificazione}?statoProdotto=%{statoProdotto}&amp;prodottoId=%{prodottoId}&amp;id=%{#statusCert.index}" 
												 cssClass="bkg attachment" title="%{#attr.valueScaricaCertificazione}">
												<s:property value="%{prodotto.documenti.certificazioniRichiesteFileName.get(#statusCert.index)}"/>
											</s:a>
										</c:otherwise>
									</c:choose>
								</li>
							</s:iterator>
						</ul>
					</s:if>
					<s:elseif test="%{!inCarrello && prodotto.dettaglioProdotto.certificazioniRichieste != null 
									  && prodotto.dettaglioProdotto.certificazioniRichieste.length > 0}">
						<s:set var="elencoDocumentiAllegati" value="%{prodotto.dettaglioProdotto.certificazioniRichieste}"/>
						<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/bandi/inc/iteratorDocAllegati.jsp">
							<jsp:param name="path" value="downloadDocumentoRiservato"/>
						</jsp:include>
					</s:elseif>
					<s:else>
						<wp:i18n key="LABEL_NO_DOCUMENTI" />
					</s:else>
				</div>
			</div>

			<div class="detail-row">
				<label><wp:i18n key="LABEL_SCHEDE_TECNICHE" /> : </label>
				<wp:i18n key="LABEL_SCARICA_SCHEDA_TECNICA" var="valueScaricaSchedaTecnica"/>
				<div class="detail-subrow">
					<s:if test="%{inCarrello && prodotto.documenti.schedeTecniche.size() > 0}">
						<s:url id="urlDownloadSchedaTecnica" namespace="/do/FrontEnd/Cataloghi" action="downloadAllegatoSchedaTecnica" />
						<ul class="list">
							<s:iterator value="%{prodotto.documenti.schedeTecnicheFileName}" var="schedaTecnica" status="statusScheda" >
								<li class='<s:if test="%{#statusScheda.first}">first</s:if> <s:if test="%{#statusScheda.last}">last</s:if>'>
									<c:choose>
										<c:when test="${skin == 'highcontrast' || skin == 'text'}">
											<s:a href="%{#urlDownloadSchedaTecnica}?statoProdotto=%{statoProdotto}&amp;prodottoId=%{prodottoId}&amp;id=%{#statusScheda.index}" 
												 title="%{#attr.valueScaricaSchedaTecnica}">
												 <wp:i18n key="LABEL_SCARICA_SCHEDA_TECNICA" />
											</s:a>
										</c:when>
										<c:otherwise>
											<s:a href="%{#urlDownloadSchedaTecnica}?statoProdotto=%{statoProdotto}&amp;prodottoId=%{prodottoId}&amp;id=%{#statusScheda.index}" 
												 cssClass="bkg attachment" title="%{#attr.valueScaricaSchedaTecnica}">
												<s:property value="%{prodotto.documenti.schedeTecnicheFileName.get(#statusScheda.index)}"/>
											</s:a>
										</c:otherwise>
									</c:choose>
								</li>
							</s:iterator>
						</ul>
					</s:if>
					<s:elseif test="%{!inCarrello && prodotto.dettaglioProdotto.schedeTecniche != null 
									   && prodotto.dettaglioProdotto.schedeTecniche.length > 0}">
						<s:set var="elencoDocumentiAllegati" value="%{prodotto.dettaglioProdotto.schedeTecniche}"/>
						<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/bandi/inc/iteratorDocAllegati.jsp">
							<jsp:param name="path" value="downloadDocumentoRiservato"/>
						</jsp:include>
					</s:elseif>
					<s:else>
						<wp:i18n key="LABEL_NO_DOCUMENTI" />
					</s:else>
				</div>
			</div>

			<s:hidden name="obbligoGaranzia" value="%{prodotto.articolo.dettaglioArticolo.obbligoGaranzia}" />
			<s:if test="%{prodotto.articolo.dettaglioArticolo.obbligoGaranzia}">
				<div class="detail-row">
					<label><wp:i18n key="LABEL_PERIODO_GARANZIA_DI_MESI" /> : </label>
					<s:property value="%{prodotto.dettaglioProdotto.garanzia != null ? prodotto.dettaglioProdotto.garanzia : DEFAULT_NUM_MESI_GARANZIA }"/>
				</div>
			</s:if>
		</div>
		
		<div class="detail-section">
			<h3 class="detail-section-title"><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_PREZZI_QUANTITA" /></h3>

			<div class="detail-row">
				<label><wp:i18n key="LABEL_UNITA_MISURA_DEL_PREZZO" /> : </label>
				<s:iterator value="maps['tipiUnitaMisura']">
					<s:if test="%{key.equals(prodotto.articolo.dettaglioArticolo.unitaMisuraDetermPrezzo)}">
						<s:property value="%{value}"/>
					</s:if>
				</s:iterator>
			</div>

			<div class="detail-row">
				<label><wp:i18n key="LABEL_PREZZO" /> : </label>
				<s:if test="%{prodotto.dettaglioProdotto.prezzoUnitario != null}">
					<s:text name="format.money"><s:param value="%{prodotto.dettaglioProdotto.prezzoUnitario}"/></s:text>&nbsp;&euro;
				</s:if>
			</div>

			<s:if test="%{TIPO_PREZZO_UNITA_DI_MISURA == prodotto.articolo.dettaglioArticolo.prezzoUnitarioPer}">
				<div class="detail-row">
					<label><wp:i18n key="LABEL_NUM_UNITA_PREZZO_ACQUISTO" /> : </label>
					<s:text name="format.money"><s:param value="%{prodotto.dettaglioProdotto.quantitaUMPrezzo}" /></s:text>
				</div>
				
				<div class="detail-row">
					<label><wp:i18n key="LABEL_UNITA_MISURA_ACQUISTO" /> : </label>
					<s:iterator value="maps['tipiUnitaMisura']">
						<s:if test="%{key.equals(prodotto.articolo.dettaglioArticolo.unitaMisuraAcquisto)}">
							<s:property value="%{value}"/>
						</s:if>
					</s:iterator>
				</div>
			</s:if>

			<s:if test="%{TIPO_PREZZO_UNITA_DI_MISURA == prodotto.articolo.dettaglioArticolo.prezzoUnitarioPer}">
				<div class="detail-row">
					<label><wp:i18n key="LABEL_PREZZO_PRODOTTO" /> : </label>
					<s:if test="%{prodotto.dettaglioProdotto.prezzoUnitario != prezzoUnitarioPerAcquisto}">
						<s:text name="format.money"><s:param value="%{prodotto.dettaglioProdotto.prezzoUnitarioPerAcquisto}"/></s:text>&nbsp;&euro;
					</s:if>
				</div>
			</s:if>

			<div class="detail-row">
				<label><wp:i18n key="LABEL_ALIQUOTA_IVA" /> : </label>
				<s:iterator value="maps['aliquoteIVA']">
					<s:if test="%{key.equals(prodotto.dettaglioProdotto.aliquotaIVA)}">
						<s:property value="%{value}"/>
					</s:if>
				</s:iterator>
			</div>

			<s:if test="%{TIPO_PREZZO_PRODOTTO_SERVIZIO_PER_UM != prodotto.articolo.dettaglioArticolo.prezzoUnitarioPer}">
				<div class="detail-row">
					<label><wp:i18n key="LABEL_LOTTO_MINIMO_UNITA_MISURA" /> : </label>
					<s:text name="format.money"><s:param value="%{prodotto.dettaglioProdotto.quantitaUMAcquisto}"/></s:text>
				</div>
			</s:if>
		</div>

		<div class="detail-section last-detail-section">
			<h3 class="detail-section-title"><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_ALTRI_DATI" /></h3>

			<div class="detail-row">
				<label><wp:i18n key="LABEL_TEMPO_CONSEGNA" /> : </label>
				<s:property value="%{prodotto.dettaglioProdotto.tempoConsegna}"/>
				(&nbsp;<wp:i18n key="LABEL_MASSIMO" />&nbsp;<s:property value="%{prodotto.articolo.dettaglioArticolo.tempoMaxConsegna}"/>
				<s:iterator value="maps['tipiUnitaMisuraTempiConsegna']">
					<s:if test="%{key == prodotto.articolo.dettaglioArticolo.unitaMisuraTempoConsegna}">
						<s:property value="%{value.toLowerCase()}"/>
					</s:if>
				</s:iterator>)
			</div>

			<div class="detail-row">
				<label><wp:i18n key="LABEL_DATA_SCADEZA_OFFERTA" /> : </label>
				<s:date name="prodotto.dettaglioProdotto.dataScadenzaOfferta" format="dd/MM/yyyy" />
			</div>

			<s:if test="%{!inCarrello}">
				<div class="detail-row">
					<label><wp:i18n key="LABEL_STATO_PRODOTTO" /> : </label>
					<s:iterator value="maps['statiProdotto']">
						<s:if test="%{key.equals(prodotto.dettaglioProdotto.stato)}">
							<s:property value="%{value}"/>
						</s:if>
					</s:iterator>
				</div>
			</s:if>
		</div>
		
		<div class="azioni">
			<s:if test="%{!variazioneOfferta}">
				<s:if test="%{(!inCarrello 
				                && STATO_PRODOTTO_IN_CATALOGO.equals(prodotto.dettaglioProdotto.stato) 
							    && !modificheInviate && !(prodotto.modificato || prodotto.archiviato)) 
							  || (inCarrello && !PRODOTTO_ELIMINATO.equals(prodotto.statoProdotto))}">
					<form action="<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/openPageDefinizioneProdotto.action" />" method="post" class="azione">
						<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
						<div>
							<wp:i18n key="BUTTON_EDIT" var="valueModificaButton" />
							<wp:i18n key="TITLE_ACCEDI_STEP_MODIFICA_WIZARD" var="titleModificaButton" />
							<s:submit value="%{#attr.valueModificaButton}" title="%{#attr.titleModificaButton}" cssClass="button" />
							<input type="hidden" name="prodottoId" value="${prodottoId}"/>
							<input type="hidden" name="statoProdotto" value="${prodotto.statoProdotto}"/>
							<input type="hidden" name="aggiornamento" value="${true}"/>
							<input type="hidden" name="catalogo" value="${prodotto.codiceCatalogo}"/>
							<input type="hidden" name="articoloId" value="${prodotto.articolo.dettaglioArticolo.id}"/>
							<input type="hidden" name="inCarrello" value="${inCarrello}"/>
						</div>
					</form>
				</s:if>
				<s:if test="%{STATO_PRODOTTO_ARCHIVIATO.equals(prodotto.dettaglioProdotto.stato) && !inCarrello && !reinsert}">
					<form action="<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/confirmReinsertProdotto.action" />" method="post" class="azione">
						<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
						<div>
							<wp:i18n key="LABEL_REINSERISCI_PRODOTTO" var="valueReinserisciButton" />
							<wp:i18n key="TITLE_RIPRISTINA_ELEMENTO_IN_CATALOGO" var="titleReinserisciButton" />
							<s:submit value="%{#attr.valueReinserisciButton}" title="%{#attr.titleReinserisciButton}" cssClass="button" />
							<input type="hidden" name="prodottoId" value="${prodotto.dettaglioProdotto.id}"/>
							<input type="hidden" name="inCarrello" value="${inCarrello}"/>
						</div>
					</form>
				</s:if>
				<s:if test="%{inCarrello && !BOZZA.equals(prodotto.statoProdotto)}">
					<form action="<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/confirmUndoProdotto.action" />" method="post" class="azione">
						<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
						<div>
							<wp:i18n key="BUTTON_WIZARD_CANCEL" var="valueAnnullaButton" />
							<wp:i18n key="TITLE_ELIMINA_DAL_CARRELLO" var="%{titleAnnullaButton}" />
							<s:submit value="%{#attr.valueAnnullaButton}" title="%{#attr.titleAnnullaButton}" cssClass="button" />
							<input type="hidden" name="prodottoId" value="${prodotto.index}"/>
							<input type="hidden" name="statoProdotto" value="${prodotto.statoProdotto}"/>
							<input type="hidden" name="inCarrello" value="${inCarrello}"/>
							<input type="hidden" name="fromDetail" value="${true}"/>
						</div>
					</form>
				</s:if>
			</s:if>
		</div>
	</s:else>
	
	<div class="back-link">
		<s:if test="%{!inCarrello}">
			<a href="<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/searchProdottiSistema.action" />&amp;ext=${param.ext}&amp;last=1" class="bolded">
				 <wp:i18n key="LINK_BACK_TO_MODIFICA_PRODOTTI" />
			</a>
		</s:if>
		<s:else>
			<a href="<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/viewProdottiCarrello.action" />&amp;ext=${param.ext}" class="bolded">
				 <wp:i18n key="LINK_BACK_TO_RIEPILOGO_MODIFICHE" />
			</a>
		</s:else>
	</div>
</div>