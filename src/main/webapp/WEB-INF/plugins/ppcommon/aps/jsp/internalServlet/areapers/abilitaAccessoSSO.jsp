<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"  uri="/struts-tags"%>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>

<%-- 
${ssoLoginUrl}<br/>
${ssoLogoutUrl}<br/>
--%>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp">
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">

	<h2><wp:i18n key="TITLE_PAGE_ABILITA_ACCESSO_CON" /></h2>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/balloon_info.jsp">
		<jsp:param name="keyMsg" value="BALLOON_AREA_PERSONALE_ABILITA_ACCESSO_CON" />
	</jsp:include>

	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/field_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_errors.jsp" />
	<jsp:include page="/WEB-INF/plugins/ppcommon/aps/jsp/action_messages.jsp" />
	
	
	
	<fieldset>
		<legend><span class="noscreen"><wp:i18n key="LABEL_SECTION" /> </span><wp:i18n key="LABEL_ABILITA_ACCESSO_CON" /></legend>

		<s:iterator id="auth" value="autenticazioni">
		<s:if test="%{#auth == 'auth.sso.spid'}">
			<div class="fieldset-row">
				<div class="label">
					<label><wp:i18n key="LABEL_LINK_USER_TO_SPID" />: </label>					
				</div>
				<div class="element">
					<c:set var="urlSpid"><wp:info key="systemParam" paramName="applicationBaseURL" />do/FrontEnd/AreaPers/spidLogin.action</c:set>
					<jsp:include page="/WEB-INF/aps/jsp/models/inc/spid-button.jsp" >
						<jsp:param name="url" value="${urlSpid}" />
					</jsp:include> 
				</div>
			</div>	
		</s:if> 
		<s:if test="%{#auth == 'auth.sso.cie'}">
			<div class="fieldset-row">
				<div class="label">
					<label><wp:i18n key="LABEL_LINK_USER_TO_CIE" />: </label>					
				</div>
				<div class="element">
					<c:set var="urlCIE"><wp:info key="systemParam" paramName="applicationBaseURL" />do/FrontEnd/AreaPers/cieLogin.action</c:set>
					<a href="${urlCIE}" ><wp:i18n key="LABEL_LINK_ACCOUNT" /></a>
				</div>
			</div>	
		</s:if> 
		<s:if test="%{#auth == 'auth.sso.crs'}">
			<div class="fieldset-row">
				<div class="label">
					<label><wp:i18n key="LABEL_LINK_USER_TO_CRS" />: </label>					
				</div>
				<div class="element">
					<c:set var="urlCRS"><wp:info key="systemParam" paramName="applicationBaseURL" />do/FrontEnd/AreaPers/crsLogin.action</c:set>
					<a href="${urlCRS}" ><wp:i18n key="LABEL_LINK_ACCOUNT" /></a>			
				</div> 
			</div>	
		</s:if>
		<s:if test="%{#auth == 'auth.sso.cns'}">
			<div class="fieldset-row">
				<div class="label">
					<label><wp:i18n key="LABEL_LINK_USER_TO_CNS" />: </label>					
				</div>
				<div class="element">
					<c:set var="urlCNS"><wp:info key="systemParam" paramName="applicationBaseURL" />do/FrontEnd/AreaPers/cnsLogin.action</c:set>
					<a href="${urlCNS}" ><wp:i18n key="LABEL_LINK_ACCOUNT" /></a>			
				</div>  
			</div>
		</s:if> 
		<s:if test="%{#auth == 'auth.sso.gel'}">
			<div class="fieldset-row">
				<div class="label">
					<label><wp:i18n key="LABEL_LINK_USER_TO_GEL" />: </label>					
				</div>
				<div class="element">
					<c:set var="urlGEL"><wp:info key="systemParam" paramName="applicationBaseURL" />do/FrontEnd/AreaPers/gelLogin.action</c:set>
					<a href="${urlGEL}" ><wp:i18n key="LABEL_LINK_ACCOUNT" /></a>			
				</div>  
			</div>
		</s:if>
		<s:if test="%{#auth == 'auth.sso.myid'}">
			<div class="fieldset-row">
				<div class="label">
					<label><wp:i18n key="LABEL_LINK_USER_TO_MYID" />: </label>					
				</div>
				<div class="element">
					<c:set var="urlMYID"><wp:info key="systemParam" paramName="applicationBaseURL" />do/FrontEnd/AreaPers/myidLogin.action</c:set>
					<a href="${urlMYID}" ><wp:i18n key="LABEL_LINK_ACCOUNT" /></a>			
				</div>  
			</div>
		</s:if>
		<s:if test="%{#auth == 'auth.sso.federa'}">
			<div class="fieldset-row">
				<div class="label">
					<label><wp:i18n key="LABEL_LINK_USER_TO_FEDERA" />: </label>					
				</div>
				<div class="element">
					<c:set var="urlFEDERA"><wp:info key="systemParam" paramName="applicationBaseURL" />do/FrontEnd/AreaPers/federaLogin.action</c:set>
					<a href="${urlFEDERA}" ><wp:i18n key="LABEL_LINK_ACCOUNT" /></a>			
				</div>  
			</div>
		</s:if>
		<s:if test="%{#auth == 'auth.sso.shibboleth'}">
			<div class="fieldset-row">
				<div class="label">
					<label><wp:i18n key="LABEL_LINK_USER_TO_SHIBBOLETH" />: </label>					
				</div>
				<div class="element">
					<c:set var="url"><wp:info key="systemParam" paramName="applicationBaseURL" />do/FrontEnd/AreaPers/shibboletLogin.action</c:set>				
					<a href="${url}" ><wp:i18n key="LABEL_LINK_ACCOUNT" /></a>
				</div>
			</div>
		</s:if> 
		<s:if test="%{#auth == 'auth.sso.cohesion'}">
			<div class="fieldset-row">
				<div class="label">
					<label><wp:i18n key="LABEL_LINK_USER_TO_COHESION" />: </label>					
				</div>
				<div class="element">
					<a href="${urlLoginCohesion}" ><wp:i18n key="LABEL_LINK_ACCOUNT" /></a>
				</div>
			</div>
		</s:if> 
		<s:if test="%{#auth == 'auth.sso.spidbusiness'}">
			<div class="fieldset-row">
				<div class="label">
					<label><wp:i18n key="LABEL_LINK_USER_TO_SPID_BUSINESS" />: </label>					
				</div>
				<div class="element">
					<c:set var="urlSpidBusiness"><wp:info key="systemParam" paramName="applicationBaseURL" />do/FrontEnd/AreaPers/spidBusinessLogin.action</c:set>
					<jsp:include page="/WEB-INF/aps/jsp/models/inc/spidbusiness-button.jsp" >
						<jsp:param name="urlBusiness" value="${urlSpidBusiness}" />
					</jsp:include> 
				</div>
			</div>
		</s:if> 
		</s:iterator>
	</fieldset>
</div>