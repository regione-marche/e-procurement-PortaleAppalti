<%@ taglib prefix="wp" uri="aps-core.tld" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>

<s:if test="hasFieldErrors()">
<c:set var="fieldNames" value="" />
	<div class="errors" role="alert">
		<h3><wp:i18n key="LABEL_FIELD_ERRORS"/>:</h3>
		<c:set var="errors"><s:property value="%{fieldErrors}" /></c:set>
		<ul>
			<s:iterator value="fieldErrors">
				<s:iterator var="entry">
					<s:iterator value="value" status="stat">
						<li class='<s:if test="%{#stat.first}">first</s:if> <s:if test="%{#stat.last}">last</s:if>'	>
							<s:property escape="false"/>
						</li>
					</s:iterator>
					<c:set var="fieldNames">${fieldNames}&<s:property value="%{#entry.key}" /></c:set>
				</s:iterator>
			</s:iterator>
		</ul>
		<script>
			window.onload = function () {
				let fieldNames = "${fieldNames}";
				if (fieldNames !== "") {
					names = fieldNames.split("&");
					if (names.length > 0) {
						for (let i = 0; i < names.length; i++) {
							if (names[i] !== "") {
						    	let field = document.getElementById(names[i]);
						    	if (field != null)
						    		field.setAttribute("aria-invalid", "true");
							}
						}
					}
				}
	        }
		</script>
	</div>
</s:if>