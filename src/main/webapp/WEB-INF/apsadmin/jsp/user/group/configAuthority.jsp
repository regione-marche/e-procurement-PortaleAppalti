<%@ taglib prefix="s" uri="/struts-tags" %>

<h1><a href="<s:url action="list" namespace="/do/Group" />" title="<s:text name="note.goToSomewhere" />: <s:text name="title.groupManagement" />"><s:text name="title.groupManagement" /></a></h1>
<h2><s:text name="title.groupManagement.membersOf" />: <s:property value="authName" /></h2>

<s:include value="/WEB-INF/apsadmin/jsp/user/include_configAuthority.jsp" />