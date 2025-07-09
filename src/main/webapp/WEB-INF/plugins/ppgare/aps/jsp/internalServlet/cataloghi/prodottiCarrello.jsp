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
					<s:if test="%{!variazioneOfferta}">
						{ width: '10%' },
					</s:if>
					{ width: '20%' },
					{ width: '10%' },
					{ width: '10%' },
					{ width: '20%' },
					<s:if test="%{!variazioneOfferta}">
						{ width: '20%' },
					</s:if>
					<s:else>
						{ width: '10%' },
					</s:else>
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

	<h2><wp:i18n key="LABEL_RIEPILOGO_MODIFICE_IN_CORSO" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_GESTIONE_PRODOTTI_RIEPILOGO_MODIFICHE"/>
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
	
	<wp:i18n key="LABEL_YES" var="valueYesButton" />
	<wp:i18n key="BUTTON_CONFIRM" var="titleYesButton" />
	<wp:i18n key="LABEL_NO" var="valueNoButton" />
	<wp:i18n key="BUTTON_WIZARD_CANCEL" var="titleNoButton" />
	<wp:i18n key="TITLE_CONFIRM_DEL_YES" var="titleYesButton2" />
	<wp:i18n key="TITLE_CONFIRM_DEL_NO" var="titleNoButton2" />
	
	<s:if test="%{undoAll}">
		<p class="question">
			<wp:i18n key="LABEL_ANNULLA_MODIFICHE_CATALOGO" />
		</p>

		<div class="azioni">
			<form action="<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/undoAllProdotto.action" />" method="post" class="azione">
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
				<div>
					<input type="hidden" name="ext" value="${param.ext}" />
					<s:submit value="%{#attr.valueYesButton}" title="%{#attr.titleYesButton}" cssClass="button block-ui"></s:submit>
				</div>
			</form>
			<form action="<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/cancelUndoAllProdotto.action" />" method="post" class="azione">
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
				<div>
					<input type="hidden" name="ext" value="${param.ext}" />
					<s:submit value="%{#attr.valueNoButton}" title="%{#attr.titleNoButton}" cssClass="button block-ui"></s:submit>
				</div>
			</form>
		</div>
	</s:if>
	<s:elseif test="%{undo}">
		<p class="question">
			<s:if test="%{PRODOTTO_INSERITO.equals(statoProdotto)}">
				<wp:i18n key="LABEL_ANNULLARE_INSERIMENTO" />
			</s:if>
			<s:elseif test="%{PRODOTTO_MODIFICATO.equals(statoProdotto)}">
				<wp:i18n key="LABEL_ANNULLARE_MODIFICA" />
			</s:elseif>
			<s:elseif  test="%{PRODOTTO_ELIMINATO.equals(statoProdotto)}">
				<wp:i18n key="LABEL_ANNULLARE_CANCELLAZIONE" />
			</s:elseif>
			<s:else>
				<wp:i18n key="LABEL_ELIMINA_BOZZA" />
			</s:else>
			<s:if test="%{TIPO_PRODOTTO_BENE==tipoProdotto}">
				<wp:i18n key="LABEL_DEL_PRODOTTO" /> &quot;<s:property value="%{nomeProdotto}" />&quot; ?
			</s:if>
			<s:else>
				<wp:i18n key="LABEL_DEL_SERVIZIO" /> &quot;<s:property value="%{descrizioneArticolo}" />&quot; ?
			</s:else>
		</p>
		<div class="azioni">
			<form action="<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/undoProdotto.action" />" method="post" class="azione">
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
				<div>
					<input type="hidden" name="ext" value="${param.ext}" />
					<input type="hidden" name="prodottoId" value="${prodottoId}" />
					<input type="hidden" name="statoProdotto" value="${statoProdotto}" />
					<s:submit value="%{#attr.valueYesButton}" title="%{#attr.titleYesButton}" cssClass="button block-ui"></s:submit>
				</div>
			</form>
			<form action="<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/cancelUndoProdotto.action" />" method="post" class="azione">
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
				<div>
					<input type="hidden" name="ext" value="${param.ext}" />
					<s:submit value="%{#attr.valueNoButton}" title="%{#attr.titleNoButton}" cssClass="button block-ui"></s:submit>
				</div>
			</form>
		</div>
	</s:elseif>
	<s:elseif test="dettaglioComunicazione != null || dettaglioComunicazioneVariazioneOfferta != null">
		<s:url id="urlDownloadPdf" namespace="/do/FrontEnd/Cataloghi" action="downloadRiepilogoFirmato" />
		<p>
			<span class="bkg-big balloon-alert"><wp:i18n key="LABEL_INVIO_IN_ATTESA_DI_ELABORAZIONE" />.</span>
		</p>
		<p>
			<wp:i18n key="TITLE_SCARICA_RIEPILOGO" var="titleScaricaButton" />
			<c:choose>
				<c:when test="${skin == 'highcontrast' || skin == 'text'}">
					<s:a href="%{#urlDownloadPdf}" title="%{#attr.titleScaricaButton}" cssClass="important">
						<wp:i18n key="LABEL_SCARICA" />
					</s:a>
				</c:when>
				<c:otherwise>
					<s:a href="%{#urlDownloadPdf}" title="%{#attr.titleScaricaButton}" cssClass="bkg pdf">
						<wp:i18n key="LABEL_SCARICA" />
					</s:a>
				</c:otherwise>
			</c:choose>
			<wp:i18n key="LABEL_IL_RIEPILOGO_RICHIESTA_DEL" />
			<s:if test="%{dettaglioComunicazione != null}">
				<s:date name="dettaglioComunicazione.dataPubblicazione" format="dd/MM/yyyy" />.
			</s:if>
			<s:else>
				<s:date name="dettaglioComunicazioneVariazioneOfferta.dataPubblicazione" format="dd/MM/yyyy" />.
			</s:else>
		</p>
	</s:elseif>
	<s:else>
		<s:if test="%{listaProdotti.size() > 0}">
			<div class="table-container">
				<table id="listaProdotti" summary="Riepilogo modifiche da comunicare all'Ente" class="info-table">
					<thead>
						<tr>
							<s:if test="%{!variazioneOfferta}">
								<th scope="col"><wp:i18n key="LABEL_STATO" /></th>
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
								<s:if test="%{!variazioneOfferta}">
									<td>
										<s:if test="%{PRODOTTO_INSERITO.equals(#prodotto.statoProdotto)}">
											<wp:i18n key="LABEL_INSERITO" />
										</s:if>
										<s:elseif test="%{PRODOTTO_MODIFICATO.equals(#prodotto.statoProdotto)}">
											<wp:i18n key="LABEL_MODIFICATO" />
										</s:elseif>
										<s:elseif test="%{PRODOTTO_ELIMINATO.equals(#prodotto.statoProdotto)}">
											<wp:i18n key="LABEL_ELIMINATO" />
										</s:elseif>
										<s:else>
											<wp:i18n key="LABEL_BOZZA" />
										</s:else>
									</td>
								</s:if>
								<td><s:property value="%{#prodotto.articolo.dettaglioArticolo.descrizione}" /></td>
								<td><s:property value="%{#prodotto.dettaglioProdotto.colore}" /></td>
								<td><s:property value="%{#prodotto.dettaglioProdotto.codiceProdottoFornitore}" /></td>
								<td><s:property value="%{#prodotto.dettaglioProdotto.nomeCommerciale}" /></td>
								<s:if test="%{!variazioneOfferta}">
									<td><s:property value="%{#prodotto.dettaglioProdotto.descrizioneAggiuntiva}" /></td>
								</s:if>
								<s:else>
									<td class="date-content"><s:date name="%{#prodotto.dettaglioProdotto.dataScadenzaOfferta}" format="dd/MM/yyyy" /></td>
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
													<a href='<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/viewProdotto.action" />&amp;prodottoId=${prodotto.index}&amp;statoProdotto=${prodotto.statoProdotto}&amp;inCarrello=true' 
														title='<wp:i18n key="LABEL_VISUALIZZA_DETTAGLIO_PRODOTTO" />' >
														<wp:i18n key="LABEL_VISUALIZZA_DETTAGLIO_PRODOTTO" />
													</a>
												</li>
												<li>
													<a href='<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/confirmUndoProdotto.action" />&amp;prodottoId=${prodotto.index}&amp;statoProdotto=${prodotto.statoProdotto}' 
														title='<wp:i18n key="LABEL_ANNULLA_MODIFICA" />'  >
														<wp:i18n key="LABEL_ELIMINA_ELEMENTO_CARRELLO" />
													</a>
												</li>
											</c:when>
											<c:otherwise>
												<li>
													<a href='<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/viewProdotto.action" />&amp;prodottoId=${prodotto.index}&amp;statoProdotto=${prodotto.statoProdotto}&amp;inCarrello=true' 
														title='<wp:i18n key="LABEL_VISUALIZZA_DETTAGLIO_PRODOTTO" />' class="bkg detail">
													</a>
												</li>
												<li>
													<a href='<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/confirmUndoProdotto.action" />&amp;prodottoId=${prodotto.index}&amp;statoProdotto=${prodotto.statoProdotto}' 
														title='<wp:i18n key="LABEL_ANNULLA_MODIFICA" />' class="bkg undo">
													</a>
												</li>
											</c:otherwise>
										</c:choose>
									</ul>
								</td>
							</tr>
						</s:iterator>
					</tbody>
				</table>
			</div>
			<br/>
			<div class="azioni">
				<form action="<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/confirmUndoAllProdotto.action" />" method="post" class="azione">
					<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
					<div>
						<wp:i18n key="LABEL_ANNULLA_MODIFICHE" var="valueAnnullaModificheButton" />
						<wp:i18n key="TITLE_ANNULLA_MODIFICHE_CATALOGO" var="titleAnnullaModificheButton" />
						<s:submit value="%{#attr.valueAnnullaModificheButton}" title="%{#attr.titleAnnullaModificheButton}" cssClass="button" />
					</div>
				</form>
			</div>
		</s:if>
		<s:else>
			<div class="list-summary">
				<wp:i18n key="LABEL_NESSUNA_MODIFICA_CATALOGO" />.
			</div>
		</s:else>
	</s:else>
	<div class="back-link">
		<a href="<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/openGestioneProdotti.action" />&amp;catalogo=<s:property value="%{catalogo}"/>&amp;ext=${param.ext}">
			<wp:i18n key="LINK_BACK_TO_GESTIONE_PRODOTTI" />
		</a>
	</div>
</div>