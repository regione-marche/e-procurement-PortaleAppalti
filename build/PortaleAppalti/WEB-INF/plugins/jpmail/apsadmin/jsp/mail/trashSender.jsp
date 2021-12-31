<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wpsa" uri="apsadmin-core.tld" %>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>

<h1><a href="<s:url action="viewSenders" />" title="<s:text name="note.goToSomewhere" />: <s:text name="title.eMailManagement.sendersConfig" />"><s:text name="title.eMailManagement.sendersConfig" /></a></h1>

<h2><s:text name="title.eMailManagement.sendersConfig.trashSender" /></h2>
 
<s:form action="deleteSender" >
	<p class="noscreen"><wpsf:hidden name="code"/></p>
	
	<p>
		<s:text name="note.sendersConfig.trash" /> "<s:property value="code" />"?
		<wpsf:submit value="%{getText('label.remove')}" cssClass="button" />
	</p>
	
	<p><s:text name="note.sendersConfig.trash.goBack" />&nbsp;<a href="<s:url action="viewSenders"/>"><s:text name="title.eMailManagement.sendersConfig" /></a></p>
</s:form>