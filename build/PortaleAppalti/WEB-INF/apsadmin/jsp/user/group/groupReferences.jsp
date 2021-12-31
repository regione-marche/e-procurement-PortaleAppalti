<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>
<h1><a href="<s:url action="list" namespace="/do/Group" />" title="<s:text name="note.goToSomewhere" />: <s:text name="title.groupManagement" />"><s:text name="title.groupManagement" /></a></h1>

<div class="message message_error">
<h2><s:text name="message.title.ActionErrors" /></h2>
<p><s:text name="message.note.resolveReferences" />:</p>

<s:if test="references['PageManagerUtilizers']">
<h3><s:text name="message.title.referencedPages" /></h3>
<ul>
<s:iterator value="references['PageManagerUtilizers']" id="page">
	<li><s:property value="#page.code" /> - <s:property value="#page.titles[currentLang.code]" /></li>
</s:iterator>
</ul>
</s:if>

<s:if test="references['UserManagerUtilizers']">
<h3><s:text name="message.title.referencedUsers" /></h3>
<ul>
<s:iterator value="references['UserManagerUtilizers']" id="user">
	<li><s:property value="#user.username" /></li>
</s:iterator>
</ul>
</s:if>

</div>