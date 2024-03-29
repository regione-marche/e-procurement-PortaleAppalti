<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>

<h1><a href="<s:url action="viewManagers" namespace="/do/Entity" />" title="<s:text name="note.goToSomewhere" />: <s:text name="title.entityManagement" />"><s:text name="title.entityManagement" /></a></h1>
<h2><a href="<s:url action="initViewEntityTypes" namespace="/do/Entity"><s:param name="entityManagerName"><s:property value="entityManagerName" /></s:param></s:url>" title="<s:text name="note.goToSomewhere" />: <s:text name="title.entityAdmin.manager" />&#32;<s:property value="entityManagerName" />"><s:text name="title.entityAdmin.manager" />: <s:property value="entityManagerName" /></a></h2>
<h3><s:text name="title.entityTypes.editType.delete" />: <span class="monospace"><s:property value="entityTypeCode" /> - <s:property value="%{getEntityPrototype(entityTypeCode).typeDescr}" /></span></h3>

<s:form action="removeEntityType">
	<p class="noscreen">
		<wpsf:hidden name="entityManagerName" />
		<wpsf:hidden name="entityTypeCode" />
	</p>

	<p>
		<s:text name="note.entityTypes.deleteType.areYouSure" />:&#32;<em class="important"><span class="monospace"><s:property value="entityTypeCode" /> - <s:property value="%{getEntityPrototype(entityTypeCode).typeDescr}" /></span></em>&#32;?
		<wpsf:submit value="%{getText('label.remove')}" cssClass="button" />
	</p>
	
	<p><a href="<s:url action="initViewEntityTypes" namespace="/do/Entity"><s:param name="entityManagerName"><s:property value="entityManagerName" /></s:param></s:url>"><s:text name="note.backToSomewhere" />: <s:text name="title.entityAdmin.manager" />&#32;<s:property value="entityManagerName" /></a></p>

</s:form>