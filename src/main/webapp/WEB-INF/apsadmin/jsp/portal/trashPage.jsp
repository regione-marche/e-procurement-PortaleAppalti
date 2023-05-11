<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>

<h1><a href="<s:url action="viewTree" namespace="/do/Page" />" title="<s:text name="note.goToSomewhere" />: <s:text name="title.pageManagement" />"><s:text name="title.pageManagement" /></a></h1>

<s:form action="delete">
<p class="noscreen">
	<wpsf:hidden name="selectedNode"/>
	<wpsf:hidden name="nodeToBeDelete" />
</p>
<p><s:text name="note.deletePage.areYouSure" />&#32;<em class="important"><s:property value="%{getPage(nodeToBeDelete).getTitle(currentLang.getCode())}" /></em>?</p>
<p><wpsf:submit value="%{getText('page.options.delete')}" cssClass="button" /></p>
</s:form>