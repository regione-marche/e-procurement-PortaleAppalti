<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wpsa" uri="apsadmin-core.tld" %>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>

<h1><a href="<s:url action="list" namespace="/do/Role" />" title="<s:text name="note.goToSomewhere" />: <s:text name="title.roleManagement" />"><s:text name="title.roleManagement" /></a></h1>

<s:if test="getStrutsAction() == 1">
	<h2><s:text name="title.roleManagement.roleNew" /></h2>
</s:if>
<s:if test="getStrutsAction() == 2">
	<h2><s:text name="title.roleManagement.roleEdit" /></h2>	
</s:if>

<s:form action="save" >
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
	<wpsf:hidden name="strutsAction" />
</p>

<s:if test="getStrutsAction() == 2">
<p class="noscreen">
	<wpsf:hidden name="name" />
</p>
</s:if>

<p>
	<label for="name"><s:text name="name" />:</label><br />
	<wpsf:textfield name="name" disabled="%{getStrutsAction() == 2}" cssClass="text" />
</p>

<p>
	<label for="description"><s:text name="description" />:</label><br />
	<wpsf:textfield name="description" id="description" cssClass="text" />
</p>	
	
	<%-- Per WILLY - Genera una serie di checkbox e labels associate.
		vedere link http://struts.apache.org/2.x/docs/checkboxlist.html
	 --%>
<fieldset><legend><s:text name="name.permissions" /></legend>
	<p><s:text name="note.permissions.intro" /></p>
	<s:checkboxlist name="permissionNames" list="systemPermissions" listKey="name" listValue="description" />
</fieldset>
<p>
	<wpsf:submit value="%{getText('label.save')}" cssClass="button" />
</p>
	
</s:form>