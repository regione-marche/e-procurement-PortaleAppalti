<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>
<%@ taglib prefix="wpsa" uri="apsadmin-core.tld" %>
<h1><s:text name="title.contentManagement" /></h1>
<h2><s:text name="title.generalSettings.contentModels" /></h2>

<s:if test="getStrutsAction() == 1">
	<h3><s:text name="title.generalSettings.contentModels.new" /></h3>
</s:if>
<s:if test="getStrutsAction() == 2">
	<h3><s:text name="title.generalSettings.contentModels.edit" /></h3>
</s:if>                 

<s:form action="save" namespace="/do/jacms/ContentModel" >
	
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
	 
	<p class="noscreen">
		<wpsf:hidden value="%{getStrutsAction()}" name="strutsAction"/>
		
		<s:if test="getStrutsAction() == 2">
			<wpsf:hidden name="contentType" value="%{contentType}" />  
			<wpsf:hidden name="modelId" value="%{modelId}" />  
		</s:if> 
	</p>
	<p>
		<label for="newModel_id"><s:text name="contentModel.id" />:</label><br /> 
			<wpsf:textfield name="modelId" id="newModel_id" disabled="%{getStrutsAction() == 2}" cssClass="text" />  
	</p>  

	<p>
		<label for="newModel_contentType"><s:text name="contentModel.type" />:</label><br /> 
		 <wpsf:select id="newModel_contentType" list="smallContentTypes" name="contentType" listKey="code"  listValue="descr" disabled="%{getStrutsAction() == 2}" cssClass="text"></wpsf:select>
	</p> 
	<p>
		<label for="newModel_description"><s:text name="label.description" />:</label><br />
		<wpsf:textfield name="description" id="newModel_description" cssClass="text" />
	</p>
	<p>
		<label for="newModel_contentShape"><s:text name="contentModel.label.shape" />:</label><br />
		<wpsf:textarea name="contentShape" id="newModel_contentShape" cols="60" rows="10" cssClass="text"></wpsf:textarea>
	</p>
	<p>
		<label for="newModel_stylesheet"><s:text name="contentModel.label.stylesheet" />:</label><br />
		<wpsf:textfield name="stylesheet" id="newModel_stylesheet" cssClass="text" />
	</p>
	<p><wpsf:submit value="%{getText('label.save')}" cssClass="button" /></p>
</s:form>