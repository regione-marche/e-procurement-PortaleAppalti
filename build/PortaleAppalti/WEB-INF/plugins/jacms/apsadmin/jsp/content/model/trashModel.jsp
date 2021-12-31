<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="wpsa" uri="apsadmin-core.tld" %>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>

<h1><s:text name="title.contentManagement" /></h1>
<h2><a href="<s:url action="list" namespace="/do/jacms/ContentModel" />" title="<s:text name="note.goToSomewhere" />: <s:text name="title.generalSettings.contentModels" />"><s:text name="title.generalSettings.contentModels" /></a></h2>
<h3><s:text name="title.generalSettings.contentModels.remove" /></h3>

<p><s:text name="note.deleteContentModel.areYouSure" />?</p>
<p><span class="monospace"><s:property value="modelId" /></span> &ndash; <s:property value="description" /> (<s:property value="%{getSmallContentType(getContentModel(modelId).contentType).descr}" />)</p>

<s:form>
	<p class="noscreen">	
		<wpsf:hidden name="modelId"/>
	</p>
	<p><wpsf:submit action="delete" value="%{getText('label.remove')}" cssClass="button" /></p>
</s:form>
