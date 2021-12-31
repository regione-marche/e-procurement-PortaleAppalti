<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>

<div class="balloon">
	<div class="balloon-content balloon-rss">
		<a href="<wp:resourceURL />rss/<c:out value="${param.rss}"/>" title="<wp:i18n key="TITLE_RSS"/>"><wp:i18n key="LINK_RSS"/></a>
		<br/>
		<br/>
		<wp:i18n key="BALLOON_RSS"/>
	</div>
</div>