<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>

<s:if test="#lang.default">
	
	<s:if test="#attribute.failedNumberString == null">
	<s:set name="numberAttributeValue" value="#attribute.value"></s:set>
	</s:if>
	<s:else>
	<s:set name="numberAttributeValue" value="#attribute.failedNumberString"></s:set>
	</s:else>
	<wpsf:textfield id="%{#attributeTracer.getFormFieldName(#attribute)}" 
			name="%{#attributeTracer.getFormFieldName(#attribute)}" value="%{#numberAttributeValue}"
			maxlength="254" cssClass="text"></wpsf:textfield>
	
</s:if>
<s:else>
	<s:text name="note.editContent.doThisInTheDefaultLanguage.must" />.
</s:else>