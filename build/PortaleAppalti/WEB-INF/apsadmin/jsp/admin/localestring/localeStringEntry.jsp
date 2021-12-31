<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>
<%@ taglib prefix="wpsa" uri="apsadmin-core.tld" %>

<h1><s:text name="title.languageAdmin" /></h1>
<h2><s:text name="title.languageAdmin.labels" /></h2>

<s:if test="getStrutsAction() == 1">
	<h3><s:text name="title.generalSettings.locale.new" /></h3>
</s:if>
<s:elseif test="getStrutsAction() == 2">
	<h3><s:text name="title.generalSettings.locale.edit" /></h3>
</s:elseif>
	
<s:form action="save" namespace="/do/LocaleString" >
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

	<p class="noscreen">
		<wpsf:hidden value="%{getStrutsAction()}" name="strutsAction"/>
	<s:if test="getStrutsAction() == 2">	
		<wpsf:hidden value="%{key}" name="key" />
	</s:if> 
	</p>

	<p>
		<label for="editLabel_key"><s:text name="label.code" />:</label><br />
		<wpsf:textfield value="%{key}" name="key" id="editLabel_key" disabled="%{getStrutsAction() == 2}" cssClass="text" />
	</p>	

	<s:iterator id="l" value="langs">
		<p>
			<label for="lang<s:property value="code"/>">
			   <span class="monospace">(<s:property value="code" />)</span>&#32;<s:text name="label.description" />:
			</label>
			<br />
			<wpsf:textarea name="%{code}" id="%{'lang'+code}" value="%{labels[#l.code]}" cssClass="text" rows="5" cols="80"></wpsf:textarea>
		</p> 
	</s:iterator>  

	<p><wpsf:submit value="%{getText('label.save')}" cssClass="button" /></p>
</s:form> 