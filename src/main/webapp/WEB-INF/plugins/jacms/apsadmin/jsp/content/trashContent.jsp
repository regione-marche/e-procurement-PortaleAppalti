<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>
<h1><a href="<s:url action="list" namespace="/do/jacms/Content" />" title="<s:text name="note.goToSomewhere" />: <s:text name="title.contentManagement" />"><s:text name="title.contentManagement" /></a></h1>
<h2><s:text name="title.contentManagement.trashContent" /></h2>

<s:form action="deleteContentGroup">
<s:iterator id="contentIdToDelete" value="contentIds">
<p class="noscreen"><input type="hidden" name="contentIds" value="<s:property value="#contentIdToDelete" />" /></p>
</s:iterator>

<p><s:text name="note.trashContent.areYouSure" />?</p>
<ul>
<s:iterator id="contentIdToDelete" value="contentIds">
<s:set name="content" value="%{getContentVo(#contentIdToDelete)}"></s:set>
	<li><span class="monospace"><s:property value="#contentIdToDelete" /></span> &ndash; <s:property value="#content.descr" /> (<s:property value="%{getSmallContentType(#content.typeCode).descr}" />)</li>
</s:iterator>
</ul>
<p><wpsf:submit value="%{getText('label.remove')}" cssClass="button" /></p>
</s:form>