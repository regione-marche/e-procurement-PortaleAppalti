<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>

<h1><a href="<s:url action="viewTree" namespace="/do/Category" />" title="<s:text name="note.goToSomewhere" />: <s:text name="title.categoryManagement" />"><s:text name="title.categoryManagement" /></a></h1>

<%-- CATEGORIA padre = <s:property value="getCategory(selectedNode).parent.code" />  --%>

<s:form action="delete">
<p class="noscreen">
	<wpsf:hidden name="selectedNode" />
</p>
<p><s:text name="note.deleteCategory.areYouSure" />&#32;<em class="important"><s:property value="getCategory(selectedNode).title" /></em>?</p>
<p><wpsf:submit value="%{getText('label.remove')}" cssClass="button" /></p>
</s:form>