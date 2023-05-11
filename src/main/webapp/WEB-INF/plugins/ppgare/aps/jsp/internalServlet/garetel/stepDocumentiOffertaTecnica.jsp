<%@ taglib prefix="wp"  uri="aps-core.tld" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="s"   uri="/struts-tags" %>

<!-- OBSOLETO <s:set var="helper" value="%{#session['offertaTecnica']}" /> -->
<s:set var="bustaTecnica" value="%{#session['dettaglioOffertaGara'].bustaTecnica}" />
<s:set var="helper" value="%{#bustaTecnica.helper}" />
<s:set var="codiceTitolo" value="%{#bustaTecnica.codiceLotto}" />
<s:set var="documentiRichiesti" value="%{#session['dettaglioOffertaGara'].bustaTecnica.documentiRichiestiDB}" />
<s:set var="documenti" value="%{#helper.documenti}" />

<%-- <script type="text/javascript" src='<wp:resourceURL/>static/js/ppgare/uploadFile.js'></script> --%>
<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/internalServlet/fileupload/uploadFile.jsp" />

<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/send_blockUI.jsp" />

<c:set var="descrizioneHint"><wp:i18n key="LABEL_INSERIRE_DESCRIZIONE_PER_ALLEGARE_DOC" /></c:set>

<%-- prepara il BACK LINK della pagina di visualizzazione della firma digitale --%>
<%--<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/backFromPageFirmaDigitale.jsp"/>--%>
<c:set var="fromPageFirmaDigitale" scope="session"><wp:action path="/ExtStr2/do/FrontEnd/GareTel/openPageOffTecDocumenti.action"/></c:set>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2><wp:i18n key="TITLE_PAGE_GARETEL_OFFERTA_TECNICA" /> [${codiceTitolo}]</h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/field_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_messages.jsp" />
	
	<jsp:include page="stepsOffertaTecnica.jsp" />

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_WIZ_DOCUMENTI_OFFERTA_TECNICA_TEL"/>
	</jsp:include>

	<s:if test="%{#documenti.datiModificati}">
		<div class="balloon">
			<div class="balloon-content balloon-alert"><wp:i18n key="LABEL_GARETEL_MODIFICHE_NON_SALVATE_A_DOCUMENTI" /></div>
		</div>
	</s:if>
	
	<wp:i18n key="LABEL_YES" var="valueYesButton" />
	<wp:i18n key="LABEL_NO" var="valueNoButton" />
	<wp:i18n key="TITLE_CONFIRM_DEL_YES" var="titleEliminaDocumentoButton" />
	<wp:i18n key="TITLE_CONFIRM_DEL_NO" var="titleAnnullaEliminaButton" />
	
	<s:if test="%{deleteDocRichiesto}">
		<p class="question">
			<s:set var="nomeDocRichiesto" value="" />
			<s:set var="docId" value="%{#documenti.docRichiestiId.get(id)}"/>
			<s:iterator value="#documentiRichiesti" var="documento" status="statDocRich">
				<c:if test="${docId == documento.id}">
					<s:set var="nomeDocRichiesto" value="%{nome}" />
				</c:if>
			</s:iterator>
			<wp:i18n key="LABEL_ELIMINA_DOCUMENTO" /> "<s:property value="%{#nomeDocRichiesto}"/>" 
			(<wp:i18n key="LABEL_FILE" /> <s:property value="%{#documenti.docRichiestiFileName.get(id)}"/>) ?
		</p>
		<div class="azioni">
			<form action="<wp:action path="/ExtStr2/do/FrontEnd/GareTel/deleteOffTecAllegatoRichiesto.action" />" method="post" class="azione">
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
				
				<div>
					<input type="hidden" name="ext" value="${param.ext}" />
					<s:hidden name="id" value="%{id}" id="idDelete"/>
					<s:hidden name="tipoBusta" value="%{#documenti.tipoBusta}" />
					<s:hidden name="codice" value="%{#documenti.codice}" />
					<s:submit value="%{#attr.valueYesButton}" title="%{#attr.titleEliminaDocumentoButton}" cssClass="button block-ui"></s:submit>
				</div>
			</form>
			<form action="<wp:action path="/ExtStr2/do/FrontEnd/GareTel/cancelDeleteOffTecAllegatoRichiesto.action" />" method="post" class="azione">
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
				
				<div>
					<input type="hidden" name="ext" value="${param.ext}" />
					<s:hidden name="tipoBusta" value="%{#documenti.tipoBusta}" />
					<s:hidden name="operazione" value="%{#documenti.operazione}" />
					<s:hidden name="codice" value="%{#documenti.codice}" />
					<s:submit value="%{#attr.valueNoButton}" title="%{#attr.titleAnnullaEliminaButton}" cssClass="button block-ui"></s:submit>
				</div>
			</form>
		</div>
	</s:if>
	<s:elseif test="%{deleteDocUlteriore}">
		<p class="question">
			<wp:i18n key="LABEL_ELIMINA_DOCUMENTO" /> "<s:property value="%{#documenti.docUlterioriDesc.get(id)}"/>" 
			(<wp:i18n key="LABEL_FILE" /> <s:property value="%{#documenti.docUlterioriFileName.get(id)}"/>)?
		</p>
		<div class="azioni">
			<form action="<wp:action path="/ExtStr2/do/FrontEnd/GareTel/deleteOffTecAllegatoUlteriore.action" />" method="post" class="azione">
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
				
				<div>
					<input type="hidden" name="ext" value="${param.ext}" />
					<s:hidden name="id" value="%{id}" id="idDelete"/>
					<s:hidden name="tipoBusta" value="%{#documenti.tipoBusta}" />
					<s:hidden name="operazione" value="%{#documenti.operazione}" />
					<s:hidden name="codice" value="%{#documenti.codice}" />
					<s:submit value="%{#attr.valueYesButton}" title="%{#attr.titleEliminaDocumentoButton}" cssClass="button block-ui"></s:submit>
				</div>
			</form>
			<form action="<wp:action path="/ExtStr2/do/FrontEnd/GareTel/cancelDeleteOffTecAllegatoUlteriore.action" />" method="post" class="azione">
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
				
				<div>
					<input type="hidden" name="ext" value="${param.ext}" />
					<s:hidden name="tipoBusta" value="%{#documenti.tipoBusta}" />
					<s:hidden name="operazione" value="%{#documenti.operazione}" />
					<s:hidden name="codice" value="%{#documenti.codice}" />
					<s:submit value="%{#attr.valueNoButton}" title="%{#attr.titleAnnullaEliminaButton}" cssClass="button block-ui"></s:submit>
				</div>
			</form>
		</div>
	</s:elseif>
	<s:else>
		<s:set var="imgPdf"><wp:resourceURL/>static/img/pdf.svg</s:set>
		<s:set var="imgDocumentoFirmato"><wp:resourceURL/>static/img/smartcard.svg</s:set>
		<s:set var="imgExcel"><wp:resourceURL/>static/img/xls.svg</s:set>
		<s:set var="imgDocNonFirmato"><wp:resourceURL/>static/img/not-signed-alert.svg</s:set>

		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/skip_form_buttons.jsp" />

		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/mandatory_fields_message.jsp" />

		<s:if test="%{esisteFileConFirmaNonVerificata}">
			<p class="important not-signed-alert">
			<wp:i18n key="LABEL_MESSAGE_WARNING" />: <wp:i18n key="LABEL_FIRMA_DOCUMENTI_NON_VERIFICABILE_1" /> <img class="resize-svg-16" alt='<wp:i18n key="LABEL_DOCUMENTO_FIRMA_NON_VERIFICATA" />' src="<s:property value='%{#imgDocNonFirmato}'/>">, 
			<wp:i18n key="LABEL_FIRMA_DOCUMENTI_NON_VERIFICABILE_2" />
			</p>
		</s:if>

		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/skip_main_content_from_buttons.jsp" />

		<fieldset>
			<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_DOCUMENTS"/></legend>
			
			<wp:i18n key="TITLE_DOWNLOAD_FACSIMILE" var="titleDownloadFacsimile" />
			<wp:i18n key="LABEL_DOWNLOAD_FACSIMILE" var="valueDownloadFacsimile" />
			<wp:i18n key="TITLE_SCARICA_ALLEGATO" var="titleDownloadAllegato" />
			<wp:i18n key="TITLE_ELIMINA_ALLEGATO" var="titleEliminaAllegato" />
			<wp:i18n key="LABEL_DOCUMENTO_FIRMA_NON_VERIFICATA" var="valueDocumentoFirmaNonVerificata" />
			
			<div class="table-container">
				<table id="tableDocumenti" class="wizard-table">
				<caption style="display:none;"><wp:i18n key="LABEL_ELENCO_DOCUMENTI"/></caption>
					<thead>
						<tr>
							<th scope="col"><wp:i18n key="LABEL_DESCRIZIONE"/></th>
							<th scope="col"><wp:i18n key="LABEL_FACSIMILE"/></th>
							<th scope="col"><wp:i18n key="LABEL_ALLEGATO"/></th>
						</tr>
					</thead>
					<tbody>
						<!-- DOCUMENTI RICHIESTI -->
						<s:if test="%{#documentiRichiesti.size() > 0}">
							<s:url id="urlDownloadDocRichiesto" namespace="/do/FrontEnd/GareTel" action="downloadOffTecAllegatoRichiesto" />
							<s:url id="urlDownloadModello" namespace="/do/FrontEnd/DocDig" action="downloadDocumentoPubblico" />

							<s:iterator value="#documentiRichiesti" var="documento" status="statDocRich">
							
								<s:set var="isDocFirmato" value='%{#documento.nomefilefacsimile.toUpperCase().endsWith(".P7M") ||
											     				   #documento.nomefilefacsimile.toUpperCase().endsWith(".TSD")}' />
								<tr>
									<td>
										<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/attachment_info.jsp" >
											<jsp:param name="formatoFile" value="${formato}" />
											<jsp:param name="nome" value="${nome}" />
											<jsp:param name="obbligatorio" value="${obbligatorio}" />
										</jsp:include>
									</td>
									<td class="azioni">
										<ul>
											<li>
												<s:if test="%{#documento.idfacsimile != null}">

													<s:if test="%{isDocFirmato}">														
														<%-- DOCUMENTO FIRMATO .P7M .TSD --%>
														<s:set var="urlDownload"><wp:action path="/ExtStr2/do/FrontEnd/DocDig/downloadDocumentoPubblico.action"/>&amp;id=<s:property value="%{#documento.idfacsimile}"/></s:set>
													</s:if>
													<s:else>
														<%-- DOCUMENTO SENZA FIRMATO --%>
														<s:set var="urlDownload"><s:property value="%{#urlDownloadModello}"/>?id=<s:property value="%{#documento.idfacsimile}"/></s:set>													
													</s:else>

													<c:choose>
														<c:when test="${skin == 'highcontrast' || skin == 'text'}">
															<s:a href="%{#urlDownload}" title="%{#attr.titleDownloadFacsimile}"><wp:i18n key="LABEL_DOWNLOAD_FACSIMILE" /></s:a>
														</c:when>
														<c:otherwise>
															<s:a href="%{#urlDownload}" title="%{#attr.titleDownloadFacsimile}" cssClass="bkg download"></s:a>
														</c:otherwise>
													</c:choose>

												</s:if>
											</li>
										</ul>
									</td>
									<td class="azioni">
										<ul>
											<s:set var="caricato" value="%{false}" />
											<s:iterator value="#documenti.docRichiestiId" var="idDoc" status="statusCaricato">
												<s:if test="%{#idDoc == #documento.id && #documenti.docRichiestiVisibile.get(#statusCaricato.index)}">
												
													<%-- notificata i doc senza firma non verificabili --%>	
												 	<s:set var="firmaWarning" value="%{false}" />
													<s:if test="%{#documenti.docRichiestiFirmaBean.get(#statusCaricato.index)!=null}">
														<s:set var="firmaWarning" value="%{!(#documenti.docRichiestiFirmaBean.get(#statusCaricato.index).firmacheck)}" />
													</s:if>
													<s:if test='%{#firmaWarning}'>
														<img class="resize-svg-16" title="<wp:i18n key='LABEL_DOCUMENTO_FIRMA_NON_VERIFICATA' />" alt="<wp:i18n key='LABEL_DOCUMENTO_FIRMA_NON_VERIFICATA' />" src="<s:property value='%{#imgDocNonFirmato}'/>">
													</s:if>
													<c:choose>
														<c:when test="${skin == 'highcontrast' || skin == 'text'}">
															<li>
																<s:a href="%{#urlDownloadDocRichiesto}?id=%{#statusCaricato.index}&amp;tipoBusta=%{#documenti.tipoBusta}&amp;codice=%{#documenti.codice}" 
																	 title="%{#attr.titleDownloadAllegato}">
																	<wp:i18n key="LABEL_SCARICA_ALLEGATO" />
																</s:a>
																<span>(<s:property value="%{#documenti.docRichiestiSize.get(#statusCaricato.index)}"/> KB)</span>
															</li>
															<li>
																<a href='<wp:action path="/ExtStr2/do/FrontEnd/GareTel/confirmDeleteOffTecAllegatoRichiesto.action"/>&amp;id=${statusCaricato.index}&amp;ext=${param.ext}&amp;tipoBusta=<s:property value="%{#documenti.tipoBusta}"/>&amp;codice=<s:property value="%{#documenti.codice}"/>&amp;${tokenHrefParams}' 
																   title='<s:property value="%{#attr.titleEliminaAllegato}"/>'>
																	<wp:i18n key="LABEL_ELIMINA_ALLEGATO" />
																</a>
															</li>
														</c:when>
														<c:otherwise>
															<li <s:if test="%{#firmaWarning}">class="not-signed-alert"</s:if> >
																<s:a href="%{#urlDownloadDocRichiesto}?id=%{#statusCaricato.index}&amp;tipoBusta=%{#documenti.tipoBusta}&amp;codice=%{#documenti.codice}" 
																	 title='%{#attr.titleDownloadAllegato}' cssClass="bkg download">
																	<s:property value="%{#documenti.docRichiestiFileName.get(#statusCaricato.index)}"/>
																</s:a>
																<span>(<s:property value="%{#documenti.docRichiestiSize.get(#statusCaricato.index)}"/> KB)</span>
															</li>
															<li>
																<a href='<wp:action path="/ExtStr2/do/FrontEnd/GareTel/confirmDeleteOffTecAllegatoRichiesto.action"/>&amp;id=${statusCaricato.index}&amp;ext=${param.ext}&amp;tipoBusta=<s:property value="%{#documenti.tipoBusta}"/>&amp;codice=<s:property value="%{#documenti.codice}"/>&amp;${tokenHrefParams}' 
																   title='<s:property value="%{#attr.titleEliminaAllegato}"/>' class="bkg delete">
																</a>
															</li>
														</c:otherwise>
													</c:choose>
													<s:set var="caricato" value="%{true}" />
												</s:if>
											</s:iterator>
											<s:if test="%{!#caricato}">
												<li>
													<form action="<wp:action path="/ExtStr2/do/FrontEnd/GareTel/processPageOffTecDocumenti.action" />" method="post" 
																enctype="multipart/form-data" id="docRichiestoForm<s:property value="#statDocRich.index"/>">
														<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
														
														<div>
															<input type="hidden" name="ext" value="${param.ext}" />
															<input type="hidden" name="tipoBusta" value="<s:property value="%{#documenti.tipoBusta}"/>"/>
															<input type="hidden" name="operazione" value="<s:property value="%{#documenti.operazione}"/>"/>
															<input type="hidden" name="codice" value="<s:property value="%{#documenti.codice}"/>"/>
															<input type="hidden" name="docRichiestoId" value="<s:property value="%{#documento.id}"/>"/>
															<input type="hidden" name="formato" value="<s:property value="%{formato}"/>" />
															
															<label class="noscreen" for="docRichiesto_<s:property value="%{#documento.id}"/>"><wp:i18n key='LABEL_FILE'/> : </label>
																<input type="file" name="docRichiesto" id="docRichiesto_<s:property value="%{#documento.id}"/>" size="20" cssClass="block-ui"
																	aria-label="${nome}" 
																	<s:if test="%{formato == DOCUMENTO_FORMATO_FIRMATO}">accept=".p7m,.tsd,.pdf,.xml"</s:if>
																	<s:elseif test="%{formato == DOCUMENTO_FORMATO_PDF}">accept=".pdf"</s:elseif>
																	<s:elseif test="%{formato == DOCUMENTO_FORMATO_EXCEL}">accept=".xls,.xlsx,.ods"</s:elseif> />&nbsp;
															
															<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_adddocrich.jsp" >
																<jsp:param value="${formato}" name="formato"/>
															</jsp:include>
														</div>
													</form>
												</li>
											</s:if>
										</ul>
									</td>
								</tr>
							</s:iterator>
						</s:if>

						<!-- DOCUMENTI ULTERIORI -->
						<s:url id="urlDownloadDocUlteriore" namespace="/do/FrontEnd/GareTel" action="downloadOffTecAllegatoUlteriore" />

						<s:iterator value="%{#documenti.docUlterioriDesc}" var="descDocUlteriore" status="status">
							<s:if test="%{#documenti.docUlterioriVisibile.get(#status.index)}" >
								<tr>
									<td>
										<s:property value="%{#descDocUlteriore}"/>
									</td>
									<td>
										<%-- COLONNA VUOTA --%>
									</td>
									<td class="azioni">
										<ul>
											<s:set var="firmaWarning" value="%{false}" />
											<s:if test="%{#documenti.docUlterioriFirmaBean.get(#status.index)!=null}">
												<s:set var="firmaWarning" value="%{!(#documenti.docUlterioriFirmaBean.get(#status.index).firmacheck)}" />
											</s:if>
											<s:if test='%{#firmaWarning}'>
												<img class="resize-svg-16" title="<wp:i18n key='LABEL_DOCUMENTO_FIRMA_NON_VERIFICATA' />" alt="<wp:i18n key='LABEL_DOCUMENTO_FIRMA_NON_VERIFICATA' />" src="<s:property value='%{#imgDocNonFirmato}'/>">
											</s:if>
											<c:choose>
												<c:when test="${skin == 'highcontrast' || skin == 'text'}">
													<li>
														<s:a href="%{#urlDownloadDocUlteriore}?id=%{#status.index}&amp;tipoBusta=%{#documenti.tipoBusta}&amp;codice=%{#documenti.codice}" 
	 														 title="%{#attr.titleDownloadAllegato}">  
															<wp:i18n key="LABEL_SCARICA_ALLEGATO" />
														</s:a> 
														<span>(<s:property value="%{#documenti.docUlterioriSize.get(#status.index)}" /> KB)</span> 
													</li>
													<li>
														<a href='<wp:action path="/ExtStr2/do/FrontEnd/GareTel/confirmDeleteOffTecAllegatoUlteriore.action"/>&amp;id=${status.index}&amp;ext=${param.ext}&amp;tipoBusta=<s:property value="%{#documenti.tipoBusta}"/>&amp;codice=<s:property value="%{#documenti.codice}"/>&amp;${tokenHrefParams}' 
														   title='<s:property value="%{#attr.titleEliminaAllegato}"/>' >
															<wp:i18n key="LABEL_ELIMINA_ALLEGATO" />
														</a>
													</li>
												</c:when>
												<c:otherwise>
													<li>
														<s:a href="%{#urlDownloadDocUlteriore}?id=%{#status.index}&amp;tipoBusta=%{#documenti.tipoBusta}&amp;codice=%{#documenti.codice}" 
	 														 title="%{#attr.titleDownloadAllegato}" cssClass="bkg download"> 
															<s:property value="%{#documenti.docUlterioriFileName.get(#status.index)}"/>  
	 													</s:a>  
														<span>(<s:property value="%{#documenti.docUlterioriSize.get(#status.index)}" /> KB)</span>  
													</li>
													<li>
														<a href='<wp:action path="/ExtStr2/do/FrontEnd/GareTel/confirmDeleteOffTecAllegatoUlteriore.action"/>&amp;id=${status.index}&amp;ext=${param.ext}&amp;tipoBusta=<s:property value="%{#documenti.tipoBusta}"/>&amp;codice=<s:property value="%{#documenti.codice}"/>&amp;${tokenHrefParams}' 
														   title='<s:property value="%{#attr.titleEliminaAllegato}"/>' class="bkg delete">
														</a>
													</li>
												</c:otherwise>
											</c:choose>
										</ul>
									</td>
								</tr>
							</s:if>
						</s:iterator>

						<tr>
							<td colspan="3">
								<p class="noscreen">[ <a href="#" id="insDoc" ><wp:i18n key="SKIP_DOC_ULTERIORI_TO_FORM_BUTTON" /></a> ]</p>
								<form action="<wp:action path="/ExtStr2/do/FrontEnd/GareTel/processPageOffTecDocumenti.action" />" method="post" 
											enctype="multipart/form-data" id="docUlterioreForm">
									<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
									
									<div>
										<label class="noscreen" for="docUlterioreDesc"><wp:i18n key="LABEL_DOCUMENTO_DESCRIZIONE_ULTERIORE" /> : </label>
										<input type="text" name="docUlterioreDesc" id="docUlterioreDesc" class="long-text" placeholder="${descrizioneHint}" aria-label="<s:property value='%{#descDocUlteriore}'/>" />&nbsp;
										<label class="noscreen" for="docUlteriore"><wp:i18n key='LABEL_FILE'/> : </label><input type="file" name="docUlteriore" id="docUlteriore" cssClass="block-ui" />&nbsp;
										<input type="hidden" name="ext" value="${param.ext}" />
										<input type="hidden" name="tipoBusta" value="<s:property value="%{#documenti.tipoBusta}"/>"/>
										<input type="hidden" name="operazione" value="<s:property value="%{#documenti.operazione}"/>"/>
										<input type="hidden" name="codice" value="<s:property value="%{#helper.codice}"/>"/>
										<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_adddocult.jsp" />
									</div>
									<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/internalServlet/fileupload/fileupload.jsp" />
								</form>
							</td>
						</tr>
						
					</tbody>
				</table>
			</div>
			<s:set var="kbCaricati" value="%{dimensioneAttualeFileCaricati}"></s:set>
			<s:set var="kbDisponibili" value="%{limiteTotaleUploadDocBusta - dimensioneAttualeFileCaricati}"></s:set>
			<p>
				<wp:i18n key="LABEL_MAX_FILE_SIZE" /> <strong><s:property value="%{limiteUploadFile}" /></strong> KB.<br/>
				<wp:i18n key="LABEL_MAX_REQUEST_SIZE_1" /> <strong><s:property value="%{#kbCaricati}" /></strong> KB, <wp:i18n key="LABEL_MAX_REQUEST_SIZE_2" /> 
				<strong><s:property value="%{#kbDisponibili}" /></strong> KB.
			</p>
		</fieldset>

		<form action="<wp:action path="/ExtStr2/do/FrontEnd/GareTel/processPageOffTecDocumenti.action" />" method="post">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			
			<div class="azioni">
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_previous.jsp" />
				
				<s:if test="%{#documenti.datiModificati}">
					<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_cancel.jsp" />
					<wp:i18n key="BUTTON_SAVE" var="valueSaveButton" />
					<s:submit value="%{#attr.valueSaveButton}" title="%{#attr.valueSaveButton}" cssClass="button" method="save"></s:submit>
				</s:if>
				<s:else>
					<wp:i18n key="BUTTON_WIZARD_BACK_TO_MENU" var="valueBackToMenuButton" />
					<s:submit value="< %{#attr.valueBackToMenuButton}" title="%{#attr.valueBackToMenuButton}" cssClass="button" method="quit"></s:submit>
				</s:else>
				
				<input type="hidden" name="codice" value="<s:property value="%{#helper.codice}"/>"/>
				<input type="hidden" name="codiceGara" value="<s:property value="%{#helper.gara.codice}"/>"/>
				<input type="hidden" name="tipoBusta" value="<s:property value="%{#documenti.tipoBusta}"/>"/>
				<input type="hidden" name="operazione" value="<s:property value="%{#documenti.operazione}"/>"/>
				<input type="hidden" name="progressivoOfferta" value="<s:property value="%{#helper.progressivoOfferta}"/>"/>
			</div>
		</form>
	</s:else>
</div>