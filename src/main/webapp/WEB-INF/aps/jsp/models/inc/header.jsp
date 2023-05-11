<%@ taglib prefix="wp" uri="aps-core.tld"%>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>

<div id="htmlprebanner"><wp:i18n key="HTML_PRE_BANNER"/></div>
<div id="header">
	<div id="header-top"><wp:show frame="9"/></div>
	<div id="header-main">
		<jsp:include page="logo.jsp"></jsp:include>
	</div>
	<div id="header-sub"></div>
</div>
<div id="htmlpostbanner"><wp:i18n key="HTML_POST_BANNER"/></div>
<jsp:include page="menu.jsp"/>
<div id="prebreadcrumbs"><wp:i18n key="HTML_PRE_BREADCRUMBS"/></div>
<jsp:include page="breadcrumbs.jsp">
	<jsp:param name="layoutPage" value="${param.layoutPage}" />
</jsp:include>