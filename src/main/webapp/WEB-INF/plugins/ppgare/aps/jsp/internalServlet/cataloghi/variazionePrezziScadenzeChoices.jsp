<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2><wp:i18n key="TITLE_PAGE_MODIFICA_PREZZI_SCADENZE_CATALOGO" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/field_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_MODIFICA_PREZZI_SCADENZE"/>
	</jsp:include>
	
	<s:url id="urlExcelVariazioneScadenzePrezziDownload" namespace="/do/FrontEnd/Cataloghi" action="createVariazioneScadenzePrezziExcel">
		<s:param name="urlPage">${currentPageUrl}</s:param>
		<s:param name="currentFrame">${param.currentFrame}</s:param>
		<s:param name="catalogo">${catalogo}</s:param>
	</s:url>
	
	<h3><wp:i18n key="LABEL_SELEZIONA_UNA_OPZIONE" />:</h3>	
	<ul>
		<li>
			<p>
				<a class="list-element bkg go important" title='<wp:i18n key="LABEL_SCARICA_XLS_CON_CATALOGO_PRODOTTI" />' 
					 href='${urlExcelVariazioneScadenzePrezziDownload}'>
					<wp:i18n key="LABEL_SCARICA_XLS_CON_TUO_CATALOGO_PRODOTTI" />
				</a>
				<br/>
				<span>
					<wp:i18n key="LABEL_SCARICA_XLS_PRODOTTI_VARIAZIONE_PREZZI" />
				</span>
			</p>
		</li>
		<li>
			<p>
				<a class="list-element bkg go important" title='<wp:i18n key="LABEL_APRI_WIZARD_MODIFICA_PREZZI" />' 
					 href='<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/openPageImportVariazionePrezziScadenze.action"/>&amp;catalogo=${catalogo}'>
					<wp:i18n key="LABEL_IMPORTA_NUOVI_PREZZI_E_VALIDA" />
				</a>
				<br/>
				<span>
					<wp:i18n key="LABEL_AGGIORNA_CATALOGO_PREZZI_INSERITI_IN_XLS" />
				</span>
			</p>
		</li>
	</ul>
	<div class="back-link">
		<a href="<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/openGestioneProdotti.action" />&amp;ext=${param.ext}&amp;catalogo=${catalogo}">
			<wp:i18n key="LINK_BACK_TO_GESTIONE_PRODOTTI" />
		</a>
	</div>
</div>