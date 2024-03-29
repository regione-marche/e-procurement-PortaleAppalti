<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="currentCode"><wp:currentPage param="code" /></c:set>
	
<c:if test="${currentCode == 'notfound'}">
	<p class="error"><wp:i18n key="PAGE_NOT_FOUND" /></p>
</c:if>
<c:if test="${currentCode == 'errorpage'}">
	<p class="error"><wp:i18n key="GENERIC_ERROR" /></p>
</c:if>