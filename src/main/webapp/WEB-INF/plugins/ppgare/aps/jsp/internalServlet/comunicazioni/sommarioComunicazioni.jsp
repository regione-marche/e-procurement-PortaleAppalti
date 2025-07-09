<%@ taglib prefix="wp" uri="aps-core.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>

<es:checkCustomization var="visInviaComunicazioni" objectId="COMUNICAZIONI" attribute="INVIARISPONDI" feature="VIS" />

<s:set var="namespace" value="'/do/FrontEnd/Bandi'"/>
<s:set var="actionName" value="'view'"/>
<s:set var="mostraRichiesteInviate" value="false"/>
<s:set var="mostraSoccorsoIstruttorio" value="false"/>
<s:set var="mostraRettifica" value="false"/>
<c:set var="codiceElenco" value=""/>

<c:choose>
	<c:when test="${ param.entita == 'G1STIPULA' }">
		<s:set var="namespace" value="'/do/FrontEnd/Contratti'"/>
		<s:set var="actionName" value="'dettaglioStipulaContratti'"/>	
	</c:when>
	<c:when test="${ param.entita == 'APPA' }">
		<s:set var="namespace" value="'/do/FrontEnd/ContrattiLFS'"/>
		<s:set var="actionName" value="'dettaglio'"/>
	</c:when>
	<c:otherwise>
		<c:choose>
			<c:when test="${param.genere eq '10'}">
				<s:set var="namespace" value="'/do/FrontEnd/Bandi'"/>
				<s:set var="actionName" value="'viewIscrizione'"/>
				<s:set var="mostraRichiesteInviate" value="true"/>
				<s:set var="mostraSoccorsoIstruttorio" value="true"/>
				<c:set var="codiceElenco" value="${dettaglio.datiGeneraliBandoIscrizione.codice}"/>
				<s:set var="mostraRettifica" value="false"/>
			</c:when>	
			<c:when test="${param.genere eq '20'}">
				<s:set var="namespace" value="'/do/FrontEnd/Cataloghi'"/>
				<s:set var="actionName" value="'viewIscrizione'"/>
				<s:set var="mostraRichiesteInviate" value="true"/>
				<s:set var="mostraSoccorsoIstruttorio" value="true"/>
				<c:set var="codiceElenco" value="${dettaglio.datiGeneraliBandoIscrizione.codice}"/>
			</c:when>
			<c:when test="${param.genere eq '4'}">
				<s:set var="namespace" value="'/do/FrontEnd/Contratti'"/>
				<s:set var="actionName" value="'view'"/>
			</c:when>
			<c:when test="${param.genere eq '11'}">
				<s:set var="namespace" value="'/do/FrontEnd/Avvisi'"/>
				<s:set var="actionName" value="'view'"/>
			</c:when>
			<c:otherwise>
				<s:set var="mostraSoccorsoIstruttorio" value="true"/>
			</c:otherwise>
		</c:choose>
	</c:otherwise>
</c:choose>

<%-- in caso di avvisi i soccorsi istruttori non sono visibili --%>
<c:if test="${param.nascondiSoccorsoIstruttorio eq '1' or numSoccorsiIstruttori eq 0}">
	<s:set var="mostraSoccorsoIstruttorio" value="false"/>
</c:if>



<div class="detail-section last-detail-section">
	<h3 class="detail-section-title">
		<span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_COMUNICAZIONI_RISERVATE" />
	</h3>
	<div class="detail-row">
	
		<c:set var="href">&amp;comunicazioniCodiceProcedura=<s:property value="codice"/>&amp;comunicazioniGenere=<s:property value="genere"/>&amp;namespace=<s:property value="%{namespace}"/>&amp;actionName=<s:property value="%{actionName}"/>&amp;idComunicazione=<s:property value="%{idComunicazione}"/>&amp;idDestinatario=<s:property value="%{idDestinatario}"/><c:if test="${! empty param.ext}">&amp;ext=${param.ext}</c:if><c:if test="${! empty param.nappal}">&amp;codice2=${param.nappal}</c:if></c:set>
		
		<ul>
			<li>
				<a href='<wp:action path="/ExtStr2/do/FrontEnd/Comunicazioni/openPageComunicazioniRicevute.action" />${href}'
				   title='Visualizza le comunicazioni ricevute (ultimi 90 giorni)'>
					<s:property value="%{numComunicazioniRicevute}" /> <wp:i18n key="LABEL_COMUNICAZIONI_RISERVATE_N_RICEVUTE" /> 
					<s:if test="%{numComunicazioniRicevuteDaLeggere > 0}">
						<strong>(<s:property value="%{numComunicazioniRicevuteDaLeggere}" /> <wp:i18n key="LABEL_COMUNICAZIONI_RISERVATE_DA_LEGGERE" />)</strong>
					</s:if>
				</a>
			</li>
			<li>
				<a href='<wp:action path="/ExtStr2/do/FrontEnd/Comunicazioni/openPageComunicazioniArchiviate.action" />${href}'
				   title='Visualizza le comunicazioni ricevute ed archiviate (oltre i 90 giorni)'>
					<s:property value="%{numComunicazioniArchiviate}" /> <wp:i18n key="LABEL_COMUNICAZIONI_RISERVATE_N_ARCHIVIATE" /> 
					<s:if test="%{numComunicazioniArchiviateDaLeggere > 0}">
						<strong>(<s:property value="%{numComunicazioniArchiviateDaLeggere}" /> <wp:i18n key="LABEL_COMUNICAZIONI_RISERVATE_DA_LEGGERE" />)</strong>
					</s:if>
				</a>
			</li>
			<c:if test="${mostraSoccorsoIstruttorio}">
				<li>
					<a href='<wp:action path="/ExtStr2/do/FrontEnd/Comunicazioni/openPageSoccorsiIstruttori.action" />${href}'
					   title=''>
					   	<s:property value="%{numSoccorsiIstruttori}" /> <wp:i18n key="LINK_AREA_PERSONALE_SOCCORSI_ISTRUTTORI" />
					</a>
				</li>
			</c:if>
			<c:if test="${visInviaComunicazioni}">
				<li>
					<a href='<wp:action path="/ExtStr2/do/FrontEnd/Comunicazioni/openPageComunicazioniInviate.action"/>${href}'
					   title='Visualizza le comunicazioni inviate'>
						<s:property value="%{numComunicazioniInviate}" /> <wp:i18n key="LABEL_COMUNICAZIONI_RISERVATE_N_INVIATE" />
					</a>
				</li>
				<li>
					<a href='<wp:action path="/ExtStr2/do/FrontEnd/Comunicazioni/initNuovaComunicazione.action"/>&amp;codice=<s:property value="codice"/>&amp;codice2=<s:property value="nappal"/>&amp;namespace=<s:property value="%{namespace}"/>&amp;actionName=<s:property value="%{actionName}"/>'>
						<wp:i18n key="LABEL_COMUNICAZIONI_NUOVA_COMUNICAZIONE" />
					</a>
				</li>
			</c:if>

		</ul>
	</div>
	
	<c:if test="${mostraRichiesteInviate}">
		<c:if test="${ stato == 2 && sessionScope.currentUser != 'guest' }">
			<h3 class="detail-section-title">
				<span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_COMUNICAZIONI_RICHIESTE_INVIATE" />
			</h3>
			<div class="detail-row">
				<ul>
					<li>
						<a href='<wp:action path="/ExtStr2/do/FrontEnd/Comunicazioni/elencoRichieste.action" />&amp;codiceElenco=${codiceElenco}' 
						   title='<wp:i18n key="TITLE_COMUNICAZIONI_RICHIESTE_INVIATE" />' >
							<wp:i18n key="TITLE_COMUNICAZIONI_RICHIESTE_INVIATE" />
						</a>
					</li>
				</ul>
			</div>
		</c:if>
	</c:if>
</div>
