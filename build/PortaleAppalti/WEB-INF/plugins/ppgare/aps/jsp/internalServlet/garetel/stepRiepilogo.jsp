<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>

<s:set var="imgCheck"><wp:resourceURL />static/img/check.svg</s:set>
<c:set var="titolo"><wp:i18n key="TITLE_PAGE_GARETEL_RIEPILOGO_OFFERTA" /></c:set>
<c:set var="codiceBalloon" value="BALLOON_INVIO_BUSTE_TELEMATICHE_RIEPILOGO"/>


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2>${titolo}</h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/field_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="${codiceBalloon}"/>
	</jsp:include>

	<fieldset>
		<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_DATI_OPERATORE_ECONOMICO" /></legend>
		
		<div class="fieldset-row first-row">
			<div class="label">
				<label></span><wp:i18n key="LABEL_GARETEL_OGGETTO" /> : </label>
			</div>
			<div class="element">
				<s:property value="%{dettGara.datiGeneraliGara.oggetto}" />
			</div>
		</div>
	
		<div class="fieldset-row">
			<div class="label">
				<label></span><wp:i18n key="LABEL_OPERATORE_ECONOMICO" /> : </label>
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
				<s:else><p><wp:i18n key="LABEL_NOT_DEFINED" /></p></s:else>
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
	
	<s:if test="%{operazione == presentaPartecipazione}" >
	
		<%-- BUSTA PREQUALIFICA --%>
		<fieldset>
			<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_BUSTA_PREQUALIFICA" /></legend>
			
			<s:if test="%{#session.dettBustaPrequalifica.datiModificati}">
				<div class="balloon">
					<div class="balloon-content balloon-alert">
						<wp:i18n key="LABEL_NO_MODIFICHE_AI_DOCUMENTI" />
					</div>
				</div>
			</s:if>

			<c:set var="busta" scope="request" value="${bustaRiepilogativa.bustaPrequalifica}"/>
			<c:set var="docObbligatoriMancanti" scope="request" value="${docObbligatoriMancantiPrequalifica}"/>
		 	<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/garetel/inc/stepRiepilogoDocumenti.jsp">
				<jsp:param name="offertaTelematica" value="false"/>
				<jsp:param name="tipoBusta" value="${BUSTA_PRE_QUALIFICA}"/>
				<jsp:param name="codice" value="${codice}"/>
				<jsp:param name="operazione" value="${operazione}"/>
		 	</jsp:include>
		</fieldset>	
		
	</s:if>
	<s:else>

		<%-- BUSTA AMMINISTRATIVA --%>
		<fieldset>
			<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_BUSTA_AMMINISTRATIVA" /></legend>
			
			<s:if test="%{#session.dettBustaAmministrativa.datiModificati}">
				<div class="balloon">
					<div class="balloon-content balloon-alert">
						<wp:i18n key="LABEL_NO_MODIFICHE_AI_DOCUMENTI" />
					</div>
				</div>
			</s:if>

			<c:set var="busta" scope="request" value="${bustaRiepilogativa.bustaAmministrativa}"/>
			<c:set var="docObbligatoriMancanti" scope="request" value="${docObbligatoriMancantiAmministrativa}"/>
		 	<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/garetel/inc/stepRiepilogoDocumenti.jsp">
				<jsp:param name="offertaTelematica" value="false"/>
				<jsp:param name="tipoBusta" value="${BUSTA_AMMINISTRATIVA}"/>
				<jsp:param name="codice" value="${codice}"/>
				<jsp:param name="operazione" value="${operazione}"/>
		 	</jsp:include>
		 	
		</fieldset>
	
		<%-- BUSTA TECNICA --%>
		<s:if test="%{offertaTecnica}">
			<fieldset>
				<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_BUSTA_TECNICA" /></legend>
				
				<s:if test="%{#session.dettBustaTecnica.datiModificati}">
					<div class="balloon">
						<div class="balloon-content balloon-alert">
							<wp:i18n key="LABEL_NO_MODIFICHE_AI_DOCUMENTI" />
						</div>
					</div>
				</s:if>
				
				<c:set var="busta" scope="request" value="${bustaRiepilogativa.bustaTecnica}"/>
		 		<c:set var="docObbligatoriMancanti" scope="request" value="${docObbligatoriMancantiTecnica}"/>
			 	<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/garetel/inc/stepRiepilogoDocumenti.jsp">
					<jsp:param name="offertaTelematica" value="false"/>
					<jsp:param name="tipoBusta" value="${BUSTA_TECNICA}"/>
					<jsp:param name="codice" value="${codice}"/>
					<jsp:param name="operazione" value="${operazione}"/>
			 	</jsp:include> 
			</fieldset>
		</s:if>
	
		<%-- BUSTA ECONOMICA --%>
		<c:if test="${!costoFisso}">
			<fieldset>
				<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_BUSTA_ECONOMICA" /></legend>
				
				<s:if test="%{#session.dettBustaEconomica.datiModificati}">
					<div class="balloon">
						<div class="balloon-content balloon-alert">
							<wp:i18n key="LABEL_NO_MODIFICHE_AI_DOCUMENTI" />
						</div>
					</div>
				</s:if>
				
				<c:set var="busta" scope="request" value="${bustaRiepilogativa.bustaEconomica}"/>
				<c:set var="docObbligatoriMancanti" scope="request" value="${docObbligatoriMancantiEconomica}"/>
			 	<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/garetel/inc/stepRiepilogoDocumenti.jsp">
					<jsp:param name="offertaTelematica" value="${offertaTelematica}"/> 	
					<jsp:param name="tipoBusta" value="${BUSTA_ECONOMICA}"/>
					<jsp:param name="codice" value="${codice}"/>
					<jsp:param name="operazione" value="${operazione}"/>
			 	</jsp:include> 	 
			</fieldset>
		</c:if>
		
	</s:else>
	
	<div class="back-link">
		<a href="<wp:action path="/ExtStr2/do/FrontEnd/GareTel/openGestioneBuste.action" />&amp;codice=${codice}&amp;operazione=${operazione}&amp;ext=${param.ext}&amp;${tokenHrefParams}">
			<wp:i18n key="BUTTON_WIZARD_BACK_TO_MENU" />
		</a>
	</div>
</div>