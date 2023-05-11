<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>
<%@ taglib prefix="wpsa" uri="apsadmin-core.tld" %>

<h1><a href="<s:url action="viewTree" namespace="/do/Page" />" title="<s:text name="note.goToSomewhere" />: <s:text name="title.pageManagement" />"><s:text name="title.pageManagement" /></a></h1>

<p><s:text name="note.pageTree.intro" /></p>

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

<s:include value="/WEB-INF/apsadmin/jsp/portal/include/pageSearchForm.jsp" />

<s:form action="search" >

<p class="noscreen">
	<wpsf:hidden name="pageCodeToken" />
</p>

<s:set var="pagesFound" value="pagesFound"></s:set>

<s:if test="%{#pagesFound != null && #pagesFound.isEmpty() == false}">
	
	<wpsa:subset source="#pagesFound" count="10" objectName="groupPage" advanced="true" offset="5">
	<s:set name="group" value="#groupPage" />	
	<div class="pager">
		<s:include value="/WEB-INF/apsadmin/jsp/common/inc/pagerInfo.jsp" />
		<s:include value="/WEB-INF/apsadmin/jsp/common/inc/pager_formBlock.jsp" />
	</div>
	
	<ul>
	<s:iterator var="singlePage">

		<s:set name="pageFullPath">
			<%-- INIZIO BLOCCO BRICIOLE DI PANE --%>
			<s:set value="%{getBreadCrumbsTargets(#singlePage.code)}" name="breadCrumbsTargets" ></s:set>
			<s:iterator value="#breadCrumbsTargets" id="target" status="rowstatus">
				<s:if test="%{#rowstatus.index != 0}"> | </s:if>
				<s:property value="#target.titles[currentLang.code]" />
			</s:iterator>
			<%-- FINE BLOCCO BRICIOLE DI PANE --%>
		</s:set>

		<li>
			<wpsf:radio name="selectedNode" id="page_%{#singlePage.code}" value="%{#singlePage.code}" />&#32;<label for="page_<s:property value="%{#singlePage.code}" />" title="<s:property value="#pageFullPath" />"><s:property value="%{#singlePage.code}" /></label>
		</li>	
		<%-- <s:property value="%{#singlePage.titles[currentLang.code]}" /> --%> 
	</s:iterator>
	</ul>
	
	<div class="pager">
		<s:include value="/WEB-INF/apsadmin/jsp/common/inc/pager_formBlock.jsp" />
	</div>
	</wpsa:subset>

<p class="noscreen">	
	<wpsf:hidden name="copyingPageCode" />
</p>

<fieldset><legend><s:text name="title.pageActions" /></legend>
<p class="noscreen"><s:text name="title.pageActionsIntro" /></p>

<p class="buttons">

	<s:set name="iconImagePath" id="iconImagePath"><wp:resourceURL/>administration/img/icons/32x32/page-edit.png</s:set>
	<wpsf:submit action="edit" type="image" src="%{#iconImagePath}" value="%{getText('page.options.modify')}" title="%{getText('page.options.modify')}" />

	<s:set name="iconImagePath" id="iconImagePath"><wp:resourceURL/>administration/img/icons/32x32/detail.png</s:set>
	<wpsf:submit action="detail" type="image" src="%{#iconImagePath}" value="%{getText('page.options.detail')}" title="%{getText('page.options.detail')}" />

	<s:set name="iconImagePath" id="iconImagePath"><wp:resourceURL/>administration/img/icons/32x32/delete.png</s:set>	
	<wpsf:submit action="trash" type="image" src="%{#iconImagePath}" value="%{getText('page.options.delete')}" title="%{getText('page.options.delete')}" />

	<s:set name="iconImagePath" id="iconImagePath"><wp:resourceURL/>administration/img/icons/32x32/edit-copy.png</s:set>
	<wpsf:submit action="copy" type="image" src="%{#iconImagePath}" value="%{getText('page.options.copy')}" title="%{getText('page.options.copy')}" />

	<s:set name="iconImagePath" id="iconImagePath"><wp:resourceURL/>administration/img/icons/32x32/page-configure.png</s:set>
	<wpsf:submit action="configure" type="image" src="%{#iconImagePath}" value="%{getText('page.options.configure')}" title="%{getText('page.options.configure')}" />

</p>
</fieldset>
</s:if>
<s:else>
	<p>
		<s:text name="noPages.found" />
	</p>
</s:else>
	
</s:form>