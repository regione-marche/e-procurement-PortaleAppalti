<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:if test="hasActionMessages()">
	<div class="warnings" role="status">
		<h3><wp:i18n key="LABEL_ACTION_WARNINGS"/>:</h3>
		<ul>
			<s:iterator value="actionMessages">
				<li><s:property escape="false" /></li>
			</s:iterator>
		</ul>
	</div>
</s:if>
