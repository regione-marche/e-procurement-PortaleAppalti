<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>
<h1><s:text name="title.pageManagement" /></h1>

<p><s:text name="note.pageTree.intro" /></p>

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

<%-- Inclusione del form di ricerca per codice --%>
<s:include value="/WEB-INF/apsadmin/jsp/portal/include/pageSearchForm.jsp" />

<s:form>

<fieldset><legend><s:text name="title.pageTree" /></legend>
<!-- <s:property value="selectedNode" /> -->

<ul id="pageTree">
	<s:set name="inputFieldName" value="'selectedNode'" />
	<s:set name="selectedTreeNode" value="selectedNode" />
	<s:set name="liClassName" value="'page'" />
	<s:set name="currentRoot" value="treeRootNode" />
	<s:include value="/WEB-INF/apsadmin/jsp/common/treeBuilder.jsp" />
</ul>

</fieldset>

<p class="noscreen"><wpsf:hidden name="copyingPageCode" /></p>

<fieldset><legend><s:text name="title.pageActions" /></legend>
<p class="noscreen"><s:text name="title.pageActionsIntro" /></p>

<p class="buttons">
	<s:set name="iconImagePath" id="iconImagePath"><wp:resourceURL/>administration/img/icons/32x32/page-new.png</s:set>
	<wpsf:submit action="new" type="image" src="%{#iconImagePath}" value="%{getText('page.options.new')}" title="%{getText('page.options.new')}" />
	<s:set name="iconImagePath" id="iconImagePath"><wp:resourceURL/>administration/img/icons/32x32/page-edit.png</s:set>
	<wpsf:submit action="edit" type="image" src="%{#iconImagePath}" value="%{getText('page.options.modify')}" title="%{getText('page.options.modify')}" />
	<s:set name="iconImagePath" id="iconImagePath"><wp:resourceURL/>administration/img/icons/32x32/detail.png</s:set>
	<wpsf:submit action="detail" type="image" src="%{#iconImagePath}" value="%{getText('page.options.detail')}" title="%{getText('page.options.detail')}" />
	<s:set name="iconImagePath" id="iconImagePath"><wp:resourceURL/>administration/img/icons/32x32/delete.png</s:set>	
	<wpsf:submit action="trash" type="image" src="%{#iconImagePath}" value="%{getText('page.options.delete')}" title="%{getText('page.options.delete')}" />
	<s:set name="iconImagePath" id="iconImagePath"><wp:resourceURL/>administration/img/icons/32x32/go-up.png</s:set>
	<wpsf:submit action="moveUp" type="image" src="%{#iconImagePath}" value="%{getText('page.options.moveUp')}" title="%{getText('page.options.moveUp')}" />
	<s:set name="iconImagePath" id="iconImagePath"><wp:resourceURL/>administration/img/icons/32x32/go-down.png</s:set>
	<wpsf:submit action="moveDown" type="image" src="%{#iconImagePath}" value="%{getText('page.options.moveDown')}" title="%{getText('page.options.moveDown')}" />
	<s:set name="iconImagePath" id="iconImagePath"><wp:resourceURL/>administration/img/icons/32x32/edit-copy.png</s:set>
	<wpsf:submit action="copy" type="image" src="%{#iconImagePath}" value="%{getText('page.options.copy')}" title="%{getText('page.options.copy')}" />
	<s:set name="iconImagePath" id="iconImagePath"><wp:resourceURL/>administration/img/icons/32x32/edit-paste.png</s:set>
	<wpsf:submit action="paste" type="image" src="%{#iconImagePath}" value="%{getText('page.options.paste')}" title="%{getText('page.options.paste')}" />
	<s:set name="iconImagePath" id="iconImagePath"><wp:resourceURL/>administration/img/icons/32x32/page-configure.png</s:set>
	<wpsf:submit action="configure" type="image" src="%{#iconImagePath}" value="%{getText('page.options.configure')}" title="%{getText('page.options.configure')}" />
</p>
</fieldset>
</s:form>