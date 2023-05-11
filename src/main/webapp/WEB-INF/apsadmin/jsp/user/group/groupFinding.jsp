<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="wpsa" uri="apsadmin-core.tld" %>
<h1><s:text name="title.groupManagement" /></h1>
<%-- <h2><s:text name="title.groupManagement.groupList" /></h2> --%>

<s:if test="hasActionErrors()">
<div class="message message_error">
<h2><s:text name="message.title.ActionErrors" /></h2>
	<ul>
		<s:iterator value="actionErrors">
			<li><s:property escape="false" /></li>
		</s:iterator>
	</ul>
</div>
</s:if>


<p><a href="<s:url action="new" namespace="/do/Group" />"><s:text name="menu.accountAdmin.groupNew" /></a></p>

<s:if test="%{groups.size > 0}">
	<table class="generic" summary="<s:text name="note.groupList.summary" />">
	<tr>
		<th><s:text name="label.group" /></th>
		<th><s:text name="label.description" /></th>	
		<th class="icon"><abbr title="<s:text name="label.users" />">U</abbr></th>
		<th class="icon"><abbr title="<s:text name="label.remove" />">&ndash;</abbr></th>	
	</tr>
	<s:iterator id="group" value="groups">
	<tr>
		<td><a href="<s:url action="edit"><s:param name="name" value="#group.name"/></s:url>" ><s:property value="#group.name" /></a></td>
		<td><s:property value="#group.descr" /></td>
		<td class="icon"><a href="<s:url namespace="/do/Group/Auth" action="config"><s:param name="authName" value="#group.name"/></s:url>" title="<s:text name="note.manageUsersFor" />: <s:property value="#group.name" />"><img src="<wp:resourceURL />administration/img/icons/users.png" alt="<s:text name="note.manageUsersFor" />: <s:property value="#group.name" />" /></a></td>
		<td class="icon"><a href="<s:url action="trash"><s:param name="name" value="#group.name"/></s:url>"  title="<s:text name="label.remove" />: <s:property value="#group.name" />"><img src="<wp:resourceURL />administration/img/icons/delete.png" alt="<s:text name="label.alt.clear" />" /></a></td>
	</tr>
	</s:iterator>
	</table>
</s:if>
<s:else>
	<p>
		<s:text name="noGroups.found" />
	</p>
</s:else>