<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>

<s:set var="buste" value="%{#session.dettaglioOffertaGara}"/>
<s:set var="bustaPartecipazione" value="%{#buste.bustaPartecipazione}"/>
<s:set var="partecipazione" value="%{#bustaPartecipazione.helper}"/>
<s:set var="codiceTitolo" value="%{#bustaPartecipazione.codiceGara}"/>
<!-- OBSOLETO <s:set var="riepilogoBuste" value="%{#session.riepilogoBuste}" /> -->
<s:set var="riepilogoBuste" value="%{#buste.bustaRiepilogo.helper}" />

<c:set var="count" value="0" />


<s:if test="%{tipoBusta == BUSTA_AMMINISTRATIVA}">
	<s:set var="documenti" value="%{#buste.bustaAmministrativa.helperDocumenti}"/>
	<c:set var="codiceBalloon" value="BALLOON_BUSTA_AMMINISTRATIVA"/>
	<c:set var="titolo"><wp:i18n key='TITLE_PAGE_GARETEL_DOCUMENTI_BUSTA_AMMINISTRATIVA'/> [${codiceTitolo}]</c:set>
</s:if>
<s:if test="%{tipoBusta == BUSTA_TECNICA}">
	<s:set var="documenti" value="%{#buste.bustaTecnica.helperDocumenti}"/>
	<c:set var="codiceBalloon" value="BALLOON_BUSTA_TECNICA"/>
	<c:set var="titolo"><wp:i18n key='TITLE_PAGE_GARETEL_DOCUMENTI_BUSTA_TECNICA'/> [${codice}]</c:set>
</s:if>
<s:if test="%{tipoBusta == BUSTA_ECONOMICA}">
	<s:set var="documenti" value="%{#buste.bustaEconomica.helperDocumenti}"/>
	<c:set var="codiceBalloon" value="BALLOON_BUSTA_ECONOMICA"/>
	<c:set var="titolo"><wp:i18n key='TITLE_PAGE_GARETEL_DOCUMENTI_BUSTA_ECONOMICA'/> [${codice}]</c:set>
</s:if>
<s:if test="%{tipoBusta == BUSTA_PRE_QUALIFICA}">
	<s:set var="documenti" value="%{#buste.bustaPrequalifica.helperDocumenti}"/>
	<c:set var="codiceBalloon" value="BALLOON_BUSTA_PREQUALIFICA"/>
	<c:set var="titolo"><wp:i18n key='TITLE_PAGE_GARETEL_DOCUMENTI_BUSTA_PREQUALIFICA'/> [${codiceTitolo}]</c:set>
</s:if>

<c:set var="descrizioneHint"><wp:i18n key="LABEL_INSERIRE_DESCRIZIONE_PER_ALLEGARE_DOC" /></c:set>

<%--
codice=<s:property value="%{codice}"/><br/>
codiceGara=<s:property value="%{codiceGara}"/><br/>
documenti.codice=<s:property value="%{#documenti.codice}"/><br/>
documenti.codiceGara=<s:property value="%{#documenti.codiceGara}"/><br/>
riepilogoBuste.codiceGara=<s:property value="%{#riepilogoBuste.codiceGara}"/><br/>
--%>


<%-- <script type="text/javascript" src='<wp:resourceURL/>static/js/ppgare/uploadFile.js'></script> --%>
<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/internalServlet/fileupload/uploadFile.jsp" />

<%-- prepara il BACK LINK della pagina di visualizzazione della firma digitale --%>
<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/backFromPageFirmaDigitale.jsp"/>

<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/send_blockUI.jsp" />

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2><c:out value="${titolo}"/> </h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/field_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_messages.jsp" />

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="${codiceBalloon}"/>
	</jsp:include>

	<p class="noscreen">[ <a href="#" ><wp:i18n key="LABEL_VAI_A_LISTA_DOCUMENTI" /></a> ]</p>
	<p class="noscreen">[ <a href="#" ><wp:i18n key="LABEL_SALTA_DOCUMENTI_VAI_A_BOTTONI" /></a> ]</p>

	<s:if test="%{deleteDocRichiesto}">
		<p class="question">
			<s:set var="nomeDocRichiesto" value="" />
			<s:set var="docId" value="%{#documenti.docRichiestiId.get(id)}"/>
			<s:iterator value="documentiRichiesti" var="documento" status="statDocRich">
				<c:if test="${docId == documento.id}">
					<s:set var="nomeDocRichiesto" value="%{nome}" />
				</c:if>
			</s:iterator>
			<wp:i18n key="LABEL_ELIMINA_DOCUMENTO" /> "<s:property value="%{#nomeDocRichiesto}"/>"
			(<wp:i18n key="LABEL_FILE" /> <s:property value="%{#documenti.docRichiestiFileName.get(id)}"/>) ?
		</p>
		<div class="azioni">
			<form action="<wp:action path="/ExtStr2/do/FrontEnd/GareTel/deleteAllegatoRichiesto.action" />" method="post" class="azione">
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />

				<div>
					<input type="hidden" name="ext" value="${param.ext}" />
					<s:hidden name="id" value="%{id}" id="idDelete"/>
					<s:hidden name="tipoBusta" value="%{#documenti.tipoBusta}" />
					<s:hidden name="operazione" value="%{#documenti.operazione}" />

					<s:if test="%{!partecipazione.plicoUnicoOfferteDistinte}">
						<!-- NO LOTTI DISTINTI -->
						<input type="hidden" name="codice" value="<s:property value="%{#documenti.codice}"/>"/>
					</s:if>
					<s:else>
						<!-- LOTTI DISTINTI -->
						<s:hidden name="codice" value="%{#documenti.codice}" />
						<s:hidden name="codiceGara" value="%{#riepilogoBuste.codiceGara}" />
					</s:else>

					<wp:i18n key="LABEL_YES" var="valueButtonYes" />
					<wp:i18n key="TITLE_CONFIRM_DEL_YES" var="titleButtonYes" />
					<s:submit value="%{#attr.valueButtonYes}" title="%{#attr.titleButtonYes}" class="button block-ui"></s:submit>
				</div>
			</form>
			<form action="<wp:action path="/ExtStr2/do/FrontEnd/GareTel/cancelDeleteAllegatoRichiesto.action" />" method="post" class="azione">
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />

				<div>
					<input type="hidden" name="ext" value="${param.ext}" />
					<s:hidden name="tipoBusta" value="%{#documenti.tipoBusta}" />
					<s:hidden name="operazione" value="%{#documenti.operazione}" />

					<s:if test="%{!partecipazione.plicoUnicoOfferteDistinte}">
						<!-- NO LOTTI DISTINTI -->
						<input type="hidden" name="codice" value="<s:property value="%{#documenti.codice}"/>"/>
					</s:if>
					<s:else>
						<!-- LOTTI DISTINTI -->
						<s:hidden name="codice" value="%{#documenti.codice}" />
						<s:hidden name="codiceGara" value="%{#riepilogoBuste.codiceGara}" />
					</s:else>

					<wp:i18n key="LABEL_NO" var="valueButtonNo" />
					<wp:i18n key="TITLE_CONFIRM_DEL_NO" var="titleButtonNo" />
					<s:submit value="%{#attr.valueButtonNo}" title="%{#attr.titleButtonNo}" class="button block-ui"></s:submit>
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
			<form action="<wp:action path="/ExtStr2/do/FrontEnd/GareTel/deleteAllegatoUlteriore.action" />" method="post" class="azione">
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />

				<div>
					<input type="hidden" name="ext" value="${param.ext}" />
					<s:hidden name="id" value="%{id}" id="idDelete"/>
					<s:hidden name="tipoBusta" value="%{#documenti.tipoBusta}" />
					<s:hidden name="operazione" value="%{#documenti.operazione}" />

					<s:if test="%{!partecipazione.plicoUnicoOfferteDistinte}">
						<!-- NO LOTTI DISTINTI -->
						<input type="hidden" name="codice" value="<s:property value="%{#documenti.codice}"/>"/>
					</s:if>
					<s:else>
						<!-- LOTTI DISTINTI -->
						<s:hidden name="codice" value="%{#documenti.codice}" />
						<s:hidden name="codiceGara" value="%{#riepilogoBuste.codiceGara}" />
					</s:else>

					<wp:i18n key="LABEL_YES" var="valueButtonYes" />
					<wp:i18n key="TITLE_CONFIRM_DEL_YES" var="titleButtonYes" />
					<s:submit value="%{#attr.valueButtonYes}" title="%{#attr.titleButtonYes}" class="button block-ui"></s:submit>
				</div>
			</form>
			<form action="<wp:action path="/ExtStr2/do/FrontEnd/GareTel/cancelDeleteAllegatoUlteriore.action" />" method="post" class="azione">
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />

				<div>
					<input type="hidden" name="ext" value="${param.ext}" />
					<s:hidden name="tipoBusta" value="%{#documenti.tipoBusta}" />
					<s:hidden name="operazione" value="%{#documenti.operazione}" />

					<s:if test="%{!partecipazione.plicoUnicoOfferteDistinte}">
						<!-- NO LOTTI DISTINTI -->
						<input type="hidden" name="codice" value="<s:property value="%{#documenti.codice}"/>"/>
					</s:if>
					<s:else>
						<!-- LOTTI DISTINTI -->
						<s:hidden name="codice" value="%{#documenti.codice}" />
						<s:hidden name="codiceGara" value="%{#riepilogoBuste.codiceGara}" />
					</s:else>

					<wp:i18n key="LABEL_NO" var="valueButtonNo" />
					<wp:i18n key="TITLE_CONFIRM_DEL_NO" var="titleButtonNo" />
					<s:submit value="%{#attr.valueButtonNo}" title="%{#attr.titleButtonNo}" class="button block-ui"></s:submit>
				</div>
			</form>
		</div>
	</s:elseif>
	<s:else>
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

		<s:if test="%{iddocdig != null && '-1' != iddocdig}">

			<fieldset>
			<s:url action="redirectToDGUE" var="urlRedirect"></s:url>
			    <legend><wp:i18n key="LABEL_GENERATE_DGUE" /></legend>
			    <div>
			        <img class="dgue-info-image" style="display: inline;" aria-label="M-DGUE"/>
                    <h3 style="display: inline;">M-DGUE</h3>
                </div>
                <div class="dgue-info-content">
                    <wp:i18n key="LABEL_DGUE_LINK_FIRST" /> <a href='<s:property value="%{#urlRedirect}" />?codice=<s:property value="%{codice}"/>&amp;codiceGara=<s:property value="%{#riepilogoBuste.codiceGara}"/>&amp;iddocdig=<s:property value="%{iddocdig}"/>' target="blank"><wp:i18n key="LABEL_DGUE_LINK_CLICK" /></a>
                    <br>
                    <wp:i18n key="LABEL_DGUE_LINK_SECOND" /> <s:property value="%{dgueLinkSecond}" escape="false"/>
                    <br>
                    <wp:i18n key="LABEL_DGUE_LINK_THIRD" /> <s:property value="%{dgueLinkThird}" escape="false"/>
                </div>
			</fieldset>
		</s:if>

		<s:if test="%{esisteFileConFirmaNonVerificata}">
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
				<caption style = "display:none;"><wp:i18n key="LABEL_ELENCO_DOCUMENTI"/></caption>
					<thead>
						<tr>
							<th scope="col"><wp:i18n key="LABEL_DESCRIZIONE"/></th>
							<th scope="col"><wp:i18n key="LABEL_FACSIMILE"/></th>
							<th scope="col"><wp:i18n key="LABEL_ALLEGATO"/></th>
						</tr>
					</thead>
					<tbody>
						<s:if test="%{documentiRichiesti.size() > 0}">
							<s:url id="urlDownloadDocRichiesto" namespace="/do/FrontEnd/GareTel" action="downloadAllegatoRichiesto" />
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
															<s:a href="%{#urlDownload}" title="%{#attr.titleDownloadFacsimile}" class="bkg download"><wp:i18n key="LABEL_DOWNLOAD_FACSIMILE" /></s:a>
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
																<s:if test="%{!#partecipazione.plicoUnicoOfferteDistinte}">
																	<a href='<wp:action path="/ExtStr2/do/FrontEnd/GareTel/confirmDeleteAllegatoRichiesto.action"/>&amp;id=${statusCaricato.index}&amp;ext=${param.ext}&amp;tipoBusta=<s:property value="%{#documenti.tipoBusta}"/>&amp;codice=<s:property value="%{#documenti.codice}"/>'
																	   title='<s:property value="%{#attr.titleEliminaAllegato}"/>' class='bkg delete'>
																		<wp:i18n key="LABEL_ELIMINA_ALLEGATO" />
																	</a>
																</s:if>
																<s:else>
																	<a href='<wp:action path="/ExtStr2/do/FrontEnd/GareTel/confirmDeleteAllegatoRichiesto.action"/>&amp;id=${statusCaricato.index}&amp;ext=${param.ext}&amp;tipoBusta=<s:property value="%{#documenti.tipoBusta}"/>&amp;codice=<s:property value="%{codice}"/>&amp;codiceGara=<s:property value="%{#riepilogoBuste.codiceGara}"/>'
																	   title='<s:property value="%{#attr.titleEliminaAllegato}"/>' class='bkg delete'>
																		<wp:i18n key="LABEL_ELIMINA_ALLEGATO" />
																	</a>
																</s:else>
															</li>
														</c:when>
														<c:otherwise>
															<li <s:if test="%{#firmaWarning}">class="not-signed-alert"</s:if> >
																<s:a href="%{#urlDownloadDocRichiesto}?id=%{#statusCaricato.index}&amp;tipoBusta=%{#documenti.tipoBusta}&amp;codice=%{#documenti.codice}"
																	 title='%{#attr.titleDownloadAllegato}' class="bkg download">
																	<s:property value="%{#documenti.docRichiestiFileName.get(#statusCaricato.index)}"/>
																</s:a>
																<span>(<s:property value="%{#documenti.docRichiestiSize.get(#statusCaricato.index)}"/> KB)</span>
															</li>
															<li>
																<s:if test="%{!#partecipazione.plicoUnicoOfferteDistinte}">
																	<a href='<wp:action path="/ExtStr2/do/FrontEnd/GareTel/confirmDeleteAllegatoRichiesto.action"/>&amp;id=${statusCaricato.index}&amp;ext=${param.ext}&amp;tipoBusta=<s:property value="%{#documenti.tipoBusta}"/>&amp;codice=<s:property value="%{#documenti.codice}"/>'
																	   title='<s:property value="%{#attr.titleEliminaAllegato}"/>' class="bkg delete">
																	</a>
																</s:if>
																<s:else>
																	<a href='<wp:action path="/ExtStr2/do/FrontEnd/GareTel/confirmDeleteAllegatoRichiesto.action"/>&amp;id=${statusCaricato.index}&amp;ext=${param.ext}&amp;tipoBusta=<s:property value="%{#documenti.tipoBusta}"/>&amp;codice=<s:property value="%{codice}"/>&amp;codiceGara=<s:property value="%{#riepilogoBuste.codiceGara}"/>'
																	   title='<s:property value="%{#attr.titleEliminaAllegato}"/>' class="bkg delete">
																	</a>
																</s:else>
															</li>
														</c:otherwise>
													</c:choose>
													<s:set var="caricato" value="%{true}" />
												</s:if>
											</s:iterator>
											<s:if test="%{!#caricato}">
												<c:set var="count" value="${count + 1}" scope="page"/>
												<s:set var="countStruts">${count}</s:set>
												<li>
													<form action="<wp:action path="/ExtStr2/do/FrontEnd/GareTel/processPageDocumenti.action" />" method="post"
																enctype="multipart/form-data" id="docRichiestoForm<s:property value="#statDocRich.index"/>">
														<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />

														<div>
															<input type="hidden" id="ext_${count}" name="ext" value="${param.ext}" />
															<input type="hidden" id="tipoBusta_${count}" name="tipoBusta" value="<s:property value="%{#documenti.tipoBusta}"/>"/>
															<s:hidden id="operazione_%{#countStruts}" name="operazione" value="%{#documenti.operazione}" />
															<input type="hidden" id="docRichiestoId_${count}" name="docRichiestoId" value="<s:property value="%{#documento.id}"/>"/>
															<input type="hidden" id="formato_${count}" name="formato" value="<s:property value="%{formato}"/>" />

															<s:if test="%{!partecipazione.plicoUnicoOfferteDistinte}">
																<!-- NO LOTTI DISTINTI -->
																<input type="hidden" id="codice_${count}" name="codice" value="<s:property value="%{#documenti.codice}"/>"/>
															</s:if>
															<s:else>
																<!-- LOTTI DISTINTI -->
																<s:hidden id="codice_%{#countStruts}" name="codice" value="%{#documenti.codice}" />
																<s:hidden id="codiceGara_%{#countStruts}" name="codiceGara" value="%{#session.riepilogoBuste.codiceGara}" />
															</s:else>

															<label class="noscreen" for="docRichiesto_${count}"><wp:i18n key='LABEL_FILE'/> : </label>
																<input type="file" name="docRichiesto" id="docRichiesto_${count}" size="20"
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
						</s:if>

						<s:url id="urlDownloadDocUlteriore" namespace="/do/FrontEnd/GareTel" action="downloadAllegatoUlteriore" />
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
										 	<s:set var="firmaWarning" value="%{false}" />
											<s:if test="%{#documenti.docUlterioriFirmaBean.get(#status.index)!=null}">
												<s:set var="firmaWarning" value="%{!(#documenti.docUlterioriFirmaBean.get(#status.index).firmacheck)}" />
											</s:if>
											<s:if test='%{#firmaWarning}'>
												<img class="resize-svg-16" title="<wp:i18n key='LABEL_DOCUMENTO_FIRMA_NON_VERIFICATA' />" alt="<wp:i18n key='LABEL_DOCUMENTO_FIRMA_NON_VERIFICATA' />" src="<s:property value='%{#imgDocNonFirmato}'/>">
											</s:if>
											<c:choose>
												<c:when test="${skin == 'highcontrast' || skin == 'text'}">
													<li >
														<s:a href="%{#urlDownloadDocUlteriore}?id=%{#status.index}&amp;tipoBusta=%{#documenti.tipoBusta}&amp;codice=%{#documenti.codice}"
																title="%{#attr.titleDownloadAllegato}">
															<wp:i18n key="LABEL_SCARICA_ALLEGATO" />
														</s:a>
														<span>(<s:property value="%{#documenti.docUlterioriSize.get(#status.index)}" /> KB)</span>
													</li>
													<li>
														<s:if test="%{!#partecipazione.plicoUnicoOfferteDistinte}">
															<a href='<wp:action path="/ExtStr2/do/FrontEnd/GareTel/confirmDeleteAllegatoUlteriore.action"/>&amp;id=${status.index}&amp;ext=${param.ext}&amp;tipoBusta=<s:property value="%{#documenti.tipoBusta}"/>&amp;codice=<s:property value="%{#documenti.codice}"/>'
															   title='<s:property value="%{#attr.titleEliminaAllegato}"/>' class="bkg delete">
																 <wp:i18n key="LABEL_ELIMINA_ALLEGATO" />
															</a>
														</s:if>
														<s:else>
															<a href='<wp:action path="/ExtStr2/do/FrontEnd/GareTel/confirmDeleteAllegatoUlteriore.action"/>&amp;id=${status.index}&amp;ext=${param.ext}&amp;tipoBusta=<s:property value="%{#documenti.tipoBusta}"/>&amp;codice=<s:property value="%{codice}"/>&amp;codiceGara=<s:property value="%{#riepilogoBuste.codiceGara}"/>'
															   title='<s:property value="%{#attr.titleEliminaAllegato}"/>' class="bkg delete">
																 <wp:i18n key="LABEL_ELIMINA_ALLEGATO" />
															</a>
														</s:else>
													</li>
												</c:when>
												<c:otherwise>
													<li <s:if test="%{#firmaWarning}">class="not-signed-alert"</s:if>>
														<s:a href="%{#urlDownloadDocUlteriore}?id=%{#status.index}&amp;tipoBusta=%{#documenti.tipoBusta}&amp;codice=%{#documenti.codice}"
															 title="%{#attr.titleDownloadAllegato}" class="bkg download">
															<s:property value="%{#documenti.docUlterioriFileName.get(#status.index)}"/>
														</s:a>
														<span>(<s:property value="%{#documenti.docUlterioriSize.get(#status.index)}" /> KB)</span>
													</li>
													<li>
														<s:if test="%{!#partecipazione.plicoUnicoOfferteDistinte}">
															<a href='<wp:action path="/ExtStr2/do/FrontEnd/GareTel/confirmDeleteAllegatoUlteriore.action"/>&amp;id=${status.index}&amp;ext=${param.ext}&amp;tipoBusta=<s:property value="%{#documenti.tipoBusta}"/>&amp;codice=<s:property value="%{#documenti.codice}"/>'
															   title="<s:property value='%{#attr.titleEliminaAllegato}'/>" class="bkg delete">
															</a>
														</s:if>
														<s:else>
															<a href='<wp:action path="/ExtStr2/do/FrontEnd/GareTel/confirmDeleteAllegatoUlteriore.action"/>&amp;id=${status.index}&amp;ext=${param.ext}&amp;tipoBusta=<s:property value="%{#documenti.tipoBusta}"/>&amp;codice=<s:property value="%{codice}"/>&amp;codiceGara=<s:property value="%{#riepilogoBuste.codiceGara}"/>'
															   title="<s:property value='%{#attr.titleEliminaAllegato}'/>" class="bkg delete">
															</a>
														</s:else>
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
								<%-- Salta il form di inserimento di un documento ulteriore e vai ai bottoni --%>
								<p class="noscreen">[ <a href="#" id="insDoc" ><wp:i18n key="LABEL_COMUNICAZIONI_SALTA_FORM_VAI_BOTTONI" /></a> ]</p>
								<form action="<wp:action path="/ExtStr2/do/FrontEnd/GareTel/processPageDocumenti.action" />" method="post"
											enctype="multipart/form-data" id="docUlterioreForm">
									<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />

									<div>
										<c:set var="count" value="${count + 1}" scope="page"/>
										<s:set var="countStruts">${count}</s:set>

										<label class="noscreen" for="docUlterioreDesc_${count}"><wp:i18n key="LABEL_DOCUMENTO_DESCRIZIONE_ULTERIORE" /> : </label>
										<input type="text" name="docUlterioreDesc" id="docUlterioreDesc_${count}" class="long-text" placeholder="${descrizioneHint}" aria-label="<s:property value='%{#descDocUlteriore}'/>" />&nbsp;
										<label class="noscreen" for="docUlteriore_${count}"><wp:i18n key='LABEL_FILE'/> : </label><input type="file" name="docUlteriore" id="docUlteriore_${count}" class="block-ui" />&nbsp;

										<input type="hidden" id="ext_${count}" name="ext" value="${param.ext}" />
										<input type="hidden" id="tipoBusta_${count}" name="tipoBusta" value="<s:property value="%{#documenti.tipoBusta}"/>"/>
										<s:hidden id="operazione_%{#countStruts}" name="operazione" value="%{#documenti.operazione}" />

										<s:if test="%{!partecipazione.plicoUnicoOfferteDistinte}">
											<!-- NO LOTTI DISTINTI -->
											<input type="hidden" id="codice_${count}" name="codice" value="<s:property value="%{#documenti.codice}"/>"/>
										</s:if>
										<s:else>
											<!-- LOTTI DISTINTI -->
											<s:hidden id="codice_%{#countStruts}" name="codice" value="%{#documenti.codice}" />
											<s:hidden id="codiceGara_%{#countStruts}" name="codiceGara" value="%{#session.riepilogoBuste.codiceGara}" />
										</s:else>

										<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_adddocult.jsp" />
									</div>
								</form>
							</td>
						</tr>
					</tbody>
				</table>
			</div>
			
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/internalServlet/fileupload/fileupload.jsp" />
			
			<p>
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/internalServlet/fileupload/infoUploadFile.jsp">
					<jsp:param name="dimensioneAttualeFileCaricati" value="${dimensioneAttualeFileCaricati}"/>
				</jsp:include>
			</p>
		</fieldset>

		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/skip_main_content_from_buttons.jsp" />

		<form action="<wp:action path="/ExtStr2/do/FrontEnd/GareTel/processPageDocumenti.action" />" method="post" id="mainForm">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />

			<div class="azioni">
				<c:set var="count" value="${count + 1}" scope="page"/>
				<s:set var="countStruts">${count}</s:set>

				<input type="hidden" id="ext_${count}" name="ext" value="${param.ext}" />
				<input type="hidden" id="tipoBusta_${count}" name="tipoBusta" value="<s:property value="%{#documenti.tipoBusta}"/>"/>
				<s:hidden id="operazione_%{#countStruts}" name="operazione" value="%{#documenti.operazione}" />

				<s:if test="%{!partecipazione.plicoUnicoOfferteDistinte}">
					<!-- NO LOTTI DISTINTI -->
					<input type="hidden" id="codice_${count}" name="codice" value="<s:property value="%{#documenti.codice}"/>"/>
				</s:if>
				<s:else>
					<!-- LOTTI DISTINTI -->
					<s:hidden id="codice_%{#countStruts}" name="codice" value="%{#documenti.codice}" />
					<s:hidden id="codiceGara_%{#countStruts}" name="codiceGara" value="%{#riepilogoBuste.riepilogoBuste.codiceGara}" />
					<s:hidden id="progressivoOfferta_%{#countStruts}" name="progressivoOfferta" value="%{#riepilogoBuste.progressivoOfferta}" />
				</s:else>

				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_back_to_menu.jsp" />

				<s:if test="%{#documenti.datiModificati}">
					<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_save_docs.jsp" />
					<s:submit value="Salva documenti" title="Salva le modifiche ai documenti" class="button block-ui" method="save" ></s:submit>
				</s:if>
				
				<wp:i18n key="BUTTON_WIZARD_VERIFICA_BUSTA" var="valueVerificaButton" />
				<wp:i18n key="TITLE_WIZARD_VERIFICA_BUSTA" var="titleVerificaButton" />
				<s:submit value="%{#attr.valueVerificaButton}" title="%{#attr.titleVerificaButton}" cssClass="button" method="check" ></s:submit>
				
			</div>
		</form>
	</s:else>
</div>