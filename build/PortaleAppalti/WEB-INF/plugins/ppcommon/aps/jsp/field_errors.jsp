<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:if test="hasFieldErrors()">
	<div class="errors">
		<h3><wp:i18n key="LABEL_FIELD_ERRORS"/>:</h3>
		<ul>
			<s:iterator value="fieldErrors">
				<s:iterator value="value" status="stat">
					<li class='<s:if test="%{#stat.first}">first</s:if> <s:if test="%{#stat.last}">last</s:if>'>
						<s:property/>
					</li>
				</s:iterator>
			</s:iterator>
		</ul>
	</div>
</s:if>