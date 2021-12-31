<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wpsa" uri="apsadmin-core.tld" %>

<h1><a href="<s:url action="viewTree" namespace="/do/Category" />" title="<s:text name="note.goToSomewhere" />: <s:text name="title.categoryManagement" />"><s:text name="title.categoryManagement" /></a></h1>

<div class="message message_error">
<h2><s:text name="message.title.ActionErrors" /></h2>
<p><s:text name="message.note.resolveReferences" />:</p>
<s:if test="references['jacmsContentManagerUtilizers']">
<h3><s:text name="message.title.referencedContents" /></h3>
<ul>
<s:iterator id="content" value="references['jacmsContentManagerUtilizers']">
	<li><s:property value="#content.id" /> - <s:property value="#content.descr" /></li>
</s:iterator>
</ul>
</s:if>

<s:if test="references['jacmsResourceManagerUtilizers']">
<h3><s:text name="message.title.referencedResources" /></h3>
<ul>
<s:iterator value="references['jacmsResourceManagerUtilizers']" id="resource">
	<li><s:property value="#resource.id" /> - <s:property value="#resource.descr" /></li>
</s:iterator>
</ul>
</s:if>

</div>