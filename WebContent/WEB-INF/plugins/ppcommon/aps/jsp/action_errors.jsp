<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:if test="hasActionErrors()">
	<div class="errors">
		<h3><wp:i18n key="LABEL_ACTION_ERRORS"/>:</h3>
		<ul>
			<s:iterator value="actionErrors" status="stat">
				<li class='<s:if test="%{#stat.first}">first</s:if> <s:if test="%{#stat.last}">last</s:if>'>
					<s:property/>
				</li>
			</s:iterator>
		</ul>
	</div>
</s:if>