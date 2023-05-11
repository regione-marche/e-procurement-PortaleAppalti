<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="wpsa" uri="apsadmin-core.tld" %>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>

<h1><a href="<s:url action="list" namespace="/do/jacms/Content" />" title="<s:text name="note.goToSomewhere" />: <s:text name="title.contentManagement" />"><s:text name="title.contentManagement" /></a></h1>

<h2><s:text name="title.contentEditing" /></h2>
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
<p><s:text name="note.workingOn" />: <em><s:property value="content.descr" /></em> (<s:property value="content.typeDescr" />)
<span class="noscreen"><s:text name="note.editContent" /></span> 
<%-- 
<s:if test="content.onLine">
<br /><s:text name="note.lastApprovedIntro" />&#32;<a href="<s:url action="viewLastApproved" namespace="/do/jacms/Content"><s:param name="contentId" value="content.id" /></s:url>"><s:text name="note.lastApproved" /></a>.
</s:if>
--%>
</p>

<%--
<h3><s:text name="note.flagsLegend" /></h3>
<dl class="table-display">
<dt><s:text name="Content.attribute.flag.mandatory.short" /></dt>
	<dd><s:text name="Content.attribute.flag.mandatory.full" /></dd>
<dt><s:text name="Content.attribute.flag.searcheable.short" /></dt>
	<dd><s:text name="Content.attribute.flag.searcheable.full" /></dd>
<dt><s:text name="Content.attribute.flag.indexed.short" /></dt>
	<dd><s:text name="Content.attribute.flag.indexed.full" /></dd>
<dt><s:text name="Content.attribute.flag.minLength.short" /></dt>
	<dd><s:text name="Content.attribute.flag.minLength.full" /></dd>
<dt><s:text name="Content.attribute.flag.maxLength.short" /></dt>
	<dd><s:text name="Content.attribute.flag.maxLength.full" /></dd>				
</dl>
--%>
<h3 class="noscreen" id="quickmenu"><s:text name="title.quickMenu" /></h3>
<ul class="menu horizontal tab-toggle-bar"><li><a href="#info" id="info_tab_quickmenu" class="tab-toggle"><abbr title="<s:text name="title.contentInfo" />">Info</abbr></a></li><s:iterator value="langs" id="lang"><li><a href="#<s:property value="#lang.code" />_tab" class="tab-toggle"><s:property value="#lang.descr" /></a></li></s:iterator></ul>

<s:set name="removeIcon" id="removeIcon"><wp:resourceURL/>administration/img/icons/list-remove.png</s:set>

<s:form cssClass="tab-container">
<div id="info" class="tab">
<h3 class="js_noscreen"><s:text name="title.contentInfo" /> (<a href="#quickmenu" id="info_content_goBackToQuickMenu"><s:text name="note.goBackToQuickMenu" /></a>)</h3>
<p>
	<label for="descr"><s:text name="label.description" />:</label><br />
	<wpsf:textfield id="descr" name="descr" value="%{content.descr}" maxlength="255" cssClass="text" />
</p> 
<p>
	<label for="mainGroup"><s:text name="label.ownerGroup" />:</label><br />
	<s:set name="lockGroupSelect" value="%{content.id != null || content.mainGroup != null}"></s:set>
	<wpsf:select name="mainGroup" id="mainGroup" list="allowedGroups" value="content.mainGroup" 
		listKey="name" listValue="descr" disabled="%{lockGroupSelect}" cssClass="text" />
</p>

<!--  INIZIO BLOCCO SELEZIONE GRUPPI SUPPLEMENTARI ABILITATI ALLA VISUALIZZAZIONE -->

<p class="important"><s:text name="label.extraGroups" /><span class="noscreen">:</span></p>
<s:if test="content.groups.size != 0">
<ul>
<s:iterator value="content.groups" id="groupName">
	<li>
		<wpsa:actionParam action="removeGroup" var="actionName" >
			<wpsa:actionSubParam name="extraGroupName" value="%{#groupName}" />
		</wpsa:actionParam>
		<wpsf:submit action="%{#actionName}" type="image" src="%{#removeIcon}" value="%{getText('label.remove')}" title="%{getText('label.remove')}" />: <s:property value="%{getGroupsMap()[#groupName].getDescr()}"/> 
	</li>
</s:iterator>
</ul>
</s:if>
<p>
	<label for="extraGroups"><s:text name="label.join" />&#32;<s:text name="label.group" />:</label><br />
	<wpsf:select name="extraGroupName" id="extraGroups" list="groups" 
		listKey="name" listValue="descr" cssClass="text" />
	<wpsf:submit action="joinGroup" value="%{getText('label.join')}" cssClass="button" />
</p>

<!-- FINE BLOCCO SELEZIONE GRUPPI SUPPLEMENTARI ABILITATI ALLA VISUALIZZAZIONE -->

<p>
<label for="status"><s:text name="label.state" />:</label><br />
<wpsf:select name="status" id="status" list="avalaibleStatus" value="%{content.status}" cssClass="text" listKey="key" listValue="%{getText(value)}" />
</p>

<!-- INIZIO CATEGORIE -->
<fieldset>
	<legend><s:text name="title.categoriesManagement"/></legend>

<ul id="categoryTree">
	<s:set name="inputFieldName" value="'categoryCode'" />
	<s:set name="selectedTreeNode" value="categoryCode" />
	<s:set name="liClassName" value="'category'" />
	<s:set name="currentRoot" value="categoryRoot" />
	<s:include value="/WEB-INF/apsadmin/jsp/common/treeBuilder.jsp" />
</ul>

<p><wpsf:submit action="joinCategory" value="%{getText('label.join')}" cssClass="button" /></p>

<table class="generic" summary="<s:text name="note.contentCategories.summary"/>: <s:property value="content.descr" />">
<caption><s:text name="title.contentCategories.list"/></caption>
<tr>	
	<th><s:text name="label.category"/></th>
	<th class="icon"><abbr title="<s:text name="label.remove" />">&ndash;</abbr></th>
</tr>
<s:iterator value="content.categories" id="contentCategory">
<tr>
	<td><s:property value="#contentCategory.defaultFullTitle"/></td>
	<td class="icon">
		<wpsa:actionParam action="removeCategory" var="actionName" >
			<wpsa:actionSubParam name="categoryCode" value="%{#contentCategory.code}" />
		</wpsa:actionParam>
		<s:set name="iconImagePath" id="iconImagePath"><wp:resourceURL />administration/img/icons/delete.png</s:set>
		<wpsf:submit type="image" src="%{#iconImagePath}" action="%{#actionName}" value="%{getText('label.remove')}" title="%{getText('label.remove') + ' ' + #contentCategory.defaultFullTitle}" />
	</td>
</tr>
</s:iterator>
</table>
</fieldset>
<!-- FINE CATEGORIE -->

</div>
<!-- START CICLO LINGUA -->
<s:iterator value="langs" id="lang">
<div id="<s:property value="#lang.code" />_tab" class="tab">
<h3 class="js_noscreen"><s:property value="#lang.descr" /> (<a class="backLink" href="#quickmenu" id="<s:property value="#lang.code" />_tab_quickmenu"><s:text name="note.goBackToQuickMenu" /></a>)</h3>

<!-- START CICLO ATTRIBUTI -->
<s:iterator value="content.attributeList" id="attribute">
<div class="contentAttributeBox">
<%-- INIZIALIZZAZIONE TRACCIATORE --%>
<wpsa:tracerFactory var="attributeTracer" lang="%{#lang.code}" />

<s:if test="#attribute.type == 'List' || #attribute.type == 'Monolist'">
<p class="important">
	<span class="monospace">(<s:text name="label.list" />)&#32;</span><s:property value="#attribute.name" /><s:include value="/WEB-INF/apsadmin/jsp/entity/modules/include/attributeInfo.jsp" />:
</p>
</s:if>
<s:elseif test="#attribute.type == 'Boolean' || #attribute.type == 'ThreeState'">
<p class="important">
	<s:property value="#attribute.name" /><s:include value="/WEB-INF/apsadmin/jsp/entity/modules/include/attributeInfo.jsp" />:
</p>
</s:elseif>
<s:elseif test="#attribute.type == 'CheckBox'">
<!-- Leave this completely blank -->
</s:elseif>
<s:else>
<p>
	<label for="<s:property value="%{#attributeTracer.getFormFieldName(#attribute)}" />"><s:property value="#attribute.name" /><s:include value="/WEB-INF/apsadmin/jsp/entity/modules/include/attributeInfo.jsp" />:</label><br />
</s:else>

<s:if test="#attribute.type == 'Monotext'">
<!-- ############# ATTRIBUTO TESTO MONOLINGUA ############# -->
<s:include value="/WEB-INF/apsadmin/jsp/entity/modules/monotextAttribute.jsp" />
</p>
</s:if>

<s:elseif test="#attribute.type == 'Text'">
<!-- ############# ATTRIBUTO TESTO SEMPLICE MULTILINGUA ############# -->
<s:include value="/WEB-INF/apsadmin/jsp/entity/modules/textAttribute.jsp" />
</p>
</s:elseif>

<s:elseif test="#attribute.type == 'Longtext'">
<!-- ############# ATTRIBUTO TESTOLUNGO ############# -->
<s:include value="/WEB-INF/apsadmin/jsp/entity/modules/longtextAttribute.jsp" />
</p>
</s:elseif>

<s:elseif test="#attribute.type == 'Hypertext'">
<!-- ############# ATTRIBUTO HYPERTEXT ############# -->
<s:include value="/WEB-INF/apsadmin/jsp/entity/modules/hypertextAttribute.jsp" />
</p>
</s:elseif>

<s:elseif test="#attribute.type == 'Image'">
<!-- ############# ATTRIBUTO Image ############# -->
<s:include value="/WEB-INF/plugins/jacms/apsadmin/jsp/content/modules/imageAttribute.jsp" />
</p>
</s:elseif>

<s:elseif test="#attribute.type == 'Attach'">
<!-- ############# ATTRIBUTO Attach ############# -->
<s:include value="/WEB-INF/plugins/jacms/apsadmin/jsp/content/modules/attachAttribute.jsp" />
</p>
</s:elseif>

<s:elseif test="#attribute.type == 'Boolean'">
<!-- ############# ATTRIBUTO Boolean ############# -->
<s:include value="/WEB-INF/apsadmin/jsp/entity/modules/booleanAttribute.jsp" />
</s:elseif>

<s:elseif test="#attribute.type == 'ThreeState'">
<!-- ############# ATTRIBUTO ThreeState ############# -->
<s:include value="/WEB-INF/apsadmin/jsp/entity/modules/threeStateAttribute.jsp" />
</s:elseif>

<s:elseif test="#attribute.type == 'Number'">
<!-- ############# ATTRIBUTO Number ############# -->
<s:include value="/WEB-INF/apsadmin/jsp/entity/modules/numberAttribute.jsp" />
</p>
</s:elseif>

<s:elseif test="#attribute.type == 'Date'">
<!-- ############# ATTRIBUTO Date ############# -->
<s:include value="/WEB-INF/apsadmin/jsp/entity/modules/dateAttribute.jsp" />
</p>
</s:elseif>

<s:elseif test="#attribute.type == 'Link'">
<!-- ############# ATTRIBUTO Link ############# -->
<s:include value="/WEB-INF/plugins/jacms/apsadmin/jsp/content/modules/linkAttribute.jsp" />
</p>
</s:elseif>

<s:elseif test="#attribute.type == 'Enumerator'">
<!-- ############# ATTRIBUTO TESTO Enumerator ############# -->
<s:include value="/WEB-INF/apsadmin/jsp/entity/modules/enumeratorAttribute.jsp" />
</p>
</s:elseif>

<s:elseif test="#attribute.type == 'CheckBox'">
<!-- ############# ATTRIBUTO CheckBox ############# -->
<p><s:include value="/WEB-INF/apsadmin/jsp/entity/modules/checkBoxAttribute.jsp" /></p>
</s:elseif>

<s:elseif test="#attribute.type == 'Monolist'">
<!-- ############# ATTRIBUTO Monolist ############# -->
<s:include value="/WEB-INF/plugins/jacms/apsadmin/jsp/content/modules/monolistAttribute.jsp" />
</s:elseif>

<s:elseif test="#attribute.type == 'List'">
<!-- ############# ATTRIBUTO List ############# -->
<s:include value="/WEB-INF/apsadmin/jsp/entity/modules/listAttribute.jsp" />
</s:elseif>

<s:elseif test="#attribute.type == 'Composite'">
<!-- ############# ATTRIBUTO Composite ############# -->
<s:include value="/WEB-INF/plugins/jacms/apsadmin/jsp/content/modules/compositeAttribute.jsp" />
</p>
</s:elseif>


</div>
</s:iterator>
<!-- END CICLO ATTRIBUTI -->
</div>
</s:iterator>
<!-- END CICLO LINGUA -->

<h3 class="noscreen"><s:text name="title.contentActionsIntro" /></h3>

<fieldset><legend><s:text name="title.contentPreview" /></legend>
<s:set var="showingPageSelectItems" value="showingPageSelectItems"></s:set>
<s:if test="!#showingPageSelectItems.isEmpty()">
	<div class="buttons">
		<p><label for="previewPageCode"><s:text name="name.preview.page" /></label>: 
		<wpsf:select name="previewPageCode" id="previewPageCode" list="#showingPageSelectItems" listKey="key" listValue="value" /></p>
		<s:set name="iconImagePath" id="iconImagePath"><wp:resourceURL/>administration/img/icons/32x32/content-preview.png</s:set>	
		<p><wpsf:submit action="preview" type="image" src="%{#iconImagePath}" value="%{getText('label.preview')}" title="%{getText('note.button.previewContent')}" /></p>
	</div>
</s:if>
<s:else>
	<p><s:text name="label.preview.noPreviewPages" /></p>
</s:else>
</fieldset>

<fieldset><legend><s:text name="title.contentActions" /></legend>
<p class="buttons">

	<s:set name="iconImagePath" id="iconImagePath"><wp:resourceURL/>administration/img/icons/32x32/save.png</s:set>
	<wpsf:submit action="save" type="image" src="%{#iconImagePath}" value="%{getText('label.save')}" title="%{getText('note.button.saveContent')}" />
<wp:ifauthorized permission="validateContents">
	<s:set name="iconImagePath" id="iconImagePath"><wp:resourceURL/>administration/img/icons/32x32/approve.png</s:set>
	<wpsf:submit action="saveAndApprove" type="image" src="%{#iconImagePath}" value="%{getText('label.saveAndApprove')}" title="%{getText('note.button.saveAndApprove')}" />
	<s:if test="content.onLine">
		<s:set name="iconImagePath" id="iconImagePath"><wp:resourceURL/>administration/img/icons/32x32/suspend.png</s:set>
		<wpsf:submit action="suspend" type="image" src="%{#iconImagePath}" value="%{getText('label.suspend')}" title="%{getText('note.button.suspend')}" />
	</s:if>
</wp:ifauthorized>	
	
</p>
</fieldset>
</s:form>