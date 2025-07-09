<%@ taglib prefix="wp"  uri="aps-core.tld" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="s"   uri="/struts-tags" %>

<%--
il parametro "richiestaRettifica" va passato negli attributi della request, come segue:  
 	 
	<s:set var="richiestaRettifica" value="lottoEconomicaRettifica.get(#lotto)"/>
 	<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/garetel/inc/infoRichiestaRettifica.jsp"/>

--%>

<%-- 
 #richiestaRettifica.abilitaRichiesta=<s:property value="%{#richiestaRettifica.abilitaRichiesta}"/><br/>
 #richiestaRettifica.abilitaInvio=<s:property value="%{#richiestaRettifica.abilitaInvio}"/><br/>
 #richiestaRettifica.statoRichiesta=<s:property value="%{#richiestaRettifica.statoRichiesta}"/><br/>
 #richiestaRettifica.ultimaRichiesta=<s:property value="%{#richiestaRettifica.ultimaRichiesta}"/><br/>
 #richiestaRettifica.ultimaRettifica=<s:property value="%{#richiestaRettifica.ultimaRettifica}"/><br/>
--%>

<s:if test="%{#richiestaRettifica.ultimaRichiesta != null || #richiestaRettifica.ultimaRettifica != null}">
	<s:if test="%{#richiestaRettifica.statoRichiesta >= 0}">
		<div class="note">
			<s:set var="showUltimaRichiesta" value="%{#richiestaRettifica.ultimaRichiesta != null}"/>
			<s:set var="showUltimaRettifica" value="%{#richiestaRettifica.ultimaRettifica != null}"/>
			<s:if test="%{#richiestaRettifica.ultimaRichiesta != null && #richiestaRettifica.ultimaRettifica != null}">
				<s:set var="showUltimaRichiesta" value="%{#richiestaRettifica.ultimaRichiesta.id > #richiestaRettifica.ultimaRettifica.id}"/>
			</s:if>
			
			<s:if test="%{#showUltimaRichiesta}">
				<wp:i18n key="LABEL_RETTIFICA_ULTIMA_RICHIESTA_RETTIFICA" /> <s:date name="%{#richiestaRettifica.ultimaRichiesta.dataInserimento}" format="dd/MM/yyyy HH:mm:ss" />
			</s:if>
			<s:elseif test="%{#showUltimaRettifica}">
				<wp:i18n key="LABEL_RETTIFICA_ULTIMA_RETTIFICA" /> <s:date name="%{#richiestaRettifica.ultimaRettifica.dataInserimento}" format="dd/MM/yyyy HH:mm:ss" />
			</s:elseif>
			
			<s:if test="%{#richiestaRettifica.ultimaRichiesta != null}">
				<s:if test="%{#richiestaRettifica.statoRichiesta == 1}">
					<wp:i18n key="LABEL_RETTIFICA_IN_ATTESA_DI_APPROVAZIONE" var="lblStatoRettifica"/>
				</s:if>
				<s:elseif test="%{#richiestaRettifica.statoRichiesta == 2}">
					<wp:i18n key="LABEL_RETTIFICA_RIFIUTATA" var="lblStatoRettifica"/>
				</s:elseif>
				<s:elseif test="%{#richiestaRettifica.statoRichiesta == 3}">
					<wp:i18n key="LABEL_RETTIFICA_ACCETTATA" var="lblStatoRettifica"/>
					<wp:i18n key="LABEL_DATA_SCADENZA" var="lblScadenza"/>
					<c:set var="scadenza">${fn:toLowerCase(attr.lblScadenza)} <s:date name="%{#richiestaRettifica.dataScadenza}" format="dd/MM/yyyy HH:mm" /></c:set>
				</s:elseif>
				<s:elseif test="%{#richiestaRettifica.statoRichiesta == 4}">
					<wp:i18n key="LABEL_RETTIFICA_TRASMESSA" var="lblStatoRettifica"/>
				</s:elseif>
				<s:elseif test="%{#richiestaRettifica.statoRichiesta > 4}">
					<wp:i18n key="LABEL_RETTIFICA_TRASMESSA" var="lblStatoRettifica"/>
				</s:elseif>
				<c:if test="${attr.lblStatoRettifica != null && attr.lblStatoRettifica != ''}">
					(${fn:toUpperCase(attr.lblStatoRettifica)}) <c:if test="${scadenza != null && scadenza != ''}">${scadenza}</c:if>
				</c:if>
			</s:if>
		</div>
	</s:if>
</s:if>

<s:if test="%{#richiestaRettifica.abilitaRichiesta || #richiestaRettifica.abilitaInvio}">
	<div class="azioni">
		<%-- richiesta rettifica --%>
		<s:if test="%{#richiestaRettifica.abilitaRichiesta}">
			<form action="<wp:action path="/ExtStr2/do/FrontEnd/Comunicazioni/confirmRichiestaRettifica.action" />" method="post" class="azione">
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
				<div>
					<input type="hidden" name="ext" value="${param.ext}" />
					<input type="hidden" name="from" value="bandi" />
					<s:hidden name="codice" value="%{codice}" />
					<s:hidden name="codice2" value="%{#lotto}" />
					<s:hidden name="operazione" value="%{operazione}" />
					<s:hidden name="progressivoOfferta" value="%{progressivoOfferta}" />
					<s:hidden name="tipoBusta" value="%{#richiestaRettifica.tipoBusta}" />
					<s:hidden name="rettifica" value="RICHIESTA" />		<%-- vedi WizardRettificaHelper.FasiRettifica --%>
					<wp:i18n key="LABEL_RICHIESTA_RETTIFICA_BUSTA" var="valueButtonRichiestaRettifica" />
					<s:submit value="%{#attr.valueButtonRichiestaRettifica}" title="%{#attr.valueButtonRichiestaRettifica}" cssClass="button"></s:submit>
				</div>
			</form>
		</s:if>	
		
		<%-- invio rettifica --%>
		<s:if test="%{#richiestaRettifica.abilitaInvio}">
			<form action="<wp:action path="/ExtStr2/do/FrontEnd/Comunicazioni/initNuovaComunicazione.action" />" method="post" class="azione">
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
				<div>
					<input type="hidden" name="ext" value="${param.ext}" />
					<input type="hidden" name="from" value="bandi" />
					<s:hidden name="codice" value="%{codice}" />
					<s:hidden name="codice2" value="%{#lotto}" />
					<s:hidden name="operazione" value="%{operazione}" />
					<s:hidden name="progressivoOfferta" value="%{progressivoOfferta}" />
					<s:hidden name="tipoBusta" value="%{#richiestaRettifica.tipoBusta}" />
					<s:hidden name="id" value="%{richiestaRettifica.id}" />
					<s:hidden name="applicativo" value="%{richiestaRettifica.applicativo}" />
					<s:hidden name="rettifica" value="RETTIFICA" />		<%-- vedi WizardRettificaHelper.FasiRettifica --%>
					<wp:i18n key="LABEL_RETTIFICA_BUSTA" var="valueButtonRettifica" />
					<s:submit value="%{#attr.valueButtonRettifica}" title="%{#attr.valueButtonRettifica}" cssClass="button"></s:submit>
				</div>
			</form>
		</s:if>
	</div>
</s:if>