<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>

<h1><a href="<s:url action="viewManagers" namespace="/do/Entity" />" title="<s:text name="note.goToSomewhere" />: <s:text name="title.entityManagement" />"><s:text name="title.entityManagement" /></a></h1>
<h2><a href="<s:url action="initViewEntityTypes" namespace="/do/Entity"><s:param name="entityManagerName"><s:property value="entityManagerName" /></s:param></s:url>" title="<s:text name="note.goToSomewhere" />: <s:text name="title.entityAdmin.manager" />&#32;<s:property value="entityManagerName" />"><s:text name="title.entityAdmin.manager" />: <s:property value="entityManagerName" /></a></h2>
<h3><a href="<s:url action="initEditEntityType" namespace="/do/Entity"><s:param name="entityManagerName"><s:property value="entityManagerName" /></s:param><s:param name="entityTypeCode"><s:property value="entityType.typeCode" /></s:param></s:url>" title="<s:text name="note.goToSomewhere" />: <s:text name="title.entityTypes.editType.edit" />"><s:text name="title.entityTypes.editType.edit" />: <span class="monospace"><s:property value="entityType.typeCode" /> - <s:property value="entityType.typeDescr" /></span></a></h3>

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

<s:form action="saveAttribute">

<p class="noscreen">
	<wpsf:hidden name="attributeTypeCode" />
	<wpsf:hidden name="strutsAction" />
</p>

<s:if test="strutsAction == 2">
<p class="noscreen">
	<wpsf:hidden name="attributeName" />
</p>
</s:if>

<s:if test="strutsAction == 1">
	<s:set name="attribute" value="getAttributePrototype(attributeTypeCode)" />
</s:if>
<s:else>
	<s:set name="attribute" value="entityType.getAttribute(attributeName)" />
</s:else>

<p><span class="important"><s:text name="label.type" />:</span> <span class="monospace"><s:property value="attributeTypeCode" /></span></p>

<fieldset><legend><s:text name="label.info" /></legend>
	<p>
	<s:if test="strutsAction == 1">
		<label for="attributeName"><s:text name="label.code" />:</label><br />
		<wpsf:textfield cssClass="text" name="attributeName" id="attributeName" />
	</s:if>
	<s:else>
		<span class="important"><s:text name="label.code" />:</span> <span class="monospace"><s:property value="attributeName" /></span>
	</s:else>	
	</p>

	<p>
		<wpsf:checkbox name="required" id="required" />&#32;<label for="required"><s:text name="Entity.attribute.flag.mandatory.full" /></label>
	</p>

<s:if test="!(#attribute.type == 'List') && !(#attribute.type == 'Monolist')">
	
	<s:if test="isEntityManagerSearchEngineUser() && isIndexableOptionSupported(attributeTypeCode)">
	<p>
		<wpsf:checkbox name="indexable" id="indexable" />&#32;<label for="indexable"><s:text name="Entity.attribute.flag.indexed.full" /></label>
	</p>
	</s:if>
	
	<s:if test="isSearchableOptionSupported(attributeTypeCode)">
	<p>
		<wpsf:checkbox name="searcheable" id="searcheable" />&#32;<label for="searcheable"><s:text name="Entity.attribute.flag.searcheable.full" /></label>
	</p>
	</s:if>
</s:if>

</fieldset>

<s:if test="#attribute.type == 'Enumerator'">
<fieldset><legend><s:text name="label.settings" /></legend>
	<p>
		<label for="enumeratorStaticItems"><s:text name="Entity.attribute.setting.enumerator.items" />:</label><br />
		<wpsf:textfield name="enumeratorStaticItems" id="enumeratorStaticItems" cssClass="text" />
	</p>
	
	<p>
		<label for="enumeratorStaticItemsSeparator"><s:text name="Entity.attribute.setting.enumerator.separator" />:</label><br />
		<wpsf:textfield name="enumeratorStaticItemsSeparator" id="enumeratorStaticItemsSeparator" cssClass="text" />
	</p>
</fieldset>
</s:if>

<s:elseif test="#attribute.textAttribute">
<fieldset><legend><s:text name="label.settings" /></legend>
	<p>
		<label for="minLength"><s:text name="Entity.attribute.flag.minLength.full" />:</label><br />
		<wpsf:textfield name="minLength" id="minLength" cssClass="text"/>
	</p>

	<p>
		<label for="maxLength"><s:text name="Entity.attribute.flag.maxLength.full" />:</label><br />	
		<wpsf:textfield name="maxLength" id="maxLength" cssClass="text" />
	</p>
	
	<p>
		<label for="regexp"><s:text name="Entity.attribute.setting.regexp.full" />:</label><br />	
		<wpsf:textfield name="regexp" id="regexp" cssClass="text" />
	</p>

</fieldset>
</s:elseif>

<s:if test="#attribute.type == 'List' || #attribute.type == 'Monolist'">
<fieldset><legend><s:text name="label.settings" /></legend>
<p>
	<label for="listNestedType"><s:text name="Entity.attribute.setting.listType" />:</label><br />	
	<wpsf:select list="getAllowedNestedTypes(#attribute)" name="listNestedType" id="listNestedType" listKey="type" listValue="type" />
</p>
</fieldset>
</s:if>

<p>
	<wpsf:submit value="%{getText('label.continue')}" cssClass="button" />
</p>

</s:form>