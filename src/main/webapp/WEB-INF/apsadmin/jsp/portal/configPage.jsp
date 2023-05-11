<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>

<h1><a href="<s:url action="viewTree" namespace="/do/Page" />" title="<s:text name="note.goToSomewhere" />: <s:text name="title.pageManagement" />"><s:text name="title.pageManagement" /></a></h1>
<h2><s:text name="title.configPage" /></h2>

<s:include value="/WEB-INF/apsadmin/jsp/portal/include/pageInfo.jsp" />

<table class="generic" summary="<s:text name="note.page.pageConfig.summary" />">
<tr>
	<th><abbr title="<s:text name="name.position" />">P</abbr></th>
	<th><s:text name="label.description" /></th>
	<th><s:text name="name.showlet" /></th>
	<th class="icon"><abbr title="<s:text name="label.remove" />">&ndash;</abbr></th>	
</tr>
<s:iterator value="currentPage.showlets" id="showlet" status="rowstatus">
<s:set var="showletType" value="#showlet.getType()" ></s:set>
<tr>
	<td class="rightText">
		<s:if test="currentPage.getModel().getMainFrame() == #rowstatus.index"><img src="<wp:resourceURL/>administration/img/icons/16x16/emblem-important.png" alt="<s:text name="name.mainFrame" />: " title="<s:text name="name.mainFrame" />" /><s:property value="#rowstatus.index"/></s:if>
		<s:else><s:property value="#rowstatus.index"/></s:else>
	</td>
	<td>
		<a href="
		<s:url action="editFrame" namespace="/do/Page">
			<s:param name="pageCode"><s:property value="currentPage.code"/></s:param>
			<s:param name="frame"><s:property value="#rowstatus.index"/></s:param>
		</s:url>
		"><s:property value="currentPage.getModel().getFrames()[#rowstatus.index]"/></a>
	</td>
	<td><s:property value="%{getTitle(#showletType.getCode(), #showletType.getTitles())}" /></td>
	<td class="icon">
		<a href="
		<s:url action="removeShowlet" namespace="/do/Page">
			<s:param name="pageCode"><s:property value="currentPage.code"/></s:param>
			<s:param name="frame"><s:property value="#rowstatus.index"/></s:param>
		</s:url>
		" title="<s:text name="label.clear" />: <s:property value="%{getTitle(#showletType.code, #showletType.titles)}" />"><img src="<wp:resourceURL/>administration/img/icons/clear.png" alt="<s:text name="label.alt.clear" />" /></a>
	</td>
</tr>
</s:iterator>
</table>