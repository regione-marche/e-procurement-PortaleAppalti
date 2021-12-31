<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>

<c:set var="codGara" value="${codiceGara}"/>
<c:if test="${codiceGara == null}">
	<c:set var="codGara" value="${codice}"/>
</c:if>

<c:set var="domandaPartecipazione" value="${operazione == PresentaPartecipazione}" />
<c:set var="invioOfferta" value="${operazione == InviaOfferta}" />
<s:set name="terminiAperti" value="0"/>
<c:set var="ristretta" value="${dettaglioGara.datiGeneraliGara.iterGara == 2 ||
								dettaglioGara.datiGeneraliGara.iterGara == 4}" />

<c:choose>
	<c:when test="${invioOfferta}">
		<s:set name="terminiAperti" value="%{abilitazioniGara.richInvioOfferta}"/>
		
		<wp:i18n key='TITLE_PAGE_GARETEL_LISTA_OFFERTE' var="labelTitle"/>
		<c:set var="labelBalloon" value='BALLOON_LISTA_OFFERTE_TELEMATICHE'/>
		<wp:i18n key="LABEL_LISTA_OFFERTE" var="labelLegend"/>
		<wp:i18n key='LABEL_PROGRESSIVO_OFFERTA' var="labelColProg"/>
		<wp:i18n key='LABEL_STATO_OFFERTA' var="labelColStato"/>
	</c:when>
	<c:otherwise>
		<s:set name="terminiAperti" value="%{abilitazioniGara.richPartecipazione}"/>
		
		<wp:i18n key='TITLE_PAGE_GARETEL_LISTA_DOMANDE_PARTECIPAZIONE' var="labelTitle"/>
		<c:set var="labelBalloon" value='BALLOON_LISTA_DOMANDE_PARTECIPAZIONE_TELEMATICHE'/>
		<wp:i18n key="LABEL_LISTA_DOMANDE" var="labelLegend"/>
		<wp:i18n key='LABEL_PROGRESSIVO_DOMANDA' var="labelColProg"/>
		<wp:i18n key='LABEL_STATO_DOMANDA' var="labelColStato"/>
	</c:otherwise>
</c:choose>


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2>
		${labelTitle}
	</h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="${labelBalloon}"/>
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_messages.jsp" />
	
	<c:set var="progressivoSingolaPresente" value="0" />
	
	<fieldset>
		<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span>${labelLegend}</legend>
		<table class="wizard-table" summary="${labelLegend}" />
		<table>
			<tr>
				<th scope="col">&nbsp;&nbsp;#&nbsp;&nbsp;</th>
				<th scope="col"><wp:i18n key="LABEL_CONCORRENTE"/></th>
				<th scope="col"><wp:i18n key="LABEL_MODALITA_PARTECIPAZIONE"/></th>
				<th scope="col"><wp:i18n key="LABEL_LOTTI"/></th>
				<th scope="col">${labelColStato}</th>
				<th scope="col"><wp:i18n key="ACTIONS" /></th>
			</tr>
			<s:iterator value="listaOfferte" var="item" status="stat">
				
				<s:if test="%{#item.progressivoOfferta == 1}" >
					<c:set var="progressivoSingolaPresente" value="1" />
				</s:if>
			
				<s:if test="%{#item.stato == 1}" >
					<!-- offerta in compilazione -->
					<c:set var="stato"><wp:i18n key="LABEL_STATO_IN_COMPILAZIONE"/></c:set>
					<c:choose>
						<c:when test="${operazione == presentaPartecipazione}">
							<c:set var="href" value="/ExtStr2/do/FrontEnd/GareTel/openGestioneBuste.action"/>
						</c:when>
						<c:when test="${operazione == inviaOfferta}">
							<c:set var="href" value="/ExtStr2/do/FrontEnd/GareTel/openGestioneBusteDistinte.action"/>
						</c:when>
						<c:otherwise>
							Page "gestioneListaOfferte": parameter "operazione" not set!
						</c:otherwise>
					</c:choose>
				</s:if>
				<s:else>
					<!-- offerta inviata -->
					<c:set var="stato"><wp:i18n key="LABEL_STATO_INVIATO"/></c:set>
					<c:choose>
						<c:when test="${operazione == presentaPartecipazione}">
							<c:set var="href" value="/ExtStr2/do/FrontEnd/GareTel/openRiepilogoOfferta.action"/>
						</c:when>
						<c:when test="${operazione == inviaOfferta}">
							<c:set var="href" value="/ExtStr2/do/FrontEnd/GareTel/riepilogoOfferteDistinte.action"/>
						</c:when>
						<c:otherwise>
							Page "gestioneListaOfferte": parameter "operazione" not set!
						</c:otherwise>
					</c:choose>
				</s:else>

				<c:set var="hrefAnnulla" value="/ExtStr2/do/FrontEnd/GareTel/annullaOffertaListaOfferte.action"/>
				
				<tr>
					<td>
						&nbsp;&nbsp;<s:property value="%{#stat.index + 1}" />&nbsp;&nbsp;
					</td>
					<td>
						<s:if test="%{#terminiAperti || #item.stato != 1}">
							<form action="<wp:action path='${href}' />" method="post">
								<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
								<input type="hidden" name="codice" value="${codGara}" />
								<input type="hidden" name="operazione" value="${operazione}" />
								<input type="hidden" name="progressivoOfferta" value="<s:property value='%{#item.progressivoOfferta}'/>" />
								<input type="hidden" name="fromListaOfferte" value="1" />
								<input type="hidden" name="ext" value="${param.ext}" />
								
								<a href="javascript:;" onclick="parentNode.submit();">
									<s:property value="#item.concorrente" />
								</a>
							</form>
						</s:if>
						<s:else>
							<s:property value="#item.concorrente" />
						</s:else>
					</td>
					<td>
						<s:if test="%{#item.tipoPartecipazione == 1}" >
							<ul>
							<s:iterator value="#item.mandanti" var="mandante" status="statmandanti">
								<li>
									<s:property value="#mandante" /><br/>
								</li>
							</s:iterator>
							</ul>
						</s:if>
						<s:else>
							<wp:i18n key="LABEL_TIPO_PARTECIPAZIONE_SINGOLA"/>
						</s:else>
					</td>
					<td>
						<s:iterator value="#item.lotti" var="lotto" status="statlotti">
							<wp:i18n key="LABEL_LOTTO" />&nbsp;<s:property value="#lotto.codiceInterno"/>
							<br/>
						</s:iterator>
					</td>
					<td>
						${stato}
					</td>
					<td class="azioni">
						<s:if test="%{#terminiAperti && #item.stato == 1}">
							
							<s:if test="%{#item.annullamento}">
								<c:set var="clsTipoAnnullamento" value="undo"/>
								<c:if test="${invioOfferta}">
									<wp:i18n key="LABEL_ANNULLA_OFFERTA" var="labelElimina"/>
								</c:if>
								<c:if test="${!invioOfferta}">
									<wp:i18n key="LABEL_ANNULLA_DOMANDA" var="labelElimina"/>
								</c:if>
							</s:if>
							<s:if test="%{#item.eliminazione}">
								<c:set var="clsTipoAnnullamento" value="delete"/>
								<c:if test="${invioOfferta}">
									<wp:i18n key="LABEL_ELIMINA_OFFERTA" var="labelElimina"/>
								</c:if>
								<c:if test="${!invioOfferta}">
									<wp:i18n key="LABEL_ELIMINA_DOMANDA" var="labelElimina"/>
								</c:if>
							</s:if>
							<s:if test="%{#item.rettifica}">
								<c:set var="clsTipoAnnullamento" value="modified"/>
								<c:if test="${invioOfferta}">
									<wp:i18n key="LABEL_RETTIFICA_OFFERTA" var="labelElimina"/>
								</c:if>
								<c:if test="${!invioOfferta}">
									<wp:i18n key="LABEL_RETTIFICA_DOMANDA" var="labelElimina"/>
								</c:if>
							</s:if>
							
							<c:choose>
								<c:when test="${skin == 'highcontrast' || skin == 'text'}">
									<a href="<wp:action path='${hrefAnnulla}'/>&amp;codice=${codGara}&amp;operazione=${operazione}&amp;progressivoOfferta=<s:property value='%{#item.progressivoOfferta}'/>&amp;ext=${param.ext}&amp;${tokenHrefParams}" 
										title='<s:property value="%{#attr.labelElimina}"/>' class='bkg ${clsTipoAnnullamento}'>
										<s:property value="%{#attr.lblElimina}"/>
									</a>
								</c:when>
								<c:otherwise>
									<a href="<wp:action path='${hrefAnnulla}'/>&amp;codice=${codGara}&amp;operazione=${operazione}&amp;progressivoOfferta=<s:property value='%{#item.progressivoOfferta}'/>&amp;ext=${param.ext}&amp;${tokenHrefParams}" 
										title='<s:property value="%{#attr.labelElimina}"/>' class="bkg ${clsTipoAnnullamento}">
									</a>
								</c:otherwise>
							</c:choose>
						</s:if>
					</td>
				</tr>
			</s:iterator>
		</table>
	</fieldset>
		
	<s:if test="%{#terminiAperti}">
		<form action="<wp:action path="/ExtStr2/do/FrontEnd/GareTel/nuovaOffertaListaOfferte.action" />" method="post">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
			
			<div class="azioni">
				<c:choose>
					<c:when test="${invioOfferta}">
						<wp:i18n key='BUTTON_NUOVA_OFFERTA' var="buttonNuovo"/>
						<wp:i18n key='BUTTON_NUOVA_OFFERTA_RTI' var="buttonNuovoRti"/>
					</c:when>
					<c:otherwise>
						<wp:i18n key='BUTTON_NUOVA_DOMANDA' var="buttonNuovo"/>
						<wp:i18n key='BUTTON_NUOVA_DOMANDA_RTI' var="buttonNuovoRti"/>
					</c:otherwise>
				</c:choose>
				<c:if test="${abilitaOffertaSingola}">
					<c:if test="${progressivoSingolaPresente != 1}">
						<s:submit value="%{#attr.buttonNuovo}" title="%{#attr.buttonNuovo}" cssClass="button" method="nuovaOffertaSingola" ></s:submit>
					</c:if>
				</c:if>
				<s:submit value="%{#attr.buttonNuovoRti}" title="%{#attr.buttonNuovoRti}" cssClass="button" method="nuovaOfferta" ></s:submit>
				
				<input type="hidden" name="codice" value="<s:property value='%{codice}'/>" />
				<input type="hidden" name="codiceGara" value="<s:property value='%{codiceGara}'/>" />
				<input type="hidden" name="operazione" value="<s:property value='%{operazione}'/>" />
				<input type="hidden" name="progressivoOfferta" value=""/>
			</div>
		</form>
	</s:if>
		
	<div class="back-link">
		<a href="<wp:action path="/ExtStr2/do/FrontEnd/Bandi/view.action" />&amp;codice=${codGara}&amp;idComunicazione=${idComunicazione}&amp;${tokenHrefParams}">
			<wp:i18n key="LINK_BACK_TO_PROCEDURE" />
		</a>
	</div>
</div>
