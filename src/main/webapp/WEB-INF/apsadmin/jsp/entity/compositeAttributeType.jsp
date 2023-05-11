<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>

<h1><a href="<s:url action="viewManagers" namespace="/do/Entity" />" title="<s:text name="note.goToSomewhere" />: <s:text name="title.entityManagement" />"><s:text name="title.entityManagement" /></a></h1>
<h2><a href="<s:url action="initViewEntityTypes" namespace="/do/Entity"><s:param name="entityManagerName"><s:property value="entityManagerName" /></s:param></s:url>" title="<s:text name="note.goToSomewhere" />: <s:text name="title.entityAdmin.manager" />&#32;<s:property value="entityManagerName" />"><s:text name="title.entityAdmin.manager" />: <s:property value="entityManagerName" /></a></h2>
<h3><a href="<s:url action="initEditEntityType" namespace="/do/Entity"><s:param name="entityManagerName"><s:property value="entityManagerName" /></s:param><s:param name="entityTypeCode"><s:property value="entityType.typeCode" /></s:param></s:url>" title="<s:text name="note.goToSomewhere" />: <s:text name="title.entityTypes.editType.edit" />"><s:text name="title.entityTypes.editType.edit" />: <span class="monospace"><s:property value="entityType.typeCode" /> - <s:property value="entityType.typeDescr" /></span></a></h3>

<s:form action="saveCompositeAttribute">

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

<s:set name="compositeAttribute" value="compositeAttributeOnEdit" />
<p><span class="important"><s:text name="label.type" />:</span> <span class="monospace"><s:property value="#compositeAttribute.type" /></span></p>

<fieldset><legend><s:text name="label.info" /></legend>

	<p>
		<span class="important"><s:text name="label.code" />:</span> <span class="monospace"><s:property value="#compositeAttribute.name" /></span>
	</p>

</fieldset>

<fieldset><legend><s:text name="label.settings" /></legend>

	<p>
		<label for="attributeTypeCode"><s:text name="label.type" />:</label><br />
		<wpsf:select list="allowedAttributeElementTypes" id="attributeTypeCode" name="attributeTypeCode" listKey="type" listValue="type"></wpsf:select>
		<wpsf:submit value="%{getText('label.add')}" action="addAttributeElement" cssClass="button" />
	</p>

</fieldset>

<fieldset><legend><s:text name="label.attributes" /></legend>

	<table class="generic" summary="<s:text name="note.entityAdmin.compositeAttribute.list.summary" />" id="fagiano_compositeTypesList">
	<caption><s:text name="label.attributes" /></caption>
	<tr>
		<th><s:text name="label.code" /></th>
		<th><s:text name="label.type" /></th>
		<th class="icon"><abbr title="<s:text name="Entity.attribute.flag.mandatory.full" />"><s:text name="Entity.attribute.flag.mandatory.short" /></abbr></th>
		<th class="icon" colspan="3"><abbr title="<s:text name="label.actions" />">&ndash;</abbr></th>
	</tr>

<s:iterator value="#compositeAttribute.attributes" var="attribute" status="elementStatus">
	<tr>
		<td class="monospace"><s:property value="#attribute.name" /></td>
 		<td class="monospace"><s:property value="#attribute.type" /></td>
 		
		<s:if test="#attribute.required">
			<s:set var="tmpStatus">yes</s:set>
		</s:if>
		<s:else>
			<s:set var="tmpStatus">no</s:set>
		</s:else>
		<td class="icon"><img src="<wp:resourceURL />administration/img/icons/<s:property value="#attribute.required" />.png" alt="<s:text name="label.%{#tmpStatus}" />" title="<s:text name="label.%{#tmpStatus}" />" /></td>
 		
		<s:set name="elementIndex" value="#elementStatus.index" />
		<s:include value="/WEB-INF/apsadmin/jsp/entity/include/compositeAttributeElementOperationModule.jsp" />
	</tr>
</s:iterator>

	</table>

</fieldset>

<p>
	<wpsf:submit value="%{getText('label.continue')}" action="saveCompositeAttribute" cssClass="button" />
</p>

</s:form>