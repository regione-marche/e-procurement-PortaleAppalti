<%@ taglib prefix="wp" uri="aps-core.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<script type="text/javascript"
	src='<wp:resourceURL/>static/js/ppgare/viewComunicazioni.js'></script>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp">
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>


<%--
********************************************************************************
	dettaglio.datiGeneraliBandoIscrizione.fase
		0 = iscrizione chiusa
		1 = iscrizione aperta 
		2 = iscrizione chiusa e termine validit� non scaduto
		
	dettaglio.datiGeneraliBandoIscrizione.stato 
		1 = iscrizione aperta
		2 = iscrizione chiusa
		
	stato dell'elenco
		null = l'impresa non risulta iscritta 
		1 = l'impresa ha un'iscrizione in corso 
		2 = l'impresa ha presentato la domanda d'iscrizione 
********************************************************************************
--%>
<jsp:useBean id="now" class="java.util.Date" />

<%-- calcola periodo di validita' e inizio fine iscrizione/rinnovo --%>
<c:set var="dataInizioValidita"  />
<s:if test="%{dettaglio.datiGeneraliBandoIscrizione.dataInizioValidita != null}">
	<c:set var="dataInizioValidita" value="${dettaglio.datiGeneraliBandoIscrizione.dataInizioValidita}" />
</s:if>

<c:set var="dataFineValidita"  />
<s:if test="%{dettaglio.datiGeneraliBandoIscrizione.dataFineValidita != null}">
	<fmt:formatDate var="dataFineValidita" pattern="dd/MM/yyyy" value="${dettaglio.datiGeneraliBandoIscrizione.dataFineValidita}" /> 
	<fmt:parseDate var="dataFineValidita" pattern="dd/MM/yyyy HH:mm:ss" value="${dataFineValidita} 23:59:59" />
</s:if>

<c:set var="dataInizioIscrizione"  />
<s:if test="%{dettaglio.datiGeneraliBandoIscrizione.dataInizioIscrizione != null}">
	<fmt:formatDate var="dta" pattern="dd/MM/yyyy" value="${dettaglio.datiGeneraliBandoIscrizione.dataInizioIscrizione}"  />	
	<fmt:parseDate var="dataInizioIscrizione" pattern="dd/MM/yyyy" value="${dta}" />
	<s:if test="%{dettaglio.datiGeneraliBandoIscrizione.oraInizioIscrizione != null}">
		<fmt:parseDate var="dataInizioIscrizione" pattern="dd/MM/yyyy HH:mm">${dta} ${dettaglio.datiGeneraliBandoIscrizione.oraInizioIscrizione}</fmt:parseDate> 
	</s:if>
</s:if>

<c:set var="dataFineIscrizione"  />
<s:if test="%{dettaglio.datiGeneraliBandoIscrizione.dataFineIscrizione != null}">
	<fmt:formatDate var="dta" pattern="dd/MM/yyyy" value="${dettaglio.datiGeneraliBandoIscrizione.dataFineIscrizione}"  />
	<fmt:parseDate var="dataFineIscrizione" pattern="dd/MM/yyyy HH:mm:ss" value="${dta} 23:59:59" />
	<s:if test="%{dettaglio.datiGeneraliBandoIscrizione.oraFineIscrizione != null}">
		<fmt:parseDate var="dataFineIscrizione" pattern="dd/MM/yyyy HH:mm">${dta} ${dettaglio.datiGeneraliBandoIscrizione.oraFineIscrizione}</fmt:parseDate>
	</s:if>	
</s:if>

<c:set var="validitaScaduta" 
	value="${not empty dataFineValidita && now.time > dataFineValidita.time}" />

<c:set var="completamentoIscrizione" value="${!validitaScaduta 
		&& ((dettaglio.datiGeneraliBandoIscrizione.fase == 1 && stato == 1)
		    || iscrizioneInBozza)}" />

<c:set var="completamentoRinnovo" value="${!validitaScaduta
		&& rinnovoInBozza}"/>
		
<c:set var="aggiornamentoDatiDocumenti" value="${!validitaScaduta 
		&& dettaglio.datiGeneraliBandoIscrizione.fase == 1 && stato == 2}" />
		
<c:set var="aggiornamentoDocumenti" value="${!validitaScaduta 
		&& (dettaglio.datiGeneraliBandoIscrizione.fase == 2 
			|| dettaglio.datiGeneraliBandoIscrizione.fase == 0) 
		&& stato == 2}" />
		
<c:set var="completamentoAggiornamento" value="${aggiornamentoInBozza}"/>
		
<c:set var="richiestaIscrizione" value="${!validitaScaduta 
		&& dettaglio.datiGeneraliBandoIscrizione.fase == 1 && (stato != 1 && stato != 2)
		&& !completamentoIscrizione}" />
		
<c:set var="rinnovoIscrizione" value="${!validitaScaduta 
		&& stato == 2
		&& impresaAbilitataAlRinnovo
		&& !completamentoRinnovo}" />

	
<div class="portgare-view">

    <s:set var="isAttivo" value="%{isAttiva()}"/>
    <c:set var="isAttivo"><s:property value="%{#isAttivo}" /></c:set>

    <s:if test="%{#isAttivo}">
        <c:set var="titleKey" value="TITLE_PAGE_DETTAGLIO_ELENCO_OE" />
        <c:set var="balloonKey" value="BALLOON_DETTAGLIO_BANDO_ISCRIZIONE" />
    </s:if>
    <s:else>
        <c:set var="titleKey" value="TITLE_PAGE_DETTAGLIO_ELENCO_OE_ARCHIVIATO" />
        <c:set var="balloonKey" value="BALLOON_DETTAGLIO_BANDO_ISCRIZIONE_ARCHIVIATO" />
    </s:else>

	<h2><wp:i18n key="${titleKey}" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
        <jsp:param name="keyMsg" value="${balloonKey}" />
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />

	<wp:i18n key="LABEL_ELENCO" var="entita"/>
	<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/bandi/inc/bandoIscrizione.jsp">
		<jsp:param name="entita" value="${entita}" />
		<jsp:param name="ext" value="${param.ext}" />
	</jsp:include>
	
	<div class="azioni">
		<c:if test="${sessionScope.currentUser != 'guest' && isAttivo}">
			<c:if test="${completamentoIscrizione}">
				<form action='<wp:action path="/ExtStr2/do/FrontEnd/IscrAlbo/reloadIscrizione.action" />'
						method="post" class="azione">
					<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
					<div>
						<wp:i18n key="BUTTON_ISCRALBO_COMPLETA_ISCRIZIONE" var="valueCompletaIscrizioneButton" />
						<wp:i18n key="TITLE_ISCRALBO_COMPLETA_ISCRIZIONE_INIZIATA" var="titleCompletaIscrizioneButton" />
						<s:submit value="%{#attr.valueCompletaIscrizioneButton}"
								  title="%{#attr.titleCompletaIscrizioneButton}"
								  cssClass="button" />
						<input type="hidden" name="codice" value="${param.codice}" /> 
						<input type="hidden" name="ext" value="${param.ext}" />
					</div>
				</form>
			</c:if>
			<c:if test="${aggiornamentoDatiDocumenti}">
				<c:if test="${!completamentoAggiornamento}">
					<form action="<wp:action path="/ExtStr2/do/FrontEnd/IscrAlbo/updateIscrizione.action" />"
							method="post" class="azione">
					<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
						<div>
							<wp:i18n key="BUTTON_ISCRALBO_AGGIORNA_DATI_DOCUMENTI" var="valueAggiornaDocButton" />
							<wp:i18n key="TITLE_ISCRALBO_AGGIORNA_DATI_DOCUMENTI_ISCRIZIONE" var="titleAggiornaDocButton" />
							<s:submit value="%{#attr.valueAggiornaDocButton}"
									  title="%{#attr.titleAggiornaDocButton}"
									  cssClass="button" />
							<input type="hidden" name="codice" value="${param.codice}" /> 
							<input type="hidden" name="ext" value="${param.ext}" />
						</div>
					</form>
				</c:if>
				<c:if test="${completamentoAggiornamento}">
					<form action="<wp:action path="/ExtStr2/do/FrontEnd/IscrAlbo/updateIscrizione.action" />"
							method="post" class="azione">
						<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
						<div>
							<wp:i18n key="BUTTON_ISCRALBO_COMPLETA_DATI_DOCUMENTI" var="valueCompletaDatiDocButton" />
							<wp:i18n key="TITLE_ISCRALBO_COMPLETA_DATI_DOCUMENTI" var="titleCompletaDatiDocButton" />							
							<s:submit value="%{#attr.valueCompletaDatiDocButton}"
									  title="%{#attr.titleCompletaDatiDocButton}"
									  cssClass="button" />
							<input type="hidden" name="codice" value="${param.codice}" /> 
							<input type="hidden" name="ext" value="${param.ext}" />
						</div>
					</form>
				</c:if>
			</c:if>
			<c:if test="${completamentoRinnovo}">
				<form action="<wp:action path="/ExtStr2/do/FrontEnd/IscrAlbo/initRinnovo.action" />"
						method="post" class="azione">
					<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
					<div>
						<wp:i18n key="BUTTON_ISCRALBO_COMPLETA_RINNOVO" var="valueCompletaRinnovoButton" />
						<wp:i18n key="TITLE_ISCRALBO_COMPLETA_RINNOVO_INIZIATO" var="titleCompletaRinnovoButton" />
						<s:submit value="%{#attr.valueCompletaRinnovoButton}"
								  title="%{#attr.titleCompletaRinnovoButton}"
						          cssClass="button" />
						<input type="hidden" name="codice" value="${param.codice}" /> 
						<input type="hidden" name="ext" value="${param.ext}" /> 
						<input type="hidden" name="tipoElenco" value="${TIPOLOGIA_ELENCO_STANDARD}" />
					</div>
				</form>
			</c:if>
			<c:if test="${richiestaIscrizione}">
				<form action="<wp:action path="/ExtStr2/do/FrontEnd/IscrAlbo/newIscrizione.action" />"
						method="post" class="azione">
					<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
					<div>
						<wp:i18n key="BUTTON_ISCRALBO_RICHIESTA_ISCRIZIONE" var="valueRichiestaIscrizioneButton" />
						<wp:i18n key="TITLE_ISCRALBO_RICHIESTA_ISCRIZIONE_ALBO" var="titleRichiestaIscrizioneButton" />
						<s:submit value="%{#attr.valueRichiestaIscrizioneButton}"
								  title="%{#attr.titleRichiestaIscrizioneButton}"
								  cssClass="button" />
						<input type="hidden" name="codice" value="${param.codice}" /> 
						<input type="hidden" name="ext" value="${param.ext}" />
					</div>
				</form>
			</c:if>
			<c:if test="${aggiornamentoDocumenti}">
				<c:if test="${!completamentoAggiornamento}">
					<form action="<wp:action path="/ExtStr2/do/FrontEnd/IscrAlbo/updateDocIscrizione.action" />"
							method="post" class="azione">
						<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
						<div>
							<wp:i18n key="BUTTON_ISCRALBO_AGGIORNA_DOCUMENTI" var="valueAggiornaDocButton" />
							<wp:i18n key="TITLE_ISCRALBO_AGGIORNA_DOCUMENTI_INSERITI" var="titleAggiornaDocButton" />
							<s:submit value="%{#attr.valueAggiornaDocButton}"
								  	  title="%{#attr.titleAggiornaDocButton}"
									  cssClass="button" />
							<input type="hidden" name="codice" value="${param.codice}" /> 
							<input type="hidden" name="ext" value="${param.ext}" />
						</div>
					</form>
				</c:if>
				<c:if test="${completamentoAggiornamento}">
					<form action="<wp:action path="/ExtStr2/do/FrontEnd/IscrAlbo/updateDocIscrizione.action" />"
							method="post" class="azione">
						<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
						<div>
							<wp:i18n key="BUTTON_ISCRALBO_COMPLETAMENTO_DOCUMENTI" var="valueCompletamentoDocButton" />
							<wp:i18n key="TITLE_ISCRALBO_COMPLETAMENTO_DOCUMENTI_INSERITI" var="titlecompletamentoDocButton" />
							<s:submit value="%{#attr.valueCompletamentoDocButton}"
								  	  title="%{#attr.titlecompletamentoDocButton}"
									  cssClass="button" />
							<input type="hidden" name="codice" value="${param.codice}" /> 
							<input type="hidden" name="ext" value="${param.ext}" />
						</div>
					</form>
				</c:if>
			</c:if>
			<c:if test="${rinnovoIscrizione}">
				<form action="<wp:action path="/ExtStr2/do/FrontEnd/IscrAlbo/initRinnovo.action" />"
						method="post" class="azione">
					<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
					<div>
						<wp:i18n key="BUTTON_ISCRALBO_RINNOVO_ISCRIZIONE" var="valueRinnovoIscrizioneButton" />
						<wp:i18n key="TITLE_ISCRALBO_APRI_PAGINA_RINNOVO_ISCRIZIONE" var="titleRinnovoIscrizioneButton" />
						<s:submit value="%{#attr.valueRinnovoIscrizioneButton}"
							  	  title="%{#attr.titleRinnovoIscrizioneButton}"
								  cssClass="button" />
						<input type="hidden" name="codice" value="${param.codice}" /> 
						<input type="hidden" name="ext" value="${param.ext}" /> 
						<input type="hidden" name="tipoElenco" value="${TIPOLOGIA_ELENCO_STANDARD}" />
					</div>
				</form>
			</c:if>
		</c:if>
	</div>
	
<%-- 	
sessionScope.fromPage: ${sessionScope.fromPage}<br/>
param.ext: ${param.ext}<br/>
ext: <s:property value="%{ext}" /> <br/>
--%>

	<c:choose>
		<c:when test="${sessionScope.fromPage != null && sessionScope.fromPage eq 'news'}">
			<div class="back-link">
				<a href="<wp:action path="/ExtStr2/do/FrontEnd/Comunicazioni/${sessionScope.fromPage}.action"/>&amp;${tokenHrefParams}">
					<wp:i18n key="LINK_BACK_TO_NEWS" />
				</a>
			</div>
		</c:when>
		<c:when test="${empty param.ext}">
		    <s:if test="%{#isAttivo}">
		        <c:set var="backUrl" value="/ExtStr2/do/FrontEnd/Bandi/listAllIscrizione.action" />
		    </s:if>
		    <s:else>
                <c:set var="backUrl" value="/ExtStr2/do/FrontEnd/Bandi/listAllIscrizioneArchiviata.action" />
		    </s:else>
			<div class="back-link">
				<a href='<wp:action path="${backUrl}" />&amp;${tokenHrefParams}'>
					<wp:i18n key="LINK_BACK_TO_LIST" />
				</a>
			</div>
		</c:when>
		<c:otherwise>
			<div class="back-link">
				<a href="<wp:action path="/ExtStr2/do/FrontEnd/Avvisi/view.action" />&amp;codice=<s:property value="codice"/>&amp;${tokenHrefParams}">
					<wp:i18n key="LINK_BACK_TO_AVVISO" />
				</a>
			</div>
		</c:otherwise>
	</c:choose>
</div>