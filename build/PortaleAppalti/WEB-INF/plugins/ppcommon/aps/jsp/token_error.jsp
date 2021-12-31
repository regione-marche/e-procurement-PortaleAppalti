<%@ taglib prefix="wp"   uri="aps-core.tld" %>
<%@ taglib prefix="c"    uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s"    uri="/struts-tags" %>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="application" />
</jsp:include>

<div class="portgare-view">
	
	<h2><wp:i18n key="TITLE_TOKEN_ERROR"/></h2>
	
	<wp:i18n key="LABEL_MESSAGE_TOKEN_ERROR"/>
	
	<div class="back-link">
		<c:set var="urlhome"><wp:url page="homepage" />?${tokenHrefParams}</c:set>
		<a href="${urlhome}"><wp:i18n key="LINK_GO_TO_HOMEPAGE"/></a>
	</div>

</div>