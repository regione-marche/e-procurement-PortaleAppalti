<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="wp" uri="aps-core.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>

<es:checkCustomization var="visSA" objectId="COMUNICAZIONI" attribute="STAZIONEAPPALTANTE" feature="VIS" />
<es:checkCustomization var="visOE" objectId="COMUNICAZIONI" attribute="OPERATORE" feature="VIS" />

<s:if test="%{tipoComunicazione == 'ricevute'}">
	<c:set var="path" value="/ExtStr2/do/FrontEnd/Comunicazioni/openPageComunicazioniRicevute.action" />
	<c:set var="pathDettaglio" value="/ExtStr2/do/FrontEnd/Comunicazioni/openPageDettaglioComunicazioneRicevuta.action" />
	<c:set var="title"><wp:i18n key="TITLE_COMUNICAZIONI_LISTA_RICEVUTE" /></c:set>
	<c:set var="balloon" value="BALLOON_COMUNICAZIONI_RICEVUTE" />
</s:if>
<s:elseif test="%{tipoComunicazione == 'soccorsiIstruttori'}">
	<c:set var="path" value="/ExtStr2/do/FrontEnd/Comunicazioni/openPageSoccorsiIstruttori.action" />
	<c:set var="pathDettaglio" value="/ExtStr2/do/FrontEnd/Comunicazioni/openPageDettaglioSoccorsoIstruttorio.action" />
	<c:set var="title"><wp:i18n key="TITLE_LISTA_SOCCORSI_ISTRUTTORI" /></c:set>
	<c:set var="balloon" value="BALLOON_SOCCORSI_ISTRUTTORI" />
</s:elseif> 
<s:else>
	<c:set var="path" value="/ExtStr2/do/FrontEnd/Comunicazioni/openPageComunicazioniArchiviate.action" />
	<c:set var="pathDettaglio" value="/ExtStr2/do/FrontEnd/Comunicazioni/openPageDettaglioComunicazioneRicevuta.action" />
	<c:set var="title"><wp:i18n key="TITLE_COMUNICAZIONI_LISTA_ARCHIVIATE" /></c:set>
	<c:set var="balloon" value="BALLOON_COMUNICAZIONI_ARCHIVIATE" />
</s:else>


<s:if test="%{entita == 'G1STIPULA'}">
	<c:set var="pathProcedura" value="/ExtStr2/do/FrontEnd/Contratti/dettaglioStipulaContratti.action"/>
</s:if>
<s:if test="%{entita == 'APPA'}">
	<c:set var="pathProcedura" value="/ExtStr2/do/FrontEnd/ContrattiLFS/dettaglio.action"/>
</s:if>
<s:if test="%{entita == null}">
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
</s:if>


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp">
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<c:set var="showRiferimento" value="${genere == null && entita == null}"/>

<div class="portgare-list">
	
	<h2>${title}</h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="${balloon}" />
	</jsp:include>

	<s:if test="%{comunicazioni.dati.size > 0}">
		<div class="list-summary">
			<wp:i18n key="SEARCH_RESULTS_INTRO" />
			<s:property value="model.iTotalDisplayRecords" />
			<wp:i18n key="SEARCH_RESULTS_OUTRO" />.
		</div>
		<form action="<wp:action path="${path}"/>" method="post">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			
			<table class="info-table">
				<thead>
					<tr>
						<c:if test="${showRiferimento == true}">
							<th scope="col"><wp:i18n key="LABEL_COMUNICAZIONI_RIFERIMENTO" /></th>
						</c:if>
						<s:if test="%{#attr.visSA}">
							<th scope="col"><wp:i18n key="LABEL_COMUNICAZIONI_MITTENTE" /></th>
						</s:if>
						<s:if test="%{#attr.visOE}">
							<th scope="col"><wp:i18n key="LABEL_COMUNICAZIONI_DESTINATARIO" /></th>
						</s:if>
						<th scope="col"><wp:i18n key="LABEL_COMUNICAZIONI_OGGETTO" /></th>
						<th scope="col"><wp:i18n key="LABEL_COMUNICAZIONI_RICEVUTO_IL" /></th>
						<th scope="col"><wp:i18n key="LABEL_COMUNICAZIONI_STATO" /></th>
					</tr>
				</thead>
				<tbody>
					<s:iterator var="riga" value="comunicazioni.dati" status="status">
					
						<c:set var="href"> 
							&amp;idComunicazione=<s:property value="%{idComunicazione}"/>
							<c:if test="${! empty param.ext}">&amp;ext=${param.ext}</c:if>
							&amp;idDestinatario=<s:property value="%{idDestinatario}"/>
							&amp;applicativo=<s:property value="%{applicativo}"/>
						</c:set>
						
						<tr>
							<c:if test="${showRiferimento == true}">
								<td>
									<s:property value="%{codice}" />
								</td>
							</c:if>
							<s:if test="%{#attr.visSA}">
								<td>
									<s:property value="%{mittente}" />
								</td>
							</s:if>
							<s:if test="%{#attr.visOE}">
								<td
									<s:if test="dataLettura == null">class="detail-link-content"</s:if>>
									<s:property value="%{destinatario}" />
								</td>
							</s:if>
							<td
								<s:if test="dataLettura == null">class="detail-link-content"</s:if> >
								<a href='<wp:action path="${pathDettaglio}"/>${href}'>
									<s:property value="%{oggetto}" />
								</a>
							</td>
							<td
								<s:if test="dataLettura == null">class="detail-link-content"</s:if>>
								<s:date name="dataInvio.time" format="dd/MM/yyyy HH:mm:ss" />
							</td>
							<td
								<s:if test="dataLettura == null">class="detail-link-content"</s:if>>
								<s:if test="dataLettura.time != null">
									<wp:i18n key="LABEL_COMUNICAZIONI_LETTA" />
								</s:if>
								<s:else>
									<strong><wp:i18n key="LABEL_COMUNICAZIONI_DA_LEGGERE" /></strong>
								</s:else>
							</td>
						</tr>
					</s:iterator>
				</tbody>
			</table>
			
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/pagination.jsp"></jsp:include>
			
			<s:hidden name="comunicazioniCodiceProcedura" value="%{comunicazioniCodiceProcedura}"/> 
			<s:hidden name="genere" value="%{comunicazioniGenere}"/> 
			<s:hidden name="namespace" value="%{namespace}"/>
			<s:hidden name="actionName" value="%{actionName}"/>
		</form>
	</s:if>
	<s:else>
		<%-- Accessibility Fix Criterion 3.2.2: insert an invisible "submit" button as workaraound --%>
		<input disabled="disabled" type="submit" style="display:none;"/>
		<wp:i18n key="LABEL_NO_COMUNICAZIONI" />.
	</s:else>
	
	<div class="back-link">
		<s:if test="%{genere != null || entita != null}">
			<c:set var="href"> 
				&amp;codice=<s:property value="%{comunicazioniCodiceProcedura}"/>
				<c:if test="${! empty param.ext}">&amp;ext=${param.ext}</c:if>
				<c:if test="${! empty codice2}">&amp;nappal=${codice2}</c:if>
				&amp;idComunicazione=<s:property value="%{idComunicazione}"/>&amp;idDestinatario=<s:property value="%{idDestinatario}"/>
			</c:set>
			
			<a href='<wp:action path="${pathProcedura}" />${href}'>
				<wp:i18n key="LINK_BACK_TO_PROCEDURE" />
			</a>
		</s:if>
		<s:else>
			<a href="ppcommon_area_personale.wp">
				<wp:i18n key="LINK_BACK_TO_AREAPERSONALE" />
			</a>
		</s:else>
	</div>
</div>