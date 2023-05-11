<%@ taglib prefix="wpsa" uri="apsadmin-core.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>
<h1 class="noscreen" id="fagiano_start"><s:text name="title.mainFunctions" /></h1>
<p class="signature whiteBox"><a href="http://www.japsportal.org/<s:property value="currentLang.code"/>/updates/" title="<s:text name="note.checkForUpdates" />">jAPS 2.0, version <wp:info key="systemParam" paramName="version" /></a></p>
<p class="rightText">
	<em><s:text name="note.userbar.welcome"/></em>,
	<strong><c:out value="${sessionScope.currentUser}"/></strong>!
</p>
<h2 class="noscreen"><s:text name="note.superMenu" /></h2>
<ul class="menu horizontal rightText">
	<li><a href="<s:url action="main" namespace="/do" />"><img src="<wp:resourceURL/>administration/img/icons/32x32/go-first.png" alt="<s:text name="note.goToMain" />" title="<s:text name="note.goToMain" />" /></a></li>
	<li class="noscreen"><a href="#manage"><s:text name="note.goToManageMenu" /></a></li>
	<li class="noscreen"><a href="#fagiano_mainContent"><s:text name="note.skipToMainContent" /></a></li>
	<c:if test="${sessionScope.currentUser.japsUser}">
	<li><a href="<s:url action="editPassword" namespace="/do/CurrentUser" />"><img src="<wp:resourceURL/>administration/img/icons/32x32/security.png" alt="<s:text name="note.changeYourPassword" />" title="<s:text name="note.changeYourPassword" />" /></a></li></c:if>
	<li><a href="<s:url value="/" />"><img src="<wp:resourceURL/>administration/img/icons/32x32/go-home.png" alt="<s:text name="note.goToPortal" /> ( <s:text name="note.sameWindow" /> )" title="<s:text name="note.goToPortal" /> ( <s:text name="note.sameWindow" /> )" /></a></li>
	<li><a href="<s:url action="logout" namespace="/do" />"><img src="<wp:resourceURL/>administration/img/icons/32x32/system-log-out.png" alt="<s:text name="menu.exit"/>" title="<s:text name="menu.exit"/>" /></a></li>
</ul>