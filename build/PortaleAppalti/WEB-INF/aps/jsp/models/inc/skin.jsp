<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>

<%-- imposta skin --%>
<c:set var="skin" value="" scope="request"/>
<c:if test="${! empty param.skin}">
	<c:set var="skin" value="${param.skin}" scope="request"/>
</c:if>
<c:if test="${empty skin}">
	<c:set var="skin" value="${cookie.skin.value}" scope="request"/>
</c:if>

<%-- imposta skin --%>
<c:set var="font" value="${param.font}" />

<%-- imposta layout --%>
<c:set var="layout" value="${param.layout}" />
<c:if test="${empty layout}">
	<c:set var="layout" value="${cookie.layout.value}" />
</c:if>

<es:getAppParam name="layoutStyle" var="dir" />
<c:if test="${! empty layout}">
	<c:set var="dir" value="${layout}"/>
</c:if>

<%-- layout:${layout} | skin:${skin} | font:${font} <br/>  --%>

<c:choose>
	<c:when test="${skin == 'highcontrast'}">
		<wp:headInfo type="CSS" info="showlets/${param.cssName}-text.css" />
		<wp:headInfo type="CSS" info="highcontrast/showlets/${param.cssName}.css" />
	</c:when>
	<c:when test="${skin == 'text'}">
		<wp:headInfo type="CSS" info="showlets/${param.cssName}-text.css" />
	</c:when>
	<c:otherwise>
		<wp:headInfo type="CSS" info="showlets/${param.cssName}.css" />
<%-- 		<es:getAppParam name="layoutStyle" var="dir"/> --%>
		<wp:headInfo type="CSS" info="${dir}/showlets/${param.cssName}.css" />
	</c:otherwise>
</c:choose>