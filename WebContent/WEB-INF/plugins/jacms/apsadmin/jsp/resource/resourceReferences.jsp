<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>

<h1><a href="<s:url action="list" namespace="/do/jacms/Resource"><s:param name="resourceTypeCode"><s:property value="resourceTypeCode" /></s:param></s:url>" title="<s:text name="note.goToSomewhere" />: <s:text name="title.resourceManagement" />"><s:text name="title.resourceManagement" /></a></h1>

<div class="message message_error">
<h2><s:text name="message.title.ActionErrors" /></h2>
<p><s:text name="message.note.resolveReferences" />:</p>
<s:if test="references['jacmsContentManagerUtilizers']">
<h3><s:text name="message.title.referencedContents" /></h3>
<ul>
<s:iterator value="references['jacmsContentManagerUtilizers']" id="content">
	<li><s:property value="#content.id" /> - <s:property value="#content.descr" /></li>
</s:iterator>
</ul>
</s:if>
</div>