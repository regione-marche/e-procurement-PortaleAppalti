<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wpsa" uri="apsadmin-core.tld" %>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>

<h1><a href="<s:url action="list" namespace="/do/User" />" title="<s:text name="note.goToSomewhere" />: <s:text name="title.userManagement" />"><s:text name="title.userManagement" /></a></h1>
<s:if test="getStrutsAction() == 1">
	<h2><s:text name="title.userManagement.userNew" /></h2>
</s:if>
<s:if test="getStrutsAction() == 2">
	<h2><s:text name="title.userManagement.userEdit" /></h2>	
</s:if>

<s:form action="save" namespace="/do/User">
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
	<wpsf:hidden name="username" />
</p>
</s:if>

<fieldset><legend>Info</legend>
<p>
	<label for="username"><s:text name="username" />:</label><br />
	<wpsf:textfield name="username" id="username" disabled="%{getStrutsAction() == 2}" cssClass="text" />
</p>

<p>
	<label for="password"><s:text name="password" />:</label><br />
	<wpsf:password name="password" id="password" cssClass="text" />
</p>

<p>
	<label for="passwordConfirm"><s:text name="passwordConfirm" />:</label><br />
	<wpsf:password name="passwordConfirm" id="passwordConfirm" cssClass="text" />
</p>


<p>
	<wpsf:checkbox name="active" id="active" />&#32;<label for="active"><s:text name="note.userStatus.active" /></label>
</p>
</fieldset>

<s:if test="getStrutsAction() == 2">
<fieldset><legend><s:text name="label.state" /></legend>
<dl class="table-display">
<dt><s:text name="label.date.registration" /></dt>
	<dd><s:date name="user.creationDate" format="dd/MM/yyyy" /></dd>
<dt><s:text name="label.date.lastLogin" /></dt>
	<dd><s:date name="user.lastAccess" format="dd/MM/yyyy" /><s:if test="!user.accountNotExpired">&#32;<span class="important">(<s:text name="note.userStatus.expiredAccount" />)</span></s:if></dd>
<dt><s:text name="label.date.lastPasswordChange" /></dt>
	<dd><s:date name="user.lastPasswordChange" format="dd/MM/yyyy" /><s:if test="!user.credentialsNotExpired">&#32;<span class="important">(<s:text name="note.userStatus.expiredPassword" />)</span></s:if></dd>
</dl>	
<p>
	<wpsf:checkbox name="reset" id="reset" />&#32;<label for="reset"><s:text name="note.userStatus.reset" /></label>
</p>

</fieldset>
</s:if>



<p>
	<wpsf:submit value="%{getText('label.save')}" cssClass="button" />
</p>

</s:form>