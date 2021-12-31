<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>
<%@ taglib prefix="wpsa" uri="apsadmin-core.tld" %>
<wpsa:actionParam action="moveListElement" var="actionName" >
	<wpsa:actionSubParam name="attributeName" value="%{#attribute.name}" />
	<wpsa:actionSubParam name="listLangCode" value="%{#lang.code}" />
	<wpsa:actionSubParam name="elementIndex" value="%{#elementIndex}" />
	<wpsa:actionSubParam name="movement" value="UP" />
</wpsa:actionParam>
<s:set name="iconImagePath" id="iconImagePath"><wp:resourceURL/>administration/img/icons/go-up.png</s:set>
<wpsf:submit action="%{#actionName}" type="image" src="%{#iconImagePath}" value="%{getText('label.moveUp')}" title="%{getText('label.moveInPositionNumber')}: %{#elementIndex}" />

<wpsa:actionParam action="moveListElement" var="actionName" >
	<wpsa:actionSubParam name="attributeName" value="%{#attribute.name}" />
	<wpsa:actionSubParam name="listLangCode" value="%{#lang.code}" />
	<wpsa:actionSubParam name="elementIndex" value="%{#elementIndex}" />
	<wpsa:actionSubParam name="movement" value="DOWN" />
</wpsa:actionParam>
<s:set name="iconImagePath" id="iconImagePath"><wp:resourceURL/>administration/img/icons/go-down.png</s:set>
<wpsf:submit action="%{#actionName}" type="image" src="%{#iconImagePath}" value="%{getText('label.moveDown')}" title="%{getText('label.moveInPositionNumber')}: %{#elementIndex+2}" />

<wpsa:actionParam action="removeListElement" var="actionName" >
	<wpsa:actionSubParam name="attributeName" value="%{#attribute.name}" />
	<wpsa:actionSubParam name="listLangCode" value="%{#lang.code}" />
	<wpsa:actionSubParam name="elementIndex" value="%{#elementIndex}" />
</wpsa:actionParam>
<s:set name="iconImagePath" id="iconImagePath"><wp:resourceURL/>administration/img/icons/list-remove.png</s:set>
<wpsf:submit action="%{#actionName}" type="image" src="%{#iconImagePath}" value="%{getText('label.remove')}" title="%{getText('label.remove')}" />