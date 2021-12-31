<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="wpsa" uri="apsadmin-core.tld" %>
<h1><s:text name="title.roleManagement" /></h1>
<%--<h2><s:text name="title.roleManagement.roleList" /></h2>  --%>

	<s:if test="hasActionErrors()">
<div class="message message_error">	
<h3><s:text name="message.title.ActionErrors" /></h3>
		<ul>
			<s:iterator value="actionErrors">
				<li><s:property escape="false" /></li>
			</s:iterator>
		</ul>
</div>
	</s:if>	

<p><a href="<s:url action="new" namespace="/do/Role" />"><s:text name="menu.accountAdmin.roleNew" /></a></p>

<table class="generic" summary="<s:text name="note.roleList.summary" />">
<tr>
	<th><s:text name="label.role" /></th>
	<th><s:text name="label.description" /></th>	
	<th class="icon"><abbr title="<s:text name="label.users" />">U</abbr></th>
	<th class="icon"><abbr title="<s:text name="label.remove" />">&ndash;</abbr></th>
</tr>
<s:iterator id="role" value="roles">
<tr>
	<td><a href="<s:url action="edit"><s:param name="name" value="#role.name"/></s:url>" ><s:property value="#role.name" /></a></td>
 	<td><s:property value="#role.description" /></td>
	<td class="icon"><a href="<s:url namespace="/do/Role/Auth" action="config"><s:param name="authName" value="#role.name"/></s:url>" title="<s:text name="note.assignToUsers" />: <s:property value="#role.name" />"><img src="<wp:resourceURL />administration/img/icons/users.png" alt="<s:text name="note.assignToUsers" />: <s:property value="#role.name" />" /></a></td>
	<td class="icon"><a href="<s:url action="trash"><s:param name="name" value="#role.name"/></s:url>"  title="<s:text name="label.remove" />: <s:property value="#role.name" />"><img src="<wp:resourceURL />administration/img/icons/delete.png" alt="<s:text name="label.alt.clear" />" /></a></td>
</tr>
</s:iterator>
</table>