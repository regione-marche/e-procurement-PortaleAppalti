<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="wp" uri="aps-core.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp">
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<%--		
genere == 10	BANDO
genere == 20 	CATALOGO
 --%>
<s:set var="genere"><s:property value="#session.comunicazioniGenereProcedura"/></s:set> 
<s:set var="visualizzaAllegati" value="1"/>
			
<s:if test="%{#genere == 20}">
	<s:if test="%{RICHIESTA_VARIAZIONE_PRODOTTI_CATALOGO.equals(dettaglio.tipoInvio)}">
		<s:set var="visualizzaAllegati" value="0"/>
	</s:if>
	<s:if test="%{RICHIESTA_VARIAZIONE_PREZZI_E_SCADENZE_CATALOGO.equals(dettaglio.tipoInvio)}">
		<s:set var="visualizzaAllegati" value="0"/>
	</s:if>
</s:if> 		


<div class="portgare-view">

	<h2><wp:i18n key="TITLE_COMUNICAZIONI_DETTAGLIO_INVIO" /></h2>
	
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_DETTAGLIO_INVIO"/>
	</jsp:include>
	
	<fieldset>
		<legend>
			<span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_COMUNICAZIONI_DETTAGLIO" />
		</legend>
 
		<div class="fieldset-row first-row">
			<div class="label">
				<label><wp:i18n key="LABEL_COMUNICAZIONI_DATA_PRESENTAZIONE" /> : </label>
			</div>
			<div class="element">
				<span><s:date name="%{dettaglio.dataPresentazione}" format="dd/MM/yyyy HH:mm:ss" /></span>				
			</div>
		</div>

		<c:if test="${not empty dettaglio.numeroProtocollo}">
		<div class="fieldset-row">
			<div class="label">
				<label><wp:i18n key="LABEL_COMUNICAZIONI_DATI_PROTOCOLLO" /> : </label>
			</div>
			<div class="element">				
				<span>
				<wp:i18n key="LABEL_COMUNICAZIONI_PROTOCOLLO_N" /> <s:property value="%{dettaglio.numeroProtocollo}" />
				<wp:i18n key="LABEL_COMUNICAZIONI_PROTOCOLLO_DEL" /> <s:date name="%{dettaglio.dataProtocollo}" format="dd/MM/yyyy" />				
				</span>						
			</div>
		</div>
		</c:if> 

		<div class="fieldset-row">
			<div class="label">
				<label><wp:i18n key="LABEL_COMUNICAZIONI_DATA_ACQUISIZIONE" /> : </label>
			</div>
			<s:if test="%{dettaglio.stato != 5}" >
				<div class="element">
					<span><s:date name="%{dettaglio.dataAcquisizione}" format="dd/MM/yyyy HH:mm:ss" /></span>
				</div>	
			</s:if>			
		</div>

		<div class="fieldset-row">
			<div class="label">
				<label><wp:i18n key="LABEL_COMUNICAZIONI_TIPO_INVIO" /> : </label>
			</div>
			<div class="element">			
				<span>
					<%-- 
					<s:property value="%{dettaglio.tipoInvio}" />  
					--%>
					<s:iterator value="maps['tipiComunicazione']" var="m">								
						<s:if test="%{#m.key == dettaglio.tipoInvio}" >
							<s:property value="%{#m.value}" />
						</s:if>
					</s:iterator>
				</span>				
			</div>
		</div>

		<div class="fieldset-row">
			<div class="label">
				<label><wp:i18n key="LABEL_COMUNICAZIONI_STATO" /> : </label>
			</div>
			<div class="element">
				<span>
				<s:iterator value="maps['statiComunicazione']" var="m">
					<s:if test="%{#m.key == dettaglio.stato}" >
						<s:property value="%{#m.value}" />
					</s:if>
				</s:iterator>
				</span>
			</div>
		</div>
		
		<s:if test="%{#visualizzaAllegati == 1}">
			<div class="fieldset-row">
				<div class="label">
					<label><wp:i18n key="LABEL_COMUNICAZIONI_ALLEGATI" /> : </label>
				</div>
				<div class="element">
					<span>
						<s:if test="%{dettaglio.documentiAllegati.length > 0}">
							<%-- prepara il BACK LINK della pagina di visualizzazione della firma digitale --%>
							<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/backFromPageFirmaDigitale.jsp"/>
							
							<%-- <s:set var="numeroDocumentiAllegati" value="0" /> --%>
							<s:set var="elencoDocumentiAllegati" value="%{dettaglio.documentiAllegati}"/>
							<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/bandi/inc/iteratorRichiesteAllegati.jsp">
								<jsp:param name="path" value="downloadAllegatoRichiesta"/>
							</jsp:include>
						</s:if>
						<s:else>
							<wp:i18n key="LABEL_NO_ALLEGATI" />.
						</s:else>
					</span>
				</div>
			</div>
		</s:if>
		
	</fieldset>

	<div class="back-link">
		<a href='<wp:action path="/ExtStr2/do/FrontEnd/Comunicazioni/elencoRichieste.action"/>&amp;codiceElenco=<s:property value="%{codiceElenco}"/>&amp;${tokenHrefParams}' 
			title="Torna alla lista delle richieste inviate" >
			<wp:i18n key="LINK_BACK_TO_LIST" />
		</a>
	</div>

</div>