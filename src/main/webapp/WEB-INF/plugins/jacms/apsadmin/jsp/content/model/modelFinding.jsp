<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>
<%@ taglib prefix="wpsa" uri="apsadmin-core.tld" %>
<h1><s:text name="title.contentManagement" /></h1>
<h2><s:text name="title.generalSettings.contentModels" /></h2>

<s:form action="search" >

<p><label for="contentType"><s:text name="label.search.for"/>&#32;<s:text name="label.type"/>:</label><br />
<wpsf:select name="contentType" id="contentType" 
	list="smallContentTypes" listKey="code" listValue="descr" 
	headerKey="" headerValue="%{getText('label.all')}" cssClass="text"></wpsf:select>
</p>

<p><wpsf:submit value="%{getText('label.search')}" cssClass="button" /></p>

</s:form>

<p><a href="<s:url action="new" namespace="/do/jacms/ContentModel" />"><s:text name="jacms.menu.contentModelAdmin.new" /></a></p>

<s:form action="search">

<p class="noscreen"><input type="hidden" name="contentType" value="" id="search_contentType"/></p>

	<s:if test="%{contentModels.size > 0}" >
	
		<wpsa:subset source="contentModels" count="10" objectName="groupContentModel" advanced="true" offset="5">
		<s:set name="group" value="#groupContentModel" />
		
		<div class="pager">
			<s:include value="/WEB-INF/apsadmin/jsp/common/inc/pagerInfo.jsp" />
			<s:include value="/WEB-INF/apsadmin/jsp/common/inc/pager_formBlock.jsp" />
		</div>
			
		<table class="generic" summary="<s:text name="note.generalSettings.contentModels.summary" />">
		<caption><s:text name="title.generalSettings.contentModels.installedModels" /></caption>
			
			<tr>
				<th><s:text name="contentModel.id" /></th>
				<th><s:text name="contentModel.type" /></th>
				<th><s:text name="label.description" /></th>
				<th class="icon"><abbr title="<s:text name="label.remove" />">&ndash;</abbr></th>
			</tr>
			
			<s:iterator id="model">
				<tr>
						<td><s:property value="#model.id" /></td>
						<td><s:property value="%{getSmallContentType(#model.contentType).descr}" /></td>  
						<td>
							<a href="<s:url action="edit" namespace="/do/jacms/ContentModel" />?modelId=<s:property value="#model.id" />" title="<s:text name="label.edit" /> &ldquo;<s:property value="#model.description" />&rdquo;">
								<s:property value="#model.description" />
							</a> 
						</td>
						<td class="icon">
							<a href="<s:url action="trash" namespace="/do/jacms/ContentModel" />?modelId=<s:property value="#model.id" />" title="<s:text name="label.remove" /> &ldquo;<s:property value="#model.description" />&rdquo;">
								<img src="<wp:resourceURL />administration/img/icons/delete.png" alt="<s:text name="label.alt.clear" />" />
							</a> 
						</td>
				</tr>
			</s:iterator>
		</table>
	<div class="pager">
		<s:include value="/WEB-INF/apsadmin/jsp/common/inc/pager_formBlock.jsp" />
	</div>
	</wpsa:subset>
	</s:if>
	<s:else>
		<p><s:text name="contentModel.noModels" /></p> 
	</s:else>
</s:form>