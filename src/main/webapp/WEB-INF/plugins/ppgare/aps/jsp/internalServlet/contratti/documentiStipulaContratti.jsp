<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>


<wp:i18n key="LABEL_YES" var="valueYesButton" />
<wp:i18n key="LABEL_NO" var="valueNoButton" />
<wp:i18n key="TITLE_CONFIRM_DEL_YES" var="titleYesButton"/>
<wp:i18n key="TITLE_CONFIRM_DEL_NO" var="titleNoButton"/>
<script>
	var allowEmptyField = true;
</script>
<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/internalServlet/fileupload/uploadFile.jsp" />

<%-- prepara il BACK LINK della pagina di visualizzazione della firma digitale --%>
<c:set var="fromPageFirmaDigitale" scope="session"><wp:action path="/ExtStr2/do/FrontEnd/Contratti/openPageDocumentiStipule.action?statoStipula=${statoStipula}&codice=${codice}&codiceStipula=${codiceStipula}"/></c:set>
<div class="portgare-view">

	<h2><wp:i18n key="TITLE_PAGE_DOCUMENTI_STIPULE" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_DOCUMENTI_STIPULE"/>
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_messages.jsp" />


		<s:if test="%{documentiReadOnly.size == 0 && documentiUpload.size == 0}">
			<wp:i18n key="LABEL_NO_DOCUMENTI" />
		</s:if>
		<s:if test="%{documentiReadOnly.size > 0}">
			<fieldset>
				<legend>
					<span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_DOCSTIPULE_LISTA" />
				</legend>
				<s:iterator id="fase" value="fasiDocReadonly">
					<div class="fieldset-row ">
					<s:iterator value="maps['listaFasiStipule']">
						<s:if test="%{key == #fase}"><b><s:property value="%{value}"/></b></s:if>
					</s:iterator>
					</div>
					<s:iterator id="doc" value="documentiReadOnly">
						<s:if test="%{#doc.fase == #fase}">
							<div class="fieldset-row ">
								<s:if test="%{#doc.iddocdig != null}">
									<div class = "label">
										<s:set var="isDocFirmato" value='%{#doc.nomeFile.toUpperCase().endsWith(".P7M") ||
												     		   				#doc.nomeFile.toUpperCase().endsWith(".TSD")}' />

										<s:if test = "%{#isDocFirmato}">
											<c:set var="urlFileDownload"><wp:action path="/ExtStr2/do/FrontEnd/Contratti/downloadAllegatoStipula.action"/></c:set>
											<c:set var="url" value="${urlFileDownload}&amp;docId=${id}&amp;codice=${codice}&amp;iddocdig=${iddocdig}"/>
											<form action='${url}' method="post">
												<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
												<a href="javascript:;" onclick="parentNode.submit();" title='<wp:i18n key="LABEL_SCARICA_ALLEGATO" />'>
													<s:property value="titolo" />
												</a>
											</form>
										</s:if>
										<s:else >
											<s:url id="urlFileDownloadNoFirma" namespace="/do/FrontEnd/Contratti" action="downloadAllegatoStipula" />
											<form action='${urlFileDownloadNoFirma}' method="post">
												<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
												<a href="javascript:;" onclick="parentNode.submit();" title='<wp:i18n key="LABEL_SCARICA_ALLEGATO" />'>
													<s:property value="titolo" />
												</a>
												<input type="hidden" name="docId" value="${id}" />
												<input type="hidden" name="codice" value="${idStipula}" />
											</form>
										</s:else>

									</div>
									<div class = "element">
										<s:property value="note" />
									</div>

								</s:if>
								<s:if test="%{#doc.iddocdig == null}">
									<div class = "label">
										<s:property value="titolo" />
										<s:property value="descrizione" />
									</div>
									<div class = "element">

									</div>
								</s:if>
							</div>
						</s:if>
					</s:iterator>
				</s:iterator>
			</fieldset>
		</s:if>


		<s:if test="%{documentiUpload.size > 0}">

		    <p>
<%--
			<wp:i18n key="LABEL_DOCSTIPULE_INFO" />
--%>
			</p>
			<s:set var="imgDocNonFirmato"><wp:resourceURL/>static/img/not-signed-alert.svg</s:set>
			<s:if test="%{esisteFileConFirmaNonVerificata}">
				<p class="important not-signed-alert">
				<wp:i18n key="LABEL_MESSAGE_WARNING" />: <wp:i18n key="LABEL_FIRMA_DOCUMENTI_NON_VERIFICABILE_1" /> <img class="resize-svg-16" alt='<wp:i18n key="LABEL_DOCUMENTO_FIRMA_NON_VERIFICATA" />' src="<s:property value='%{#imgDocNonFirmato}'/>">,
				<wp:i18n key="LABEL_FIRMA_DOCUMENTI_NON_VERIFICABILE_2" />
				</p>
			</s:if>

			<s:iterator id="fase" value="fasiDocUpload">
				<s:iterator value="maps['listaFasiStipule']">
					<s:if test="%{key == #fase}"><b><s:property value="%{value}"/></b></s:if>
				</s:iterator>

				<table id="tableDocumenti" class="wizard-table" summary="Elenco dei documenti allegati">
					<thead>
						<tr>
							<th scope="col"><wp:i18n key="LABEL_DOCSTIPULE_DOCUMENTO" /></th>
							<th scope="col"><wp:i18n key="LABEL_NOTE_DOCUMENTO" /></th>
							<th scope="col"><wp:i18n key="LABEL_DOCSTIPULE_ALLEGATO" /></th>
						</tr>
					</thead>
					<tbody>
						<s:iterator id="doc" value="documentiUpload">
<%--
doc.titolo=${doc.titolo}><br/>
doc.descrizione=${doc.descrizione}<br/>
doc.formato=${doc.formato}<br/>
doc.obbligatorio=${doc.obbligatorio}<br/>
doc.nomeFile=${doc.nomeFile}<br/>
 --%>
							<%--
							<s:set var="isDocFirmato" value='%{#doc.nomefilefacsimile.toUpperCase().endsWith(".P7M") ||
												     		   #doc.nomefilefacsimile.toUpperCase().endsWith(".TSD")}' />
	 						--%>

							<s:if test="%{#doc.fase == #fase}">
								<tr>
									<td>
										<c:set var="nomeAllegato"><s:property value="titolo" /><br/><s:property value="descrizione" /></c:set>
										<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/attachment_info.jsp" >
											<jsp:param name="formatoFile" value="${doc.formato}" />
											<jsp:param name="nome" value="${nomeAllegato}" />
											<jsp:param name="obbligatorio" value="${doc.obbligatorio}" />
										</jsp:include>
									</td>

									<s:if test="%{#doc.iddocdig != null}">
										<s:set var="firmaWarning" value='%{"2".equalsIgnoreCase(#doc.firmacheck)}' />
										<td>
											<s:property value="note" /> (${nomeFile})
										</td>
										<td>
											<div style="display: flex;width: max-content;">
												<s:if test='%{#firmaWarning}'>
													<img class="resize-svg-16" title="<wp:i18n key='LABEL_DOCUMENTO_FIRMA_NON_VERIFICATA' />" alt="<wp:i18n key='LABEL_DOCUMENTO_FIRMA_NON_VERIFICATA' />" src="<s:property value='%{#imgDocNonFirmato}'/>">&nbsp;&nbsp;
												</s:if>
												<c:if test = "${fn:endsWith(nomeFile, 'p7m') || fn:endsWith(nomeFile, 'tsd')|| fn:endsWith(nomeFile, 'P7M') || fn:endsWith(nomeFile, 'TSD')}">

														<c:set var="urlFileDownload"><wp:action path="/ExtStr2/do/FrontEnd/Contratti/downloadAllegatoStipula.action"/></c:set>
														<c:set var="url" value="${urlFileDownload}&amp;docId=${id}&amp;codice=${codice}&amp;iddocdig=${iddocdig}"/>
														<form action='${url}' method="post">
															<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
															<a href="javascript:;" onclick="parentNode.submit();" title='<wp:i18n key="LABEL_SCARICA_ALLEGATO" />' class = "bkg download <s:if test="%{#firmaWarning}">not-signed-alert</s:if>">
																${nomeFile}
															</a>
														</form>
												</c:if>
												<c:if test = "${!(fn:endsWith(nomeFile, 'p7m') || fn:endsWith(nomeFile, 'tsd')|| fn:endsWith(nomeFile, 'P7M') || fn:endsWith(nomeFile, 'TSD'))}">
													<s:url id="urlFileDownloadNoFirma" namespace="/do/FrontEnd/Contratti" action="downloadAllegatoStipula" />
													<form action='${urlFileDownloadNoFirma}' method="post">
														<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
														<a href="javascript:;" onclick="parentNode.submit();" title='<wp:i18n key="LABEL_SCARICA_ALLEGATO" />' class = "bkg download <s:if test="%{#firmaWarning}">not-signed-alert</s:if>">
															${nomeFile}
														</a>
														<input type="hidden" name="docId" value="${id}" />
													</form>
												</c:if>
												<form action="<wp:action path="/ExtStr2/do/FrontEnd/Contratti/confermaDialogDelete.action" />" method="post">
													<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
													<a href="javascript:;" onclick="parentNode.submit();" title='<wp:i18n key="LABEL_ELIMINA_ALLEGATO" />' class = "bkg delete">
													</a>
													<input type="hidden" name="docId" value="${iddocdig}" />
													<input type="hidden" name="codice" value="${codice}" />
													<input type="hidden" name="codiceStipula" value="<s:property value="%{codiceStipula}"/>"/>
												</form>
											</div>
										</td>
									</s:if>
									<s:if test="%{#doc.iddocdig == null}">
										<td colspan="2">
											<form action="<wp:action path='/ExtStr2/do/FrontEnd/Contratti/processPageDocumentiContratti.action' />"
												method="post" enctype="multipart/form-data" style="display:flex;" >
												<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
												<label class="noscreen" for="noteDoc_${id}">noteDoc</label>
												<input type="text" id="noteDoc_${id}" name="noteDoc" class="long-text" />
												<input type="hidden" name="codice" value="<s:property value="%{codice}"/>"/>
												<input type="hidden" name="codiceStipula" value="<s:property value="%{codiceStipula}"/>"/>
												<input type="hidden" name="docId" value="${id}" />
												<input type="hidden" name="formato" value="<s:property value="%{formato}"/>" />
												<input type="file" name="docToUpload"
														aria-label="${nome}"
														<s:if test="%{formato == DOCUMENTO_FORMATO_FIRMATO}">accept=".p7m,.tsd,.pdf,.xml"</s:if>
														<s:elseif test="%{formato == DOCUMENTO_FORMATO_PDF}">accept=".pdf"</s:elseif>
														<s:elseif test="%{formato == DOCUMENTO_FORMATO_EXCEL}">accept=".xls,.xlsx,.ods"</s:elseif> />

												<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_adddocrich.jsp" >
													<jsp:param name="formato" value="${formato}" />
												</jsp:include>

												<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/internalServlet/fileupload/fileupload.jsp" />
											</form>
										</td>
									</s:if>
								</tr>
							</s:if>
						</s:iterator>
					</tbody>
				</table>

			</s:iterator>
		</s:if>
		<s:if test="%{deleteDoc == false && invioStipula == false}">
			<s:if test="%{documentiUpload.size > 0}">
				<div class="azioni">
					<form action="<wp:action path="/ExtStr2/do/FrontEnd/Contratti/confermaDialogInvio.action" />" method="post" class="azione">
						<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
						<div>
							<input type="submit" value="<wp:i18n key="BUTTON_CONFIRM_STIPULE" />" class="button" title="Conferma i documenti delle stipule">
							<input type="hidden" name="codiceStipula" value="<s:property value="%{codiceStipula}"/>"/>
							<input type="hidden" name="codice" value="<s:property value="%{codice}"/>"/>
						</div>
					</form>
				</div>
			</s:if>
		</s:if>

	<s:if test="%{deleteDoc}">
		<p class="question">
			<wp:i18n key='LABEL_ELIMINA_DOCUMENTO'/>?
		</p>
		<div class="azioni">
			<form action="<wp:action path="/ExtStr2/do/FrontEnd/Contratti/deleteAllegatoStipula.action" />" method="post" class="azione">
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
				<div>
					<input type="hidden" name="docId" value="<s:property value="%{docId}"/>"/>
					<input type="hidden" name="codice" value="<s:property value="%{codice}"/>"/>
					<input type="hidden" name="codiceStipula" value="<s:property value="%{codiceStipula}"/>"/>
					<s:submit value="%{#attr.valueYesButton}" title="%{#attr.titleYesButton}" cssClass="button block-ui"></s:submit>
				</div>
			</form>
			<form action="<wp:action path="/ExtStr2/do/FrontEnd/Contratti/cancelDialogDelete.action" />" method="post" class="azione">
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
				<div>
					<input type="hidden" name="docId" value="${iddocdig}" />
					<input type="hidden" name="codice" value="${codice}" />
					<input type="hidden" name="codiceStipula" value="<s:property value="%{codiceStipula}"/>"/>
					<s:submit value="%{#attr.valueNoButton}" title="%{#attr.titleNoButton}" cssClass="button block-ui"></s:submit>
				</div>
			</form>
		</div>
	</s:if>
	<s:if test="%{invioStipula}">
		<p class="question">
			<wp:i18n key='LABEL_CONFERMA_INVIO_STIPULA'/>
		</p>
		<div class="azioni">
			<form action="<wp:action path="/ExtStr2/do/FrontEnd/Contratti/confermaStipula.action" />" method="post" class="azione">
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
				<div>
					<s:submit value="%{#attr.valueYesButton}" title="%{#attr.titleYesButton}" cssClass="button block-ui"></s:submit>
					<input type="hidden" name="codiceStipula" value="<s:property value="%{codiceStipula}"/>"/>
					<input type="hidden" name="codice" value="<s:property value="%{codice}"/>"/>
				</div>
			</form>
			<form action="<wp:action path="/ExtStr2/do/FrontEnd/Contratti/cancelDialogInvio.action" />" method="post" class="azione">
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
				<div>
					<input type="hidden" name="docId" value="${iddocdig}" />
					<input type="hidden" name="codice" value="${codice}" />
					<input type="hidden" name="codiceStipula" value="<s:property value="%{codiceStipula}"/>"/>
					<s:submit value="%{#attr.valueNoButton}" title="%{#attr.titleNoButton}" cssClass="button block-ui"></s:submit>
				</div>
			</form>
		</div>
	</s:if>
	<div class="back-link">
		<a href='<wp:action path="/ExtStr2/do/FrontEnd/Contratti/dettaglioStipulaContratti.action" />&amp;id=<s:property value="codice"/>&amp;codice=<s:property value="%{codiceStipula}"/>&amp;&amp;${tokenHrefParams}'
			   title='<wp:i18n key="LINK_VIEW_DETAIL" />'>
				<wp:i18n key="LINK_BACK_DETTAGLIO_STIPULA" />
		</a>
	</div>
</div>
