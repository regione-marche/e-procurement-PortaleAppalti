<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wpsa" uri="apsadmin-core.tld" %>

NAME: <s:property value="name" />

<br />

DESCRIPTION: <s:property value="description" />

<br />

<s:iterator value="rolePermissions">
	PERMISSION: <s:property value="name" /> - <s:property value="description" />
	
	<br />
	
</s:iterator>