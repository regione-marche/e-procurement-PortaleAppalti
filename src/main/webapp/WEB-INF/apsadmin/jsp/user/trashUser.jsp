<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wpsa" uri="apsadmin-core.tld" %>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>

<h1><a href="<s:url action="list" namespace="/do/User" />" title="<s:text name="note.goToSomewhere" />: <s:text name="title.userManagement" />"><s:text name="title.userManagement" /></a></h1>

<h2><s:text name="title.userManagement.userTrash" /></h2>
 
<s:form action="delete" namespace="/do/User" >
	<p class="noscreen"><wpsf:hidden name="username"/></p>
	
	<p>
		<s:text name="note.userConfirm.trash" /> "<s:property value="username" />"?
		<wpsf:submit value="%{getText('label.remove')}" cssClass="button" />
	</p>
	
	<p><s:text name="note.userConfirm.trash.goBack" />&#32;<a href="<s:url action="list" namespace="/do/User" />"><s:text name="menu.accountAdmin.users" /></a></p>
</s:form>