<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>


<h1><s:text name="title.generalSettings" /></h1>
<h2><a href="<s:url action="viewManagers" namespace="/do/Entity" />" title="<s:text name="note.goToSomewhere" />: <s:text name="title.entityManagement" />"><s:text name="title.entityAdmin.manage" /></a></h2>

<%--
<h3><s:text name="title.entityAdmin.entityManagers.reload" /></h3>
--%>

<p class="message message_confirm">
	<span class="noscreen"><s:text name="messages.confirm" />: </span><s:text name="message.reloadEntities.ok" />.
</p>

<p>
	<a href="<s:url action="viewManagers" namespace="/do/Entity" />"><s:text name="note.backToSomewhere" />: <s:text name="title.entityManagement" /></a>
</p>