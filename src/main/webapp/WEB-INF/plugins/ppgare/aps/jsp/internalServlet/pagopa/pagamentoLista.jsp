<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/jquery_ui.jsp" />

<%-- ******************************************************************************** --%>
<script type="text/javascript">
<!--//--><![CDATA[//><!--

// apertura della pagina...
$(document).ready(function() {
	console.log("PagoPA","Pagina caricata correttamente.");
	
});

//--><!]]>
</script>
<%-- ******************************************************************************** --%>


<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp">
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-list">
	<h2><wp:i18n key="TITLE_PAGE_PAGOPA_LISTA" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_PAGOPA_LISTA" />
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
	
	<form action="<wp:url />" method="post">
		<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/pagopa/pagamentoSearchForm.jsp" ></jsp:include>
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/token_input.jsp" />
		<input type="hidden" name="last" value="1" />
		<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/pagopa/pagamentoListaElement.jsp" ></jsp:include>
		<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/pagination.jsp"></jsp:include>
	</form>

	<div class="back-link">
		<a href="<wp:url page="ppcommon_area_personale" />">
			<wp:i18n key="LINK_BACK_TO_AREAPERSONALE" />
		</a>
	</div>
</div>