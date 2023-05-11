<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="wpsa" uri="apsadmin-core.tld" %>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>

<h1><s:text name="title.eMailManagement" /></h1>
<h2><s:text name="label.smtpConfig" /></h2>

<s:form action="saveSmtp" >
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

<p>
	<s:text name="label.jpmail.intro" />
</p>

<p>
	<label for="smtpHost"><s:text name="smtpHost" />:</label><br />
	<wpsf:textfield name="smtpHost" id="smtpHost" cssClass="text" />
</p>
<p>
	<label for="smtpPort"><s:text name="label.smtpPort" />:</label><br />
	<wpsf:textfield name="smtpPort" id="smtpPort" cssClass="text" />
</p>
<p>
	<label for="smtpProtocol"><s:text name="label.smtpProtocol" />:</label><br />
	<wpsf:select name="smtpProtocol" id="smtpProtocol" list="availableProtocols" cssClass="text" listKey="key" listValue="%{getText(value)}" />
</p>
<p>
	<label for="smtpTimeout"><s:text name="label.smtpTimeout" />:</label><br />
	<wpsf:textfield name="smtpTimeout" id="smtpTimeout" cssClass="text" />
</p>
<p>
	<label for="smtpUserName"><s:text name="smtpUserName" />:</label><br />
	<wpsf:textfield name="smtpUserName" id="smtpUserName" cssClass="text" />
</p>
<p>
	<label for="smtpPassword"><s:text name="smtpPassword" />:</label><br />
	<wpsf:password name="smtpPassword" id="smtpPassword" cssClass="text" />
</p>
<p>
	<wpsf:checkbox name="debug" id="debug" />&nbsp;<label for="debug"><s:text name="label.debug" /></label>
</p>
<p>
	<wpsf:submit value="%{getText('label.save')}" cssClass="button" />
</p>
	
</s:form>