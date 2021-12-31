<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>

<%-- <script type="text/javascript" src='<wp:resourceURL/>static/js/ppgare/uploadFile.js'></script> --%>
<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/internalServlet/fileupload/uploadFile.jsp" />
 
<s:set var="helper" value="%{#session['dettOffertaAsta']}"/>
<s:set var="helperDocumenti" value="%{#helper.documenti}"/>

<s:set var="imgDocumentoFirmato"><wp:resourceURL/>static/img/smartcard.svg</s:set>


<s:set var="documentiNonFirmati" value="false"/>
<s:iterator value="%{#helperDocumenti.docUlterioriDesc}" var="descDocUlteriore" status="status">
	<%-- notificata i doc senza firma non verificabili --%>	
	<s:set var="firmaWarning" 
			value='%{!(#helperDocumenti.docUlterioriFileName.get(#status.index).toUpperCase().endsWith(".P7M") ||
			           #helperDocumenti.docUlterioriFileName.get(#status.index).toUpperCase().endsWith(".TSD"))}' />													
	<s:if test="%{#firmaWarning}">
		<s:set var="documentiNonFirmati" value="true"/>
	</s:if>																																				
</s:iterator>


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2><wp:i18n key='TITLE_PAGE_ASTA_CONFERMA_OFFERTA_FINALE'/></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/field_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_messages.jsp" />

	<jsp:include page="stepsConfermaOfferta.jsp" />

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_WIZ_CONFERMA_ASTA_DOCUMENTI"/>
	</jsp:include>

	<s:if test="%{deleteAllegato}">
		<s:if test="%{not #helper.datiInviati}">
			<p class="question">
				<wp:i18n key='LABEL_ELIMINA_DOCUMENTO'/> "<s:property value="%{#helperDocumenti.docUlterioriDesc.get(id)}" />" 
				(<wp:i18n key='LABEL_FILE'/> <s:property value="%{#helperDocumenti.docUlterioriFileName.get(id)}" />)?
			</p>
			<div class="azioni">
				<form action="<wp:action path="/ExtStr2/do/FrontEnd/Aste/deleteAllegato.action" />" method="post" class="azione">
					<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
					
					<div>
						<s:hidden name="id" value="%{id}" id="idDelete" />
						<input type="hidden" name="ext" value="${param.ext}" />
						<wp:i18n key="LABEL_YES" var="valueButtonYes" />
						<wp:i18n key="TITLE_CONFIRM_DEL_YES" var="titleButtonYes" />
						<s:submit value="%{#attr.valueButtonYes}" title="%{#attr.titleButtonYes}" cssClass="button block-ui"></s:submit>
					</div>
				</form>
				<form action="<wp:action path="/ExtStr2/do/FrontEnd/Aste/cancelDeleteAllegato.action" />"
						method="post" class="azione">
					<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
					<div>
						<input type="hidden" name="ext" value="${param.ext}" />
						<wp:i18n key="LABEL_NO" var="valueButtonNo" />
						<wp:i18n key="TITLE_CONFIRM_DEL_NO" var="titleButtonNo" />
						<s:submit value="%{#attr.valueButtonNo}" title="%{#attr.titleButtonNo}" cssClass="button block-ui"></s:submit>
					</div>
				</form>
			</div>
		</s:if>
	</s:if>
	<s:else>
		<!-- ALLEGATI -->
		<s:set var="imgDocNonFirmato"><wp:resourceURL/>static/img/not-signed-alert.svg</s:set>
		
		<p class="instructions"><wp:i18n key="LABEL_DOCUMENTI_OBBLIGATORI_PER_PROCEDERE" /></p>

		<s:if test="%{#documentiNonFirmati}">
			<p class="important not-signed-alert">
			<wp:i18n key="LABEL_MESSAGE_WARNING" />: <wp:i18n key="LABEL_FIRMA_DOCUMENTI_NON_VERIFICABILE_1" /> <img class="resize-svg-16" alt='<wp:i18n key="LABEL_DOCUMENTO_FIRMA_NON_VERIFICATA" />' src="<s:property value='%{#imgDocNonFirmato}'/>">, 
			<wp:i18n key="LABEL_FIRMA_DOCUMENTI_NON_VERIFICABILE_2" />
			</p>
		</s:if>

		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/skip_form_buttons.jsp" />

		<fieldset>
			<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_DOCUMENTI_ALLEGATI" /></legend>
			
			<div class="table-container">
				<table id="tableDocumenti" class="wizard-table" summary='<wp:i18n key="LABEL_ELENCO_DOCUMENTI" />'>
					<thead>
						<tr>
							<th scope="col"><wp:i18n key="LABEL_DESCRIZIONE" /></th>
							<th scope="col"><wp:i18n key="LABEL_ALLEGATO" /></th>
						</tr>
					</thead>
					<tbody>
						<s:url id="urlDownload" namespace="/do/FrontEnd/Aste" action="downloadAllegatoAsta" />

						<s:set var="docOffertaAllegato" value="false"/>
						<s:iterator value="%{#helperDocumenti.docUlterioriDesc}" var="descDocUlteriore" status="status">								
							<tr>
								<s:set var="docOffertaAllegato" value="true"/>
								
								<td >
									<c:choose>
										<c:when test="${skin == 'highcontrast' || skin == 'text'}">
											<span title='<wp:i18n key="TITLE_RICHIESTA_FIRMA_DIGITALE" />'>[<wp:i18n key="LABEL_RICHIESTA_FIRMA_DIGITALE" />]</span>
										</c:when>
										<c:otherwise>
											[
											<img class="resize-svg-16" src="${imgDocumentoFirmato}" alt='<wp:i18n key="LABEL_RICHIESTA_FIRMA_DIGITALE" />' title='<wp:i18n key="TITLE_RICHIESTA_FIRMA_DIGITALE" />' />
											]
										</c:otherwise>
									</c:choose>
									<s:property value="%{#descDocUlteriore}"/> <span class="required-field">*</span>
								</td>
								
								<td class="azioni">
									<ul>
										<%-- notificata i doc senza firma non verificabili --%>	
										<s:set var="firmaWarning" 
												value='%{!(#helperDocumenti.docUlterioriFileName.get(#status.index).toUpperCase().endsWith(".P7M") ||
												           #helperDocumenti.docUlterioriFileName.get(#status.index).toUpperCase().endsWith(".TSD"))}' />

										<s:if test='%{#firmaWarning}'>
											<img class="resize-svg-16" title='<wp:i18n key="LABEL_DOCUMENTO_FIRMA_NON_VERIFICATA" />' alt='<wp:i18n key="LABEL_DOCUMENTO_FIRMA_NON_VERIFICATA" />' src="<s:property value='%{#imgDocNonFirmato}'/>">
										</s:if>
										
										<c:choose>
											<c:when test="${skin == 'highcontrast' || skin == 'text'}">
												<li>
													<s:property value="%{#helperDocumenti.docUlterioriFileName.get(#status.index)}" />
 													<span>(<s:property value="%{#helperDocumenti.docUlterioriSize.get(#status.index)}" /> KB)</span> 
												</li>
												<s:if test="%{not #helper.datiInviati}">
													<li>
														<a href='<wp:action path="/ExtStr2/do/FrontEnd/Aste/confirmDeleteAllegato.action"/>&amp;id=${status.index}&amp;ext=${param.ext}&amp;${tokenHrefParams}' 
														   title='<wp:i18n key="TITLE_ELIMINA_ALLEGATO"/>'>
															 <wp:i18n key="TITLE_ELIMINA_ALLEGATO"/>
														</a>
													</li>
												</s:if>
											</c:when>
											<c:otherwise>
												<li <s:if test="%{#firmaWarning}">class="not-signed-alert"</s:if> >
													<%-- 
													<s:a href="%{#urlDownload}?id=%{#status.index}&amp;codice=%{codice}" 
 														 title="Scarica l'allegato" cssClass="bkg download">
 														<s:property value="%{#helperDocumenti.docUlterioriFileName.get(#status.index)}"/> 
													</s:a>
													--%>
													<s:property value="%{#helperDocumenti.docUlterioriFileName.get(#status.index)}" />
													<span>(<s:property value="%{#helperDocumenti.docUlterioriSize.get(#status.index)}" /> KB)</span>  
												</li>
												<s:if test="%{not #helper.datiInviati}">
													<li>
														<a href='<wp:action path="/ExtStr2/do/FrontEnd/Aste/confirmDeleteAllegato.action"/>&amp;id=${status.index}&amp;ext=${param.ext}&amp;${tokenHrefParams}' 
														   title='<wp:i18n key="TITLE_ELIMINA_ALLEGATO"/>' class="bkg delete">
														</a>
													</li>
												</s:if>
											</c:otherwise>
										</c:choose>
									</ul>
								</td>
							</tr>
						</s:iterator>
	
						<s:if test="%{not #docOffertaAllegato}">
							<s:if test="%{not #helper.datiInviati}">
								<tr>
									<td >
										<c:choose>
											<c:when test="${skin == 'highcontrast' || skin == 'text'}">
												<span title='<wp:i18n key="TITLE_RICHIESTA_FIRMA_DIGITALE" />'>[<wp:i18n key="LABEL_RICHIESTA_FIRMA_DIGITALE" />]</span>
											</c:when>
											<c:otherwise>
												[
												<img class="resize-svg-16" src="${imgDocumentoFirmato}" alt='<wp:i18n key="LABEL_RICHIESTA_FIRMA_DIGITALE" />' title='<wp:i18n key="TITLE_RICHIESTA_FIRMA_DIGITALE" />' />
												]
											</c:otherwise>
										</c:choose>
										<wp:i18n key="BUTTON_CONFERMA_OFFERTA_FINALE" /> <span class="required-field">*</span>
									</td>
									
									<td >
										<p class="noscreen">[ <a href="#buttons" id="insDoc" ><wp:i18n key="LABEL_COMUNICAZIONI_SALTA_FORM_VAI_BOTTONI" /></a> ]</p>
										
										<form action="<wp:action path='/ExtStr2/do/FrontEnd/Aste/aggiungiAllegato.action' />"
							          		  method="post" enctype="multipart/form-data" id="docUlterioreForm">
											<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
											
											<div>
												<span class="noscreen"></span><input type="file" name="docUlteriore" size="20" accept=".p7m,.tsd,.pdf,.xml" />&nbsp;
												
												<wp:i18n key="BUTTON_ATTACH_FILE_FIRMATO" var="valueAttachButton" />
												<wp:i18n key="TITLE_ATTACH_FILE_FIRMATO" var="titleAttachButton" />
												<s:submit value="%{#attr.valueAttachButton}" title="%{#attr.titleAttachButton}" cssClass="button block-ui" />
												
												<input type="hidden" name="ext" value="${param.ext}" />
												<input type="hidden" name="codice" value="<s:property value="%{codice}"/>"/>
												<input type="hidden" name="docUlterioreDesc" value='<wp:i18n key="BUTTON_CONFERMA_OFFERTA_FINALE" />' />
											</div>
											<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/internalServlet/fileupload/fileupload.jsp" />
										</form>
									</td>
								</tr>
							</s:if>
						</s:if>
					</tbody>
				</table>
			</div>
			<s:set var="kbCaricati" value="%{dimensioneAttualeFileCaricati}"></s:set>
			<s:set var="kbDisponibili" value="%{limiteTotaleUpload - dimensioneAttualeFileCaricati}"></s:set>
			<p>
				<wp:i18n key="LABEL_MAX_FILE_SIZE" /> <strong><s:property value="%{limiteUploadFile}" /></strong> KB.
			</p>
	
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/skip_main_content_from_buttons.jsp" />

		</fieldset>
			
		<form action="<wp:action path="/ExtStr2/do/FrontEnd/Aste/processPageUploadPdf.action" />" method="post">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			
			<div class="azioni">
				<input type="hidden" name="page" value="uploadpdf"/>
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_previous.jsp" />
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_next.jsp" />
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_cancel.jsp" />
			</div>
		</form>	
	</s:else>
</div>
