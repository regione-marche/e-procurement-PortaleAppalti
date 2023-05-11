<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>
<h1><s:text name="title.categoryManagement" /></h1>

<s:form>

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

<fieldset><legend><s:text name="title.categoryTree" /></legend>
<!--  ###<s:property value="selectedNode" />###  -->
<ul id="categoryTree">
	<s:set name="inputFieldName" value="'selectedNode'" />
	<s:set name="selectedTreeNode" value="selectedNode" />
	<s:set name="liClassName" value="'category'" />
	<s:set name="currentRoot" value="treeRootNode" />
	<s:include value="/WEB-INF/apsadmin/jsp/common/treeBuilder.jsp" />
</ul>
</fieldset>
<fieldset><legend><s:text name="title.categoryActions" /></legend>
<p class="noscreen"><s:text name="title.categoryActionsIntro" /></p>
<p class="buttons">

	<s:set id="iconImagePath" name="iconImagePath"><wp:resourceURL/>administration/img/icons/32x32/page-new.png</s:set>
	<wpsf:submit action="new" type="image" src="%{#iconImagePath}" value="%{getText('category.options.new')}" title="%{getText('category.options.new')}" />
	<s:set id="iconImagePath" name="iconImagePath"><wp:resourceURL/>administration/img/icons/32x32/page-edit.png</s:set>
	<wpsf:submit action="edit" type="image" src="%{#iconImagePath}" value="%{getText('category.options.modify')}" title="%{getText('category.options.modify')}" />
	<s:set id="iconImagePath" name="iconImagePath"><wp:resourceURL/>administration/img/icons/32x32/delete.png</s:set>
	<wpsf:submit action="trash" type="image" src="%{#iconImagePath}" value="%{getText('category.options.delete')}" title="%{getText('category.options.delete')}" />
</p>
</fieldset>
</s:form>