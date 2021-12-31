<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wpsa" uri="apsadmin-core.tld" %>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>

<h1><a href="<s:url action="viewTree" namespace="/do/Category" />" title="<s:text name="note.goToSomewhere" />: <s:text name="title.categoryManagement" />"><s:text name="title.categoryManagement" /></a></h1>
<s:if test="getStrutsAction() == 1">
	<h2><s:text name="title.newCategory" /></h2>
</s:if>
<s:elseif test="getStrutsAction() == 2">
	<h2><s:text name="title.editCategory" /></h2>
</s:elseif>

<s:form action="save">
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
	<wpsf:hidden name="strutsAction" />
</p>
<s:if test="getStrutsAction() == 2"><wpsf:hidden name="categoryCode" /></s:if>
<p class="noscreen">
	<wpsf:hidden name="parentCategoryCode" />
</p>
<p>
	<label for="categoryCode"><s:text name="name.categoryCode" />:</label><br />
<s:if test="getStrutsAction() != 2">
	<wpsf:textfield name="categoryCode" id="categoryCode" cssClass="text" />
	<wpsf:hidden name="selectedNode" value="%{parentCategoryCode}" />
</s:if>
<s:elseif test="getStrutsAction() == 2">
	<wpsf:textfield name="categoryCode" id="categoryCode" disabled="true" cssClass="text" />
	<wpsf:hidden name="selectedNode" value="%{categoryCode}" />
</s:elseif>
</p>

<s:iterator value="langs">
<p>
	<label for="lang<s:property value="code" />"><span class="monospace">(<s:property value="code" />)</span> <s:text name="name.categoryTitle" />:</label><br />
	<wpsf:textfield name="%{'lang'+code}" id="%{'lang'+code}" value="%{titles.get(code)}" cssClass="text" />
</p>
</s:iterator>

<p><wpsf:submit value="%{getText('label.save')}" cssClass="button" /></p>

</s:form>