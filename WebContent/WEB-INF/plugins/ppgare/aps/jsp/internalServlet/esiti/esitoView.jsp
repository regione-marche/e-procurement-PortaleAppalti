<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="es"
	uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld"%>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<es:checkCustomization var="visAderenti" objectId="GARE" attribute="ADERENTI" feature="VIS" />

<div class="portgare-view">

	<h2><wp:i18n key="TITLE_PAGE_DETTAGLIO_ESITO" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_DETTAGLIO_ESITO"/>
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />

	<s:if test="%{dettaglioEsito.datiGenerali.dataUltimoAggiornamento != null}">					
		<div class="align-right important last-update-detail">
			<wp:i18n key="LABEL_LAST_UPDATE" />	<s:date name="dettaglioEsito.datiGenerali.dataUltimoAggiornamento" format="dd/MM/yyyy" />
		</div>
	</s:if>		
			
	<div class="detail-section first-detail-section">
		<h3 class="detail-section-title"><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_STAZIONE_APPALTANTE" /></h3>

		<div class="detail-row">
			<label><wp:i18n key="LABEL_DENOMINAZIONE" /> : </label>
			<s:iterator value="maps['stazioniAppaltanti']">
				<s:if test="%{key == dettaglioEsito.stazioneAppaltante.codice}"><s:property value="%{value}"/></s:if>
			</s:iterator>
		</div>
		
		<div class="detail-row">
			<label><wp:i18n key="LABEL_RUP" /> : </label>
			<s:property value="dettaglioEsito.stazioneAppaltante.rup" />
		</div>
		
		<c:if test="${visAderenti}">
			<s:if test="%{dettaglioEsito.soggettiAderenti.length > 0}" >
				<div class="detail-row">
					<label><wp:i18n key="LABEL_SOGGETTI_ADERENTI" /> : </label>
		
					<div class="detail-subrow">
		 				<ul class="list">
							<s:iterator var="soggetto" value="%{dettaglioEsito.soggettiAderenti}" status="stat">
								<li class='<s:if test="%{#stat.first}">first</s:if> <s:if test="%{#stat.last}">last</s:if>'>
									<s:property value="%{#soggetto.codiceFiscale}" /> - <s:property value="#soggetto.denominazione" />
		 						</li>	 
							</s:iterator>
		 				</ul>
					</div>					
				</div>
			</s:if>
		</c:if>
	</div>
	
	<div class="detail-section">
		<h3 class="detail-section-title"><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_DATI_GENERALI" /></h3>

		<div class="detail-row">
			<label><wp:i18n key="LABEL_TITOLO" /> : </label>
			<s:property value="dettaglioEsito.datiGenerali.oggetto" /> 
			<c:if test="${! empty dettaglioEsito.datiGenerali.cig}">- <wp:i18n key="LABEL_CIG" />: <s:property value="dettaglioEsito.datiGenerali.cig" /></c:if>
			<c:if test="${! empty dettaglioEsito.datiGenerali.cup}">- <wp:i18n key="LABEL_CUP" />: <s:property value="dettaglioEsito.datiGenerali.cup" /></c:if>
		</div>

		<div class="detail-row">
			<label><wp:i18n key="LABEL_TIPO_APPALTO" /> : </label>
			<s:iterator value="maps['tipiAppalto']">
				<s:if test="%{key == dettaglioEsito.datiGenerali.tipoAppalto}"><s:property value="%{value}"/></s:if>
			</s:iterator>	
		</div>

		<div class="detail-row">
			<label><wp:i18n key="LABEL_DATA_PUBBLICAZIONE_ESITO" /> : </label>
			<s:date name="dettaglioEsito.datiGenerali.dataPubblicazione" format="dd/MM/yyyy" />
		</div>

		<div class="detail-row">
			<label><wp:i18n key="LABEL_STATO_GARA" /> : </label>
			<s:if test="%{dettaglioEsito.datiGenerali.stato != null}">
				<s:iterator value="maps['statiGara']">
					<s:if test="%{key == dettaglioEsito.datiGenerali.stato}"><s:property value="%{value}"/></s:if>
				</s:iterator>	
				<s:if test="%{dettaglioEsito.datiGenerali.esito != null}"> - <s:property value="dettaglioEsito.datiGenerali.esito" /></s:if>
			</s:if>
		</div>

		<div class="detail-row">
			<label><wp:i18n key="LABEL_RIFERIMENTO_PROCEDURA" /> : </label>
				<s:property value="dettaglioEsito.datiGenerali.codice" />
		</div>

		<div class="detail-row">
			<ul class="list">
				<li class='first last'>
					<a href="<wp:action path="/ExtStr2/do/FrontEnd/Esiti/viewLotti.action"/>&amp;codice=${param.codice}&amp;ext=${param.ext}&amp;${tokenHrefParams}"
					   class="bkg-big go"	<%-- class="bkg link" --%> 
					   title="<wp:i18n key="LABEL_VISUALIZZA_LOTTI" />" >
						<wp:i18n key="LABEL_LOTTI" />
					</a>
				</li>
			</ul>
		</div>
		
		<div class="detail-row">
			<ul class="list">
				<li class='first last'>
					<a href="<wp:action path="/ExtStr2/do/FrontEnd/Esiti/viewAttiDocumenti.action"/>&amp;codice=${param.codice}&amp;ext=${param.ext}&amp;${tokenHrefParams}"
					   class="bkg-big go"	<%-- class="bkg link" --%> 
					   title="<wp:i18n key="LABEL_VISUALIZZA_ATTI_DOC_ART29" />" >
						<wp:i18n key="LABEL_ATTI_DOC_ART29" />
					</a>
				</li>
			</ul>
		</div>
	</div>

	<div class="detail-section last-detail-section">
		<h3 class="detail-section-title"><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_DETTAGLIO_ESITO_DOCUMENTAZIONE_ESITO" /></h3>
		
		<div class="detail-row">
			<s:set var="numeroDocumentiAllegati" value="0" />
			<s:set var="elencoDocumentiAllegati" value="%{dettaglioEsito.documento}"/>
			<c:set var="dataPubblicazioneEsito"><s:date name="dettaglioEsito.datiGenerali.dataPubblicazione" format="dd/MM/yyyy" /></c:set>
			<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/bandi/inc/iteratorDocAllegati.jsp">
				<jsp:param name="path" value="downloadDocumentoPubblico"/>
				<jsp:param name="dataPubblicazione" value="${dataPubblicazioneEsito}" />
			</jsp:include>
			<div class="detail-subrow">
				<s:iterator var="lotto" value="%{dettaglioEsito.lotto}" status="status">
					<s:if test="%{#lotto.documento.length > 0}">
						<s:if test="%{dettaglioEsito.lotto.length > 1}">
							<span class="important"><wp:i18n key="LABEL_LOTTO" /> <s:property value="%{#lotto.codiceInterno}" /></span>
						</s:if>
						<s:set var="elencoDocumentiAllegati" value="%{#lotto.documento}"/>
						<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/bandi/inc/iteratorDocAllegati.jsp">
							<jsp:param name="path" value="downloadDocumentoPubblico"/>
							<jsp:param name="dataPubblicazione" value="${dataPubblicazioneEsito}" />
						</jsp:include>
					</s:if>
				</s:iterator>
			</div>
			<s:if test="%{#numeroDocumentiAllegati == 0}">
				<wp:i18n key="LABEL_NO_DOCUMENT" />
			</s:if>
		</div>
	</div>

	<div class="azioni">

		<c:if test="${empty (param.ext)}">
			<c:if test="${menuBandiVisibile && !empty (dettaglioEsito.datiGenerali.dataPubblicazioneBando)}">
				<form action="<wp:action path="/ExtStr2/do/FrontEnd/Bandi/view.action" />" method="post" class="azione">
					<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
					<div>
						<wp:i18n key="BUTTON_DETTAGLIO_ESITO_BANDO" var="valueBandoGaraButton" />
						<s:submit value="%{#attr.valueBandoGaraButton}" cssClass="button" />
						<input type="hidden" name="codice" value="${param.codice}" />
						<input type="hidden" name="ext" value="1" />
					</div>
				</form>
			</c:if>
		</c:if>
	</div>

	
	<!-- se il dettaglio e' stato aperto da una ricerca,          -->
	<!-- allora si informa la ricerca di mantenere l'ultimo stato -->
	<c:set var="last" value=''/>
	<c:if test="${sessionScope.fromPage != null && sessionScope.fromPageOwner eq 'esiti'}">
		<c:set var="last" value="&amp;last=1"/>
	</c:if>
	
	<c:choose>
		<c:when test="${empty param.ext}">
			<c:choose>
				<c:when test="${sessionScope.fromSearch}">
					<div class="back-link">
						<a href="<wp:action path="/ExtStr2/do/FrontEnd/Esiti/search.action" />&amp;last=1&amp;${tokenHrefParams}">
							<wp:i18n key="LINK_BACK_TO_SEARCH" />
						</a>
					</div>
				</c:when>
				<c:when test="${!empty sessionScope.fromPage}">
					<div class="back-link">
						<a href="<wp:action path="/ExtStr2/do/FrontEnd/Esiti/${sessionScope.fromPage}.action" />${last}&amp;${tokenHrefParams}">
							<wp:i18n key="LINK_BACK_TO_LIST" />
						</a>
					</div>
				</c:when>
			</c:choose>
		</c:when>
		<c:when test="${!empty sessionScope.fromPage && sessionScope.fromPage eq 'listSommeUrgenze'}">
			<div class="back-link">
				<a href="<wp:action path="/ExtStr2/do/FrontEnd/Bandi/listSommeUrgenze.action" />${last}&amp;${tokenHrefParams}">
					<wp:i18n key="LINK_BACK_TO_LIST" />
				</a>
			</div>
		</c:when>
		<c:otherwise>
			<div class="back-link">
				<a href="<wp:action path="/ExtStr2/do/FrontEnd/Bandi/view.action" />&amp;codice=<s:property value="codice"/>${last}&amp;${tokenHrefParams}">
					<wp:i18n key="LINK_DETTAGLIO_ESITO_BACK_TO_GARA" />
				</a>
			</div>
		</c:otherwise>
	</c:choose>
</div>