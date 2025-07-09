<%@ taglib prefix="wp" uri="aps-core.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="es"
	uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld"%>

<script	src='<wp:resourceURL/>static/js/ppgare/viewComunicazioni.js'></script>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp">
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<es:checkCustomization var="visBarcode" objectId="GARE" attribute="BARCODE" feature="VIS" />
<es:checkCustomization var="visDocRichiesti" objectId="GARE" attribute="DOCRICHIESTI" feature="VIS" />
<es:checkCustomization var="visDocAtti" objectId="GARE" attribute="ATTIDOC" feature="VIS" />
<es:checkCustomization var="visAderenti" objectId="GARE" attribute="ADERENTI" feature="VIS" />
<es:checkCustomization var="visDocInvitoPubblica" objectId="GARE" attribute="DOCINVITOPUBBLICA" feature="VIS" />
<es:getAppParam name = "denominazioneStazioneAppaltanteUnica" var = "stazAppUnica" scope = "page"/> 	

<%-- "codiceFromLotto" viene impostato da viewFromLotto --%>
<c:set var="codice" value="${param.codice}"/>
<c:if test="${! empty codiceFromLotto}" >
	<c:set var="codice" value="${codiceFromLotto}" />
</c:if>

<c:set var="currentDate" value="<%= new java.util.Date() %>"/>
<c:set var="proceduraTelematica" value="${dettaglioGara.datiGeneraliGara.proceduraTelematica}"/>
<c:set var="richPartecipazione" value="${abilitazioniGara.richPartecipazione}"/>
<c:set var="richInvioOfferta" value="${abilitazioniGara.richInvioOfferta}"/>
<c:set var="richComprovaRequisiti" value="${abilitazioniGara.richComprovaRequisiti}"/>
<c:set var="faseGara" value="${dettaglioGara.datiGeneraliGara.faseGara}"/>
<c:set var="garaPrivatistica" value="${dettaglioGara.datiGeneraliGara.garaPrivatistica}" />
<c:set var="visualizzaEspletamento" value="${dettaglioGara.datiGeneraliGara.visualizzaEspletamento}" />
<c:set var="garaSospesa" value="${false}" />
<s:if test="%{dettaglioGara.datiGeneraliGara.stato != null && (dettaglioGara.datiGeneraliGara.stato == 4 || dettaglioGara.datiGeneraliGara.stato == 3)}">
	<c:set var="garaSospesa" value="${true}" />
	<c:set var="richPartecipazione" value="${false}"/>
	<c:set var="richInvioOfferta" value="${false}"/>
	<c:set var="richComprovaRequisiti" value="${false}"/>
</s:if>

<c:set var="dataTermineRichiestaChiarimenti"><s:date name="dettaglioGara.datiGeneraliGara.dataTermineRichiestaChiarimenti" format="dd/MM/yyyy" /></c:set>
<c:set var="dataTermineRispostaOperatori"><s:date name="dettaglioGara.datiGeneraliGara.dataTermineRispostaOperatori" format="dd/MM/yyyy" /></c:set>

<%--
********************************************************************************
	dettaglioGara.datiGeneraliGara.iterGara==1  indica una gara aperta
	dettaglioGara.datiGeneraliGara.iterGara==2 o 4 indica una gara ristretta
	dettaglioGara.datiGeneraliGara.iterGara==3  indica una gara negoziata
********************************************************************************
--%>

<%-- tale data e' un'aggiunta prevista solo per le ristrette, e solo se sono stato invitato a presentare offerta --%>
<c:set var="visTermineOfferta"
	value="${((sessionScope.currentUser != 'guest')
			        && (dettaglioGara.datiGeneraliGara.iterGara == 2 || dettaglioGara.datiGeneraliGara.iterGara == 4)
                    && invitoPresentato) }"/>

<%-- o si tratta di una procedura aperta, oppure indipendentemente che sia negoziata o ristretta, o un concorso di progettazione pubblico 
	   devo essere loggato ed invitato a presentare offerta --%>
<c:set var="visDocumentazioneOfferta"
 	 value="${ (dettaglioGara.datiGeneraliGara.iterGara == 1)
		        || (sessionScope.currentUser != 'guest' && invitoPresentato) 
		        || (dettaglioGara.datiGeneraliGara.iterGara == 9)}" />

<c:set var="dataPubblicazioneBando"><s:date name="dettaglioGara.datiGeneraliGara.dataPubblicazione" format="dd/MM/yyyy" /></c:set>

<%--
<c:if test="${sessionScope.currentUser != 'guest'}">
	proceduraTelematica: ${proceduraTelematica}<br/>
	richPartecipazione: ${richPartecipazione}<br/>
	abilitaRiepilogoRichiesta: ${abilitaRiepilogoRichiesta}<br/>
	richInvioOfferta: ${richInvioOfferta}<br/>
	abilitaRiepilogoOfferta: ${abilitaRiepilogoOfferta}<br/>
	richComprovaRequisiti: ${richComprovaRequisiti}<br/>
	visBarcode: ${visBarcode}<br/>
	visTermineOfferta: ${visTermineOfferta}<br/>
	visDocumentazioneOfferta: ${visDocumentazioneOfferta}<br/>
	invitoPresentato: ${invitoPresentato}<br/>

	currentUser: ${sessionScope.currentUser} <br/>
	proceduraTelematica: ${proceduraTelematica} <br/>
	faseGara: ${faseGara} <br/>
	abilitaRiepilogoRichiesta: ${abilitaRiepilogoRichiesta} <br/>
	<br/>
</c:if>
--%>

<div class="portgare-view">

	<h2><wp:i18n key="TITLE_PAGE_DETTAGLIO_GARA" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_DETTAGLIO_BANDO" />
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />

	<s:if test="%{dettaglioGara.datiGeneraliGara.dataUltimoAggiornamento != null}">
		<div class="align-right important last-update-detail">
			<wp:i18n key="LABEL_LAST_UPDATE" />	<s:date name="dettaglioGara.datiGeneraliGara.dataUltimoAggiornamento" format="dd/MM/yyyy" />
		</div>
	</s:if>

	<div class="detail-section first-detail-section">
		<h3 class="detail-section-title">
			<span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_STAZIONE_APPALTANTE" />
		</h3>

		<div class="detail-row">
			<label><wp:i18n key="LABEL_DENOMINAZIONE" /> : </label>
			<c:choose>
					<c:when test="${! empty stazAppUnica }">
						<s:set var="stazAppUnicaToStruts">${stazAppUnica}</s:set>
						<s:property value="stazAppUnicaToStruts" />
					</c:when>
					<c:otherwise>
						<s:iterator value="maps['stazioniAppaltanti']">
							<s:if test="%{key == dettaglioGara.stazioneAppaltante.codice}">
								<s:property value="%{value}" />
							</s:if>
						</s:iterator>
					</c:otherwise>
			</c:choose>
		</div>

		<div class="detail-row">
			<label><wp:i18n key="LABEL_RUP" /> : </label>
			<s:property value="dettaglioGara.stazioneAppaltante.rup" />
		</div>
		<s:if test="%{dettaglioGara.stazioneAppaltante.responsabileFaseAffidamento != null && !dettaglioGara.stazioneAppaltante.responsabileFaseAffidamento.isEmpty()}">
			<div class="detail-row">
				<label><wp:i18n key="LABEL_RESPONSABILE_FASE_AFFIDAMENTO" /> : </label>
				<s:property value="dettaglioGara.stazioneAppaltante.responsabileFaseAffidamento" />
			</div>
		</s:if>

		<c:if test="${visAderenti}">
			<s:if test="%{dettaglioGara.soggettiAderenti.length > 0}" >
				<div class="detail-row">
					<label><wp:i18n key="LABEL_SOGGETTI_ADERENTI" /> : </label>

                    <s:if test="%{dettaglioGara.soggettiAderenti.length > 0}">
                        <div class="detail-subrow">
                            <ul class="list">
                                <s:iterator var="soggetto" value="%{dettaglioGara.soggettiAderenti}" status="stat">
                                    <li class='<s:if test="%{#stat.first}">first</s:if> <s:if test="%{#stat.last}">last</s:if>'>
                                        <s:property value="%{#soggetto.codiceFiscale}" /> - <s:property value="#soggetto.denominazione" />
                                    </li>
                                </s:iterator>
                            </ul>
                        </div>
					</s:if>
				</div>
			</s:if>
		</c:if>
	</div>

	<div class="detail-section">
		<h3 class="detail-section-title">
			<span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_DATI_GENERALI" />
		</h3>

		<div class="detail-row">
			<label><wp:i18n key="LABEL_TITOLO" /> :</label>
			<s:property value="dettaglioGara.datiGeneraliGara.oggetto" />
			<c:if test="${garaPrivatistica == null}">
				<c:if test="${! empty dettaglioGara.datiGeneraliGara.cig}">- <wp:i18n key="LABEL_CIG" /> : <s:property
						value="dettaglioGara.datiGeneraliGara.cig" />
				</c:if>
				<c:if test="${! empty dettaglioGara.datiGeneraliGara.cup}">- <wp:i18n key="LABEL_CUP" /> : <s:property
						value="dettaglioGara.datiGeneraliGara.cup" />
				</c:if>
			</c:if>
		</div>

		<div class="detail-row">
			<label><wp:i18n key="LABEL_TIPO_APPALTO" /> : </label>
			<s:iterator value="maps['tipiAppalto']">
				<s:if test="%{key == dettaglioGara.datiGeneraliGara.tipoAppalto}">
					<s:property value="%{value}" />
				</s:if>
			</s:iterator>
		</div>

		<div class="detail-row">
			<label><wp:i18n key="LABEL_TIPO_PROCEDURA" /> : </label>
			<s:iterator value="maps['tipiProcedura']">
				<s:if test="%{key == dettaglioGara.datiGeneraliGara.tipoProcedura}">
					<s:property value="%{value}" />
				</s:if>
			</s:iterator>
		</div>

		<s:if test='%{"9".equals(dettaglioGara.datiGeneraliGara.iterGara)}'>
            <div class="detail-row">
                <label><wp:i18n key="LABEL_TIPO_PROCEDURA_CONCORSO" /> : </label>
                <s:iterator value="maps['tipoProceduraConcorso']">
                    <s:if test="%{key == dettaglioGara.datiGeneraliGara.tipoConcorso}">
                        <s:property value="%{value}" />
                    </s:if>
                </s:iterator>
            </div>
        </s:if>

		<s:if test="%{dettaglioGara.datiGeneraliGara.iterGara != 7}">
			<s:if test="%{dettaglioGara.datiGeneraliGara.tipoAggiudicazione != null}">
				<div class="detail-row">
					<label><wp:i18n key="LABEL_CRITERIO_AGGIUDICAZIONE" /> : </label>
					<s:iterator value="maps['tipiAggiudicazione']">
						<s:if
							test="%{key == dettaglioGara.datiGeneraliGara.tipoAggiudicazione}">
							<s:property value="%{value}" />
						</s:if>
					</s:iterator>
				</div>
			</s:if>

			<c:if test="${dettaglioGara.datiGeneraliGara.nascondiImportoBaseGara != 1}">
				<div class="detail-row">
					<label><wp:i18n key="LABEL_IMPORTO_BASE_GARA" /> : </label>
					<s:if test="%{dettaglioGara.datiGeneraliGara.importo != null}">
						<s:text name="format.money">
							<s:param value="dettaglioGara.datiGeneraliGara.importo" />
						</s:text> &euro;
					</s:if>
				</div>
				<div class="detail-row">
                    <label><wp:i18n key="LABEL_VALORE_COMPLESSIVO_APPALTO" /> : </label>
                    <s:if test="%{dettaglioGara.datiGeneraliGara.valoreComplessivo != null}">
                        <s:text name="format.money">
                            <s:param value="dettaglioGara.datiGeneraliGara.valoreComplessivo" />
                        </s:text> &euro;
                    </s:if>
                </div>
			</c:if>
		</s:if>

		<div class="detail-row">
			<label><wp:i18n key="LABEL_DATA_PUBBLICAZIONE_BANDO" /> : </label>
			<s:date name="dettaglioGara.datiGeneraliGara.dataPubblicazione"
				format="dd/MM/yyyy" />
		</div>

		<div class="detail-row">
			<label><wp:i18n key="LABEL_DATA_SCADENZA_BANDO" /> : </label>
			<s:date name="dettaglioGara.datiGeneraliGara.dataTermine"
				format="dd/MM/yyyy" />
			<s:if test="%{dettaglioGara.datiGeneraliGara.oraTermine != null}">
				<wp:i18n key="LABEL_ENTRO_LE_ORE" /> <s:property
					value="dettaglioGara.datiGeneraliGara.oraTermine" />
			</s:if>

			<%-- RETTIFICHE OFFERTE IN UNA PROC NEGOZIATA O APERTA --%>
			<s:if test="%{dettaglioGara.datiGeneraliGara.iterGara != 2 && dettaglioGara.datiGeneraliGara.iterGara != 4}">
			<c:if test="${not empty  dettaglioGara.datiGeneraliGara.rettifichePresentazioneOfferta}">
				<div class="detail-subrow">
					<div class="list">
						<h4 class="detail-section-title"><span class="noscreen"><wp:i18n key="LABEL_SUBSECTION" /> </span><wp:i18n key="LABEL_DETTAGLIO_GARA_SCADENZE_PRECEDENTI" /></h4>
						<div class="table-container">
							<table class="info-table">
								<thead>
									<tr>
										<th scope="col"><wp:i18n key="LABEL_DATA_SCADENZA_BANDO" /></th>
										<th scope="col"><wp:i18n key="LABEL_DETTAGLIO_GARA_ORA_SCADENZA" /></th>
										<th scope="col"><wp:i18n key="LABEL_DETTAGLIO_GARA_DATA_ORA_RETTIFICA" /></th>
									</tr>
								</thead>
								<tbody>
									<s:iterator var="rettifiche" value="%{dettaglioGara.datiGeneraliGara.rettifichePresentazioneOfferta}">
										<tr>
											<td><s:date name="#rettifiche.dataTermine" format="dd/MM/yyyy" /></td>
											<td><s:property value="#rettifiche.oraTermine" /></td>
											<td><s:date name="#rettifiche.dataRettifica" format="dd/MM/yyyy HH:mm:ss" /></td>
										</tr>
									</s:iterator>
								</tbody>
							</table>
						</div>
					</div>
				</div>
			</c:if>
			</s:if>

			<%-- RETTIFICHE DOMANDE IN UNA PROC RISTRETTA --%>
			<c:if test="${not empty  dettaglioGara.datiGeneraliGara.rettifichePresentazioneDomanda}">
				<div class="detail-subrow" >
					<div class="list">
						<h4 class="detail-section-title"><span class="noscreen"><wp:i18n key="LABEL_SUBSECTION" /> </span><wp:i18n key="LABEL_DETTAGLIO_GARA_SCADENZE_PRECEDENTI" /></h4>
						<div class="table-container">
							<table class="info-table">
								<thead>
									<tr>
										<th scope="col"><wp:i18n key="LABEL_DATA_SCADENZA_BANDO" /></th>
										<th scope="col"><wp:i18n key="LABEL_DETTAGLIO_GARA_ORA_SCADENZA" /></th>
										<th scope="col"><wp:i18n key="LABEL_DETTAGLIO_GARA_DATA_ORA_RETTIFICA" /></th>
									</tr>
								</thead>
								<tbody>
									<s:iterator var="rettifiche" value="%{dettaglioGara.datiGeneraliGara.rettifichePresentazioneDomanda}">
										<tr>
											<td><s:date name="#rettifiche.dataTermine" format="dd/MM/yyyy" /></td>
											<td><s:property value="#rettifiche.oraTermine" /></td>
											<td><s:date  name="#rettifiche.dataRettifica" format="dd/MM/yyyy HH:mm:ss" /></td>
										</tr>
									</s:iterator>
								</tbody>
							</table>
						</div>
					</div>
				</div>
			</c:if>
		</div>

		<c:if test="${visTermineOfferta}">
			<s:if test="%{dettaglioGara.datiGeneraliGara.oraTerminePresentazioneOfferta != null}">
				<div class="detail-row">
					<label><wp:i18n key="LABEL_DATA_TERMINE_PRESENZAZIONE_OFFERTE" /> : </label>
					<s:date name="dettaglioGara.datiGeneraliGara.dataTerminePresentazioneOfferta"
						format="dd/MM/yyyy" />
					<s:if test="%{dettaglioGara.datiGeneraliGara.oraTerminePresentazioneOfferta != null}">
						<wp:i18n key="LABEL_ENTRO_LE_ORE" /> <s:property
							value="dettaglioGara.datiGeneraliGara.oraTerminePresentazioneOfferta" />
					</s:if>


				<!-- RETTIFICHE OFFERTE IN UNA PROC RISTRETTA -->
				<c:if test="${not empty  dettaglioGara.datiGeneraliGara.rettifichePresentazioneOfferta }">
					<div class="detail-subrow" >
						<div class="list">
							<h4 class="detail-section-title"><span class="noscreen"><wp:i18n key="LABEL_SUBSECTION" /> </span><wp:i18n key="LABEL_DETTAGLIO_GARA_TERMINI_OFFERTE_PRECEDENTI" /></h4>
							<div class="table-container">
								<table class="info-table">
									<thead>
										<tr>
											<th scope="col"><wp:i18n key="LABEL_DATA_SCADENZA_BANDO" /></th>
											<th scope="col"><wp:i18n key="LABEL_DETTAGLIO_GARA_ORA_SCADENZA" /></th>
											<th scope="col"><wp:i18n key="LABEL_DETTAGLIO_GARA_DATA_ORA_RETTIFICA" /></th>
										</tr>
									</thead>
									<tbody>
										<s:iterator var="rettifiche" value="%{dettaglioGara.datiGeneraliGara.rettifichePresentazioneOfferta}">
											<tr>
												<td><s:date name="#rettifiche.dataTermine" format="dd/MM/yyyy" /></td>
												<td><s:property value="#rettifiche.oraTermine" /></td>
												<td><s:date name="#rettifiche.dataRettifica" format="dd/MM/yyyy HH:mm:ss" /></td>
											</tr>
										</s:iterator>
									</tbody>
								</table>
							</div>
						</div>
					</div>
				</c:if>
			</div>
			</s:if>
		</c:if>

		<%--
		<div class="detail-row">
			<label>Procedura telematica : </label>
			<s:if test="%{dettaglioGara.datiGeneraliGara.proceduraTelematica}">
				SI<s:if test="%{dettaglioGara.datiGeneraliGara.astaElettronica}">, con ricorso all'asta elettronica</s:if>
			</s:if>
			<s:else>NO</s:else>
		</div>
 		--%>

		 <c:if test="${!empty dataTermineRichiestaChiarimenti}">
			<div class="detail-row">
				<label><wp:i18n key="DATA_TERMINE_RICHIESTA_CHIARIMENTI" /> : </label>
					${dataTermineRichiestaChiarimenti}
			</div>
		</c:if>
		<c:if test="${!empty dataTermineRispostaOperatori}">
			<div class="detail-row">
				<label><wp:i18n key="DATA_TERMINE_RISPOSTA_CHIARIMENTI" /> : </label>
					${dataTermineRispostaOperatori}
			</div>
		</c:if>
		<div class="detail-row">
			<label><wp:i18n key="LABEL_RIFERIMENTO_PROCEDURA" /> : </label>
				<s:property value="dettaglioGara.datiGeneraliGara.codice" />
		</div>

		<s:if test="%{numeroOrdineInvito != null}">
			<div class="detail-row">
				<label><wp:i18n key="LABEL_NUMERO_ORDINE_INVITO" /> : </label>
					<s:property value="%{numeroOrdineInvito}" />
			</div>
		</s:if>

		<div class="detail-row">
			<label><wp:i18n key="LABEL_STATO_GARA" /> : </label>
			<s:if test="%{dettaglioGara.datiGeneraliGara.stato != null}">
				<s:iterator value="maps['statiDettaglioGara']">
					<s:if test="%{key == dettaglioGara.datiGeneraliGara.stato}">
						<s:property value="%{value}" />
					</s:if>
				</s:iterator>
				<s:if test="%{dettaglioGara.datiGeneraliGara.esito != null}"> - <s:property
						value="dettaglioGara.datiGeneraliGara.esito" />
				</s:if>
			</s:if>
		</div>

		<div class="detail-row">
			<ul class="list">
				<li class='first last'>
					<a href="<wp:action path="/ExtStr2/do/FrontEnd/Bandi/viewLotti.action"/>&amp;codice=${codice}&amp;ext=${param.ext}"
					   class="bkg-big go"
					   title="<wp:i18n key="LABEL_VISUALIZZA_LOTTI" />" >
						<wp:i18n key="LABEL_LOTTI" />
					</a>
				</li>
			</ul>
		</div>
		
		<%-- BDNCP url trasparenza scheda ANAC --%>
		<c:if test="${! empty dettaglioGara.datiGeneraliGara.BDNCPAnac}">
			<div class="detail-row">
				<ul class="list">
					<li class='first last'>
						<a href="<wp:action path="/ExtStr2/do/FrontEnd/Bandi/viewBDNCP.action"/>&amp;codice=${codice}&amp;ext=${param.ext}"
						   class="bkg-big go"
						   title="<wp:i18n key="LABEL_VISUALIZZA_BDNCP" />" >
							<wp:i18n key="LABEL_BDNCP" />
						</a>
					</li>
				</ul>
			</div>
		</c:if>

		<!-- ATTI E DOCUMENTI BANDO GARA -->
		<c:if test="${visDocAtti}">
			<div class="detail-row">
				<ul class="list">
					<li class='first last'>
						<a href="<wp:action path="/ExtStr2/do/FrontEnd/Bandi/viewAttiDocumenti.action"/>&amp;codice=${codice}&amp;ext=${param.ext}"
						   class="bkg-big go"
						   title="<wp:i18n key="LABEL_VISUALIZZA_ALTRI_ATTI_DOCUMENTI" />">
							<wp:i18n key="LABEL_ALTRI_ATTI_DOCUMENTI" />
						</a>
					</li>
				</ul>
			</div>
		</c:if>
	</div>

	<s:set var="numeroDocumentiAllegati"
		value="%{dettaglioGara.documento != null ? dettaglioGara.documento.length : 0}" />
	<s:iterator var="lotto" value="%{dettaglioGara.lotto}">
		<s:set var="numeroDocumentiAllegati"
			value="%{#numeroDocumentiAllegati + (#lotto.documento != null ? #lotto.documento.length : 0)}" />
	</s:iterator>

	<!-- DOCUMENTAZIONE DI GARA -->
	<s:if test="%{#numeroDocumentiAllegati > 0}">
		<div class="detail-section">
			<h3 class="detail-section-title">
				<span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_DETTAGLIO_GARA_DOCUMENTAZIONE_GARA" />
			</h3>

			<div class="detail-row">
				<s:set var="elencoDocumentiAllegati"
					value="%{dettaglioGara.documento}" />
				<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/bandi/inc/iteratorDocAllegati.jsp">
					<jsp:param name="path" value="downloadDocumentoPubblico"/>
					<jsp:param name="dataPubblicazione" value="${dataPubblicazioneBando}" />
				</jsp:include>
				<div class="detail-subrow">
					<s:iterator var="lotto" value="%{dettaglioGara.lotto}"
						status="status">
						<s:if test="%{#lotto.documento.length > 0}">
							<s:if test="%{dettaglioGara.lotto.length > 1}">
								<span class="important"><wp:i18n key="LABEL_LOTTO" /> <s:property value="%{codiceInterno}" /></span>
							</s:if>
							<s:set var="elencoDocumentiAllegati" value="%{#lotto.documento}" />
							<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/bandi/inc/iteratorDocAllegati.jsp">
								<jsp:param name="path" value="downloadDocumentoPubblico"/>
								<jsp:param name="dataPubblicazione" value="${dataPubblicazioneBando}" />
							</jsp:include>
						</s:if>
					</s:iterator>
				</div>
			</div>
		</div>
	</s:if>

	<!-- DOCUMENTAZIONE DI INVITO -->
	<s:set var="visualizzaSezioneInvito" value="false"/>
	<c:if test="${invitoPresentato}">
		<s:set var="visualizzaSezioneInvito" value="true"/>
	</c:if>
	<c:if test="${visDocInvitoPubblica}" >
		<s:set var="visualizzaSezioneInvito" value="true"/>
	</c:if>

	<s:if test="%{visualizzaSezioneInvito}">
		<s:set var="numeroDocumentiAllegati"
			value="%{dettaglioGara.invito != null ? dettaglioGara.invito.length : 0}" />
		<s:if test="%{#numeroDocumentiAllegati > 0}">
			<div class="detail-section">
				<h3 class="detail-section-title">
					<span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_DETTAGLIO_GARA_DOCUMENTAZIONE_INVITO" />
				</h3>
				<div class="detail-row">

					<c:set var="tipoDownload" value="downloadDocumentoRiservato" />
					<c:if test="${visDocInvitoPubblica}" >
						<c:set var="tipoDownload" value="downloadDocumentoInvito" />
					</c:if>
					<%-- quando la gara scade l'invito diventa pubblico --%>
					<c:if test="${currentDate > dettaglioGara.datiGeneraliGara.dataTerminePresentazioneOfferta}" >
						<c:set var="tipoDownload" value="downloadDocumentoPubblico" />
					</c:if>
					
					<s:set var="elencoDocumentiAllegati" value="%{dettaglioGara.invito}" />
					<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/bandi/inc/iteratorDocAllegati.jsp">
						<jsp:param name="path" value="${tipoDownload}"/>
						<jsp:param name="dataPubblicazione" value="${dataPubblicazioneBando}" />
					</jsp:include>
				</div>
			</div>
		</s:if>
	</s:if>

	<!-- REQUISITI RICHIESTI -->
	<s:set var="numeroRequisitiRichiesti"
		value="%{dettaglioGara.requisitoRichiesto != null ? dettaglioGara.requisitoRichiesto.length : 0}" />
	<s:iterator var="lotto" value="%{dettaglioGara.lotto}">
		<s:set var="numeroRequisitiRichiesti"
			value="%{#numeroRequisitiRichiesti + (#lotto.requisitoRichiesto != null ? #lotto.requisitoRichiesto.length : 0)}" />
	</s:iterator>
	<s:if test="%{#numeroRequisitiRichiesti > 0}">
		<div class="detail-section">
			<h3 class="detail-section-title">
				<span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_DETTAGLIO_GARA_REQUISITI_CONCORRENTI" />
			</h3>

			<div class="detail-row">
				<s:set var="elencoRequisitiRichiesti"
					value="%{dettaglioGara.requisitoRichiesto}" />
				<s:include
					value="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/bandi/inc/iteratorReqRichiesti.jsp" />
				<div class="detail-subrow">
					<s:iterator var="lotto" value="%{dettaglioGara.lotto}"
						status="status">
						<s:if test="%{#lotto.requisitoRichiesto.length > 0}">
							<s:if test="%{dettaglioGara.lotto.length > 1}">
								<span class="important"><wp:i18n key="LABEL_LOTTO" /> <s:property value="%{codiceInterno}" /></span>
							</s:if>
							<s:set var="elencoRequisitiRichiesti"
								value="%{#lotto.requisitoRichiesto}" />
							<s:include
								value="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/bandi/inc/iteratorReqRichiesti.jsp" />
						</s:if>
					</s:iterator>
				</div>
			</div>
		</div>
	</s:if>

	<c:if test="${visDocRichiesti}">
	<s:set var="numeroDocumentiRichiesti" value="0" />

	<s:set var="numeroDocumentiRichiesti"
		value="%{#numeroDocumentiRichiesti + (dettaglioGara.documentoBustaPreQualifica != null ? dettaglioGara.documentoBustaPreQualifica.length : 0)}" />
	<s:iterator var="lotto" value="%{dettaglioGara.lotto}">
		<s:set var="numeroDocumentiRichiesti"
			value="%{#numeroDocumentiRichiesti + (#lotto.documentoBustaPreQualifica != null ? #lotto.documentoBustaPreQualifica.length : 0)}" />
	</s:iterator>

	<c:if test="${visDocumentazioneOfferta}">
		<s:set var="numeroDocumentiRichiesti"
			value="%{#numeroDocumentiRichiesti + (dettaglioGara.documentoBustaAmministrativa != null ? dettaglioGara.documentoBustaAmministrativa.length : 0)}" />
		<s:set var="numeroDocumentiRichiesti"
			value="%{#numeroDocumentiRichiesti + (dettaglioGara.documentoBustaTecnica != null ? dettaglioGara.documentoBustaTecnica.length : 0)}" />
		<s:set var="numeroDocumentiRichiesti"
			value="%{#numeroDocumentiRichiesti + (dettaglioGara.documentoBustaEconomica != null ? dettaglioGara.documentoBustaEconomica.length : 0)}" />
		<s:iterator var="lotto" value="%{dettaglioGara.lotto}">
			<s:set var="numeroDocumentiRichiesti"
				value="%{#numeroDocumentiRichiesti + (#lotto.documentoBustaAmministrativa != null ? #lotto.documentoBustaAmministrativa.length : 0)}" />
			<s:set var="numeroDocumentiRichiesti"
				value="%{#numeroDocumentiRichiesti + (#lotto.documentoBustaTecnica != null ? #lotto.documentoBustaTecnica.length : 0)}" />
			<s:set var="numeroDocumentiRichiesti"
				value="%{#numeroDocumentiRichiesti + (#lotto.documentoBustaEconomica != null ? #lotto.documentoBustaEconomica.length : 0)}" />
		</s:iterator>
	</c:if>

	<s:if test="%{#numeroDocumentiRichiesti > 0}">
		<div class="detail-section">
			<h3 class="detail-section-title">
				<span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_DETTAGLIO_GARA_DOCUMENTAZIONE_CONCORRENTI" />
			</h3>

			<!-- DOCUMENTAZIONE PER LA DOMANDA DI PARTECIPAZIONE -->
			<s:set var="documentiRichiestiPreQualifica"
				value="%{(dettaglioGara.documentoBustaPreQualifica != null ? dettaglioGara.documentoBustaPreQualifica.length : 0)}" />
			<s:iterator var="lotto" value="%{dettaglioGara.lotto}"
				status="status">
				<s:set var="documentiRichiestiPreQualifica"
					value="%{#documentiRichiestiPreQualifica + (#lotto.documentoBustaPreQualifica != null ? #lotto.documentoBustaPreQualifica.length : 0)}" />
			</s:iterator>
			<s:if test="%{#documentiRichiestiPreQualifica > 0}">
				<div class="detail-row">
					<span class="important"><wp:i18n key="LABEL_BUSTA_PREQUALIFICA" /></span>
					<s:set var="elencoDocumentiRichiesti"
						value="%{dettaglioGara.documentoBustaPreQualifica}" />
					<s:include
						value="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/bandi/inc/iteratorDocRichiesti.jsp" />
					<div class="detail-subrow">
						<s:iterator var="lotto" value="%{dettaglioGara.lotto}"
							status="status">
							<s:if
								test="%{#lotto.documentoBustaPreQualifica.length > 0 && dettaglioGara.lotto.length > 1}">
								<span class="important list"><wp:i18n key="LABEL_LOTTO" /> <s:property value="%{#lotto.codiceInterno}" /></span>
							</s:if>
							<s:set var="elencoDocumentiRichiesti"
								value="%{#lotto.documentoBustaPreQualifica}" />
							<s:include
								value="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/bandi/inc/iteratorDocRichiesti.jsp" />
						</s:iterator>
					</div>
				</div>
			</s:if>

			<!-- DOCUMENTAZIONE PER L'OFFERTA -->
			<c:if test="${visDocumentazioneOfferta}">

				<s:set var="documentiRichiestiAmministrativa"
					value="%{(dettaglioGara.documentoBustaAmministrativa != null ? dettaglioGara.documentoBustaAmministrativa.length : 0)}" />
				<s:iterator var="lotto" value="%{dettaglioGara.lotto}"
					status="status">
					<s:set var="documentiRichiestiAmministrativa"
						value="%{#documentiRichiestiAmministrativa + (#lotto.documentoBustaAmministrativa != null ? #lotto.documentoBustaAmministrativa.length : 0)}" />
				</s:iterator>
				<s:if test="%{#documentiRichiestiAmministrativa > 0}">
					<div class="detail-row">
						<span class="important"><wp:i18n key="LABEL_BUSTA_AMMINISTRATIVA" /></span>
						<s:set var="elencoDocumentiRichiesti"
							value="%{dettaglioGara.documentoBustaAmministrativa}" />
						<s:include
							value="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/bandi/inc/iteratorDocRichiesti.jsp" />
						<div class="detail-subrow">
							<s:iterator var="lotto" value="%{dettaglioGara.lotto}"
								status="status">
								<s:if
									test="%{#lotto.documentoBustaAmministrativa.length > 0 && dettaglioGara.lotto.length > 1}">
									<span class="important list"><wp:i18n key="LABEL_LOTTO" /> <s:property value="%{#lotto.codiceInterno}" /></span>
								</s:if>
								<s:set var="elencoDocumentiRichiesti"
									value="%{#lotto.documentoBustaAmministrativa}" />
								<s:include
									value="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/bandi/inc/iteratorDocRichiesti.jsp" />
							</s:iterator>
						</div>
					</div>
				</s:if>

				<s:set var="documentiRichiestiTecnica"
					value="%{(dettaglioGara.documentoBustaTecnica != null ? dettaglioGara.documentoBustaTecnica.length : 0)}" />
				<s:iterator var="lotto" value="%{dettaglioGara.lotto}"
					status="status">
					<s:set var="documentiRichiestiTecnica"
						value="%{#documentiRichiestiTecnica + (#lotto.documentoBustaTecnica != null ? #lotto.documentoBustaTecnica.length : 0)}" />
				</s:iterator>
				<s:if test="%{#documentiRichiestiTecnica > 0}">
					<div class="detail-row">
						<span class="important"><wp:i18n key="LABEL_BUSTA_TECNICA" /></span>
						<s:set var="elencoDocumentiRichiesti"
							value="%{dettaglioGara.documentoBustaTecnica}" />
						<s:include
							value="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/bandi/inc/iteratorDocRichiesti.jsp" />
						<div class="detail-subrow">
							<s:iterator var="lotto" value="%{dettaglioGara.lotto}"
								status="status">
								<s:if
									test="%{#lotto.documentoBustaTecnica.length > 0 && dettaglioGara.lotto.length > 1}">
									<span class="important list"><wp:i18n key="LABEL_LOTTO" /> <s:property value="%{#lotto.codiceInterno}" /></span>
								</s:if>
								<s:set var="elencoDocumentiRichiesti"
									value="%{#lotto.documentoBustaTecnica}" />
								<s:include
									value="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/bandi/inc/iteratorDocRichiesti.jsp" />
							</s:iterator>
						</div>
					</div>
				</s:if>

				<s:set var="documentiRichiestiEconomica"
					value="%{(dettaglioGara.documentoBustaEconomica != null ? dettaglioGara.documentoBustaEconomica.length : 0)}" />
				<s:iterator var="lotto" value="%{dettaglioGara.lotto}"
					status="status">
					<s:set var="documentiRichiestiEconomica"
						value="%{#documentiRichiestiEconomica + (#lotto.documentoBustaEconomica != null ? #lotto.documentoBustaEconomica.length : 0)}" />
				</s:iterator>
				<s:if test="%{#documentiRichiestiEconomica > 0}">
					<div class="detail-row">
						<span class="important list"><wp:i18n key="LABEL_BUSTA_ECONOMICA" /></span>
						<s:set var="elencoDocumentiRichiesti"
							value="%{dettaglioGara.documentoBustaEconomica}" />
						<s:include
							value="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/bandi/inc/iteratorDocRichiesti.jsp" />
						<div class="detail-subrow">
							<s:iterator var="lotto" value="%{dettaglioGara.lotto}"
								status="status">
								<s:if
									test="%{#lotto.documentoBustaEconomica.length > 0 && dettaglioGara.lotto.length > 1}">
									<span class="important list"><wp:i18n key="LABEL_LOTTO" /> <s:property value="%{#lotto.codiceInterno}" /></span>
								</s:if>
								<s:set var="elencoDocumentiRichiesti"
									value="%{#lotto.documentoBustaEconomica}" />
								<s:include
									value="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/bandi/inc/iteratorDocRichiesti.jsp" />
							</s:iterator>
						</div>
					</div>
				</s:if>

			</c:if>
		</div>
	</s:if>
	</c:if>

	<!-- ACCESSO ALLA DOCUMENTAZIONE PER L'INVITO E FASI DELL'ASTA ELETTRONICA -->
	<c:if test="${sessionScope.currentUser != 'guest'}" >
		<s:if test="%{dettaglioGara.datiGeneraliGara.invitataAsta && dettaglioGara.datiGeneraliGara.astaElettronica}">
			<div class="detail-row">
				<ul class="list">
					<li class='first last'>
						<a href="<wp:action path="/ExtStr2/do/FrontEnd/Aste/viewDocumenti.action"/>&amp;codice=${codice}&amp;ext="
						   class="bkg-big go"
						   title="<wp:i18n key="LABEL_DETTAGLIO_GARA_ASTA_ELETTRONICA" />" >
							<wp:i18n key="LABEL_ASTA_ELETTRONICA" />
						</a>
					</li>
				</ul>
			</div>
		</s:if>
	</c:if>

	<!-- DOCUMENTAZIONE DELL'ESPLETAMENTO DI GARA  -->
	<c:if test="${(sessionScope.currentUser != 'guest'
	              && proceduraTelematica
 	              && faseGara >= 2
	              && abilitaRiepilogoOfferta
	              && (visualizzaEspletamento || abilitaAccessoDocumenti))}" >
		<div class="detail-section">
			<h3 class="detail-section-title">
				<span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_ESPLETAMENTO_GARA" />
			</h3>

			<%-- fasi di gara --%>
			<c:if test="${visualizzaEspletamento}">
				<div class="detail-row">
					<ul class="list">
						<li class='first last'>
							<a href="<wp:action path="/ExtStr2/do/FrontEnd/GareTel/espletGaraFasi.action"/>&amp;codice=${codice}&amp;ext=${param.ext}"
							   class="bkg-big go"
							   title="<wp:i18n key="LABEL_DETTAGLIO_GARA_ESPLETAMENTO_GARA" />" >
								<wp:i18n key="LABEL_DETTAGLIO_GARA_FASI_GARA" />
							</a>
						</li>
					</ul>
				</div>
			</c:if>
			
			<%-- accesso ai documenti art. 36 --%>
			<c:if test="${abilitaAccessoDocumenti}">
				<div class="detail-row">
					<ul class="list">
						<li class='first last'>
							<s:if test="%{garaLotti}">
								<c:set var="href" value="/ExtStr2/do/FrontEnd/GareTel/espletGaraViewAccessoDocumentiLotti.action" />
							</s:if>
							<s:else>
							    <c:set var="href" value="/ExtStr2/do/FrontEnd/GareTel/espletGaraViewAccessoDocumenti.action" />
							</s:else>
							<a href="<wp:action path="${href}"/>&amp;codice=${codice}&amp;ext=${param.ext}"
							   class="bkg-big go"
							   title="<wp:i18n key="LABEL_ACCESSO_DOCUMENTI" />" >
								<wp:i18n key="LABEL_ACCESSO_DOCUMENTI" />
							</a>
						</li>
					</ul>
				</div>
			</c:if>
		</div>
	</c:if>

	<div
		class="detail-section <c:if test="${sessionScope.currentUser == 'guest'}">last-detail-section</c:if>">
		<h3 class="detail-section-title">
			<span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_COMUNICAZIONI_PUBBLICHE_SA" />
		</h3>

		<div class="detail-row">
			<s:set var="numeroComunicazioniAmministrazione" value="0" />
			<s:set var="elencoComunicazioniAmministrazione"
				value="%{dettaglioGara.comunicazione}" />
			<s:include
				value="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/bandi/inc/iteratorComAmministrazione.jsp" />
			<div class="detail-subrow">
				<s:iterator var="lotto" value="%{dettaglioGara.lotto}"
					status="status">
					<s:if test="%{#lotto.comunicazione.length > 0}">
						<s:if test="%{dettaglioGara.lotto.length > 1}">
							<span class="important"><wp:i18n key="LABEL_LOTTO" /> <s:property
									value="%{#status.index+1}" /></span>
						</s:if>
						<s:set var="elencoComunicazioniAmministrazione"
							value="%{#lotto.comunicazione}" />
						<s:include
							value="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/bandi/inc/iteratorComAmministrazione.jsp" />
					</s:if>
				</s:iterator>
			</div>
			<s:if test="%{#numeroComunicazioniAmministrazione == 0}">
				<wp:i18n key="LABEL_NO_COMUNICAZIONI_PUBBLICHE_SA" />
			</s:if>
		</div>
	</div>


	<c:if test="${sessionScope.currentUser != 'guest'}">
		<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/comunicazioni/sommarioComunicazioni.jsp" >
			<jsp:param name="genere" value="${genere}"/>
		</jsp:include>
	</c:if>

	<div class="azioni">
		<c:if test="${empty param.ext}">
			<c:if test="${! empty dettaglioGara.datiGeneraliGara.dataEsito}">
				<form
					action="<wp:action path="/ExtStr2/do/FrontEnd/Esiti/view.action" />"
					method="post" class="azione">
					<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
					<div>
						<wp:i18n key="BUTTON_DETTAGLIO_GARA_ESITO" var="valueEsitoButton" />
						<s:submit value="%{#attr.valueEsitoButton}" cssClass="button" />
						<input type="hidden" name="codice" value="${codice}" />
						<input type="hidden" name="ext" value="1" />
					</div>
				</form>
			</c:if>
			<c:if test="${sessionScope.currentUser != 'guest'}">

				<c:if test="${richPartecipazione}">
					<s:if test="%{dettaglioGara.datiGeneraliGara.busteDistinte}">
						<c:set var="href" value="/ExtStr2/do/FrontEnd/GareTel/openGestioneListaOfferte.action"/>
					</s:if>
					<s:else>
						<c:set var="href" value="/ExtStr2/do/FrontEnd/GareTel/openGestioneBuste.action"/>
					</s:else>

					<wp:i18n key="BUTTON_DETTAGLIO_GARA_PRESENTA_PARTECIPAZIONE" var="valueDomandaPartecipazioneButton" />
					<wp:i18n key="LABEL_DETTAGLIO_GARA_PRESENTA_PARTECIPAZIONE" var="titleDomandaPartecipazioneButton" />
					<c:choose>
						<c:when test="${proceduraTelematica}">
							<form action="<wp:action path="${href}" />" method="post" class="azione">
								<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
								<div>
									<s:submit value="%{#attr.valueDomandaPartecipazioneButton}" title="%{#attr.titleDomandaPartecipazioneButton}" cssClass="button" />
									<input type="hidden" name="codice" value="${codice}"/>
									<input type="hidden" name="operazione" value="${presentaPartecipazione}"/>
									<input type="hidden" name="codiceGara" value="${codice}"/>
									<input type="hidden" name="idComunicazione" value="${idComunicazione}"/>
								</div>
							</form>
						</c:when>
						<c:otherwise>
							<c:if test="${visBarcode}">
								<form
									action="<wp:action path="/ExtStr2/do/FrontEnd/RichPartBando/newPartecipazione.action" />"
									method="post" class="azione">
									<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
									<div>
										<s:submit value="%{#attr.valueDomandaPartecipazioneButton}" title="%{#attr.titleDomandaPartecipazioneButton}" cssClass="button" />
										<input type="hidden" name="codice" value="${codice}" />
									</div>
								</form>
							</c:if>
						</c:otherwise>
					</c:choose>
				</c:if>

				<s:if test="%{abilitaRiepilogoRichiesta}">
					<s:if test="%{dettaglioGara.datiGeneraliGara.busteDistinte}">
						<c:set var="href" value="/ExtStr2/do/FrontEnd/GareTel/openGestioneListaOfferte.action"/>
					</s:if>
					<s:else>
						<c:set var="href" value="/ExtStr2/do/FrontEnd/GareTel/openRiepilogoOfferta.action"/>
					</s:else>
					<wp:i18n key="BUTTON_DETTAGLIO_GARA_RIEPILOGO_PARTECIPAZIONE" var="valueRiepilogoPartecipazioneButton" />
					<wp:i18n key="LABEL_DETTAGLIO_GARA_RIEPILOGO_PARTECIPAZIONE" var="titleRiepilogoPartecipazioneButton" />
					<c:if test="${proceduraTelematica}">
						<form action="<wp:action path="${href}" />" method="post" class="azione">
							<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
							<div>
								<s:submit value="%{#attr.valueRiepilogoPartecipazioneButton}" title="%{#attr.titleRiepilogoPartecipazioneButton}" cssClass="button" />
								<input type="hidden" name="codice" value="${codice}" />
								<input type="hidden" name="operazione" value="${presentaPartecipazione}" />
								<input type="hidden" name="idComunicazione" value="${idComunicazione}"/>
								<input type="hidden" name="garaSospesa" value="${garaSospesa}"/>
							</div>
						</form>
					</c:if>
				</s:if>

				<c:if test="${richInvioOfferta}">
					<s:if test="%{dettaglioGara.datiGeneraliGara.busteDistinte}">
						<c:set var="href" value="/ExtStr2/do/FrontEnd/GareTel/openGestioneListaOfferte.action"/>
					</s:if>
					<s:else>
						<c:set var="href" value="/ExtStr2/do/FrontEnd/GareTel/openGestioneBuste.action"/>
					</s:else>
					<wp:i18n key="BUTTON_DETTAGLIO_GARA_PRESENTA_OFFERTA" var="valuePresentaOffertaButton" />
					<wp:i18n key="LABEL_DETTAGLIO_GARA_PRESENTA_OFFERTA" var="titlePresentaOffertaButton" />

					<c:choose>
						<c:when test="${proceduraTelematica}">
							<s:if test="%{!dettaglioGara.datiGeneraliGara.busteDistinte}">
								<!-- OFFERTA UNICA -->
								<form action="<wp:action path="${href}" />"	method="post" class="azione">
									<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
									<div>
										<s:submit value="%{#attr.valuePresentaOffertaButton}" title="%{#attr.titlePresentaOffertaButton}" cssClass="button" />
										<input type="hidden" name="codice" value="${codice}" />
										<input type="hidden" name="operazione" value="${inviaOfferta}" />
										<input type="hidden" name="idComunicazione" value="${idComunicazione}"/>
									</div>
								</form>
							</s:if>
							<s:else>
								<!-- OFFERTE DISTINTE -->
								<form action="<wp:action path="${href}" />" method="post" class="azione">
									<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
									<div>
										<s:submit value="%{#attr.valuePresentaOffertaButton}" title="%{#attr.titlePresentaOffertaButton}" cssClass="button" />
										<input type="hidden" name="codiceGara" value="${codice}" />
										<input type="hidden" name="operazione" value="${inviaOfferta}" />
									</div>
								</form>
							</s:else>
						</c:when>
						<c:otherwise>
							<c:if test="${visBarcode}">
								<form
									action="<wp:action path="/ExtStr2/do/FrontEnd/RichPartBando/newInvioOfferta.action" />"
									method="post" class="azione">
									<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
									<div>
										<s:submit value="%{#attr.valuePresentaOffertaButton}" title="%{#attr.titlePresentaOffertaButton}" cssClass="button" />
										<input type="hidden" name="codice" value="${codice}" />
									</div>
								</form>
							</c:if>
						</c:otherwise>
					</c:choose>
				</c:if>

				<s:if test="%{abilitaRiepilogoOfferta}">
					<s:if test="%{dettaglioGara.datiGeneraliGara.proceduraTelematica}">
						<s:if test="%{dettaglioGara.datiGeneraliGara.busteDistinte}">
							<c:set var="href" value="/ExtStr2/do/FrontEnd/GareTel/openGestioneListaOfferte.action"/>
						</s:if>
						<s:else>
							<c:set var="href" value="/ExtStr2/do/FrontEnd/GareTel/openRiepilogoOfferta.action"/>
						</s:else>
						<wp:i18n key="BUTTON_DETTAGLIO_GARA_RIEPILOGO_OFFERTA" var="valueRiepilogoOffertaButton" />
						<wp:i18n key="LABEL_DETTAGLIO_GARA_RIEPILOGO_OFFERTA" var="titleRiepilogoOffertaButton" />

						<form action="<wp:action path="${href}" />"	method="post" class="azione">
							<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
							<div>
								<s:submit value="%{#attr.valueRiepilogoOffertaButton}" title="%{#attr.titleRiepilogoOffertaButton}" cssClass="button" />
								<input type="hidden" name="codice" value="${codice}"/>
								<input type="hidden" name="operazione" value="${inviaOfferta}" />
								<input type="hidden" name="garaSospesa" value="${garaSospesa}"/>
								<s:if test="%{dettaglioGara.datiGeneraliGara.busteDistinte}">
									<input type="hidden" name="codiceGara" value="${codice}"/>
								</s:if>
								<input type="hidden" name="idComunicazione" value="${idComunicazione}"/>
							</div>
						</form>
					</s:if>
				</s:if>

				<c:if test="${richComprovaRequisiti}">
					<c:if test="${visBarcode}">
						<form
							action="<wp:action path="/ExtStr2/do/FrontEnd/RichPartBando/newComprovaRequisiti.action" />"
							method="post" class="azione">
							<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
							<div>
								<wp:i18n key="BUTTON_DETTAGLIO_GARA_COMPROVA_REQUISITI" var="valueComprovaRequisitiButton" />
								<wp:i18n key="LABEL_DETTAGLIO_GARA_COMPROVA_REQUISITI" var="titleComprovaRequisitiButton" />
								<s:submit value="%{#attr.valueComprovaRequisitiButton}" title="%{#attr.titleComprovaRequisitiButton}" cssClass="button" />
								<input type="hidden" name="codice" value="${codice}" />
							</div>
						</form>
					</c:if>
				</c:if>

				<!-- RINUNCIA OFFERTA -->
				<c:if test="${abilitaRinunciaOfferta && !garaSospesa}">
					<c:set var="href" value="/ExtStr2/do/FrontEnd/GareTel/openPageRinunciaOfferta.action"/>
					<wp:i18n key="BUTTON_DETTAGLIO_GARA_RINUNCIA_OFFERTA" var="valueRinunciaOffertaButton" />

					<form action="<wp:action path="${href}" />"	method="post" class="azione">
						<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
						<div>
							<input type="hidden" name="operazione" value="${inviaOfferta}" />
							<input type="hidden" name="codice" value="${codice}"/>
							<s:submit value="%{#attr.valueRinunciaOffertaButton}" title="%{#attr.valueRinunciaOffertaButton}" cssClass="button" />
						</div>
					</form>
				</c:if>
				<c:if test="${abilitaRiepilogoRinunciaOfferta}">
					<c:set var="href" value="/ExtStr2/do/FrontEnd/GareTel/openPageRinunciaOfferta.action"/>
					<wp:i18n key="BUTTON_DETTAGLIO_GARA_RIEPILOGO_RINUNCIA" var="valueRiepilogoRinunciaOffertaButton" />

					<form action="<wp:action path="${href}" />"	method="post" class="azione">
						<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
						<div>
							<input type="hidden" name="operazione" value="${inviaOfferta}" />
							<input type="hidden" name="codice" value="${codice}"/>
							<input type="hidden" name="garaSospesa" value="${garaSospesa}"/>
							<s:submit value="%{#attr.valueRiepilogoRinunciaOffertaButton}" title="%{#attr.valueRiepilogoRinunciaOffertaButton}" cssClass="button" />
						</div>
					</form>
				</c:if>
				
				<!-- ASTA ELETTRONICA -->	
				<s:if test="%{dettaglioGara.datiGeneraliGara.invitataAsta && dettaglioGara.datiGeneraliGara.astaElettronica}" >
					<%-- lascia alla action "openAsta" il compito di decidere --%> 
					<%-- a chi inoltrare il controllo                         --%>
					<%-- (riepilogo, classifica, openlottidistinti)        	  --%>
					<form action="<wp:action path="/ExtStr2/do/FrontEnd/Aste/openAsta.action"/>" method="post" class="azione">
						<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
						<div>
				  			<s:if test="%{dettaglioGara.datiGeneraliGara.astaAttiva && abilitaPartecipaAsta}" >
								<wp:i18n key="BUTTON_DETTAGLIO_GARA_OFFERTA_ASTA" var="valuePartecipaAstaButton" />
								<wp:i18n key="LABEL_DETTAGLIO_GARA_OFFERTA_ASTA" var="titlePartecipaAstaButton" />
								<s:submit value="%{#attr.valuePartecipaAstaButton}" title="%{#attr.titlePartecipaAstaButton}" cssClass="button" />
							</s:if>
							<s:else>
								<s:if test="%{abilitaRiepilogoAsta}" >
									<wp:i18n key="BUTTON_DETTAGLIO_GARA_RIEPILOGO_ASTA" var="valueRiepilogoAstaButton" />
									<wp:i18n key="LABEL_DETTAGLIO_GARA_RIEPILOGO_ASTA" var="titleRiepilogoAstaButton" />
									<s:submit value="%{#attr.valueRiepilogoAstaButton}" title="%{#attr.titleRiepilogoAstaButton}" cssClass="button" />
								</s:if>
							</s:else>
							<input type="hidden" name="codice" value="${codice}" />
						</div>
					</form>
				</s:if>
				
			</c:if>
		</c:if>
	</div>
	
<%--
sessionScope.fromPage: ${sessionScope.fromPage}<br/>
 --%>
 	
	<c:choose>	
		<c:when test="${sessionScope.fromPage != null && sessionScope.fromPage eq 'news'}"> 
			<div class="back-link">
				<a href="<wp:action path="/ExtStr2/do/FrontEnd/Comunicazioni/${sessionScope.fromPage}.action"/>">
					<wp:i18n key="LINK_BACK_TO_NEWS" />
				</a>
			</div>
		</c:when>
		<c:when test="${sessionScope.fromPage != null && 
		                (sessionScope.fromPage eq 'openPageDettaglioComunicazioneInviata' ||
		                 sessionScope.fromPage eq 'openPageDettaglioComunicazioneRicevuta')}">
			<div class="back-link">
				<%-- 
				  BandoViewAction memorizza in sessione l'idComunicazione se 
				  viene passato da DettaglioComunicazioniAction, che puo'
				  verificare se si ritorna dal bando e recuperare dalla sessione
				  l'id per aprire la comunicazione relativa...  
				--%>
				<a href="<wp:action path="/ExtStr2/do/FrontEnd/Comunicazioni/${sessionScope.fromPage}.action"/>&amp;fromBando=1&amp;idComunicazione=${idComunicazione}&amp;idDestinatario=${idDestinatario}" >
					<c:choose>
						<c:when test="${sessionScope.fromPage eq 'openPageDettaglioComunicazioneInviata'}">
							<wp:i18n key="LINK_BACK_TO_COMMUNICATIONS_SENT" />
						</c:when>
						<c:when test="${sessionScope.fromPage eq 'openPageDettaglioComunicazioneRicevuta'}">
							<wp:i18n key="LINK_BACK_TO_COMMUNICATIONS_RECEIVED" />
						</c:when>
					</c:choose>	
				</a>
			</div>
		</c:when>
		<c:when test="${sessionScope.fromPage != null && sessionScope.fromPage eq 'listSommeUrgenze'}">
			<div class="back-link">
				<a href="<wp:action path="/ExtStr2/do/FrontEnd/Bandi/listSommeUrgenze.action"/>&amp;last=1">
					<wp:i18n key="LINK_BACK_TO_LIST" />
				</a>
			</div>
		</c:when>
		<c:when test="${empty param.ext}">
			<!-- se il dettaglio e' stato aperto da una ricerca,          -->
			<!-- allora si informa la ricerca di mantenere l'ultimo stato -->
			<c:set var="last" value=''/>
			<c:if test="${sessionScope.fromPage != null && sessionScope.fromPageOwner eq 'bandi'}">
				<c:set var="last" value="&amp;last=1"/>
			</c:if>
			
			<c:choose>
				<c:when test="${sessionScope.fromSearch }">
					<c:if test="${sessionScope.fromPage != null}">
						<div class="back-link">
							<a href="<wp:action path="/ExtStr2/do/FrontEnd/Bandi/${sessionScope.fromPage}.action"/>&amp;last=1">
								<c:choose>
									<c:when test="${sessionScope.fromPage eq 'searchProcedure'}"><wp:i18n key="LINK_BACK_TO_SEARCH" /></c:when>
									<c:otherwise><wp:i18n key="LINK_BACK_TO_LIST" /></c:otherwise>
								</c:choose>
							</a>
						</div>
					</c:if>
				</c:when>
				<c:otherwise>
					<c:if test="${sessionScope.fromPage != null}">
						<div class="back-link">
							<a href="<wp:action path="/ExtStr2/do/FrontEnd/Bandi/${sessionScope.fromPage}.action"/>${last}">
								<wp:i18n key="LINK_BACK_TO_LIST" />
							</a>
						</div>
					</c:if>
				</c:otherwise>
			</c:choose>
		</c:when>
		<c:otherwise>
			<div class="back-link">
				<a href="<wp:action path="/ExtStr2/do/FrontEnd/Esiti/view.action" />&amp;codice=<s:property value="codice"/>">
					<wp:i18n key="LINK_BACK_TO_ESITO" />
				</a>
			</div>
		</c:otherwise>
	</c:choose>
</div>