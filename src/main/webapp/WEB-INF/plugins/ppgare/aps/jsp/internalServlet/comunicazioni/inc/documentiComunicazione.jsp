<%@ taglib prefix="wp" uri="aps-core.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<c:set var="descrizioneHint"><wp:i18n key="LABEL_INSERIRE_DESCRIZIONE_PER_ALLEGARE_DOC" /></c:set>
<s:set var="helper" value="%{#session['nuovaComunicazione']}"/>
<s:set var="helperDocumenti" value="%{#session['nuovaComunicazione'].documenti}"/>

<s:if test="%{deleteAllegato}">
	<%-- ELIMINA DOCUMENTO ULTERIORE DELLA COMUNICAZIONE --%>
	<p class="question">
		<wp:i18n key="LABEL_ELIMINA_DOCUMENTO" /> "<s:property value="%{#helperDocumenti.docUlterioriDesc.get(id)}" />" 
		(file <s:property value="%{#helperDocumenti.docUlterioriFileName.get(id)}" />)?
	</p>
	<div class="azioni">
		<form action="<wp:action path="/ExtStr2/do/FrontEnd/Comunicazioni/deleteAllegato.action" />"
			  method="post" class="azione">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			<div>
				<s:hidden name="id" value="%{id}" id="idDelete" />
				<input type="hidden" name="ext" value="${param.ext}" />
				<wp:i18n key="LABEL_YES" var="valueYesButton" />
				<wp:i18n key="TITLE_CONFIRM_DEL_YES" var="titleYesButton" />
				<s:submit value="%{#attr.valueYesButton}" title="%{#attr.titleYesButton}" cssClass="button block-ui"></s:submit>
			</div>
		</form>
		<form action="<wp:action path="/ExtStr2/do/FrontEnd/Comunicazioni/cancelDeleteAllegato.action" />"
			  method="post" class="azione">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			<div>
				<input type="hidden" name="ext" value="${param.ext}" />
				<wp:i18n key="LABEL_NO" var="valueNoButton" />
				<wp:i18n key="TITLE_CONFIRM_DEL_NO" var="titleNoButton" />
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
						<th scope="col"><wp:i18n key="LABEL_COMUNICAZIONI_DESCRIZIONE" /></th>
						<th scope="col"><wp:i18n key="LABEL_ALLEGATO" /></th>
					</tr>
				</thead>
				<tbody>
					<s:url id="urlDownload" namespace="/do/FrontEnd/Comunicazioni" action="downloadAllegatoNuovaComunicazione" />

					<s:iterator value="%{#helperDocumenti.docUlterioriDesc}" var="descDocUlteriore" status="status">
						<tr>
							<td>
								<s:property value="%{#descDocUlteriore}"/>
							</td>
							
							<td class="azioni">
								<ul>
									<c:choose>
										<c:when test="${skin == 'highcontrast' || skin == 'text'}">
											<li>
												<s:a href="%{#urlDownload}?id=%{#status.index}&amp;codice=%{codice}" 
													 title="%{#attr.titleScaricaAllegato}">
														<wp:i18n key="LABEL_SCARICA_ALLEGATO"/>
												</s:a> 
													<span>(<s:property value="%{#helperDocumenti.docUlterioriSize.get(#status.index)}" /> KB)</span> 
											</li>
											<li>
												<a href='<wp:action path="/ExtStr2/do/FrontEnd/Comunicazioni/confirmDeleteAllegato.action"/>&amp;id=${status.index}&amp;ext=${param.ext}' 
												   title="${attr.titleEliminaAllegato}"> 
													 <wp:i18n key="LABEL_ELIMINA_ALLEGATO"/>
												</a>
											</li>
										</c:when>
										<c:otherwise>
											<li>
												<s:a href="%{#urlDownload}?id=%{#status.index}&amp;codice=%{codice}" 
													 title="%{#attr.titleScaricaAllegato}" cssClass="bkg download">
														<s:property value="%{#helperDocumenti.docUlterioriFileName.get(#status.index)}"/> 
												</s:a>  
												<span>(<s:property value="%{#helperDocumenti.docUlterioriSize.get(#status.index)}" /> KB)</span>  
											</li>
											<li>
												<a href='<wp:action path="/ExtStr2/do/FrontEnd/Comunicazioni/confirmDeleteAllegato.action"/>&amp;id=${status.index}&amp;ext=${param.ext}' 
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
							<form action="<wp:action path="/ExtStr2/do/FrontEnd/Comunicazioni/aggiungiAllegatoComunicazione.action" />"
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
		<p>
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/internalServlet/fileupload/infoUploadFile.jsp">
				<jsp:param name="dimensioneAttualeFileCaricati" value="<s:property value='%{dimensioneAttualeFileCaricati}'/>"/>
			</jsp:include>
		</p>
	</fieldset> 

</s:else> 
