<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>
<%@ taglib prefix="wpsa" uri="apsadmin-core.tld" %>

<td class="icon">
	<wpsa:actionParam action="moveAttributeElement" var="actionName">
		<wpsa:actionSubParam name="attributeIndex" value="%{#elementIndex}" />
		<wpsa:actionSubParam name="movement" value="UP" />
	</wpsa:actionParam>
	<s:set name="iconImagePath" id="iconImagePath"><wp:resourceURL/>administration/img/icons/go-up.png</s:set>
	<wpsf:submit action="%{#actionName}" type="image" src="%{#iconImagePath}" value="%{getText('label.moveUp')}" title="%{getText('label.moveInPositionNumber')}: %{#elementIndex}" />
</td>

<td class="icon">
	<wpsa:actionParam action="moveAttributeElement" var="actionName" >
		<wpsa:actionSubParam name="attributeIndex" value="%{#elementIndex}" />
		<wpsa:actionSubParam name="movement" value="DOWN" />
	</wpsa:actionParam>
	<s:set name="iconImagePath" id="iconImagePath"><wp:resourceURL/>administration/img/icons/go-down.png</s:set>
	<wpsf:submit action="%{#actionName}" type="image" src="%{#iconImagePath}" value="%{getText('label.moveDown')}" title="%{getText('label.moveInPositionNumber')}: %{#elementIndex+2}" />
</td>

<td class="icon">
	<wpsa:actionParam action="removeAttributeElement" var="actionName" >
		<wpsa:actionSubParam name="attributeIndex" value="%{#elementIndex}" />
	</wpsa:actionParam>
	<s:set name="iconImagePath" id="iconImagePath"><wp:resourceURL/>administration/img/icons/list-remove.png</s:set>
	<wpsf:submit action="%{#actionName}" type="image" src="%{#iconImagePath}" value="%{getText('label.remove')}" title="%{getText('label.remove')}" />
</td>