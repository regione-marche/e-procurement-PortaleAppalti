<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>

<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/send_blockUI.jsp" />
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
	
			$('#listaProdotti').dataTable({
				scrollX: true,
				scrollY: "25em",
				scrollCollapse: true,
				autoWidth: false,
				columns: [
					{ width: '5%' },
					<s:if test='%{model.stato == null || model.stato.equals("")}'>
						{ width: '10%' },
					</s:if>
					<s:if test='%{model.stato == null || model.stato.equals("")}'>
						{ width: '10%' },
					</s:if>
					<s:else>
						{ width: '20%' },
					</s:else>
					{ width: '10%' },
					{ width: '10%' },
					{ width: '15%' },
					{ width: '20%' },
					{ width: '10%'	},
					{ width: '10%' }
				]
			});
		});
	</script>
</c:if>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2><wp:i18n key="TITLE_PAGE_MODIFICA_PRODOTTI_CATALOGO" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_GESTIONE_PRODOTTI_CATALOGO_SISTEMA"/>
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />

	<s:if test="%{delete}">
		<p class="question">
			<s:if test="%{TIPO_PRODOTTO_BENE == tipoProdotto}">
				<wp:i18n key="LABEL_ELIMINARE_PRODOTTO" /> &quot;<s:property value="%{nomeProdotto}" />&quot; ?
			</s:if>
			<s:else>
				<wp:i18n key="LABEL_ELIMINARE_SERVIZIO" /> &quot;<s:property value="%{descrizioneArticolo}" />&quot; ?
			</s:else>
		</p>
		<div class="azioni">
			<form action="<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/deleteProdotto.action" />" method="post" class="azione">
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
				<div>
					<input type="hidden" name="ext" value="${param.ext}" />
					<input type="hidden" name="prodottoId" value="${prodottoId}" />
					
					<wp:i18n key="LABEL_YES" var="valueYesButton" />
					<wp:i18n key="BUTTON_CONFIRM" var="titleConfermaButton" />
					<s:submit value="%{#attr.valueYesButton}" title="%{#attr.titleConfermaButton}" cssClass="button block-ui"></s:submit>
				</div>
			</form>
			<form action="<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/cancelDeleteProdotto.action" />" method="post" class="azione">
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
				<div>
					<input type="hidden" name="ext" value="${param.ext}" />
					
					<wp:i18n key="LABEL_NO" var="valueNoButton" />
					<wp:i18n key="BUTTON_WIZARD_CANCEL" var="titleAnnullaButton" />
					<s:submit value="%{#attr.valueNoButton}" title="%{#attr.titleAnnullaButton}" cssClass="button block-ui"></s:submit>
				</div>
			</form>
		</div>
	</s:if>
	<s:else>
		<form action="<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/searchProdottiSistema.action" />" method="post">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			
			<fieldset>
				<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_SEARCH_CRITERIA" /></legend>

				<s:if test='%{!model.advancedSearch}'>
					<div class="search">
						<c:set var="lblFiltra"><wp:i18n key="LABEL_FILTRA_PRODOTTI_PER" /></c:set>
						<wp:i18n key="LABEL_FILTRA_PRODOTTI_PER" /> <s:textfield name="model.googleLike" id="model.googleLike" cssClass="text" 
												 								 value="%{model.googleLike}" aria-label="${lblFiltra}" />
						<wp:i18n key="BUTTON_FILTRA" var="valueFiltraButton" />
						<s:submit value="%{#attr.valueFiltraButton}" cssClass="button" />
						<a href='<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/searchProdottiSistema.action" />&amp;model.advancedSearch=true' 
							 title='<wp:i18n key="LABEL_RICERCA_AVANZATA" />' >
							 <wp:i18n key="LABEL_RICERCA_AVANZATA" />
						</a>
					</div>
				</s:if>
				<s:else>
					<div class="fieldset-row first-row">
						<div class="label">
							<label for="model.tipologia"><wp:i18n key="LABEL_TIPOLOGIA" /> :</label>
						</div>
						<div class="element">
							<select id="model.tipologia" name="model.tipologia" >
								<option value=""><wp:i18n key="OPT_TUTTE_LE_TIPOLOGIE" /></option>
								<s:iterator value="maps['tipiArticolo']" var="tipologia" status="statTipologia">
									<s:if test="%{key.equals(model.tipologia)}">
										<option value='<s:property value="key"/>' selected="selected"><s:property value="value"/></option>
									</s:if>
									<s:else>
										<option value='<s:property value="key"/>'><s:property value="value"/></option>
									</s:else>
								</s:iterator>
							</select>
						</div>
					</div>
				</s:else>

				<s:if test='%{model.advancedSearch}'>

					<div class="fieldset-row">
						<div class="label">
							<label for="model.descrizioneArticolo"><wp:i18n key="LABEL_DESCRIZIONE_ARTICOLO" /> : </label>
						</div>
						<div class="element">
							<s:textfield name="model.descrizioneArticolo" id="model.descrizioneArticolo" cssClass="text" 
										 value="%{model.descrizioneArticolo}" maxlength="50" size="50" />
						</div>
					</div>

					<div class="fieldset-row">
						<div class="label">
							<label for="model.colore"><wp:i18n key="LABEL_COLORE_ARTICOLO" /> : </label>
						</div>
						<div class="element">
							<s:textfield name="model.colore" id="model.colore" cssClass="text" 
										 value="%{model.colore}" maxlength="50" size="50" />
						</div>
					</div>

					<div class="fieldset-row">
						<div class="label">
							<label for="model.codice"><wp:i18n key="LABEL_CODICE_PRODOTTO_FORNITORE" /> : </label>
						</div>
						<div class="element">
							<s:textfield name="model.codice" id="model.codice" cssClass="text" 
										 value="%{model.codice}" maxlength="50" size="50" />
						</div>
					</div>

					<div class="fieldset-row">
						<div class="label">
							<label for="model.nome"><wp:i18n key="LABEL_NOME" /> : </label>
						</div>
						<div class="element">
							<s:textfield name="model.nome" id="model.nome" cssClass="text" 
										 value="%{model.nome}" maxlength="50" size="50" />
						</div>
					</div>

					<div class="fieldset-row">
						<div class="label">
							<label for="model.descrizioneAggiuntiva"><wp:i18n key="LABEL_DESCRIZIONE_AGGIUNTIVA" /> : </label>
						</div>
						<div class="element">
							<s:textfield name="model.descrizioneAggiuntiva" id="model.descrizioneAggiuntiva" cssClass="text" 
										 value="%{model.descrizioneAggiuntiva}" maxlength="50" size="50" />
						</div>
					</div>

					<div class="fieldset-row last-row">
						<div class="label">
							<label for="model.stato"><wp:i18n key="LABEL_STATO" /> : </label>
						</div>
						<div class="element">
							<select id="model.stato" name="model.stato" >
								<option value=""><wp:i18n key="OPT_TUTTI_GLI_STATI" /></option>
								<s:iterator value="maps['statiProdotto']" var="stato" status="statStato">
									<s:if test="%{key.equals(model.stato)}">
										<option value='<s:property value="key"/>' selected="selected"><s:property value="value"/></option>
									</s:if>
									<s:else>
										<option value='<s:property value="key"/>'><s:property value="value"/></option>
									</s:else>
								</s:iterator>
							</select>
						</div>
					</div>

					<div class="azioni">
						<wp:i18n key="BUTTON_FILTRA" var="valueFiltraButton" />
						<s:submit value="%{#attr.valueFiltraButton}" cssClass="button">
							<input type="hidden" name="model.advancedSearch" value="${true}" />
							<input type="hidden" name="ext" value="${param.ext}" />
						</s:submit>
						<a href='<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/searchProdottiSistema.action" />&amp;model.advancedSearch=false' 
							 title='<wp:i18n key="LABEL_RICERCA_SEMPLIFICATA" />' >
							 <wp:i18n key="LABEL_RICERCA_SEMPLIFICATA" />
						</a>
					</div>
				</s:if>
			</fieldset>

			<s:if test="%{listaProdotti.size() > 0}">
				
				<s:set var="imgModified"><wp:resourceURL/>static/img/modified.svg</s:set>
				<s:set var="imgDeleted"><wp:resourceURL/>static/img/deleted.svg</s:set>
				<s:set var="imgVariazioneOfferta"><wp:resourceURL/>static/img/modified_offer.png</s:set>
				
				<div class="list-summary">
					<wp:i18n key="SEARCH_RESULTS_INTRO" /> <s:property value="model.iTotalDisplayRecords"/> <wp:i18n key="SEARCH_RESULTS_OUTRO" /> <wp:i18n key="SEARCH_RESULTS_COUNT" /> <s:property value="model.iTotalRecords"/>.
				</div>
				
				<div class="table-container">
					<table id="listaProdotti" summary="Prodotti a disposizione dell'Ente" class="info-table">
						<thead>
							<tr>
								<th scope="col">&nbsp;</th>
								<s:if test='%{model.stato == null || model.stato.equals("")}'>
									<th scope="col"><wp:i18n key="LABEL_STATO_OFFERTA" /></th>
								</s:if>
								<th scope="col"><wp:i18n key="LABEL_DESCRIZIONE_ARTICOLO" /></th>
								<th scope="col"><wp:i18n key="LABEL_COLORE_ARTICOLO" /></th>
								<th scope="col"><wp:i18n key="LABEL_CODICE_PRODOTTO_FORNITORE" /></th>
								<th scope="col"><wp:i18n key="LABEL_NOME_COMMERCIALE" /></th>
								<s:if test="%{!variazioneOfferta}">
									<th scope="col"><wp:i18n key="LABEL_DESCRIZIONE_AGGIUNTIVA" /></th>
								</s:if>
								<s:else>
									<th scope="col"><wp:i18n key="LABEL_DATA_SCADEZA_OFFERTA" /></th>
								</s:else>
								<th scope="col"><wp:i18n key="LABEL_PREZZO" /></th>
								<th scope="col" class="action" style="min-width: 4em;"><wp:i18n key="ACTIONS" /></th>
							</tr>
						</thead>
						<tbody>			
							<s:iterator var="prodotto" value="listaProdotti" status="statProdotto">
								<tr>
									<td class="azioni">
										<ul class="azioni">
											<li>
												<s:if test="%{#prodotto.modificato && variazioneOfferta}">
													<c:choose>
														<c:when test="${skin == 'highcontrast' || skin == 'text'}">
															<wp:i18n key="LABEL_CATALOGHI_RICHIESTA_VARIAZIONE_IN_CORSO" />
														</c:when>
														<c:otherwise>
															<img src="${imgVariazioneOfferta}" title='<wp:i18n key="LABEL_CATALOGHI_RICHIESTA_VARIAZIONE_IN_CORSO" />' alt='<wp:i18n key="LABEL_CATALOGHI_RICHIESTA_VARIAZIONE_OFFERTA" />' />
														</c:otherwise>
													</c:choose>
												</s:if>
												<s:elseif test="%{#prodotto.modificato}">
													<c:choose>
														<c:when test="${skin == 'highcontrast' || skin == 'text'}">
															<wp:i18n key="LABEL_CATALOGHI_RICHIESTA_MODIFICA_IN_CORSO" />
														</c:when>
														<c:otherwise>
															<img class="resize-svg-16" src="${imgModified}" title='<wp:i18n key="LABEL_CATALOGHI_RICHIESTA_MODIFICA_IN_CORSO" />' alt='<wp:i18n key="LABEL_CATALOGHI_RICHIESTA_MODIFICA" />' />
														</c:otherwise>
													</c:choose>
												</s:elseif>
												<s:if test="%{#prodotto.archiviato}">
													<c:choose>
														<c:when test="${skin == 'highcontrast' || skin == 'text'}">
															<wp:i18n key="LABEL_CATALOGHI_RICHIESTA_ELIMINAZIONE_IN_CORSO" />
														</c:when>
														<c:otherwise>
															<img class="resize-svg-16" src="${imgDeleted}" title='<wp:i18n key="LABEL_CATALOGHI_RICHIESTA_ELIMINAZIONE_IN_CORSO" />' alt='<wp:i18n key="LABEL_CATALOGHI_RICHIESTA_ELIMINAZIONE" />' />
														</c:otherwise>
													</c:choose>
												</s:if>
											</li>
										</ul>
									</td>
									<s:if test='%{model.stato == null || model.stato.equals("")}'>
										<td>
											<s:property value="%{#prodotto.dettaglioProdotto.stato}"/>
										</td>
									</s:if>
									<td><s:property value="%{#prodotto.articolo.dettaglioArticolo.descrizione}" /></td>
									<td><s:property value="%{#prodotto.dettaglioProdotto.colore}" /></td>
									<td><s:property value="%{#prodotto.dettaglioProdotto.codiceProdottoFornitore}" /></td>
									<td><s:property value="%{#prodotto.dettaglioProdotto.nomeCommerciale}" /></td>
									<s:if test="%{!variazioneOfferta}">
										<td>
											<s:property value="%{#prodotto.dettaglioProdotto.descrizioneAggiuntiva}" />
										</td>
									</s:if>
									<s:else>
										<td class="date-content">
											<s:date name="%{#prodotto.dettaglioProdotto.dataScadenzaOfferta}" format="dd/MM/yyyy" />
										</td>
									</s:else>
									<td class="money-content">
										<s:if test="%{#prodotto.dettaglioProdotto.prezzoUnitarioPerAcquisto != null}">
											<s:text name="format.money"><s:param value="%{#prodotto.dettaglioProdotto.prezzoUnitarioPerAcquisto}"/></s:text>
										</s:if>
									</td>
									<td class="azioni">
										<ul>
											<c:choose>
												<c:when test="${skin == 'highcontrast' || skin == 'text'}">
													<li>
														<a href='<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/viewProdotto.action" />&amp;prodottoId=${prodotto.dettaglioProdotto.id}&amp;inCarrello=false' 
															 title='<wp:i18n key="LABEL_VISUALIZZA_DETTAGLIO_PRODOTTO" />' >
															 <wp:i18n key="LABEL_VISUALIZZA_DETTAGLIO_PRODOTTO" />
														</a>
													</li>
													<s:if test='%{#prodotto.dettaglioProdotto.stato.toUpperCase().contains("CATALOGO") && dettaglioComunicazione == null && !#prodotto.archiviato && !#prodotto.modificato && !variazioneOfferta && dettaglioComunicazioneVariazioneOfferta == null}'>
														<li>
															<a href='<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/confirmDeleteProdotto.action" />&amp;prodottoId=${prodotto.dettaglioProdotto.id}' 
																 title='<wp:i18n key="LABEL_ELIMINA_PRODOTTO" />' >
																 <wp:i18n key="LABEL_ELIMINA_PRODOTTO" />
															</a>
														</li>
													</s:if>
												</c:when>
												<c:otherwise>
													<li>
														<a href='<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/viewProdotto.action" />&amp;prodottoId=${prodotto.dettaglioProdotto.id}&amp;inCarrello=false' 
															 title='<wp:i18n key="LABEL_VISUALIZZA_DETTAGLIO_PRODOTTO" />' class="bkg detail">
														</a>
													</li>
													<s:if test='%{#prodotto.dettaglioProdotto.stato.toUpperCase().contains("CATALOGO") && dettaglioComunicazione == null && !#prodotto.archiviato && !#prodotto.modificato && !variazioneOfferta && dettaglioComunicazioneVariazioneOfferta == null}'>
														<li>
															<a href='<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/confirmDeleteProdotto.action" />&amp;prodottoId=${prodotto.dettaglioProdotto.id}' 
															 	title='<wp:i18n key="LABEL_ELIMINA_PRODOTTO_DA_CATALOGO" />' class="bkg delete">
															</a>
														</li>
													</s:if>
												</c:otherwise>
											</c:choose>
										</ul>
									</td>
								</tr>
							</s:iterator>
						</tbody>
					</table>
				</div>
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/pagination.jsp"></jsp:include>
			</s:if>
			<s:else>
				<%-- Accessibility Fix Criterion 3.2.2: insert an invisible "submit" button as workaraound --%>
				<input disabled="disabled" type="submit" style="display:none;"/>
				<div class="list-summary">
					<wp:i18n key="SEARCH_NOTHING_FOUND" />.
				</div>
			</s:else>
		</form>
	</s:else>
	
	<div class="back-link">
		<a href="<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/openGestioneProdotti.action" />&amp;catalogo=<s:property value="%{catalogo}"/>&amp;ext=${param.ext}">
			<wp:i18n key="LINK_BACK_TO_GESTIONE_PRODOTTI" />
		</a>
	</div>
</div>