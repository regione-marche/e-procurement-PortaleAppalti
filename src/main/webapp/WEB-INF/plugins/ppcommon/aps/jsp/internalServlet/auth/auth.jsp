<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>								

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>
<wp:headInfo type="CSS" info="spid-sp-access-button.min.css" />
<es:checkCustomization var="visRegistrati" objectId="AREARISERVATA" attribute="REGISTRATI" feature="VIS" />
<es:checkCustomization var="actRegistrati" objectId="REG-IMPRESA" attribute="SOLOESTERE" feature="ACT" />

<script>
function loginWithCredentials(){
	$("#loginForm").submit();
}

</script>
<c:set var="imgPath"><wp:resourceURL/>static/img</c:set>
<div class="sso_container">


<s:iterator id="auth" value="sistemiAutenticazione">

	<s:if test="%{#auth == 'auth.form'}">
		<div class="sso_section">
			<h2><wp:i18n key="auth_FORM_LOGIN_TITLE"/></h2>
			<div class="auth_description">
				<wp:i18n key="auth_FORM_LOGIN_DESCRIPTION"/>
			</div>
			<div class="credentials_form">
				<form action="<wp:url/>" id="loginForm" method="post" class="reserved_area_form">
					<div>
						<div class="credentials-field">
							<div class="credentials-label">
								<label ><wp:i18n key="USERNAME" />:</label>
							</div>
							<div class="credentials-input">
								<input type="text" name="username" class="text username" title="<wp:i18n key="USERNAME" />"  style="width:100%;"/>
								<%--
								<input type="text" name="username" class="text username" title="<wp:i18n key="USERNAME" />" maxlength="40" style="width:100%;"/> 
								--%>
							</div>
						</div>
						<div class="credentials-field">
							<div class="credentials-label">
								<label ><wp:i18n key="PASSWORD" />:</label>
							</div>
							<div class="credentials-input">
								<input type="password" name="password" class="text password" title="<wp:i18n key="PASSWORD" />"  style="width:100%;"/>
							</div>
						</div>
					</div>
					<c:if test="${accountExpired}">
						<p class="important">
							<wp:i18n key="USER_STATUS_EXPIRED" />
						</p>
					</c:if>
					<c:if test="${wrongAccountCredential}">
						<p class="important">
							<wp:i18n key="USER_STATUS_CREDENTIALS_INVALID" />
						</p>
					</c:if>
					<c:if test="${accountDisabled}">
						<p class="important">
							<wp:i18n key="USER_STATUS_DISABLED" />
						</p>
					</c:if>
					<c:if test="${suspendedIp}">
						<p class="important">
							<c:out value="${suspendedIpMessage}" />
						</p>
					</c:if>
					<div class="auth_button">
						<a href="javascript:void(0);" onClick="loginWithCredentials()" class="italia-it-button italia-it-button-size-m button-spid">
							<span class="italia-it-button-text"><wp:i18n key="auth_FORM_LOGIN_BUTTON"/></span>
						</a>
					</div>
				</form>
				<div class="links">
					<c:if test="${visRegistrati}">
                        <a href="<wp:url page="ppgare_registr" />?${tokenHrefParams}" class="genlink"><wp:i18n key="jpuserreg_REGISTRATION" /></a> |
                    </c:if>
                    <a href="<wp:url page="passwordrecover" />" class="genlink"><wp:i18n key="jpuserreg_PASSWORD_RECOVER" /></a>
                </div>
                <c:if test="${visRegistrati && actRegistrati}">
                    <div class="element note" style="margin: auto;">
                        <wp:i18n key="LABEL_REGISTRATION_ONLY_NOT_ITALIANS" />
                    </div>
                </c:if>
			</div>
			<script>
			$('.reserved_area_form').keydown(function(event) {
				if (event.which == 13) {
					// As ASCII code for ENTER key is "13"
					this.submit(); // Submit form code
				}
			});
			</script>		
		</div>
	</s:if> 
	<s:if test="%{#auth == 'auth.sso.spid'}">
		<div class="sso_section">
			<h2><wp:i18n key="auth_SPID_LOGIN_TITLE"/></h2>
			<div class="auth_description">
				<wp:i18n key="auth_SPID_LOGIN_DESCRIPTION"/>
			</div>
			<div class="auth_button">
				<c:set var="urlSpid"><wp:info key="systemParam" paramName="applicationBaseURL" />do/SSO/SpidLogin.action</c:set>
				<jsp:include page="/WEB-INF/aps/jsp/models/inc/spid-button.jsp" >
					<jsp:param name="url" value="${urlSpid}" />
				</jsp:include>
			</div>
		</div>

	</s:if> 
	<s:if test="%{#auth == 'auth.sso.spidbusiness'}">
		<div class="sso_section">
			<h2><wp:i18n key="SPID_BUSINESS_LOGIN_TITLE"/></h2>
			<div class="auth_description">
				<wp:i18n key="auth_SPID_BUSINESS_LOGIN_DESCRIPTION"/>
			</div>
			<div class="auth_button">
				<c:set var="urlSpidBusiness"><wp:info key="systemParam" paramName="applicationBaseURL" />do/SSO/SpidBusinessLogin.action</c:set>
				<jsp:include page="/WEB-INF/aps/jsp/models/inc/spidbusiness-button.jsp" >
					<jsp:param name="urlBusiness" value="${urlSpidBusiness}" />
				</jsp:include>
			</div>
		</div>
	</s:if> 
	<s:if test="%{#auth == 'auth.sso.shibboleth'}">
		<div class="sso_section">
			<h2><wp:i18n key="auth_SHIBBOLETH_LOGIN_TITLE"/></h2>
			<div class="auth_description">
				<wp:i18n key="auth_SHIBBOLETH_LOGIN_DESCRIPTION"/>
			</div>
			<div class="auth_button">
				<a href="${urlLoginShibboleth}" class="italia-it-button italia-it-button-size-m button-spid">
					<span class="italia-it-button-icon"><img src="${imgPath}/spid-button/spid-ico-circle-bb.svg" alt=""/></span>
					<span class="italia-it-button-text"><wp:i18n key="auth_SHIBBOLETH_LOGIN_BUTTON"/></span>
				</a>
			</div>
		</div>
	</s:if> 
	<s:if test="%{#auth == 'auth.sso.cohesion'}">
		<div class="sso_section">
			<h2><wp:i18n key="auth_COHESION_LOGIN_TITLE"/></h2>
			<div class="auth_description">
				<wp:i18n key="auth_COHESION_LOGIN_DESCRIPTION"/>
			</div>
			<div class="auth_button">
				<a href="${urlLoginCohesion}" class="italia-it-button italia-it-button-size-m button-spid">
					<span class="italia-it-button-icon"><img src="${imgPath}/logo_cohesion.png" alt=""/></span>
					<span class="italia-it-button-text"><wp:i18n key="auth_COHESION_LOGIN_BUTTON"/></span>
				</a>
			</div>
		</div>

	</s:if> 
	<s:if test="%{#auth == 'auth.sso.cie'}">
		<div class="sso_section">
			<h2><wp:i18n key="auth_CIE_LOGIN_TITLE"/></h2>
			<div class="auth_description">
				<wp:i18n key="auth_CIE_LOGIN_DESCRIPTION"/>
			</div>
			<div class="auth_button">
				<c:set var="urlCie"><wp:info key="systemParam" paramName="applicationBaseURL" />do/SSO/CieLogin.action</c:set>
				<a href="${urlCie}" class="italia-it-button italia-it-button-size-m button-spid">
					<span class="italia-it-button-icon"><img src="${imgPath}/Logo_CIE_ID.svg" onerror="this.src='${imgPath}/spid-ico-circle-bb.png'; this.onerror=null;" alt="" /></span>
					<span class="italia-it-button-text"><wp:i18n key="auth_CIE_LOGIN_BUTTON"/></span>
				</a>
			</div>
		</div>
	</s:if> 
	<s:if test="%{#auth == 'auth.sso.cns'}">
		<div class="sso_section">
			<h2><wp:i18n key="auth_CNS_LOGIN_TITLE"/></h2>
			<div class="auth_description">
				<wp:i18n key="auth_CNS_LOGIN_DESCRIPTION"/>
			</div>
			<div class="auth_button">
				<c:set var="urlCns"><wp:info key="systemParam" paramName="applicationBaseURL" />do/SSO/CnsLogin.action</c:set>
				<a href="${urlCns}" class="italia-it-button italia-it-button-size-m button-spid">
					<span class="italia-it-button-icon"><img src="${imgPath}/icon_cns2x.png" onerror="this.src='${imgPath}/spid-ico-circle-bb.png'; this.onerror=null;" alt="" /></span>
					<span class="italia-it-button-text"><wp:i18n key="auth_CNS_LOGIN_BUTTON"/></span>
				</a>
			</div>
		</div>
	</s:if> 
	<s:if test="%{#auth == 'auth.sso.crs'}">
		<div class="sso_section">
			<h2><wp:i18n key="auth_CRS_LOGIN_TITLE"/></h2>
			<div class="auth_description">
				<wp:i18n key="auth_CRS_LOGIN_DESCRIPTION"/>
			</div>
			<div class="auth_button">
				<c:set var="urlCrs"><wp:info key="systemParam" paramName="applicationBaseURL" />do/SSO/CrsLogin.action</c:set>
				<a href="${urlCrs}" class="italia-it-button italia-it-button-size-m button-spid">
					<span class="italia-it-button-icon"><img src="${imgPath}/icon_cns2x.png" alt="" /></span>
					<span class="italia-it-button-text"><wp:i18n key="auth_CRS_LOGIN_BUTTON"/></span>
				</a>
			</div>
		</div>
	</s:if> 
	<s:if test="%{#auth == 'auth.sso.gel'}">
		<div class="sso_section">
			<h2><wp:i18n key="auth_GEL_LOGIN_TITLE"/></h2>
			<div class="auth_description">
				<wp:i18n key="auth_GEL_LOGIN_DESCRIPTION"/>
			</div>
			<div class="auth_button">
				<c:set var="urlGel"><wp:info key="systemParam" paramName="applicationBaseURL" />do/SSO/GelLogin.action</c:set>
				<a href="${urlGel}" class="italia-it-button italia-it-button-size-m button-spid">
					<span class="italia-it-button-icon"><img src="${imgPath}/logo_gel.png" alt="" /></span>
					<span class="italia-it-button-text"><wp:i18n key="auth_GEL_LOGIN_BUTTON"/></span>
				</a>
			</div>
		</div>
	</s:if>
	<s:if test="%{#auth == 'auth.sso.myid'}">
		<div class="sso_section">
			<h2><wp:i18n key="auth_MYID_LOGIN_TITLE"/></h2>
			<div class="auth_description">
				<wp:i18n key="auth_MYID_LOGIN_DESCRIPTION"/>
			</div>
			<div class="auth_button">
				<c:set var="urlMyId"><wp:info key="systemParam" paramName="applicationBaseURL" />do/SSO/MyIdLogin.action</c:set>
				<a href="${urlMyId}" class="italia-it-button italia-it-button-size-m button-spid">
					<span class="italia-it-button-icon"><img src="${imgPath}/logo_myid.png" alt="" /></span>
					<span class="italia-it-button-text"><wp:i18n key="auth_MYID_LOGIN_BUTTON"/></span>
				</a>
			</div>
		</div>
	</s:if>
	<s:if test="%{#auth == 'auth.sso.federa'}">
		<div class="sso_section">
			<h2><wp:i18n key="auth_FEDERA_LOGIN_TITLE"/></h2>
			<div class="auth_description">
				<wp:i18n key="auth_FEDERA_LOGIN_DESCRIPTION"/>
			</div>
			<div class="auth_button">
				<c:set var="urlFedera"><wp:info key="systemParam" paramName="applicationBaseURL" />do/SSO/FederaLogin.action</c:set>
				<a href="${urlFedera}" class="italia-it-button italia-it-button-size-m button-spid">
					<span class="italia-it-button-icon"><img src="${imgPath}/logo_federa.png" alt="" /></span>
					<span class="italia-it-button-text"><wp:i18n key="auth_FEDERA_LOGIN_BUTTON"/></span>
				</a>
			</div>
		</div>
	</s:if>
	<s:if test="%{#auth == 'auth.sso.gateway'}">
		<div class="sso_section">
			<h2><wp:i18n key="auth_GATEWAY_LOGIN_TITLE"/></h2>
			<div class="auth_description">
				<wp:i18n key="auth_GATEWAY_LOGIN_DESCRIPTION"/>
			</div>
			<div class="auth_button">
				<c:set var="urlGateway"><wp:info key="systemParam" paramName="applicationBaseURL" />do/SSO/GatewayLogin.action</c:set>
				<a href="${urlGateway}" class="italia-it-button italia-it-button-size-m button-spid">
					<span class="italia-it-button-icon"><img src="${imgPath}/logo_gateway.gif" alt="" /></span>
					<span class="italia-it-button-text"><wp:i18n key="auth_GATEWAY_LOGIN_BUTTON"/></span>
				</a>
			</div>
		</div>
	</s:if>
</s:iterator>
</div>

