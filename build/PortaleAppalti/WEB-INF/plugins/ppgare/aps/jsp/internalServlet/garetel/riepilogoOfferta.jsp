<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>

<c:set var="isOfferta" value="${operazione == inviaOfferta}"/>
<c:set var="isPartecipazione" value="${operazione == presentaPartecipazione}"/>

<c:if test="${isOfferta}">
	<c:set var="titolo"><wp:i18n key='TITLE_PAGE_GARETEL_RIEPILOGO_OFFERTA'/></c:set>
	<c:set var="codiceBalloon" value="BALLOON_RIEPILOGO_OFFERTA"/> 
</c:if>
<c:if test="${isPartecipazione}">
	<c:set var="titolo"><wp:i18n key='TITLE_PAGE_GARETEL_RIEPILOGO_PARTECIPAZIONE'/></c:set>
	<c:set var="codiceBalloon" value="BALLOON_RIEPILOGO_PARTECIPAZIONE"/> 
</c:if>


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2>${titolo}</h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="${codiceBalloon}"/>
	</jsp:include>

	<s:url id="urlDownloadBusta" namespace="/do/FrontEnd/GareTel" action="downloadBusta" />

	<fieldset>
		<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key='LABEL_DATI_OPERATORE_ECONOMICO'/></legend>
		
		<div class="fieldset-row first-row">
			<div class="label">
				<label><wp:i18n key="LABEL_GARETEL_OGGETTO" /> : </label>
			</div>
			<div class="element">
				<s:property value="%{dettGara.datiGeneraliGara.oggetto}" />
			</div>
		</div>
	
		<div class="fieldset-row">
			<div class="label">
				<label><wp:i18n key="LABEL_OPERATORE_ECONOMICO" /> : </label>
			</div>
			<div class="element">
				<s:property value="%{datiImpresa.datiPrincipaliImpresa.ragioneSociale}" />
			</div>
		</div>
			
		<div class="fieldset-row">
			<div class="label">
				<label><wp:i18n key="LABEL_LEGALI_RAPPRESENTANTI" /> : </label>
			</div>
			<div class="element">
				<s:if test="%{datiImpresa.legaliRappresentantiImpresa.size() > 0}">
					<ul class="list">
						<s:iterator value="%{datiImpresa.legaliRappresentantiImpresa.iterator()}" var="legaleRappresentante" status="stat">
							<li class='<s:if test="%{#stat.first}">first</s:if> <s:if test="%{#stat.last}">last</s:if>'>
								<s:property value="%{#legaleRappresentante.cognome}"/> <s:property value="%{#legaleRappresentante.nome}"/> 
								<c:if test="${! empty legaleRappresentante.dataInizioIncarico}">
									<wp:i18n key="LABEL_DA_DATA" /> <s:property value="%{#legaleRappresentante.dataInizioIncarico}"/>
								</c:if> 
								<c:if test="${! empty legaleRappresentante.dataFineIncarico}">
									<wp:i18n key="LABEL_A_DATA" /> <s:property value="%{#legaleRappresentante.dataFineIncarico}"/>
								</c:if>
							</li>
						</s:iterator>
					</ul>
				</s:if>
				<s:else><wp:i18n key="LABEL_NOT_DEFINED" /></s:else>
			</div>
		</div>

		<div class="fieldset-row">
			<div class="label">
			<label><wp:i18n key="LABEL_DIRETTORI_TECNICI" /> : </label>
			</div>
			<div class="element">
				<s:if test="%{datiImpresa.direttoriTecniciImpresa.size() > 0}">
					<ul class="list">
						<s:iterator value="%{datiImpresa.direttoriTecniciImpresa.iterator()}" var="direttoreTecnico" status="stat">
							<li class='<s:if test="%{#stat.first}">first</s:if> <s:if test="%{#stat.last}">last</s:if>'>
								<s:property value="%{#direttoreTecnico.cognome}"/> <s:property value="%{#direttoreTecnico.nome}"/> 
								<c:if test="${! empty direttoreTecnico.dataInizioIncarico}">
									<wp:i18n key="LABEL_DA_DATA" /> <s:property value="%{#direttoreTecnico.dataInizioIncarico}"/>
								</c:if> 
								<c:if test="${! empty direttoreTecnico.dataFineIncarico}">
									<wp:i18n key="LABEL_A_DATA" /> <s:property value="%{#direttoreTecnico.dataFineIncarico}"/>
								</c:if>
							</li>
						</s:iterator>
					</ul>
				</s:if>
				<s:else><wp:i18n key="LABEL_NOT_DEFINED" /></s:else>
			</div>
		</div>

		<div class="fieldset-row last-row">
			<div class="label">
				<label><wp:i18n key="LABEL_PARTECIPA_COME_MANDATARIA_RTI" /> : </label>
			</div>
			<div class="element">
				<s:if test="%{rti}"><wp:i18n key="LABEL_YES" />, <wp:i18n key="LABEL_PER_CONTO_DI" /> <s:property value="%{denominazioneRti}"/></s:if>
				<s:else><wp:i18n key="LABEL_NO" /></s:else>
			</div>
		</div>
	</fieldset>

	<fieldset>
		<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_DOCUMENTAZIONE_ALLEGATA" /></legend>

		<c:choose>
			<c:when test="${operazione == inviaOfferta}">
				<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/garetel/inc/riepilogoDocumentiOfferta.jsp" />
			</c:when>
			<c:when test="${operazione == presentaPartecipazione}">
				<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/garetel/inc/riepilogoDocumentiPartecipazione.jsp" />
			</c:when>
			<c:otherwise>
				Page riepilogoOfferta: parameter "operazione" non initialized
			</c:otherwise>
		</c:choose>
	</fieldset>

	<div class="azioni">
		<s:if test="%{abilitaRettifica}">
			<form action="<wp:action path="/ExtStr2/do/FrontEnd/GareTel/confirmRettificaOfferta.action" />" method="post" class="azione">
				<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
				
				<div>
					<input type="hidden" name="ext" value="${param.ext}" />
					<s:hidden name="codice" value="%{codice}" />
					<s:hidden name="progressivoOfferta" value="%{progressivoOfferta}" />	
					<s:if test="%{fromListaOfferte == 1}">
						<s:hidden name="fromListaOfferte" value="%{fromListaOfferte}" />
					</s:if>
					<s:if test="%{operazione == inviaOfferta}">
						<wp:i18n key="LABEL_ANNULLA_RIPRESENTA_OFFERTA" var="valueButtonAnnullaRipresenta" />
						<s:hidden name="operazione" value="%{inviaOfferta}" />
						<s:submit value="%{#attr.valueButtonAnnullaRipresenta}" title="%{#attr.valueButtonAnnullaRipresenta}" cssClass="button"></s:submit>
					</s:if>
					<s:else>
						<wp:i18n key="LABEL_ANNULLA_RIPRESENTA_PARTECIPAZIONE" var="valueButtonAnnullaRipresenta" />
						<s:hidden name="operazione" value="%{presentaPartecipazione}" />
						<s:submit value="%{#attr.valueButtonAnnullaRipresenta}" title="%{#attr.valueButtonAnnullaRipresenta}" cssClass="button"></s:submit>
					</s:else>
				</div>
			</form>
		</s:if>
	</div>
</div>

<div class="back-link">
	<a href="<wp:action path="/ExtStr2/do/FrontEnd/Bandi/view.action" />&amp;codice=${codice}&amp;ext=${param.ext}&amp;${tokenHrefParams}">
		<wp:i18n key="LINK_BACK_TO_PROCEDURE" />
	</a>
</div>
