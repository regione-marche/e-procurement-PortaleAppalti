<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="site_map" />
</jsp:include>

<c:set var="actualLevel" value="-1"/>

<div id="site-map">
	<h2><wp:i18n key="TITLE_SITEMAP" /></h2>
	<wp:nav spec="code(homepage).subtree(10)" var="item">
		<c:choose>
			<c:when test="${item.level > actualLevel}"> <%-- si apre un livello --%>
				<ul>
			</c:when>
			<c:when test="${item.level < actualLevel}"> <%-- si chiudono n livelli --%>
				<c:forEach begin="${item.level}" end="${actualLevel-1}">
				</li></ul>
				</c:forEach>
				</li>
			</c:when>
			<c:otherwise>  <%-- si chiude semplicemente l'elemento --%>
				</li>
			</c:otherwise>
		</c:choose>
		<li>
		<c:choose>
			<c:when test="${item.voidTarget}"><span>${item.title}</span></c:when>
			<c:otherwise>
			<a href="${item.url}" title="${item.title}">${item.title}</a>
			</c:otherwise>
		</c:choose>
		<c:set var="actualLevel" value="${item.level}"/>
	</wp:nav>
	<c:forEach begin="0" end="${actualLevel}">  <%-- si chiudono tutti i livelli rimasti aperti --%>
		</li></ul>
	</c:forEach>
</div>