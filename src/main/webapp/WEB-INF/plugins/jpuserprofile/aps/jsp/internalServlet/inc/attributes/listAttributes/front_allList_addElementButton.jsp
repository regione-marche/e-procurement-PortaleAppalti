<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>
<%@ taglib prefix="wpsa" uri="apsadmin-core.tld" %>

<s:set var="add_label"><wp:i18n key="jpuserprofile_ADDITEM_LIST" /></s:set>

<wpsa:actionParam action="addListElement" var="actionName" >
	<wpsa:actionSubParam name="attributeName" value="%{#attribute.name}" />
	<wpsa:actionSubParam name="listLangCode" value="%{#lang.code}" />
</wpsa:actionParam>
<s:set name="iconImagePath" id="iconImagePath"><wp:resourceURL/>administration/img/icons/list-add.png</s:set> 
<wpsf:submit action="%{#actionName}" type="image" src="%{#iconImagePath}" value="%{add_label}" title="%{i18n_attribute_name}%{': '}%{add_label}" />