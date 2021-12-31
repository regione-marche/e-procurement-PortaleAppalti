<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wpsa" uri="apsadmin-core.tld" %>

<li class="<s:property value="#liClassName" />"><input type="radio" name="<s:property value="#inputFieldName" />" id="<s:property value="#currentRoot.code" />" value="<s:property value="#currentRoot.code" />" <s:if test="#currentRoot.children.length > 0">class="subTreeToggler tree_<s:property value="#currentRoot.code" />" </s:if> <s:if test="#currentRoot.code == #selectedTreeNode"> checked="checked"</s:if> /><label for="<s:property value="#currentRoot.code" />"><s:property value="getTitle(#currentRoot.code, #currentRoot.titles)" /></label>
<s:if test="#currentRoot.children.length > 0">
	<ul class="treeToggler" id="tree_<s:property value="#currentRoot.code" />">
		<s:iterator value="#currentRoot.children" id="node">
			<s:set name="currentRoot" value="#node" />
			<s:include value="/WEB-INF/apsadmin/jsp/common/treeBuilder.jsp" />
		</s:iterator>
	</ul>
</s:if>
</li>