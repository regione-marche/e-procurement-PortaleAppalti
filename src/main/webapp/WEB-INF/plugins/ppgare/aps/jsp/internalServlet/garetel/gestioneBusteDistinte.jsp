<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<s:if test="%{operazione == inviaOfferta}">
		<%-- MENU OFFERTA --%>
		<h2>
			<%-- Invio buste telematiche <s:if test="%{operazione == inviaOfferta}">offerta</s:if><s:else>partecipazione</s:else> --%>
			<s:if test="%{operazione == inviaOfferta}">
				<wp:i18n key='TITLE_PAGE_GARETEL_INVIO_BUSTE_OFFERTA'/>
			</s:if>
			<s:else>
				<wp:i18n key='TITLE_PAGE_GARETEL_INVIO_BUSTE_PARTECIPAZIONE'/>
			</s:else>
			[${codiceGara}]
		</h2>
	
		<s:if test="%{operazione == inviaOfferta}">
			<c:set var="keyMsg" value="BALLOON_INVIO_BUSTE_TELEMATICHE_OFFERTA"/>
		</s:if>
		<s:else>
			<c:set var="keyMsg" value="BALLOON_INVIO_BUSTE_TELEMATICHE_PARTECIPAZIONE"/>
		</s:else>
		
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
			<jsp:param name="keyMsg" value="${keyMsg}"/>
		</jsp:include>
	
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_messages.jsp" />
		
		<div class="menu-gestione">
			<ul>
				<%-- INIZIA COMPILAZIONE --%>
				<li class='bkg config <s:if test="%{(bustaAmmAlreadySent || bustaTecAlreadySent || bustaEcoAlreadySent) || protocollazioneMailFallita}">disabled</s:if>'>
					<s:if test="%{operazione == inviaOfferta}">
						<a class="menu-item-link go" 
							title='<s:if test="%{!(bustaAmmAlreadySent || bustaTecAlreadySent || bustaEcoAlreadySent)}"><wp:i18n key="LABEL_GARETEL_WIZARD_INIZIA_OFFERTA"/></s:if>
								   <s:else>(<wp:i18n key="LABEL_FUNZIONE_DISABILITATA"/>) <wp:i18n key="LABEL_GARETEL_WIZARD_INVIO_IN_CORSO"/></s:else>' 
							href='<s:if test="%{!(bustaAmmAlreadySent || bustaTecAlreadySent || bustaEcoAlreadySent)}">
									<wp:action path="/ExtStr2/do/FrontEnd/RichPartBando/newInvioOfferta.action"/>&amp;codice=${codiceGara}&amp;progressivoOfferta=${progressivoOfferta}&amp;${tokenHrefParams}
								  </s:if>'>
							<span class="menu-item-link-label"><wp:i18n key='LABEL_INIZIA_COMPILAZIONE_OFFERTA'/></span>
						</a>
					</s:if>
					<s:else>
						<a class="menu-item-link go"
							title="<wp:i18n key='LABEL_GARETEL_WIZARD_INIZIA_DOMANDA_PARTECIPAZIONE'/>" 
							href='<wp:action path="/ExtStr2/do/FrontEnd/RichPartBando/newPartecipazione.action"/>&amp;codice=${codice}&amp;progressivoOfferta=${progressivoOfferta}&amp;${tokenHrefParams}'>
							<span class="menu-item-link-label"><wp:i18n key='LABEL_INIZIA_COMPILAZIONE_DOMANDA'/></span>
						</a>
					</s:else>
				</li>
				
				<%-- BUSTA AMMINISTRATIVA --%>
				<s:if test="%{!noBustaAmministrativa}">
					<li class='bkg administration <s:if test="%{(lottiSelezionati <= 0) || comunicazione == null || bustaAmmAlreadySent || protocollazioneMailFallita}">disabled</s:if>'>
						<a class="menu-item-link go"
							title='<s:if test="%{comunicazione != null && !bustaAmmAlreadySent}"><wp:i18n key="LABEL_GARETEL_WIZARD_MODIFICA_BUSTA_AMMINISTRATIVA"/></s:if>
								   <s:elseif test="%{comunicazione == null}">(<wp:i18n key="LABEL_FUNZIONE_DISABILITATA"/>) <wp:i18n key="LABEL_GARETEL_WIZARD_NO_COMUNICAZIONI_STATO_BOZZA"/></s:elseif>
								   <s:else>(<wp:i18n key="LABEL_FUNZIONE_DISABILITATA"/>) <wp:i18n key="LABEL_GARETEL_WIZARD_BUSTAAMM_GIA_INOLTRATA"/></s:else>'
							href='<s:if test="%{(lottiSelezionati > 0) && (comunicazione != null && !bustaAmmAlreadySent)}">
									<wp:action path="/ExtStr2/do/FrontEnd/GareTel/openPageDocumenti.action" />&amp;tipoBusta=${BUSTA_AMMINISTRATIVA}&amp;codiceGara=${codiceGara}&amp;operazione=${operazione}&amp;progressivoOfferta=${progressivoOfferta}&amp;${tokenHrefParams}
								  </s:if><s:else>#</s:else>'>
							<span class="menu-item-link-label"><wp:i18n key="LABEL_BUSTA_AMMINISTRATIVA" /></span>
						</a>
					</li>
				</s:if>
				
				<%-- BUSTE TECNICHE --%>
				<s:if test="%{almenoUnaBustaTecnica}">
					<li class='bkg technology <s:if test="%{comunicazione == null || bustaTecAlreadySent || protocollazioneMailFallita}">disabled</s:if>'>
						<a class="menu-item-link go"
							title='<s:if test="%{comunicazione != null && !bustaTecAlreadySent && almenoUnaBustaTecnica}"><wp:i18n key="LABEL_GARETEL_WIZARD_MODIFICA_BUSTA_TECNICA"/></s:if>
								   <s:elseif test="%{comunicazione == null || !almenoUnaOffertaTecnica}">(<wp:i18n key='LABEL_FUNZIONE_DISABILITATA'/>) <wp:i18n key="LABEL_GARETEL_WIZARD_NO_COMUNICAZIONI_STATO_BOZZA"/><wp:i18n key="LABEL_GARETEL_WIZARD_GARA_NO_OEPV_BUSTATEC"/></s:elseif>
								   <s:else>(<wp:i18n key='LABEL_FUNZIONE_DISABILITATA'/>) <wp:i18n key="LABEL_GARETEL_WIZARD_BUSTATEC_GIA_INOLTRATA"/></s:else>'
							href='<s:if test="%{comunicazione != null && !bustaTecAlreadySent && offertaTecnica}">
									<wp:action path="/ExtStr2/do/FrontEnd/GareTel/openPageListaBusteTecnicheDistinte.action" />&amp;tipoBusta=${BUSTA_TECNICA}&amp;codiceGara=${codiceGara}&amp;operazione=${operazione}&amp;progressivoOfferta=${progressivoOfferta}&amp;${tokenHrefParams}
								  </s:if><s:else>#</s:else>'>
							<span class="menu-item-link-label"><wp:i18n key="LABEL_BUSTE_TECNICHE" /></span>
						</a>
					</li>
				</s:if>
				
				<%-- BUSTE ECONOMICHE --%>
				<s:if test="%{almenoUnaBustaEconomica}">
					<li class='bkg economy <s:if test="%{comunicazione == null || bustaEcoAlreadySent || protocollazioneMailFallita}">disabled</s:if>'>
						<a class="menu-item-link go" 
							title='<s:if test="%{comunicazione != null && !bustaEcoAlreadySent}"><wp:i18n key="LABEL_GARETEL_WIZARD_MODIFICA_BUSTA_ECONOMICA"/></s:if>
								   <s:elseif test="%{comunicazione == null}">(<wp:i18n key='LABEL_FUNZIONE_DISABILITATA'/>) <wp:i18n key="LABEL_GARETEL_WIZARD_NO_COMUNICAZIONI_STATO_BOZZA"/></s:elseif>
								   <s:else>(<wp:i18n key='LABEL_FUNZIONE_DISABILITATA'/>) <wp:i18n key="LABEL_GARETEL_WIZARD_BUSTAECO_GIA_INOLTRATA"/></s:else>'
							href='<s:if test="%{comunicazione != null && !bustaEcoAlreadySent}">
									<wp:action path="/ExtStr2/do/FrontEnd/GareTel/openPageListaBusteEconomicheDistinte.action" />&amp;tipoBusta=${BUSTA_ECONOMICA}&amp;codiceGara=${codiceGara}&amp;operazione=${operazione}&amp;progressivoOfferta=${progressivoOfferta}&amp;${tokenHrefParams}
								  </s:if><s:else>#</s:else>'>
							<span class="menu-item-link-label"><wp:i18n key="LABEL_BUSTE_ECONOMICHE" /></span>
						</a>
					</li>
				</s:if>
				
				<%-- RIEPILOGO --%>
				<li class='bkg summary <s:if test="%{(nessunDocPerAmm && nessunDocPerTec && nessunDocPerEco) || protocollazioneMailFallita}">disabled</s:if>'>
					<a class="menu-item-link go"
						title='<s:if test="%{!nessunDocPerAmm || !nessunDocPerTec || !nessunDocPerEco}"><wp:i18n key="LABEL_GARETEL_WIZARD_RIEPILOGO_DATI"/></s:if>
							   <s:else>(<wp:i18n key="LABEL_FUNZIONE_DISABILITATA"/>) <wp:i18n key="LABEL_GARETEL_WIZARD_NO_COMUNICAZIONI_BUSTE"/></s:else>'
						href='<s:if test="%{!nessunDocPerAmm || !nessunDocPerTec || !nessunDocPerEco}">
								<wp:action path="/ExtStr2/do/FrontEnd/GareTel/openRiepilogoOfferteDistinte.action" />&amp;codiceGara=${codiceGara}&amp;operazione=${operazione}&amp;progressivoOfferta=${progressivoOfferta}&amp;${tokenHrefParams}
							  </s:if><s:else>#</s:else>'>
						<span class="menu-item-link-label"><wp:i18n key="LABEL_RIEPILOGO"/></span>
					</a>
				</li>
				
				<%-- CONFERMA E INVIA --%>
				<li class='last bkg send <s:if test="%{(lottiSelezionati <= 0) || (nessunDocPerAmm && nessunDocPerTec && nessunDocPerEco)}">disabled</s:if>'>
					<a class="menu-item-link go"
						title='<s:if test="%{operazione == inviaOfferta}"><wp:i18n key="LABEL_GARETEL_WIZARD_CONFERMA_INOLTRA_OFFERTA"/></s:if>
							   <s:else><wp:i18n key="LABEL_GARETEL_WIZARD_CONFERMA_INOLTRA_DOMANDA"/></s:else>'
						href='<s:if test="%{(lottiSelezionati > 0) && (!nessunDocPerAmm || !nessunDocPerTec || !nessunDocPerEco)}">
								<wp:action path="/ExtStr2/do/FrontEnd/GareTel/confirmInvioOfferteDistinte.action"/>&amp;codice=${codiceGara}&amp;operazione=${operazione}&amp;progressivoOfferta=${progressivoOfferta}&amp;${tokenHrefParams}
							  </s:if><s:else>#</s:else>'>
						<span class="menu-item-link-label">
							<s:if test="%{operazione == inviaOfferta}"><wp:i18n key="LABEL_CONFERMA_INVIO_OFFERTA"/></s:if>
							<s:else><wp:i18n key="LABEL_CONFERMA_INVIO_DOMANDA"/></s:else>
						</span>
					</a>
				</li>
				
			</ul>
		</div>
	</s:if>
	<s:else>
		<%-- MENU DOMANDA PARTECIPAZIONE --%>
		<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/garetel/inc/gestioneBustePartecipazione.jsp" />
	</s:else>
			
	<div class="back-link">
		<a href="<wp:action path="/ExtStr2/do/FrontEnd/Bandi/view.action" />&amp;codice=${codiceGara}&amp;${tokenHrefParams}">
			<wp:i18n key="LINK_BACK_TO_PROCEDURE" />
		</a>
	</div>
</div>