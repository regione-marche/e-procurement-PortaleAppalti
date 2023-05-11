<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>

<script src='<wp:resourceURL/>static/js/ppgare/viewComunicazioni.js'></script>

<c:set var="completamentoIscrizione" value="${iscrizioneInBozza}" />

<c:set var="completamentoRinnovo" value="${rinnovoInBozza}"/>

<c:set var="completamentoAggiornamento" value="${aggiornamentoInBozza}"/>

<c:set var="rinnovoIscrizione" value="${
		dettaglio.datiGeneraliBandoIscrizione.fase != 0 && impresaAbilitataAlRinnovo && stato == 2
		&& !completamentoRinnovo}"/>


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">
    <s:set var="isAttivo" value="%{isAttiva()}"/>
    <c:set var="isAttivo"><s:property value="%{#isAttivo}" /></c:set>

    <s:if test="%{#isAttivo}">
        <c:set var="titleKey" value="TITLE_PAGE_DETTAGLIO_CATALOGO" />
        <c:set var="balloonKey" value="BALLOON_DETTAGLIO_CATALOGO" />
    </s:if>
    <s:else>
        <c:set var="titleKey" value="TITLE_PAGE_DETTAGLIO_CATALOGO_ARCHIVIATO" />
        <c:set var="balloonKey" value="BALLOON_DETTAGLIO_CATALOGO_ARCHIVIATO" />
    </s:else>

	<h2><wp:i18n key="${titleKey}" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="${balloonKey}"/>
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />

	<wp:i18n key="LABEL_MERCATO_ELETTRONICO" var="entita" />
	<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/bandi/inc/bandoIscrizione.jsp">
		<jsp:param name="entita" value="${entita}"/>
	</jsp:include>
	
	<div class="azioni">
		<c:if test="${sessionScope.currentUser != 'guest' && isAttivo}">
			<s:if test="%{impresaAbilitataAlCatalogo}">
				<form action="<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/openGestioneProdotti.action" />" method="post" class="azione">
					<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
					<div>
						<wp:i18n key="BUTTON_DETTAGLIO_CATALOGO_GESTIONE_PRODOTTI" var="valueGestionProdottiButton" />
						<wp:i18n key="TITLE_DETTAGLIO_CATALOGO_GESTIONE_PRODOTTI" var="titleGestionProdottiButton" />
						<s:submit value="%{#attr.valueGestionProdottiButton}" 
						          title="%{#attr.titleGestionProdottiButton}" 
								  cssClass="button"/>
						<input type="hidden" name="catalogo" value="${param.codice}"/>
					</div>
				</form>
			</s:if>
			<c:if test="${dettaglio.datiGeneraliBandoIscrizione.fase == 1}">
				<s:if test="%{stato == 1}">
					<form action="<wp:action path="/ExtStr2/do/FrontEnd/IscrAlbo/reloadCatalogoIscrizione.action" />" method="post" class="azione">
						<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
						<div>
							<wp:i18n key="BUTTON_DETTAGLIO_CATALOGO_COMPLETA_ISCRIZIONE" var="valueCompletaIscrizioneButton" />
							<wp:i18n key="TITLE_DETTAGLIO_CATALOGO_COMPLETA_ISCRIZIONE" var="titleCompletaIscrizioneButton" />
							<s:submit value="%{#attr.valueCompletaIscrizioneButton}" 
						          	  title="%{#attr.titleCompletaIscrizioneButton}"
							          cssClass="button"/>
							<input type="hidden" name="codice" value="${param.codice}"/>
							<input type="hidden" name="ext" value="${param.ext}" />
						</div>
					</form>
				</s:if>
				<s:elseif test="%{stato == 2}">
					<c:if test="${!completamentoAggiornamento}">
						<form action="<wp:action path="/ExtStr2/do/FrontEnd/IscrAlbo/updateCatalogoIscrizione.action" />" method="post" class="azione">
							<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
							<div>
								<wp:i18n key="BUTTON_DETTAGLIO_CATALOGO_AGGIORNA_DATI_DOCUMENTI" var="valueAggiornaDatiDocButton" />
								<wp:i18n key="TITLE_DETTAGLIO_CATALOGO_AGGIORNA_DATI_DOCUMENTI" var="titleAggiornaDatiDocButton" />
								<s:submit value="%{#attr.valueAggiornaDatiDocButton}" 
						          	  	  title="%{#attr.titleAggiornaDatiDocButton}" 
										  cssClass="button"/>
								<input type="hidden" name="codice" value="${param.codice}"/>
								<input type="hidden" name="ext" value="${param.ext}"/>
							</div>
						</form>
					</c:if>
					<c:if test="${completamentoAggiornamento}">
						<form action="<wp:action path="/ExtStr2/do/FrontEnd/IscrAlbo/updateCatalogoIscrizione.action" />" method="post" class="azione">
							<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
							<div>
								<wp:i18n key="BUTTON_DETTAGLIO_CATALOGO_COMPLETA_DATI_DOCUMENTI" var="valueCompletaDatiDocButton" />
								<wp:i18n key="TITLE_DETTAGLIO_CATALOGO_COMPLETA_DATI_DOCUMENTI" var="titleCompletaDatiDocButton" />
								<s:submit value="%{#attr.valueCompletaDatiDocButton}" 
						          	  	  title="%{#attr.titleCompletaDatiDocButton}" 
										  cssClass="button"/>
								<input type="hidden" name="codice" value="${param.codice}"/>
								<input type="hidden" name="ext" value="${param.ext}"/>
							</div>
						</form>
					</c:if>
				</s:elseif>
				<s:else>
					<form action="<wp:action path="/ExtStr2/do/FrontEnd/IscrAlbo/newCatalogoIscrizione.action" />" method="post" class="azione">
						<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
						<div>
							<wp:i18n key="BUTTON_DETTAGLIO_CATALOGO_RICHIESTA_ISCRIZIONE" var="valueRichiestaIscrButton" />
							<wp:i18n key="TITLE_DETTAGLIO_CATALOGO_RICHIESTA_ISCRIZIONE" var="titleRichiestaIscrButton" />							
							<s:submit value="%{#attr.valueRichiestaIscrButton}" 
						          	  title="%{#attr.titleRichiestaIscrButton}" 
									  cssClass="button"/>
							<input type="hidden" name="codice" value="${param.codice}"/>
							<input type="hidden" name="ext" value="${param.ext}"/>
						</div>
					</form>
				</s:else>
			</c:if>
			<c:if test="${completamentoRinnovo}">
				<form action="<wp:action path="/ExtStr2/do/FrontEnd/IscrAlbo/initCatalogoRinnovo.action" />" method="post" class="azione">
					<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
					<div>
						<wp:i18n key="BUTTON_DETTAGLIO_CATALOGO_COMPLETA_RINNOVO" var="valueCompletaRinnovoButton" />
						<wp:i18n key="TITLE_DETTAGLIO_CATALOGO_COMPLETA_RINNOVO" var="titleCompletaRinnovoButton" />
						<s:submit value="%{#attr.valueCompletaRinnovoButton}" 
						          title="%{#attr.titleCompletaRinnovoButton}" 
								  cssClass="button" />
						<input type="hidden" name="codice" value="${param.codice}"/>
						<input type="hidden" name="ext" value="${param.ext}" />
						<input type="hidden" name="tipoElenco" value="${TIPOLOGIA_ELENCO_CATALOGO}" />
					</div>
				</form>
			</c:if>
			<c:if test="${dettaglio.datiGeneraliBandoIscrizione.fase == 2 && stato == 2}">
				<c:if test="${!completamentoAggiornamento}">
					<form action="<wp:action path="/ExtStr2/do/FrontEnd/IscrAlbo/updateCatalogoDocIscrizione.action" />" method="post" class="azione">
						<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
						<div>
							<wp:i18n key="BUTTON_DETTAGLIO_CATALOGO_AGGIORNA_DOCUMENTI" var="valueAggiornaDocButton" />
							<wp:i18n key="TITLE_DETTAGLIO_CATALOGO_AGGIORNA_DOCUMENTI" var="titleAggiornaDocButton" />
							<s:submit value="%{#attr.valueAggiornaDocButton}" 
						          	  title="%{#attr.titleAggiornaDocButton}" 
									  cssClass="button"/>
							<input type="hidden" name="codice" value="${param.codice}"/>
							<input type="hidden" name="ext" value="${param.ext}"/>
						</div>
					</form>
				</c:if>
				<c:if test="${completamentoAggiornamento}">
					<form action="<wp:action path="/ExtStr2/do/FrontEnd/IscrAlbo/updateCatalogoDocIscrizione.action" />" method="post" class="azione">
						<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
						<div>
							<wp:i18n key="BUTTON_DETTAGLIO_CATALOGO_COMPLETA_DOCUMENTI" var="valueCompletaDocButton" />
							<wp:i18n key="TITLE_DETTAGLIO_CATALOGO_COMPLETA_DOCUMENTI" var="titleCompletaDocButton" />
							<s:submit value="%{#attr.valueCompletaDocButton}" 
						          	  title="%{#attr.titleCompletaDocButton}" 
									  cssClass="button"/>
							<input type="hidden" name="codice" value="${param.codice}"/>
							<input type="hidden" name="ext" value="${param.ext}"/>
						</div>
					</form>
				</c:if>
			</c:if>
			<c:if test="${rinnovoIscrizione}">
				<form action="<wp:action path="/ExtStr2/do/FrontEnd/IscrAlbo/initCatalogoRinnovo.action" />" method="post" class="azione">
					<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
					<div>
						<wp:i18n key="BUTTON_DETTAGLIO_CATALOGO_RINNOVO_ISCRIZIONE" var="valueRinnovoIscrButton" />
						<wp:i18n key="TITLE_DETTAGLIO_CATALOGO_RINNOVO_ISCRIZIONE" var="titleRinnovoIscrButton" />
						<s:submit value="%{#attr.valueRinnovoIscrButton}" 
					          	  title="%{#attr.titleRinnovoIscrButton}"
								  cssClass="button" />
						<input type="hidden" name="codice" value="${param.codice}"/>
						<input type="hidden" name="ext" value="${param.ext}" />
						<input type="hidden" name="tipoElenco" value="${TIPOLOGIA_ELENCO_CATALOGO}" />
					</div>
				</form>
			</c:if>
		</c:if>
	</div>
	
	<div class="back-link">
		<c:choose>
			<c:when test="${sessionScope.fromPage != null && sessionScope.fromPage eq 'news'}">
				<a href="<wp:action path="/ExtStr2/do/FrontEnd/Comunicazioni/${sessionScope.fromPage}.action"/>&amp;${tokenHrefParams}">
					<wp:i18n key="LINK_BACK_TO_NEWS" />
				</a>
			</c:when>
			<c:when test="${empty param.ext}">
			    <s:if test="%{#isAttivo}">
                    <c:set var="backUrl" value="/ExtStr2/do/FrontEnd/Cataloghi/listAllCataloghi.action" />
                </s:if>
                <s:else>
                    <c:set var="backUrl" value="/ExtStr2/do/FrontEnd/Cataloghi/listAllCataloghiArchiviati.action" />
                </s:else>
				<a href='<wp:action path="${backUrl}" />&amp;${tokenHrefParams}'>
					<wp:i18n key="LINK_BACK_TO_LIST" />
				</a>
			</c:when>
			<c:otherwise>
				<a href='<wp:action path="/ExtStr2/do/FrontEnd/Avvisi/view.action" />&amp;codice=<s:property value="codice"/>&amp;${tokenHrefParams}'>
					<wp:i18n key="LINK_BACK_TO_AVVISO" />
				</a>
			</c:otherwise>
		</c:choose>
	</div>
</div>