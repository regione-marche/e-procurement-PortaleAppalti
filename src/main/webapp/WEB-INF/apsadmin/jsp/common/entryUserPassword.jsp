<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="wpsa" uri="apsadmin-core.tld" %>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>
<h1><s:text name="title.changePassword" /></h1>
<s:form action="changePassword">

	<s:if test="hasFieldErrors()">
<div class="message message_error">
<h2><s:text name="message.title.FieldErrors" /></h2>	
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
<h2><s:text name="message.title.ActionErrors" /></h2>	
	<ul>
	<s:iterator value="actionErrors">
		<li><s:property escape="false" /></li>
	</s:iterator>
	</ul>
</div>
	</s:if>

<p class="noscreen">
	<wpsf:hidden name="username" />
</p>

<p>
	<label for="oldPassword"><s:text name="label.oldPassword" />:</label><br />
	<wpsf:password name="oldPassword" id="oldPassword" cssClass="text" />
</p>

<p>
	<label for="password"><s:text name="label.password" />:</label><br />
	<wpsf:password name="password" id="password" cssClass="text" />
</p>

<p>
	<label for="passwordConfirm"><s:text name="label.passwordConfirm" />:</label><br />
	<wpsf:password name="passwordConfirm" id="passwordConfirm" cssClass="text" />
</p>

<p><wpsf:submit value="%{getText('label.confirm')}" cssClass="button" /></p>

</s:form>