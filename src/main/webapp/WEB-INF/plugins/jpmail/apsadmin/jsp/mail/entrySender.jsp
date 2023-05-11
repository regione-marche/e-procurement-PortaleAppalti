<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="wpsa" uri="apsadmin-core.tld" %>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>

<h1><a href="<s:url action="viewSenders" />" title="<s:text name="note.goToSomewhere" />: <s:text name="title.eMailManagement" /> "><s:text name="title.eMailManagement" /></a></h1>

<h2>
	<s:if test="%{strutsAction==1}" ><s:text name="title.eMailManagement.newSender" /></s:if>
	<s:else><s:text name="title.eMailManagement.editSender" />:&nbsp;<s:property value="currentCode"/></s:else>
</h2>

<s:form action="saveSender" >
	<s:if test="hasFieldErrors()">
	<div class="message message_error">	
		<h3><s:text name="message.title.FieldErrors" /></h3>
		<ul>
			<s:iterator value="fieldErrors">
				<s:iterator value="value">
					<li><s:property escape="false" /></li>
				</s:iterator>
			</s:iterator>
		</ul>
	</div>
	</s:if>
	<s:if test="hasActionErrors()">
	<div class="message message_error">	
		<h3><s:text name="message.title.ActionErrors" /></h3>
		<ul>
			<s:iterator value="actionErrors">
				<li><s:property escape="false" /></li>
			</s:iterator>
		</ul>
	</div>
	</s:if>
	
<p class="noscreen">	
	<wpsf:hidden name="strutsAction"/>
	<wpsf:hidden name="currentCode"/>
</p>
<fieldset><legend><s:text name="label.sender" /></legend>
<p>
	<label for="code"><s:text name="code" />:</label><br />
	<wpsf:textfield name="code" id="code" cssClass="text" />
</p>
<p>
	<label for="mail"><s:text name="mail" />:</label><br />
	<wpsf:textfield name="mail" id="mail" cssClass="text" />
</p>
</fieldset>
<p>
	<wpsf:submit value="%{getText('label.save')}" cssClass="button" />
</p>
	
</s:form>