<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>

<script type="text/javascript" src='<wp:resourceURL/>static/js/jquery.treeview.js'></script>
<script type="text/javascript" src='<wp:resourceURL/>static/js/ppgare/categorieIscrizione.js'></script>
<script type="text/javascript">
<!--//--><![CDATA[//><!--
var collapseTree = false;
var labelCoprimiCategorie = '<wp:i18n key="LABEL_LISTA_CATEGORIE_COMPRIMI" />';
var labelEspandiCategorie = '<wp:i18n key="LABEL_LISTA_CATEGORIE_ESPANDI" />';
//--><!]]></script>

<wp:headInfo type="CSS" info="jquery/treeview/jquery.treeview.css" />


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2><wp:i18n key="TITLE_PAGE_CATEGORIE_OE_DETTAGLIO_CATALOGO" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_CATEGORIE_OPERATORE_DETTAGLIO_CATALOGO"/>
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />

	<h3 class="detail-section-title"><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_CATEGORIE_PRESTAZIONI" /></h3>

    <fieldset>
        <legend><wp:i18n key='LABEL_ISCRALBO_CATEGORIE_ELENCO_OE'/></legend>

        <!-- Visualizzazione albero categorie -->
        <jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/bandi/inc/categorieIscrizioneOperatore.jsp" />
    </fieldset>
 	
	<div class="back-link">
		<a href="<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/viewIscrizione.action" />&amp;codice=${param.codice}&amp;ext=${param.ext}&amp;${tokenHrefParams}">
			<wp:i18n key="LINK_BACK_TO_ISCRIZIONE" />
		</a>
	</div>
</div>