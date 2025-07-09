<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>

<s:set var="helperProdotto" value="%{#session.dettProdotto}"/>
<s:set var="helperArticolo" value="%{#session.dettProdotto.articolo.dettaglioArticolo}"/>
<s:set var="helperDocumenti" value="%{#session.dettProdotto.documenti}"/>
<%--
helperProdotto=<s:property value="%{#helperProdotto}" /><br/>
helperArticolo=<s:property value="%{#helperArticolo}" /><br/>
helperDocumenti=<s:property value="%{#helperDocumenti}" /><br/>
 --%>
 

<s:if test="%{!#session.dettProdotto.aggiornamento}">
	<c:set var="codiceBalloon" value="BALLOON_WIZ_PRODOTTO_INS_DOCUMENTI"/>
</s:if>
<s:else>
	<c:set var="codiceBalloon" value="BALLOON_WIZ_PRODOTTO_AGG_DOCUMENTI"/>
</s:else>


<%-- <script type="text/javascript" src='<wp:resourceURL/>static/js/ppgare/uploadFile.js'></script> --%>

<c:set var="validFilenameChars"><%= it.maggioli.eldasoft.plugins.ppcommon.aps.system.services.utils.FileUploadUtilities.getValidFilenameChars() %></c:set>
<c:set var="invalidFileMsg"><s:property value='%{getText("Errors.invalidFileName")}' /></c:set>

<script type="text/javascript">
var sfogliaImmagine 		= 'immagine';
var sfogliaCertificazione 	= 'certRic';
var sfogliaSchedaTecnica 	= 'schedTec';
var addImmagine 			= 'addImmagine';
var addCertificazione 		= 'addCertificazioneRichiesta';
var addSchedaTecnica 		= 'addSchedaTecnica';

var validFilenameChars = '${validFilenameChars}';
var invalidFilenameMsg = '${invalidFileMsg}';

$(document).ready(function(){
	
	$("[name='method:" + addImmagine + "']").hide();
	$("[name='method:" + addCertificazione + "']").hide();
	$("[name='method:" + addSchedaTecnica + "']").hide();
	
	$("[name='" + sfogliaImmagine + "']").change(function() {
		var file = $(this)[0].files[0];	
		if( isValidFilename(file.name) ) {
			$("[name='method:" + addImmagine + "']").trigger('click');
		}
	});
	
	$("[name='" + sfogliaCertificazione + "']").change(function() {
		var file = $(this)[0].files[0];	
		if( isValidFilename(file.name) ) {
			$("[name='method:" + addCertificazione + "']").trigger('click');
		}
	});
	
	$("[name='" + sfogliaSchedaTecnica + "']").change(function() {
		var file = $(this)[0].files[0];	
		if( isValidFilename(file.name) ) {
			$("[name='method:" + addSchedaTecnica + "']").trigger('click');
		}
	});
	
	
	// visualizzazione del messaggio di errore per la validazione dei nomi dei file	
	//
	$("input[type='file']").on("change", function() {
		$(this).closest("div").find(".invalidFilenameMsg").hide();
		
		var file = $(this)[0].files[0];	
		if( isValidFilename(file.name) ) {
			// carica il file
		} else {
			$(this).closest("div").append('<div class="invalidFilenameMsg errors">' + invalidFilenameMsg + '</div>');	
		}
	});
	
	function isValidFilename(filename) {
		var reg = new RegExp("^[" + validFilenameChars + "]+$", "g");
		return (reg.test(filename));
	}

});
</script>


<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/send_blockUI.jsp" />

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
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_messages.jsp" />

	<jsp:include page="stepsProdotto.jsp" />

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="${codiceBalloon}"/>
	</jsp:include>
	
	<wp:i18n key="LABEL_YES" var="valueYesButton" />
	<wp:i18n key="TITLE_CONFIRM_DEL_YES" var="titleYesButton" />
	<wp:i18n key="LABEL_NO" var="valueNoButton" />
	<wp:i18n key="TITLE_CONFIRM_DEL_NO" var="titleNoButton" />
	
	<s:if test="%{deleteCertificazioneRichiesta && !#helperDocumenti.certificazioniRichieste.isEmpty()}">
		<p class="question">
			<wp:i18n key="LABEL_ELIMINARE_CERTIFICAZIONE" />
			(<wp:i18n key="LABEL_FILE" /> <s:property value="%{#helperDocumenti.certificazioniRichiesteFileName.get(id)}"/>) ?
		</p>
		<div class="azioni">
			<form action="<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/deleteAllegatoCertificazione.action" />" method="post" class="azione">
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
				<div>
					<s:hidden name="id" value="%{id}" id="idDelete"/>
					<input type="hidden" name="ext" value="${param.ext}" />
					<s:submit value="%{#attr.valueYesButton}" title="%{#attr.titleYesButton}" cssClass="button block-ui"></s:submit>
				</div>
			</form>
			<form action="<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/cancelDeleteAllegatoCertificazione.action" />" method="post" class="azione">
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
				<div>
					<input type="hidden" name="ext" value="${param.ext}" />
					<s:submit value="%{#attr.valueNoButton}" title="%{#attr.valueNoButton}" cssClass="button block-ui"></s:submit>
				</div>
			</form>
		</div>
	</s:if>
	<s:elseif test="%{deleteSchedeTecnica && !#helperDocumenti.schedeTecniche.isEmpty()}">
		<p class="question">
			<wp:i18n key="LABEL_ELIMINARE_SCHEDA_TECNICA" />
			(<wp:i18n key="LABEL_FILE" /> <s:property value="%{#helperDocumenti.schedeTecnicheFileName.get(id)}"/>) ?
		</p>
		<div class="azioni">
			<form action="<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/deleteAllegatoSchedaTecnica.action" />" method="post" class="azione">
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
				<div>
					<s:hidden name="id" value="%{id}" id="idDelete"/>
					<input type="hidden" name="ext" value="${param.ext}" />
					<s:submit value="%{#attr.valueYesButton}" title="%{#attr.titleYesButton}" cssClass="button block-ui"></s:submit>
				</div>
			</form>
			<form action="<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/cancelDeleteAllegatoSchedaTecnica.action" />" method="post" class="azione">
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
				<div>
					<input type="hidden" name="ext" value="${param.ext}" />
					<s:submit value="%{#attr.valueNoButton}" title="%{#attr.titleNoButton}" cssClass="button block-ui"></s:submit>
				</div>
			</form>
		</div>
	</s:elseif>
	<s:elseif test="%{deleteImmagine && #helperDocumenti.immagine != null}">
		<p class="question">
			<wp:i18n key="LABEL_ELIMINARE_IMMAGINE" />?
		</p>
		<div class="azioni">
			<form action="<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/deleteAllegatoImmagine.action" />" method="post" class="azione">
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
				<div>
					<input type="hidden" name="ext" value="${param.ext}" />
					<s:submit value="%{#attr.valueYesButton}" title="%{#attr.titleYesButton}" cssClass="button block-ui"></s:submit>
				</div>
			</form>
			<form action="<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/cancelDeleteAllegatoImmagine.action" />" method="post" class="azione">
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
				<div>
					<input type="hidden" name="ext" value="${param.ext}" />
					<s:submit value="%{#attr.valueNoButton}" title="%{#attr.titleNoButton}" cssClass="button block-ui"></s:submit>
				</div>
			</form>
		</div>
	</s:elseif>
	<s:else>
		<fieldset>
			<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_DOCUMENTS" /></legend>

			<p class="noscreen">[ <a href="#" ><wp:i18n key="LABEL_VAI_A_LISTA_DOCUMENTI" /></a> ]</p>
			<p class="noscreen">[ <a href="#" ><wp:i18n key="LABEL_SALTA_DOCUMENTI_VAI_A_BOTTONI" /></a> ]</p>

			<s:if test="%{!#session.dettProdotto.aggiornamento">
				<p class="instructions">
					<wp:i18n key="LABEL_DOCUMENTI_OBBLIGATORI_PROCEDERE_INVIO_PROD" />
				</p>
			</s:if>

			<s:url id="urlDownloadImmagine" namespace="/do/FrontEnd/Cataloghi" action="downloadAllegatoImmagine" />
			<s:url id="urlDownloadFacSimile" namespace="/do/FrontEnd/DocDig" action="downloadDocumentoPubblico" />
			<s:url id="urlDownloadCertificazione" namespace="/do/FrontEnd/Cataloghi" action="downloadAllegatoCertificazione" />
			<s:url id="urlDownloadSchedaTecnica" namespace="/do/FrontEnd/Cataloghi" action="downloadAllegatoSchedaTecnica" />
			
			<div class="table-container">
				<table id="tableDocumenti" class="wizard-table" summary="Tabella dei documenti relativi al prodotto">
					<thead>
						<tr>
							<th scope="col"><wp:i18n key="LABEL_DETTAGLIO_ARTICOLO_DESCRIZIONE" /></th>
							<th scope="col"><wp:i18n key="LABEL_FILE" /></th>
							<th scope="col"><wp:i18n key="ACTIONS" /></th>
						</tr>
					</thead>
					<tbody>
						<!-- IMMAGINE -->
						<tr>
							<form id="tableDocumentiImmaginiForm" action="<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/processPageDocumenti.action" />" method="post" enctype="multipart/form-data">
								<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />

								<s:set var="immagineRequired" value="%{#helperArticolo.obbligoImmagine && #helperDocumenti.immagine == null}" />
								
								<td>
									<wp:i18n key="LABEL_IMMAGINE_PRODOTTO" />
									
									<%-- questa riga � corretta??? "ArticoloType" non ha un attributo "articolo" 
									<s:if test="%{#helperArticolo.articolo.obbligoImmagine && #helperDocumenti.immagine == null}">
									--%>
									<s:if test="%{immagineRequired}">
										<span class="required-field">*</span>
									</s:if>
								</td>
								<td>
									<wp:i18n key="LABEL_SCARICA_IMMAGINE" var="valueScaricaImmagine" />
									<s:set var="immagineCaricata" value="%{false}" />
									<s:if test="%{#helperDocumenti.immagineFileName != null}">
										<s:set var="immagineCaricata" value="%{true}" />
										<c:choose>
											<c:when test="${skin == 'highcontrast' || skin == 'text'}">
												<s:a href="%{#urlDownloadImmagine}" title="%{#attr.valueScaricaImmagine}">
													<wp:i18n key="LABEL_SCARICA_IMMAGINE" />
												</s:a>
											</c:when>
											<c:otherwise>
												<s:a href="%{#urlDownloadImmagine}" cssClass="bkg img" title="%{#attr.valueScaricaImmagine}">
													<s:property value="{#helperDocumenti.immagineFileName}"/>
												</s:a>
											</c:otherwise>
										</c:choose>
									</s:if>
									<s:else>
										<div>
										<input type="hidden" name="ext" value="${param.ext}" />
										<c:set var="imgRequired"><s:property value="%{#immagineRequired}"/></c:set>
										<input type="file" name="immagine" size="20" aria-label="<wp:i18n key='LABEL_IMMAGINE_PRODOTTO'/>" />
										</div>
									</s:else>
									<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/internalServlet/fileupload/fileupload.jsp" />
								</td>
								<td class="azioni">
									<wp:i18n key="LABEL_ELIMINA_IMMAGINE" var="valueEliminaImmagine" />
									<wp:i18n key="BUTTON_ATTACH_FILE" var="valueAllega" />
									<ul>
										<li>
											<s:if test="%{!#immagineCaricata}">
												<s:submit value="%{#attr.valueAllega}" title="%{#attr.valueAllega}" cssClass="button block-ui" method="addImmagine" />
											</s:if>
											<s:else>
												<c:choose>
													<c:when test="${skin == 'highcontrast' || skin == 'text'}">
														<a href="<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/confirmDeleteAllegatoImmagine.action"/>&amp;ext=${param.ext}" 
															 title="${valueEliminaImmagine}"><wp:i18n key="LABEL_ELIMINA_IMMAGINE" />
														</a>
													</c:when>
													<c:otherwise>
														<a class="bkg delete" href="<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/confirmDeleteAllegatoImmagine.action"/>&amp;ext=${param.ext}" 
															 title="${valueEliminaImmagine}" >
														</a>
													</c:otherwise>
												</c:choose>
											</s:else>
										</li>
									</ul>
								</td>
							</form>
						</tr>
						<!-- CERTIFICAZIONI RICHIESTE -->
						<tr>
							<td>
								<wp:i18n key="LABEL_DETTAGLIO_ARTICOLO_CERTIFICAZIONI_RICH" />
							</td>
							<td>
								<s:property value="%{#helperArticolo.certificazioniRichieste}" />
							</td>
							<td class="azioni">
								&nbsp;
							</td>
						</tr>
						<!-- LISTA FAC-SIMILE CERTIFICAZIONI -->
						<s:iterator value="%{#helperArticolo.facSimileCertificazioni}" var="facSimileCertificazione" status="statFacSimile">
							<tr>
								<td>
									<wp:i18n key="LABEL_FACSIMILE_CERTIFICAZIONI" />
									<s:if test="%{#helperArticolo.facSimileCertificazioni.size() > 1}">
										&nbsp;n&ordm;<s:property value="%{#statFacSimile.count}" />
									</s:if>
								</td>
								<td>
									<wp:i18n key="LABEL_SCARICA_FACSIMILE" var="valueScaricaFacsimile" />
									<c:choose>
										<c:when test="${skin == 'highcontrast' || skin == 'text'}">
											<s:a href="%{#urlDownloadFacSimile}?id=%{#facSimileCertificazione.id}" title="%{#attr.valueScaricaFacsimile}">
												<wp:i18n key="LABEL_SCARICA_FACSIMILE" />
											</s:a>
										</c:when>
										<c:otherwise>
											<s:a href="%{#urlDownloadFacSimile}?id=%{#facSimileCertificazione.id}" cssClass="bkg attachment" title="%{#attr.valueScaricaFacsimile}">
												<s:property value="%{#facSimileCertificazione.nomefile}"/>
											</s:a>
										</c:otherwise>
									</c:choose>
								</td>
								<td class="azioni">
									&nbsp;
								</td>
							</tr>
						</s:iterator>
						<!-- LISTA CERTIFICAZIONI RICHIESTE -->
						<s:iterator value="%{#helperDocumenti.certificazioniRichieste}" var="certificazione" status="statCertificazione">
							<tr>
								<td>
									<wp:i18n key="LABEL_CERTIFICAZIONE_RICHIESTA" />
									<s:if test="%{#helperDocumenti.certificazioniRichieste.size() > 1}">
										&nbsp;n&ordm;<s:property value="%{#statCertificazione.count}" />
									</s:if>
								</td>
								<td>
									<wp:i18n key="LABEL_SCARICA_CERTIFICAZIONE" var="valueScaricaCertificazione" />
									<c:choose>
										<c:when test="${skin == 'highcontrast' || skin == 'text'}">
											<s:a href="%{#urlDownloadCertificazione}?id=%{#statCertificazione.index}" title="%{#attr.valueScaricaCertificazione}">
												<wp:i18n key="LABEL_SCARICA_CERTIFICAZIONE" />
											</s:a>
										</c:when>
										<c:otherwise>
											<s:a href="%{#urlDownloadCertificazione}?id=%{#statCertificazione.index}" cssClass="bkg attachment" title="%{#attr.valueScaricaCertificazione}">
												<s:property value="%{#helperDocumenti.certificazioniRichiesteFileName.get(#statCertificazione.index)}"/>
											</s:a>
										</c:otherwise>
									</c:choose>
								</td>
								<td class="azioni">
									<ul>
										<li>
											<c:choose>
												<c:when test="${skin == 'highcontrast' || skin == 'text'}">
													<a href="<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/confirmDeleteAllegatoCertificazione.action"/>&amp;id=<s:property value="%{#statCertificazione.index}"/>&amp;ext=${param.ext}" title='<wp:i18n key="LABEL_ELIMINA_CERTIFICAZIONE" />' >
														<wp:i18n key="LABEL_ELIMINA_CERTIFICAZIONE" />
													</a>
												</c:when>
												<c:otherwise>
													<a href="<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/confirmDeleteAllegatoCertificazione.action"/>&amp;id=<s:property value="%{#statCertificazione.index}"/>&amp;ext=${param.ext}" 
														 title='<wp:i18n key="LABEL_ELIMINA_CERTIFICAZIONE" />' class="bkg delete">
													</a>
												</c:otherwise>
											</c:choose>
										</li>
									</ul>
								</td>
							</tr>
						</s:iterator>
						
						<!-- INSERIMENTO CERTIFICAZIONE RICHIESTA -->
						<tr>
							<form id="tableDocumentiCertificazioniForm" action="<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/processPageDocumenti.action" />" method="post" enctype="multipart/form-data">
								<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
								
								<s:set var="certificazioneRequired" value="%{#helperArticolo.obbligoCertificazioni 
						  				     								 && #helperDocumenti.certificazioniRichieste.isEmpty()}" />
		
								<td>
									<wp:i18n key="LABEL_NUOVA_CERTIFICAZIONE" />
									<s:if test="%{certificazioneRequired}">
										<span class="required-field">*</span>
									</s:if>
								</td>
								<td>
									<div>
									<input type="hidden" name="ext" value="${param.ext}" />
									<c:set var="certRequired"><s:property value="%{#certificazioneRequired}"/></c:set>
									<input type="file" name="certRic" size="20" aria-label="<wp:i18n key='LABEL_NUOVA_CERTIFICAZIONE'/>" />
									</div>
								</td>
								<td class="azioni">
									<wp:i18n key="BUTTON_ATTACH_FILE" var="valueAllega" />
									<ul>
										<li>
											<s:submit value="%{#attr.valueAllega}" title="%{#attr.valueAllega}" cssClass="button block-ui" method="addCertificazioneRichiesta" />
										</li>
									</ul>
								</td>
							</form>
						</tr>
						<!-- LISTA SCHEDE TECNICHE -->
						<s:iterator value="%{#helperDocumenti.schedeTecniche}" var="schedaTecniche" status="statSchedaTecniche">
							<tr>
								<td>
									<wp:i18n key="LABEL_SCHEDA_TECNICA" />
									<s:if test="%{#helperDocumenti.schedeTecniche.size() > 1}">
										&nbsp;n&ordm;<s:property value="%{#statSchedaTecniche.count}" />
									</s:if>
								</td>
								<td>
									<wp:i18n key="LABEL_SCARICA_SCHEDA_TECNICA" var="valueScaricaSchedaTecnica"/>
									<c:choose>
										<c:when test="${skin == 'highcontrast' || skin == 'text'}">
											<s:a href="%{#urlDownloadSchedaTecnica}?id=%{#statSchedaTecniche.index}" title="%{#attr.valueScaricaSchedaTecnica}">
												<wp:i18n key="LABEL_SCARICA_SCHEDA_TECNICA" />
											</s:a>
										</c:when>
										<c:otherwise>
											<s:a href="%{#urlDownloadSchedaTecnica}?id=%{#statSchedaTecniche.index}" cssClass="bkg attachment" title="%{#attr.valueScaricaSchedaTecnica}">
												<s:property value="%{#helperDocumenti.schedeTecnicheFileName.get(#statSchedaTecniche.index)}"/>
											</s:a>
										</c:otherwise>
									</c:choose>
								</td>
								<td class="azioni">
									<ul>
										<li>
											<c:choose>
												<c:when test="${skin == 'highcontrast' || skin == 'text'}">
													<a href="<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/confirmDeleteAllegatoSchedaTecnica.action"/>&amp;id=<s:property value="%{#statSchedaTecniche.index}"/>&amp;ext=${param.ext}" 
													   title='<wp:i18n key="LABEL_ELIMINA_SCHEDA_TECNICA" />' >
														 <wp:i18n key="LABEL_ELIMINA_SCHEDA_TECNICA" />
													</a>
												</c:when>
												<c:otherwise>
													<a href="<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/confirmDeleteAllegatoSchedaTecnica.action"/>&amp;id=<s:property value="%{#statSchedaTecniche.index}"/>&amp;ext=${param.ext}" 
													   title='<wp:i18n key="LABEL_ELIMINA_SCHEDA_TECNICA" />' class="bkg delete">
													</a>
												</c:otherwise>
											</c:choose>
										</li>
									</ul>
								</td>
							</tr>
						</s:iterator>
						<!-- INSERIMENTO SCHEDA TECNICA -->
						<tr>
							<form id="tableDocumentiSchedeTecnicheForm" action="<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/processPageDocumenti.action" />" method="post" enctype="multipart/form-data">
								<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
								
								<s:set var="schedaTecRequired" value="%{#helperArticolo.obbligoSchedaTecnica && #helperDocumenti.schedeTecniche.isEmpty()}" />

								<td>
									<wp:i18n key="LABEL_NUOVA_SCHEDA_TECNICA" />
									<s:if test="%{schedaTecRequired}">
										<span class="required-field">*</span>
									</s:if>
								</td>
								<td>
									<div>
									<input type="hidden" name="ext" value="${param.ext}" />
									<c:set var="schedaRequired"><s:property value="%{#schedaTecRequired}"/></c:set>
									<span class="noscreen"><wp:i18n key='LABEL_FILE'/> : </span>
									<input type="file" name="schedTec" size="20" aria-label="<wp:i18n key='LABEL_NUOVA_SCHEDA_TECNICA'/>" />
									</div>
								</td>
								<td class="azioni">
									<wp:i18n key="BUTTON_ATTACH_FILE" var="valueAllega" />
									<ul>
										<li>
											<s:submit value="%{#attr.valueAllega}" title="%{#attr.valueAllega}" cssClass="button block-ui" method="addSchedaTecnica" />
										</li>
									</ul>
								</td>
							</form>	
						</tr>
					</tbody>
				</table>
			</div>
 			
			<s:set var="kbCaricati" value="%{dimensioneAttualeFileCaricati}"></s:set>
			<s:set var="kbDisponibili" value="%{limiteTotaleUpload - dimensioneAttualeFileCaricati}"></s:set>
			<p>
				<%--
				<wp:i18n key="LABEL_MAX_FILE_SIZE" /> <strong><s:property value="%{limiteUploadFile}" /></strong> KB.<br/>
				<wp:i18n key="LABEL_MAX_REQUEST_SIZE_1" /> <strong><s:property value="%{#kbCaricati}" /></strong> KB <wp:i18n key="LABEL_MAX_REQUEST_SIZE_2" /> 
				<strong><s:property value="getText('{0,number,#,##0}',{#kbDisponibili})"/></strong> <wp:i18n key="LABEL_KB_LIBERI_NEL_CATALOGO_PRODOTTI" />
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/internalServlet/fileupload/estensioniAmmesse.jsp"/>
				--%>
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/internalServlet/fileupload/infoUploadFile.jsp">
					<jsp:param name="dimensioneAttualeFileCaricati" value="${kbCaricati}"/>
				</jsp:include>
			</p>
		</fieldset>
		
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/skip_main_content_from_buttons.jsp" />

		<form action="<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/processPageDocumenti.action" />" method="post">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			<div class="azioni">
				<input type="hidden" name="ext" value="${param.ext}" />
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_previous.jsp" />
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_next.jsp" />
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_cancel.jsp" />
			</div>
		</form>
	</s:else>
</div>