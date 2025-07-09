<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>

<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/jquery_treeview.jsp" />
<script type="text/javascript" src='<wp:resourceURL/>static/js/ppgare/categorieIscrizione.js'></script>

<script type="text/javascript">
<!--//--><![CDATA[//><!--
var collapseTree = true;
var labelCoprimiCategorie = '<wp:i18n key="LABEL_LISTA_CATEGORIE_COMPRIMI" />';
var labelEspandiCategorie = '<wp:i18n key="LABEL_LISTA_CATEGORIE_ESPANDI" />';
//--><!]]>
</script>



<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2><wp:i18n key="TITLE_PAGE_CATEGORIE_DETTAGLIO_CATALOGO" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_CATEGORIE_DETTAGLIO_CATALOGO"/>
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />

	<h3 class="detail-section-title"><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_CATEGORIE_PRESTAZIONI" /></h3>

	<form action="<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/viewCategorieIscrizione.action" />" method="post">
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
		<div class="search">
			<label for="filtroCategorie"><wp:i18n key="LABEL_FILTRA_CATEGORIE_PER" /></label> <input type="text" name="filtroCategorie" id="filtroCategorie" value="${param.filtroCategorie}" />
			<input type="hidden" name="codice" value="${param.codice}"/>
			<input type="hidden" name="ext" value="${param.ext}"/>
			<wp:i18n key="BUTTON_FILTRA_CATEGORIE_PER" var="valueFiltraButton" />
			<s:submit value="%{#attr.valueFiltraButton}" cssClass="button"/>
		</div>
	</form>
	
	<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/bandi/inc/categorieIscrizione.jsp">
		<jsp:param name="mercatoElettronico" value="${true}"/>
	</jsp:include>
	
	<div class="back-link">
		<a href="<wp:action path="/ExtStr2/do/FrontEnd/Cataloghi/viewIscrizione.action" />&amp;codice=${param.codice}&amp;ext=${param.ext}">
			<wp:i18n key="LINK_BACK_TO_ISCRIZIONE" />
		</a>
	</div>
</div>