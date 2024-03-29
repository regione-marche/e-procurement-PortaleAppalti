<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>
<%@ taglib prefix="wpsa" uri="apsadmin-core.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="cache_buster"  value="${param.fagianoCache}" />
<c:choose>
	<c:when test="${cache_buster == null}"><c:set var="cache_buster"  value="fa" /></c:when>  
	<c:otherwise><c:set var="cache_buster">${fn:substring(cache_buster, 1, -1)}${fn:substring(cache_buster, 0, 1)}</c:set></c:otherwise> 
</c:choose>

<s:include value="/WEB-INF/apsadmin/jsp/entity/viewEntityTypes.jsp" />

<h3 id="fagiano_startReloadIndexes"><s:text name="title.entityAdmin.entityTypes.indexes" /></h3>
<p>
	<s:text name="note.entityAdmin.entityTypes.indexes.intro" />
</p>
<p>
	<s:if test="getSearcherManagerStatus() == 1">
				<a href="
					<s:url namespace="/do/jacms/Entity" action="viewEntityTypes" anchor="fagiano_startReloadIndexes">
						<s:param name="entityManagerName"><s:property value="entityManagerName" /></s:param>
						<s:param name="fagianoCache"><c:out value="${cache_buster}" /></s:param> 
					</s:url> 
					"><s:text name="label.indexes.status.wip" /></a>
	</s:if>
	<s:elseif test="getSearcherManagerStatus() == 2">
				<a href="
					<s:url namespace="/do/jacms/Entity" action="reloadContentsIndex" anchor="fagiano_startReloadIndexes">
						<s:param name="entityManagerName"><s:property value="entityManagerName" /></s:param>
						<s:param name="fagianoCache"><c:out value="${cache_buster}" /></s:param>
					</s:url>
					"><s:text name="label.indexes.status.ko" /></a>
	</s:elseif>
	<s:elseif test="getSearcherManagerStatus() == 0">
				<a href="
					<s:url namespace="/do/jacms/Entity" action="reloadContentsIndex" anchor="fagiano_startReloadIndexes">
						<s:param name="entityManagerName"><s:property value="entityManagerName" /></s:param>
						<s:param name="fagianoCache"><c:out value="${cache_buster}" /></s:param>
					</s:url>
					"><s:text name="label.indexes.status.ok" /></a>		
	</s:elseif>
</p>