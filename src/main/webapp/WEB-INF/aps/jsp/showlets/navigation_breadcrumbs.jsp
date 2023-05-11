<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%
/*
	Date: 2005/07/12  
	Author: William Ghelfi <w.ghelfi@agiletec.it> 
	Author: Eugenio Santoboni <e.santoboni@agiletec.it>
	
	Crea la barra di navigazione "a briciole di pane"
*/  		
%>

<%
// genera un nuovo token per le voci dei menu
com.agiletec.apsadmin.system.TokenInterceptor.saveSessionToken(pageContext);
%>

<wp:currentPage param="code" var="currentViewCode" />

<span class="youarehere"><wp:i18n key="YOU_ARE_HERE" />:</span>
<c:set var="first" value="true" />
<wp:nav spec="current.path" var="currentTarget">
	<c:set var="currentCode"><c:out value="${currentTarget.code}" /></c:set>
	<c:if test="${first != 'true'}"> &raquo; </c:if>
	<c:set var="label" value="${currentTarget.title}" />
	<c:if test="${fn:length(label) gt 40}">
		<c:set var="label" value="${fn:substring(label,0,40)}..." />	
	</c:if>
	
	<c:choose>
		<c:when test="${!currentTarget['voidTarget']}">
			<c:choose>
				<c:when test="${currentCode == currentViewCode}">
					<span class="active" title='<c:out value="${currentTarget.title}" />'><c:out value="${label}" /></span>
				</c:when>
				<c:otherwise>
					<span><a href="<c:out value="${currentTarget.url}?${tokenHrefParams}" />" title='<c:out value="${currentTarget.title}" />'><c:out value="${label}" /></a></span>
				</c:otherwise>
			</c:choose>
		</c:when>
		<c:otherwise>
		<span title='<c:out value="${currentTarget.title}" />'><c:out value="${label}" /></span>
		</c:otherwise>
	</c:choose>
	<c:set var="first" value="false" />
</wp:nav>
