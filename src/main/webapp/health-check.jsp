<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="wp" uri="aps-core.tld"%>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>
<%@ page trimDirectiveWhitespaces="true" %>

<c:catch var="catchExceptionStartup">
	<es:checkCustomization var="visConsensoPrivacy" objectId="REG-IMPRESA" attribute="PRIVACY" feature="VIS" />
</c:catch>
<c:choose>
    <c:when test="${catchExceptionStartup == null}">
		<%
			String status = "OK";
			try {
				it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.WSGareAppaltoWrapper wsAppalti =
					(it.maggioli.eldasoft.plugins.ppgare.aps.system.services.bandi.WSGareAppaltoWrapper)
					com.agiletec.aps.util.ApsWebApplicationUtils.getBean("WSGareAppalto", request);

				if(wsAppalti != null) {
					wsAppalti.getProxyWSGare().getVersion().getVersion();
				} else {
					status = "KO";
				}
			} catch(Exception ex) {
				status = "KO";
			}
		%>
<%=status%>
    </c:when>    
    <c:otherwise>
KO
    </c:otherwise>
</c:choose>