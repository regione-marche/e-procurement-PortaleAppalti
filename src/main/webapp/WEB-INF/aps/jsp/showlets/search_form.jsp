<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="/WEB-INF/aps/jsp/models/inc/skin.jsp" >
	<jsp:param name="skin" value="${param.skin}" />
	<jsp:param name="cssName" value="search_form" />
</jsp:include>

<form id="search-form" action="<wp:url page="search_result" />" >
	<div>
		<label for="search"><wp:i18n key="SEARCH" /></label>: 
		<input type="text" name="search" class="text" id="search" value=""/>
		<input type="submit" value="<wp:i18n key="BUTTON_SEARCH_IN_CMS_OK" />" class="button" title="<wp:i18n key="TITLE_SEARCH_IN_CMS" />"/>
	</div>
</form>