<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>
<%@ taglib prefix="wpsa" uri="apsadmin-core.tld" %>
<h1><a href="<s:url action="viewTree" namespace="/do/Page" />" title="<s:text name="note.goToSomewhere" />: <s:text name="title.pageManagement" />"><s:text name="title.pageManagement" /></a></h1>
<h2><s:text name="title.configPage" /></h2>
<s:include value="/WEB-INF/apsadmin/jsp/portal/include/pageInfo.jsp" />
<s:include value="/WEB-INF/apsadmin/jsp/portal/include/frameInfo.jsp" />

<h3>Showlet: <s:property value="%{getTitle(showlet.type.code, showlet.type.titles)}" /></h3>

<s:form action="saveListViewerConfig" namespace="/do/jacms/Page/SpecialShowlet/ListViewer">
<p class="noscreen">
	<wpsf:hidden name="pageCode" />
	<wpsf:hidden name="frame" />
	<wpsf:hidden name="showletTypeCode" value="%{showlet.type.code}" />
</p>

	<s:if test="hasFieldErrors()">
<div class="message message_error">
<h4><s:text name="message.title.FieldErrors" /></h4>	
	<ul>
	<s:iterator value="fieldErrors">
		<s:iterator value="value">
		<li><s:property escape="false" /></li>
		</s:iterator>
	</s:iterator>
	</ul>
</div>
	</s:if>


<s:if test="showlet.config['contentType'] == null">
<%-- SELEZIONE DEL TIPO DI CONTENUTO --%>
<p>
	<label for="contentType"><s:text name="label.type"/>:</label><br />
	<wpsf:select name="contentType" id="contentType" list="contentTypes" listKey="code" listValue="descr" cssClass="text" />
</p>
<p><wpsf:submit action="configListViewer" value="%{getText('label.continue')}" cssClass="button" /></p>

</s:if>
<s:else>
<fieldset><legend><s:text name="title.contentInfo" /></legend>
<p>
	<label for="contentType"><s:text name="label.type"/>:</label><br />
	<wpsf:select name="contentType" id="contentType" list="contentTypes" listKey="code" listValue="descr" disabled="true" value="%{getShowlet().getConfig().get('contentType')}" cssClass="text" />
	<wpsf:submit action="changeContentType" value="%{getText('label.change')}" cssClass="button" />	
</p>
<p class="noscreen">
	<wpsf:hidden name="contentType" value="%{getShowlet().getConfig().get('contentType')}" />
</p>	
<p>
	<label for="category"><s:text name="label.category" />:</label><br />
	<wpsf:select name="category" id="category" list="categories" listKey="code" listValue="getFullTitle(currentLang.code)" headerKey="" headerValue="%{getText('label.all')}" value="%{getShowlet().getConfig().get('category')}" cssClass="text" />
</p>
</fieldset>

<fieldset><legend><s:text name="title.filterOptions" /></legend>

<s:if test="null != filtersProperties && filtersProperties.size()>0" >
<table class="generic" summary="<s:text name="note.page.contentListViewer.summary" />">
<tr>			
	<th><abbr title="<s:text name="label.number" />">N</abbr></th>
	<th><s:text name="name.filterDescription" /></th>
	<th><s:text name="label.order" /></th>
	<th class="icon" colspan="3"><abbr title="<s:text name="label.actions" />">&ndash;</abbr></th> 
</tr>
<s:iterator value="filtersProperties" id="filter" status="rowstatus">
<tr>
	<td><s:property value="#rowstatus.index+1"/></td>
	<td>
		<s:text name="label.filterBy" /><strong>
			<s:if test="#filter['key'] == 'created'">
				<s:text name="label.creationDate" />
			</s:if>
			<s:elseif test="#filter['key'] == 'modified'">
				<s:text name="label.lastModifyDate" />			
			</s:elseif>
			<s:else>
				<s:property value="#filter['key']" />
			</s:else>
		</strong><s:if test="(#filter['start'] != null) || (#filter['end'] != null) || (#filter['value'] != null)">,
		<s:if test="#filter['start'] != null">
			<s:text name="label.filterFrom" /><strong>
				<s:if test="#filter['start'] == 'today'">
					<s:text name="label.today" />				
				</s:if>
				<s:else>
					<s:property value="#filter['start']" />
				</s:else>
			</strong>
		</s:if>
		<s:if test="#filter['end'] != null">
			<s:text name="label.filterTo" /><strong>
				<s:if test="#filter['end'] == 'today'">
					<s:text name="label.today" />				
				</s:if>
				<s:else>
					<s:property value="#filter['end']" />
				</s:else>
			</strong>
		</s:if>
		<s:if test="#filter['value'] != null">
			<s:text name="label.filterValue" />:<strong> <s:property value="#filter['value']" /></strong>
				<s:if test="#filter['likeOption'] == 'true'">
					<em>(<s:text name="label.filterValue.isLike" />)</em>
				</s:if>
		</s:if>
		</s:if>
	</td>
	<td>
	<s:if test="#filter['order'] == 'ASC'"><s:text name="label.order.ascendant" /></s:if>
	<s:if test="#filter['order'] == 'DESC'"><s:text name="label.order.descendant" /></s:if>
	</td>
	<td class="icon">
		<wpsa:actionParam action="moveFilter" var="actionName" >
			<wpsa:actionSubParam name="filterIndex" value="%{#rowstatus.index}" />
			<wpsa:actionSubParam name="movement" value="UP" />
		</wpsa:actionParam>
		<s:set name="iconImagePath" id="iconImagePath"><wp:resourceURL/>administration/img/icons/go-up.png</s:set>		
		<wpsf:submit action="%{#actionName}" type="image" src="%{#iconImagePath}" value="%{getText('label.moveUp')}" title="%{getText('label.moveUp')}" />
	</td>
	<td class="icon">	
		<wpsa:actionParam action="moveFilter" var="actionName" >
			<wpsa:actionSubParam name="filterIndex" value="%{#rowstatus.index}" />
			<wpsa:actionSubParam name="movement" value="DOWN" />
		</wpsa:actionParam>
		<s:set name="iconImagePath" id="iconImagePath"><wp:resourceURL/>administration/img/icons/go-down.png</s:set>
		<wpsf:submit action="%{#actionName}" type="image" src="%{#iconImagePath}" value="%{getText('label.moveDown')}" title="%{getText('label.moveDown')}" />
	</td>
	<td class="icon">	
		<wpsa:actionParam action="removeFilter" var="actionName" >
			<wpsa:actionSubParam name="filterIndex" value="%{#rowstatus.index}" />
		</wpsa:actionParam>
		<s:set name="iconImagePath" id="iconImagePath"><wp:resourceURL/>administration/img/icons/list-remove.png</s:set>
		<wpsf:submit action="%{#actionName}" type="image"  src="%{#iconImagePath}" value="%{getText('label.remove')}" title="%{getText('label.remove')}" />
	</td>	
</tr>
</s:iterator>
</table>
</s:if>
<s:else>
	<p><s:text name="note.filters.none" /></p>		
</s:else>

<p><wpsf:submit action="newFilter" value="%{getText('label.add')}" cssClass="button" /></p>
<p class="noscreen">
	<wpsf:hidden name="filters" value="%{getShowlet().getConfig().get('filters')}" />
</p>
</fieldset>

<fieldset><legend><s:text name="title.publishingOptions" /></legend>
<p>
	<label for="modelId"><s:text name="label.contentModel" />:</label><br />
	<wpsf:select name="modelId" id="modelId" value="%{getShowlet().getConfig().get('modelId')}" 
		list="%{getModelsForContentType(showlet.config['contentType'])}" headerKey="" headerValue="%{getText('label.default')}" listKey="id" listValue="description" cssClass="text" />
</p>

<p>
	<label for="maxElemForItem"><s:text name="label.maxElements" />:</label><br />
	<wpsf:select name="maxElemForItem" id="maxElemForItem" value="%{getShowlet().getConfig().get('maxElemForItem')}" 
		headerKey="" headerValue="%{getText('label.all')}" list="#{1:1,2:2,3:3,4:4,5:5,6:6,7:7,8:8,9:9,10:10,15:15,20:20}" cssClass="text" />
</p>
</fieldset>

<p class="buttons"><wpsf:submit action="saveListViewerConfig" value="%{getText('label.save')}" cssClass="button" /></p>

</s:else>

</s:form>