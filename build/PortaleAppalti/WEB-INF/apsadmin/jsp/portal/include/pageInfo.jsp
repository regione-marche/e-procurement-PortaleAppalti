<%@ taglib prefix="s" uri="/struts-tags" %>
<p><s:text name="note.youAreHere" />: 

<s:set value="%{getBreadCrumbsTargets(currentPage.code)}" name="breadCrumbsTargets" ></s:set>
<s:iterator value="#breadCrumbsTargets" id="target" status="rowstatus">
<s:if test="%{#rowstatus.index != 0}"> &raquo; </s:if>
<s:if test="%{#rowstatus.index == (#breadCrumbsTargets.size()-1)}">
<s:property value="#target.titles[currentLang.code]" />
</s:if>
<s:else>
<a href="<s:url namespace="/do/Page" action="viewTree" ><s:param name="selectedNode"><s:property value="#target.code" /></s:param></s:url>" title="<s:text name="note.goToSomewhere" />: <s:property value="#target.titles[currentLang.code]" />"><s:property value="#target.titles[currentLang.code]" /></a>
</s:else>
</s:iterator>

</p>
<h3><s:text name="title.configPage.youAreDoing" /></h3>

<dl class="table-display">
	<dt><s:text name="name.pageTitle" /></dt>
		<dd>
<s:iterator value="langs" status="rowStatus">
		<s:if test="#rowStatus.index != 0">, </s:if><span class="monospace">(<abbr title="<s:property value="descr" />"><s:property value="code" /></abbr>)</span> <s:property value="currentPage.getTitles()[code]" />
</s:iterator>
		</dd>
	<dt><s:text name="name.pageGroup" /></dt>
		<dd><s:property value="systemGroups[currentPage.group].descr" /></dd>
	<dt><s:text name="name.pageModel" /></dt>
		<dd><s:property value="currentPage.getModel().getDescr()" /></dd>		
</dl>

