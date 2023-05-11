<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="wpsa" uri="apsadmin-core.tld" %>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>
<wp:contentNegotiation mimeType="application/xhtml+xml" charset="utf-8"/>

<h1><s:text name="title.contentManagement" /></h1>
<h2><s:text name="title.generalSettings.contentModels" /></h2>
<h3><s:text name="title.generalSettings.contentModels.remove" /></h3>

<div class="message message_error">
<h4><s:text name="message.title.ActionErrors" /></h4>
<p><s:text name="contentModel.reference"><s:param><s:property value="modelId" /></s:param></s:text>:</p>

<ul>
	<s:iterator id="contentId" value="referencedContentsOnPages">
		<li> 
			<s:if test="%{referencingPages[#contentId].size == 1}">
				<s:text name="contentModel.reference.page" />
			</s:if>
			<s:else>
				<s:text name="contentModel.reference.pages" />:
			</s:else>
			<s:iterator id="page" value="referencingPages[#contentId]">
			
			<s:set name="pageGroup" value="#page.group"></s:set>
			<wp:ifauthorized groupName="${pageGroup}"> &ldquo;<a href="<s:url action="new" namespace="/do/Page"/>?selectedNode=<s:property value="#page.code" />&amp;action:configure=true" title="Configura la pagina..."></wp:ifauthorized>
			<s:property value="#page.titles[currentLang.code]" />
			<wp:ifauthorized groupName="${pageGroup}"></a>&rdquo; </wp:ifauthorized>
			<s:if test="%{referencingPages[#contentId].size > 1}">,</s:if>
			</s:iterator>
			<s:set name="content" value="%{getContentVo(#contentId)}"></s:set>
			<s:text name="contentModel.reference.by" />&#32;<s:property value="#content.id" /> &ldquo;<s:property value="#content.descr" />&rdquo;
			
		</li>
	</s:iterator>
</ul>

<p><s:text name="contentModel.tip" />.</p>
</div>