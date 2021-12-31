<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>

<%-- <script type="text/javascript" src='<wp:resourceURL/>static/js/ppgare/uploadFile.js'></script> --%>
<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/internalServlet/fileupload/uploadFileCatalogo.jsp" />

<%-- notificata i doc senza firma non verificabili --%>
<s:set var="documentiNonFirmati" value="false"/>
<s:set var="firmaWarning" value='%{!(riepilogoFileName.toUpperCase().endsWith(".P7M") ||
		           				     riepilogoFileName.toUpperCase().endsWith(".TSD"))}' />
<s:if test="%{#firmaWarning}">
	<s:set var="documentiNonFirmati" value="true"/>
</s:if>


<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/send_blockUI.jsp" />

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2><wp:i18n key="TITLE_PAGE_CATALOGHI_FIRMA_RIEPILOGO" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/field_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_messages.jsp" />

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_FIRMA_RIEPILOGO_MODIFICHE"/>
	</jsp:include>

	<s:if test="%{listaFirmatari.isEmpty()}">
		<p>
			<wp:i18n key="LABEL_PER_PROSEGUIRE_IMPOSTARE_UN_SOGGETTO" /> "<wp:i18n key="LABEL_RESPONSABILE_DICHIARAZIONI" />".
		</p>
		<div class="azioni">
			<form action="<wp:url page="ppgare_impr_visdati"/>" method="post" class="azione">
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
				<div>
					<wp:i18n key="LINK_AREA_PERSONALE_DATI_OP" var="buttonDatiOPValue" />
					<wp:i18n key="TITLE_VISUALIZZA_DATI_OP" var="buttonDatiOPTitle" />
					<s:submit value="%{#attr.buttonDatiOPValue}" title="%{#attr.buttonDatiOPTitle}" cssClass="button" />
				</div>
			</form>
		</div>
	</s:if>
	<s:elseif test="%{deleteRiepilogo}">
		<p class="question">
			<wp:i18n key="LABEL_ELIMINA_RIEPILOGO" /> ?
		</p>
		<div class="azioni">
			<form action="<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/deleteRiepilogo.action" />" method="post" class="azione">
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
				<div>
					<input type="hidden" name="ext" value="${param.ext}" />
					<wp:i18n key="LABEL_YES" var="valueButtonYes" />
					<wp:i18n key="TITLE_CONFIRM_DEL_YES" var="titleButtonYes" />
					<s:submit value="%{#attr.valueButtonYes}" title="%{#attr.titleButtonYes}" cssClass="button block-ui"></s:submit>
				</div>
			</form>
			<form action="<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/cancelDeleteRiepilogo.action" />" method="post" class="azione">
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
				<div>
					<input type="hidden" name="ext" value="${param.ext}" />
					<wp:i18n key="LABEL_NO" var="valueButtonNo" />
					<wp:i18n key="TITLE_CONFIRM_DEL_NO" var="titleButtonNo" />
					<s:submit value="%{#attr.valueButtonNo}" title="%{#attr.titleButtonNo}" cssClass="button block-ui"></s:submit>
				</div>
			</form>
		</div>
	</s:elseif>
	<s:else>
		<div id="sign_and_send">
			
			<s:if test="%{!riepilogoAlreadyUploaded}">
				<s:url id="urlPdfDomandaDownload" namespace="/do/FrontEnd/Cataloghi" action="createModificheProdottiPdf">
					<s:param name="urlPage">${currentPageUrl}</s:param>
					<s:param name="currentFrame">${param.currentFrame}</s:param>
				</s:url>

				<form action="${urlPdfDomandaDownload}" method="post" id="createPdfForm">
					<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
					<p>
						<wp:i18n key="LABEL_LISTA_SOGGETTI_DIRITTO_FIRMA" />:
					</p>
					<ul class="list">
						<s:iterator var="firmatario" value="listaFirmatari" status="status">
							<li class='<s:if test="%{#status.first}">first</s:if> <s:if test="%{#status.last}">last</s:if>'>
								<input type="radio" name="firmatarioSelezionato" 
										id='firmatarioSelezionato<s:property value="%{#status.index}"/>' 
										value='<s:if test="%{#firmatario.lista != null}"><s:property value="#firmatario.lista" />-<s:property value="#firmatario.index" /></s:if>' 
										<s:if test="%{#status.count==1}"> checked="checked"</s:if> 
										aria-label='<s:property value="%{#firmatario.nominativo}" />'/>
								<s:property value="%{#firmatario.nominativo}" />
							</li>
						</s:iterator>
					</ul>
					<div>
						<input type="hidden" name="urlPage" value="${currentPageUrl}" />
						
						<wp:i18n key="TITLE_WIZARD_GENERA_PDF" var="buttonGenPDFTitle" />
						<wp:i18n key="TITLE_GENERA_PDF_VARIAZIONI_PREZZO" var="buttonGenPDFPrezziTitle" />
						<wp:i18n key="TITLE_GENERA_PDF_MODIFICHE_PRODOTTI" var="buttonGenPDFProdottiTitle" />
						<s:if test="%{variazioneOfferta}">
							<s:submit method="createPdf" value="%{#attr.buttonGenPDFTitle}" title="%{#attr.buttonGenPDFPrezziTitle}" cssClass="button"/>
						</s:if>
						<s:else>
							<s:submit method="createPdf" value="%{#attr.buttonGenPDFTitle}" title="%{#attr.buttonGenPDFProdottiTitle}" cssClass="button"/>
						</s:else>
					</div>
				</form>
			</s:if>
			<form action="<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/processModificheProdotti.action" />" method="post" enctype="multipart/form-data" id="sendForm">
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
				<s:if test="%{!riepilogoAlreadyUploaded}">
					<p>
						<div>
							<wp:i18n key="LABEL_RIEPILOGO_FIRMATO" />:
							<input type="hidden" name="ext" value="${param.ext}" />
							<input type="file" name="riepilogo" size="20" accept=".p7m,.tsd,.pdf,.xml"/ aria-label='<wp:i18n key="LABEL_RIEPILOGO_FIRMATO" />'>
							<wp:i18n key="LABEL_ALLEGA" var="buttonAllegaValue" />
							<s:submit value="%{#attr.buttonAllegaValue}" title="%{#attr.buttonAllegaValue}" cssClass="button block-ui" method="add"></s:submit>
						</div>
					</p>
					<p>
						(<wp:i18n key="LABEL_MAX_FILE_SIZE" /> <strong><s:property value="%{limiteUploadFile}" /></strong> KB)
					</p>
					<div class="azioni">
						<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_cancel.jsp" />
						<input type="hidden" name="ext" value="${param.ext}" />
					</div>
				</s:if>
				<s:else>
					<p>
						<wp:i18n key="LABEL_RIEPILOGO_FIRMATO_VALIDATO" />
						<s:if test="%{variazioneOfferta}">
							<wp:i18n key="LABEL_PROCEDERE_INVIO_VARIAZIONI_PREZZO" />
						</s:if>
						<s:else>
							<wp:i18n key="LABEL_PROCEDERE_INVIO_MODIFICHE_CATALOGO" />
						</s:else>
					</p>
					
					<s:set var="imgDocNonFirmato"><wp:resourceURL/>static/img/not-signed-alert.svg</s:set>
					<s:if test="%{#documentiNonFirmati}">
						<p class="important not-signed-alert">
						<wp:i18n key="LABEL_MESSAGE_WARNING" />: <wp:i18n key="LABEL_FIRMA_DOCUMENTI_NON_VERIFICABILE_1" /> <img class="resize-svg-16" alt='<wp:i18n key="LABEL_DOCUMENTO_FIRMA_NON_VERIFICATA" />' src="<s:property value='%{#imgDocNonFirmato}'/>">, 
						<wp:i18n key="LABEL_FIRMA_DOCUMENTI_NON_VERIFICABILE_2" />
						</p>
					</s:if>
					
					<p <s:if test="%{#firmaWarning}">class="not-signed-alert"</s:if> >
						<s:if test='%{#firmaWarning}'><img class="resize-svg-16" title='<wp:i18n key="LABEL_DOCUMENTO_FIRMA_NON_VERIFICATA" />' alt='<wp:i18n key="LABEL_DOCUMENTO_FIRMA_NON_VERIFICATA" />' src="<s:property value='%{#imgDocNonFirmato}'/>"></s:if>
					
						<s:url id="urlDownloadRiepilogoFirmato" namespace="/do/FrontEnd/Cataloghi" action="downloadRiepilogoFirmato" />
						<wp:i18n key="LABEL_SCARICA_RIEPILOGO_FIRMATO" var="valueScaricaRiepilogo" />
						<wp:i18n key="LABEL_ELIMINA_RIEPILOGO_FIRMATO" var="valueEliminaRiepilogo" />
						<c:choose>
							<c:when test="${skin == 'highcontrast' || skin == 'text'}">
								<s:a href="%{#urlDownloadRiepilogoFirmato}&amp;%{#attr.tokenHrefParams}" title="%{#attr.valueScaricaRiepilogo}">
									${attr.valueScaricaRiepilogo}
								</s:a>
								<a href="<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/confirmDeleteRiepilogo.action"/>&amp;ext=${param.ext}&amp;${tokenHrefParams}" title="%{#attr.valueEliminaRiepilogo}">
									${attr.valueEliminaRiepilogo}
								</a>
							</c:when>
							<c:otherwise>
								<s:a href="%{#urlDownloadRiepilogoFirmato}&amp;%{#attr.tokenHrefParams}" title="%{#attr.valueScaricaRiepilogo}" cssClass="bkg p7m">
									<s:property value="{riepilogoFileName}"/>
								</s:a>
								<a href="<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/confirmDeleteRiepilogo.action"/>&amp;ext=${param.ext}&amp;${tokenHrefParams}" title="%{#attr.valueEliminaRiepilogo}" class="bkg delete">
								</a>
							</c:otherwise>
						</c:choose>
					</p>
					<div class="azioni">
						<s:if test="!sendBlocked">
							<wp:i18n key="BUTTON_SEARCH" var="valueSearchButton" />
							<wp:i18n key="TITLE_INVIA_VARIAZIONE_PREZZO" var="titleInviaPrezzo" />
							<wp:i18n key="TITLE_INVIA_MODIFICA_PRODOTTI" var="titleInviaProdotti" />
							<s:if test="%{variazioneOfferta}">
								<s:submit value="%{#attr.valueSearchButton}" title="%{#attr.titleInviaPrezzo}" cssClass="button block-ui" method="send"></s:submit>
							</s:if>
							<s:else>
								<s:submit value="%{#attr.valueSearchButton}" title="%{#attr.titleInviaProdotti}" cssClass="button block-ui" method="send"></s:submit>
							</s:else>
						</s:if>
						<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_cancel.jsp" />
					</div>
				</s:else>
			</form>
		</div>
	</s:else>
</div>