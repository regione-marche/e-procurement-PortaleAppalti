<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld"%>


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<es:checkCustomization var="visAderenti" objectId="GARE" attribute="ADERENTI" feature="VIS" />

<div class="portgare-view">

	<h2><wp:i18n key="TITLE_PAGE_DETTAGLIO_AVVISO" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
			<jsp:param name="keyMsg" value="BALLOON_DETTAGLIO_AVVISO"/>
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />

	<s:if test="%{dettaglioAvviso.datiGenerali.dataUltimoAggiornamento != null}">					
		<div class="align-right important last-update-detail">
			<wp:i18n key="LABEL_LAST_UPDATE" />	<s:date name="dettaglioAvviso.datiGenerali.dataUltimoAggiornamento" format="dd/MM/yyyy" />
		</div>
	</s:if> 		

	<div class="detail-section first-detail-section">
		<h3 class="detail-section-title"><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_STAZIONE_APPALTANTE" /></h3>
		
		<div class="detail-row">
			<label><wp:i18n key="LABEL_DENOMINAZIONE" /> : </label>
			<s:iterator value="maps['stazioniAppaltanti']">
			<s:if test="%{key == dettaglioAvviso.stazioneAppaltante.codice}"><s:property value="%{value}"/></s:if>
			</s:iterator>
		</div>
		
		<div class="detail-row">
			<label><wp:i18n key="LABEL_RUP" /> : </label>
			<s:property value="dettaglioAvviso.stazioneAppaltante.rup" />
		</div>
	
		<c:if test="${visAderenti}">
			<s:if test="%{dettaglioAvviso.soggettiAderenti.length > 0}" >
				<div class="detail-row">
					<label><wp:i18n key="LABEL_SOGGETTI_ADERENTI" /> : </label>
		
					<div class="detail-subrow">
		 				<ul class="list">
							<s:iterator var="soggetto" value="%{dettaglioAvviso.soggettiAderenti}" status="stat">
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
			<label><wp:i18n key="LABEL_TIPO_AVVISO" /> : </label>
			<s:iterator value="maps['tipiAvviso']">
			<s:if test="%{key == dettaglioAvviso.datiGenerali.tipoAvviso}"><s:property value="%{value}"/></s:if>
			</s:iterator>	
		</div>
		
		<div class="detail-row">
			<label><wp:i18n key="LABEL_TITOLO" /> : </label>
			<s:property value="dettaglioAvviso.datiGenerali.oggetto" />
		</div>
	
		<div class="detail-row">
			<label><wp:i18n key="LABEL_AVVISO_PER" /> : </label>
			<s:iterator value="maps['tipiElencoOperatori']">
			<s:if test="%{key == dettaglioAvviso.datiGenerali.tipoAppalto}"><s:property value="%{value}"/></s:if>
			</s:iterator>	
		</div>

		<div class="detail-row">
			<label><wp:i18n key="LABEL_DATA_PUBBLICAZIONE_AVVISO" /> : </label>
			<s:date name="dettaglioAvviso.datiGenerali.dataPubblicazione" format="dd/MM/yyyy" />
		</div>

		<div class="detail-row">
			<label><wp:i18n key="LABEL_DATA_SCADENZA_AVVISO" /> : </label>
			<s:date name="dettaglioAvviso.datiGenerali.dataScadenza" format="dd/MM/yyyy" />
			<s:if test="%{dettaglioAvviso.datiGenerali.oraScadenza != null}">
			 <wp:i18n key="LABEL_ENTRO_LE_ORE" /> <s:property value="dettaglioAvviso.datiGenerali.oraScadenza" />
			 </s:if> 
		</div>

		<div class="detail-row">
			<label><wp:i18n key="LABEL_RIFERIMENTO_PROCEDURA" /> : </label>
				<s:property value="dettaglioAvviso.datiGenerali.codice" />
		</div>
		
		<!-- ALTRI DOCUMENTI -->
		<div class="detail-row">
			<ul class="list">
				<li class='first last'>
					<a href="<wp:action path="/ExtStr2/do/FrontEnd/Avvisi/viewAltriDocumenti.action"/>&amp;codice=${codice}&amp;ext=${param.ext}&amp;${tokenHrefParams}"
					   class="bkg-big go" 
					   title='<wp:i18n key="LABEL_ALTRI_DOCUMENTI" />' >
						<wp:i18n key="LABEL_ALTRI_DOCUMENTI" />
					</a>
				</li>
			</ul>
		</div>
	</div>

	<div class="detail-section last-detail-section">
		<h3 class="detail-section-title"><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_DOCUMENTATION" /></h3>
		
		<div class="detail-row">
			<s:set var="numeroDocumentiAllegati" value="0" />
			<s:set var="elencoDocumentiAllegati" value="%{dettaglioAvviso.documento}"/>
			<c:set var="dataPubblicazioneAvviso"><s:date name="dettaglioAvviso.datiGenerali.dataPubblicazione" format="dd/MM/yyyy" /></c:set>
			<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/bandi/inc/iteratorDocAllegati.jsp">
				<jsp:param name="path" value="downloadDocumentoPubblico"/>
				<jsp:param name="dataPubblicazione" value="${dataPubblicazioneAvviso}" />
			</jsp:include>
			<s:if test="%{#numeroDocumentiAllegati == 0}">
				<wp:i18n key="LABEL_NO_DOCUMENT" />
			</s:if>
		</div>
	</div>


	<div class='detail-section <c:if test="${sessionScope.currentUser == 'guest'}">last-detail-section</c:if>'>
		<h3 class="detail-section-title"><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_COMUNICAZIONI_PUBBLICHE_SA" /></h3>
		
		<div class="detail-row">
			<s:set var="numeroComunicazioniAmministrazione" value="0" />
			<s:set var="elencoComunicazioniAmministrazione" value="%{dettaglioAvviso.comunicazione}"/>
			<s:include value="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/bandi/inc/iteratorComAmministrazione.jsp" />
			<s:if test="%{#numeroComunicazioniAmministrazione == 0}">
				<wp:i18n key="LABEL_NO_COMUNICAZIONI_PUBBLICHE_SA" />
			</s:if>
		</div>
	</div>

	<%-- comunicazioni ricevute, archiviate, inviate, invia nuova comunicazione --%>
	<c:if test="${sessionScope.currentUser != 'guest'}">
		<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/comunicazioni/sommarioComunicazioni.jsp" >
			<jsp:param name="genere" value="${genere}"/>
			<jsp:param name="nascondiSoccorsoIstruttorio" value="1"/>
		</jsp:include>
	</c:if>
		
	
	<c:if test="${menuElenchiVisibile && dettaglioAvviso.datiGenerali.tipoAvviso eq '0'}">
		<div class="azioni">
			<form action="<wp:action path="/ExtStr2/do/FrontEnd/Bandi/viewIscrizione.action" />" method="post" class="azione">
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
				<div>
					<wp:i18n key="BUTTON_DETTAGLIO_AVVISO_BANDO_ISCRIZIONE_ELENCO" var="valueBandoIscrizioneButton" />
					<s:submit value="%{#attr.valueBandoIscrizioneButton}" cssClass="button" />
					<input type="hidden" name="codice" value="${param.codice}" />
					<input type="hidden" name="ext" value="1" />
				</div>
			</form>
		</div>
	</c:if>
		
	<!-- se il dettaglio e' stato aperto da una ricerca,          -->
	<!-- allora si informa la ricerca di mantenere l'ultimo stato -->
	<c:set var="last" value=''/>
	<c:if test="${sessionScope.fromPage != null && sessionScope.fromPageOwner eq 'avvisi'}">
		<c:set var="last" value="&amp;last=1"/>
	</c:if>
	
	<c:set var="dettaglioComunicazioneInviata" value="${sessionScope.fromPage != null && sessionScope.fromPage eq 'openPageDettaglioComunicazioneInviata'}"/>
	<c:set var="dettaglioComunicazioneRicevuta" value="${sessionScope.fromPage != null && sessionScope.fromPage eq 'openPageDettaglioComunicazioneRicevuta'}"/>
	
	<c:choose>
		<c:when test="${sessionScope.fromSearch}">
			<div class="back-link">
				<a href="<wp:action path="/ExtStr2/do/FrontEnd/Avvisi/search.action" />&amp;last=1&amp;${tokenHrefParams}">
					<wp:i18n key="LINK_BACK_TO_SEARCH" />
				</a>
			</div>
		</c:when>
		<c:when test="${dettaglioComunicazioneInviata || dettaglioComunicazioneRicevuta}">
			<div class="back-link">
				<%-- 
				<a href="<wp:action path="/ExtStr2/do/FrontEnd/Comunicazioni/${sessionScope.fromPage}.action"/>&amp;fromAvviso=1&amp;idComunicazione=${idComunicazione}&amp;idDestinatario=${idDestinatario}&amp;${tokenHrefParams}">
				--%>
				<a href="<wp:action path="/ExtStr2/do/FrontEnd/Comunicazioni/${sessionScope.fromPage}.action"/>&amp;fromAvviso=1&amp;${tokenHrefParams}">
					<c:choose>
						<c:when test="${dettaglioComunicazioneInviata}">
							<wp:i18n key="LINK_BACK_TO_COMMUNICATIONS_SENT" />
						</c:when>
						<c:when test="${dettaglioComunicazioneRicevuta}">
							<wp:i18n key="LINK_BACK_TO_COMMUNICATIONS_RECEIVED" />
						</c:when>
					</c:choose>
				</a>
			</div>
		</c:when>
		<c:otherwise>
			<div class="back-link">
				<a href="<wp:action path="/ExtStr2/do/FrontEnd/Avvisi/${sessionScope.fromPage}.action" />${last}&amp;${tokenHrefParams}">
					<wp:i18n key="LINK_BACK_TO_LIST" />
				</a>
			</div>
		</c:otherwise>
	</c:choose>
	
</div>