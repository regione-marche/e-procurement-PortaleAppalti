<%@ taglib prefix="s" uri="/struts-tags" %>

<h1><a href="<s:url action="list" namespace="/do/Role" />" title="<s:text name="note.goToSomewhere" />: <s:text name="title.roleManagement" />"><s:text name="title.roleManagement" /></a></h1>
<h2><s:text name="title.roleManagement.assignToUsers" />: <s:property value="authName" /></h2>

<s:include value="/WEB-INF/apsadmin/jsp/user/include_configAuthority.jsp" />