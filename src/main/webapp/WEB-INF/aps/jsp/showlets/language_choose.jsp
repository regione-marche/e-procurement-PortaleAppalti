<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="language_choose" />
</jsp:include>

<wp:info key="langs" var="langs" />
<wp:info key="currentLang" var="currentLang" />
<div id="language-sub-menu" class="language-choose float-right">
	<c:forEach var="lang" items="${langs}" varStatus="status">
		<c:if test="${status.index gt 0}"> | </c:if>
		<c:choose>
			<c:when test="${currentLang == lang.code}"><strong><c:out value="${lang.code}" /></strong></c:when>
			<c:otherwise><a class="genlink" href="<wp:url lang="${lang.code}" paramRepeat="false"><wp:urlPar name="request_locale">${lang.code}</wp:urlPar></wp:url>" title="<c:out value="${lang.descr}" />"><c:out value="${lang.code}" /></a></c:otherwise>
		</c:choose>
	</c:forEach>		
</div>
