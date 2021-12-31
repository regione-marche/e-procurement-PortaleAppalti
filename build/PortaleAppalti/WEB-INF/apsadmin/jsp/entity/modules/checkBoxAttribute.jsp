<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>

<s:set name="checkedValue" value="%{#attribute.booleanValue != null && #attribute.booleanValue ==true}" />

<s:if test="#lang.default">
		<wpsf:checkbox name="%{#attributeTracer.getFormFieldName(#attribute)}" id="%{#attributeTracer.getFormFieldName(#attribute)}" value="#checkedValue"/>&#32;
		<label for="<s:property value="#attributeTracer.getFormFieldName(#attribute)" />"><s:property value="#attributeTracer.getFormFieldName(#attribute)" /></label>
</s:if>
<s:else>
	<s:text name="note.editContent.doThisInTheDefaultLanguage.must" />.
</s:else>