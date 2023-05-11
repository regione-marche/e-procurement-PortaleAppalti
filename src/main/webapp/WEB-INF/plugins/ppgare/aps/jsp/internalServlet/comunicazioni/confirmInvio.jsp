<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix = "fn" uri = "http://java.sun.com/jsp/jstl/functions" %>

<s:if test="%{from == 'rispondi'}">
	<c:set var="pathProcedura" value="/ExtStr2/do/FrontEnd/Comunicazioni/openPageDettaglioComunicazioneRicevuta.action"/>
</s:if>
<s:else>
	<c:if test="${sessionScope.comunicazioniEnititaProcedura == 'G1STIPULA'}">
		<c:set var="pathProcedura" value="/ExtStr2/do/FrontEnd/Contratti/dettaglioStipulaContratti.action"/>
	</c:if>
	<c:if test="${sessionScope.comunicazioniEnititaProcedura == 'APPA'}">
		<c:set var="pathProcedura" value="/ExtStr2/do/FrontEnd/ContrattiLFS/dettaglio.action"/>
	</c:if>
	<c:if test="${sessionScope.comunicazioniEnititaProcedura == null}">
		<s:if test="%{genere == 10}">
			<c:set var="pathProcedura" value="/ExtStr2/do/FrontEnd/Bandi/viewIscrizione.action"/>
		</s:if>
		<s:elseif test="%{genere == 20}">
			<c:set var="pathProcedura" value="/ExtStr2/do/FrontEnd/Cataloghi/viewIscrizione.action"/>
		</s:elseif>
		<s:elseif test="%{genere == 4}">
			<c:set var="pathProcedura" value="/ExtStr2/do/FrontEnd/Contratti/view.action"/>
		</s:elseif>
		<s:elseif test="%{genere == 11}">
			<c:set var="pathProcedura" value="/ExtStr2/do/FrontEnd/Avvisi/view.action"/>
		</s:elseif>
		<s:else>
			<c:set var="pathProcedura" value="/ExtStr2/do/FrontEnd/Bandi/view.action"/>
		</s:else>
	</c:if>	
</s:else>

<s:if test="%{soccorsoIstruttorio}" >
	<c:set var="title"><wp:i18n key="TITLE_SOCCORSO_ISTRUTTORIO_NUOVO" /></c:set>
</s:if>
<s:else>
	<c:set var="title"><wp:i18n key="TITLE_PAGE_COMUNICAZIONI_CONFERMA_INVIO" /></c:set>
</s:else>


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">
	
	<h2>${title}</h2>

	<p><wp:i18n key="LABEL_CONFERMA_INVIO" /></p>
	
	<p>	
	<s:if test="%{codiceSistema == 'LAPISOPERA'}"> 
		${LABEL_RICHIESTA_CON_ID}
	</s:if>
	<s:else>
		<wp:i18n key="LABEL_RICHIESTA_INVIATA_IL" /> <s:date name="dataInvio" format="dd/MM/yyyy HH:mm:ss" /> <s:if test="dataProtocollo != null"><wp:i18n key="LABEL_RICHIESTA_PROTOCOLLATA_IL" /> <s:property value="dataProtocollo"/></s:if>
		<s:if test="%{presentiDatiProtocollazione}"> <wp:i18n key="LABEL_CON_ANNO" /> <s:property value="annoProtocollo"/> <wp:i18n key="LABEL_E_NUMERO" /> <s:property value="numeroProtocollo"/></s:if>.
	</s:else>
	</p>

	<s:if test="%{msgErrore != null}">
		<p><wp:i18n key="LABEL_MESSAGE_WARNING" />: <s:property value="%{msgErrore}"/></p>
	</s:if>
	<div class="azioni">
		<form action='<wp:action path="${pathProcedura}" />&amp;codice=<s:property value="%{codice}"/>&amp;applicativo=<s:property value="%{applicativo}"/>&amp;idComunicazione=<s:property value="%{idComunicazione}"/>&amp;idDestinatario=<s:property value="%{idDestinatario}"/>&amp;nappal=<s:property value="%{codice2}"/>'
				method="post" class="azione">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			<div>
				<wp:i18n key="BUTTON_BACK_PREVIOUS_STEP" var="valueBackToStep" />
				<s:submit value="%{#attr.valueBackToStep}" title="%{#attr.valueBackToStep}" cssClass="button block-ui"/>
			</div>
		</form>
	</div>
</div>