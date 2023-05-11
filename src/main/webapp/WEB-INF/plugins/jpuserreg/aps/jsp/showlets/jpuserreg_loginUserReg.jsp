<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="login_form" />
</jsp:include>

<es:checkCustomization var="visRegistrati" objectId="AREARISERVATA" attribute="REGISTRATI" feature="VIS" />

<%
// genera un nuovo token per le voci dei menu
com.agiletec.apsadmin.system.TokenInterceptor.saveSessionToken(pageContext);
%>

<div class="menu-box">
	<h2><wp:i18n key="RESERVED_AREA" /></h2>
	<div class="login-box">

		<c:choose>
			<c:when test="${sessionScope.currentUser != 'guest' || sessionScope.accountSSO != null}">
				<p class="welcome-message">
					<wp:i18n key="WELCOME" /> 
					<span>
						<!-- s:text name="#session.currentUser.profile.getValue(#session.currentUser.profile.firstNameAttributeName)" / -->
						
						<c:if test="${sessionScope.currentUser != 'guest'}">
							<c:set var="username" value="${sessionScope.currentUser.username}" scope="page"/>
							<c:set var="profilo" value="${sessionScope.currentUser.profile}" scope="page"/>
						</c:if>
						<c:if test="${sessionScope.accountSSO != null}">
							<c:set var="nome" value="${sessionScope.accountSSO.nome}" scope="page"/>
						</c:if> 
						<c:choose>
							<c:when test="${sessionScope.currentUser != 'guest' && sessionScope.accountSSO ==  null}">
								<% 
								Object nome = null;
								%>		
								<c:if test="${! empty profilo}">
									<c:set var="nomeAttributo" value="${profilo.firstNameAttributeName}" scope="page"/>		
									<% 
									com.agiletec.plugins.jpuserprofile.aps.system.services.profile.model.UserProfile profilo = (com.agiletec.plugins.jpuserprofile.aps.system.services.profile.model.UserProfile)pageContext.getAttribute("profilo");
									nome = profilo.getValue((String)pageContext.getAttribute("nomeAttributo"));
									%>
								</c:if>
								<%
								if (nome == null) {
									nome = (String)pageContext.getAttribute("username");
								}
								pageContext.setAttribute("nome", nome);
								%>
								${fn:escapeXml(nome)}
							</c:when>
							<c:otherwise>
								<c:choose>
									<c:when test="${(sessionScope.accountSSO !=  null) && (sessionScope.currentUser != null) && (sessionScope.currentUser == 'guest')}">
										<a href="<wp:url page="ppcommon_area_soggetto_sso"/>" class="genlink">${sessionScope.accountSSO.nome}<c:if test="${! empty sessionScope.accountSSO.cognome}"> ${sessionScope.accountSSO.cognome}</c:if></a> 
									</c:when>
									<c:otherwise>
										<a href="<wp:url page="ppcommon_area_soggetto_sso"/>" class="genlink">${sessionScope.accountSSO.nome}<c:if test="${! empty sessionScope.accountSSO.cognome}"> ${sessionScope.accountSSO.cognome}</c:if></a>
										, <wp:i18n key="LABEL_OPERATING_ON_BEHALF" /> <strong>
										<% 
										Object nome = null;
										%>	
										<c:choose>
											<c:when test="${sessionScope.accountSSO.ragioneSociale != null}">
												<strong><c:out value="${sessionScope.accountSSO.ragioneSociale}" /></strong>
											</c:when>
											<c:when test="${! empty profilo}"> 
												<c:set var="nomeAttributo" value="${profilo.firstNameAttributeName}" scope="page"/>		
												<% 
												com.agiletec.plugins.jpuserprofile.aps.system.services.profile.model.UserProfile profilo = (com.agiletec.plugins.jpuserprofile.aps.system.services.profile.model.UserProfile)pageContext.getAttribute("profilo");
												nome = profilo.getValue((String)pageContext.getAttribute("nomeAttributo"));
												%>			
											</c:when>
										</c:choose>
								<%
								if (nome == null) {
									nome = (String)pageContext.getAttribute("username");
								}
								pageContext.setAttribute("nome", nome);
								%>
								${fn:escapeXml(nome)}</strong>
									</c:otherwise>
								
								</c:choose>
							</c:otherwise> 
						</c:choose>
					</span>!
				</p>
				<div class="links">
					<c:choose>
						<c:when test="${sessionScope.accountSSO != null}">
							<c:if test="${(sessionScope.currentUser != 'guest')}">
								<a href="<wp:url page="ppcommon_area_personale" />" class="genlink"><wp:i18n key="TITLE_PAGE_AREA_PERSONALE" /></a> |
							</c:if>
							<a href="<wp:info key="systemParam" paramName="applicationBaseURL" />do/logout.action" class="genlink"><wp:i18n key="LOGOUT" /></a>
						</c:when>
						<c:when test="${sessionScope.currentUser.credentialsNotExpired}">
							<c:choose>
								<c:when test="${sessionScope.accountSSO == null}">
									<a href="<wp:url page="ppcommon_area_personale" />" class="genlink"><wp:i18n key="TITLE_PAGE_AREA_PERSONALE" /></a> |
								</c:when>
								<c:otherwise>
									<c:if test="${(sessionScope.currentUser != 'guest')}">
										<a href="<wp:url page="ppcommon_area_personale" />" class="genlink"><wp:i18n key="TITLE_PAGE_AREA_PERSONALE" /></a> |
									</c:if>
								</c:otherwise>
							</c:choose>
						
							<c:choose>
								<c:when test="${sessionScope.accountSSO == null}">
									<a href="<wp:info key="systemParam" paramName="applicationBaseURL" />do/logout.action" class="genlink"><wp:i18n key="LOGOUT" /></a>
								</c:when>
								<c:otherwise>
									<a href="<wp:info key="systemParam" paramName="applicationBaseURL" />do/SSO/LogoutSSO.action" class="genlink"><wp:i18n key="LOGOUT" /></a>
								</c:otherwise>
							</c:choose> 
						</c:when>
						<c:otherwise>
							<p class="important">
								<wp:i18n key="USER_STATUS_EXPIRED_PASSWORD" />
							</p>
							<a href="<wp:url page="ppcommon_cambia_password" />" class="genlink alert-link"><wp:i18n key="USER_STATUS_EXPIRED_PASSWORD_CHANGE" /></a> |
							<a href="<wp:info key="systemParam" paramName="applicationBaseURL" />do/logout.action" class="genlink"><wp:i18n key="LOGOUT" /></a>
							
						</c:otherwise>
					</c:choose>
				</div>
				<wp:ifauthorized permission="enterBackend">
					<div class="links">
						<a href="<wp:info key="systemParam" paramName="applicationBaseURL" />do/main.action?request_locale=<wp:info key="currentLang" />" class="genlink"><wp:i18n key="ADMINISTRATION" /></a>
					</div>
				</wp:ifauthorized>
				<wp:ifauthorized permission="superuser" >
					<c:set var="ctrl" value="admin" />
				</wp:ifauthorized>
			</c:when>
			<c:otherwise>
				<div class="links">
					<div>
						<a href="<wp:url page="ppgare_auth" />" class="genlink"><wp:i18n key="auth_ACCEDI" /></a> 
					</div>
				</div>
			</c:otherwise>
		</c:choose>
	</div>
</div>
