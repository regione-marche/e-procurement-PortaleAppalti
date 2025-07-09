<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
// genera un nuovo token per le voci dei menu
com.agiletec.apsadmin.system.TokenInterceptor.saveSessionToken(pageContext);
%>

<c:set var="baseUrl"><wp:info key="systemParam" paramName="applicationBaseURL" /></c:set>
<c:set var="defaultLangCode"><wp:info key="defaultLang"></wp:info></c:set>

<%-- Se esiste, si utilizza il language impostato nel cookie che è un backup di quello in sessione --%>
<c:set var="currentLangCode" value="${cookie.WW_TRANS_I18N_LOCALE.value}" />
<c:if test="${empty currentLangCode}">
<%-- si utilizza il language impostato in sessione dall'interceptor I18nInterceptor di struts2, il default altrimenti --%>
    <c:set var="currentLangCode" value="${sessionScope.WW_TRANS_I18N_LOCALE}" />
    <c:if test="${empty currentLangCode}">
    	<c:set var="currentLangCode" value="${defaultLangCode}" />
    </c:if>
</c:if>

<c:redirect url="${baseUrl}${currentLangCode}/homepage.wp"/>