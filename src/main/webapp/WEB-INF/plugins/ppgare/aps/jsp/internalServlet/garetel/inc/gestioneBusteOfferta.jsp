<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>

<c:set var="urlPartecipazione">
	<s:if test="%{!(bustaAmmAlreadySent || bustaTecAlreadySent || bustaEcoAlreadySent || protocollazioneMailFallita)}">
		<wp:action path="/ExtStr2/do/FrontEnd/RichPartBando/newInvioOfferta.action"/>
	</s:if><s:else>#</s:else>
</c:set>

<c:set var="urlAmministrativa">
	<s:if test="%{comunicazione != null && !bustaAmmAlreadySent}">
		<wp:action path="/ExtStr2/do/FrontEnd/GareTel/openPageDocumenti.action" />
	</s:if><s:else>#</s:else>
</c:set>

<c:set var="urlTecnica">
	<s:if test="%{comunicazione != null && !bustaTecAlreadySent && offertaTecnica}">
		<wp:action path="/ExtStr2/do/FrontEnd/GareTel/openPageDocumenti.action" />
	</s:if><s:else>#</s:else>
</c:set>

<c:set var="urlEconomica">
	<s:if test="%{comunicazione != null && !bustaEcoAlreadySent}">
		<s:if test="%{!offertaTelematica}">
			<wp:action path="/ExtStr2/do/FrontEnd/GareTel/openPageDocumenti.action" />
		</s:if>
		<s:else>
			<wp:action path="/ExtStr2/do/FrontEnd/GareTel/initOffTel.action" />
		</s:else>
	</s:if>
	<s:else>#</s:else>
</c:set>

<c:set var="urlRiepilogo">
	<s:if test="%{comunicazioneBustaAmm != null || comunicazioneBustaTec != null || (comunicazioneBustaEco != null && !costoFisso)}">
		<wp:action path="/ExtStr2/do/FrontEnd/GareTel/openRiepilogo.action" />
	</s:if>
	<s:else>#</s:else>
</c:set>

<c:set var="urlInvio">
	<s:if test="%{(comunicazioneBustaAmm != null || comunicazioneBustaTec != null || (comunicazioneBustaEco != null || !costoFisso)) || (comunicazione != null && (bustaAmmAlreadySent || bustaTecAlreadySent || bustaEcoAlreadySent))}">
		<wp:action path="/ExtStr2/do/FrontEnd/GareTel/confirmInvio.action"/>
	</s:if>
	<s:else>#</s:else>
</c:set>


<h2><wp:i18n key='TITLE_PAGE_GARETEL_INVIO_BUSTE_OFFERTA'/> [${codice}]</h2>

<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
	<jsp:param name="keyMsg" value="BALLOON_INVIO_BUSTE_TELEMATICHE_OFFERTA"/>
</jsp:include>

<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_messages.jsp" />

<div class="menu-gestione">
	<ul>
		<%-- INIZIA COMPILAZIONE --%>
		<li class='bkg config <s:if test="%{(bustaAmmAlreadySent || bustaTecAlreadySent || bustaEcoAlreadySent) || protocollazioneMailFallita}">disabled</s:if>'>
			<form action='${urlPartecipazione}' method="post">
	    		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
	    		<input type="hidden" name="codice" value="${codice}" />
 				<input type="hidden" name="progressivoOfferta" value="${progressivoOfferta}" />
 				
 				<c:set var="lblTitleIniziaCompilazione">
 					<s:if test="%{!(bustaAmmAlreadySent || bustaTecAlreadySent || bustaEcoAlreadySent || protocollazioneMailFallita)}"><wp:i18n key="LABEL_GARETEL_WIZARD_INIZIA_OFFERTA"/></s:if>
					<s:else>(<wp:i18n key="LABEL_FUNZIONE_DISABILITATA"/>) <wp:i18n key="LABEL_GARETEL_WIZARD_INVIO_IN_CORSO"/></s:else>
	 			</c:set>
 				
				<a class="menu-item-link go" href="javascript:;" onclick="parentNode.submit();" title="${lblTitleIniziaCompilazione}" > 
					<span class="menu-item-link-label"><wp:i18n key="LABEL_INIZIA_COMPILAZIONE_OFFERTA" /></span>
				</a>
			</form>
		</li>
		
		<%-- BUSTA AMMINISTRATIVA --%>
		<s:if test="%{!noBustaAmministrativa}">
			<li class='bkg administration <s:if test="%{comunicazione == null || bustaAmmAlreadySent || protocollazioneMailFallita}">disabled</s:if>'>
				<form action='${urlAmministrativa}' method="post">
		    		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
		    		<input type="hidden" name="tipoBusta" value="${BUSTA_AMMINISTRATIVA}" />
		    		<input type="hidden" name="codice" value="${codice}" />
	 				<input type="hidden" name="progressivoOfferta" value="${progressivoOfferta}" />
	 				<input type="hidden" name="operazione" value="${operazione}" />
	 				
	 				<c:set var="lblTitleBustaAmm">
 						<s:if test="%{comunicazione != null && !bustaAmmAlreadySent}"><wp:i18n key="LABEL_GARETEL_WIZARD_MODIFICA_BUSTA_AMMINISTRATIVA"/></s:if>
						<s:elseif test="%{comunicazione == null}">(<wp:i18n key="LABEL_FUNZIONE_DISABILITATA"/>) <wp:i18n key="LABEL_GARETEL_WIZARD_NO_COMUNICAZIONI_STATO_BOZZA"/></s:elseif>
						<s:else>(<wp:i18n key="LABEL_FUNZIONE_DISABILITATA"/>) <wp:i18n key="LABEL_GARETEL_WIZARD_BUSTAAMM_GIA_INOLTRATA"/></s:else>
	 				</c:set>
	 			
	 				<a class="menu-item-link go" href="javascript:;" onclick="parentNode.submit();" title="${lblTitleBustaAmm}" >
						<span class="menu-item-link-label"><wp:i18n key="LABEL_BUSTA_AMMINISTRATIVA" /></span>
					</a>
				</form>
			</li>
		</s:if>
		
		<%-- BUSTA TECNICA --%>
		<s:if test="%{offertaTecnica}">
			<li class='bkg technology <s:if test="%{comunicazione == null || bustaTecAlreadySent || protocollazioneMailFallita}">disabled</s:if>'>
				<form action='${urlTecnica}' method="post">
		    		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
		    		<input type="hidden" name="tipoBusta" value="${BUSTA_TECNICA}" />
		    		<input type="hidden" name="codice" value="${codice}" />
	 				<input type="hidden" name="progressivoOfferta" value="${progressivoOfferta}" />
	 				<input type="hidden" name="operazione" value="${operazione}" />
	 			
		 			<c:set var="lblTitleBustaTec">
	 					<s:if test="%{comunicazione != null && !bustaTecAlreadySent && offertaTecnica}"><wp:i18n key="LABEL_GARETEL_WIZARD_MODIFICA_BUSTA_TECNICA"/></s:if>
						<s:elseif test="%{comunicazione == null || !offertaTecnica}">(<wp:i18n key="LABEL_FUNZIONE_DISABILITATA"/>) <wp:i18n key="LABEL_GARETEL_WIZARD_NO_COMUNICAZIONI_STATO_BOZZA"/><wp:i18n key="LABEL_GARETEL_WIZARD_GARA_NO_OEPV_BUSTATEC"/></s:elseif>
						<s:else>(<wp:i18n key="LABEL_FUNZIONE_DISABILITATA"/>) <wp:i18n key="LABEL_GARETEL_WIZARD_BUSTATEC_GIA_INOLTRATA"/></s:else>
		 			</c:set>
	 				
	 				<a class="menu-item-link go" href="javascript:;" onclick="parentNode.submit();" title="${lblTitleBustaTec}" >
						<span class="menu-item-link-label"><wp:i18n key="LABEL_BUSTA_TECNICA" /></span>
					</a>
				</form>
			</li>
		</s:if>
		
		<%-- BUSTA ECONOMICA --%>
		<s:if test="%{!costoFisso}">
			<li class='bkg economy <s:if test="%{comunicazione == null || bustaEcoAlreadySent || protocollazioneMailFallita}">disabled</s:if>'>
				<form action='${urlEconomica}' method="post">
		    		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
		    		<input type="hidden" name="tipoBusta" value="${BUSTA_ECONOMICA}" />
		    		<input type="hidden" name="codice" value="${codice}" />
	 				<input type="hidden" name="progressivoOfferta" value="${progressivoOfferta}" />
	 				<input type="hidden" name="operazione" value="${operazione}" />
	 				
	 				<c:set var="lblTitleBustaEco">
 						<s:if test="%{comunicazione != null && !bustaEcoAlreadySent}"><wp:i18n key="LABEL_GARETEL_WIZARD_MODIFICA_BUSTA_ECONOMICA"/></s:if>
						<s:elseif test="%{comunicazione == null}">(<wp:i18n key="LABEL_FUNZIONE_DISABILITATA"/>) <wp:i18n key="LABEL_GARETEL_WIZARD_NO_COMUNICAZIONI_STATO_BOZZA"/></s:elseif>
						<s:else>(<wp:i18n key="LABEL_FUNZIONE_DISABILITATA"/>) <wp:i18n key="LABEL_GARETEL_WIZARD_BUSTAECO_GIA_INOLTRATA"/></s:else>
	 				</c:set>
	 				
					<a class="menu-item-link go" href="javascript:;" onclick="parentNode.submit();" title="${lblTitleBustaEco}">
						<span class="menu-item-link-label"><wp:i18n key="LABEL_BUSTA_ECONOMICA" /></span>
					</a>
				</form>
			</li>
		</s:if>

		<%-- RIEPILOGO --%>
		<li class='bkg summary <s:if test="%{(comunicazioneBustaAmm == null && comunicazioneBustaTec == null && comunicazioneBustaEco == null) || protocollazioneMailFallita}">disabled</s:if>'>
			<form action='${urlRiepilogo}' method="post">
	    		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
	    		<input type="hidden" name="codice" value="${codice}" />
 				<input type="hidden" name="progressivoOfferta" value="${progressivoOfferta}" />
 				<input type="hidden" name="operazione" value="${operazione}" />
 				
 				<c:set var="lblTitleRiepilogo">
 					<s:if test="%{comunicazioneBustaAmm != null || comunicazioneBustaTec != null || (comunicazioneBustaEco != null && !costoFisso)}"><wp:i18n key="LABEL_GARETEL_WIZARD_RIEPILOGO_DATI"/></s:if>
					<s:else>(<wp:i18n key="LABEL_FUNZIONE_DISABILITATA"/>) <wp:i18n key="LABEL_GARETEL_WIZARD_NO_COMUNICAZIONI_BUSTE"/></s:else>
	 			</c:set>
	 			
 				<a class="menu-item-link go" href="javascript:;" onclick="parentNode.submit();" title="${lblTitleRiepilogo}">
					<span class="menu-item-link-label"><wp:i18n key='LABEL_RIEPILOGO'/></span>
				</a>
			</form>
		</li>
		
		<%-- CONFERMA E INVIA --%>
		<li class='last bkg send <s:if test="%{(comunicazioneBustaAmm == null && comunicazioneBustaTec == null && comunicazioneBustaEco == null) && !(comunicazione != null && (bustaAmmAlreadySent || bustaTecAlreadySent || bustaEcoAlreadySent))}">disabled</s:if>'>
			<form action='${urlInvio}' method="post">
	    		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
	    		<input type="hidden" name="codice" value="${codice}" />
 				<input type="hidden" name="progressivoOfferta" value="${progressivoOfferta}" />
 				<input type="hidden" name="operazione" value="${operazione}" />
 				
 				<a class="menu-item-link go" href="javascript:;" onclick="parentNode.submit();" 
 					title="<wp:i18n key='LABEL_GARETEL_WIZARD_CONFERMA_INOLTRA_OFFERTA'/>" >
					<span class="menu-item-link-label"><wp:i18n key='LABEL_CONFERMA_INVIO_OFFERTA'/></span>
				</a>
			</form>
		</li>
	</ul>
</div>
