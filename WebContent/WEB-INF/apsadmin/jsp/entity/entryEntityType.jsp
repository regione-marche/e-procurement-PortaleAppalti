<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wpsa" uri="apsadmin-core.tld" %>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>

<h1><a href="<s:url action="viewManagers" namespace="/do/Entity" />" title="<s:text name="note.goToSomewhere" />: <s:text name="title.entityManagement" />"><s:text name="title.entityManagement" /></a></h1>
<h2><a href="<s:url action="initViewEntityTypes" namespace="/do/Entity"><s:param name="entityManagerName"><s:property value="entityManagerName" /></s:param></s:url>" title="<s:text name="note.goToSomewhere" />: <s:text name="title.entityAdmin.manager" />&#32;<s:property value="entityManagerName" />"><s:text name="title.entityAdmin.manager" />: <s:property value="entityManagerName" /></a></h2>

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

<s:if test="operationId == 1">
<h3><s:text name="title.entityTypes.editType.new" /></h3>
</s:if>
<s:else>
<h3><s:text name="title.entityTypes.editType.edit" /></h3>
</s:else>

<p><s:text name="note.entityTypes.editType.intro.1" /></p>
<p><s:text name="note.entityTypes.editType.intro.2" /></p>

<s:form action="saveEntityType">

<s:set var="entityType" value="entityType" />
<s:if test="operationId != 1">
<p class="noscreen">	
	<wpsf:hidden name="entityTypeCode" value="%{#entityType.typeCode}" />
</p>
</s:if>

<fieldset><legend><s:text name="label.info" /></legend>
	<p>
	<s:if test="operationId == 1">
		<label for="entityTypeCode"><s:text name="label.code" />:</label><br />
		<wpsf:textfield name="entityTypeCode" id="entityTypeCode" value="%{#entityType.typeCode}" cssClass="text" />
	</s:if>
	<s:else>
		<span class="important"><s:text name="label.code" />:</span> <s:property value="#entityType.typeCode" />
	</s:else>
	</p>
	
	<p>
		<label for="entityTypeDescription"><s:text name="label.description" />:</label><br />
		<wpsf:textfield name="entityTypeDescription" id="entityTypeDescription" value="%{#entityType.typeDescr}" cssClass="text" />
	</p>
</fieldset>

<fieldset><legend><s:text name="label.attributes" /></legend>
	<s:include value="/WEB-INF/apsadmin/jsp/entity/include/addNewAttributeOperationModule.jsp" />
	<s:include value="/WEB-INF/apsadmin/jsp/entity/include/entityTypeAttributes.jsp" />
</fieldset>

<p class="buttons">
	<wpsf:submit value="%{getText('label.save')}" action="saveEntityType" cssClass="button" />
</p>

</s:form>