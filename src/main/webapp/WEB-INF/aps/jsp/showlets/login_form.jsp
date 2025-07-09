<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="login_form" />
</jsp:include>

<div class="menu-box">
	<h2><wp:i18n key="RESERVED_AREA" /></h2>
	<div>
		<c:choose>
			<c:when test="${sessionScope.currentUser != 'guest' && !accountAccettazioneConsensi}">
				<p class="welcome-message links"><wp:i18n key="WELCOME" />, <span><c:out value="${sessionScope.currentUser}"/></span>!</p>

				<c:if test="${sessionScope.currentUser.japsUser}">
					<%-- 
					<dl>
						<dt><wp:i18n key="USER_DATE_CREATION" /></dt>
						<dd><c:out value="${sessionScope.currentUser.creationDate}"/></dd>
						
						<dt><wp:i18n key="USER_DATE_ACCESS_LAST" /></dt>
						<dd><c:out value="${sessionScope.currentUser.lastAccess}"/></dd>
						
						<dt><wp:i18n key="USER_DATE_PASSWORD_CHANGE_LAST" /></dt>
						<dd><c:out value="${sessionScope.currentUser.lastPasswordChange}"/></dd>
					</dl>
					--%>
					<c:if test="${sessionScope.accountSSO != null}">
						<c:if test="${!sessionScope.currentUser.credentialsNotExpired}">
							<p><wp:i18n key="USER_STATUS_EXPIRED_PASSWORD" />: <a href="<wp:info key="systemParam" paramName="applicationBaseURL" />do/editPassword.action"><wp:i18n key="USER_STATUS_EXPIRED_PASSWORD_CHANGE" /></a></p>
							</c:if>
						</c:if>
					</c:if>

				<p><a href="<wp:info key="systemParam" paramName="applicationBaseURL" />do/logout.action"><wp:i18n key="LOGOUT" /></a><wp:ifauthorized permission="enterBackend"> | <a href="<wp:info key="systemParam" paramName="applicationBaseURL" />do/main.action?request_locale=<wp:info key="currentLang" />"><wp:i18n key="ADMINISTRATION" /></a></wp:ifauthorized></p>
			</c:when>
			<c:otherwise>

				<c:if test="${accountExpired}">
					<p><wp:i18n key="USER_STATUS_EXPIRED" /></p>
				</c:if>
				<c:if test="${wrongAccountCredential}">
					<p><wp:i18n key="USER_STATUS_CREDENTIALS_INVALID" /></p>
				</c:if>
				<c:if test="${accountDisabled}">
					<p><wp:i18n key="USER_STATUS_DISABLED" /></p>
				</c:if>
				<c:if test="${suspendedIp}">
					<p><c:out value="${suspendedIpMessage}"/></p>
				</c:if>
				<c:if test="${accountAccettazioneConsensi}">
					<p><wp:i18n key="USER_ACCETTAZIONE_CONSENSI" /></p>
				</c:if>

				<form action="<wp:url/>" method="post">
					<p class="rightText">
						<label><wp:i18n key="USERNAME" />:</label>
						<input type="text" name="username" class="text"/>
					</p>
					<p class="rightText">
						<label><wp:i18n key="PASSWORD" />:</label>
						<input type="password" name="password" class="text"/>
					</p>
					<p class="rightText"><input type="submit" value="Ok" class="button"/></p>
				</form>
			</c:otherwise>
		</c:choose>
	</div>
</div>