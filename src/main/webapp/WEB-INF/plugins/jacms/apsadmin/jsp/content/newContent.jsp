<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="wpsa" uri="apsadmin-core.tld" %>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>

<h1><a href="<s:url action="list" namespace="/do/jacms/Content" />" title="<s:text name="note.goToSomewhere" />: <s:text name="title.contentManagement" />"><s:text name="title.contentManagement" /></a></h1>
<h2><s:text name="title.contentEditing" /></h2>
<h3><s:text name="title.contentInfo" /></h3>

	<s:if test="hasFieldErrors()">
<div class="message message_error">
<h4><s:text name="message.title.FieldErrors" /></h4>	
	<ul>
	<s:iterator value="fieldErrors">
		<s:iterator value="value">
		<li><s:property escape="false" /></li>
		</s:iterator>
	</s:iterator>
	</ul>
</div>
	</s:if>

<s:form action="createNewVoid">
<p><s:text name="note.editContentIntro" /></p>
	<p>
	<label for="contentTypeCode"><s:text name="name.contentType" />:</label><br />
	<wpsf:select name="contentTypeCode" id="contentTypeCode" list="contentTypes" listKey="code" listValue="descr" cssClass="text" />
	</p>
	<p>
	<label for="contentDescription"><s:text name="name.contentDescription" />:</label><br />
	<wpsf:textfield name="contentDescription" id="contentDescription" cssClass="text" />
	</p>
	<p>
	<label for="contentStatus"><s:text name="label.state" />:</label><br />
	<wpsf:select name="contentStatus" id="contentStatus" list="avalaibleStatus" cssClass="text" listKey="key" listValue="%{getText(value)}" />
	</p>
	<p><wpsf:submit value="%{getText('label.continue')}" title="%{getText('note.button.nextToContentEditing')}" cssClass="button" /></p>
</s:form>