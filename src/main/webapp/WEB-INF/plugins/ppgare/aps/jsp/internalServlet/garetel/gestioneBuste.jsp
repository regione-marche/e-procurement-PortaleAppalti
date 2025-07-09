<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>

<%--
operazione             = ${operazione}<br/>
comunicazioneBustaAmm  = ${comunicazioneBustaAmm}<br/>
comunicazioneBustaTec  = ${comunicazioneBustaTec}<br/>
comunicazioneBustaEco  = ${comunicazioneBustaEco}<br/>
comunicazioneBustaPreq = ${comunicazioneBustaPreq}<br/>
bustaAmmAlreadySent    = ${bustaAmmAlreadySent}<br/>
bustaTecAlreadySent    = ${bustaTecAlreadySent}<br/>
bustaEcoAlreadySent    = ${bustaEcoAlreadySent}<br/>
bustaPreqAlreadySent   = ${bustaPreqAlreadySent}<br/>
costoFisso			   = ${costoFisso}<br/>
--%>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<c:choose>
		<c:when test="${operazione == presentaPartecipazione}">
			<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/garetel/inc/gestioneBustePartecipazione.jsp" />
		</c:when>
		<c:when test="${operazione == inviaOfferta}">
			<jsp:include page="/WEB-INF/plugins/ppgare/aps/jsp/internalServlet/garetel/inc/gestioneBusteOfferta.jsp" />
		</c:when>
		<c:otherwise>
			Page "gestioneBuste": parameter "operazione" not set!
		</c:otherwise>
	</c:choose>

	<div class="back-link">
		<a href="<wp:action path="/ExtStr2/do/FrontEnd/Bandi/view.action" />&amp;codice=${codice}&amp;ext=${param.ext}">
			<wp:i18n key="LINK_BACK_TO_PROCEDURE" />
		</a>
	</div>
</div>