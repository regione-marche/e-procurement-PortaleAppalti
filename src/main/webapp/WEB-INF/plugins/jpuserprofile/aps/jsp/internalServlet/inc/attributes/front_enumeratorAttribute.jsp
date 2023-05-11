<%@ taglib prefix="wpsf" uri="apsadmin-form.tld" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<wpsf:select name="%{#attributeTracer.getFormFieldName(#attribute)}" id="%{attribute_id}"  
	headerKey="" headerValue="" 
	list="#attribute.items" value="%{#attribute.getText()}" />
