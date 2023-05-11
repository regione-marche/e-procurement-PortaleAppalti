<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>

<h2><wp:i18n key='TITLE_PAGE_GARETEL_INVIO_BUSTE_PARTECIPAZIONE'/> [${codice}]</h2>

<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
	<jsp:param name="keyMsg" value="BALLOON_INVIO_BUSTE_TELEMATICHE_PARTECIPAZIONE"/>
</jsp:include>

<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_messages.jsp" />

<div class="menu-gestione">
	<ul>
		<%-- INIZIA COMPILAZIONE --%>
		<li class='bkg config <s:if test="%{bustaPreqAlreadySent || protocollazioneMailFallita}">disabled</s:if>'>
			<a class="menu-item-link go" 
				title="<s:if test='%{!(bustaPreqAlreadySent || protocollazioneMailFallita)}'><wp:i18n key='LABEL_GARETEL_WIZARD_INIZIA_DOMANDA_PARTECIPAZIONE'/></s:if>
					   <s:else>(<wp:i18n key='LABEL_FUNZIONE_DISABILITATA'/>) <wp:i18n key='LABEL_GARETEL_WIZARD_INVIO_IN_CORSO'/></s:else>" 
				href='<s:if test="%{!(bustaPreqAlreadySent || protocollazioneMailFallita)}">
						<wp:action path="/ExtStr2/do/FrontEnd/RichPartBando/newPartecipazione.action"/>&amp;codice=${codice}&amp;progressivoOfferta=${progressivoOfferta}&amp;${tokenHrefParams}
					  </s:if><s:else>#</s:else>'>
				<span class="menu-item-link-label"><wp:i18n key='LABEL_INIZIA_COMPILAZIONE_DOMANDA'/></span>
			</a>
		</li>
		
		<%-- BUSTA PREQUALIFICA --%>
		<li class='bkg administration <s:if test="%{comunicazione == null || bustaPreqAlreadySent || protocollazioneMailFallita}">disabled</s:if>'>
			<a class="menu-item-link go" 
				title="<s:if test='%{comunicazione != null && !bustaPreqAlreadySent}'><wp:i18n key='LABEL_GARETEL_WIZARD_DOCUMENTI_PREQUALIFICA'/></s:if>
						<s:elseif test='%{comunicazione == null}'>(<wp:i18n key='LABEL_FUNZIONE_DISABILITATA'/>) <wp:i18n key='LABEL_GARETEL_WIZARD_NO_COMUNICAZIONI_STATO_BOZZA'/></s:elseif>
						<s:else>(<wp:i18n key='LABEL_FUNZIONE_DISABILITATA'/>) <wp:i18n key='LABEL_GARETEL_WIZARD_COMUNICAZIONE_GIA_INOLTRATA'/></s:else>" 
				href='<s:if test="%{comunicazione != null && !bustaPreqAlreadySent}">
						<wp:action path="/ExtStr2/do/FrontEnd/GareTel/openPageDocumenti.action" />&amp;tipoBusta=${BUSTA_PRE_QUALIFICA}&amp;codice=${codice}&amp;progressivoOfferta=${progressivoOfferta}&amp;operazione=${operazione}&amp;${tokenHrefParams}
					  </s:if><s:else>#</s:else>'>
				<span class="menu-item-link-label"><wp:i18n key="LABEL_BUSTA_PREQUALIFICA" /></span>
			</a>
		</li>
		
		<%-- RIEPILOGO --%>
		<li class='bkg summary <s:if test="%{comunicazioneBustaPreq == null || protocollazioneMailFallita}">disabled</s:if>'>
			<a class="menu-item-link go" 
				title="<s:if test='%{comunicazioneBustaPreq != null}'><wp:i18n key='LABEL_GARETEL_WIZARD_RIEPILOGO'/></s:if>
					   <s:else>(<wp:i18n key='LABEL_FUNZIONE_DISABILITATA'/>) <wp:i18n key='LABEL_GARETEL_WIZARD_NO_COMUNICAZIONI_BUSTE'/></s:else>" 
				href='<s:if test="%{comunicazioneBustaPreq != null}">
						<wp:action path="/ExtStr2/do/FrontEnd/GareTel/openRiepilogo.action" />&amp;codice=${codice}&amp;progressivoOfferta=${progressivoOfferta}&amp;operazione=${operazione}&amp;${tokenHrefParams}
					  </s:if><s:else>#</s:else>'>
				<span class="menu-item-link-label"><wp:i18n key="LABEL_RIEPILOGO" /></span>
			</a>
		</li>
		
		<%-- CONFERMA E INVIA --%>
		<li class='last bkg send <s:if test="%{(comunicazioneBustaPreq == null) && !(comunicazione != null && bustaPreqAlreadySent)}">disabled</s:if>'>
			<a class="menu-item-link go" 
				title="<wp:i18n key='LABEL_GARETEL_WIZARD_CONFERMA_INOLTRA_DOMANDA'/>"
				href='<s:if test="%{(comunicazioneBustaPreq != null) || (comunicazione != null && bustaPreqAlreadySent)}">
						<wp:action path="/ExtStr2/do/FrontEnd/GareTel/confirmInvio.action"/>&amp;codice=${codice}&amp;progressivoOfferta=${progressivoOfferta}&amp;operazione=${operazione}&amp;${tokenHrefParams}
					  </s:if><s:else>#</s:else>'>
				<span class="menu-item-link-label"><wp:i18n key="LABEL_CONFERMA_INVIO_DOMANDA" /></span>
			</a>
		</li>
	</ul>
</div>
