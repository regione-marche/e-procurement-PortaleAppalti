<%@ taglib prefix="wp" uri="aps-core.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<c:set var="descrizioneHint"><wp:i18n key="LABEL_INSERIRE_DESCRIZIONE_PER_ALLEGARE_DOC" /></c:set>
<s:set var="helper" value="%{#session['nuovaComunicazione']}"/>
<s:set var="helperDocumenti" value="%{#session['nuovaComunicazione'].documenti}"/>

<s:if test="%{deleteAllegato}">
	<c:set var="hrefDeleteAllegato" value="/ExtStr2/do/FrontEnd/Comunicazioni/deleteAllegatoSoccorso.action" />	
	<c:set var="hrefCancelDeleteAllegato" value="/ExtStr2/do/FrontEnd/Comunicazioni/cancelDeleteAllegatoSoccorso.action" />
	<c:set var="hrefConfirmDeleteAllegato" value="/ExtStr2/do/FrontEnd/Comunicazioni/confirmDeleteAllegato.action" />
</s:if>
<s:if test="%{deleteAllegatoRichiesto}">
	<c:set var="hrefDeleteAllegato" value="/ExtStr2/do/FrontEnd/Comunicazioni/deleteAllegatoSoccorsoRichiesto.action" />		
	<c:set var="hrefCancelDeleteAllegato" value="/ExtStr2/do/FrontEnd/Comunicazioni/cancelDeleteAllegatoSoccorsoRichiesto.action" />
	<c:set var="hrefConfirmDeleteAllegato" value="/ExtStr2/do/FrontEnd/Comunicazioni/confirmDeleteAllegatoRichiesto.action" />
</s:if>
	

<s:if test="%{deleteAllegato || deleteAllegatoRichiesto}">
	<%-- ELIMINA DOCUMENTO ULTERIORE/RICHIESTO DEL SOCCORSO ISTRUTTORIO --%>
	<p class="question">
		<s:if test="%{deleteAllegato}">
			<wp:i18n key="LABEL_ELIMINA_DOCUMENTO" /> "<s:property value="%{#helperDocumenti.docUlterioriDesc.get(id)}" />" 
			(file <s:property value="%{#helperDocumenti.docUlterioriFileName.get(id)}" />)?
		</s:if>
		<s:if test="%{deleteAllegatoRichiesto}">
			<wp:i18n key="LABEL_ELIMINA_DOCUMENTO" /> "<s:property value="%{#helperDocumenti.docRichiestiId.get(id)}" />" 
			(file <s:property value="%{#helperDocumenti.docRichiestiFileName.get(id)}" />)?
		</s:if> 
	</p>
	<div class="azioni">
		<wp:i18n key="LABEL_YES" var="valueYesButton" />
		<wp:i18n key="TITLE_CONFIRM_DEL_YES" var="titleYesButton" />
		<wp:i18n key="LABEL_NO" var="valueNoButton" />
		<wp:i18n key="TITLE_CONFIRM_DEL_NO" var="titleNoButton" />
		
		<form action="<wp:action path="${hrefDeleteAllegato}" />" method="post" class="azione">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			<div>
				<s:hidden name="id" value="%{id}" id="idDelete" />
				<input type="hidden" name="ext" value="${param.ext}" />
				<s:submit value="%{#attr.valueYesButton}" title="%{#attr.titleYesButton}" cssClass="button block-ui"></s:submit>
			</div>
		</form>
		
		<form action="<wp:action path="${hrefCancelDeleteAllegato}" />"  method="post" class="azione">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			<div>
				<input type="hidden" name="ext" value="${param.ext}" />					
				<s:submit value="%{#attr.valueNoButton}" title="%{#attr.titleNoButton}" cssClass="button block-ui"></s:submit>
			</div>
		</form>
	</div>
</s:if>
<s:else> 
	<!-- ALLEGATI -->
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/skip_form_buttons.jsp" />

	<fieldset>
		<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_DOCUMENTI_ALLEGATI" /></legend>
		
		<wp:i18n key="TITLE_SCARICA_ALLEGATO" var="titleScaricaAllegato" />
		<wp:i18n key="TITLE_ELIMINA_ALLEGATO" var="titleEliminaAllegato" />
		
		<div class="table-container">
			<table id="tableDocumenti" class="wizard-table" summary="Elenco dei documenti allegati">
				<thead>
					<tr>
						<th scope="col"><wp:i18n key="LABEL_DESCRIZIONE"/></th>
						<%--
						<th scope="col"><wp:i18n key="LABEL_FACSIMILE"/></th>
						 --%>
						<th scope="col"><wp:i18n key="LABEL_ALLEGATO"/></th>
						
					</tr>
				</thead>
				<tbody>
					<%-- DOCUMENTI RICHIESTI - SOCCORSO ISTRUTTORIO --%>
					<s:url id="urlDownloadModello" namespace="/do/FrontEnd/DocDig" action="downloadDocumentoPubblico" />							
					<s:url id="urlDownloadDocRichiesto" namespace="/do/FrontEnd/Comunicazioni" action="downloadAllegatoSoccorsoRichiesto" />
			
					<s:iterator value="documentiRichiesti" var="documento" status="statDocRich">
	
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
							
<%--
							<td class="azioni">
								<ul>
									<li>
										<s:if test="%{#documento.idfacsimile != null}">
										
											<s:if test="%{isDocFirmato}">
												<!-- DOCUMENTO FIRMATO .P7M .TSD -->
												<s:set var="urlDownload"><wp:action path="/ExtStr2/do/FrontEnd/DocDig/downloadDocumentoPubblico.action"/>&amp;id=<s:property value="%{#documento.idfacsimile}"/></s:set>																												
											</s:if>
											<s:else>
												<!-- DOCUMENTO SENZA FIRMATO -->
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
--%>
							
							<td class="azioni">
								<ul>
									<s:set var="caricato" value="%{false}" />
									<s:iterator value="#helperDocumenti.docRichiestiId" var="idDoc" status="statusCaricato">
										<s:if test="%{#idDoc == #documento.id}">
										
											<%-- notificata i doc senza firma non verificabili --%>	
											<s:set var="firmaWarning" 
													value='%{formato == DOCUMENTO_FORMATO_FIRMATO &&
															 !(#helperDocumenti.docRichiestiFileName.get(#statusCaricato.index).toUpperCase().endsWith(".P7M") ||
													           #helperDocumenti.docRichiestiFileName.get(#statusCaricato.index).toUpperCase().endsWith(".TSD"))}' />

											<s:if test='%{#firmaWarning}'>
												<img class="resize-svg-16" title="<wp:i18n key='LABEL_DOCUMENTO_FIRMA_NON_VERIFICATA' />" alt="<wp:i18n key='LABEL_DOCUMENTO_FIRMA_NON_VERIFICATA' />" src="<s:property value='%{#imgDocNonFirmato}'/>">
											</s:if>
											
											<c:choose>
												<c:when test="${skin == 'highcontrast' || skin == 'text'}">
													<li>
														<s:a href="%{#urlDownloadDocRichiesto}?id=%{#statusCaricato.index}&amp;codice=%{codice}" 
															 title="%{#attr.titleDownloadAllegato}">
															<wp:i18n key="LABEL_SCARICA_ALLEGATO" />
														</s:a>
														<span>(<s:property value="%{#helperDocumenti.docRichiestiSize.get(#statusCaricato.index)}"/> KB)</span>
													</li>
													<li>
														<a href='<wp:action path="/ExtStr2/do/FrontEnd/Comunicazioni/confirmDeleteAllegatoSoccorsoRichiesto.action"/>&amp;id=${statusCaricato.index}&amp;ext=${param.ext}&amp;codice=<s:property value="%{codice}"/>&amp;${tokenHrefParams}' 
														   title='<s:property value="%{#attr.titleEliminaAllegato}"/>' class='bkg delete'>
															<wp:i18n key="LABEL_ELIMINA_ALLEGATO" />
														</a>
													</li>
												</c:when>
												<c:otherwise>
													<li <s:if test="%{#firmaWarning}">class="not-signed-alert"</s:if> >
														<s:a href="%{#urlDownloadDocRichiesto}?id=%{#statusCaricato.index}&amp;codice=%{codice}" 
															 title='%{#attr.titleDownloadAllegato}' cssClass="bkg download">
															<s:property value="%{#helperDocumenti.docRichiestiFileName.get(#statusCaricato.index)}"/>
														</s:a>
														<span>(<s:property value="%{#helperDocumenti.docRichiestiSize.get(#statusCaricato.index)}"/> KB)</span>
													</li>
													<li>
														<a href='<wp:action path="/ExtStr2/do/FrontEnd/Comunicazioni/confirmDeleteAllegatoSoccorsoRichiesto.action"/>&amp;id=${statusCaricato.index}&amp;ext=${param.ext}&amp;codice=<s:property value="%{codice}"/>&amp;${tokenHrefParams}' 
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
											<form action="<wp:action path="/ExtStr2/do/FrontEnd/Comunicazioni/aggiungiAllegatoSoccorsoRichiesto.action" />" method="post" 
														enctype="multipart/form-data" id="docRichiestoForm<s:property value="#statDocRich.index"/>">
												<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
												
												<div>
													<input type="hidden" name="ext" value="${param.ext}" />
													<input type="hidden" name="docRichiestoId" value="<s:property value="%{#documento.id}"/>"/>
													<input type="hidden" name="formato" value="<s:property value="%{formato}"/>" />															
													<input type="hidden" name="codice" value="<s:property value="%{codice}"/>"/>
													
													<label class="noscreen" for="docRichiesto"><wp:i18n key='LABEL_FILE'/> : </label>
													<input type="file" name="docRichiesto" id="docRichiesto" size="20" 
														aria-label="${nome}" 
														<s:if test="%{formato == DOCUMENTO_FORMATO_FIRMATO}">accept=".p7m,.tsd,.pdf,.xml"</s:if>
														<s:elseif test="%{formato == DOCUMENTO_FORMATO_PDF}">accept=".pdf"</s:elseif>
														<s:elseif test="%{formato == DOCUMENTO_FORMATO_EXCEL}">accept=".xls,.xlsx,.ods"</s:elseif> />&nbsp;
													
													<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_adddocrich.jsp" >
														<jsp:param name="formato" value="${formato}" />
													</jsp:include>
												</div>
											</form>
										</li>
									</s:if>
								</ul>
							</td>
						</tr>
					</s:iterator>


					<%-- DOCUMENTI ULTERIORI - SOCCORSO ISTRUTTORIO --%>
					<s:url id="urlDownload" namespace="/do/FrontEnd/Comunicazioni" action="downloadAllegatoSoccorso" />

					<s:iterator value="%{#helperDocumenti.docUlterioriDesc}" var="descDocUlteriore" status="status">
						<tr>
							<td>
								<s:property value="%{#descDocUlteriore}"/>
							</td>

<%--							
							<td></td>
--%>
							 
							<td class="azioni">
								<ul>
									<c:choose>
										<c:when test="${skin == 'highcontrast' || skin == 'text'}">
											<li>
												<s:a href="%{#urlDownload}?id=%{#status.index}&amp;codice=%{codice}&amp;%{#attr.tokenHrefParams}" 
													 title="%{#attr.titleScaricaAllegato}">
														<wp:i18n key="LABEL_SCARICA_ALLEGATO"/>
												</s:a> 
													<span>(<s:property value="%{#helperDocumenti.docUlterioriSize.get(#status.index)}" /> KB)</span> 
											</li>
											<li>
												<a href='<wp:action path="/ExtStr2/do/FrontEnd/Comunicazioni/confirmDeleteAllegatoSoccorso.action"/>&amp;id=${status.index}&amp;ext=${param.ext}&amp;${tokenHrefParams}' 
												   title="${attr.titleEliminaAllegato}"> 
													 <wp:i18n key="LABEL_ELIMINA_ALLEGATO"/>
												</a>
											</li>
										</c:when>
										<c:otherwise>
											<li>
												<s:a href="%{#urlDownload}?id=%{#status.index}&amp;codice=%{codice}&amp;%{#attr.tokenHrefParams}" 
													 title="%{#attr.titleScaricaAllegato}" cssClass="bkg download">
														<s:property value="%{#helperDocumenti.docUlterioriFileName.get(#status.index)}"/> 
												</s:a>  
												<span>(<s:property value="%{#helperDocumenti.docUlterioriSize.get(#status.index)}" /> KB)</span>  
											</li>
											<li>
												<a href='<wp:action path="/ExtStr2/do/FrontEnd/Comunicazioni/confirmDeleteAllegatoSoccorso.action"/>&amp;id=${status.index}&amp;ext=${param.ext}&amp;${tokenHrefParams}' 
												   title="${attr.titleEliminaAllegato}" class="bkg delete">
												</a>
											</li>
										</c:otherwise>
									</c:choose>
								</ul>
							</td>
						</tr>
					</s:iterator>

					<tr>
						<td colspan="3">
							<p class="noscreen">[ <a href="#" id="insDoc" ><wp:i18n key="LABEL_COMUNICAZIONI_SALTA_FORM_VAI_BOTTONI" /></a> ]</p>
							<form action="<wp:action path="/ExtStr2/do/FrontEnd/Comunicazioni/aggiungiAllegatoSoccorso.action" />"
									method="post" enctype="multipart/form-data" id="docUlterioreForm">
								<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
								<div>
									<label class="noscreen" for="docUlterioreDesc"><wp:i18n key="LABEL_COMUNICAZIONI_DESCRIZIONE" /> : </label><input type="text" name="docUlterioreDesc" id="docUlterioreDesc" class="long-text" placeholder="${descrizioneHint}"/>&nbsp;
									<label class="noscreen" for="docUlteriore"><wp:i18n key="LABEL_FILE" /> : </label><input type="file" name="docUlteriore" id="docUlteriore" />&nbsp; 
									<input type="hidden" name="ext" value="${param.ext}" />
									<input type="hidden" name="codice" value="<s:property value="%{codice}"/>"/>
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
		<s:set var="kbDisponibili" value="%{limiteTotaleUpload - dimensioneAttualeFileCaricati}"></s:set>
		<p>
			<wp:i18n key="LABEL_MAX_FILE_SIZE" /> <strong><s:property value="%{limiteUploadFile}" /></strong> KB.<br/>
			<wp:i18n key="LABEL_MAX_REQUEST_SIZE_1" /> <strong><s:property value="%{#kbCaricati}" /></strong> KB, <wp:i18n key="LABEL_MAX_REQUEST_SIZE_2" /> 
			<strong><s:property value="%{#kbDisponibili}" /></strong> KB.
		</p>
	</fieldset>
</s:else>