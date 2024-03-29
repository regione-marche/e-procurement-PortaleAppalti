<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<s:if test="#lang.default">
	<wpsf:select name="%{#attributeTracer.getFormFieldName(#attribute)}" id="%{#attributeTracer.getFormFieldName(#attribute)}" 
		headerKey="" headerValue="" 
		list="#attribute.items" value="%{#attribute.getText()}" />
</s:if>
<s:else>
	<s:if test="#attributeTracer.listElement">
		<wpsf:select name="%{#attributeTracer.getFormFieldName(#attribute)}" id="%{#attributeTracer.getFormFieldName(#attribute)}" 
			headerKey="" headerValue="" 
			list="#attribute.items" value="%{#attribute.getText()}" />
	</s:if>
	<s:else>
		<s:text name="note.editContent.doThisInTheDefaultLanguage.must" />.
	</s:else>
</s:else>