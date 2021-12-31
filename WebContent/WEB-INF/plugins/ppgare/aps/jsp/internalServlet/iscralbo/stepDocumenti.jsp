<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>

<es:checkCustomization var="visGeneraDomandaPDF" objectId="ISCRALBO-DOCUM" attribute="PDFDOMANDA" feature="VIS" />

<%-- <script type="text/javascript" src='<wp:resourceURL/>static/js/ppgare/uploadFile.js'></script> --%>
<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/internalServlet/fileupload/uploadFile.jsp" />

<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/send_blockUI.jsp" />

<c:set var="descrizioneHint"><wp:i18n key="LABEL_INSERIRE_DESCRIZIONE_PER_ALLEGARE_DOC" /></c:set>

<%-- prepara il BACK LINK della pagina di visualizzazione della firma digitale --%>
<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/backFromPageFirmaDigitale.jsp"/>

<%--
<s:if test="%{#session.dettIscrAlbo != null}">
	<s:set name="sessionIdObj" value="'dettIscrAlbo'"/>
	<c:set var="sessionIdObj" value="dettIscrAlbo"/>
	<c:set var="nextAction" value="/ExtStr2/do/FrontEnd/IscrAlbo/processPageIscrAlboGeneraPdf.action"/>		
	<s:set var="helper" value="%{#session.dettIscrAlbo}"/>
	<s:set var="helperDoc" value="%{#session.dettIscrAlbo.documenti}"/>
</s:if>
<s:else>
	<s:set name="sessionIdObj" value="'dettRinnAlbo'"/>
	<c:set var="sessionIdObj" value="dettRinnAlbo"/>
	<c:set var="nextAction" value="/ExtStr2/do/FrontEnd/IscrAlbo/processPageRinnovoGeneraPdf.action"/>	
	<s:set var="helper" value="%{#session.dettRinnAlbo}"/>
	<s:set var="helperDoc" value="%{#session.dettRinnAlbo.documenti}"/>
</s:else>
--%>
<s:if test="%{#session.dettIscrAlbo != null}">
	<s:set name="sessionIdObj" value="'dettIscrAlbo'"/>
	<c:set var="sessionIdObj" value="dettIscrAlbo"/>
	<c:set var="nextAction" value="/ExtStr2/do/FrontEnd/IscrAlbo/processPageIscrAlboGeneraPdf.action"/>
</s:if>
<s:else>
	<s:set name="sessionIdObj" value="'dettRinnAlbo'"/>
	<c:set var="sessionIdObj" value="dettRinnAlbo"/>
	<c:set var="nextAction" value="/ExtStr2/do/FrontEnd/IscrAlbo/processPageRinnovoGeneraPdf.action"/>	
</s:else>

<s:set name="helper" value="%{#session[#sessionIdObj]}"/>
<s:set name="helperDoc" value="%{#helper.documenti}"/>


<s:if test="%{!#session.dettIscrAlbo.aggiornamentoIscrizione}">
	<s:if test="%{#session.dettIscrAlbo.tipologia == 2}">
		<c:set var="codiceBalloon" value="BALLOON_ISCR_CATALOGO_DOCUMENTI"/>
	</s:if>
	<s:else>
		<c:set var="codiceBalloon" value="BALLOON_ISCR_ALBO_DOCUMENTI"/>
	</s:else>	
</s:if>
<s:else>
	<c:set var="codiceBalloon" value="BALLOON_AGG_ISCR_ALBO_DOCUMENTI"/>
</s:else>	

<s:if test="%{#helper.tipologia == 2}">
	<c:set var="titolo"><wp:i18n key='TITLE_PAGE_ISCRALBO_MERCATO_ELETTRONICO'/></c:set>
</s:if>
<s:else>
	<c:set var="titolo"><wp:i18n key='TITLE_PAGE_ISCRALBO_ELENCO_OE'/></c:set>
</s:else>

<%-- verifica se visualizzare il messaggio di warning per i documenti senza firma --%>
<s:set var="documentiNonFirmati" value="false"/>
<s:if test="%{documentiRichiesti.size() > 0}">
	<s:iterator value="documentiRichiesti" var="documento" status="statDocRich">
		<s:iterator value="#helperDoc.docRichiestiId" var="idDoc" status="status">
			<s:if test="%{#idDoc == #documento.id}"> 	
				<%-- notificata i doc senza firma non verificabili --%>					
				<s:set var="firmaWarning" 
						value='%{formato == DOCUMENTO_FORMATO_FIRMATO &&
								 !(#helperDoc.docRichiestiFileName.get(#status.index).toUpperCase().endsWith(".P7M") ||
						           #helperDoc.docRichiestiFileName.get(#status.index).toUpperCase().endsWith(".TSD"))}' />							           												
				<s:if test="%{#firmaWarning}">
					<s:set var="documentiNonFirmati" value="true"/>
				</s:if>																																				
			</s:if>
		</s:iterator>
	</s:iterator>
</s:if>


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">
	
	<s:if test="%{!#helper.aggiornamentoIscrizione && !#helper.rinnovoIscrizione}">
		<h2><wp:i18n key='TITLE_PAGE_ISCRALBO_RICHIESTA_ISCRIZIONE'/> <c:out value="${titolo}" /></h2>
	</s:if>
	<s:elseif test="%{#helper.rinnovoIscrizione}">
		<h2><wp:i18n key='TITLE_PAGE_ISCRALBO_RICHIESTA_ISCRIZIONE'/> <c:out value="${titolo}" /></h2>
	</s:elseif>
	<s:else>
		<h2><wp:i18n key='TITLE_PAGE_ISCRALBO_AGGIORNAMENTO_ISCRIZIONE'/> <c:out value="${titolo}" /></h2>
	</s:else>
 
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/field_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_messages.jsp" />

	<jsp:include page="stepsIscrizione.jsp" >
		<jsp:param value="${sessionIdObj}" name="sessionIdObj"/>
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="${codiceBalloon}"/>
	</jsp:include>

	<wp:i18n key="LABEL_YES" var="valueYesButton" />
	<wp:i18n key="LABEL_NO" var="valueNoButton" />
	<wp:i18n key="TITLE_CONFIRM_DEL_YES" var="titleYesButton"/>
	<wp:i18n key="TITLE_CONFIRM_DEL_NO" var="titleNoButton"/>
	
	<s:if test="%{deleteDocRichiesto}">
		<p class="question">
			<s:set var="nomeDocRichiesto" value="" />
			<s:set var="docId" value="%{#helperDoc.docRichiestiId.get(id)}"/>
			<s:iterator value="documentiRichiesti" var="documento" status="statDocRich">
				<c:if test="${docId == documento.id}">
					<s:set var="nomeDocRichiesto" value="%{nome}" />
				</c:if>
			</s:iterator>
			<wp:i18n key='LABEL_ELIMINA_DOCUMENTO'/> "<s:property value="%{#nomeDocRichiesto}"/>" 
			(file <s:property value="%{#helperDoc.docRichiestiFileName.get(id)}"/>) ?
		</p>
		<div class="azioni">
			<form action="<wp:action path="/ExtStr2/do/FrontEnd/IscrAlbo/deleteAllegatoRichiesto.action" />" method="post" class="azione">
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
				
				<div>
					<s:hidden name="id" value="%{id}" id="idDelete"/>
					<input type="hidden" name="ext" value="${param.ext}" />
					<s:submit value="%{#attr.valueYesButton}" title="%{#attr.titleYesButton}" cssClass="button block-ui"></s:submit>
				</div>
			</form>
			<form action="<wp:action path="/ExtStr2/do/FrontEnd/IscrAlbo/cancelDeleteAllegatoRichiesto.action" />" method="post" class="azione">
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
				
				<div>
					<input type="hidden" name="ext" value="${param.ext}" />
					<s:submit value="%{#attr.valueNoButton}" title="%{#attr.titleNoButton}" cssClass="button block-ui"></s:submit>
				</div>
			</form>
		</div>
	</s:if>
	<s:elseif test="%{deleteDocUlteriore}">
		<p class="question">
			<wp:i18n key='LABEL_ELIMINA_DOCUMENTO'/> "<s:property value="%{#helperDoc.docUlterioriDesc.get(id)}"/>" 
			(file <s:property value="%{#helperDoc.docUlterioriFileName.get(id)}"/>)?
		</p>
		<div class="azioni">
			<form action="<wp:action path="/ExtStr2/do/FrontEnd/IscrAlbo/deleteAllegatoUlteriore.action" />" method="post" class="azione">
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
				
				<div>
					<s:hidden name="id" value="%{id}" id="idDelete"/>
					<input type="hidden" name="ext" value="${param.ext}" />
					<s:submit value="%{#attr.valueYesButton}" title="%{#attr.titleYesButton}" cssClass="button block-ui"></s:submit>
				</div>
			</form>
			<form action="<wp:action path="/ExtStr2/do/FrontEnd/IscrAlbo/cancelDeleteAllegatoUlteriore.action" />" method="post" class="azione">
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
				
				<div>
					<input type="hidden" name="ext" value="${param.ext}" />
					<s:submit value="%{#attr.valueNoButton}" title="%{#attr.titleNoButton}" cssClass="button block-ui"></s:submit>
				</div>
			</form>
		</div>
	</s:elseif>
	<s:else>
		<s:set var="imgPdf"><wp:resourceURL/>static/img/pdf.svg</s:set>
		<s:set var="imgDocumentoFirmato"><wp:resourceURL/>static/img/smartcard.svg</s:set>
		<s:set var="imgExcel"><wp:resourceURL/>static/img/xls.svg</s:set>
		<s:set var="imgDocNonFirmato"><wp:resourceURL/>static/img/not-signed-alert.svg</s:set>
		
		<p class="noscreen">[ <a href="#" ><wp:i18n key="LABEL_VAI_A_LISTA_DOCUMENTI" /></a> ]</p>
		<p class="noscreen">[ <a href="#" ><wp:i18n key="LABEL_SALTA_DOCUMENTI_VAI_A_BOTTONI" /></a> ]</p>

		<s:url id="urlPdfDomandaDownload" namespace="/do/FrontEnd/IscrAlbo" action="createDomandaPdf">
			<s:param name="urlPage">${currentPageUrl}</s:param>
			<s:param name="currentFrame">${param.currentFrame}</s:param>
		</s:url>
		
		<s:if test="%{!#session.dettIscrAlbo.aggiornamentoIscrizione}">
			<p class="instructions"><wp:i18n key="LABEL_DOCUMENTI_OBBLIGATORI_PER_PROCEDERE"/></p>
		</s:if>
		
		<s:if test="%{#documentiNonFirmati}">
			<p class="important not-signed-alert">
			<wp:i18n key="LABEL_MESSAGE_WARNING" />: <wp:i18n key="LABEL_FIRMA_DOCUMENTI_NON_VERIFICABILE_1" /> <img class="resize-svg-16" alt='<wp:i18n key="LABEL_DOCUMENTO_FIRMA_NON_VERIFICATA" />' src="<s:property value='%{#imgDocNonFirmato}'/>">, 
			<wp:i18n key="LABEL_FIRMA_DOCUMENTI_NON_VERIFICABILE_2" />
			</p>
		</s:if>

		<fieldset>
			<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_DOCUMENTS"/></legend>

			<wp:i18n key="TITLE_SCARICA_ALLEGATO" var="titleDownloadAllegato"/>
			<wp:i18n key="LABEL_SCARICA_ALLEGATO" var="valueDownloadAllegato"/>
			<wp:i18n key="TITLE_ELIMINA_ALLEGATO" var="titleEliminaAllegato"/>
			<wp:i18n key="LABEL_ELIMINA_ALLEGATO" var="valueEliminaAllegato"/>
			<wp:i18n key="TITLE_DOWNLOAD_FACSIMILE" var="titleDownloadFacsimile"/>
			<wp:i18n key="LABEL_DOWNLOAD_FACSIMILE" var="valueDownloadFacsimile"/>

			<table class="wizard-table" summary='<wp:i18n key="LABEL_ELENCO_DOCUMENTI"/>'>
				<tr>
					<th scope="col"><wp:i18n key="LABEL_DESCRIZIONE"/></th>
					<th scope="col"><wp:i18n key="LABEL_FACSIMILE"/></th>
					<th scope="col"><wp:i18n key="LABEL_ALLEGATO"/></th>
				</tr>
				<s:if test="%{documentiRichiesti.size() > 0}">

					<s:url id="urlDownloadDocRichiesto" namespace="/do/FrontEnd/IscrAlbo" action="downloadAllegatoRichiesto" />
					<s:url id="urlDownloadModello" namespace="/do/FrontEnd/DocDig" action="downloadDocumentoPubblico" />

					<s:iterator value="documentiRichiesti" var="documento" status="statDocRich">
					
						<s:set var="isDocFirmato" value='%{#documento.nomefilefacsimile.toUpperCase().endsWith(".P7M") ||
									     				   #documento.nomefilefacsimile.toUpperCase().endsWith(".TSD")}' />								     
						<tr>
							<td>
								<c:set var="docObbligatorio" value=""/>
								<s:if test="%{!#session.dettIscrAlbo.aggiornamentoIscrizione}" > 
									<c:set var="docObbligatorio"><s:property value='%{obbligatorio}'/></c:set>
								</s:if>
									
								<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/attachment_info.jsp" >
									<jsp:param name="formatoFile" value="${formato}" />
									<jsp:param name="nome" value="${nome}" />
									<jsp:param name="obbligatorio" value="${docObbligatorio}" />
								</jsp:include>
							</td>
							<td class="azioni">
								<ul>
									<li>
										<s:if test="%{#documento.idfacsimile != null}">
										
											<s:if test="%{isDocFirmato}">
												<%-- DOCUMENTO FIRMATO .P7M .TSD --%>
												<s:set var="urlDownload"><wp:action path="/ExtStr2/do/FrontEnd/DocDig/downloadDocumentoPubblico.action"/>&amp;id=<s:property value="%{#documento.idfacsimile}"/>&amp;${tokenHrefParams}</s:set>																												
											</s:if>
											<s:else>
												<%-- DOCUMENTO SENZA FIRMATO --%>
												<s:set var="urlDownload"><s:property value="%{#urlDownloadModello}"/>?id=<s:property value="%{#documento.idfacsimile}"/>&amp;${tokenHrefParams}</s:set>													
											</s:else>

											<c:choose>
												<c:when test="${skin == 'highcontrast' || skin == 'text'}">
													<s:a href="%{#urlDownload}" title="%{#attr.titleDownloadFacsimile}">%{#attr.valueDownloadFacsimile}</s:a>
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
									<s:if test="%{!#session.dettIscrAlbo.aggiornamentoIscrizione}">
										<s:set var="caricato" value="%{false}" />
										<s:iterator value="#helperDoc.docRichiestiId" var="idDoc" status="status">
											<s:if test="%{#idDoc == #documento.id}">
											
												<%-- notificata i doc senza firma non verificabili --%>	
												<s:set var="firmaWarning" 
														value='%{formato == DOCUMENTO_FORMATO_FIRMATO &&
																 !(#helperDoc.docRichiestiFileName.get(#status.index).toUpperCase().endsWith(".P7M") ||
														           #helperDoc.docRichiestiFileName.get(#status.index).toUpperCase().endsWith(".TSD"))}' />

												<s:if test='%{#firmaWarning}'><img class="resize-svg-16" title="<wp:i18n key='LABEL_DOCUMENTO_FIRMA_NON_VERIFICATA'/>" alt="<wp:i18n key='LABEL_DOCUMENTO_FIRMA_NON_VERIFICATA'/>" src="<s:property value='%{#imgDocNonFirmato}'/>"></s:if>
											
												<c:choose>
													<c:when test="${skin == 'highcontrast' || skin == 'text'}">
														<li>
															<s:a href="%{#urlDownloadDocRichiesto}?id=%{#status.index}&amp;%{#attr.tokenHrefParams}" title="%{#attr.titleDownloadAllegato}">%{#attr.valueDownloadAllegato}</s:a> (<s:property value="%{#helperDoc.docRichiestiSize.get(#status.index)}" /> KB)
														</li>
														<li>
															<a href="<wp:action path="/ExtStr2/do/FrontEnd/IscrAlbo/confirmDeleteAllegatoRichiesto.action"/>&amp;id=${status.index}&amp;ext=${param.ext}&amp;${tokenHrefParams}" title="${attr.titleEliminaAllegato}">
																${attr.valueEliminaAllegato}
															</a>
														</li>
													</c:when>
													<c:otherwise>
														<li <s:if test="%{#firmaWarning}">class="not-signed-alert"</s:if> >
															<s:a href="%{#urlDownloadDocRichiesto}?id=%{#status.index}&amp;%{#attr.tokenHrefParams}" title="%{#attr.titleDownloadAllegato}" cssClass="bkg download">
																<s:property value="%{#helperDoc.docRichiestiFileName.get(#status.index)}"/>
															</s:a> 
															(<s:property value="%{#helperDoc.docRichiestiSize.get(#status.index)}" /> KB)
														</li>
														<li>
															<a href="<wp:action path="/ExtStr2/do/FrontEnd/IscrAlbo/confirmDeleteAllegatoRichiesto.action"/>&amp;id=${status.index}&amp;ext=${param.ext}&amp;${tokenHrefParams}" title="${attr.titleEliminaAllegato}" class="bkg delete">
															</a>
														</li>
													</c:otherwise>
												</c:choose>
												<s:set var="caricato" value="%{true}" />
											</s:if>
										</s:iterator>
										<s:if test="%{!#caricato}">
											<li>
												<form action="<wp:action path="/ExtStr2/do/FrontEnd/IscrAlbo/processPageIscrAlboDocumenti.action" />" method="post"
															enctype="multipart/form-data" id="docRichiestoForm<s:property value="#statDocRich.index"/>">
													<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
													
													<div>
														<input type="hidden" name="ext" value="${param.ext}" />
														<input type="hidden" name="docRichiestoId" value="<s:property value="#documento.id"/>"/>
														<input type="hidden" name="formato" value="<s:property value="formato"/>" />
														<label class="noscreen" for="docRichiesto"><wp:i18n key='LABEL_FILE'/> : </label><input type="file" name="docRichiesto" id="docRichiesto" size="20" <s:if test="%{formato == DOCUMENTO_FORMATO_FIRMATO}">accept=".p7m,.tsd,.pdf,.xml"</s:if><s:elseif test="%{formato == DOCUMENTO_FORMATO_PDF}">accept=".pdf"</s:elseif><s:elseif test="%{formato == DOCUMENTO_FORMATO_EXCEL}">accept=".xls,.xlsx,.ods"</s:elseif> />&nbsp;
														<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_adddocrich.jsp" >
															<jsp:param value="${formato}" name="formato"/>
														</jsp:include>
													</div>
												</form>
											</li>
										</s:if>
									</s:if>
									<s:else>
										<%-- AGGIORNAMENTO ISCRIZIONE --%>
										<li></li>
									</s:else>
								</ul>
							</td>
						</tr>
					</s:iterator>
				</s:if>

				<s:url id="urlDownloadDocUlteriore" namespace="/do/FrontEnd/IscrAlbo" action="downloadAllegatoUlteriore" />

				<s:iterator value="#helperDoc.docUlterioriDesc" var="descDocUlteriore" status="status">
					<s:if test="%{! #helperDoc.docUlterioriNascosti.contains(#descDocUlteriore)}">
						<tr>
							<td>
								<s:property value="%{#descDocUlteriore}"/>
							</td>
							<td></td>
							<td class="azioni">
								<ul>
									<c:choose>
										<c:when test="${skin == 'highcontrast' || skin == 'text'}">
											<li>
												<s:a href="%{#urlDownloadDocUlteriore}?id=%{#status.index}&amp;%{#attr.tokenHrefParams}" title="%{#attr.titleDownloadAllegato}">%{#attr.valueDownloadAllegato}</s:a> (<s:property value="%{#helperDoc.docUlterioriSize.get(#status.index)}" /> KB)
											</li>
											<li>
												<a href="<wp:action path="/ExtStr2/do/FrontEnd/IscrAlbo/confirmDeleteAllegatoUlteriore.action"/>&amp;id=${status.index}&amp;ext=${param.ext}" title="${attr.titleEliminaAllegato}">${attr.valueEliminaAllegato}</a>
											</li>
										</c:when>
										<c:otherwise>
											<li>
												<s:a href="%{#urlDownloadDocUlteriore}?id=%{#status.index}&amp;%{#attr.tokenHrefParams}" title="%{#attr.titleDownloadAllegato}" cssClass="bkg download"><s:property value="%{#helperDoc.docUlterioriFileName.get(#status.index)}"/></s:a> (<s:property value="%{#helperDoc.docUlterioriSize.get(#status.index)}" /> KB)
											</li>
											<li>
												<a href="<wp:action path="/ExtStr2/do/FrontEnd/IscrAlbo/confirmDeleteAllegatoUlteriore.action"/>&amp;id=${status.index}&amp;ext=${param.ext}&amp;${tokenHrefParams}" title="${attr.titleDownloadAllegato}" class="bkg delete">
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
						<form action="<wp:action path="/ExtStr2/do/FrontEnd/IscrAlbo/processPageIscrAlboDocumenti.action" />" method="post" 
									enctype="multipart/form-data" id="docUlterioreForm">
							<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
							
							<div>
								<label class="noscreen" for="docUlterioreDesc"><wp:i18n key="LABEL_DOCUMENTO_DESCRIZIONE_ULTERIORE" /> : </label>
								<input type="text" name="docUlterioreDesc" id="docUlterioreDesc" class="long-text" placeholder="${descrizioneHint}"/>&nbsp;
								<label class="noscreen" for="docUlteriore"><wp:i18n key='LABEL_FILE'/> : </label><input type="file" name="docUlteriore" id="docUlteriore" />&nbsp;
								<input type="hidden" name="ext" value="${param.ext}" />
								<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_adddocult.jsp" />
							</div>
							<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/internalServlet/fileupload/fileupload.jsp" />
						</form>
					</td>
				</tr>
			</table>

			<s:set var="kbCaricati" value="%{dimensioneAttualeFileCaricati}"></s:set>
			<s:set var="kbDisponibili" value="%{limiteTotaleUploadDocIscrizione - dimensioneAttualeFileCaricati}"></s:set>
			<p>
				<wp:i18n key="LABEL_MAX_FILE_SIZE" /> <strong><s:property value="%{limiteUploadFile}" /></strong> KB.<br/>
				<wp:i18n key="LABEL_MAX_REQUEST_SIZE_1" /> <span class="important"><s:property value="%{#kbCaricati}" /></span> KB, <wp:i18n key="LABEL_MAX_REQUEST_SIZE_2" /> <strong><s:property value="%{#kbDisponibili}" /></strong> KB.
			</p>
		</fieldset>

		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/skip_main_content_from_buttons.jsp" />

		<div class="azioni">
			<form action="<wp:action path="/ExtStr2/do/FrontEnd/IscrAlbo/processPageIscrAlboDocumenti.action" />" method="post" 
					enctype="multipart/form-data" id="mainForm">
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
				
				<div>
					<input type="hidden" name="ext" value="${param.ext}" />
					<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_previous.jsp" />
					<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_next.jsp" />
					<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_cancel.jsp" />
				</div>
			</form>
		</div>

		<s:if test="%{!#session.dettIscrAlbo.aggiornamentoIscrizione}">
			<c:if test="${visGeneraDomandaPDF}">
				<script type="text/javascript">
					<!--//--><![CDATA[//><!--
						document.getElementById("createPdf").style.display = "inline";
						document.getElementById("linkCreatePdf").style.display = "none";
					//--><!]]>
				</script>
			</c:if>
		</s:if>
	</s:else>
</div>