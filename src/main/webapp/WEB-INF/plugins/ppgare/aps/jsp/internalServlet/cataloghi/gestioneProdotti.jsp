<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>

<c:set var="urlInserisciProdotti">
	<s:if test="%{variazioneOfferta || dettaglioComunicazioneVariazioneOfferta != null || dettaglioComunicazione != null}">
		 #
	</s:if>
	<s:else>
		<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/inserProdottoChoices.action"/>
	</s:else>
</c:set>

<c:set var="urlModificaProdotti">
	<s:if test="%{variazioneOfferta || dettaglioComunicazioneVariazioneOfferta != null || dettaglioComunicazione != null}">
		#
	</s:if>
	<s:else>
		<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/viewProdottiSistema.action"/>
	</s:else>
</c:set>

<c:set var="urlModificaPrezzi">
	<s:if test="%{(modifichePresenti && !variazioneOfferta) || dettaglioComunicazione != null || dettaglioComunicazioneVariazioneOfferta != null}">
		#
	</s:if>
	<s:else>
		<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/variazionePrezziScadenzeChoices.action"/>
	</s:else>
</c:set>

<c:set var="urlRiepilogo">
	<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/viewProdottiCarrello.action" />
</c:set>

<c:set var="urlInvia">
	<s:if test="%{modifichePresenti && dettaglioComunicazione == null && dettaglioComunicazioneVariazioneOfferta == null}">
	 	<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/openPageInviaModificheProdotti.action"/>
	</s:if>
	<s:else>
		#
	</s:else>
</c:set>


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2><wp:i18n key="TITLE_PAGE_CATALOGHI_GESTIONE_PRODOTTI" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_GESTIONE_PRODOTTI"/>
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
	
	<div class="menu-gestione">
		<ul>
			<li class="bkg new <s:if test="%{variazioneOfferta || dettaglioComunicazioneVariazioneOfferta != null || dettaglioComunicazione != null}">disabled</s:if>">
				<form action='${urlInserisciProdotti}' method="post">
		    		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
		    		<input type="hidden" name="catalogo" value="${catalogo}" />

					<a class="menu-item-link go"
						title="<wp:i18n key='TITLE_APRI_WIZARD_INSERIMENTO_PRODOTTO' />"
						href="javascript:;" onclick="parentNode.submit();" >
						<span class="menu-item-link-label"><wp:i18n key="LABEL_INSERISCI_PRODOTTI" /></span>
					</a>
				</form>
			</li>
			<li class="bkg edit <s:if test="%{variazioneOfferta || dettaglioComunicazioneVariazioneOfferta != null || dettaglioComunicazione != null}">disabled</s:if>">				
				<form action='${urlModificaProdotti}' method="post">
		    		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />

					<a class="menu-item-link go" 
						title="<wp:i18n key='TITLE_VISUALIZZA_E_MODIFICA_PRODOTTI' />"
						<%-- href='wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/viewProdottiSistema.action" />'> --%>
						href="javascript:;" onclick="parentNode.submit();" >
						<span class="menu-item-link-label"><wp:i18n key="LABEL_MODIFICA_PRODOTTI" /></span>
					</a>
				</form>
			</li>
			<li class="bkg economy <s:if test="%{(modifichePresenti && !variazioneOfferta) || dettaglioComunicazione != null || dettaglioComunicazioneVariazioneOfferta != null}">disabled</s:if>">				
				<form action='${urlModificaPrezzi}' method="post">
		    		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
		    		<input type="hidden" name="catalogo" value="${catalogo}" />

					<a class="menu-item-link go" 
						 title="<wp:i18n key='TITLE_VISUALIZZA_DOWNLOAD_UPLOAD_XLS_PREZZI' />"
						 href="javascript:;" onclick="parentNode.submit();" > 
						<span class="menu-item-link-label"><wp:i18n key="LABEL_MODIFICA_PREZZI_SCADENZE" /></span>
					</a>
				</form>
			</li>
			<li class='bkg summary <s:if test="%{modifichePresenti || bozze > 0}">with-info</s:if>'>
				<form action='${urlRiepilogo}' method="post">
		    		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />

					<a class="menu-item-link go" 
						title="<wp:i18n key='TITLE_VISUALIZZA_CARRELLO_MODIFICHE' />"
						href="javascript:;" onclick="parentNode.submit();" >
						<span class="menu-item-link-label"><wp:i18n key="LABEL_RIEPILOGO_MODIFICE_IN_CORSO" /></span>
						<span class="info">
							<s:if test="%{inseriti > 0}"><wp:i18n key="LABEL_INSERITI" /> (<s:property value="%{inseriti}"/>)</s:if>
							<s:if test="%{inseriti > 0 && (modificati > 0 || archiviati > 0 || bozze > 0)}">,</s:if>
							<s:if test="%{modificati > 0}"><wp:i18n key="LABEL_MODIFICATI" /> (<s:property value="%{modificati}"/>)</s:if>
							<s:if test="%{modificati > 0 && (archiviati > 0 || bozze > 0)}">,</s:if>
							<s:if test="%{archiviati > 0}"><wp:i18n key="LABEL_ELIMINATI" /> (<s:property value="%{archiviati}"/>)</s:if>
							<s:if test="%{archiviati > 0 && (bozze > 0)}">,</s:if>
							<s:if test="%{bozze > 0}"><wp:i18n key="LABEL_BOZZE" /> (<s:property value="%{bozze}"/>)</s:if>
						</span>
					</a>
				</form>
			</li>
			<li class="last bkg send <s:if test="%{!modifichePresenti || dettaglioComunicazione != null || dettaglioComunicazioneVariazioneOfferta != null}">disabled</s:if>">								
				<form action='${urlInvia}' method="post">
		    		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
		    		
					<a class="menu-item-link go" 
						<s:if test="%{dettaglioComunicazione == null && dettaglioComunicazioneVariazioneOfferta == null && !variazioneOfferta && modifichePresenti}"> title='<wp:i18n key="TITLE_CONSENTE_TRASFERIMENTO_MODIFICHE_A_BO" />' </s:if>
						<s:elseif test="%{dettaglioComunicazioneVariazioneOfferta == null && variazioneOfferta && dettaglioComunicazione == null && modifichePresenti}"> title='<wp:i18n key="TITLE_CONSENTE_TRASFERIMENTO_VARIAZIONI_A_BO" />' </s:elseif>
						<s:elseif test="%{dettaglioComunicazione == null && dettaglioComunicazioneVariazioneOfferta == null && !modifichePresenti)}"> title='<wp:i18n key="TITLE_FUNZIONE_DISABILITATA_MODIFICHE_DA_INVIARE" />' </s:elseif>
						<s:else> title="<wp:i18n key='TITLE_FUNZIONE_DISABILITATA_RICHIESTA_GIA_INVIATA' />" </s:else>
						href="javascript:;" onclick="parentNode.submit();" >
						<span class="menu-item-link-label"><wp:i18n key="LABEL_CONFERMA_INVIA_MODIFICHE" /></span>
					</a>
				</form>
			</li>
		</ul>
	</div>
	<div class="back-link">
		<a href='<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/viewIscrizione.action" />&amp;codice=${catalogo}&amp;ext=${param.ext}'>
			<wp:i18n key="LINK_BACK_TO_ISCRIZIONE" />
		</a>
	</div>
</div>