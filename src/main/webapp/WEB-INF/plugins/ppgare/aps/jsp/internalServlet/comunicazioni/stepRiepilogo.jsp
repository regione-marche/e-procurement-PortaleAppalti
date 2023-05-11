<%@ taglib prefix="wp" uri="aps-core.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<s:set var="helper" value="%{#session['nuovaComunicazione']}" />


<s:if test="%{#helper.modello > 0}" >
	<c:set var="title"><wp:i18n key="TITLE_SOCCORSO_ISTRUTTORIO_NUOVO" /></c:set>
	<%-- <c:set var="balloon" value="BALLOON_WIZ_SOCCORSO_RIEPILOGO" /> --%>
	<c:set var="balloon" value="BALLOON_WIZ_COMUNICAZIONE_RIEPILOGO" />
	<c:set var="hrefAzioni"><wp:action path="/ExtStr2/do/FrontEnd/Comunicazioni/processPageRiepilogoNuovoSoccorso.action" /></c:set>
	<s:url id="urlDownloadDocUlteriore" namespace="/do/FrontEnd/Comunicazioni" action="downloadAllegatoSoccorso" />
	<s:url id="urlDownloadDocRichiesto" namespace="/do/FrontEnd/Comunicazioni" action="downloadAllegatoSoccorsoRichiesto" />
	<s:set var="documentiRichiesti" value="%{#helper.documentiRichiesti}" />
</s:if>
<s:else>
	<c:set var="title"><wp:i18n key="TITLE_COMUNICAZIONI_NUOVA" /></c:set>
	<c:set var="balloon" value="BALLOON_WIZ_COMUNICAZIONE_RIEPILOGO" />
	<c:set var="hrefAzioni"><wp:action path="/ExtStr2/do/FrontEnd/Comunicazioni/processPageRiepilogoNuovaComunicazione.action" /></c:set>
	<s:url id="urlDownloadDocUlteriore" namespace="/do/FrontEnd/Comunicazioni" action="downloadAllegatoNuovaComunicazione" />
</s:else>

<s:set var="tipoComunicazioniCount" value="0"/>
<s:if test="%{#helper.entita eq 'APPA'}" >
	<s:iterator value="%{maps['tipologieComunicazioni']}">
		<s:set var="tipoComunicazioniCount" value="%{#tipoComunicazioniCount + 1}"/>
	</s:iterator>
</s:if>

<%--
 helper.entita =<s:property value="%{#helper.entita}" /><br/>
 tipoComunicazioniCount=<s:property value="%{#tipoComunicazioniCount}" /><br/>
id=<s:property value="%{id}" /><br/>
 --%>


<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/send_blockUI.jsp" />

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp">
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2>${title}</h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/field_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
	
	<jsp:include page="stepsComunicazione.jsp" />
	
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="${balloon}"/>
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/skip_main_content_from_buttons.jsp" />

	<fieldset>
		<legend>
			<span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_COMUNICAZIONI_TESTO" />
		</legend>

		<div class="fieldset-row first-row">
			<div class="label">
				<label><wp:i18n key="LABEL_COMUNICAZIONI_MITTENTE" /> : </label>
			</div>
			<div class="element">
				<s:property value="%{#helper.mittente}" />
			</div>
		</div>
		
		<s:if test="%{#tipoComunicazioniCount > 0}">
			<div class="fieldset-row ">
				<div class="label">
					<label for="tipoRichiesta"><wp:i18n key="LABEL_COMUNICAZIONI_TIPO_RICHIESTA" /> : </label>
				</div>
				<div class="element">
					<s:iterator value="maps['tipologieComunicazioni']">
						<s:if test="%{key == #helper.tipoRichiesta}">
							<s:property value="%{value}"/> 
						</s:if>
					</s:iterator>
				</div>
			</div>
		</s:if>

		<div class="fieldset-row">
			<div class="label">
				<label><wp:i18n key="LABEL_COMUNICAZIONI_OGGETTO" /> : </label>
			</div>
			<div class="element">
				<s:property value="%{#helper.oggetto}" />
			</div>
		</div>

		<div class="fieldset-row last-row">
			<div class="label">
				<label><wp:i18n key="LABEL_COMUNICAZIONI_TESTO" /> : </label>
			</div>
			<div class="element">
				<s:property value="%{#helper.testo}" />
			</div>
		</div>
	</fieldset>

	<fieldset>
		<legend>
			<span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_COMUNICAZIONI_ALLEGATI" />
		</legend>

		<div class="fieldset-row first-row last-row">
			<s:set name="countRich" value="%{0}" />
			<s:if test="%{#helper.documenti.docRichiestiId != null}" >
				<s:set name="countRich" value="%{#helper.documenti.docRichiestiId.size()}" />
			</s:if>
			
			<s:set name="countUlt" value="%{0}" />
			<s:if test="%{#helper.documenti.docUlterioriDesc != null}" >
				<s:set name="countUlt" value="%{#helper.documenti.docUlterioriDesc.size()}" />
			</s:if>
			
			<div class="label">
				<label><wp:i18n key="LABEL_DOCUMENTI_INSERITI" /> 
					<s:if test="%{#countRich + #countUlt > 0}">(<s:property value="%{#countRich + #countUlt}" />)</s:if> :
				</label>
			</div>
			<div class="element">
				<s:if test="%{#countRich + #countUlt > 0}">
					<wp:i18n key="LABEL_SCARICA_ALLEGATO" var="valueScaricaAllegato" />
					<wp:i18n key="TITLE_SCARICA_ALLEGATO" var="titleScaricaAllegato" />
					
					<ul class="list">
					
						<%-- documenti richiesti --%>
						<s:if test="%{#helper.documenti.docRichiesti.size() > 0}">	
							<s:iterator value="documentiRichiesti" var="doc">
								<s:iterator	value="#helper.documenti.docRichiestiId" var="idDoc" status="stat">
									<s:if test="%{#idDoc == #doc.id}">
										<li
											class='<s:if test="%{#stat.first}">first</s:if> <s:if test="%{#stat.last && #countUlt le 0}">last</s:if>' >
											<c:choose>
												<c:when test="${skin == 'highcontrast' || skin == 'text'}">
													<s:a href="%{#urlDownloadDocRichiesto}?id=%{#stat.index}&amp;codice=%{codice}" title="%{#attr.titleScaricaAllegato}">
														<s:property value="%{#doc.nome}" /> (<s:property value="%{#helper.documenti.docRichiestiFileName.get(#stat.index)}" />)
													</s:a>
													<%-- <span>(<s:property value="%{#helper.documenti.docRichiestiSize.get(#stat.index)}"/> KB)</span> --%>
												</c:when>
												<c:otherwise>
													<s:a href="%{#urlDownloadDocRichiesto}?id=%{#stat.index}&amp;codice=%{codice}" title='%{#attr.titleScaricaAllegato}' cssClass="bkg download">
														<s:property value="%{#doc.nome}" /> (<s:property value="%{#helper.documenti.docRichiestiFileName.get(#stat.index)}" />)
													</s:a>
													<%-- <span>(<s:property value="%{#helper.documenti.docRichiestiSize.get(#stat.index)}"/> KB)</span> --%>
												</c:otherwise>
											</c:choose>
										</li>
									</s:if>
								</s:iterator>
							</s:iterator>
						</s:if>
						
						<%-- documenti ulteriori --%>
						<s:if test="%{#helper.documenti.docUlteriori.size() > 0}">
							<s:iterator value="%{#helper.documenti.docUlterioriFileName}" var="documento" status="stat">
								<li
									class='<s:if test="%{#stat.first && #countRich le 0}">first</s:if> <s:if test="%{#stat.last}">last</s:if>'>
									<c:choose>
										<c:when test="${skin == 'highcontrast' || skin == 'text'}">
											<s:a href="%{#urlDownloadDocUlteriore}?id=%{#stat.index}&amp;%{#attr.tokenHrefParams}" title="%{#attr.titleScaricaAllegato}">
												<s:property value="%{#documento}" />
											</s:a>
										</c:when>
										<c:otherwise>
											<s:a href="%{#urlDownloadDocUlteriore}?id=%{#stat.index}&amp;%{#attr.tokenHrefParams}" title="%{#attr.titleScaricaAllegato}" cssClass="bkg download">
												<s:property value="%{#documento}" />
											</s:a>
										</c:otherwise>
									</c:choose>
								</li>
							</s:iterator>
						</s:if>		
						
					</ul>
				</s:if>				
				<s:else>
					<wp:i18n key="LABEL_NO_DOCUMENT" />.
				</s:else>
			</div>
		</div>
	</fieldset>
	
	<div class="azioni">
		<form action="${hrefAzioni}" method="post">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			<div>
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_previous.jsp" />
				<s:if test="!sendBlocked">
					<wp:i18n key="LABEL_COMUNICAZIONI_INVIA_COMUNICAZIONE" var="valueInviaComuniczione" />
					<wp:i18n key="TITLE_COMUNICAZIONI_INVIA_COMUNICAZIONE" var="titleInviaComuniczione" />
					<s:submit value="%{#attr.valueInviaComuniczione}" title="%{#attr.titleInviaComuniczione}" cssClass="button block-ui" method="send"></s:submit>
				</s:if>
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_cancel.jsp" />
			</div>
		</form>
	</div>
</div>