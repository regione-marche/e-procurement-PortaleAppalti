<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>
<%@ taglib prefix="wpsa" uri="apsadmin-core.tld" %>
<s:include value="linkAttributeConfigIntro.jsp"></s:include>
<h3><s:text name="title.configureLinkAttribute" />&#32;(<s:text name="title.step2of2" />)</h3>
<s:include value="linkAttributeConfigReminder.jsp"></s:include>
<p>
	<s:text name="note.choosePageToLink" />
	<s:if test="contentId != null">&#32;<s:text name="note.choosePageToLink.forTheContent" />: <s:property value="contentId"/> &ndash; <s:property value="%{getContentVo(contentId).descr}"/></s:if>.
</p>

<s:form>
<s:if test="hasFieldErrors()">
<ul>
	<s:iterator value="fieldErrors">
		<s:iterator value="value">
            <li><s:property escape="false" /></li>
		</s:iterator>
	</s:iterator>
</ul>
</s:if>

<p><s:if test="contentId == null">
<wpsf:hidden name="linkType" value="2" ></wpsf:hidden>
</s:if>
<s:else>
<wpsf:hidden name="contentId"></wpsf:hidden>
<wpsf:hidden name="linkType" value="4" ></wpsf:hidden>
</s:else></p>

<fieldset><legend><s:text name="title.pageTree" /></legend>
<ul id="pageTree">
	<s:set name="inputFieldName" value="'selectedNode'" />
	<s:set name="selectedTreeNode" value="selectedNode" />
	<s:set name="liClassName" value="'page'" />
	<s:set name="currentRoot" value="treeRootNode" />
	<s:include value="/WEB-INF/apsadmin/jsp/common/treeBuilder.jsp" />
</ul>
</fieldset>

<p><wpsf:submit action="joinPageLink" value="%{getText('label.confirm')}" title="%{getText('label.confirm')}" cssClass="button" /></p>

</s:form>

<s:include value="/WEB-INF/plugins/jacms/apsadmin/jsp/content/modules/include/backToContentButton.jsp" />