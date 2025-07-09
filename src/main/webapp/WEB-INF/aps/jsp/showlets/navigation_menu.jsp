<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>


<%
// genera un nuovo token per le voci dei menu
com.agiletec.apsadmin.system.TokenInterceptor.saveSessionToken(pageContext);
%>


<c:if test="${(!empty credentialsExpired && !credentialsExpired) || sessionScope.currentUser.credentialsNotExpired || sessionScope.accountSSO != null}">
	
	<div class="menu-box">
		
		<c:set var="prev" value="-1" />
		<wp:currentPage param="code" var="currentViewCode" />
		<c:set var="startClosing" value="0" />

		<wp:nav var="currentTarget">
			<c:set var="current"><c:out value="${currentTarget.level}" /></c:set>
			<c:set var="currentCode"><c:out value="${currentTarget.code}" /></c:set>

			<c:if test="${current == prev}"></li></c:if>
			<c:if test="${current < prev}"></li></ul></div></c:if>
			<c:if test="${(current > prev) && (current != 0)}"><ul></c:if>

			<c:if test="${currentTarget.page.showable}">
			<c:choose>
				<c:when test="${!currentTarget['voidTarget'] && prev >= 0}">
				<li>
					<span <c:if test="${currentCode == currentViewCode}">class="current"</c:if>>
						<a href="<c:out value="${currentTarget.url}" />" title="<wp:i18n key="VAI_PAGINA" />: <c:out value="${currentTarget.title}" />">
							<c:out value="${currentTarget.title}" />
						</a>
					</span>
				</c:when>
				<c:otherwise>
					<div class="navigation-box"><h2><c:out value="${currentTarget.title}" /></h2>
				</c:otherwise>
			</c:choose>
			</c:if>
			<c:set var="prev"><c:out value="${currentTarget.level}" /></c:set>
		</wp:nav>
		<c:if test="${prev>0}">
			<c:forEach begin="${startClosing}" end="${prev-1}" ></li></ul></div></c:forEach> 
		</c:if>
	</div>
</c:if>