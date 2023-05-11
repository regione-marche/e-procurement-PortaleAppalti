<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>

<h1><a href="<s:url action="viewTree" namespace="/do/Page" />" title="<s:text name="note.goToSomewhere" />: <s:text name="title.pageManagement" />"><s:text name="title.pageManagement" /></a></h1>
<h2><s:text name="title.detailPage" /></h2>

<p><s:text name="note.youAreHere" />: 
<s:set value="%{getBreadCrumbsTargets(selectedNode)}" name="breadCrumbsTargets" ></s:set>
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

<dl class="table-display">
	<dt><s:text name="name.pageCode" /></dt>
		<dd><s:property value="pageCode" /></dd>
	<dt><s:text name="name.pageTitle" /></dt>
		<dd>
<s:iterator value="langs" status="rowStatus">
		<s:if test="#rowStatus.index != 0">, </s:if><span class="monospace">(<abbr title="<s:property value="descr" />"><s:property value="code" /></abbr>)</span> <s:property value="%{titles.get(code)}" />
</s:iterator>
		</dd>
	<dt><s:text name="name.pageGroup" /></dt>
		<dd>
			<s:property value="%{getSystemGroups().get(group).descr}" />
		</dd>
	<dt><s:text name="name.pageModel" /></dt>
		<dd>
			<s:property value="%{getPageModel(model).descr}" />
		</dd>

<s:if test="showable">
	<s:set name="iconImagePath" id="iconImagePath"><wp:resourceURL/>administration/img/icons/true.png</s:set>
	<s:set name="booleanStatus" value="%{getText('label.yes')}" />
</s:if>
<s:else>
	<s:set name="iconImagePath" id="iconImagePath"><wp:resourceURL/>administration/img/icons/false.png</s:set>
	<s:set name="booleanStatus" value="%{getText('label.no')}" />
</s:else>		
	<dt><s:text name="name.isShowablePage" /></dt>
		<dd>
			<img src="<s:property value="iconImagePath" />" alt="<s:property value="booleanStatus" />" title="<s:property value="booleanStatus" />" />
		</dd>
		
<s:if test="viewerPage">
	<s:set name="iconImagePath" id="iconImagePath"><wp:resourceURL/>administration/img/icons/true.png</s:set>
	<s:set name="booleanStatus" value="%{getText('label.yes')}" />
</s:if>
<s:else>
	<s:set name="iconImagePath" id="iconImagePath"><wp:resourceURL/>administration/img/icons/false.png</s:set>
	<s:set name="booleanStatus" value="%{getText('label.no')}" />
</s:else>		
	<dt><s:text name="name.isViewerPage" /></dt>
		<dd>
			<img src="<s:property value="iconImagePath" />" alt="<s:property value="booleanStatus" />" title="<s:property value="booleanStatus" />" />
		</dd>		
</dl>