<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="wpsa" uri="apsadmin-core.tld" %>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>

<s:if test="onEditContent">
<h1><s:text name="title.contentManagement" /></h1>
<h2><s:text name="title.contentEditing" /></h2>
<s:text name="contentDescription" />: <s:property value="content.descr" /> 
<!--
 	<s:if test="content.id == null"> NUOVO </s:if>
	<s:else> CON ID '<s:property value="content.id" />' </s:else>
 -->
<h3><s:text name="title.resourceManagement" />: <s:text name="title.resourceManagement.resourceNew" /></h3> 	
</s:if>

<s:if test="!onEditContent">
	<h1><a href="<s:url action="list" namespace="/do/jacms/Resource"><s:param name="resourceTypeCode"><s:property value="resourceTypeCode" /></s:param></s:url>" title="<s:text name="note.goToSomewhere" />: <s:text name="title.resourceManagement" />"><s:text name="title.resourceManagement" /></a></h1>
	<h2>
		<s:if test="getStrutsAction() == 1"><s:text name="title.resourceManagement.resourceNew" /></s:if>
		<s:else><s:text name="title.resourceManagement.resourceEdit" /></s:else>
		<%-- 
		<s:text name="resourceType.%{resourceTypeCode}"></s:text>
		--%>
	</h2>
</s:if>

<s:form action="save" method="post" enctype="multipart/form-data">
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
	<wpsf:hidden name="resourceTypeCode" />
<s:iterator value="categoryCodes" id="categoryCode" status="rowstatus">
<input type="hidden" name="categoryCodes" value="<s:property value="#categoryCode" />" id="categoryCodes-<s:property value="#rowstatus.index" />"/>
</s:iterator>
	<wpsf:hidden name="resourceId" />
</p>

<p>
	<label for="descr"><s:text name="label.description" />:</label><br />
	<wpsf:textfield name="descr" id="descr" cssClass="text" />
</p>

<s:set name="lockGroupSelect" value="%{resourceId != null && resourceId != 0}"></s:set>
<p>
	<label for="mainGroup"><s:text name="label.group" />:</label><br />
	<wpsf:select name="mainGroup" id="mainGroup" list="allowedGroups" value="mainGroup" 
		listKey="name" listValue="descr" disabled="%{lockGroupSelect}" cssClass="text" />
</p>

<s:if test="%{lockGroupSelect}">
<p class="noscreen">
	<wpsf:hidden name="mainGroup" />
</p>
</s:if>

<fieldset>
	<legend><s:text name="title.resourceCategoriesManagement"/></legend>
<ul id="categoryTree">
	<s:set name="inputFieldName" value="'categoryCode'" />
	<s:set name="selectedTreeNode" value="selectedNode" />
	<s:set name="liClassName" value="'category'" />
	<s:set name="currentRoot" value="categoryRoot" />
	<s:include value="/WEB-INF/apsadmin/jsp/common/treeBuilder.jsp" />
</ul>
<p>
	<wpsf:submit action="joinCategory" value="%{getText('label.join')}" cssClass="button" />
</p>

<table class="generic" summary="<s:text name="note.resourceCategories.summary"/>">
<caption><s:text name="title.resourceCategories.list"/></caption>
<tr>	
	<th><s:text name="label.category"/></th>
	<th class="icon"><abbr title="<s:text name="label.remove" />">&ndash;</abbr></th>
</tr>
<s:iterator value="categoryCodes" id="categoryCode">
<s:set name="resourceCategory" value="%{getCategory(#categoryCode)}"></s:set>
<tr>
	<td><s:property value="#resourceCategory.fullTitle"/></td>
	<td class="icon">
		<wpsa:actionParam action="removeCategory" var="actionName" >
			<wpsa:actionSubParam name="categoryCode" value="%{#resourceCategory.code}" />
		</wpsa:actionParam>
		<s:set name="iconImagePath" id="iconImagePath"><wp:resourceURL />administration/img/icons/delete.png</s:set>
		<wpsf:submit type="image" src="%{#iconImagePath}" action="%{#actionName}" value="%{getText('label.remove')}" title="%{getText('label.remove')}" />
	</td>
</tr>
</s:iterator>
</table>
</fieldset>

<s:if test="getStrutsAction() == 1">
<p>
	<label for="upload"><s:text name="label.file" />:</label><br />
	<s:file name="upload" id="upload" label="label.file"/>
</p>
</s:if>

<p>
	<wpsf:submit value="%{getText('label.save')}" cssClass="button" />
</p>

</s:form>



<s:if test="onEditContent">

<s:include value="/WEB-INF/plugins/jacms/apsadmin/jsp/content/modules/include/backToContentButton.jsp" />

</s:if>