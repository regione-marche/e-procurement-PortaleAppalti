<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wpsa" uri="apsadmin-core.tld" %>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>

<h1><a href="<s:url action="list" namespace="/do/jacms/Resource"><s:param name="resourceTypeCode"><s:property value="resourceTypeCode" /></s:param></s:url>" title="<s:text name="note.goToSomewhere" />: <s:text name="title.resourceManagement" />"><s:text name="title.resourceManagement" /></a></h1>
<h2><s:text name="title.resourceManagement.resourceTrash" /></h2>

<s:form action="delete">
	<p class="noscreen">
		<wpsf:hidden name="resourceId"/>
		<wpsf:hidden name="resourceTypeCode" />
	</p>

	<p>
		<s:text name="note.deleteResource.areYouSure" />:&#32;<em class="important"><s:property value="%{loadResource(resourceId).descr}" /></em>&#32;?
		<wpsf:submit value="%{getText('label.remove')}" cssClass="button" />
	</p>
	
	<p><s:text name="note.deleteResource.areYouSure.goBack" />&#32;<a href="<s:url action="list" namespace="/do/jacms/Resource"><s:param name="resourceTypeCode"><s:property value="resourceTypeCode" /></s:param></s:url>" title="<s:text name="note.goToSomewhere" />: <s:text name="title.resourceManagement" />"><s:text name="title.resourceManagement" /></a></p>
</s:form>