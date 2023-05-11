<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>
<%@ taglib prefix="wpsa" uri="apsadmin-core.tld" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>

<table class="generic" summary="<s:text name="note.entityTypes.list.summary" />" id="fagiano_entityTypesList">
<caption><s:text name="label.attributes" /></caption>
<tr>
	<th class="icon"><abbr title="<s:text name="label.edit" />">M</abbr></th>
	<th><s:text name="label.code" /></th>
	<th><s:text name="label.type" /></th>
	<th class="icon"><abbr title="<s:text name="Entity.attribute.flag.mandatory.full" />"><s:text name="Entity.attribute.flag.mandatory.short" /></abbr></th>
	<th class="icon"><abbr title="<s:text name="Entity.attribute.flag.searcheable.full" />"><s:text name="Entity.attribute.flag.searcheable.short" /></abbr></th>
	<th class="icon" colspan="3"><abbr title="<s:text name="label.actions" />">&ndash;</abbr></th>
</tr>

<s:iterator value="#entityType.attributeList" var="attribute" status="elementStatus">
<tr>
	<td class="icon">
		<s:set name="iconImagePath" id="iconImagePath"><wp:resourceURL />administration/img/icons/edit-content.png</s:set>		
		<wpsa:actionParam action="editAttribute" var="actionName" >
			<wpsa:actionSubParam name="attributeName" value="%{#attribute.name}" />
		</wpsa:actionParam>
		<wpsf:submit action="%{#actionName}" type="image" src="%{#iconImagePath}" value="%{getText('label.edit')}" title="%{getText('label.edit')}: %{#attribute.name}" />
	</td>
	<td class="monospace"><s:property value="#attribute.name" /></td>
	<td class="monospace">
	<s:property value="#attribute.type" />
	<s:if test="#attribute.type == 'Monolist' || #attribute.type == 'List'">: <s:property value="#attribute.nestedAttributeTypeCode" /></s:if>
	</td>
	
	<s:if test="#attribute.required">
		<s:set var="tmpStatus">yes</s:set>
	</s:if>
	<s:else>
		<s:set var="tmpStatus">no</s:set>
	</s:else>
	<td class="icon"><img src="<wp:resourceURL />administration/img/icons/<s:property value="#attribute.required" />.png" alt="<s:text name="label.%{#tmpStatus}" />" title="<s:text name="label.%{#tmpStatus}" />" /></td>

	<s:if test="#attribute.searcheable">
		<s:set var="tmpStatus">yes</s:set>
	</s:if>
	<s:else>
		<s:set var="tmpStatus">no</s:set>
	</s:else>
	<td class="icon"><img src="<wp:resourceURL />administration/img/icons/<s:property value="#attribute.searcheable" />.png" alt="<s:text name="label.%{#tmpStatus}" />" title="<s:text name="label.%{#tmpStatus}" />" /></td>	

	<s:set name="elementIndex" value="#elementStatus.index" />
	<s:include value="/WEB-INF/apsadmin/jsp/entity/include/attributeOperationModule.jsp" />
</tr>
</s:iterator>

</table>