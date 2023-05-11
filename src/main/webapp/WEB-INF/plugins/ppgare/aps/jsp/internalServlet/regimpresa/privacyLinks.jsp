<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>

<es:getAppParam name="privacy.pubblicaCondizioniUsoStd" var="showDefaultTermOfUse"/> 	

<ul>
	<li>
		<c:choose>
		<c:when test="${showDefaultTermOfUse == 1}">	
			<a href="<wp:url page="ppgare_auth" />?actionPath=/ExtStr2/do/FrontEnd/RegistrImpr/openTermOfUse.action&amp;currentFrame=7" target="_blank"><wp:i18n key="LABEL_TRATTAMENTO_DATI" /></a>
		</c:when>			
		<c:otherwise>
	   		<a href="<wp:resourceURL/>cms/documents/Regole_utilizzo_piattaforma_telematica.pdf" target="_blank"><wp:i18n key="LABEL_TRATTAMENTO_DATI" /></a>
		</c:otherwise>
	</c:choose>				
	</li>
	<c:if test="${showDefaultTermOfUse == 1}">
		<li>
			<a href="<wp:url page="ppgare_auth" />?actionPath=/ExtStr2/do/FrontEnd/RegistrImpr/openDataUsageInfo.action&amp;currentFrame=7" target="_blank"><wp:i18n key="LABEL_INFO_PRIVACY" /></a>
		</li>
	</c:if>
</ul>
