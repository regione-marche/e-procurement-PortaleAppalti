<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wpsa" uri="apsadmin-core.tld" %>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>

<h1><a href="<s:url action="list" namespace="/do/Role" />" title="<s:text name="note.goToSomewhere" />: <s:text name="title.roleManagement" />"><s:text name="title.roleManagement" /></a></h1>
<h2><s:text name="title.roleManagement.roleTrash" /></h2>

<s:form action="delete" >
	<p class="noscreen"><wpsf:hidden name="name"/></p>
	
	<p> 
		<s:text name="note.roleConfirm.trash" /> "<s:property value="name" />"? 
		<wpsf:submit value="%{getText('label.remove')}" cssClass="button" />
	</p>

	<p>
		<s:text name="note.roleConfirm.trash.goBack" />&#32;<a href="<s:url action="list" namespace="/do/Role" />"><s:text name="menu.accountAdmin.roles" /></a>
	</p>

</s:form>