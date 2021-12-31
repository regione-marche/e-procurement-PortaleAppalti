<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wpsa" uri="apsadmin-core.tld" %>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>

<h1><a href="<s:url action="list" namespace="/do/Group" />" title="<s:text name="note.goToSomewhere" />: <s:text name="title.groupManagement" />"><s:text name="title.groupManagement" /></a></h1>

<h2><s:text name="title.groupManagement.groupTrash" /></h2>
 
<s:form action="delete">
	<p class="noscreen"><wpsf:hidden name="name"/></p>

	<p>
		<s:text name="note.groupConfirm.trash" /> "<s:property value="name" />"? 
		<wpsf:submit value="%{getText('label.remove')}" cssClass="button" />
	</p>

	<p>
		<s:text name="note.groupConfirm.trash.goBack" />&#32;<a href="<s:url action="list" namespace="/do/Group" />"><s:text name="menu.accountAdmin.groups" /></a>
	</p>

</s:form>

