<%@page import="com.agiletec.apsadmin.system.TokenInterceptor"%>
<%@ taglib prefix="wp" uri="aps-core.tld"%>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>

<es:getAppParam name="denominazioneStazioneAppaltanteUnica" var="stazAppUnica" scope = "page"/> 	

<s:if test="%{dettaglio.datiGeneraliBandoIscrizione.dataUltimoAggiornamento != null}">					
	<div class="align-right important last-update-detail">
		<wp:i18n key="LABEL_LAST_UPDATE" />	<s:date name="dettaglio.datiGeneraliBandoIscrizione.dataUltimoAggiornamento" format="dd/MM/yyyy" />
	</div>
</s:if>

<c:set var="dataPubblicazioneBando"><s:date name="dettaglio.datiGeneraliBandoIscrizione.dataPubblicazione" format="dd/MM/yyyy" /></c:set>

<div class="detail-section first-detail-section">
	<h3 class="detail-section-title"><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_STAZIONE_APPALTANTE" /></h3>

	<s:if test="maps['stazioniAppaltanti'].size() > 1">
		<div class="detail-row">
			<label><wp:i18n key="LABEL_DENOMINAZIONE" /> : </label>
			<c:choose>
				<c:when test="${! empty stazAppUnica }">
					<s:set var="stazAppUnicaToStruts">${stazAppUnica}</s:set>
					<s:property value="stazAppUnicaToStruts" />
				</c:when>
				<c:otherwise>
					<s:iterator value="maps['stazioniAppaltanti']">
								<s:if test="%{key == dettaglio.stazioneAppaltante.codice}">
									<s:property value="%{value}"/>
								</s:if>
					</s:iterator>
				</c:otherwise>
			</c:choose>
		</div>
	</s:if>

	<div class="detail-row">
		<label><wp:i18n key="LABEL_RESPONSABILE" /> ${fn:toLowerCase(param.entita)} : </label>
		<s:property value="dettaglio.stazioneAppaltante.rup" />
	</div>
</div>

<div class="detail-section">
	<h3 class="detail-section-title"><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_DATI_GENERALI" /></h3>

	<div class="detail-row">
		<label><wp:i18n key="LABEL_TITOLO_BANDO_AVVISO" /> : </label>
		<s:property value="dettaglio.datiGeneraliBandoIscrizione.oggetto" /> 
	</div>

	<div class="detail-row">
		<label>${param.entita} <wp:i18n key="LABEL_PER" /> :</label>
		<s:iterator value="maps['tipiElencoOperatori']">
			<s:if test="%{key == dettaglio.datiGeneraliBandoIscrizione.tipoElenco}">
				<s:property value="%{value}"/>
			</s:if>
		</s:iterator>	
	</div>

	<div class="detail-row">
		<label><wp:i18n key="LABEL_DATA_PUBBLICAZIONE_BANDO" /> : </label>
		<s:date name="dettaglio.datiGeneraliBandoIscrizione.dataPubblicazione" format="dd/MM/yyyy" />
	</div>

	<div class="detail-row">
		<label><wp:i18n key="LABEL_DATA_INIZIO_VALIDITA_BANDO" /> : </label>
		<s:if test="%{dettaglio.datiGeneraliBandoIscrizione.dataInizioValidita != null}">
			<wp:i18n key="LABEL_DA_DATA" /> <s:date name="dettaglio.datiGeneraliBandoIscrizione.dataInizioValidita" format="dd/MM/yyyy" />
		</s:if>
		<s:if test="%{dettaglio.datiGeneraliBandoIscrizione.dataFineValidita != null}">
			<wp:i18n key="LABEL_A_DATA" /> <s:date name="dettaglio.datiGeneraliBandoIscrizione.dataFineValidita" format="dd/MM/yyyy" />
		</s:if>
	</div>

	<div class="detail-row">
		<label><wp:i18n key="LABEL_ISCRALBO_DATA_APERTURA_ISCRIZIONI" /> : </label>
		<s:date name="dettaglio.datiGeneraliBandoIscrizione.dataInizioIscrizione" format="dd/MM/yyyy" />
		<s:if test="%{dettaglio.datiGeneraliBandoIscrizione.oraInizioIscrizione != null}">
			<wp:i18n key="LABEL_DALLE_ORE" /> <s:property value="dettaglio.datiGeneraliBandoIscrizione.oraInizioIscrizione" />
		</s:if> 
	</div>

	<div class="detail-row">
		<label><wp:i18n key="LABEL_ISCRALBO_DATA_CHIUSURA_ISCRIZIONI" /> : </label>
		<s:date name="dettaglio.datiGeneraliBandoIscrizione.dataFineIscrizione" format="dd/MM/yyyy" />
		<s:if test="%{dettaglio.datiGeneraliBandoIscrizione.oraFineIscrizione != null}">
			<wp:i18n key="LABEL_ENTRO_LE_ORE" /> <s:property value="dettaglio.datiGeneraliBandoIscrizione.oraFineIscrizione" />
		</s:if>
	</div>

	<div class="detail-row">
		<label><wp:i18n key="LABEL_RIFERIMENTO_PROCEDURA" /> : </label>
		<s:property value="dettaglio.datiGeneraliBandoIscrizione.codice" />
	</div>

	<div class="detail-row">
		<label><wp:i18n key="LABEL_STATO_GARA" /> : </label>
		<s:iterator value="maps['statiIscrAlbo']">
			<s:if test="%{key == dettaglio.datiGeneraliBandoIscrizione.stato}">
				<s:property value="%{value}"/>
			</s:if>
		</s:iterator>	
	</div>
</div>

<div class="detail-section">
	<h3 class="detail-section-title">
		<span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_CATEGORIE" /><s:if test="%{genere == 20L}"> <wp:i18n key="LABEL_ED_ARTICOLI" /></s:if>
	</h3>
	
	<div class="detail-row">
		<s:if test="%{genere == 20L}">
			<c:set var="urlCategoriePreviste"><wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/viewCategorieIscrizione.action" /></c:set>
			<c:set var="urlCategorieOperatore"><wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/viewCategorieIscrizioneOperatore.action" /></c:set>
		</s:if>
		<s:else>
			<c:set var="urlCategoriePreviste"><wp:action path="/ExtStr2/do/FrontEnd/Bandi/viewCategorieIscrizione.action" /></c:set>
			<c:set var="urlCategorieOperatore"><wp:action path="/ExtStr2/do/FrontEnd/Bandi/viewCategorieIscrizioneOperatore.action" /></c:set>
		</s:else>
		<ul>
  			<wp:i18n key="LABEL_MERCATO_ELETTRONICO" var="mepa" />
  			<%-- <wp:i18n key="LABEL_ELENCO" var="elenco" /> --%>
			<li>
				<a href='${urlCategoriePreviste}&amp;codice=<s:property value="codice"/>&amp;ext=${param.ext}' 
					title="<wp:i18n key="LABEL_ISCRALBO_VISUALIZZA_CATEGORIE_PREVISTE_BANDO" />"
					class="bkg-big go" >
				   	<c:choose>
				   		<c:when test="${param.entita == mepa}"><wp:i18n key="LABEL_ISCRALBO_CATEGORIE_MEPA"/> </c:when>
				   		<c:otherwise><wp:i18n key="LABEL_ISCRALBO_CATEGORIE_ELENCO"/> </c:otherwise>
				   	</c:choose>
				</a>
			</li>
			<c:if test="${sessionScope.currentUser != 'guest'}">
				<s:if test="%{stato == 2}">
					<li>
						<a href='${urlCategorieOperatore}&amp;codice=<s:property value="codice"/>&amp;tipoclassifica=<s:property value="tipoClassifica"/>&amp;ext=${param.ext}' 
							title="<wp:i18n key="LABEL_ISCRALBO_VISUALIZZA_CATEGORIE_ISCRIZ_BANDO" />"
							class="bkg-big go" >
							<wp:i18n key="LABEL_CATEGORIE_ISCRITTO" />
						</a>
					</li>
				</s:if>
			</c:if>
			<s:if test="%{genere == 20L}">
					<li>
						<a href='<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/openSearchArticoli.action" />&amp;codiceCatalogo=<s:property value="codice"/>&amp;ext=${param.ext}' 
							title="<wp:i18n key="LABEL_ISCRALBO_VISUALIZZA_ARTICOLI_MEPA" />"
							 class="bkg-big go" >
						    <c:choose>
						   		<c:when test="${param.entita == mepa}"><wp:i18n key="LABEL_ISCRALBO_ARTICOLI_MEPA"/> </c:when>
						   		<c:otherwise><wp:i18n key="LABEL_ISCRALBO_ARTICOLI_ELENCO"/> </c:otherwise>
						   	</c:choose>
						</a>
					</li>
			</s:if>
		</ul>
	</div>
	
	<!-- OPERATORI ECONOMICI ABILITATI A ELENCO -->	
	<s:if test="%{dettaglio.datiGeneraliBandoIscrizione.pubblicaOperatori}">
		<div class="detail-row">
			<c:set var="urlOperatoriAbilitatiElenco"><wp:action path="/ExtStr2/do/FrontEnd/Bandi/viewOperatoriIscritti.action" /></c:set>
			<ul>
				<li>
					<%-- "Mercato elettronico" --%>
					<c:set var="entita"><c:if test="${! empty param.entita}">&amp;entita=${param.entita}</c:if></c:set>
					
					<a href='${urlOperatoriAbilitatiElenco}&amp;codice=<s:property value="codice"/>&amp;ext=${param.ext}${entita}'
						class="bkg-big go" 
						title="<wp:i18n key="LABEL_ISCRALBO_VISUALIZZA_OE_ABILITATI_A" /> ${fn:toLowerCase(param.entita)}">
						<wp:i18n key="LABEL_ISCRALBO_OE_ABILITATI_A" /> ${fn:toLowerCase(param.entita)}
					</a> 
				</li>
			</ul>
		</div>
	</s:if>	

	<!-- ALTRI DOCUMENTI -->
	<div class="detail-row">
		<ul class="list">
			<li class='first last'>
				<a href="<wp:action path="/ExtStr2/do/FrontEnd/Bandi/viewAltriDocumenti.action"/>&amp;codice=${codice}&amp;ext=${param.ext}&amp;entita=${param.entita}"
				   class="bkg-big go" 
				   title='<wp:i18n key="LABEL_ALTRI_DOCUMENTI" />' >
					<wp:i18n key="LABEL_ALTRI_DOCUMENTI" />
				</a>
			</li>
		</ul>
	</div>
</div>

<div class="detail-section">
	<h3 class="detail-section-title"><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_DOCUMENTATION" /></h3>
	
	<div class="detail-row">
		<s:set var="numeroDocumentiAllegati" value="0" />
		<s:set var="elencoDocumentiAllegati" value="%{dettaglio.documento}"/>
		<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/bandi/inc/iteratorDocAllegati.jsp">
			<jsp:param name="path" value="downloadDocumentoPubblico"/>
			<jsp:param name="codice" value='${param.codice}'/>
			<jsp:param name="ext" value='${param.ext}'/>
			<jsp:param name="dataPubblicazione" value="${dataPubblicazioneBando}" />
		</jsp:include>
		<s:if test="%{#numeroDocumentiAllegati == 0}">
			<wp:i18n key="LABEL_NO_DOCUMENT" />
		</s:if>
	</div>
</div>

<s:if test="%{dettaglio.requisitoRichiesto.length > 0}">
	<div class="detail-section">
		<h3 class="detail-section-title"><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_ISCRALBO_REQUISITI_RICHIESTI_OE" /></h3>
		
		<div class="detail-row">
			<s:set var="elencoRequisitiRichiesti" value="%{dettaglio.requisitoRichiesto}"/>
			<s:include value="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/bandi/inc/iteratorReqRichiesti.jsp"/>
		</div>
	</div>
</s:if>		

<div class='detail-section <c:if test="${sessionScope.currentUser == 'guest'}">last-detail-section</c:if>'>
	<h3 class="detail-section-title"><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_COMUNICAZIONI_PUBBLICHE_SA" /></h3>
	
	<div class="detail-row">
		<s:set var="numeroComunicazioniAmministrazione" value="0" />
		<s:set var="elencoComunicazioniAmministrazione" value="%{dettaglio.comunicazione}"/>
		<s:include value="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/bandi/inc/iteratorComAmministrazione.jsp" />
		<s:if test="%{#numeroComunicazioniAmministrazione == 0}">
			<wp:i18n key="LABEL_NO_COMUNICAZIONI_PUBBLICHE_SA" />
		</s:if>
	</div>
</div>

<c:if test="${sessionScope.currentUser != 'guest'}">
	<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/comunicazioni/sommarioComunicazioni.jsp" >
		<jsp:param name="genere" value="${genere}"/>
	</jsp:include>
</c:if>
