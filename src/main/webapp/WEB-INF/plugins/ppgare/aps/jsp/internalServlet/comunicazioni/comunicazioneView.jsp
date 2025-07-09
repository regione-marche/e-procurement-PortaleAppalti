<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="wp" uri="aps-core.tld"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<es:checkCustomization var="visInviaComunicazioni" objectId="COMUNICAZIONI" attribute="INVIARISPONDI" feature="VIS" />
<es:checkCustomization var="visSA" objectId="COMUNICAZIONI" attribute="STAZIONEAPPALTANTE" feature="VIS" />
<es:checkCustomization var="visOE" objectId="COMUNICAZIONI" attribute="OPERATORE" feature="VIS" />

<s:set var="isRicevuteArchiviateSoccorsi" value="%{tipo == 'ricevute' || tipo=='archiviate' || tipo == 'soccorsiIstruttori'}"/>
<s:set var="showMittente" value="%{(#isRicevuteArchiviateSoccorsi && #attr.visSA) 
								   || (!#isRicevuteArchiviateSoccorsi && #attr.visOE)}"/>
<s:set var="showDestinatario" value="%{(#isRicevuteArchiviateSoccorsi && #attr.visOE)
					   			   || (!#isRicevuteArchiviateSoccorsi && #attr.visSA)}"/>

<s:set var="doEscape" value="%{tipo != 'ricevute'}"/>

<s:set var="isContrattoLFS" value="%{'APPA'.equalsIgnoreCase(comunicazione.entita)}"/>
<s:set var="isStipula" value="%{'G1STIPULA'.equalsIgnoreCase(comunicazione.entita)}"/>
<s:set var="isGenericaRiservata" value="%{'HOMEPG'.equalsIgnoreCase(comunicazione.entita)}"/>

<s:set var="isRispostaRettifica" value="%{comunicazione.modello == 11 || comunicazione.modello == 12 
										  || comunicazione.modello == 16 || comunicazione.modello == 17}"/>
<s:set var="isAccettazioneRettifica" value="%{comunicazione.modello == 11 || comunicazione.modello == 16}"/>

<s:if test="%{tipo == 'ricevute'}">
	<c:set var="title"><wp:i18n key="TITLE_COMUNICAZIONI_DETTAGLIO_RICEVUTA" /></c:set>
	<c:set var="balloon" value="BALLOON_DETT_COMUNICAZIONE_RICEVUTA" />
	<c:set var="path" value="/ExtStr2/do/FrontEnd/Comunicazioni/openPageComunicazioniRicevute.action" />
</s:if>
<s:elseif test="%{tipo == 'archiviate'}">
	<c:set var="title"><wp:i18n key="TITLE_COMUNICAZIONI_DETTAGLIO_RICEVUTA" /></c:set>
	<c:set var="balloon" value="BALLOON_DETT_COMUNICAZIONE_RICEVUTA" />
	<c:set var="path" value="/ExtStr2/do/FrontEnd/Comunicazioni/openPageComunicazioniArchiviate.action" />
</s:elseif>
<s:elseif test="%{tipo == 'soccorsiIstruttori'}">
	<c:set var="title"><wp:i18n key="TITLE_SOCCORSO_ISTRUTTORIO" /></c:set>
	<c:set var="balloon" value="BALLOON_SOCCORSO_ISTRUTTORIO" />
	<c:set var="path" value="/ExtStr2/do/FrontEnd/Comunicazioni/openPageSoccorsiIstruttori.action" />
</s:elseif>
<s:else>
	<c:set var="title"><wp:i18n key="TITLE_COMUNICAZIONI_DETTAGLIO_INVIATA" /></c:set>
	<c:set var="balloon" value="BALLOON_DETT_COMUNICAZIONE_INVIATA" />
	<c:set var="path" value="/ExtStr2/do/FrontEnd/Comunicazioni/openPageComunicazioniInviate.action" />
	<c:set var="idprg" value="PA"/>
</s:else>

<s:if test="%{comunicazione.entita == 'APPA' || comunicazione.entita == 'G1STIPULA'}">
	<s:if test="%{comunicazione.entita == 'APPA'}">
		<c:set var="pathProcedura" value="/ExtStr2/do/FrontEnd/ContrattiLFS/dettaglio.action"/>
		<c:set var="pageProcedura" value="ppgare_contratti_lfs_lista" />
	</s:if>
	<s:if test="%{comunicazione.entita == 'G1STIPULA'}">
		<c:set var="pathProcedura" value="/ExtStr2/do/FrontEnd/Contratti/dettaglioStipulaContratti.action"/>
		<c:set var="pageProcedura" value="ppcommon_contracts" />
	</s:if>
</s:if>
<s:elseif test="%{comunicazione.entita == 'G1STIPULA'}">
	<c:set var="pathProcedura" value="/ExtStr2/do/FrontEnd/Contratti/dettaglioStipulaContratti.action"/>
	<c:set var="pageProcedura" value="ppcommon_contracts" />
</s:elseif>
<s:else>
	<s:if test="%{genere == 10}">
		<c:set var="pathProcedura" value="/ExtStr2/do/FrontEnd/Bandi/viewIscrizione.action"/>
		<c:set var="pageProcedura" value="ppgare_oper_ec_bandi_avvisi" />
	</s:if>
	<s:elseif test="%{genere == 20}">
		<c:set var="pathProcedura" value="/ExtStr2/do/FrontEnd/Cataloghi/viewIscrizione.action"/>
		<c:set var="pageProcedura" value="ppgare_cataloghi_bandi_avvisi" />
	</s:elseif>
	<s:elseif test="%{genere == 4}">
		<c:set var="pathProcedura" value="/ExtStr2/do/FrontEnd/Contratti/view.action"/>
		<c:set var="pageProcedura" value="ppgare_contratti_ordini_lista" />
	</s:elseif>
	<s:elseif test="%{genere == 11}">
		<c:set var="pathProcedura" value="/ExtStr2/do/FrontEnd/Avvisi/view.action"/>
		<c:set var="pageProcedura" value="ppgare_avvisi_lista" />
	</s:elseif>
	<s:else>
		<c:set var="pathProcedura" value="/ExtStr2/do/FrontEnd/Bandi/view.action"/>
		<c:set var="pageProcedura" value="ppgare_bandi_lista" />
	</s:else>
</s:else>


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp">
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2>${title}</h2>
	
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="${balloon}"/>
	</jsp:include>	
	
	<fieldset>
		<legend>
			<span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_COMUNICAZIONI_DETTAGLIO" />
		</legend>

		
		<s:if test="%{#showMittente}">
			<div class="fieldset-row first-row">
				<div class="label">
					<label><wp:i18n key="LABEL_COMUNICAZIONI_MITTENTE" /> : </label>
				</div>
				<div class="element">
					<span><s:property value="%{comunicazione.mittente}" escape="<s:property value='%{doEscape}'/>" /></span>
				</div>
			</div>
		</s:if>
		
		<s:if test="%{#showDestinatario}">
			<div class="fieldset-row <s:if test='%{!#showMittente}'>first-row</s:if>">
				<div class="label">
					<label><wp:i18n key="LABEL_COMUNICAZIONI_DESTINATARIO" /> : </label>
				</div>
				<div class="element">
					<span><s:property value="%{comunicazione.destinatario}" escape="<s:property value='%{doEscape}'/>" /></span>
				</div>
			</div>
		</s:if>
		
		<div class="fieldset-row">
			<div class="label">
				<label><wp:i18n key="LABEL_COMUNICAZIONI_OGGETTO" /> : </label>
			</div>
			<div class="element">
				<span><s:property value="%{comunicazione.oggetto}" escape="<s:property value='%{doEscape}'/>" /></span>
			</div>
		</div>

		<div class="fieldset-row">
			<div class="label">
				<label><wp:i18n key="LABEL_COMUNICAZIONI_TESTO" /> : </label>
			</div>
			<div class="element">
				<span>
					<s:set var="txt" value="%{comunicazione.testo}"/>
					<s:if test="%{#txt != null && #txt.trim().length() > 0}">
						<s:property value="%{#txt}" escape="<s:property value='%{doEscape}'/>" />
					</s:if>
				</span>
			</div>
		</div>

		<div class="fieldset-row">
			<div class="label">
				<label><wp:i18n key="LABEL_COMUNICAZIONI_DATAINVIO" /> : </label>
			</div>
			<div class="element">
				<span><s:date name="comunicazione.dataInvio.time" format="dd/MM/yyyy HH:mm:ss" /></span>
			</div>
		</div>

		<s:if test="%{servizioProtocollazione}">
			<div class="fieldset-row">
				<div class="label">
					<label><wp:i18n key="LABEL_COMUNICAZIONI_DATAPROTOCOLLO" /> : </label>
				</div>
				<div class="element">
					<span><s:date name="comunicazione.dataProtocollo.time" format="dd/MM/yyyy HH:mm:ss" /></span>
				</div>
			</div>
	
			<div class="fieldset-row">
				<div class="label">
					<label><wp:i18n key="LABEL_COMUNICAZIONI_NUMPROTOCOLLO" /> : </label>
				</div>
				<div class="element">
					<span><s:property value="%{comunicazione.numeroProtocollo}" escape="<s:property value='%{doEscape}'/>" /></span>
				</div>
			</div>
		</s:if>

		<s:if test="%{!inviata}">
			<div class="fieldset-row">
				<div class="label">
					<label><wp:i18n key="LABEL_COMUNICAZIONI_DATALETTURA" /> : </label>
				</div>
				<div class="element">
					<span><s:date name="comunicazione.dataLettura.time" format="dd/MM/yyyy HH:mm:ss" /></span>
				</div>
			</div>
		</s:if>

		<s:if test="%{comunicazione.dataScadenza != null}">
			<div class="fieldset-row">
				<div class="label">
					<label><wp:i18n key="LABEL_DATA_SCADENZA" /> : </label>
				</div>
				<div class="element">
					<span>
						<s:date name="comunicazione.dataScadenza" format="dd/MM/yyyy" />
						<s:if test="%{comunicazione.oraScadenza != null}">
							<wp:i18n key="LABEL_ENTRO_LE_ORE" /> <s:property value="comunicazione.oraScadenza" />
						</s:if>
					</span>
				</div>
			</div>
		</s:if>
		
		<s:if test="%{comunicazione.modello == 10 || comunicazione.modello == 11 || comunicazione.modello == 12 || comunicazione.modello == 13
					  || comunicazione.modello == 15 || comunicazione.modello == 16|| comunicazione.modello == 17 || comunicazione.modello == 18}">
			<div class="fieldset-row">
				<div class="label">
					<label><wp:i18n key="LABEL_FASE_RETTIFICA" /> : </label>
				</div>
				<div class="element">
					<span>
						<s:iterator value="maps['tipiComunicazioneUlteriori']" var="m">
							<s:if test="%{#m.key == comunicazione.modello}" >
								<s:property value="%{#m.value}" />
							</s:if>
						</s:iterator>
					</span>
				</div>
			</div>	
		</s:if>
		
		<div class="fieldset-row">
			<div class="label">
				<label><wp:i18n key="LABEL_COMUNICAZIONI_ALLEGATI" /> : </label>
			</div>
			<div class="element">
				<s:if test="%{comunicazione.documento.length > 0}">
					<s:set var="elencoDocumentiAllegati" value="%{comunicazione.documento}" />
					<s:if test="%{inviata}">
						<s:set var="idprg" value="'PA'"/>
					</s:if>
					<s:if test="{!inviata}">
						<s:set var="idprg" value="%{comunicazione.applicativo}" />
					</s:if>
					<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/bandi/inc/iteratorDocAllegati.jsp">
						<jsp:param name="path" value="downloadDocumentoRiservato"/>
					</jsp:include>
				</s:if>
				<s:else>
					<wp:i18n key="LABEL_NO_DOCUMENT" />.
				</s:else>
			</div>
		</div>
	</fieldset>
	
	<div class="azioni">
		<s:if test="%{!inviata && #attr.visInviaComunicazioni && !comunicazione.bloccaRispondi && !soccorsoScaduto && !rettifica && !#isRispostaRettifica}">
			<form action='<wp:action path="/ExtStr2/do/FrontEnd/Comunicazioni/initNuovaComunicazione.action" />'
				method="post" class="azione">
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
				<div>
					<wp:i18n key="BUTTON_NUOVA_COMUNICAZIONE" var="valueRispondi"/>
					<s:submit value="%{#attr.valueRispondi}" title="%{#attr.valueRispondi}" cssClass="button block-ui" method="initNuovaComunicazione"/>
					<s:hidden id="pagina" name="pagina" value="%{pagina}"/>
 					<s:hidden id="from" name="from" value="rispondi"/>
 					<s:hidden id="codice" name="codice" value="%{codice}"/>
 					<s:hidden id="applicativo" name="applicativo" value="%{applicativo}"/>
 					<s:hidden id="id" name="id" value="%{idComunicazione}"/>
 					<s:hidden id="idDestinatario" name="idDestinatario" value="%{idDestinatario}"/>
 					<s:hidden id="destinatario" name="destinatario" value="%{comunicazione.destinatario}"/>
				</div>
			</form>
		</s:if>
		
		<s:if test="%{rettifica && #isAccettazioneRettifica}">
			<%-- INVIO RETTIFICA SU RICHIESTA DI RETTIFICA --%>
			<form action="<wp:action path="/ExtStr2/do/FrontEnd/Comunicazioni/initNuovaComunicazione.action" />" method="post" class="azione">
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
				<div>
					<s:hidden id="pagina" name="pagina" value="%{pagina}"/>
 					<s:hidden id="from" name="from" value="rispondi"/>
					<input type="hidden" name="ext" value="${param.ext}" />
					<s:hidden name="codice" value="%{codice}" />
					<s:hidden name="codice2" value="%{#codice2}" />
					<s:if test="%{comunicazione.modello == 10 || comunicazione.modello == 11 || comunicazione.modello == 12 || comunicazione.modello == 13}">
						<s:hidden name="tipoBusta" value="%{BUSTA_TECNICA}" /> 
					</s:if>
					<s:elseif test="%{comunicazione.modello == 15 || comunicazione.modello == 16 || comunicazione.modello == 17 || comunicazione.modello == 18}">
						<s:hidden name="tipoBusta" value="%{BUSTA_ECONOMICA}" />
					</s:elseif>
					<s:hidden name="applicativo" value="%{applicativo}" />
					<s:hidden name="id" value="%{idComunicazione}" />
					<s:hidden id="idDestinatario" name="idDestinatario" value="%{idDestinatario}"/>
					<s:hidden name="rettifica" value="RETTIFICA" />		<%-- vedi WizardRettificaHelper.FasiRettifica --%>
					<wp:i18n key="LABEL_RETTIFICA_BUSTA" var="valueButtonRettifica" />
					<s:submit value="%{#attr.valueButtonRettifica}" title="%{#attr.valueButtonRettifica}" cssClass="button"></s:submit>
				</div>
			</form>
		</s:if>
		
		<s:if test="%{abilitaProcedura && !#isGenericaRiservata}">
			<%-- VAI A PROCEDURA / VAI A CONTRATTO --%>
		 	<form action='<wp:action path="${pathProcedura}" page="${pageProcedura}" />&amp;idComunicazione=<s:property value="%{idComunicazione}"/>&amp;idDestinatario=<s:property value="%{idDestinatario}"/>'
				method="post" class="azione">
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
				<div>
					<s:if test="%{ #isStipula }">
						<wp:i18n key="BUTTON_VAI_AL_CONTRATTO" var="valueGoToProcedura"/>
					</s:if>
					<s:else>
						<wp:i18n key="BUTTON_VAI_ALLA_PROCEDURA" var="valueGoToProcedura"/>
					</s:else>
					<s:submit value="%{#attr.valueGoToProcedura}" title="%{#attr.valueGoToProcedura}" cssClass="button block-ui"></s:submit>
					<s:hidden id="codStipula" name="codice" value="%{codice}"/>
					<s:hidden id="codStipula2" name="nappal" value="%{codice2}"/>
				</div>
			</form>
		</s:if>
	</div>

	<div class="back-link">
		<c:set var="href">
			&amp;pagina=<s:property value="%{pagina}"/>
			<c:if test="${! empty param.ext}">&amp;ext=${param.ext}</c:if>
		</c:set>

		<wp:i18n key="LINK_BACK_TO_LIST" var="titleBackLink"/>
		<a href='<wp:action path="${path}"/>${href}' title="${attr.titleBackLink}">
			<wp:i18n key="LINK_BACK_TO_LIST" />
		</a>
	</div>

</div>