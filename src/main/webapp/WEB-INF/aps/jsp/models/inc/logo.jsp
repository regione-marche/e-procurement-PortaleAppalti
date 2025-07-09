<%@ taglib prefix="wp" uri="aps-core.tld"%>
<%@ taglib prefix="es" uri="/WEB-INF/plugins/ppcommon/aps/tld/eldasoft-common-core.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%-- visualizzazione del menu di login in alto a dx --%>
<c:catch var="exceptionSessioneInvalidata"> 
<es:checkCustomization var="loginTopRight" objectId="LAYOUT" attribute="LOGINTOPRIGHT" feature="ACT" />
</c:catch> 
<c:set var="loginTopRight" value="false"/>


<%-- imposta layout --%>
<c:set var="layout" value="${param.layout}" />
<c:if test="${empty layout}">
	<c:set var="layout" value="${cookie.layout.value}" />
</c:if>

<%-- imposta layoutStyle --%>
<es:getAppParam name="layoutStyle" var="layoutStyle"/>
<c:if test="${! empty layout}">
	<c:set var="layoutStyle" value="${layout}"/>
</c:if>

<%-- imposta layoutLogo --%>
<c:set var="banner" value="banner_logo.png" />
<c:if test="${layoutStyle eq 'appalti-contratti' || layoutStyle eq 'appalti-contratti-v2'}">
	<c:set var="banner" value="banner_logo.svg" />
</c:if>



<div id="logo">
	<div id="logo-top"></div>
	<div id="logo-main">
		<c:if test="${loginTopRight}">
			<div style="width: 80%; float:left">
		</c:if>
		<a href="<wp:i18n key="URL_LINK_BANNER" />" title="<wp:i18n key="TITLE_LINK_BANNER" />">
			<img alt="<wp:i18n key="LOGO" />" src="<wp:imgURL/>${layoutStyle}/${banner}" />
		</a>
		<c:if test="${loginTopRight}">
			</div>
		</c:if>
		
		<%-- visualizzazione login in altro a destra --%>
		<c:if test="${loginTopRight}">
			<div style="width: 20%; float:right">
				<wp:show frame="3"/>
			</div>
		</c:if>
	</div>
	<div id="logo-sub"></div><!-- end #logo -->
</div>
