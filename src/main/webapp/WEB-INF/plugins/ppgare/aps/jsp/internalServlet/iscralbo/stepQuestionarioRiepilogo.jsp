<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>
					
					
<s:if test="%{#session.dettIscrAlbo != null}">
	<s:set name="sessionIdObj" value="'dettIscrAlbo'" />
	<c:set var="nextAction" value="/ExtStr2/do/FrontEnd/IscrAlbo/endWizard.action" />
</s:if>
<s:else>
	<s:set name="sessionIdObj" value="'dettRinnAlbo'" />
	<c:set var="nextAction" value="/ExtStr2/do/FrontEnd/IscrAlbo/rinnovo.action" />
</s:else>

<s:set name="helper" value="%{#session[#sessionIdObj]}" />
<s:set name="documenti" value="%{#helper.documenti}" />


<s:if test="%{!#helper.rinnovoIscrizione}">
	<s:if test="%{!#helper.aggiornamentoIscrizione}">
		<c:set var="codiceBalloon" value="BALLOON_ISCR_ALBO_RIEPILOGO" />
	</s:if>
	<s:else>
		<c:set var="codiceBalloon" value="BALLOON_AGG_ISCR_ALBO_RIEPILOGO" />
	</s:else>
</s:if>
<s:else>
	<c:set var="codiceBalloon" value="BALLOON_RIN_ISCR_ALBO_RIEPILOGO" />
</s:else>

<s:if test="%{#helper.tipologia == 2}">
	<c:set var="titolo"><wp:i18n key='TITLE_PAGE_ISCRALBO_MERCATO_ELETTRONICO'/></c:set>
</s:if>
<s:else>
	<c:set var="titolo"><wp:i18n key='TITLE_PAGE_ISCRALBO_ELENCO_OE'/></c:set>
</s:else>
				

<%-- verifica se e' necessario visualizzare la notifica di firma non verificabile --%>
<s:set var="documentiNonFirmati" value="false"/>
<%--
<s:iterator value="#documenti.docRichiestiId" var="idDoc" status="status">
	<s:if test="%{#documenti.docRichiestiVisibile.get(#status.index)}">	
		<s:set var="firmaWarning" 
				value='%{formato == DOCUMENTO_FORMATO_FIRMATO &&
						 !(#documenti.docRichiestiFileName.get(#status.index).toUpperCase().endsWith(".P7M") ||
						   #documenti.docRichiestiFileName.get(#status.index).toUpperCase().endsWith(".TSD"))}' />
		<s:if test='%{#firmaWarning}'>
			<s:set var="documentiNonFirmati" value="true"/>
		</s:if>
	</s:if>
</s:iterator>
<s:iterator value="#documenti.docUlterioriDesc" var="descDocUlteriore" status="status">
	<s:if test="%{#documenti.docUlterioriVisibile.get(#status.index)}" >
		<s:set var="firmaWarning" 
				value='%{!(#documenti.docUlterioriFileName.get(#status.index).toUpperCase().endsWith(".P7M") ||
				           #documenti.docUlterioriFileName.get(#status.index).toUpperCase().endsWith(".TSD"))}' />
		<s:if test='%{#firmaWarning}'>
			<s:set var="documentiNonFirmati" value="true"/>
		</s:if>
	</s:if>
</s:iterator>
--%>

<c:set var="descrizioneHint"><wp:i18n key="LABEL_INSERIRE_DESCRIZIONE_PER_ALLEGARE_DOC" /></c:set>


<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/internalServlet/fileupload/uploadFile.jsp" />

<%-- prepara il BACK LINK della pagina di visualizzazione della firma digitale --%>
<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/backFromPageFirmaDigitale.jsp"/>

<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/send_blockUI.jsp" />

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

	<jsp:include page="stepsIscrizione.jsp">
		<jsp:param name="sessionIdObj" value="${sessionIdObj}" />
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="${codiceBalloon}"/>
	</jsp:include>

	<p class="noscreen">[ <a href="#" ><wp:i18n key="LABEL_VAI_A_LISTA_DOCUMENTI" /></a> ]</p>
	<p class="noscreen">[ <a href="#" ><wp:i18n key="LABEL_SALTA_DOCUMENTI_VAI_A_BOTTONI" /></a> ]</p>
	
	<s:set var="imgPdf"><wp:resourceURL/>static/img/pdf.svg</s:set>
	<s:set var="imgDocumentoFirmato"><wp:resourceURL/>static/img/smartcard.svg</s:set>
	<s:set var="imgExcel"><wp:resourceURL/>static/img/xls.svg</s:set>
	<s:set var="imgDocNonFirmato"><wp:resourceURL/>static/img/not-signed-alert.svg</s:set>	
	
	<s:if test="%{#documenti.datiModificati}">
		<div class="balloon">
			<div class="balloon-content balloon-alert">
				<wp:i18n key="LABEL_NO_MODIFICHE_AI_DOCUMENTI" />.
			</div>
		</div>
	</s:if>
	
	<p class="instructions"><wp:i18n key="LABEL_DOCUMENTI_OBBLIGATORI_PER_PROCEDERE"/></p>
	
	<s:if test="%{#documentiNonFirmati}">
		<p class="important not-signed-alert">
		<wp:i18n key="LABEL_MESSAGE_WARNING" />: <wp:i18n key="LABEL_FIRMA_DOCUMENTI_NON_VERIFICABILE_1" /> <img class="resize-svg-16" alt='<wp:i18n key="LABEL_DOCUMENTO_FIRMA_NON_VERIFICATA" />' src="<s:property value='%{#imgDocNonFirmato}'/>">, 
		<wp:i18n key="LABEL_FIRMA_DOCUMENTI_NON_VERIFICABILE_2" />
		</p>
	</s:if>
	
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
				<%-- calcolo dei kbCaricati --%>
				<s:set var="dimensioneAttualeFileCaricati" value="0" />
					<s:if test="%{documentiRichiesti.size() > 0}">
						<s:url id="urlDownloadDocRichiesto" namespace="/do/FrontEnd/IscrAlbo" action="downloadAllegatoRichiesto" />
						<s:url id="urlDownloadModello" namespace="/do/FrontEnd/DocDig" action="downloadDocumentoPubblico" />

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
											
												<%-- notificata i doc senza firma non verificabili 
												<s:set var="firmaWarning" 
														value='%{formato == DOCUMENTO_FORMATO_FIRMATO &&
																 !(#documenti.docRichiestiFileName.get(#statusCaricato.index).toUpperCase().endsWith(".P7M") ||
														           #documenti.docRichiestiFileName.get(#statusCaricato.index).toUpperCase().endsWith(".TSD"))}' />
												
												<s:if test='%{#firmaWarning}'>
													<img class="resize-svg-16" title="<wp:i18n key='LABEL_DOCUMENTO_FIRMA_NON_VERIFICATA' />" alt="<wp:i18n key='LABEL_DOCUMENTO_FIRMA_NON_VERIFICATA' />" src="<s:property value='%{#imgDocNonFirmato}'/>">
												</s:if>
												 --%>
												
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
														</li>
													</c:when>
													<c:otherwise>
													<%--<li <s:if test="%{#firmaWarning}">class="not-signed-alert"</s:if> >--%>
														<li>
															<s:a href="%{#urlDownloadDocRichiesto}?id=%{#statusCaricato.index}&amp;tipoBusta=%{#documenti.tipoBusta}&amp;codice=%{#documenti.codice}" 
																 title='%{#attr.titleDownloadAllegato}' cssClass="bkg download">
																<s:property value="%{#documenti.docRichiestiFileName.get(#statusCaricato.index)}"/>
															</s:a>
															<span>(<s:property value="%{#documenti.docRichiestiSize.get(#statusCaricato.index)}"/> KB)</span>
														</li>
														<li>
														</li>
													</c:otherwise>
												</c:choose>
												<s:set var="caricato" value="%{true}" />
												<s:set var="dimensioneAttualeFileCaricati" value="%{#dimensioneAttualeFileCaricati + #documenti.docRichiestiSize.get(#statusCaricato.index)}"></s:set>
											</s:if>
										</s:iterator>
										<s:if test="%{!#caricato}">
											<li>
											</li>
										</s:if>
									</ul>
								</td>
							</tr>
						</s:iterator>
					</s:if>

					<s:url id="urlDownloadDocUlteriore" namespace="/do/FrontEnd/IscrAlbo" action="downloadAllegatoUlteriore" />
					<s:iterator value="#documenti.docUlterioriDesc" var="descDocUlteriore" status="status">
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
										<%-- notificata i doc senza firma non verificabili 
										<s:set var="firmaWarning" 
												value='%{!(#documenti.docUlterioriFileName.get(#status.index).toUpperCase().endsWith(".P7M") ||
												           #documenti.docUlterioriFileName.get(#status.index).toUpperCase().endsWith(".TSD"))}' />
										
										<s:if test='%{#firmaWarning}'>
											<img class="resize-svg-16" title="<wp:i18n key='LABEL_DOCUMENTO_FIRMA_NON_VERIFICATA' />" alt="<wp:i18n key='LABEL_DOCUMENTO_FIRMA_NON_VERIFICATA' />" src="<s:property value='%{#imgDocNonFirmato}'/>">
										</s:if>
										 --%>
										 
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
												</li>
											</c:otherwise>
										</c:choose>
										<s:set var="dimensioneAttualeFileCaricati" value="%{#dimensioneAttualeFileCaricati + #documenti.docUlterioriSize.get(#status.index)}"></s:set>
									</ul>
								</td>
							</tr>
						</s:if>
					</s:iterator>

					<tr>
						<td colspan="3">
							<%-- Salta il form di inserimento di un documento ulteriore e vai ai bottoni --%>
							<p class="noscreen">[ <a href="#" id="insDoc" ><wp:i18n key="LABEL_COMUNICAZIONI_SALTA_FORM_VAI_BOTTONI" /></a> ]</p>							
						</td>
					</tr>
				</tbody>
			</table>
		</div>
		<%-- <s:set var="kbCaricati" value="%{dimensioneAttualeFileCaricati}"></s:set> --%>
		<s:set var="kbDisponibili" value="%{limiteUploadFile - #dimensioneAttualeFileCaricati}"></s:set>
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/internalServlet/fileupload/fileupload.jsp" />
		<p>
			<wp:i18n key="LABEL_MAX_FILE_SIZE" /> <strong><s:property value="%{limiteUploadFile}" /></strong> KB.<br/>
			<wp:i18n key="LABEL_MAX_REQUEST_SIZE_1" /> <strong><s:property value="%{#dimensioneAttualeFileCaricati}" /></strong> KB, <wp:i18n key="LABEL_MAX_REQUEST_SIZE_2" /> 
			<strong><s:property value="%{#kbDisponibili}" /></strong> KB.
		</p>
	</fieldset>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/skip_main_content_from_buttons.jsp" />

	<form action="<wp:action path="/ExtStr2/do/FrontEnd/IscrAlbo/processQCQuestionarioRiepilogo.action" />" method="post" >
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
		
		<div class="azioni">
			<input type="hidden" name="page" value="openQCSummary"/>
			<input type="hidden" name="ext" value="${param.ext}" />
			<s:hidden name="codice" value="%{#helper.idBando}" />
			<c:if test="${ !rettifica }">
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_previous.jsp" />
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_next.jsp" />				
			</c:if>
			<input type="hidden" name="fromQFormChanged" value="${rettifica}" />			
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_cancel.jsp" />
		</div>
	</form>

</div>