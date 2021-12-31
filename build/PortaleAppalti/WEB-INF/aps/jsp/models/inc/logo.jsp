<%@ taglib prefix="wp" uri="aps-core.tld"%>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%-- imposta layout --%>
<c:set var="layout" value="${param.layout}" />
<c:if test="${empty layout}">
	<c:set var="layout" value="${cookie.layout.value}" />
</c:if>

<%-- imposta layoutStyle --%>
<es:getAppParam name="layoutStyle" var="layoutStyle"/>
<c:if test="${! empty layout}">
	<c:set var="layoutStyle" value="${layout}"/>
</c:if>

<div id="logo">
	<div id="logo-top"></div>
	<div id="logo-main">
			<a href="<wp:i18n key="URL_LINK_BANNER" />" title="<wp:i18n key="TITLE_LINK_BANNER" />">
				<img alt="<wp:i18n key="LOGO" />" src="<wp:imgURL/>${layoutStyle}/banner_logo.png" />
			</a>
	</div>
	<div id="logo-sub"></div><!-- end #logo -->
</div>
