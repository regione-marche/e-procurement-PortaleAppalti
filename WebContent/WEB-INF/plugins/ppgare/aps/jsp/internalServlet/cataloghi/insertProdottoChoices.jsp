<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2><wp:i18n key="TITLE_PAGE_CATALOGHI_INSERIMENTO_PRODOTTI_CATALOGO" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/field_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_WIZ_PRODOTTO_INS_PRODOTTO_CHOICES"/>
	</jsp:include>
	<ul>
		<li>
			<p>
				<a class="list-element bkg go important" 
					title='<wp:i18n key="TITLE_WIZARD_INSERIMENTO_NUOVO_PRODOTTO" />' 
					href='<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/initProdottoWizard.action"/>&amp;catalogo=${catalogo}&amp;${tokenHrefParams}'>
					<wp:i18n key="LABEL_INSERISCI_UN_PRODOTTO" />
				</a>
				<br/>
				<span>
					<wp:i18n key="LABEL_INFO_WIZARD_INSERIMENTO_NUOVO_PRODOTTO" />
				</span>
			</p>
		</li>
		<li>
			<p>
				<a class="list-element bkg go important" 
					title='<wp:i18n key="TITLE_WIZARD_EXPORT_ARTICOLI_CATALOGO" />' 
					href='<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/openPageSelezioneArticoli.action"/>&amp;catalogo=${catalogo}&amp;${tokenHrefParams}'>
					<wp:i18n key="LABEL_SCARICA_FILE_XLS" />
				</a>
				<br/>
				<span>
					<wp:i18n key="LABEL_INFO_WIZARD_EXPORT_ARTICOLI_CATALOGO" />
				</span>
			</p>
		</li>
		<li>
			<p>
				<a class="list-element bkg go important" 
					title='<wp:i18n key="TITLE_WIZARD_IMPORT_MASSIVO_PRODOTTI" />' 
					href='<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/openPageImportProducts.action"/>&amp;catalogo=${catalogo}&amp;${tokenHrefParams}'>
					<wp:i18n key="LABEL_INSERISCI_PRODOTTI_CARICATI_DA_XLS" /> 
				</a>
				<br/>
				<span>
					<wp:i18n key="LABEL_INFO_WIZARD_IMPORT_MASSIVO_PRODOTTI" /> 
				</span>
			</p>
		</li>
	</ul>
	<div class="back-link">
		<a href="<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/openGestioneProdotti.action" />&amp;ext=${param.ext}&amp;catalogo=${catalogo}&amp;${tokenHrefParams}">
			<wp:i18n key="LINK_BACK_TO_GESTIONE_PRODOTTI" />
		</a>
	</div>
</div>