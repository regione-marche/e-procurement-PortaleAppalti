<%@ taglib prefix="s" uri="/struts-tags" %>

<s:if test="#lang.default">
	<s:property value="#attribute.value" />
</s:if>
<s:else>
	<s:text name="EntityAttribute.monolang.defaultValue" />
</s:else>