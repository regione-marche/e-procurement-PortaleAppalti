<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>

<s:set var="partecipazione" value="%{#session.dettaglioOffertaGara.bustaPartecipazione.helper}"/>

<s:set var="presentaPartecipazione" value="%{#partecipazione.tipoEvento == 1}" />
<s:set var="invioOfferta" value="%{#partecipazione.tipoEvento == 2}" />

<%--
presentaPartecipazione: ${presentaPartecipazione}=<s:property value="%{#presentaPartecipazione}"/><br/>
invioOfferta: ${invioOfferta}=<s:property value="%{#invioOfferta}"/><br/>
--%>

<s:if test="%{#presentaPartecipazione}">
	<c:set var="titolo"><wp:i18n key='TITLE_PAGE_RICHPART_PRESENTA_PARTECIPAZIONE'/></c:set>
	<s:if test="%{#partecipazione.garaTelematica}">
		<c:set var="codiceBalloon" value="BALLOON_PART_GARA_RIEPILOGO_GARATEL"/>
	</s:if>
	<s:else>
		<c:set var="codiceBalloon" value="BALLOON_PART_GARA_RIEPILOGO"/>
	</s:else>
</s:if>
<s:elseif test="%{#invioOfferta}">
	<c:set var="titolo"><wp:i18n key='TITLE_PAGE_RICHPART_PRESENTA_OFFERTA'/></c:set>
	<s:if test="%{#partecipazione.garaTelematica}">
		<c:set var="codiceBalloon" value="BALLOON_INVIO_OFFERTA_RIEPILOGO_GARATEL"/>
	</s:if>
	<s:else>
		<c:set var="codiceBalloon" value="BALLOON_INVIO_OFFERTA_RIEPILOGO"/>
	</s:else>
</s:elseif>
<s:else>
	<c:set var="titolo"><wp:i18n key='TITLE_PAGE_RICHPART_PRESENTA_DOCUMENTAZIONE'/></c:set>
	<c:set var="codiceBalloon" value="BALLOON_INVIO_DOC_ART48_RIEPILOGO"/>
</s:else>	


<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/send_blockUI.jsp"/>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>
<c:set var="codiceTitolo" value="${partecipazione.getIdBando()}"/>
<div class="portgare-view">

	<h2>${titolo} [${codiceTitolo}]</h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/field_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_messages.jsp" />

	<jsp:include page="stepsPartecipazione.jsp" />

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="${codiceBalloon}"/>
	</jsp:include>

	<form action="<wp:action path="/ExtStr2/do/FrontEnd/RichPartBando/processPageRiepilogo.action" />" method="post">
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
		
		<fieldset>
			<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_RIEPILOGO" /></legend>

			<div class="fieldset-row first-row">
				<div class="label">
					<label><wp:i18n key="LABEL_GARETEL_OGGETTO" /></label>
				</div>
				<div class="element">
					<s:property value="%{#partecipazione.descBando}" />
				</div>
			</div>

			<div class="fieldset-row">
				<div class="label">
					<label><wp:i18n key="LABEL_OPERATORE_ECONOMICO" /></label>
				</div>
				<div class="element">
					<s:property value="%{#partecipazione.datiPrincipaliImpresa.ragioneSociale}" />
				</div>
			</div>

			<div class="fieldset-row">
				<div class="label">
					<label><wp:i18n key="LABEL_LEGALI_RAPPRESENTANTI" /></label>
				</div>
				<div class="element">
					<s:if test="%{#partecipazione.legaliRappresentantiImpresa.size() > 0}">
						<ul class="list">
							<s:iterator value="%{#partecipazione.legaliRappresentantiImpresa.iterator()}" var="legaleRappresentante" status="stat">
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
					<label><wp:i18n key="LABEL_DIRETTORI_TECNICI" /> </label>
				</div>
				<div class="element">
					<s:if test="%{#partecipazione.direttoriTecniciImpresa.size() > 0}">
						<ul class="list">
							<s:iterator value="%{#partecipazione.direttoriTecniciImpresa.iterator()}" var="direttoreTecnico" status="stat">
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

			<div class='fieldset-row <s:if test="%{!(#partecipazione.lottiDistinti || #partecipazione.plicoUnicoOfferteDistinte)}">last-row</s:if>'>
				<div class="label">
					<label><wp:i18n key="LABEL_PARTECIPA_COME_MANDATARIA_RTI" /></label>
				</div>
				<div class="element">
					<s:if test="%{#partecipazione.rti}"><wp:i18n key="LABEL_YES" />, <wp:i18n key="LABEL_PER_CONTO_DI" /> <s:property value="%{#partecipazione.denominazioneRTI}"/></s:if>
					<s:else><wp:i18n key="LABEL_NO" /></s:else>
				</div>
			</div>
			
			<!-- liste partecipanti RTI (mandanti/consorziate esecutrici)-->
			<s:if test="%{#partecipazione.rti}">
				<s:if test="%{#partecipazione.componenti != null && #partecipazione.componenti.size() > 0}">
					<div class="fieldset-row">
						<div class="label">
							<label>
								<s:if test="%{#partecipazione.impresa.consorzio}">
									<wp:i18n key="LABEL_CONSORZIATE_ESECUTRICI"/>
								</s:if>
								<s:else>
									<wp:i18n key="LABEL_MANDANTI"/>
								</s:else>
							</label>
						</div>
						<div class="element">
							<ul class="list">
								<s:iterator value="%{#partecipazione.componenti}" var="partecipante" status="status">
									<li class='<s:if test="%{#status.first}">first</s:if> <s:if test="%{#status.last}">last</s:if>'>
										<s:property value="%{#partecipante.ragioneSociale}" />
									</li>
								</s:iterator>
							</ul>
						</div>
					</div>
				</s:if>
			</s:if>	

			<s:if test="%{#partecipazione.lottiDistinti || #partecipazione.plicoUnicoOfferteDistinte}">
				<%-- 
				TEMPORANEAMENTE DISABILITATI
				<c:set var="showLottiNonAmmessi" 
				       value="${invioOfferta && not empty(lottiNonAmmessiCodice)}"/>
				 --%>
				<c:set var="showLottiNonAmmessi" value="false"/>
				
				<div class='fieldset-row <c:if test="${not showLottiNonAmmessi}">last-row</c:if>'>
					<div class="label">
						<s:if test="%{#partecipazione.garaTelematica}">
							<label><wp:i18n key="LABEL_LOTTI" /></label>
						</s:if>
						<s:else>
							<label><wp:i18n key="LABEL_CODICE_A_BARRE_PER" /></label>
						</s:else>
					</div>
					<div class="element">
						<ul class="list">
							<s:iterator value="codiciInterni" var="currLotto" status="status">
								<li class='<s:if test="%{#status.first}">first</s:if> <s:if test="%{#status.last}">last</s:if>'>
									<s:if test="%{#partecipazione.garaTelematica}">
										<wp:i18n key="LABEL_LOTTO" /> <s:property value="currLotto"/>&nbsp;-&nbsp;
									</s:if>	
									<s:property value="%{lotti.get(#status.index)}"/>
								</li>
							</s:iterator> 
						</ul>
					</div>
				</div>
				
				<!-- Lotti non ammessi -->
				<c:if test="${showLottiNonAmmessi}">
					<div class="fieldset-row last-row">
						<div class="label">
							<s:if test="%{#partecipazione.garaTelematica}">
							</s:if>
							<label><wp:i18n key="LABEL_LOTTI_NON_AMMESSI" /></label>
						</div>
						<div class="element">
							<ul class="list">
								<s:iterator value="lottiNonAmmessiCodice" var="currLotto" status="status">
									<li class='<s:if test="%{#status.first}">first</s:if> <s:if test="%{#status.last}">last</s:if>'>
										<i>
										<s:if test="%{#partecipazione.garaTelematica}">
											<wp:i18n key="LABEL_LOTTO" /> <s:property value="currLotto"/>&nbsp;-&nbsp;
										</s:if>	
										<s:property value="%{lottiNonAmmessi.get(#status.index)}"/>
										</i>
									</li>
								</s:iterator> 
							</ul>
						</div>
					</div>
				</c:if>
				
			</s:if>
						
		</fieldset>

		<div class="azioni">
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_previous.jsp" />
			
			<wp:i18n key="BUTTON_CONFIRM" var="valueConfirmButton" />
			<wp:i18n key="TITLE_CONFERMA_E_INOLTRA" var="titleConfirmButton" />
			<s:submit value="%{#attr.valueConfirmButton}" title="%{#attr.titleConfirmButton}" cssClass="button block-ui" method="send"></s:submit>
			
			<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/button_cancel.jsp" />
		</div>
	</form>
</div>
